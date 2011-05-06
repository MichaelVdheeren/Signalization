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

/**
 * This class represents an RFID tag.
 * 
 * @author arjan
 */
public class RFIDTag
{
    public int[] data;
    public int type;
    public String tag;

    public long longvalue;
    public long lastSeen;

    /**
     * The constructor. A raw data package is passed in in the same
     * way it was read from the reader: the first byte represents the
     * tag type, the other bytes represent the tag serial number.
     */
    public RFIDTag(int[] data) 
    {
		this.data = data;
		this.type = data[0];
		this.longvalue = 0;
	
		StringBuffer s = new StringBuffer();
		for (int i=1; i<data.length; i++) {
		    String hex = Integer.toHexString(data[i]).toUpperCase();
		    if (hex.length() == 1)
			s.append("0");
		    s.append(hex);
		    if (i < data.length-1) s.append(":");
	
		    this.longvalue += data[i] << ((i-1)*8);
		}
	
		this.tag = s.toString();
    }

    /**
     * This function returns the type of the tag as a string
     */
    public String getTagTypeString() 
    {
		switch (type) {
		case 0x01:
		    return "Mifare Ultralight";
		case 0x02:
		    return "Mifare Standard 1k";
		case 0x03:
		    return "Mifare Classic 4k";
		}
		
		return "Unknown tag type";
    }

    /**
     * Represent this tag as a string. The tag represents its data in
     * hexadecimal byte-format, with lowercase letters, separated by
     * colons, e.g. "4a:9a:c8:b1"
     */
    public String toString() 
    {
    	return this.tag;
    }

    /**
     * Test for equation with 'raw' reader data. This method is only
     * here for performance reasons, please use one of the other
     * equals() functions instead as they are easier to implement.
     */
    public boolean equals(int[] data) 
    {
		if (data.length != this.data.length) return false;
	
		for (int i=0; i<this.data.length; i++)
		    if (this.data[i] != data[i]) return false;
	
		return true;
    }

    /**
     * Test for equation with another RFID tag.
     */
    public boolean equals(RFIDTag t) 
    {
		if (t.data.length != this.data.length) return false;
		return t.longvalue == this.longvalue;
	}

    /**
     * Test for equation with a string. The tag represents its data in
     * hexadecimal byte-format, with lowercase letters, separated by
     * colons, e.g. "4a:9a:c8:b1"
     */
    public boolean equals(String s) 
    {
    	return this.tag.equals(s);
    }

    public void bump() 
    {
    	this.lastSeen = System.currentTimeMillis();
    }
}
