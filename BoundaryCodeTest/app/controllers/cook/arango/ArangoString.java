
package controllers.cook.arango;

/**
 * POJO facade for how ArangoDB stores values.
 */
public class ArangoString {

	public String _value;

	@Override
	public String toString() {
		return "ArangoString [_value=" + _value + "]";
	}
}
