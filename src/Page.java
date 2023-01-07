import java.util.Arrays;
import java.util.Vector;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Integer; 
import java.lang.String;
import java.lang.Double ;
import java.lang.Boolean ;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.awt.Polygon;

public class Page implements java.io.Serializable  {
	
	 Vector <Record> records;
	 public String filename;
	 static int c;
	
	
	public Page(String tablename,int n)
	{
		records = new Vector<Record>(n);
		filename=tablename+c+"";
		c++;
	}
	public int getSize()
	{
		 return records.size();
	
		 
	}
	public void add(Record r) {
		records.add(r);
		// TODO Auto-generated method stub
		
	}
		
}