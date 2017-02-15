import java.util.ArrayList;

public class Instance {

	private ArrayList<Double> data = new ArrayList<Double>();
	private int id;
	
	public ArrayList<Double> getData() {
		return data;
	}

	public void setData(ArrayList<Double> data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Instance [data=" + data + ", id=" + id + "]";
	}
	
	
}
