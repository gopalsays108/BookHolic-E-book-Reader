package com.gopal.allbooks;

public class MoreBookModel {

    public String authorName;
    public String bookName;
    public String coverUrl;
    private String date;
    public String key;
    public String pdfUrl;
    private long number;
    public String privacy;

    public MoreBookModel() {

    }

    public MoreBookModel(String authorName, String bookName, String coverUrl,
                         String date, String key, String pdfUrl,
                         long number, String privacy) {
        this.authorName = authorName;
        this.bookName = bookName;
        this.coverUrl = coverUrl;
        this.date = date;
        this.key = key;
        this.pdfUrl = pdfUrl;
        this.number = number;
        this.privacy = privacy;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
