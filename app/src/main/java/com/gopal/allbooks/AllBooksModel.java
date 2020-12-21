package com.gopal.allbooks;

public class AllBooksModel {

    public String authorName;
    public String bookName;
    public String coverUrl;
    public String desc;
    public String date;
    public String uploadId;
    public String type;
    public String key;
    public String pdfUrl;
    public long number;
    public String currentUserId;

    public AllBooksModel() {
    }

//    public AllBooksModel(String authorName, String bookName, String coverUrl, String desc,
//                         String date, String uploadId, String type, String pdfName, long number, String key,
//                         String currentUserId) {
//        this.authorName = authorName;
//        this.bookName = bookName;
//        this.coverUrl = coverUrl;
//        this.desc = desc;
//        this.date = date;
//        this.uploadId = uploadId;
//        this.type = type;
//        this.pdfUrl = pdfName;
//        this.number = number;
//        this.key = key;
//        this.currentUserId = currentUserId;
//    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
