package com.walmart.intern.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.walmart.intern.model.ReservationRequest;

public class BatchAllocationService implements AllocationService{

	@Override
	public StringBuilder allocateSeats(String filePath) {

		StringBuilder output = null;
		File inputFile = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		ArrayList<ReservationRequest> requestList = null;
		ReservationRequest reservReq = null;
		try
		{
			inputFile = new File(filePath);
			fis = new FileInputStream(inputFile);
			br = new BufferedReader(new InputStreamReader(fis));
			
			output = new StringBuilder();
			requestList = new ArrayList<ReservationRequest>();
			
			String line = null;
			while ((line = br.readLine()) != null) {
				
				String[] input = line.split(" ");
				
				reservReq = new ReservationRequest(input[0],Integer.parseInt(input[1]));
				requestList.add(reservReq);
				
			}
			
			//Sort Array By descending Order of Requested Seats
			Collections.sort(requestList, new Comparator<ReservationRequest>(){
			     public int compare(ReservationRequest o1, ReservationRequest o2){
			         if(o1.getNoOfSeats() == o2.getNoOfSeats())
			             return 0;
			         return o1.getNoOfSeats() < o2.getNoOfSeats() ? 1 : -1;
			     }
			});
			
			
			
			//Sort Array By Request Id
			Collections.sort(requestList, new Comparator<ReservationRequest>(){
			     public int compare(ReservationRequest o1, ReservationRequest o2){
			    	 return o1.getReservationId().compareTo(o2.getReservationId());
			     }
			});
			

			
			
		}
		catch(Exception e )
		{
			
		}
		return output;
	}
	
	

}
