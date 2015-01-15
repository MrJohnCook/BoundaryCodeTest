
package controllers.cook;

/**
 * Common structure for all Arango documents
 */
public abstract class BaseArangoDocument {
	public String _key;
	public String _rev;
	public String _id;

	protected BaseArangoDocument() {
		super();
	}

}
