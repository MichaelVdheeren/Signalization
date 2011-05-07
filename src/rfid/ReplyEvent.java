package rfid;

/**
 * Class representing a reply event.
 */
public class ReplyEvent {
	// The reader which that raised the event.
	private final AbstractReader reader;
	// The command which preceded the reply.
	private final AbstractCommand command;
	// The reply received.
	private final AbstractReply reply;
	
	/**
	 * Create a new reply event.
	 * @param 	reader
	 * 			The reader that raised the event.
	 * @param 	command
	 * 			The command which preceded the reply.
	 * @param 	reply
	 * 			The reply received.
	 */
	public ReplyEvent(AbstractReader reader, AbstractCommand command, AbstractReply reply) {
		this.reader = reader;
		this.command = command;
		this.reply = reply;
	}

	/**
	 * Retrieve the reader who raised the event.
	 * @return	The reader who raised the event.
	 */
	public AbstractReader getReader() {
		return reader;
	}

	/**
	 * Retrieve the command which preceded the reply.
	 * @return	The command which preceded the reply.
	 */
	public AbstractCommand getCommand() {
		return command;
	}

	/**
	 * Retrieve the reply.
	 * @return	The reply.
	 */
	public AbstractReply getReply() {
		return reply;
	}
}
