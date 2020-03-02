package com.mortex.accenture.task.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "TempList")
public class TempModel {

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public void setId(int id) {
        this.id = id;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @ColumnInfo(name = "Temp")
    private double temp;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "date")
    private long date;

    public String getCity() {
        return city;
    }

    public Integer getId() {
        return id;
    }

    public double getTemp() {
        return temp;
    }

    public long getDate() {
        return date;
    }

    public TempModel(Integer id, double temp, long date, String city) {
        this.id = id;
        this.temp = temp;
        this.date = date;
        this.city = city;
    }


}
