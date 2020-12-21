package com.gopal.requestbook;

public class RequestModel {

    public String authorName;
    public String bookName;
    public String date;

    public RequestModel() {

    }

    public RequestModel(String authorName, String bookName , String date) {
        this.authorName = authorName;
        this.bookName = bookName;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
