import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import rfid.idtronic.EDHFReader;


public class Main {

	/**
	 * @note http://www.on-signal.org/projects/RFID_Processing_Examples
	 * @param args
	 */
	public static void main(String[] args) {
		EDHFReader reader;
		String portName = "/dev/tty.SLAB_USBtoUART";
		
		
		try {
			reader = new EDHFReader(portName);
			reader.open();
			reader.start();
	    } catch (NoSuchPortException e) {
	    	System.out.println("Port "+portName+" was not found!");
	    } catch (PortInUseException e) {
	    	System.out.println("Port "+portName+" is in use!");
		}
	}

}
