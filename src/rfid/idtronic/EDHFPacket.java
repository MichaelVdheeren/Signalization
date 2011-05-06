package rfid.idtronic;

import rfid.RFIDPacket;

public abstract class EDHFPacket extends RFIDPacket {
	private final int stx = 0xaa;
	private final int etx = 0xbb;
	
	public EDHFPacket() {}

	public abstract int getDeviceAddress();
	
	public int getStartOfPacket() {
		return this.stx;
	}
	
	public int getEndOfPacket() {
		return this.etx;
	}
	
	
}
