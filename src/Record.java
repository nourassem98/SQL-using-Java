import java.util.Vector;

public class Record implements  java.io.Serializable {
	Vector <Object> tuples;
	public Object key;

	public Record(Vector<Object> tuples) {
		super();
		this.tuples = tuples;
		this.key = tuples.get(0);
	}
	
	public Boolean equal(Record x){
		for(int i=0;i<tuples.size();i++){
			if(!(tuples.get(i)+"").equals(x.tuples.get(i)+""))
				return false;
		}
		return true;
	}
	
	public Object getObject(int i)
	{
		return tuples.get(i);
	}
	
}