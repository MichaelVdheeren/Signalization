import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import org.onsignal.rfid.RFIDReader;


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
	        reader.setVerbose(true);
	    } catch (NoSuchPortException e) {
	    	System.out.println("Port "+portName+" was not found!");
	    } catch (PortInUseException e) {
	    	System.out.println("Port "+portName+" is in use by another program.");
	    }
	}

}
