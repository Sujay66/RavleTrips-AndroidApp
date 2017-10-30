/* Copyright 2017 - www.raveltrips.com
* You may not use this file except in compliance with the License.
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.raveltrips.android.ravel.models;

import java.io.Serializable;
import java.util.List;


/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 09-Apr-2017
 */
public class Trip implements Serializable {


	private String id;
	private String name;
	private String description;
	private String location;
	private GPSCoOrdinate gps;
	private List<String> videoUrls;
	private List<String> imageUrls;
	private Double price;
	private Double rating;
	private List<Review> reviews;
	private Integer reviewCount;
	private List<String> keywords;
	private List<String> pindropIds;
	private String createdDate;
	private String modifiedDate;
	private String creatorId;


	public Trip(){}


	public Trip(String name, String description, List<String> videoUrls, List<String> imageUrls, Double price,
				Double rating, List<Review> reviews, List<String> keywords, List<String> pindropIds, String createdDate,
				String creatorId,String location) {
		super();
		this.name = name;
		this.description = description;
		this.videoUrls = videoUrls;
		this.imageUrls = imageUrls;
		this.price = price;
		this.rating = rating;
		this.reviews = reviews;
		if(this.reviews!=null)
			this.reviewCount = reviews.size();
		this.keywords = keywords;
		this.pindropIds = pindropIds;
		this.createdDate = createdDate;
		this.creatorId = creatorId;
		this.location=location;
	}

	public Trip copyFromTrip(Trip trip){
		if(!IsEmpty(trip.getName()))this.name = trip.getName();
		if(!IsEmpty(trip.getDescription()))this.description = trip.getDescription();
		if(trip.getVideoUrls()!=null)this.videoUrls = trip.getVideoUrls();
		if(trip.getImageUrls()!=null)this.imageUrls = trip.getImageUrls();
		if(trip.getPrice()>0)this.price = trip.getPrice();
		if(trip.getRating()>0)this.rating = trip.getRating();
		if(trip.getReviews()!=null){
			this.reviews = trip.getReviews();
			this.reviewCount = reviews.size();
		}
		if(trip.getKeywords()!=null)this.keywords = trip.getKeywords();
		if(trip.getPindropIds()!=null)this.pindropIds = trip.getPindropIds();
		if(!IsEmpty(trip.getCreatedDate()))this.createdDate = trip.getCreatedDate();
		if(!IsEmpty(trip.getCreatorId()))this.creatorId = trip.getCreatorId();
		if(!IsEmpty(trip.getLocation()))this.location = trip.getLocation();
		if(trip.getGps()!=null)this.gps = trip.getGps();

		return this;
	}

	boolean IsEmpty(String str){
		return (str == null || str.isEmpty());
	}



	/**
	 * @return the gps
	 */
	public GPSCoOrdinate getGps() {
		return gps;
	}


	/**
	 * @param gps the gps to set
	 */
	public void setGps(GPSCoOrdinate gps) {
		this.gps = gps;
	}


	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}


	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the reviewCount
	 */
	public Integer getReviewCount() {
		return reviewCount;
	}


	/**
	 * @param reviewCount the reviewCount to set
	 */
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the videoUrls
	 */
	public List<String> getVideoUrls() {
		return videoUrls;
	}

	/**
	 * @param videoUrls the videoUrls to set
	 */
	public void setVideoUrls(List<String> videoUrls) {
		this.videoUrls = videoUrls;
	}

	/**
	 * @return the imageUrls
	 */
	public List<String> getImageUrls() {
		return imageUrls;
	}

	/**
	 * @param imageUrls the imageUrls to set
	 */
	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * @return the rating
	 */
	public Double getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(Double rating) {
		this.rating = rating;
	}



	/**
	 * @return the reviews
	 */
	public List<Review> getReviews() {
		return reviews;
	}


	/**
	 * @param reviews the reviews to set
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
		if(this.reviews!=null)
			this.reviewCount=this.reviews.size();
	}


	/**
	 * @return the creatorId
	 */
	public String getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creatorId the creatorId to set
	 */
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

}
