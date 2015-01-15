
package controllers.cook;

/**
 * The POJO of the coding problem.
 * The properties of this POJO and the corresponding JSON do not match
 * the intrinsic document structure of ArangoDB.  The conversion constructor
 * in this class greatly simplifies the generation of responses.
 * 
 */
public class Notification {

	private long id;
	private int user_id;
	private int timestamp;
	private String message;
	private boolean isRead;

	/**
	 * Default constructor
	 *
	 */
	public Notification() {
		super();
	}

	/**
	 * Conversion constructor, taking the internal Notification
	 * structure that comes from ArangoDB.
	 *
	 * @param arango
	 */
	public Notification(final ArangoNotification arango) {
		super();
		this.id = Long.parseLong(arango._key);
		this.user_id = arango._children.user_id._value;
		this.timestamp = arango._children.timestamp._value;
		this.message = arango._children.message._value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Notification other = (Notification) obj;
		if (id != other.id) {
			return false;
		}
		if (isRead != other.isRead) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		if (user_id != other.user_id) {
			return false;
		}
		return true;
	}

	public long getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public int getUser_id() {
		return user_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (id ^ (id >>> 32));
		result = (prime * result) + (isRead ? 1231 : 1237);
		result = (prime * result) + ((message == null) ? 0 : message.hashCode());
		result = (prime * result) + timestamp;
		result = (prime * result) + user_id;
		return result;
	}

	public boolean isIsRead() {
		return isRead;
	}

	public void setId(final long newId) {
		this.id = newId;
	}

	public void setIsRead(final boolean isRead) {
		this.isRead = isRead;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public void setTimestamp(final int timestamp) {
		this.timestamp = timestamp;
	}

	public void setUser_id(final int user_id) {
		this.user_id = user_id;
	}

	@Override
	public String toString() {
		return "Notification [uuid=" + id + ", user_id=" + user_id + ", timestamp=" + timestamp + ", message=" + message + ", isRead=" + isRead + "]";
	}

}
