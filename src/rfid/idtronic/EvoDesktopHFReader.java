package rfid.idtronic;

public class EvoDesktopHFReader {
	public EvoDesktopHFReader() {
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
	}
	
	
}
