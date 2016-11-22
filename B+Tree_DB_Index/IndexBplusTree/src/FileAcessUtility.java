import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;


/**
 * This is Utility file for File Random Acceess Mecahanism in Java
 * 
 * 
 * @version 1.0
 * @author Varun Dani
 *
 */
public class FileAcessUtility {

	public static RandomAccessFile indexFile;
	
	
	public static void initialize(String fileName)
	{
		try
		{
			indexFile = new RandomAccessFile(fileName, "rw");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void initializeHeaders(String fileName)
	{
		try
		{
			indexFile.seek(0);
			indexFile.writeUTF(fileName);
			
			indexFile.seek(256);
			indexFile.writeLong(1024L);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Object getObjectOnLocation(Long seekLocation)
	{
		Object obj  = null;
		try
		{
			indexFile.seek(seekLocation);
			
			byte[] bb = new byte[1024] ;
			indexFile.readFully(bb);
		         
			ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  bb ) );
			obj = ois.readObject();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return obj;
	}
	
	public static Object getObjectOnLocationofSize(Long seekLocation,int length)
	{
		Object obj  = null;
		try
		{
			indexFile.seek(seekLocation);
			
			byte[] bb = new byte[length] ;
			indexFile.readFully(bb);
		         
			ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(  bb ) );
			obj = ois.readObject();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return obj;
	}
	
	public static Long getEndOfFileSeek()
	{
		try {
			return indexFile.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Long writeObjectAtEOF(Object obj)
	{
		Long endOfFileSeek = null;
		try
		{
			endOfFileSeek = getEndOfFileSeek();
			
			indexFile.seek(endOfFileSeek);
			
			 ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
	         ObjectOutputStream oos1 = new ObjectOutputStream( baos1 );
	         oos1.writeObject( obj );
	         oos1.close();
	         indexFile.write(baos1.toByteArray());
	         
	         indexFile.seek(endOfFileSeek+1024);
	         indexFile.writeUTF(".");
	         
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return endOfFileSeek;
	}
	public static int writeObjectBlock(Object obj,Long seekLocation)
	{
		ByteArrayOutputStream baos1 = null;
		try
		{
			
			indexFile.seek(seekLocation);
			
			 baos1 = new ByteArrayOutputStream();
	         ObjectOutputStream oos1 = new ObjectOutputStream( baos1 );
	         oos1.writeObject( obj );
	         oos1.close();
	         indexFile.write(baos1.toByteArray());
	         
	         indexFile.seek(seekLocation+1024);
	         indexFile.writeUTF(".");
	        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 return baos1.toByteArray().length;
	}
	
	public static Long writeObjectAtPosition(Object obj,Long seek)
	{
		try
		{
			indexFile.seek(seek);
			
			 ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
	         ObjectOutputStream oos1 = new ObjectOutputStream( baos1 );
	         oos1.writeObject( obj );
	         oos1.close();
	         indexFile.write(baos1.toByteArray());
	         
	         indexFile.seek(seek+1024);
	         indexFile.writeUTF(".");
	         
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return seek;
	}
	
	public static Long writeHeaderBlock(Long obj,Long seek)
	{
		try
		{
			indexFile.seek(seek);
	        indexFile.writeLong(obj);
	         
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return seek;
	}
	
	public static Long writeHeaderBlockInt(int obj,Long seek)
	{
		try
		{
			indexFile.seek(seek);
	        indexFile.writeInt(obj);
	         
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return seek;
	}
	
	public static Long getHeaderBlock()
	{
		try
		{
			indexFile.seek(0);
			indexFile.seek(512L);
	        return indexFile.readLong();
	         
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return null;
	}
	
	public static int getHeaderBlockInt()
	{
		try
		{
			indexFile.seek(0);
			indexFile.seek(520L);
	        return indexFile.readInt();
	         
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return 0;
	}
	
	public static Long getHeaderBlockAddress()
	{
		try {
			indexFile.seek(256);
			return indexFile.readLong();
		} catch (IOException e) {
			return 1024L;
		}
		
	}
	
	public static void setHeaderBlockAddress(Long addressValue)
	{
		try {
			indexFile.seek(256);
			indexFile.writeLong(addressValue);
		} catch (IOException e) {
			
		}
		
	}
	
}
