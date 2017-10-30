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
public class Profile {

	private String id;
	private String name;
	private String email;
	private String address="";
	private String mobNo="";
	private String imageUrl="";
	private List<String> tripsCompletedIds;
	private List<String> tripsWishlistIds;
	private List<String> orderIds;
	private Cart cart;
	private String createdDate;
	private String modifiedDate;
	private String role="USER";
	
	public Profile(){}

	
	public Profile(String name,  String email, List<String> tripsCompletedIds,
			List<String> tripsWishlistIds, List<String> tripsUpcomingIds, List<String> orderIds, Cart cart,
			String createdDate,String role) {
		super();
		this.name = name;
		this.email = email;
		this.tripsCompletedIds = tripsCompletedIds;
		this.tripsWishlistIds = tripsWishlistIds;
		this.orderIds = orderIds;
		this.cart = cart;
		this.createdDate = createdDate;
		this.role = role;
	}
	
	public Profile copyFromProfile(Profile updatedProfile){
		
		if(updatedProfile.getCart()!=null) this.setCart(updatedProfile.getCart());
		if(!IsEmpty(updatedProfile.getCreatedDate()))this.setCreatedDate(updatedProfile.getCreatedDate());
		if(!IsEmpty(updatedProfile.getEmail()))this.setEmail(updatedProfile.getEmail());
		if(!IsEmpty(updatedProfile.getName()))this.setName(updatedProfile.getName());
		if(updatedProfile.getOrderIds()!=null)this.setOrderIds(updatedProfile.getOrderIds());
		if(!IsEmpty(updatedProfile.getRole()))this.setRole(updatedProfile.getRole());
		if(updatedProfile.getTripsCompletedIds()!=null)this.setTripsCompletedIds(updatedProfile.getTripsCompletedIds());
		if(updatedProfile.getTripsWishlistIds()!=null)this.setTripsWishlistIds(updatedProfile.getTripsWishlistIds());
		if(!IsEmpty(updatedProfile.getMobNo()))this.setMobNo(updatedProfile.getMobNo());
		if(!IsEmpty(updatedProfile.getAddress()))this.setAddress(updatedProfile.getAddress());
		if(!IsEmpty(updatedProfile.getImageUrl()))this.setImageUrl(updatedProfile.getImageUrl());
		
		return this;
	}
	
	boolean IsEmpty(String str){
		return (str == null || str.isEmpty());
	}


	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}


	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}


	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}


	/**
	 * @return the mobNo
	 */
	public String getMobNo() {
		return mobNo;
	}


	/**
	 * @param mobNo the mobNo to set
	 */
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}


	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}


	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}


	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}


	/**
	 * @return the modifiedDate
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}


	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the tripsCompletedIds
	 */
	public List<String> getTripsCompletedIds() {
		return tripsCompletedIds;
	}

	/**
	 * @param tripsCompletedIds the tripsCompletedIds to set
	 */
	public void setTripsCompletedIds(List<String> tripsCompletedIds) {
		this.tripsCompletedIds = tripsCompletedIds;
	}

	/**
	 * @return the tripsWishlistIds
	 */
	public List<String> getTripsWishlistIds() {
		return tripsWishlistIds;
	}

	/**
	 * @param tripsWishlistIds the tripsWishlistIds to set
	 */
	public void setTripsWishlistIds(List<String> tripsWishlistIds) {
		this.tripsWishlistIds = tripsWishlistIds;
	}

	/**
	 * @return the orderIds
	 */
	public List<String> getOrderIds() {
		return orderIds;
	}

	/**
	 * @param orderIds the orderIds to set
	 */
	public void setOrderIds(List<String> orderIds) {
		this.orderIds = orderIds;
	}

	/**
	 * @return the cart
	 */
	public Cart getCart() {
		return cart;
	}

	/**
	 * @param cart the cart to set
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

}
