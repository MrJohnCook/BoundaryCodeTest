
package controllers.cook;

import controllers.cook.arango.ArangoBoolean;
import controllers.cook.arango.ArangoInt;
import controllers.cook.arango.ArangoString;

/**
 * In ArangoDB, all the custom fields of a JSON document are stored in the 
 * object named "_children".  Each document type needs a custom Children
 * definition.
 */
public class ArangoNotificationChildren {
	public ArangoInt user_id;
	public ArangoInt timestamp;
	public ArangoBoolean isRead;
	public ArangoString message;

	public ArangoNotificationChildren() {
		super();
	}
}
