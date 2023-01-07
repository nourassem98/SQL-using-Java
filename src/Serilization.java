import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class Serilization {

	public static void ser() {
		try {
			 FileOutputStream fileOut =
			         new FileOutputStream("/tmp/employee.ser");
			         ObjectOutputStream out = new ObjectOutputStream(fileOut);
			         //Record temp= new Record();
			         //out.writeObject(temp);
			         out.close();
			         fileOut.close();
			         System.out.printf("Serialized data is saved in /tmp/employee.ser");
			      } catch (IOException i) {
			         i.printStackTrace();
			      }
	}
	
 
	

	
}
