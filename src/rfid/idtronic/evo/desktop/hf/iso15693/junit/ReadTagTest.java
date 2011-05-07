package rfid.idtronic.evo.desktop.hf.iso15693.junit;


import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import org.junit.Before;
import org.junit.Test;

import rfid.AbstractCommand;
import rfid.ReplyEvent;
import rfid.ReplyEventListener;
import rfid.AbstractReply;
import rfid.idtronic.evo.desktop.hf.EDHFReader;
import rfid.idtronic.evo.desktop.hf.iso15693.ReadCommand;

public class ReadTagTest {
	EDHFReader reader;
	String portName = "/dev/tty.SLAB_USBtoUART";
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void readTest() throws NoSuchPortException, PortInUseException, InterruptedException  {
		reader = new EDHFReader(portName);
		reader.addListener(new ReplyEventListener() {
			
			@Override
			public void receivedReply(ReplyEvent event) {
				// System.out.println(command.toString());
				System.out.println(event.getReply().toString());
			}
		});
		
		reader.open();
		reader.execute(new ReadCommand(2, 1, 30, true));
		reader.join();
	}
}
