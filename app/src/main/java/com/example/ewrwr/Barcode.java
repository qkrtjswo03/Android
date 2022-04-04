package com.example.ewrwr;

public class Barcode {
    public String getBacordNum() {
        return bacordNum;
    }

    public void setBacordNum(String bacordNum) {
        this.bacordNum = bacordNum;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodDate() {
        return foodDate;
    }

    public void setFoodDate(String foodDate) {
        this.foodDate = foodDate;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    String bacordNum;
    String foodName;
    String foodDate;
    String foodCategory;
    String foodImage;


    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }



    //변수에대한 처리르 위한 생성자
    public Barcode(String bacordNum, String foodName, String foodDate, String foodCategory, String foodImage){
        this.bacordNum = bacordNum;
        this.foodName = foodName;
        this.foodDate = foodDate;
        this.foodCategory = foodCategory;
        this.foodImage = foodImage;
    }
}