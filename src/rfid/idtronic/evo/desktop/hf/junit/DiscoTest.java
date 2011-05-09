package rfid.idtronic.evo.desktop.hf.junit;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import rfid.idtronic.evo.desktop.hf.EDHFReader;


public class DiscoTest {
	EDHFReader reader;
	String portName = "/dev/tty.SLAB_USBtoUART";
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void controlled1Test() throws NoSuchPortException, PortInUseException, InterruptedException, IOException  {
		reader = new EDHFReader(portName);		
		reader.open();
		reader.setControlLed1(0x18,0x0A);
		reader.join();
	}
}