/**
 *
 * Mifare Java RFID library
 *
 * Copyright 2008 Arjan Scherpenisse, <arjan@scherpenisse.net>
 *
 * Website: http://www.on-signal.org/projects/RFID_Processing
 * ---------------------------------------------------------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package rfid;


import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

public abstract class RFIDReader extends Thread
{
    private CommPortIdentifier portId = null;
    private String portName;
    private SerialPort serialPort;
    protected InputStream in;
    protected OutputStream out;
    protected boolean running = false;

    private Vector<RFIDListener> listeners = new Vector<RFIDListener>();
    
    protected int threadSleepTime = 10;

    public RFIDReader(String portName) throws NoSuchPortException {
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
		
		return true;
    }

    /**
     * Shuts down the reader thread and closes the serial port.
     */
    public void close() {
		serialPort.close();
    }

    /**
     * Adds a listener for this reader
     */
    public void addListener(RFIDListener l) {
    	listeners.add(l);
    }

    /**
     * Removes a listener
     */
    public void removeListener(RFIDListener l) {
    	listeners.remove(l);
    }

    protected void sendPacket(RFIDPacket packet) throws IOException {
    	int length = packet.getLength();
		out.write(packet.getBytes(), 0, length);
    }
    
    public void start() {
    	running = true;
    	super.start();
    }
    
    protected abstract RFIDPacket receivePacket() throws IOException;
}
