package com.walmart.intern.model;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

import com.walmart.intern.service.AllocationService;


/**
 *  Theater Class Model Getting Map of Theater Screen and Seats 
 *  
 * @author Varun Dani
 * @version 1.0
 */
public class Theater {

	//Reverse Order Tree Map For Priority J -> A
	private TreeMap<String,LinkedList<Integer>> seats = new TreeMap<String,LinkedList<Integer>>(Comparator.reverseOrder());
	private static Theater theaterInstance = null;
	
	private Theater(){
		
		if(seats.size()<=0)
		{
			//initialize tree map
			initializeSeatsMap();
		}
		
	}
	
	/**
	 * Factory method for getting Instance
	 * 
	 * @return
	 */
	public static Theater getInstance()
	{
		if(theaterInstance==null)
			return new Theater();
		return theaterInstance;
	}
	
	/**
	 * Initialize Seats Map with Predefined Values 
	 */
	private void initializeSeatsMap()
	{
		for(int i=0;i<AllocationService.ROWS_IN_SCREEN;i++)
		{
			seats.put(Character.toString((char)(i+65)), initializeRows());
		}
	}
	
	
	/**
	 * Initializing seats in each Row 
	 * 
	 * @return
	 */
	private LinkedList<Integer> initializeRows()
	{
		LinkedList<Integer> arrLstObj = new LinkedList<Integer>();
		for(int i = 0;i<AllocationService.SEATS_IN_ROW;i++)
		{
			arrLstObj.add(i+1);
		}
		return arrLstObj;
	}
	
	
	//Getter Setter for Seats Map 

	public TreeMap<String, LinkedList<Integer>> getSeats() {
		return seats;
	}

	public void setSeats(TreeMap<String, LinkedList<Integer>> seats) {
		this.seats = seats;
	}

}
