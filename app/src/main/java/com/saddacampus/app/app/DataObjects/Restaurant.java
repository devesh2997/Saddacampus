package com.saddacampus.app.app.DataObjects;

import java.io.Serializable;


/**
 * Created by Devesh Anand on 10-06-2017.
 */

public class Restaurant implements Serializable {

    private String name;
    private String contact;
    private boolean acceptsOnlinePayment;
    private String address;
    private String code;
    private String imageResourceId;
    private String minOrderAmount;
    private String timing;
    private float deliveryCharges;
    private float deliveryChargeSlab;
    private int newOffer;
    private int hasNewOffer;
    private int offer;
    private String status;
    private String speciality;
    private float rating;
    private int messageStatus;
    private String message;

    public Restaurant(String name, String contact, boolean acceptsOnlinePayment, String address, String code, String imageResourceId, String minOrderAmount, String timing, float deliveryCharges, float deliveryChargeSlab, int newOffer, int hasNewOffer, int offer, String status, String speciality, float rating, int messageStatus, String message) {
        this.name = name;
        this.contact = contact;
        this.acceptsOnlinePayment = acceptsOnlinePayment;
        this.address = address;
        this.code = code;
        this.imageResourceId = imageResourceId;
        this.minOrderAmount = minOrderAmount;
        this.timing = timing;
        this.deliveryCharges = deliveryCharges;
        this.deliveryChargeSlab = deliveryChargeSlab;
        this.newOffer = newOffer;
        this.hasNewOffer = hasNewOffer;
        this.offer = offer;
        this.status = status;
        this.speciality = speciality;
        this.rating = rating;
        this.messageStatus = messageStatus;
        this.message = message;
    }

    public boolean acceptsOnlinePayment() {
        return acceptsOnlinePayment;
    }

    public void setAcceptsOnlinePayment(boolean acceptsOnlinePayment) {
        this.acceptsOnlinePayment = acceptsOnlinePayment;
    }

    public float getDeliveryChargeSlab() {
        return deliveryChargeSlab;
    }

    public void setDeliveryChargeSlab(float deliveryChargeSlab) {
        this.deliveryChargeSlab = deliveryChargeSlab;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public String getMessage() {
        return message;
    }

    public int getHasNewOffer() {
        return hasNewOffer;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(String imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(String minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public int getNewOffer() {
        return newOffer;
    }

    public int getOffer() {
        return offer;
    }






}
