package com.example.ewrwr;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ScanResultRequest extends StringRequest {

    final static private String URL = "http://qkfldk197.dothome.co.kr/foodInsert.php";
    private Map<String, String> parameters;

    public ScanResultRequest(String foodName, String foodDate, String division, String category, String foodImage, String Email, Response.Listener<String> listener) { //생성자 호출
        super(Request.Method.POST, URL, listener, null); //상속받은 StringRequest 의 생성자를 부른다. (어떤 방법으로 어디에, 에러시)
        parameters = new HashMap<>(); //해쉬맵 형태로 값을 받아옴
        parameters.put("foodName", foodName);
        parameters.put("foodDate", foodDate);
        parameters.put("division", division);
        parameters.put("category", category); //("태그", 실제데이터) 형태로 저장
        parameters.put("foodImage", foodImage);
        parameters.put("Email", Email);
    }


    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
