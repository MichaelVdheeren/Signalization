package rfid.idtronic.evo.desktop.hf;

import rfid.AbstractReply;

/**
 * Class representing a reply for the iDtronic EVO DESKTOP USB HF RFID Reader.
 */
public class EDHFReply extends AbstractReply {

	/**
	 * Create a reply.
	 * @param	bytes
	 * 			The bytes which resemble the reply.
	 */
	public EDHFReply(byte[] bytes) {
		super(bytes);
	}

	@Override
	public int getChecksum() {
		int byteLength = getBytes().length;
		return getBytes()[byteLength-2];
	}
	
	/**
	 * Get the status code.
	 * @return	The status code.
	 */
	public int getStatus() {
		return getBytes()[3];
	}

	@Override
	public byte[] getData() {
		int dataLength = getBytes()[2]-1;
		byte[] data = new byte[dataLength];
		
		for (int i=0; i<dataLength; i++)
			data[0] = getBytes()[4+i];
		
		return data;
	}

	/**
	 * Get the address of the device which processed this command.
	 * @return	The device address.
	 */
	public int getDeviceAddress() {
		return getBytes()[1];
	}
	
	/**
	 * Get the start byte of the packet.
	 * @return	The start byte of the packet.
	 */
	public int getStartOfPacket() {
		return getBytes()[0];
	}
	
	/**
	 * Get the end byte of the packet.
	 * @return	The end byte of the packet.
	 */
	public int getEndOfPacket() {
		return getBytes()[getBytes().length-1];
	}
}
