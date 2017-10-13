package com.saddacampus.app.app.DataObjects;

import java.io.Serializable;

/**
 * Created by Devesh Anand on 08-06-2017.
 */

public class Category implements Serializable{

    private String categoryName;
    private String imageResourceId;
    private String tags;
    private String isOffer;
    private String isVeg;

    public Category(String categoryName, String imageResourceId, String tags, String isOffer, String isVeg) {

        this.categoryName = categoryName;
        this.imageResourceId = imageResourceId;
        this.tags = tags;
        this.isOffer=isOffer;
        this.isVeg = isVeg;
    }

    public String getIsOffer() {
        return isOffer;
    }
    public String getIsVeg() {
        return isVeg;
    }
    public String getTags() {
        return tags;
    }

    public String getCategoryName() {
        return categoryName;
    }




    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageResourceId() {
        return imageResourceId;
    }


   /* public String getSubCategoryName()
    {

        return  s;
    }*/

    public void setImageResourceId(String imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getNumberOfTags(){
        String res[] = tags.split(":");
        int length = res.length;

        return length;
    }
}
