package rfid.idtronic.evo.desktop.hf;

import rfid.AbstractCommand;

/**
 * Class representing a command for the iDtronic EVO DESKTOP USB HF RFID Reader.
 */
public class EDHFCommand extends AbstractCommand {
	private final int deviceAddress;
	private final int command;
	private final byte[] data;
	private final int stx = 0xaa;
	private final int etx = 0xbb;

	/**
	 * Create a new command.
	 * @param	command
	 * 			The command code for this command.
	 * @param	data
	 * 			The data to send with this command.
	 */
	public EDHFCommand(int command, byte[] data) {
		this(0x00,command,data);
	}
	
	/**
	 * Create a new command.
	 * @param	command
	 * 			The command code for this command.
	 * @param	data
	 * 			The data to send with this command.
	 * @param 	autoRepeat
	 * 			Set if this command needs to be repeated or not.
	 */
	public EDHFCommand(int command, byte[] data, boolean autoRepeat) {
		this(0x00,command,data,autoRepeat);
	}
	
	/**
	 * Create a new command.
	 * @param	deviceAddress
	 * 			The address of the device which needs to process the command.
	 * @param	command
	 * 			The command code for this command.
	 * @param	data
	 * 			The data to send with this command.
	 */
	public EDHFCommand(int deviceAddress, int command, byte[] data) {
		super();
		this.deviceAddress = deviceAddress;
		this.command = command;
		this.data = data;
	}
	
	/**
	 * Create a new command.
	 * @param	deviceAddress
	 * 			The address of the device which needs to process the command.
	 * @param	command
	 * 			The command code for this command.
	 * @param	data
	 * 			The data to send with this command.
	 * @param 	autoRepeat
	 * 			Set if this command needs to be repeated or not.
	 */
	public EDHFCommand(int deviceAddress, int command, byte[] data, boolean autoRepeat) {
		super(autoRepeat);
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
	
	/**
	 * Get the command code for this command.
	 * @return	The command code for this command.
	 */
	public int getCommand() {
		return this.command;
	}

	@Override
	public byte[] getData() {
		return this.data;
	}

	/**
	 * Get the address of the device which needs to
	 * process this command.
	 * @return	The device address.
	 */
	public int getDeviceAddress() {
		return deviceAddress;
	}
	
	/**
	 * Get the start byte of the packet.
	 * @return	The start byte of the packet.
	 */
	public int getStartOfPacket() {
		return this.stx;
	}
	
	/**
	 * Get the end byte of the packet.
	 * @return	The end byte of the packet.
	 */
	public int getEndOfPacket() {
		return this.etx;
	}
}
