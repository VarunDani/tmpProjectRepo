package com.walmart.intern.main;

import java.io.File;
import java.io.FileWriter;

import com.walmart.intern.service.AllocationService;
import com.walmart.intern.service.BatchAllocationService;
import com.walmart.intern.service.IterativeAllocationService;

/**
 * This class is developed for Internship homework for Walmart(VA)
 * It is main class for allocating seats for Movie Theater with Seats capacity 200 (10*20)
 * 
 * Usage Info : AllocateSeating [-b]/[-i] <FilePath>
 * 
 * <FilePath> : Input File Path that contains Request Id and Number of Seats
 * 
 * [-b] : Batch Processing of Reservation Requests and generating output at End.
 * 
 * [-i] : Iterative processing of Reservation Requests and generating output.
 * 
 * 
 * 
 * 	@author Varun Dani
 *	@version 1.0
 */
public class AllocateSeating {

	public static void main(String[] args) {
		
		AllocationService service = null;
		StringBuilder resultSb = null;
		try
		{
			//For Input Arguments 
			if(checkInputArgs(args))
			{
				//boolean fragmentAllowed  = false;
				
				//Argument Structure is true - Proceed for methodology
				if(args[0].equals("-b"))
				{
					service = new BatchAllocationService();
				}
				else if(args[0].equals("-i"))
				{
					service = new IterativeAllocationService();
				}
				
				resultSb = service.allocateSeats(args[1]);
				
				//Exporting Result to File 
				if(createOutputFile(args[1],resultSb))
				{
					System.out.println("Output File Created Successfully : "+args[1]+".output");
				}
				
			//Completed Successfully
			}
			else
			{
				showUsageError();
			}
		
		}
		catch(Exception e)
		{
			showUsageError();
		}
	}
	
	
	
	/**
	 * Showing Usage Error Details on System Error Console
	 * 
	 */
	private static void showUsageError()
	{
		System.err.println("Usage: AllocateSeating [-b]/[-i] <FilePath> ");
	}
	
	
	
	/**
	 * Check for Input arguments
	 * 
	 * @param args
	 * @return
	 */
	private static boolean checkInputArgs(String[] args)
	{
		try
		{
			if(args!=null && args.length>=2 && (args[0].equals("-b") || args[0].equals("-i")))
				return true;
		}
		catch(Exception e)
		{
			//case if null passed as args[0]
			return false;
		}
		
		return false;
	}
	
	
	/**
	 * To write calculated Output to File 
	 * 
	 * @param filePath
	 * @param output
	 * @return
	 */
	private static boolean createOutputFile(String filePath,StringBuilder output)
	{
		File outputFile = null;
		try
		{
			outputFile = new File(filePath+".output");
			FileWriter fileWriter = new FileWriter(outputFile);

	        fileWriter.write(output.toString());
			
	        fileWriter.close();
			return true;
		}
		catch(Exception e)
		{
			System.err.println("Problem in Creating Output File");
			return false;
		}
	}
}
