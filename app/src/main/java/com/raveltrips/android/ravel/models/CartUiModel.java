/* Copyright 2017 - www.raveltrips.com
* You may not use this file except in compliance with the License.
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.raveltrips.android.ravel.models;

import java.util.List;

/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 15-Apr-2017
 * model for transport of actual cart items to client
 */
public class CartUiModel {
	
	private List<Trip> trips;
	private List<Pindrop> pindrops;
	
	public CartUiModel(){}

	/**
	 * @return the trips
	 */
	public List<Trip> getTrips() {
		return trips;
	}

	/**
	 * @param trips the trips to set
	 */
	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}

	/**
	 * @return the pindrops
	 */
	public List<Pindrop> getPindrops() {
		return pindrops;
	}

	/**
	 * @param pindrops the pindrops to set
	 */
	public void setPindrops(List<Pindrop> pindrops) {
		this.pindrops = pindrops;
	}
	
	

}
