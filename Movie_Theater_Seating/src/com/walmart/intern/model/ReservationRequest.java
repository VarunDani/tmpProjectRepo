package com.walmart.intern.model;


/**
 * Reservation Request Model Class that contains reservation entity as whole
 * Reservation ID
 * Number of Reservation per ID
 * 
 * @author Vaurn Dani
 * @version 1.0
 *
 */
public class ReservationRequest implements Comparable<ReservationRequest>{

	
	private String reservationId;
	private int noOfSeats;
	private String[] allocatedSeats;
	
	//Constructors 
	
	public ReservationRequest(){}
	
	public ReservationRequest(String reservationId, int noOfSeats) {
		super();
		this.reservationId = reservationId;
		this.noOfSeats = noOfSeats;
		this.allocatedSeats = new String[noOfSeats];
	}
	
	
	//Relevant Getter Setter
	
	public String getReservationId() {
		return reservationId;
	}
	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}
	public int getNoOfSeats() {
		return noOfSeats;
	}
	public void setNoOfSeats(int noOfSeats) {
		this.noOfSeats = noOfSeats;
	}
	public String[] getAllocatedSeats() {
		return allocatedSeats;
	}
	public void setAllocatedSeats(String[] allocatedSeats) {
		this.allocatedSeats = allocatedSeats;
	}
	
	
	/**
	 * Customize To String Method
	 */
	@Override
	public String toString() {
		return "Reservation Id=" + reservationId + ", Number of Seats=" + noOfSeats + "]";
	}

	
	/**
	 * Implemented Compare to Method for sorting of Array of Objects For Approach 1
	 * comparing two objects based on number of seats 
	 */
	@Override
	public int compareTo(ReservationRequest resReq) {
		return (noOfSeats - resReq.noOfSeats);
	}

	
	public StringBuilder getOutputString()
	{
		StringBuilder optString = new StringBuilder();
		optString.append(reservationId);
		optString.append(" ");
		
		for(int i=0;i<allocatedSeats.length;i++)
		{
			if(allocatedSeats[i]!=null && !allocatedSeats[i].equals(""))
			{
				optString.append(allocatedSeats[i]);
				optString.append(",");
			}
		}
		
		optString.deleteCharAt(optString.length() - 1);
		return optString;
	}
	
}
