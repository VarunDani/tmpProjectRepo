package com.walmart.intern.model;

public class MaxQueueObj implements Comparable<MaxQueueObj>{

	private String row;
	private Integer capacity;
	
	
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}


	/**
	 * Will Generated in Descending Order 
	 */
	@Override
	public int compareTo(MaxQueueObj o) {
		return o.getCapacity().compareTo(getCapacity());
	}
	

}
