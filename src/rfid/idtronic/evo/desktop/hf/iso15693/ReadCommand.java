package rfid.idtronic.evo.desktop.hf.iso15693;

import rfid.idtronic.evo.desktop.hf.EDHFCommand;

/**
 * Class representing an iso15693 read command for the iDtronic EVO DESKTOP USB HF RFID Reader.
 */
public class ReadCommand extends EDHFCommand {	
	public ReadCommand(int flags, int firstBlockNb, int nbOfBlocks) {
		this(flags, firstBlockNb, nbOfBlocks,false);
	}
	
	public ReadCommand(int flags, int firstBlockNb, int nbOfBlocks, boolean autoRepeat) {
		super(0x11, 
				new byte[]{
				(byte)flags, 
				(byte)firstBlockNb, 
				(byte)nbOfBlocks
				}
		,autoRepeat);
	}
}
