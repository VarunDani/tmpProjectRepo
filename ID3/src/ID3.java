import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ID3 {

	private static int nodeNumber = 0;
	
	public static void main(String[] args) {
		
		
		HashMap<String,ArrayList<Double>> vericalData = new HashMap<String,ArrayList<Double>> ();
		
		ArrayList<Instance> dataList = new ArrayList<Instance>();
		ArrayList<String> attributeList =  new ArrayList<String>();
		ArrayList<Integer> totalRows = new ArrayList<Integer>();
		
		//Reading Data File and fir it to structure 
		readDataFile(args[0],dataList,attributeList,totalRows);
		 
		
		//Set Custom Data-Structures
		setCustomDataStructure(vericalData,dataList,attributeList);
		
		
		//Removing Class Column from Attribute table 
		attributeList.remove(attributeList.size()-1);
		
		//Printing Sample Data - TODO Remove
		//System.out.println(vericalData);
		
		
		//Entropy Calculation System - Generic method 
		//System.out.println(calculateEntropyAll(attributeList, totalRows, vericalData));
		
		// Generate Binary Tree Here
		nodeNumber = 0;
		Node root = generateTree(null,totalRows,attributeList,vericalData,1,"",0);
		
		Node traversalRoot = root;
		traverseTree(traversalRoot);
		
		
	/*	1. Create New Node 
		2. Assign totalRows and attributes
		3. Calculate Information Gain and Get Column 
		4. Save That InfoGain and Column to Node
		5. Calculate New totalRows, attributes and positiveClasses , negativeClasses
		6. Split Node and Generate Left and Right Node*/
		
		//Stop Condition 
/*		1. until positiveClasses==0 or negativeClasses==0 in Node 
		2. All have zero Info Gain (optional)*/
		
		
		
		
		
	}


	private static void traverseTree(Node root) {

		if(root!=null){
			
			int spaceAllocation = (root.levelNumber*1);
			String a  = "";
			StringBuilder sb = new StringBuilder();
			
			for(int i=1;i<spaceAllocation;i++){
				sb.append("  ");
				sb.append("|");
			}
			
			if(sb.length()>0)
			sb.deleteCharAt(sb.lastIndexOf("|"));
			System.out.println(sb.append(root.toString()).toString());
			traverseTree(root.leftNode);
			traverseTree(root.rightNode);
		}
	}


	private static Node generateTree(Node root,ArrayList<Integer> totalRows,ArrayList<String> attributeList,
			HashMap<String,ArrayList<Double>> vericalData,int levelNumber,String removedAttribute,int columnValue) {
		
		if(root==null){
			root = new Node();
			root.id = ++nodeNumber;
			root.levelNumber = levelNumber;
			
			root.totalRows = (ArrayList<Integer>) totalRows.clone();
			root.attributes = (ArrayList<String>) attributeList.clone();
			String[] returnString = calculateEntropyAll(attributeList, totalRows, vericalData);
			
			//Setting Attribute
			root.column = returnString[0];
			//Setting Info gain at that Node 
			root.infoGain = Double.valueOf(returnString[1]);
			
			double[] classesLabel = getClassesLabel(totalRows,vericalData);
			
			root.negativeClasses = classesLabel[0];
			root.positiveClasses = classesLabel[1];
			
			root.expectedLabel = root.negativeClasses<root.positiveClasses ? 1 : root.negativeClasses>root.positiveClasses?0:1;
			
			root.removedAttribute = removedAttribute;
			root.columnValue = columnValue;
			
			//Put end Conditions 
			if(root.negativeClasses!=0 && root.positiveClasses!=0)
			{
				//Removing attribute 
				root.attributes.remove(root.attributes.indexOf(root.column));
				
				root.leftNode = generateTree(root.leftNode, calculateTotalRows(root.column,totalRows,vericalData,0), root.attributes, vericalData,levelNumber+1,root.column,0);
				root.rightNode = generateTree(root.rightNode, calculateTotalRows(root.column,totalRows,vericalData,1), root.attributes, vericalData,levelNumber+1,root.column,1);
			}
		}
		return root;
		
	}



	


	private static ArrayList<Integer> calculateTotalRows(String column, ArrayList<Integer> totalRows,
			HashMap<String, ArrayList<Double>> vericalData, double i) 
	{
		
		ArrayList<Integer>  newTotalRows = new  ArrayList<Integer>();
		ArrayList<Double> columnData = vericalData.get(column);
		
		for(int index : totalRows){
			if(columnData.get(index)==i){
				newTotalRows.add(index);
			}
		}
			
		return newTotalRows;
	}


	private static double[] getClassesLabel(ArrayList<Integer> totalRows, HashMap<String, ArrayList<Double>> vericalData) {
		
		 ArrayList<Double> classValues = vericalData.get("Class");
		 double[] classesLabel = new double[2];
		 
		 int positiveClasses = 0;
		 int negativeClasses = 0;
		 
		for(int index : totalRows){
			if(classValues.get(index)==1){
				++positiveClasses;
			}
			else{
				++negativeClasses;
			}
		}
		classesLabel[0] = negativeClasses;
		classesLabel[1] = positiveClasses;
		
 		return classesLabel;
	}


	private static String[] calculateEntropyAll(ArrayList<String> attributeList,ArrayList<Integer> totalRows,HashMap<String,ArrayList<Double>> vericalData) {
		
		Double maxInfoGain = -1D;
		String attribute = "";
		String[] returnString = new String[2];
		
		for(String columnName : attributeList){
			Double tmpInfoGain = calculateEntropy(columnName, totalRows, vericalData);
			if(tmpInfoGain>maxInfoGain)
			{
				maxInfoGain = tmpInfoGain;
				attribute = columnName;
			}
		}
		
		returnString[0]=attribute;
		returnString[1]=String.valueOf(maxInfoGain);
		return returnString;
	}


	private static double calculateEntropy(String columnName,ArrayList<Integer> rows,HashMap<String,ArrayList<Double>> vericalData) {
		
		ArrayList<Double> columnData = vericalData.get(columnName);
		ArrayList<Double> labelData = vericalData.get("Class");
		double infoGain = 0D;
		
		double c_00 = 0;
		double c_01 = 0;
		double c_10 =0;
		double c_11 = 0;
						 		
				
			for(int row : rows){

				Double leftValue = columnData.get(row);
				Double rightValue = labelData.get(row);
				
				
				//Calculating Counter for 0 and 1 
					if(leftValue==0 && rightValue==0)
						++c_00;
					else if(leftValue==0 && rightValue==1)
						++c_01;
					else if(leftValue==1 && rightValue==0)
						++c_10;
					else 
						++c_11;
			}	
				//Total Size 
				double total = rows.size();
					
				//Values Needed for equation
				double t_plus = c_11 + c_01;
				double t_neg = c_10 + c_00;
				double c_neg = c_01 + c_00;
				double c_plus = c_10 + c_11;
			
				//Entropy Values 
				double ent_before = 0D;
				if(total!=0){
					ent_before =( t_plus==0? 0 : -((t_plus / total) * log2((t_plus / total))))  
								+ ( t_neg==0? 0 : -((t_neg / total) * log2((t_neg / total))));
				}
				
	
				double ent_right = 0D;
				if(c_plus!=0){
					ent_right =( c_11==0? 0 : -((c_11 / c_plus) * log2((c_11 / c_plus))))  
								+ ( c_10==0? 0 : -((c_10 / c_plus) * log2((c_10 / c_plus))));
				}
				
				
				double ent_left = 0D;
				if(c_neg!=0){
					ent_left =( c_00==0? 0 : -((c_00 / c_neg) * log2((c_00 / c_neg))))  
								+ ( c_01==0? 0 : -((c_01 / c_neg) * log2((c_01 / c_neg))));
				}
				
				
				double ent_after = 0D;
				if(total!=0){
					ent_after = (c_neg / total) * ent_left + (c_plus / total) * ent_right;
				}
				
				infoGain = ent_before - ent_after;
			
			
		return infoGain;
	}

	//Setting Vertical List 
	private static void setCustomDataStructure(HashMap<String, ArrayList<Double>> vericalData,
			ArrayList<Instance> dataList, ArrayList<String> attributeList) {
		
			int i = 0;
			for(String attribute : attributeList){
				
				ArrayList<Double> verticalData = new ArrayList<Double>();
				for(Instance inst: dataList )
				{
					verticalData.add(inst.getData().get(i));
				}
				++i;
				vericalData.put(attribute, verticalData);
			}
	}


	
	//Read File 
	private static void readDataFile(String fileName,ArrayList<Instance> dataList,ArrayList<String> attributeList,ArrayList<Integer> totalRows) {
		
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String sCurrentLine;

			
			//Reading First Row of Attributes 
			if((sCurrentLine = br.readLine()) != null)
			{
				String columns[] = sCurrentLine.split(",");
				for(String column : columns)
				{
					attributeList.add(column);
				}
			}
			
			int i = 0;
			//Read Data in Instance List 	
			while ((sCurrentLine = br.readLine()) != null) {
				Instance dataRow = new Instance();
				ArrayList<Double> doubleData = new ArrayList<Double>();
				for(String data : sCurrentLine.split(",")){
					doubleData.add(Double.parseDouble(data));
					dataRow.setData(doubleData );
				}
				totalRows.add(i);
				dataRow.setId(++i);
				dataList.add(dataRow);
			}

		} catch (IOException e) {
			System.out.println("Some Problem Occured in Reading File : "+fileName);
			e.printStackTrace();
		}
		
	}
	
	
	private static double log2(double x)
	{
	    return (double) (Math.log(x) / Math.log(2));
	}
	
	
}
