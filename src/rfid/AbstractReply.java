package rfid;

/**
 * Class representing the reply received from the RFID reader.
 */
public abstract class AbstractReply extends AbstractPacket {
	private static final long serialVersionUID = 1L;
	private final byte[] bytes;
	
	/**
	 * Creates a new reply from the given <bytes>
	 * @param	bytes
	 * 			The bytes which represent the reply packet.
	 */
	public AbstractReply(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	public byte[] getBytes() {
		return this.bytes;
	}
}
