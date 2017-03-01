import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 *
 */
public class PreProcessor {

	static Integer attributesize =  new Integer(0);
	
	public static void main(String[] args) {

		HashMap<Integer,ArrayList<String>> vericalData = new HashMap<Integer,ArrayList<String>> ();
		HashMap<Integer,ArrayList<Double>> vericalDataRefined = new HashMap<Integer,ArrayList<Double>> ();
		
		ArrayList<PreInstance> dataList = new ArrayList<PreInstance>();
		ArrayList<Integer> totalRows = new ArrayList<Integer>();
		String splitter = "\\s+";
		
		try
		{
			try{splitter = args[2]!=null?args[2]:"\\s+";}catch(Exception e){splitter = "\\s+";}
			//Reading Data File and fir it to structure //With Filter on Null and Blank 
			readDataFile(args[0],dataList,totalRows,splitter);
			
			//Set Custom Data-Structures
			setCustomDataStructure(vericalData,dataList);
			
			//Convert String Data to Integer 
			convertToInt(vericalData,vericalDataRefined);
			
			//Write Data Structure to File 
			writeToFile(args[1],vericalDataRefined,totalRows);
			
			
			System.out.println("Post Processed File Generated Successfully : "+args[1]);
		}
		catch(Exception e )
		{
			System.out.println("PreProcessing : Usage Error \n"
					+ "Please Use Pre Processing as Command : PreProcessor <> <> "); //TODO - Finalize Format 
		}
	}

	
	
	private static void writeToFile(String filePath,HashMap<Integer,ArrayList<Double>> vericalDataRefined,ArrayList<Integer> totalRows) throws Exception{

		BufferedWriter bw = null;
		//FileWriter fw = null;
		StringBuilder sb = null;
		PrintWriter writer = null;
		
		try {

			writer = new PrintWriter(filePath, "UTF-8");
			
			//fw = new FileWriter(fileName+"newFile.csv");
			//bw = new BufferedWriter(fw);
			
			for(int i = 0;i<totalRows.size();i++){
				sb = new StringBuilder();
				String prefix = "";
				for(Map.Entry<Integer, ArrayList<Double>> column : vericalDataRefined.entrySet()){
					sb.append(prefix);
					prefix = ",";
					sb.append(String.valueOf(column.getValue().get(i)));
				}
				//System.out.println(sb.toString());
				writer.println(sb.toString());
			}

		} catch (IOException e) {
			System.out.println("There some problem in Creating Output File : Please Check Output File Path ");
			throw e;

		}finally{
			writer.close();
		}
		
	}



	private static void convertToInt(HashMap<Integer, ArrayList<String>> vericalData,HashMap<Integer, ArrayList<Double>> vericalDataRefined) {
		
		for(Map.Entry<Integer, ArrayList<String>> column : vericalData.entrySet()){
			
			ArrayList<String> columnValues = column.getValue();
			ArrayList<Double> numericColumn = new ArrayList<Double>();
			
			//Exception Checking for Parsing 
			try
			{
				Double.parseDouble(columnValues.get(0));
				
				//Convert to Double Array 
				normalizeToDouble(columnValues,numericColumn);
			
			}
			catch(Exception e )
			{
				//It is Not Parsed Value - Convert It 
				if(column.getKey()==vericalData.size()-1){
					convertToDoubleValues(columnValues,numericColumn,false);
				}
				else{
					convertToDoubleValues(columnValues,numericColumn,true);
				}
				
			}
			
			//Reconstructing Data Structure 
			vericalDataRefined.put(column.getKey(), numericColumn);
		}
		
	}



	private static void convertToDoubleValues(ArrayList<String> columnValues,ArrayList<Double> numericColumn,boolean scaleYN) {
		
		//TODO - make a track for Assignment of Each String variable to Numeric Value 
		HashMap<String,Double> stringMap = new HashMap<String,Double>();
		Double index = 0D;
		
		for(String value : columnValues){
			
			if(stringMap.containsKey(value)){
				numericColumn.add(stringMap.get(value));
			}
			else{
				
				//We Can put Any Value here for Particular Scenario
				//Double index = new Double(columnValues.indexOf(value));
				
				stringMap.put(value, index++);
				numericColumn.add(stringMap.get(value));
			}
			
		}
		
		//Scale Data from 0 - 1
		Double mapSize = new Double(stringMap.size());
		for(int i = 0;i<numericColumn.size();i++){
			if(scaleYN)
			numericColumn.set(i, (numericColumn.get(i)/mapSize));
		}
		
	}



	private static void normalizeToDouble(ArrayList<String> columnValues, ArrayList<Double> numericColumn) {
		
		for(String value : columnValues){
			numericColumn.add(Double.parseDouble(value));
		}
		
		//Calculating Mean for value 
		Double totalSum = 0D;
		for(Double value : numericColumn){
			totalSum +=value;
		}
		Double mean = totalSum/numericColumn.size();
		//System.out.println("Mean Value : "+mean);
		
		//Calculating Standard Deviation 
		totalSum = 0D;
		for(Double value : numericColumn){
			totalSum += Math.pow((value-mean), 2);
		}
		Double staDev = Math.sqrt(totalSum/numericColumn.size());
		//System.out.println("SD Value : "+staDev);
		
		
		//Resetting values
		for(int i = 0;i<numericColumn.size();i++){
			numericColumn.set(i, (numericColumn.get(i)-mean)/staDev);
		}
	}



	/**
	 * Setting vertical Data List for Computation 
	 * 
	 * @param vericalData
	 * @param dataList
	 * @param attributeList
	 */
		private static void setCustomDataStructure(HashMap<Integer, ArrayList<String>> vericalData,
				ArrayList<PreInstance> dataList) {
			
				for(int i = 0;i<attributesize;i++){
					ArrayList<String> verticalData = new ArrayList<String>();
					for(PreInstance inst: dataList )
					{
						verticalData.add(inst.getData().get(i));
					}
					vericalData.put(i, verticalData);
				}
					
		}
		
	/**
	 * Read Data File and Convert into DataSets
	 * 
	 * @param fileName
	 * @param dataList
	 * @param attributeList
	 * @param totalRows
	 */
		private static void readDataFile(String fileName,ArrayList<PreInstance> dataList,ArrayList<Integer> totalRows,String splitter) {
			
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

				String sCurrentLine;

				int i = 0;
				//Read Data in PreInstance List 	
				while ((sCurrentLine = br.readLine()) != null) {
					PreInstance dataRow = new PreInstance();
					ArrayList<String> doubleData = new ArrayList<String>();
					
					//Make it Configurable via command Prompt 
					String columns[] = sCurrentLine.trim().split(splitter);
					if(i==0)
					attributesize = columns.length;
					
					for(String data : columns){
						if(data!=null && !"".equals(data))
						doubleData.add(data.trim());
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

}



class PreInstance {

	private ArrayList<String> data = new ArrayList<String>();
	private int id;
	
	public ArrayList<String> getData() {
		return data;
	}

	public void setData(ArrayList<String> data) {
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
		return "PreInstance [data=" + data + ", id=" + id + "]";
	}
	
	
}