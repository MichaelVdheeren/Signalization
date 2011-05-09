package rfid.idtronic.evo.desktop.hf.junit;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import org.junit.Before;
import org.junit.Test;

import rfid.ReplyEvent;
import rfid.ReplyEventListener;
import rfid.idtronic.evo.desktop.hf.EDHFReader;


public class DiscoTest {
	EDHFReader reader;
	String portName = "/dev/tty.SLAB_USBtoUART";
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void controlled1Test() throws NoSuchPortException, PortInUseException, InterruptedException  {
		reader = new EDHFReader(portName);
		reader.addListener(new ReplyEventListener() {
			
			@Override
			public void receivedReply(ReplyEvent event) {
				// System.out.println(command.toString());
				System.out.println(event.getReply().toString());
			}
		});
		
		reader.open();
		reader.setControlLed1(0x18,0x0A);
		reader.join();
	}
}