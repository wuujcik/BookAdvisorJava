package com.wuujcik.bookadvisor;

public class Book {

    private String[] mAuthor;
    private String mTitle;
    private String mDescription;
    private double mPrice;
    private String mWeb;
    private String mImage;

    public Book (String[] author, String title, String description, double price, String web, String image){
        mAuthor = author;
        mTitle = title;
        mDescription = description;
        mPrice = price;
        mWeb = web;
        mImage = image;
    }

    public String[] getmAuthor() {
        return mAuthor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public double getmPrice() {
        return mPrice;
    }

    public String getmWeb() {
        return mWeb;
    }

    public String getmImage() {
        return mImage;
    }
}
