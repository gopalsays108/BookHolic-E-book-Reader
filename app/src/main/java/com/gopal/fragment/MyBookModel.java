package com.gopal.fragment;

public class MyBookModel {

    public String authorName;
    public String  bookName;
    public String coverUrl;
    public String desc;
    public String pdfUrl;
    private String date;
    public String type;
    public String pdfName;
    private int number;
    public String privacy;
    public String uploadId;

    //--- For Firebase--------
    public MyBookModel(){

    }

    public MyBookModel(String authorName, String bookName, String coverUrl, String desc
            , String pdfUrl, String type, int number , String date , String pdfName ,  String privacy , String uploadId) {
        this.authorName = authorName;
        this.bookName = bookName;
        this.coverUrl = coverUrl;
        this.desc = desc;
        this.pdfUrl = pdfUrl;
        this.type = type;
        this.number = number;
        this.date = date;
        this.pdfName = pdfName;
        this.privacy = privacy;
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getPdfName() {
        return pdfName;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
