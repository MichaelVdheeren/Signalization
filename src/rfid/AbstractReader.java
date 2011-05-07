package rfid;


import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

/**
 * Class representing an RFID reader.
 */
public abstract class AbstractReader extends Thread
{
    private CommPortIdentifier portId = null;
    private String portName;
    private SerialPort serialPort;
    protected InputStream in;
    protected OutputStream out;
    protected boolean running = false;
    protected int threadSleepTime = 10;

    private final Stack<AbstractCommand> queue = new Stack<AbstractCommand>();
    private ArrayList<ReplyEventListener> listeners = new ArrayList<ReplyEventListener>();
   
    public AbstractReader(String portName) throws NoSuchPortException {
		this.setPort(portName);
    }

    /**
     * Set or change the name of the port. If the reader is running,
     * it will be shut down first. You'll have to call open() again to
     * restart the reader on the new port.
     */
    public void setPort(String portName) throws NoSuchPortException {
		if (this.running) this.close();
		
		Boolean glob = false;
		if (portName.endsWith("*")) {
			glob = true;
			portName = portName.substring(0, portName.length()-1);
		}
	
		portId = null;
	
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
		    portId = (CommPortIdentifier) portList.nextElement();
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		    	if (!glob) {
					if (portId.getName().equals(portName)) {
					    this.portName = portName;
					    return;
					}
		    	} else {
		    		if (portId.getName().startsWith(portName)) {
		    			this.portName = portId.getName();
		    			return;
		    		}
		    	}
		    }
		}

		this.portName = null;
		throw new NoSuchPortException();
    }

    /**
     * Opens the port and starts the reader thread. If the reader was already running
     * this function returns false, otherwise true.
     */
    public boolean open() throws PortInUseException {
		if (this.running) return false;
	
		serialPort = (SerialPort) portId.open("RFID Reader @ " + portName, 0);
	
		try {
		    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						   SerialPort.STOPBITS_1,
						   SerialPort.PARITY_NONE);
	
		    serialPort.enableReceiveTimeout(100);
		    //serialPort.enableReceiveThreshold(1);
	
		    in = serialPort.getInputStream();
		    out = serialPort.getOutputStream();
		} catch (Exception e) {
		    // If any of this initalization stuff fails, we're in trouble.
		    System.out.println("Cannot set port parameters for port " + portName + "!!!");
		    System.exit(1);
		}
		
		start();
		return true;
    }

    /**
     * Shuts down the reader thread and closes the serial port.
     */
    public void close() {
    	this.running = false;
    	this.queue.clear();
		serialPort.close();
    }

    /**
     * Add a listener for reply events to this reader. When a command
     * has been sent and it's reply received this received will be notified
     * with a ReplyEvent.
     * @param	listener
     * 			The listener to add to this readers notification list.
     */
    public void addListener(ReplyEventListener listener) {
    	listeners.add(listener);
    }

    /**
     * Remove a listener from this readers notification list. The listener
     * will not receive any ReplyEvents after it has been removed.
     * @param 	listener
     * 			The listener to remove from this readers notification list.
     */
    public void removeListener(ReplyEventListener listener) {
    	listeners.remove(listener);
    }
    
    /**
     * Notify the listeners that a command has been executed and it's reply
     * has been read successfully.
     * @param 	command
     * 			The command that has been executed.
     * @param 	reply
     * 			The reply received from the RFID reader after executing the <command>.
     */
    private void notifyListeners(AbstractCommand command, AbstractReply reply) {
    	for (ReplyEventListener listener : listeners)
    		listener.receivedReply(new ReplyEvent(this, command, reply));
    }

    /**
     * Send a command to the RFID reader
     * @param 	command
     * 			The command to send to the RFID reader.
     * @throws 	IOException
     * 			When the command could not be send successfully to the reader.
     */
    private void sendCommand(AbstractCommand command) throws IOException {
    	int length = command.getLength();
		out.write(command.getBytes(), 0, length);
    }
    
    /**
     * Start this readers thread.
     */
    public void start() {
    	running = true;
    	super.start();
    }
    
    /**
     * The method which executes the command that can be found in the queue.
     * Commands that request to be repeated are added again after they were
     * executed. Otherwise they will be removed after execution. When the
     * command is send to the reader, it's reply is received and all listeners
     * are notified of the successful retrieval of the reply.
     */
	public void run() {
		while (running) {
			try {
				AbstractCommand command = queue.pop();
		    	sendCommand(command);
		    	AbstractReply reply = receiveReply();
		    	
		    	if (command.isAutoRepeatEnabled())
		    		execute(command);
		    	
		    	if (reply != null)
		    		notifyListeners(command,reply);
		    } catch(Exception e) {
		    	continue;
		    }
	
		    try { Thread.sleep(this.threadSleepTime); } catch (InterruptedException e) {}
		}
	}
    
	/**
	 * Receive the reply from the RFID reader
	 * @return	A packet which contains the reply from the RFID reader.
	 * @throws 	IOException
	 * 			When the reply could not be received successfully from the reader.
	 */
    protected abstract AbstractReply receiveReply() throws IOException;
    
    /**
     * Add a command to the execution queue of this RFID reader.
     * @param 	command
     * 			The command to add to the execution queue of this RFID reader.
     */
    public synchronized void execute(AbstractCommand command) {
    	queue.add(command);
    }
    
    /**
     * Cancel the execution of a command currently in the queue.
     * @param 	command
     * 			The command to remove from the execution queue of this RFID reader.
     */
    public synchronized void cancel(AbstractCommand command) {
    	queue.remove(command);
    }
}
