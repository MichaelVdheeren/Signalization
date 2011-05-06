import java.io.IOException;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import rfid.RFIDReader;


public class Main {

	/**
	 * @note http://www.on-signal.org/projects/RFID_Processing_Examples
	 * @param args
	 */
	public static void main(String[] args) {
		RFIDReader reader;
		String portName = "/dev/tty.SLAB_USBtoUART";
		
		
		try {
			reader = new RFIDReader(portName);
			int[] data = new int[2];
			data[0] = 0x18;
			data[1] = 0x0A;
	        reader.sendCommand(0x88, data);
	    } catch (NoSuchPortException e) {
	    	System.out.println("Port "+portName+" was not found!");
	    } catch (PortInUseException e) {
	    	System.out.println("Port "+portName+" is in use by another program.");
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
