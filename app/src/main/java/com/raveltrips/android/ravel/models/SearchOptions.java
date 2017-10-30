/* Copyright 2017 - www.raveltrips.com
* You may not use this file except in compliance with the License.
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.raveltrips.android.ravel.models;



/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 14-Apr-2017
 * Model DTO for search options  -Android
 */
public class SearchOptions {

	private String searchLocation;
	private Double priceStart;
	private Double priceEnd;
	private Double ratingGreaterThan;
	private String currentAddress;
	private GPSCoOrdinate currentCoOrds;
	private Double rangeStart;
	private Double rangeEnd;
	private Boolean isCurrentAddressSearch = false;
	
	public SearchOptions(){}
	
	
	/**
	 * @return the searchLocation
	 */
	public String getSearchLocation() {
		return searchLocation;
	}
	/**
	 * @param searchLocation the searchLocation to set
	 */
	public void setSearchLocation(String searchLocation) {
		this.searchLocation = searchLocation;
	}
	/**
	 * @return the priceStart
	 */
	public Double getPriceStart() {
		return priceStart;
	}
	/**
	 * @param priceStart the priceStart to set
	 */
	public void setPriceStart(Double priceStart) {
		this.priceStart = priceStart;
	}
	/**
	 * @return the priceEnd
	 */
	public Double getPriceEnd() {
		return priceEnd;
	}
	/**
	 * @param priceEnd the priceEnd to set
	 */
	public void setPriceEnd(Double priceEnd) {
		this.priceEnd = priceEnd;
	}
	/**
	 * @return the ratingGreaterThan
	 */
	public Double getRatingGreaterThan() {
		return ratingGreaterThan;
	}
	/**
	 * @param ratingGreaterThan the ratingGreaterThan to set
	 */
	public void setRatingGreaterThan(Double ratingGreaterThan) {
		this.ratingGreaterThan = ratingGreaterThan;
	}
	/**
	 * @return the currentAddress
	 */
	public String getCurrentAddress() {
		return currentAddress;
	}
	/**
	 * @param currentAddress the currentAddress to set
	 */
	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
	/**
	 * @return the currentCoOrds
	 */
	public GPSCoOrdinate getCurrentCoOrds() {
		return currentCoOrds;
	}
	/**
	 * @param currentCoOrds the currentCoOrds to set
	 */
	public void setCurrentCoOrds(GPSCoOrdinate currentCoOrds) {
		this.currentCoOrds = currentCoOrds;
	}
	/**
	 * @return the rangeStart
	 */
	public Double getRangeStart() {
		return rangeStart;
	}
	/**
	 * @param rangeStart the rangeStart to set
	 */
	public void setRangeStart(Double rangeStart) {
		this.rangeStart = rangeStart;
	}
	/**
	 * @return the rangeEnd
	 */
	public Double getRangeEnd() {
		return rangeEnd;
	}
	/**
	 * @param rangeEnd the rangeEnd to set
	 */
	public void setRangeEnd(Double rangeEnd) {
		this.rangeEnd = rangeEnd;
	}
	/**
	 * @return the isCurrentAddressSearch
	 */
	public Boolean getIsCurrentAddressSearch() {
		return isCurrentAddressSearch;
	}
	/**
	 * @param isCurrentAddressSearch the isCurrentAddressSearch to set
	 */
	public void setIsCurrentAddressSearch(Boolean isCurrentAddressSearch) {
		this.isCurrentAddressSearch = isCurrentAddressSearch;
	}
	
	
}
