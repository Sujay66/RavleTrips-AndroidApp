
package com.raveltrips.android.ravel.models;



import java.util.List;


/**
 * @author Akash Anjanappa (akash.a2351@gmail.com)
 * 07-Apr-2017 
 * General Response model for rest api calls.
 */
public class ResponseModel {

    private List<?> payLoad;

    private String status;

    private String message;
    
    public List<?> getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(List<?> payLoad) {
        this.payLoad = payLoad;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        if (null == message) {
            return "OK";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
