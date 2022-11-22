package com.Duong.Expense.Object;

import java.io.Serializable;
// objective Trip
public class Trip implements Serializable { // implements serializable bam nho ra gui ra network
    private int id;
    private String NameTrip;
    private String Destination;
    private String DateFrom;
    private String DateTo;
    private String Risk;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return NameTrip;
    }

    public void setName(String name) {
        this.NameTrip = name;
    }

    public String getDes() {
        return Destination;
    }

    public void setDes(String des) {
        this.Destination = des;
    }

    public String getDateFrom() {
        return DateFrom;
    }

    public void setDateFrom(String date) {
        this.DateFrom = date;
    }

    public String getDateTo() {
        return DateTo;
    }

    public void setDateTo(String date) {
        this.DateTo = date;
    }


    public String getRisk() {
        return Risk;
    }

    public void setRisk(String risk) {
        this.Risk = risk;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public Trip(int id, String name, String des, String dateFrom, String dateTo, String risk, String desc) {
        this.id = id;
        this.NameTrip = name;
        this.Destination = des;
        this.DateFrom = dateFrom;
        this.DateTo = dateTo;
        this.Risk = risk;
        this.desc = desc;
    }

    public Trip(String name, String des, String dateFrom, String dateTo, String risk, String desc) {
        this.NameTrip = name;
        this.Destination = des;
        this.DateFrom = dateFrom;
        this.DateTo = dateTo;
        this.Risk = risk;
        this.desc = desc;
    }

    public Trip() {

    }


    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", name='" + NameTrip + '\'' +
                ", des='" + Destination + '\'' +
                ", dateFrom='" + DateFrom + '\'' +
                ", dateTo='" + DateTo + '\'' +
                ", risk='" + Risk + '\'' +
                ", desc=" + desc +
                '}';
    }
}