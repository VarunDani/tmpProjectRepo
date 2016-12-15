package com.walmart.intern.service;

public interface AllocationService {

	//Row column attribute for screen
	public static final int ROWS_IN_SCREEN = 10;
	public static final int SEATS_IN_ROW = 20;
	
	
	//Business Logic for allocating Seats for both approaches
	public StringBuilder allocateSeats(String inputPath);
	
}
