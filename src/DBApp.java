import java.awt.Polygon;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.*;


public class DBApp {
	
	static Vector <String> tables;
	static int maxsize;
	
	
	public DBApp(){
		
		tables = new Vector <String>();
		
	}
	public void init( ) {
		
	}
	
	public void createTable(String strTableName, String strClusteringKeyColumn,	Hashtable<String,String> htblColNameType ) throws IOException {
		
		String eol = System.getProperty("line.separator");

		try (Writer writer = new FileWriter("metadata.csv",true)) {
			
			BufferedReader br = new BufferedReader(new FileReader("metadata.csv"));
	        String line = br.readLine();

	        while (line != null) {

	            String[] meta = line.split(",");
	            if(meta[0].equals(strTableName)) {
	            	Exception x = new Exception("This table already exists");
	            	System.out.println("table exists");
	            	return;
	            }

	            line = br.readLine();
	        }
	        
			
			for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
			  if(strClusteringKeyColumn==entry.getKey()){
				  writer.append(strTableName)
	          	  .append(',')
		    	  .append(entry.getKey())
		          .append(',')
		          .append(entry.getValue())
		          .append(',')
		          .append("true")
		          .append(",")
		          .append("false")    //for indexing
		          .append(eol);
			  }
			}
				for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
					  if(strClusteringKeyColumn!=entry.getKey()){
						  writer.append(strTableName)
			          	  .append(',')
				    	  .append(entry.getKey())
				          .append(',')
				          .append(entry.getValue())
				          .append(',')
				          .append("false")
				          .append(",")
				          .append("false")    //for indexing
				          .append(eol);
					  }
				}
		
		
		String tableName = strTableName;
		Table newTable = new Table();
		File tablePath = new File(tableName);
		tablePath.createNewFile();
		tables.add(tableName);
		serialize(tableName,newTable);
		
		}
		//c++;
		
		
	}
	
	public void insertIntoTable(String strTableName,
			 Hashtable<String,Object> htblColNameValue) throws ClassNotFoundException, IOException {
	    
	    	BufferedReader br = new BufferedReader(new FileReader("metadata.csv"));
	        String line = br.readLine();
	        
	        HashMap<String, String> htblColNameType = new HashMap<>();
	        boolean flaj=false;
	        while (line != null) {

	            String[] meta = line.split(",");
	            if(meta[0].equals(strTableName)) {
	            	htblColNameType.put(meta[1],meta[2]);
	            	flaj=true;
	            }

	            line = br.readLine();
	           
	        }
	        if(flaj==false) {
        		Exception x = new Exception ("ERROR table not found");
        		System.out.println("table doesnot exist");
            	return;
        	}
	        
	        String column;

        	if(htblColNameValue.size() != htblColNameType.size() ) {
        		Exception x = new Exception ("ERROR not same size");
        		System.out.println("not same size");
            	return;
            	
        	}
        	
        	Hashtable<String,Object> insert= new Hashtable<String,Object>();
        	
	        
	        
	        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) 
	        
	        {
	        	column=entry.getKey();
	        	
	        	if(	!	("class "+entry.getValue()).equalsIgnoreCase(htblColNameValue.get(column).getClass()+"")) {
	        		Exception x = new Exception ("ERROR different types");
	        		System.out.println("incompatible types");
	            	return;
	        	}
	        	insert.put(column, htblColNameValue.get(column));
	        }
	        

	        
	        	 // ha loop 3al hashtaple w adakhal kol el infos fe record then in page w ha save esm el page fel vector 
		        
	        	// I insert the rdata in a new record to be inserted to a page
	        	
	        	Vector<Object> v = new Vector<Object>();
		        
		        Set<String> keys = insert.keySet();
		        for(String key: keys){
		            v.add(insert.get(key));
		        }
		        
		       
		        
		        FileReader filemaxsizeofPage = new FileReader("config.properties");
		        BufferedReader br1 = new BufferedReader(filemaxsizeofPage);
		        String line1 = br1.readLine();
		        
		       
		        //while (line1 != null) {
	
		        String[] meta1 = line1.split(" ");
	            int maxsize = Integer.parseInt(meta1[2]);
	            line1 = br1.readLine();
		        //}
		        
		        
		        
		        Record recordToBeInserted = new Record(v);
		        
		        
		        for(int i = 0; i<tables.size();i++)
		        {

		        	if(tables.get(i).equals(strTableName) )
		        	{
		        		Table t = (Table) deSerialize(tables.get(i));
		        		if(t.getSize()==0) {
		        			Page p=new Page(strTableName,maxsize);
		        			p.add(recordToBeInserted);
		        			
		        			t.addPage(p);
		        			serialize(p.filename,p);
		        		}
		        		else {
		        		String p = t.getPage(t.getSize()-1);
		        		Page nour = (Page)deSerialize(p);
		        		
						if (nour.getSize()== maxsize)
						{
							serialize(p,nour);
					        Page pnew = new Page(strTableName,maxsize);
					        pnew.add(recordToBeInserted);

							
							t.addPage(pnew);
							serialize(pnew.filename,pnew);
							
						}
						else{
							nour.add(recordToBeInserted);
							serialize(nour.filename,nour);
							
						}
		        		}
						sort(t);
						serialize(t.name,t);
						break;
		        	}
		        	
		        }
		  
	}
	
	public void updateTable(String strTableName,String strKey,Hashtable<String,Object> htblColNameValue ) throws IOException, ClassNotFoundException 
	{
		
		BufferedReader br = new BufferedReader(new FileReader("metadata.csv"));
        String line = br.readLine();
        
        HashMap<String, String> htblColNameType = new HashMap<>();
        boolean flaj=false;
        String keyname;
        while (line != null) {

            String[] meta = line.split(",");
            if(meta[0].equals(strTableName)) {
            	htblColNameType.put(meta[1],meta[2]);
            	flaj=true;
            	if(meta[1].equals("TRUE")) {
            		keyname=meta[1];
            	}
            }

            line = br.readLine();
           
        }
        if(flaj==false) {
    		Exception x = new Exception ("ERROR table not found");
    		System.out.println("table doesnot exist");
        	return;
    	}
        
        String column;

    	if(htblColNameValue.size() != htblColNameType.size() ) {
    		Exception x = new Exception ("ERROR not same size");
    		System.out.println("not same size");
        	return;
        	
    	}
    	
    	Hashtable<String,Object> insert= new Hashtable<String,Object>();
    	
        
        
        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) 
        
        {
        	column=entry.getKey();
        	
        	if(	!	("class "+entry.getValue()).equalsIgnoreCase(htblColNameValue.get(column).getClass()+"")) {
        		Exception x = new Exception ("ERROR different types");
        		System.out.println("incompatible types");
            	return;
        	}
        	insert.put(column, htblColNameValue.get(column));
        }
        
       
        
        Vector<Object> v = new Vector<Object>();
        
        Set<String> keys = insert.keySet();

        for(String key: keys){
            v.add(insert.get(key));
        }
        
        Record r = new Record (v);
        
        
        
        
        for (int k = 0 ; k<tables.size();k++)
        {
        	if(tables.get(k)==strTableName )
        	{
        		Table t = (Table)deSerialize(tables.get(k));
        		for(int i=0;i<t.pages.size();i++)
        		{
    				Page p = (Page)deSerialize(t.pages.get(i));
    				for(int j=0;j<p.getSize();j++)
    				{
    					if((p.records.get(i).getObject(0)+"").equals(r.tuples.get(0)+""))
    					{
    						p.records.set(i,r);
    					}
    				}
    				serialize(p.filename,p);
    				
        		}
        		sort(t);
        		serialize(t.name,t);
        	}
        }
        
		
	}
			
	public void deleteFromTable(String strTableName,Hashtable<String,Object> htblColNameValue) throws ClassNotFoundException, IOException
	{
		
		BufferedReader br = new BufferedReader(new FileReader("metadata.csv"));
        String line = br.readLine();
        
        HashMap<String, String> htblColNameType = new HashMap<>();
        boolean flaj=false;
        String keyname;
        while (line != null) {

            String[] meta = line.split(",");
            if(meta[0].equals(strTableName)) {
            	htblColNameType.put(meta[1],meta[2]);
            	flaj=true;
            	if(meta[1].equals("TRUE")) {
            		keyname=meta[1];
            	}
            }

            line = br.readLine();
           
        }
        if(flaj==false) {
    		Exception x = new Exception ("ERROR table not found");
    		System.out.println("table doesnot exist");
        	return;
    	}
        
        String column;

    	if(htblColNameValue.size() != htblColNameType.size() ) {
    		Exception x = new Exception ("ERROR not same size");
    		System.out.println("not same size");
        	return;
        	
    	}
    	
    	Hashtable<String,Object> insert= new Hashtable<String,Object>();
    	
        
        
        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) 
        
        {
        	column=entry.getKey();
        	
        	if(	!	("class "+entry.getValue()).equalsIgnoreCase(htblColNameValue.get(column).getClass()+"")) {
        		Exception x = new Exception ("ERROR different types");
        		System.out.println("incompatible types");
            	return;
        	}
        	insert.put(column, htblColNameValue.get(column));
        }
        
       
        
        Vector<Object> v = new Vector<Object>();
        
        Set<String> keys = insert.keySet();

        for(String key: keys){
            v.add(insert.get(key));
        }
        
        Record r = new Record (v);
        
        
        
        for (int i = 0 ; i<tables.size();i++)
        {
        	if(tables.get(i)==strTableName )
        	{
        		Table t = (Table)deSerialize(tables.get(i));
        		delete(t,r);
        		serialize(t.name,t);
        	}
        	
        }			
				
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
		
		
		public void sort(Table T) throws ClassNotFoundException, IOException { //beysort autoamtic bas linear
			int n=T.pages.size()-1;
			System.out.println(T.pages.get(n));
			System.out.println(n);
			Page currentPage =(Page) deSerialize(T.pages.get(n));
			if(n==0 && currentPage.records.size()==1 )
				return;
			for(int lastRecordIndex=currentPage.records.size()-1;
					lessThan(currentPage.records.get(lastRecordIndex).key,currentPage.records.get(lastRecordIndex-1).key);
					lastRecordIndex--) {
				
				Record tempo=currentPage.records.get(lastRecordIndex);
				currentPage.records.add(lastRecordIndex,currentPage.records.get(lastRecordIndex-1));
				currentPage.records.add(lastRecordIndex-1,tempo);
				
				if(lastRecordIndex==1 && n==0) {
					break;
				}
				
				if(lastRecordIndex==1 && n!=0) {
					Object kk=currentPage.records.get(0).key;
					Object kk2=((Page) deSerialize(T.pages.get(n-1))).records.get(((Page) deSerialize(T.pages.get(n-1))).records.size()-1).key;
					if(lessThan(kk,kk2)) {
						Record tempo2=currentPage.records.get(0);
						Page Temp= ((Page) deSerialize(T.pages.get(n-1)));
						currentPage.records.add(0,Temp.records.get(	Temp.records.size() -1	)	);
						Temp.records.add(Temp.records.size()-1,tempo2);
						n--;
						currentPage=((Page) deSerialize(T.pages.get(n)));
						lastRecordIndex=currentPage.records.size()-1;
					}
					else break;
				}
				
				
			}
			
			
		}
		
		public static boolean lessThan(Object x,Object y) {
			//java.lang.Integer, java.lang.String,java.lang.Double, java.lang.Boolean, java.util.Date and java.awt.Polygon
			String type=""+x.getClass();
			if(type.contains("Integer") && ((int) x )<( (int) y)	) {
				return true;
			}
			else if(type.contains("String") &&	((String) x ).compareTo(( (String) y) )<0){
				return true;
			}
			else if(type.contains("Double") && ((Double) x )<( (Double) y)){
				return true;
			}
			else if(type.contains("Boolean") && Boolean.compare((boolean) x, (boolean) y) >0){
				return true;
			}
			else if(type.contains("Date") && ((Date) x).after((Date) y) ){
				return true;
			}
			else if(type.contains("Polygon") ){	
				PolygonDb poly1 = new PolygonDb(((Polygon) x));
				PolygonDb poly2 = new PolygonDb(((Polygon) y));
				if(poly1.Area<poly2.Area)
					return true;
			}
			
			return false;
		}



		public static boolean delete(Table t,Record R) throws ClassNotFoundException, IOException{
			
			
			for(int i=0;i<t.pages.size();i++){
				Page p = (Page)deSerialize(t.pages.get(i));
				for(int j=0;j<p.getSize();j++){
					if((p.records.get(i)).equals(R)){
						p.records.remove(j);
						return true;
					}
				}
				
			}
			return false;
		}

	public static void main(String []args) throws IOException, ClassNotFoundException {
		
		DBApp a = new DBApp();
		String strTableName = "u2w";
		
		
		Hashtable htblColNameType = new Hashtable( );
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.double");
		a.createTable(strTableName, "id", htblColNameType);
		
		Hashtable htblColNameValue = new Hashtable( );
		

		
		for(int i=50;i<100;i++) {
		htblColNameValue.clear();

		htblColNameValue.put("id", i);
		htblColNameValue.put("gpa", new Double( 0.7 ) );
		htblColNameValue.put("name", new String("Nadeem" ) );
		
		a.insertIntoTable(strTableName , htblColNameValue );
		}
		

		System.out.println("ended");

		Table t=(Table) deSerialize(tables.get(0));
		System.out.println( t.pages	);
		
		/*File f = new File("test.class");
		createNewFile();*/
		
		
	}
}