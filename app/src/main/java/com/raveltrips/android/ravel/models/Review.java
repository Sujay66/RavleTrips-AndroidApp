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

/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 09-Apr-2017
 */
public class Review implements Serializable {

	private String profileId;
	private String username;
	private String createdDate;
	private String modifiedDate;
	private String text;
	private Double rating;
	
	public Review() {}

	public Review(String profileId, String username, String createdDate, String text,Double rating) {
		super();
		this.profileId = profileId;
		this.username = username;
		this.createdDate = createdDate;
		this.text = text;
		this.rating = rating;
	}
	
	public Review copyFromReview(Review review){
		if(!IsEmpty(review.getProfileId()))this.profileId = review.getProfileId();
		if(!IsEmpty(review.getUsername()))this.username = review.getUsername();
		if(!IsEmpty(review.getCreatedDate()))this.createdDate = review.getCreatedDate();
		if(!IsEmpty(review.getText()))this.text = review.getText();
		if(review.getRating()>0)this.rating=review.getRating();
		
		return this;
			
	}
	
	boolean IsEmpty(String str){
		return (str == null || str.isEmpty());
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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


}
