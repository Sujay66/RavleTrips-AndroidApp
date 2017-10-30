
package com.raveltrips.android.ravel.models;


import java.io.Serializable;

/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 09-Apr-2017
 */
public class GPSCoOrdinate implements Serializable{
	
	private String lattitude;
	private String longitude;
	/**
	 * @param lattitude
	 * @param longitude
	 */
	public GPSCoOrdinate(String lattitude, String longitude) {
		super();
		this.lattitude = lattitude;
		this.longitude = longitude;
	}
	
	public GPSCoOrdinate() {}

	/**
	 * @return the lattitude
	 */
	public String getLattitude() {
		return lattitude;
	}

	/**
	 * @param lattitude the lattitude to set
	 */
	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
