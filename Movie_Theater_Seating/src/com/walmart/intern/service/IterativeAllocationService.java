package com.walmart.intern.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import com.walmart.intern.model.MaxQueueObj;
import com.walmart.intern.model.ReservationRequest;
import com.walmart.intern.model.Theater;

public class IterativeAllocationService implements AllocationService{

	TreeMap<String,LinkedList<Integer>> seats = null;
	int totalSeats = AllocationService.ROWS_IN_SCREEN * AllocationService.SEATS_IN_ROW;
	
	@Override
	public StringBuilder allocateSeats(String filePath)
	{
		StringBuilder output = null;
		File inputFile = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		ReservationRequest reservReq = null;
		
		try
		{
			inputFile = new File(filePath);
			fis = new FileInputStream(inputFile);
			br = new BufferedReader(new InputStreamReader(fis));
		 
			output = new StringBuilder();
			
			//Getting Seats
			seats = Theater.getInstance().getSeats();
			
			String line = null;
			while ((line = br.readLine()) != null) {
				
				String[] input = line.split(" ");
				
				//Separate Requests From Input Strings
				reservReq = new ReservationRequest(input[0],Integer.parseInt(input[1]));
				
				selectBestSeatForReq(reservReq);
				
				output.append(reservReq.getOutputString());
				output.append("\n");
			
			}
			
			fis.close();
			br.close();
		}
		catch(Exception e )
		{
			System.err.println("Problem in reading File Input-Assigning Seats ");
		}
		return output;
	}
	
	
	/**
	 * 
	 * Selecting Best Possible Option from given Request 
	 * 
	 * @param requestid
	 * @param noOfSeats
	 * @param output
	 */
	protected void selectBestSeatForReq(ReservationRequest reservReq)
	{
		try
		{
			boolean requestAsgnmnt = false;
			int noOfSeats = reservReq.getNoOfSeats();
			for(Map.Entry<String,LinkedList<Integer>> row : seats.entrySet())
			{
				//This Row has available Seats for Request
				if(row.getValue().size()>=noOfSeats)
				{
					//Marking for Not Fragmentation
					requestAsgnmnt = true;
					
					//Allocate Available Seats from seats map for noOfSeats times
					String[] allocatedSeats = new String[noOfSeats];
					for(int i =0;i<noOfSeats;i++)
					{
						allocatedSeats[i] = row.getKey()+row.getValue().getFirst();
						seats.get(row.getKey()).removeFirst();
						totalSeats--;
					}
					reservReq.setAllocatedSeats(allocatedSeats);
					break;
				}
			}
			
			//This is also for Case when Request of seats will be greater then total seats in one Row 
			if(!requestAsgnmnt)
			{
				//No continuous Seats Available for given request - Do Fragmentation
				assignFragmentedSeats(reservReq);
			}
		}
		catch(Exception e)
		{
			System.err.println("Problem in Selecting Best Option For Request ");
		}

	}


	/**
	 * This will be heavy method for allocation of seats 
	 * 
	 * @param reservReq
	 */
	private void assignFragmentedSeats(ReservationRequest reservReq) 
	{
		//Create Max Priority Queue of Rows and capacities
		PriorityQueue<MaxQueueObj> maxQueue = null;
		maxQueue = getMaxPriorityQueue();
		
		
		if(anySeatsAvailable())
		{
			//Seats are available and check for seats available are match for requested
			if(reservReq.getNoOfSeats()<=totalSeats)
			{
				assignSeats(reservReq,maxQueue);
			}
			else
			{
				//Do Nothing - Skip This Request 
			}
		}
		//Theater Full
		
	}



	/**
	 * For Updating MaxQueue
	 * @param maxQueue
	 */
	private PriorityQueue<MaxQueueObj> getMaxPriorityQueue() {
		
		PriorityQueue<MaxQueueObj> maxQueue = new PriorityQueue<MaxQueueObj>();
		MaxQueueObj tmpObj = null;
		for(Map.Entry<String,LinkedList<Integer>> row : seats.entrySet())
		{
			tmpObj = new MaxQueueObj();
			tmpObj.setRow(row.getKey());
			tmpObj.setCapacity(row.getValue().size());
			maxQueue.add(tmpObj);
		}
		return maxQueue;
	}
	
	
	/**
	 * Check for No Empty Seats after fragmentation
	 * @param maxQueue
	 * @return
	 */
	private boolean anySeatsAvailable() {
		if(totalSeats>0)
			return true;
		return false;
	}
	
	
	
	/**
	 * To assing seats will take larger time to allocate fragmented seats to User
	 * 
	 * @param reservReq
	 * @param maxQueue
	 */
	private void assignSeats(ReservationRequest reservReq, PriorityQueue<MaxQueueObj> maxQueue) {
		
		int requestedSeats = reservReq.getNoOfSeats();
		MaxQueueObj tmpObj = null;
		
		String[] allocatedSeats = new String[requestedSeats];
		int j =0;
		while(requestedSeats>0)
		{
			tmpObj = maxQueue.poll();
			int size = seats.get(tmpObj.getRow()).size();
			for(int i=0;i<size;i++)
			{
				if(requestedSeats<=0)break;
				allocatedSeats[j++] = tmpObj.getRow()+seats.get(tmpObj.getRow()).getFirst();
				seats.get(tmpObj.getRow()).removeFirst();
				tmpObj.setCapacity(tmpObj.getCapacity()-1);
				totalSeats--;
				requestedSeats--;
			}
			maxQueue.add(tmpObj);
		}
		reservReq.setAllocatedSeats(allocatedSeats);
		
	}


}
