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
package org.onsignal.rfid;


import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class RFIDReader extends Thread
{
    private boolean verbose = false;

    private CommPortIdentifier portId = null;
    private String portName;
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private boolean running = false;

    private Hashtable<String, RFIDTag> tags = null;

    private int threadSleepTime = 10;
    private int tagRemovedTimeout = 300;

    private Vector<RFIDListener> listeners;


    /**
     * Constructor; pass in the name of the port the reader is
     * connected to. The port will be opened and the reader will be
     * started.
     */
    public RFIDReader(String portName) throws NoSuchPortException, PortInUseException 
    {
		this();
		this.setPort(portName);
		this.open();
    }

    /**
     * Constructor using a port name and an instance of a listener
     * object which will be notified on tag events.
     */
    public RFIDReader(String portName, RFIDListener listener) throws NoSuchPortException, PortInUseException 
    {
		this(portName);
		this.addListener(listener);
    }

    /**
     * COnstructor without port name, no serial port will be opened,
     * you'lll have it to do yourself using setPort() and open(). You
     * can pass in a listener object, though.
     */
    public RFIDReader(RFIDListener listener) 
    {
		this();
		this.addListener(listener);
    }

    /**
     * The basic constructor. Use RFIDReader(String portName) for more convenience.
     */
    public RFIDReader() 
    {
		this.listeners = new Vector<RFIDListener>();
		this.tags      = new Hashtable<String, RFIDTag>();
    }


    /**
     * Set or change the name of the port. If the reader is running,
     * it will be shut down first. You'll have to call open() again to
     * restart the reader on the new port.
     */
    public void setPort(String portName) throws NoSuchPortException 
    {
		if (this.running) this.close();
		
		Boolean glob = false;
		if (portName.endsWith("*"))
		{
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
     * Returns the current tag, or null if there is no tag at the reader right now.
     */
    public Collection<RFIDTag> getCurrentTags() 
    {
    	return this.tags.values();
    }


    /**
     * Opens the prot and starts the reader thread. If the reader was already running this function returns false, otherwise true.
     */
    public boolean open() throws PortInUseException 
    {
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
	
		running = true;
		//start();
	
		return true;
    }

    /**
     * Shuts down the reader thread and closes the serial port.
     */
    public void close() 
    {
		this.running = false; // stops reader thread
		this.tags    = new Hashtable<String, RFIDTag>();
		serialPort.close();
    }

    /**
     * Set the verbosity of this reader. By default, the reader is
     * verbose, e.g., messages will be printed when a tag is read and
     * removed. To mute the reader, call setVerbose(false).
     */
    public void setVerbose(boolean verbose) 
    {
    	this.verbose = verbose;
    }

    /**
     * Adds a listener for this reader
     */
    public void addListener(RFIDListener l) 
    {
    	listeners.add(l);
    }

    /**
     * Removes a listener
     */
    public void removeListener(RFIDListener l) 
    {
    	if (listeners.contains(l)) 
    	{
    		listeners.remove(l);
    	}
    }

    /**
     * Set the timeout for the removal of a tag, in
     * milliseconds. Defaults to 500 ms. When -1 is passed, tags will
     * never be marked as 'removed', e.g., getCurrentTags() will always
     * return all tags that have been read.
     */
    public void setTagRemoveTimeout(int timeout) 
    {
    	this.tagRemovedTimeout = timeout;
    }

    /**
     * Represent this reader as a string for easy printing
     */
    public String toString() 
    {
    	return "[RFID Reader @ " + this.portName + "]";
    }

    //////////////////////////////////// internal ////////////////////////////////////////

    private void verbose(String s) 
    {
		if (this.verbose)
		{
		    System.out.println(this + ": " + s);
		}
    }

    /**
     * Do not call run() directly! It is called for you when open() was succesful.
     */
    public void run() 
    {

		while (running) {
		    try {
			this.sendCommand(0x82); // seek for tag
			this.handleResponse();
		    } catch(Exception e) {
			this.verbose("Seek for tag failed!");
		    }
	
		    if (this.tagRemovedTimeout < 0) {
			// Do not check for tag remove timeouts
			continue;
		    }
	
		    long now = System.currentTimeMillis();
	
		    Iterator it = this.tags.values().iterator();
		    while (it.hasNext()) {
			RFIDTag tag;
			try {
			    tag = (RFIDTag)it.next();
			} catch (ConcurrentModificationException e) {
			    // Other thread changed the tags; skip this loop.
			    break;
			}
	
			if (now - tag.lastSeen > this.tagRemovedTimeout) {
	
			    // It has been removed
			    this.verbose("Tag removed");
			    this.tags.remove(tag.tag);
	
			    for (Enumeration e = listeners.elements(); e.hasMoreElements(); )
				((RFIDListener)e.nextElement()).tagRemoved(new RFIDTagEvent(this, tag));
			}
		    }
		    try { Thread.sleep(this.threadSleepTime); } catch (InterruptedException e) {}
		}
    }

    private void tagSeen(RFIDTag tag) 
    {

		if (!this.tags.containsKey(tag.tag)) {
		    // new tag
	
		    this.verbose("Got tag: " + tag);
		    this.tags.put(tag.tag, tag);
	
		    for (Enumeration e = listeners.elements(); e.hasMoreElements(); )
			((RFIDListener)e.nextElement()).tagAdded(new RFIDTagEvent(this, tag));
		}
	
		// bump 'last seen' timer for seen tag
		this.tags.get(tag.tag).bump();
    }
	
    private void sendCommand(int command) throws IOException 
    {
		int[] data = {};
		sendCommand(command, data);
    }

    public void sendCommand(int cmd, int[] data) throws IOException 
    {
		byte[] command = new byte[255];
		command[0] = (byte)0xAA;
		command[1] = (byte)0x00;
		command[2] = (byte)(data.length+1);
		command[3] = (byte)cmd;
	
		int checksum = command[1]^command[2]^command[3];
	
		for (int i=0; i<data.length; i++) {
		    command[4+i] = (byte)data[i];
		    checksum ^= data[i];
		}
	
		command[4+data.length] = (byte)checksum;
		command[4+data.length+1] = (byte)0xBB;
		
		int length = 6+data.length;
		
		String result = "";
		  for (int i=0; i < length; i++) {
		    result +=
		          Integer.toString((command[i] & 0xff) + 0x100, 16).substring(1);
		  }
		  
		  System.out.println(result);
		
		out.write(command, 0, length);
    }

    private void handleResponse() throws IOException 
    {
		int[] data;
		byte[] r = new byte[1];
		int read;
		int length;
		int command;
	
		read = in.read(r, 0, 1);
		if (read != 1 || r[0] != -1) return; // throw new Error("Protocol error!");
	
		read = in.read(r, 0, 1);
		if (read != 1 || r[0] != 0) return; // throw new Error("Protocol error!");
	
		read = in.read(r, 0, 1);
		if (read != 1) return; // throw new Error("Protocol error!");
	
		length = r[0]-1;
		data = new int[length];
	
		read = in.read(r, 0, 1);
		if (read != 1) return; // throw new Error("Protocol error!");
	
		command = r[0] & 0xFF;
	
		if (length > 0) {
		    for (int i=0; i<length; i++) {
			read = in.read(r, 0, 1);
			if (read == 0) return; // throw new Error("Protocol error!");
			data[i] = r[0] & 0xFF;
		    }
		}
	
		// read checksum
		read = in.read(r, 0, 1);
		if (read != 1) return; // throw new Error("Protocol error!");
	
		switch(command) {
		case 0x81:
		    this.printReaderVersion(data);
		    break;
	
		case 0x82:
		    this.handleSeekForTag(data);
		    break;
		}
	
    }

    private void printReaderVersion(int[] data) 
    {
		System.out.print("RFID Reader version: ");
		for (int i=0; i<data.length; i++)
		    System.out.print(Integer.toHexString(data[i]));
		System.out.println("");
    }

    private void handleSeekForTag(int[] data) throws IOException 
    {
		if (data.length == 1) {
		    // command in progress
		    if (data[0] == 0x55)
			this.verbose("RF field is off!!!!!");
	
		    // wait for tag to enter field
		    this.handleResponse();
		    return;
		}
	
		RFIDTag tag = new RFIDTag(data);
		this.tagSeen(tag);
    }
}
