
package com.raveltrips.android.ravel.models;

import java.util.List;


/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 09-Apr-2017
 */
public class Cart {
	
	private List<String> tripIds;
	private List<String> pindropIds;
	/**
	 * @param tripIds
	 * @param pindropIds
	 */
	public Cart(List<String> tripIds, List<String> pindropIds) {
		super();
		this.tripIds = tripIds;
		this.pindropIds = pindropIds;
	}
	
	public Cart(){}

	/**
	 * @return the tripIds
	 */
	public List<String> getTripIds() {
		return tripIds;
	}

	/**
	 * @param tripIds the tripIds to set
	 */
	public void setTripIds(List<String> tripIds) {
		this.tripIds = tripIds;
	}

	/**
	 * @return the pindropIds
	 */
	public List<String> getPindropIds() {
		return pindropIds;
	}

	/**
	 * @param pindropIds the pindropIds to set
	 */
	public void setPindropIds(List<String> pindropIds) {
		this.pindropIds = pindropIds;
	}


}
