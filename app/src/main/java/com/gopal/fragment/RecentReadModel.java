package com.gopal.fragment;

public class RecentReadModel {

    public long lastPage;
    public String date;
    public String lastDate;

    public RecentReadModel() {

    }

    public RecentReadModel(long lastPage , String date , String lastDate) {
        this.lastPage = lastPage;
        this.date = date;
        this.lastDate = lastDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getLastPage() {
        return lastPage;
    }

    public void setLastPage(long lastPage) {
        this.lastPage = lastPage;
    }
}
