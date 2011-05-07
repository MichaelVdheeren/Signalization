package rfid;

/**
 * Interface that can be used to listen for the reply from a command sent
 * to the RFID device.
 */
public interface ReplyEventListener {
	public void receivedReply(ReplyEvent event);
}
