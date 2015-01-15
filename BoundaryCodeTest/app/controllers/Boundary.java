package controllers;

import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.CursorResultSet;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.util.MapBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.cook.ArangoDB;
import controllers.cook.ArangoNotification;
import controllers.cook.Notification;

/**
 * Implements the Play Framework URL routing controller features for the
 * Notification coding assignment
 */
public class Boundary extends Controller {

	/**
	 * Handles the route PUT /notifications/by_id/:id
	 *
	 * @param id
	 * @return {@link Result}
	 */
	public static Result changeDocument(final Long id) {
		//-- trivial validation
		if ( id==null) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "NULL id");
			return badRequest(errorNode);
		}

		//-- get the payload of the put
		//-- we could error check on the POJO or the JSON, I chose JSON
		final ObjectNode json = (ObjectNode) request().body().asJson();
		if (! json.has("user_id")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'user_id'.");
			return badRequest(errorNode);
		}
		if (! json.has("id")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'id'.");
			return badRequest(errorNode);
		}
		if (! json.has("timestamp")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'timestamp'.");
			return badRequest(errorNode);
		}
		if (! json.has("message")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'message'.");
			return badRequest(errorNode);
		}

		final String docId = json.get("id").asText();
		final String webId = id.toString();
		if ( ! docId.equals(webId)) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload ID mismatch.");
			return badRequest(errorNode);
		}

		//--- convert the JSON to a POJO to handle the update
		final Notification pojo = Json.fromJson(json, Notification.class);

		//-- awareness of the ArangoNotification structure is necessary to build the query
		final String query = "FOR t IN Messages  FILTER t._key == @id " 
				+ "RETURN t ";

		final Map<String, Object> bindVars = new MapBuilder().put("id", id.toString()).get();
		CursorResultSet<ArangoNotification> rs = null;
		try {
			rs = getDriver().executeQueryWithResultSet(
					query, bindVars, ArangoNotification.class, true, 20
					);
		} catch (final ArangoException e) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", e.getMessage());
			return internalServerError(errorNode);
		}

		if ( rs.getTotalCount() == 0 ) {
			return ok();
		}

		if ( rs.getTotalCount() > 1 ) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "found "+rs.getTotalCount()+ " documents but expected 1");
			return internalServerError(errorNode);
		}

		//--- make the changes, but only to the allowed fields 
		final ArangoNotification rawNotification = rs.next();
		rawNotification._children.isRead._value = pojo.isIsRead();
		rawNotification._children.message._value = pojo.getMessage();

		try {
			getDriver().replaceDocument("Messages", id, rawNotification);
		} catch (final ArangoException e) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", e.getMessage());
			return internalServerError(errorNode);
		}
		return ok();
	}

	/**
	 * Handles the route GET /notifications/by_id/:id
	 *
	 * @param id
	 * @return {@link Result}
	 */
	public static Result getDocument(final Long id) {
		//-- trivial validation
		if ( id==null) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "NULL id");
			return badRequest(errorNode);
		}


		DocumentEntity<ArangoNotification> result = null;

		final String key = id.toString();
		try {
			result = getDriver().getDocument("Messages", key, ArangoNotification.class);
		} catch (final ArangoException e) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", e.getMessage());
			return internalServerError(errorNode);
		}
		final ArangoNotification notification2 = result.getEntity();
		final Notification notification = new Notification(notification2);
		final JsonNode node = Json.toJson(notification);

		return ok(node);

	}

	/**
	 * Handles the route GET /notifications/by_user/:userid
	 *
	 * @param userid
	 * @return {@link Result}
	 */
	public static Result getUsers(final Integer userid) {
		//-- trivial validation
		if ( userid==null) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "NULL userid");
			return badRequest(errorNode);
		}


		//-- awareness of the ArangoNotification structure is necessary to build the query
		final String query = "FOR t IN Messages  FILTER t._children.user_id._value == @userid " 
				+ "SORT t._children.timestamp._value DESC RETURN t ";

		final Map<String, Object> bindVars = new MapBuilder().put("userid", userid).get();
		CursorResultSet<ArangoNotification> rs = null;
		try {
			rs = getDriver().executeQueryWithResultSet(
					query, bindVars, ArangoNotification.class, true, 20
					);
		} catch (final ArangoException e) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", e.getMessage());
			return internalServerError(errorNode);
		}

		//--- create the response array
		final ObjectNode json = Json.newObject();
		final ArrayNode array = json.arrayNode();

		//--- iterate over the result set, convert to desired POJO/JSON type
		for( final ArangoNotification rawNotification : rs ) {
			final Notification notification = new Notification(rawNotification);
			final JsonNode node = Json.toJson(notification);
			array.add(node);
		}

		return ok(array);

	}

	/**
	 * trivial debug route
	 *
	 */
	public static Result index() {
		return ok(index.render("The coding project is running."));
	}

	/**
	 * Handles the route PUT /notifications
	 *
	 */
	public static Result newMessage() {
		final ObjectNode json = (ObjectNode) request().body().asJson();
		if (! json.has("user_id")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'user_id'.");
			return badRequest(errorNode);
		}
		if (! json.has("timestamp")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'timestamp'.");
			return badRequest(errorNode);
		}
		if (! json.has("message")) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", "ERROR: payload does not contain 'message'.");
			return badRequest(errorNode);
		}

		json.put("isRead", false );

		DocumentEntity<ArangoNotification> result = null;
		try {
			result = getDriver().createDocument("Messages", json);
		} catch (final ArangoException e) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", e.getMessage());
			return internalServerError(errorNode);
		}
		final String key = result.getDocumentKey();
		try {
			result = getDriver().getDocument("Messages", key, ArangoNotification.class);
		} catch (final ArangoException e) {
			final ObjectNode errorNode = Json.newObject();
			errorNode.put("status", "ERROR");
			errorNode.put("message", e.getMessage());
			return internalServerError(errorNode);
		}
		final ArangoNotification notification2 = result.getEntity();
		final Notification notification = new Notification(notification2);
		final JsonNode node = Json.toJson(notification);

		return ok(node);
	}

	static ArangoDriver getDriver() {
		final ArangoDB arango = ArangoDB.getInstance();
		final ArangoDriver driver = arango.getDriver();
		return driver;
	}
}
