package rfid;

/**
 * A class representing an abstract command for the RFID reader.
 */
public abstract class AbstractCommand extends AbstractPacket {
	private static final long serialVersionUID = 1L;
	private final boolean autoRepeat;
	
	/**
	 * Creates a new command, which will not be repeated.
	 */
	public AbstractCommand() {
		this(false);
	}
	
	/**
	 * Creates a new command, which will be repeated by the reader upon execution.
	 * @param	autoRepeat
	 * 			boolean indicating the repeating wish of this command.
	 */
	public AbstractCommand(boolean autoRepeat) {
		this.autoRepeat = autoRepeat;
	}
	
	/**
	 * Check whether this command wishes to be repeated.
	 * @return	True if this command wants to be repeated, false otherwise.
	 */
	public boolean isAutoRepeatEnabled() {
		return this.autoRepeat;
	}
}
