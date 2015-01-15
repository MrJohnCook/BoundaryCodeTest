package controllers.cook.arango;

/**
 * POJO facade for how ArangoDB stores values.
 */
public class ArangoInt {

	public int _value;

	public ArangoInt() {
		super();
	}

	@Override
	public String toString() {
		return "ArangoInt [_value=" + _value + "]";
	}

}
