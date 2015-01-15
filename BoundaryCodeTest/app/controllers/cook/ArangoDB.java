package controllers.cook;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;

/**
 * Singleton wrapper that handles access to the Arango DB.
 * All the credentials and connection information are hardcoded in this class.
 * There is no facility to disconnect at this time.
 * Assumes a single DB instance co-located with the play framework server instance.
 */
public class ArangoDB {

	static ArangoDB singleton = null;

	/**
	 * Returns the facade over the ArangoDB driver.
	 *
	 * @return {@link ArangoDB}
	 */
	public synchronized static ArangoDB getInstance() {
		if (singleton == null) {
			singleton = new ArangoDB();
			singleton.init();
		}
		return singleton;
	}

	private ArangoDriver driver = null;

	private ArangoDB() {
		super();
	}

	/**
	 *  @return {@link ArangoDriver} -- the running driver of the ArangoDB implementation
	 */
	public ArangoDriver getDriver() {
		return driver;
	}

	void init() {
		final ArangoConfigure configure = new ArangoConfigure();
		configure.setHost("localhost");
		configure.setPort(8529);
		configure.setUser("root");
		configure.setPassword("root");
		configure.init();

		driver = new ArangoDriver(configure, "Boundary1");
	}

	void setDriver(final ArangoDriver theDriver) {
		this.driver = theDriver;
	}
}
