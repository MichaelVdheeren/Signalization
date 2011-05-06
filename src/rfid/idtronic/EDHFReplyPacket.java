package rfid.idtronic;

public class EDHFReplyPacket extends EDHFPacket {
	private final byte[] bytes;
	
	public EDHFReplyPacket(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	public int getChecksum() {
		int byteLength = getBytes().length;
		return getBytes()[byteLength-2];
	}

	@Override
	public byte[] getBytes() {
		return this.bytes;
	}
	
	public int getCommand() {
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

	@Override
	public int getDeviceAddress() {
		return getBytes()[1];
	}

}
