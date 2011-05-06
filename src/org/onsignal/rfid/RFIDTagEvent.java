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

import java.util.*;


public class RFIDTagEvent extends EventObject
{
	public static final long serialVersionUID = 1000102;
	
    private RFIDTag tag;

    public RFIDTagEvent(RFIDReader source, RFIDTag tag) 
    {
		super(source);
		this.tag = tag;
    }

    public RFIDTag getTag() 
    {
		return this.tag;
    }

    public RFIDReader getReader() 
    {
    	return (RFIDReader)this.source;
    }
}
