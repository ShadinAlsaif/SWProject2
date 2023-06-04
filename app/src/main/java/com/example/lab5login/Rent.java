package com.example.lab5login;

import java.io.Serializable;
import java.util.Date;

public class Rent implements Serializable {
    int rentId = 0;
    String startDate = "";
    String endDate = "";
    int productId = 0;

    public Rent() {
    }

    public Rent(int rentId,String startDate, String endDate, int productId) {
        this.rentId = rentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.productId = productId;
    }

    public int getRentId() {
        return rentId;
    }

    public void setRentId(int rentId) {
        this.rentId = rentId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
