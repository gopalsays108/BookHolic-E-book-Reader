package com.gopal.ebookapp;

public class LikedBookModel {

    String bookKey;
    String date;

    public LikedBookModel() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public LikedBookModel(String bookKey, String postUserId) {
        this.bookKey = bookKey;
        this.date = postUserId;
    }

    public String getBookKey() {
        return bookKey;
    }

    public void setBookKey(String bookKey) {
        this.bookKey = bookKey;
    }
}
