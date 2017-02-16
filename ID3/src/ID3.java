import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ID3 {

	private static int nodeNumber = 0;
	private static int leafNodes = 0;
	private static int trulyIdentified = 0;
	private static int maxLevelNumber = 0;
	
	
	private static double trulyIdentifiedValidation = 0;
	private static double trulyIdentifiedTest = 0;
	private static int threshold = 0;
	
	public static void main(String[] args) {
		
		
		try{
			
			HashMap<String,ArrayList<Double>> vericalData = new HashMap<String,ArrayList<Double>> ();
			HashMap<String,ArrayList<Double>> vericalDataVal = new HashMap<String,ArrayList<Double>> ();
			HashMap<String,ArrayList<Double>> vericalDataTest = new HashMap<String,ArrayList<Double>> ();
			
			
			ArrayList<Instance> dataList = new ArrayList<Instance>();
			ArrayList<String> attributeList =  new ArrayList<String>();
			ArrayList<Integer> totalRows = new ArrayList<Integer>();
			
			
			try{
				threshold = args[4]!=null&&!"".equals(args[4])&&Integer.parseInt(args[4])>60?Integer.parseInt(args[4]):85;
			}
			catch(Exception ae){
				threshold = 85;
			}
			
			
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
			
			/*	1. Create New Node 
			2. Assign totalRows and attributes
			3. Calculate Information Gain and Get Column 
			4. Save That InfoGain and Column to Node
			5. Calculate New totalRows, attributes and positiveClasses , negativeClasses
			6. Split Node and Generate Left and Right Node
			
			//Stop Condition 
			1. until positiveClasses==0 or negativeClasses==0 in Node 
			2. All have zero Info Gain (optional)*/	
			nodeNumber = 0;
			Node root = generateTree(null,totalRows,attributeList,vericalData,1,"",0);
			
			
			//Printing in Given Format 
			Node traversalRoot = root;
			System.out.println("Pre Pruned Decision Tree\n ");
			traverseTree(traversalRoot);
			
			/*
			 *  
			--------------Before Pruning----------------
			1. Print Total Number of Training Instances 
			2. Print Total Number of Training Attribute
			3. Total Number of Nodes in Tree
			4. Number of LeafNodes in Tree
			5. Accuracy on Training Data set 
			6. Number of validation Instances
			7. Number of Validation Attributes
			8. Accuracy on Validation Data set Before Pruning
			9. Number of Testing Instances
			10. Number of Testing Attributes
			11. Accuracy on Testing Data set Before Pruning
			 
			 * 
			 */
			System.out.println("\n\n");
			System.out.println("Pre-Pruned Accuracy");
			System.out.println("------------------------------------\n");
			System.out.println("Number of training instances = "+totalRows.size());
			System.out.println("Number of training attributes = "+attributeList.size());
			System.out.println("Total number of nodes in the tree = "+nodeNumber);
			System.out.println("Number of leaf nodes in the tree ="+leafNodes);
			System.out.println("Accuracy of the model on the training dataset = "+(trulyIdentified*100)/totalRows.size());
			
			
			
			
			/*	
			 * --------------Before Pruning----------------
				1. Print Total Number of Training Instances 
				2. Print Total Number of Training Attribute
				3. Total Number of Nodes in Tree
				4. Number of LeafNodes in Tree
				5. Accuracy on Training Data set 
				6. Number of validation Instances
				7. Number of Validation Attributes
				8. Accuracy on Validation Data set Before Pruning
				9. Number of Testing Instances
				10. Number of Testing Attributes
				11. Accuracy on Testing Data set Before Pruning
				
			*/
			
			ArrayList<Instance> dataListVal = new ArrayList<Instance>();
			ArrayList<String> attributeListVal =  new ArrayList<String>();
			ArrayList<Integer> totalRowsVal = new ArrayList<Integer>();
			
			//Reading Validation Files 
			readDataFile(args[1],dataListVal,attributeListVal,totalRowsVal);
			//Set Custom Data-Structures
			setCustomDataStructure(vericalDataVal,dataListVal,attributeListVal);
			//Removing Class Column from Attribute table 
			attributeListVal.remove(attributeListVal.size()-1);
					
					
			System.out.println("\n");
			System.out.println("Number of validation instances = "+totalRowsVal.size());
			System.out.println("Number of validation attributes = "+attributeListVal.size());
			
			Node validationRoot = root;
			getTrueCountOnData(validationRoot,dataListVal,vericalDataVal.get("Class"),vericalDataVal,"validation");
			System.out.println("Accuracy of the model on the validation dataset before pruning = "+(trulyIdentifiedValidation*100)/totalRowsVal.size());
			
			
			
			ArrayList<Instance> dataListTest = new ArrayList<Instance>();
			ArrayList<String> attributeListTest =  new ArrayList<String>();
			ArrayList<Integer> totalRowsTest = new ArrayList<Integer>();
			
			//Reading Validation Files 
			readDataFile(args[2],dataListTest,attributeListTest,totalRowsTest);
			//Set Custom Data-Structures
			setCustomDataStructure(vericalDataTest,dataListTest,attributeListTest);
			//Removing Class Column from Attribute table 
			attributeListTest.remove(attributeListTest.size()-1);
			
			
			
			System.out.println("\n");
			System.out.println("Number of testing instances = "+totalRowsTest.size());
			System.out.println("Number of testing attributes = "+attributeListTest.size());
			
			Node testingRoot = root;
			getTrueCountOnData(testingRoot,dataListTest,vericalDataTest.get("Class"),vericalDataTest,"testing");
			System.out.println("Accuracy of the model on the testing dataset = "+(trulyIdentifiedTest*100)/totalRowsVal.size());
			
			
			// Prune Tree
			nodeNumber = 0;
			leafNodes = 0;
			trulyIdentified=0;
			maxLevelNumber = 0;
			Node prunedRoot = generateTree(null,totalRows,attributeList,vericalData,1,"",0);
			
			traversalRoot = prunedRoot;
			pruneTree(Double.parseDouble(args[3]),traversalRoot);
			
			
			
			//Print Pruned Tree
			//Printing in Given Format 
			traversalRoot = prunedRoot;
			System.out.println("\n\n");
			System.out.println("Post Pruned Decision Tree\n ");
			traverseTree(traversalRoot);
			
			
			System.out.println("\n\n");
			System.out.println("Post-Pruned Accuracy");
			System.out.println("------------------------------------\n");
			System.out.println("Number of training instances = "+totalRows.size());
			System.out.println("Number of training attributes = "+attributeList.size());
			
			Node countRoot = prunedRoot;
			nodeNumber = 0;
			leafNodes = 0;
			trulyIdentified = 0;
			traverseTreeCount(countRoot);
			
			System.out.println("Total number of nodes in the tree = "+nodeNumber);
			System.out.println("Number of leaf nodes in the tree ="+leafNodes);
			System.out.println("Accuracy of the model on the training dataset = "+(trulyIdentified*100)/totalRows.size());
			
			
			
			System.out.println("\n");
			System.out.println("Number of validation instances = "+totalRowsVal.size());
			System.out.println("Number of validation attributes = "+attributeListVal.size());
			testingRoot = prunedRoot;
			trulyIdentifiedValidation = 0;
			getTrueCountOnData(testingRoot,dataListVal,vericalDataVal.get("Class"),vericalDataVal,"validation");
			System.out.println("Accuracy of the model on the validation dataset After pruning = "+(trulyIdentifiedValidation*100)/totalRowsVal.size());
			
			System.out.println("\n");
			System.out.println("Number of testing instances = "+totalRowsTest.size());
			System.out.println("Number of testing attributes = ");
			testingRoot = prunedRoot;
			trulyIdentifiedTest = 0;
			getTrueCountOnData(testingRoot,dataListTest,vericalDataTest.get("Class"),vericalDataTest,"testing");
			System.out.println("Accuracy of the model on the testing dataset = "+(trulyIdentifiedTest*100)/totalRowsVal.size());
			
		}
		catch(Exception e )
		{
			//e.printStackTrace();
			System.out.println("ID3 : Usage Error \n"
					+ "ID3 : Please Refer ReadME File \n"
					+ "Syntax : ID3 <Trainng File> <Validation File> <Testing File> <Pruning Factor> <Node Purity | optional>");
		}
		
		
		
		
	}


	private static void getTrueCountOnData(Node validationRoot,ArrayList<Instance> dataListVal,ArrayList<Double> classList,
			HashMap<String,ArrayList<Double>> verticalData,String type) {
		
		ArrayList<Double> data = null;
		Node curr = validationRoot;
		int j = 0;
		for( j = 0 ;j<dataListVal.size();j++){
			
			data = dataListVal.get(j).getData();
			int i = 0;
			curr = validationRoot;
			while(curr!=null && i<data.size()-1){
				if(verticalData.get(curr.column).get(j)==0 && curr.leftNode!=null){
					curr = curr.leftNode;
				}
				else if(verticalData.get(curr.column).get(j)==1 && curr.rightNode!=null)
				{
					curr = curr.rightNode;
				}
				else{
					if(curr.expectedLabel==classList.get(j)){
						if(type.equals("validation"))
							++trulyIdentifiedValidation;
						else if(type.equals("testing"))
							++trulyIdentifiedTest;
						
						break;
					}
				}
				++i;
			}
		}
		//System.out.println(j);
	}


	private static void pruneTree(double pruneFactor,Node traversalRoot) {
		
		//Adding all node Numbers to List 
		ArrayList<Integer> nodeLabels = new ArrayList<>();
		for(int i = 1;i<=nodeNumber;i++){
			nodeLabels.add(i);
		}
		
		///Finding total nodes to prune
		int totalPruningNodes = (int) ((pruneFactor*nodeNumber));
		//Valid Pruning Level
		int pruningLevel = maxLevelNumber/2;
		
		System.out.print("\n Pruned Nodes = ");
		//Loop For Pruning 
		for(int i = 0,j=0;i<totalPruningNodes;){
			
			if(j>nodeNumber){
				break;
			}
			
			Collections.shuffle(nodeLabels);
			int pruneNumber = nodeLabels.get(0);
			nodeLabels.remove(0);
			
			Node prunedNode = searchNode(traversalRoot,pruneNumber);
			
			if(prunedNode!=null && prunedNode.levelNumber>pruningLevel){
				System.out.print(" "+prunedNode.id);
				prunedNode.leftNode = null;
				prunedNode.rightNode = null;
				++i;
			}
			
		}
		
	}
	//Test This Function
		private static Node searchNode(Node traversalRoot, int pruneNumber) {
			
			if(traversalRoot==null)return null;
			if(traversalRoot.id==pruneNumber)return traversalRoot;
			
			Node traRoot = null;
			
			traRoot = searchNode(traversalRoot.leftNode, pruneNumber);
			if(traRoot==null)
				traRoot = searchNode(traversalRoot.rightNode, pruneNumber);
			
			return traRoot;
		}

	private static void traverseTreeCount(Node traversalRoot) {
		
		if(traversalRoot==null)return;
		++nodeNumber;
		
		if(traversalRoot.leftNode!=null && traversalRoot.rightNode!=null)
		{
			traverseTreeCount(traversalRoot.leftNode);
			traverseTreeCount(traversalRoot.rightNode);
		}
		else{
			leafNodes++;
			if(traversalRoot.expectedLabel==0)
			{
				trulyIdentified+=traversalRoot.negativeClasses;
			}
			else
			{
				trulyIdentified+=traversalRoot.positiveClasses;
			}
		}
		
		return;
	}


	private static void traverseTree(Node root) {
		
		//Recursion End Condition 
		if(root!=null){
			
			int spaceAllocation = (root.levelNumber*1);
			String a  = "";
			StringBuilder sb = new StringBuilder();
			
			if(root.levelNumber>1)//Skipping First Root Node for printing
			{
				for(int i=1;i<spaceAllocation;i++){
					sb.append("  ");
					sb.append("|");
				}
				
				if(sb.length()>0) //Delete Last occurrence of "|"
				sb.deleteCharAt(sb.lastIndexOf("|"));
				
				//Append Expected Value only if it is Leaf Node 
				if(root.leftNode==null && root.rightNode==null)
					System.out.println(sb.append(root.toString()).append(" : ").append(root.expectedLabel).toString());
				else
					System.out.println(sb.append(root.toString()).toString());
			}
			
			//DFS on Tree 
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
			if(levelNumber>maxLevelNumber){
				maxLevelNumber = levelNumber;
			}
			
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
			if((root.negativeClasses*100)/(root.positiveClasses+root.negativeClasses)<=threshold 
					&& (root.positiveClasses*100)/(root.positiveClasses+root.negativeClasses)<=threshold 
					&& root.positiveClasses!=0 && root.negativeClasses!=0 &&  root.attributes.size()>0)
			{
				//Removing attribute 
				//System.out.println("dv : "+root.column);
				//System.out.println("dv : "+root.attributes.toString());
				root.attributes.remove(root.attributes.indexOf(root.column));
				
				root.leftNode = generateTree(root.leftNode, calculateTotalRows(root.column,totalRows,vericalData,0), root.attributes, vericalData,levelNumber+1,root.column,0);
				root.rightNode = generateTree(root.rightNode, calculateTotalRows(root.column,totalRows,vericalData,1), root.attributes, vericalData,levelNumber+1,root.column,1);
			}
			else
			{
				++leafNodes;
				if(root.expectedLabel==0)
				{
					trulyIdentified+=root.negativeClasses;
				}
				else
				{
					trulyIdentified+=root.positiveClasses;
				}
				
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


class Node {

	int id;
	int levelNumber;
	double infoGain;
	String column;
	double positiveClasses;
	double negativeClasses;
	ArrayList<Integer> totalRows;
	ArrayList<String> attributes;
	int expectedLabel=-1;
	
	
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
		return removedAttribute+" = "+columnValue;
	}
	
	
	
	
}
class Instance {

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
