import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 
 * This is Index Utility That will Create Index on Given Text File Data 
 * User can create index. find record from text file through index, 
 * insert a new record in file and show all sequential records in index
 * 
 * To Create Index Use Command
 *  INDEX -create <input file> <output file> <key size>
 *  
 *  
 *  To Find Record from File Use Command 
 *   INDEX -find <index filename> <key>
 *   
 *   
 *   To Insert a new Record Use Command 
 *   INDEX -insert <index filename> "new text line to be inserted." 
 *   
 *   
 *   To list Sequential Records Use Command 
 *     INDEX -list <index filename> <starting key> <count>
 *     
 *     
 * @version 1.0
 * @since 1.0
 * @author Varun Dani
 *
 */
public class INDEX {

	
	/**
	 * main method will be called as this class  is used as command line utility 
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		BPlusTree<String,Long> bPlusTree;
		RandomAccessFile inputDataFile;
		BPlusValueClass bPlusValues = new BPlusValueClass();
		
		//Parsing Command line [if there is problem through Error of Usage 
		if(args.length>0)
		{
			try
			{
				if(args[0].equals("-create"))
				{
					inputDataFile = new RandomAccessFile(args[1], "r");
					
					//Deleting Old Index File if Already Created and Create if not Exist
					File ff = new File(args[2]);
					if(ff.exists())
					{
						ff.delete();
					}
					
					//Initialize Header Information
					FileAcessUtility.initialize(args[2]);
					FileAcessUtility.initializeHeaders(args[1]);
					bPlusTree = new BPlusTree<String,Long>(args[2]);
					
					//Setting It to zero position for reading
					inputDataFile.seek(0);
					String tempLine = null;
					Long filePointerSeek = 0L;
					
					while((tempLine = inputDataFile.readLine()) !=null)
					{
						//Insert into Binary Tree
						bPlusTree.insertIntoBPlusTree(tempLine.substring(0, Integer.parseInt(args[3])).trim(),
								filePointerSeek);
						bPlusValues.put(tempLine.substring(0, Integer.parseInt(args[3])).trim(), filePointerSeek);
						//System.out.println(tempLine.substring(0, Integer.parseInt(args[3])).trim()+" : "+filePointerSeek);
						filePointerSeek = inputDataFile.getFilePointer();
						
					}
					FileAcessUtility.writeHeaderBlock(FileAcessUtility.getEndOfFileSeek(), 512L);
					
					int length = FileAcessUtility.writeObjectBlock(bPlusValues,FileAcessUtility.getEndOfFileSeek());
					FileAcessUtility.writeHeaderBlockInt(length, 520L);
					
					System.out.println("Index "+args[2]+" Created Successfully");
				}
				else if (args[0].equals("-insert"))
				{
					RandomAccessFile newFile = new RandomAccessFile(args[1], "rw");
					FileAcessUtility.initialize(args[1]);
					newFile.seek(0);
					String fileName = newFile.readUTF();
					//System.out.println(fileName);
					RandomAccessFile mainFile = new RandomAccessFile(fileName, "rw");
					
					Long length = mainFile.length();
					mainFile.seek(length);
					//mainFile.writeUTF(args[2]);
					Files.write(Paths.get(fileName), (System.lineSeparator()+args[2]).getBytes(), StandardOpenOption.APPEND);
					 
					 bPlusTree = new BPlusTree<String,Long>(args[1]);
					 
					 
					 String tempLine;
					 Long filePointerSeek = 0L;
						while((tempLine = mainFile.readLine()) !=null)
						{
							if(tempLine.equals(args[2]))
							{
								break;
							}
							filePointerSeek = mainFile.getFilePointer();
							
						}
						
						//System.out.println(filePointerSeek);
					 bPlusValues.insertValueinBPlusTree(args[2].substring(0, Integer.parseInt("15")).trim(),filePointerSeek);
					
				}
				else if (args[0].equals("-find"))
				{
					FileAcessUtility.initialize(args[1]);
					bPlusTree = new BPlusTree<String,Long>(args[1]);
					Long value = bPlusTree.searchFromBPlusTree(args[2]);
					Long recordLocation = null;
					
					RandomAccessFile newFile = new RandomAccessFile(args[1], "r");
					FileAcessUtility.initialize(args[1]);
					newFile.seek(0);
					String fileName = newFile.readUTF();
					
					RandomAccessFile mainFile = new RandomAccessFile(fileName, "r");
					
				
					if(((BPlusValueClass)FileAcessUtility.getObjectOnLocationofSize(FileAcessUtility.getHeaderBlock(),FileAcessUtility.getHeaderBlockInt())).get(args[2])!=null)
						recordLocation = ((BPlusValueClass)FileAcessUtility.getObjectOnLocationofSize(FileAcessUtility.getHeaderBlock(),FileAcessUtility.getHeaderBlockInt())).get(args[2]);
					if(recordLocation!=null )
					{
						mainFile.seek(0);
						mainFile.seek(recordLocation);
						System.out.println(mainFile.readLine());
					}
					else
					{
						System.out.println("Not Found ");
					}
					
					
					//System.out.println(value);
					
				}
				else if (args[0].equals("-list"))
				{
					
					RandomAccessFile newFile = new RandomAccessFile(args[1], "r");
					FileAcessUtility.initialize(args[1]);
					newFile.seek(0);
					String fileName = newFile.readUTF();
					//System.out.println(fileName);
					RandomAccessFile mainFile = new RandomAccessFile(fileName, "r");
					
					newFile.seek(256);
					Long ll = newFile.readLong();
					
					newFile.seek(1027L);
					
					BPlusTreeDataNode aa = null;
					Long[] valueArray;
					int counter = Integer.parseInt(args[3]);
					do
					{
						if(aa==null){newFile.seek(1027L);}
						else{newFile.seek(aa.rightSibling);}
						byte[] bb = new byte[1024] ;
						newFile.readFully(bb);
						//ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  bb ) );
						//Object obj  = ois.readObject();
						// aa = (BPlusTreeDataNode)obj;
						//System.out.println(obj.toString());
						 counter--;
					}
					while(aa!=null && aa.rightSibling!=null && counter>0);
					valueArray = bPlusValues.getListOfValues(args[2], Integer.parseInt(args[3]));
					
					for(Long location : valueArray)
					{
						mainFile.seek(0);
						mainFile.seek(location);
						System.out.println(mainFile.readLine());
					}
					mainFile.close();
				}
				else
				{
					//If No command Found for that 
					commandUsageError();
					return;
				}
				
			}
			catch(Exception e )
			{
				e.printStackTrace();
				commandUsageError();
				return;
			}
			
		}
		else
		{
			// If No parameter Found 
			commandUsageError();
			return;
		}
	
	}
	
	
	public static void commandUsageError()
	{
		System.err.println("Please Use proper Command for INDEX Command \n"
				+ "Usage Instructions : \n\n"
				+ " To Create Index Use Command\n"
				+ " INDEX -create <input file> <output file> <key size>\n\n"
				+ "To Find Record from File Use Command \n"
				+ " INDEX -find <index filename> <key>\n\n"
				+ " To Insert a new Record Use Command \n"
				+ "   INDEX -insert <index filename> \"new text line to be inserted.\" \n\n"
				+ " To list Sequential Records Use Command \n"
				+ "INDEX -list <index filename> <starting key> <count>\n\n");
	}

}
