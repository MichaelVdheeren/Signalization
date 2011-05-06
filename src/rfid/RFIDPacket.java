package rfid;

public abstract class RFIDPacket {
	public abstract int getChecksum();
	public abstract byte[] getBytes();
	public abstract byte[] getData();
	
	public int getLength() {
		return getBytes().length;
	}
	
	public String toString() {
		String result = "";
		for (int i=0; i < getLength(); i++) {
			result += Integer.toString((getBytes()[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
