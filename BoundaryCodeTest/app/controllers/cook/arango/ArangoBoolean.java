package controllers.cook.arango;

/**
 * POJO facade for how ArangoDB stores values.
 */
public class ArangoBoolean {

	public boolean _value;

	public ArangoBoolean() {
		super();
	}

	@Override
	public String toString() {
		return "ArangoBoolean [_value=" + _value + "]";
	}
}
