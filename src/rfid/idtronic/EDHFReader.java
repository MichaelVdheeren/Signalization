package rfid.idtronic;

import gnu.io.NoSuchPortException;

import java.io.IOException;

import rfid.RFIDPacket;
import rfid.RFIDReader;

public class EDHFReader extends RFIDReader {
	public EDHFReader(String portName) throws NoSuchPortException {
		super(portName);
	}
	
	public boolean setControlLed1(int timeUnits,int cycles) {
		byte[] data = new byte[2];
		data[0] = (byte)timeUnits;
		data[1] = (byte)cycles;
		
		try {
			sendPacket(new EDHFCommandPacket(0x87, data));
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean setControlLed2(int timeUnits,int cycles) {
		byte[] data = new byte[2];
		data[0] = (byte)timeUnits;
		data[1] = (byte)cycles;
		
		try {
			sendPacket(new EDHFCommandPacket(0x88, data));
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean setBuzzer(int timeUnits,int cycles) {
		byte[] data = new byte[2];
		data[0] = (byte)timeUnits;
		data[1] = (byte)cycles;
		
		try {
			sendPacket(new EDHFCommandPacket(0x89, data));
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public EDHFReplyPacket iso15693_read(int flags,int firstBlockNb, int nbOfBlocks) throws IOException {
		byte[] data = new byte[3];
		data[0] = (byte)flags;
		data[1] = (byte)firstBlockNb;
		data[2] = (byte)nbOfBlocks;
		
		sendPacket(new EDHFCommandPacket(0x11, data));
		return receivePacket();
	}

	@Override
	protected EDHFReplyPacket receivePacket() throws IOException {
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
		
		return new EDHFReplyPacket(bytes);
	}
	
	@Override
	public void run() {
		while (running) {
		    try {
		    	System.out.println(iso15693_read(0x02, 0x01, 0x15).toString());
		    } catch(Exception e) {
		    	continue;
		    }
	
		    try { Thread.sleep(this.threadSleepTime); } catch (InterruptedException e) {}
		}
	}
}
