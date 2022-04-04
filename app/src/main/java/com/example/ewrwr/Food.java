package com.example.ewrwr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Food {

    Bitmap foodImage;
    String foodName;
    String foodDate;
    String foodDday;

    public Food(Bitmap foodImage, String foodName, String foodDate, String foodDday) {
        this.foodImage = foodImage;
        this.foodName = foodName;
        this.foodDate = foodDate;
        this.foodDday = foodDday;
    }

    public Bitmap getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(Bitmap foodImage) {
        this.foodImage = foodImage;
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

    public String getFoodDday() {
        return foodDday;
    }

    public void setFoodDday(String foodDday) {
        this.foodDday = foodDday;
    }
}
