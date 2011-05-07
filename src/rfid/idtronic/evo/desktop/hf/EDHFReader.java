package rfid.idtronic.evo.desktop.hf;

import gnu.io.NoSuchPortException;

import java.io.IOException;

import rfid.AbstractReader;

/**
 * Class representing the iDtronic EVO DESKTOP USB HF RFID Reader.
 */
public class EDHFReader extends AbstractReader {
	public EDHFReader(String portName) throws NoSuchPortException {
		super(portName);
	}
	
	/**
	 * Set the control 1 led of this device.
	 * @param	timeUnits
	 * 			Units of on time. Each unit is 20ms. So the value needs to be less than 50.
	 * @param	cycles
	 * 			Number of cycles to turn on/off the LED. The cycle time is one second.
	 * @return	True if the command was executed successfully. False otherwise.
	 */
	public boolean setControlLed1(int timeUnits,int cycles) {
		if (timeUnits >= 50)
			return false;
		
		byte[] data = new byte[2];
		data[0] = (byte)timeUnits;
		data[1] = (byte)cycles;
		
		try {
			sendCommand(0x87,data);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Set the control 2 led of this device.
	 * @param	timeUnits
	 * 			Units of on time. Each unit is 20ms. So the value needs to be less than 50.
	 * @param	cycles
	 * 			Number of cycles to turn on/off the LED. The cycle time is one second.
	 * @return	True if the command was executed successfully. False otherwise.
	 */
	public boolean setControlLed2(int timeUnits,int cycles) {
		if (timeUnits >= 50)
			return false;
		
		byte[] data = new byte[2];
		data[0] = (byte)timeUnits;
		data[1] = (byte)cycles;
		
		try {
			sendCommand(0x88,data);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Set the buzzer of this device.
	 * @param	timeUnits
	 * 			Units of on time. Each unit is 20ms. So the value needs to be less than 50.
	 * @param	cycles
	 * 			Number of cycles to turn on/off the buzzer. The cycle time is one second.
	 * @return	True if the command was executed successfully. False otherwise.
	 */
	public boolean setBuzzer(int timeUnits,int cycles) {
		if (timeUnits >= 50)
			return false;
		
		byte[] data = new byte[2];
		data[0] = (byte)timeUnits;
		data[1] = (byte)cycles;
		
		try {
			sendCommand(0x89,data);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Send a command to the RFID reader.
	 * @param	cmdCode
	 * 			The command code of the command to send.
	 * @param	data
	 * 			The data to send with the command.
	 * @throws 	IOException
	 * 			When the command could not be send successfully to the reader.
	 */
	private void sendCommand(int cmdCode, byte[] data) throws IOException {
		EDHFCommand command = new EDHFCommand(cmdCode,data);
		execute(command);
	}

	@Override
	protected EDHFReply receiveReply() throws IOException {
		byte[] buffer = new byte[255];
		
		int read;
		
		read = in.read(buffer,0,1);
		
		if (read != 1)
			return null;
		
		read = in.read(buffer,1,1);
		
		if (read != 1)
			return null;
		
		read = in.read(buffer,2,1);
		
		if (read != 1)
			return null;
		
		int dataLength = buffer[2]-1;
		read = in.read(buffer,3,1);
		
		if (read != 1)
			return null;
		
		if (dataLength > 0) {
		    for (int i=4; i<dataLength+4; i++) {
		    	read = in.read(buffer, i, 1);
		    	if (read == 0) return null;
		    }
		}
		
		read = in.read(buffer,4+dataLength,1);
		
		if (read != 1)
			return null;
		
		read = in.read(buffer,5+dataLength,1);
		
		if (read != 1)
			return null;
		
		byte[] bytes = new byte[6+dataLength];
		for (int i=0; i<bytes.length; i++) {
			bytes[i] = buffer[i];
		}
		
		return new EDHFReply(bytes);
	}
}
