package rfid.idtronic;


public class EDHFCommandPacket extends EDHFPacket {
	private final int deviceAddress;
	private final int command;
	private final byte[] data;
	
	public EDHFCommandPacket(int command, byte[] data) {
		this(0x00,command,data);
	}
	
	public EDHFCommandPacket(int deviceAddress, int command, byte[] data) {
		super();
		this.deviceAddress = deviceAddress;
		this.command = command;
		this.data = data;
	}

	@Override
	public int getChecksum() {
		int checksum = getDeviceAddress() ^ getData().length+1 ^ getCommand();
		
		for (byte b : getData())
			checksum ^= b;
		
		return checksum;
	}

	@Override
	public byte[] getBytes() {
		int dataLength = getData().length;
		byte[] bytes = new byte[dataLength+6];
		
		bytes[0] = (byte)getStartOfPacket();
		bytes[1] = (byte)getDeviceAddress();
		bytes[2] = (byte)(getData().length+1);
		bytes[3] = (byte)getCommand();
		
		for (int i=0; i<dataLength; i++)
			bytes[i+4] = getData()[i];
		
		bytes[dataLength+4] = (byte)getChecksum();
		bytes[dataLength+5] = (byte)getEndOfPacket();
		
		return bytes;
	}
	
	public int getCommand() {
		return this.command;
	}

	@Override
	public byte[] getData() {
		return this.data;
	}

	@Override
	public int getDeviceAddress() {
		return deviceAddress;
	}

}
