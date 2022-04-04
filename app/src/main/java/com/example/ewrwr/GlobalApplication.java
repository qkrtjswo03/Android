package com.example.ewrwr;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

public class GlobalApplication extends Application {
    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;

    private String Email; //이메일(아이디) 전역변수로 선언
    private String Nick;
    private String profile;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    String barcode;

    public String getFoodIMG() {
        return foodIMG;
    }

    public void setFoodIMG(String foodIMG) {
        this.foodIMG = foodIMG;
    }

    String foodIMG;

    private int dYear, dMonth, dDay; //유통기한 전역변수로 선언

    public int getdYear() {
        return dYear;
    }

    public void setdYear(int dYear) {
        this.dYear = dYear;
    }

    public int getdMonth() {
        return dMonth;
    }

    public void setdMonth(int dMonth) {
        this.dMonth = dMonth;
    }

    public int getdDay() {
        return dDay;
    }

    public void setdDay(int dDay) {
        this.dDay = dDay;
    }



    public String getNick() {
        return Nick;
    }
    public void setNick(String nick) {
        Nick = nick;
    }
    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        GlobalApplication.currentActivity = currentActivity;
    }
}