//displayserver.DisplayAdmin.java
import java.net.*;
 
class DisplayAdmin
{
	public static void main(String arg[]) throws Exception {
		Socket s = new Socket("127.0.0.1", 2021);
		s.getOutputStream().write('X');
		s.close();
	}
}