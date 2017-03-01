import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class NueralNet {

	static Integer attributesize =  new Integer(0);

	static double percentSplit = 0;
	static double errorRate = 0;
	
	static int noOfHiddenlayers = 0;
	static double learningRate = 1;
	
	static double trulyIdentified = 0;
	static double regressionError = 0;
	
	public static void main(String[] args) {
		
		try
		{
			HashMap<Integer,ArrayList<Double>> vericalDataRefined = new HashMap<Integer,ArrayList<Double>> ();
			HashMap<Integer,ArrayList<Node>> layers  = new HashMap<Integer,ArrayList<Node>>();  
			
			ArrayList<Instance> dataList = new ArrayList<Instance>();
			ArrayList<Instance> testDataList = new ArrayList<Instance>();
			
			ArrayList<Integer> totalRows = new ArrayList<Integer>();
			
			//Reading Data File and fir it to structure //With Filter on Null and Blank 
			readDataFile(args[0],dataList,totalRows);
			
			//Set Custom Data-Structures
			setCustomDataStructure(vericalDataRefined,dataList);
			
			//Getting Arguments 
			percentSplit = args[1]!=null && !"".equals(args[1]) ? Double.parseDouble(args[1]) : 80;
			errorRate = args[2]!=null && !"".equals(args[2]) ? Double.parseDouble(args[2]) : 0.01;
			noOfHiddenlayers = args[3]!=null && !"".equals(args[3]) ? Integer.parseInt(args[3]) : 1;
			
			
			//Create Input Layers - First 
			ArrayList<Node> hiddenLayers =  new ArrayList<Node>();
			for(int i = 0;i<vericalDataRefined.size()-1;i++){
				hiddenLayers.add(new Node(i));
			}
			layers.put(0, hiddenLayers);
			
			//Create Number of Hidden Layers
			if(noOfHiddenlayers!=0)
			{
				for(int i = 1,arg=4;i<=noOfHiddenlayers;i++){
					hiddenLayers = new ArrayList<Node>();
					for(int j = 0; j<Integer.parseInt(args[arg]);j++){
						Node tmpNode = new Node(j);
						tmpNode.bias = 1;
						hiddenLayers.add(tmpNode);
					}
					++arg;
					layers.put(i, hiddenLayers);
				}
			}
			else{
				throw new Exception("Undefined Hidden layers ");
			}
			
			
			//Create Output Layer - Check For Classification or Regression Problem  
			createOutputLayer(vericalDataRefined,layers);
			
			
			//Generating Random Weights                                                                                                                                           
			generateRandomWeight(layers);
			
			
			//Train Neural Network 
			trainNeuralNetwork(dataList,layers,testDataList);
			
			//Perform Pass on Test Data 
			regressionError = 0;
			trulyIdentified = 0;
			testNueralNetwork(dataList,layers,testDataList);
			
			//Find Accuracy 
			findAccuracy(layers,testDataList);
		}
		catch(Exception e )
		{
			System.out.println("Nueral net Usage Error : Please Enter Values in Following Order : "
					+ "\n NueralNet <> <> <> <> ");//TODO - Make format proper in Comments 
			e.printStackTrace();
		}
		
	}


	
	private static void findAccuracy(HashMap<Integer, ArrayList<Node>> layers,ArrayList<Instance> testDataList) {
		
		ArrayList<Node> outPutLayer = layers.get(layers.size()-1);
		
		//System.out.println("aa"+outPutLayer.size());
		
		if(outPutLayer.size()==1)
		{
			//Regression Problem 
			System.out.println("Accuracy : "+(1-(Math.sqrt(regressionError)/testDataList.size()))*100);
		}
		else
		{
			//Classification Problem 
			System.out.println("Accuracy : "+(trulyIdentified/(testDataList.size()))*100);
		}
	}



	private static void testNueralNetwork(ArrayList<Instance> dataList, HashMap<Integer, ArrayList<Node>> layers,
			ArrayList<Instance> testDataList) {
			
		
		ArrayList<Node> outPutLayer = layers.get(layers.size()-1);
		
		//Iterating All testing Instances - TODO Optimize with Condition  
		for(int i = 0;i<testDataList.size();i++){
			
			Instance testInstance =  testDataList.get(i);
			forwardPassForInstance(testInstance, layers);
			
			double target = testInstance.getData().get(testInstance.getData().size()-1);
			
			if(outPutLayer.size()==1){
				//Regression problem 
				regressionError += Math.pow((target - outPutLayer.get(0).value), 2);
			}
			else{
				//Classification Problem 
				
				double maxValue = Double.MIN_VALUE;
				int maxNodeIndex = 0;
				
				for(int j = 0 ; j<outPutLayer.size();j++){
					if(outPutLayer.get(j).value>maxValue){
						maxValue = outPutLayer.get(j).value;
						maxNodeIndex = j;
					}
				}
				if(target==maxNodeIndex){
					++trulyIdentified;
				}
				
				
			}
			
		}
	}



	private static void trainNeuralNetwork(ArrayList<Instance> dataList, HashMap<Integer, ArrayList<Node>> layers,
			ArrayList<Instance> testDataList) {
		
		//Shuffle List Randomly
		Collections.shuffle(dataList);
		
		
		//Split According to Split Factor 
		int splitSize = (int) ((dataList.size() * percentSplit) / 100) ;
		
		for(int j = 0 ; j<5;j++)
		{
			for(int i = 0 ; i<splitSize;i++)
			{
				Instance trainInstance = dataList.get(i);
				
				//Forward pass
				forwardPassForInstance(trainInstance,layers);
				
				//BackWard Pass
				backWardPassForInstance(trainInstance,layers);
				
			}
		}
		
		
		
		//Add Remaining List to Testing list - This Data will return for testing 
		for(int i = splitSize;i<dataList.size();i++){
			testDataList.add(dataList.get(i));
		}
		
		
	}



	private static void backWardPassForInstance(Instance trainInstance, HashMap<Integer, ArrayList<Node>> layers) {
		
		//start from last layer 
		for(int i =layers.size()-1;i>0;i--){
			
			ArrayList<Node> layer= layers.get(i);
			
			//Calculate Error on Each Layer and Each Node 
			
			if(i == layers.size()-1){
				//This is output layer
				
				//Do for Every Node in layer
				for(int j = 0;j<layer.size();j++){
					
					double target = trainInstance.getData().get(trainInstance.getData().size()-1);
					Node tmpNode = layer.get(j);
					
					tmpNode.error = tmpNode.value * (1 - tmpNode.value) * (target - tmpNode.value);
					
				}
			}
			else{
				//This is Hidden layer 
					
					//For Every Node in Hidden Layer 
					for(int j = 0;j<layer.size();j++)
					{
					
					Node tmpNode = layer.get(j);
					double errorSum = 0;
					
					for(int k = 0; k<tmpNode.edges.size();k++){
						errorSum += tmpNode.edges.get(k) * layers.get(i+1).get(k).error;
					}
					
					tmpNode.error = tmpNode.value * (1 - tmpNode.value) * (errorSum);
					
				}
			}
		}
		
		
		//Update Weights 
		for(int i = 0;i<layers.size()-1;i++){
			ArrayList<Node> layer = layers.get(i);
			
			//For First Data layer 
			for(int j=0;j<layer.size();j++)
			{
				Node layrNode = layer.get(j);

				for(int k = 0;k<layrNode.edges.size();k++){
					
					//Updating Edge Value 
					layrNode.edges.set(k,layrNode.edges.get(k)+(layrNode.value * layers.get(i+1).get(k).error * learningRate)) ;
					
				}
				layrNode.bias += learningRate * layrNode.error;
			}
			
			
		}
		
		
	}



	private static void forwardPassForInstance(Instance trainInstance, HashMap<Integer, ArrayList<Node>> layers) {
		
		ArrayList<Double> instanceData =  trainInstance.getData();
		
		//Iterate Over Layers - Starting from layer 1 
		for(int i = 0;i<layers.size();i++){
			ArrayList<Node> layer = layers.get(i);
			
			//For First Data layer 
			if(i==0){
				for(int j=0;j<layer.size();j++)
				{
					Node layrNode = layer.get(j);
					layrNode.value = instanceData.get(j);
				}
			}
			else//If It is not data Node then Calculate Value for Current Layer From Previous Layer 
			{
				ArrayList<Node> prevLayer = layers.get(i-1);
				
				for(int j = 0;j<layer.size();j++){
					Node layrNode = layer.get(j);
					
					//----------Calculate Value - Start
					double nodeValue = 0;
					for(int k = 0;k<prevLayer.size();k++){
						nodeValue += prevLayer.get(k).value * prevLayer.get(k).edges.get(j);
					}
					layrNode.value = sigmoidFunction(layrNode.bias+nodeValue);
					
					//----------Calculate Value - End
					
				}
			}
			
		}
	}



	private static double sigmoidFunction(double nodeValue) {
		
		return 1/(1+Math.pow(Math.E, -nodeValue));  //TODO - Check Correction of This Function
	}



	private static void generateRandomWeight(HashMap<Integer, ArrayList<Node>> layers) {
		
		for(int i = 0;i<layers.size()-1;i++){
			int edges = layers.get(i+1).size();
			
			for(Node tmpNode : layers.get(i)){
				for(int j = 0;j<edges;j++){
					//Adding Random to Edge 
					tmpNode.edges.add(Math.random()*0.5);
				}
			}
			
		}
	}



	private static void createOutputLayer(HashMap<Integer, ArrayList<Double>> vericalDataRefined,
			HashMap<Integer, ArrayList<Node>> layers) {

		 ArrayList<Double> classList = vericalDataRefined.get(vericalDataRefined.size()-1);
		 HashSet<Double> hs = new HashSet<Double>();
		 for(Double value : classList){
				 hs.add(value);
		 }
		 
		 ArrayList<Node> newList = new ArrayList<Node>();
		 if(classList.size()/hs.size()<25) 
		 {
			 //Regression  - only one output node
			 Node tmpNode = new Node(0);
			 tmpNode.bias = 1;
			 newList.add(tmpNode);
		 }
		 else
		 {
			 //Classification - Number of Nodes in Distinct Values 
			 int i = 0;
			 for(Double value : hs){
				 Node tmpNode = new Node(i++);
				 tmpNode.label = value;
				 tmpNode.bias = 1;
				 newList.add(tmpNode);
			 }
		 }
		 layers.put(layers.size(), newList); 
		
	}



	/**
	 * Read Data File and Convert into DataSets
	 * 
	 * @param fileName
	 * @param dataList
	 * @param attributeList
	 * @param totalRows
	 */
		private static void readDataFile(String fileName,ArrayList<Instance> dataList,ArrayList<Integer> totalRows) {
			
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

				String sCurrentLine;

				int i = 0;
				//Read Data in Instance List 	
				while ((sCurrentLine = br.readLine()) != null) {
					Instance dataRow = new Instance();
					ArrayList<Double> doubleData = new ArrayList<Double>();
					
					String columns[] = sCurrentLine.split(",");
					if(i==0)
					attributesize = columns.length;
					
					for(String data : columns){
						if(data!=null && !"".equals(data))
						doubleData.add(Double.parseDouble(data.trim()));
					}
					
					if(doubleData.size()==attributesize){
						dataRow.setData(doubleData );
						totalRows.add(i);
						dataRow.setId(++i);
						dataList.add(dataRow);
					}
					
				}

			} catch (IOException e) {
				System.out.println("Some Problem Occured in Reading File : "+fileName);
				e.printStackTrace();
			}
			
			
		}
		
		
		/**
		 * Setting vertical Data List for Computation 
		 * 
		 * @param vericalData
		 * @param dataList
		 * @param attributeList
		 */
			private static void setCustomDataStructure(HashMap<Integer, ArrayList<Double>> vericalData,
					ArrayList<Instance> dataList) {
				
					for(int i = 0;i<attributesize;i++){
						ArrayList<Double> verticalData = new ArrayList<Double>();
						for(Instance inst: dataList )
						{
							verticalData.add(inst.getData().get(i));
						}
						vericalData.put(i, verticalData);
					}
						
			}

}



class Node{
	
	int id;
	double value;
	ArrayList<Double> edges = new ArrayList<Double>();
	double bias=0;
	
	//For output layer 
	double targetValue = 0;
	double label = 0;
	
	//For Back Propagation 
	double error;
	
	//Test Value
	double testValue;
	
	Node(int id){
		this.id = id;
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
