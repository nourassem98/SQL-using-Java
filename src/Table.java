import java.beans.Transient;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class Table implements java.io.Serializable{
	
	//transient Vector <Page> pages;
	Vector <String> pages;
	//need to create a page fadya hena
	String name;
	String key;
	static int c = 0;
	
	 public Table(){
		 pages = new Vector<String>();
	 }
	
	
	
	
	public int getSize()
	{
		 return pages.size();
	}
	
	public String getPage(int i)
	{
		return pages.get(i);
	}
	public void addPage(Page p) throws IOException
	{
		String pagename = p.filename;
		
		File pagePath = new File(pagename);
		pagePath.createNewFile();
		System.out.println("ana hena");
		pages.add(pagename);
		serialize(pagename,p);
		
		
		//pages.add(p.createNewFile1());
	}
	
	public void createPage() throws IOException
	{
		File a = new File(name+c+".class");
		a.createNewFile();
		pages.add(name+c+".class");
		c++;
		
	}
	
	public static  Object deSerialize(String name) throws IOException, ClassNotFoundException {
		
		File file = new File (name+".class");
		
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		return object;
			
}

	public static void serialize(String name, Object object) throws IOException {
		
		File file = new File (name+".class");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
	}
	
	
}