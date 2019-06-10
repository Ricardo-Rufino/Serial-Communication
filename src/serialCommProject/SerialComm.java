package serialCommProject;

import com.fazecast.jSerialComm.SerialPort;

public class SerialComm {

	public static void main(String[] args) {
		
		SerialPort portNames[] = SerialPort.getCommPorts();
		
		for(int i =  0; i < portNames.length; i++){
			System.out.println(portNames[i]);
		}
	}
	
}
