package rfid;

import java.util.Arrays;

/**
 * Class representing an abstract packet of bytes.
 */
public abstract class AbstractPacket {
	private long lastSeen;
	
	public AbstractPacket() {
		bump();
	}
	
	/**
	 * Calculate and retrieve the checksum for this packet.
	 * @return	The checksum for this packet.
	 */
	public abstract int getChecksum();
	
	/**
	 * Retrieve the bytes of which this packet consists.
	 * @return	The bytes of which this packet consists.
	 */
	public abstract byte[] getBytes();
	
	/**
	 * Retrieve the data contained in this packet.
	 * @return	The data contained in this packet.
	 */
	public abstract byte[] getData();
	
	/**
	 * Retrieve the length of the packet.
	 * @return	The total amount of bytes in this packet.
	 */
	public int getLength() {
		return getBytes().length;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i=0; i < getLength(); i++) {
			result += Integer.toString((getBytes()[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
	
	/**
	 * Update the time this packet has last been seen.
	 */
	public void bump() {
    	this.lastSeen = System.currentTimeMillis();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(getBytes());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractPacket other = (AbstractPacket) obj;
		if (!Arrays.equals(getBytes(), other.getBytes()))
			return false;
		return true;
	}
	
	
}
