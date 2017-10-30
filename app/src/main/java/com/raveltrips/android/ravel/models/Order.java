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
 * 09-Apr-2017
 */
public class Order {


	private String id;
	private String profileId;
	private List<String> tripIds;
	private List<String> pindropIds;
	private String orderDate;
	private Double amount;
	
	public Order(){}

	public Order(String profileId, List<String> tripIds, List<String> pindropIds, String orderDate) {
		super();
		this.profileId = profileId;
		this.tripIds = tripIds;
		this.pindropIds = pindropIds;
		this.orderDate = orderDate;
	}
	
	public Order copyFromOrder(Order order){
		if(!IsEmpty(order.getProfileId()))this.profileId = order.getProfileId();
		if(order.getTripIds()!=null)this.tripIds = order.getTripIds();
		if(order.getPindropIds()!=null)this.pindropIds = order.getPindropIds();
		if(!IsEmpty(orderDate))this.orderDate = order.getOrderDate();
		setAmount(order.getAmount());
		return this;
	}
	
	boolean IsEmpty(String str){
		return (str == null || str.isEmpty());
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}

	/**
	 * @param profileId the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

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

	/**
	 * @return the orderDate
	 */
	public String getOrderDate() {
		return orderDate;
	}

	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	

	
}
