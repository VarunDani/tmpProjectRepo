import java.util.ArrayList;

public class Node {

	int id;
	int levelNumber;
	double infoGain;
	String column;
	double positiveClasses;
	double negativeClasses;
	ArrayList<Integer> totalRows;
	ArrayList<String> attributes;
	int expectedLabel;
	
	
	//For Printing Purpose
	String removedAttribute;
	int columnValue;
	
	
	Node leftNode;
	Node rightNode;
	
	//It will Help for Output observation 
	/*@Override
	public String toString() {
		return "Node [ "+removedAttribute+"="+columnValue+":"+expectedLabel+" id=" + id + ", levelNumber=" + levelNumber + ", column=" + column
				+ ", expectedLabel=" + expectedLabel + "]";
	}
	*/
	
	@Override
	public String toString() {
		return removedAttribute+" = "+columnValue+" : "+expectedLabel;
	}
	
	
	
	
}
