package com.example.ewrwr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ScanResult extends AppCompatActivity {
    Bitmap bitmap;
    final Context context = this;

    private String foodName;
    private String foodDate;
    private String division;
    private String category;
    private String foodImage;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanresult);

        new BackgroundTask().execute();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan(); //바코드 스캔 카메라 실행

        Button inserBtn = (Button)findViewById(R.id.insertBtn); //등록하기 버튼
        inserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"냉장실","냉동실"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // 제목셋팅
                alertDialogBuilder.setTitle("냉장고에 넣기");
                alertDialogBuilder.setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                // 프로그램을 종료한다
                                Toast.makeText(getApplicationContext(),
                                        items[id] + " 선택했습니다.",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                                /*String division = (String) items[id];*/

                                final TextView foondNameTxt = (TextView) findViewById(R.id.foodNameTxt);
                                final TextView foodDateTxt = (TextView) findViewById(R.id.foodDate);
                                final TextView categoryTxt = (TextView)findViewById(R.id.category);
                                final ImageView foodImageView = (ImageView)findViewById(R.id.foodImageView);

                                GlobalApplication mail = (GlobalApplication)getApplication(); //전역변수 email 가져오기


                                String foodName = foondNameTxt.getText().toString();
                                String foodDate = foodDateTxt.getText().toString();
                                String category = categoryTxt.getText().toString();
                                String division = (String) items[id];
                                Log.e("division",division);
                                String Email =  mail.getEmail().toString();
                                Log.e("saveEmail2", mail.getEmail());

                                GlobalApplication ImgUrl = (GlobalApplication)getApplication();

                                Log.e("imgurl", ImgUrl.getFoodIMG());

                                String foodImage = ImgUrl.getFoodIMG();

                                Log.e("error1", foodName);
                                Log.e("error2", foodDate);
                                Log.e("error3", division);
                                Log.e("error4", category);
                                Log.e("error5", foodImage);
                                Log.e("error6", Email);



                                Response.Listener<String> responseListener = new Response.Listener<String>() { //특정 요청 이후에 리스너에서 원하는 결과값을 다룰수 있게 함
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            //자바에서 서버로 데이터를 전달하여 클라이언트와 주고받기 위해 JSON 사용
                                            JSONObject jsonResponse = new JSONObject(response);    //특정 response를 실행 했을 때 결과가 담길 수 있도록 한다.
                                            boolean success = jsonResponse.getBoolean("success");
                                            if(success) { //데이터가 db에 저장됬을 때(회원가입 성공했을 때)

                                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ScanResult.this);
                                                builder.setMessage("등록 되었습니다!").setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.e("test","test5");
                                                        Intent intent = new Intent(ScanResult.this, MainActivity.class);
                                                        ScanResult.this.startActivity(intent);
                                                    }
                                                });

                                                android.support.v7.app.AlertDialog dialog =  builder.create();
                                                dialog.show();
                                            }else {
                                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ScanResult.this);
                                                builder.setMessage("등록에 실패했습니다.").setNegativeButton("다시시도",null).create().show();
                                            }
                                        }catch(JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                Log.e("test","test4");
                                ScanResultRequest scanResultRequest = new ScanResultRequest(foodName, foodDate, division, category, foodImage, Email, responseListener);  //StringRequest를 받은 객체를 부름
                                RequestQueue queue = Volley.newRequestQueue(ScanResult.this);
                                queue.add(scanResultRequest);

                            }
                        });

                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();

            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // QR코드/ 바코드를 스캔한 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // result.getFormatName() : 바코드 종류  result.getContents() : 바코드 값
        final String BARCODE = result.getContents().toString();  //바코드 결과값 변수에 저장

        //바코드 결과 값 전역변수로 선언
        GlobalApplication barcode = (GlobalApplication)getApplication();
        barcode.setBarcode(BARCODE);
        Log.e("barcode",barcode.getBarcode().toString());
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        protected void onPreExecute() {
            target = "http://qkfldk197.dothome.co.kr/barcode.php";
        }

        @Override
        protected String doInBackground(Void... voids) {  //데이터를 받아오는 부분

            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();  //넘어오는 결과값 저장
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        public void onPostExecute(String result) { //해당결과를 처리(결과값을 result 로 받아옴)

            ImageView foodImageView = (ImageView)findViewById(R.id.foodImageView);
            TextView bacordeNumtxt = (TextView) findViewById(R.id.scanResult);
            TextView foodNametxt = (TextView) findViewById(R.id.foodNameTxt);
            TextView foodDatetxt = (TextView) findViewById(R.id.foodDate);
            TextView foodCategorytxt = (TextView) findViewById(R.id.category);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String bacordNum, foodName, foodDate, foodCategory, foodImage;

                while (count < jsonArray.length()) {

                    GlobalApplication barcode = (GlobalApplication)getApplication();
                    Log.e("scanBarcode", barcode.getBarcode().toString());

                    JSONObject object = jsonArray.getJSONObject(count);
                    bacordNum = object.getString("barcodeNum");
                    foodName = object.getString("foodName");
                    foodDate = object.getString("foodDate");
                    foodCategory = object.getString("foodCategory");
                    foodImage = object.getString("foodImage");

                      if(bacordNum.equals(barcode.getBarcode())){

                        Log.e("dbBarcode", bacordNum);
                        bacordeNumtxt.setText(bacordNum);
                        foodNametxt.setText(foodName);
                        foodDatetxt.setText(foodDate);
                        foodCategorytxt.setText(foodCategory);

                        final String imgurl = foodImage;
                        /*final String imgurl ="/storage/emulated/0/blackJin/blackJin_152745_abcdef.png";*/
                        /*final String imgurl = "https://www.costco.co.kr/medias/sys_master/h67/h25/8810677174302.jpg";*/

                          GlobalApplication ImgUrl = (GlobalApplication)getApplication();
                          ImgUrl.setFoodIMG(imgurl);

                          Thread mThread = new Thread() {
                              @Override
                              public void run() {
                                  try {

                                      URL url = new URL(imgurl);

                                      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                      conn.setDoInput(true);
                                      conn.connect();

                                      InputStream is = conn.getInputStream();
                                      bitmap = BitmapFactory.decodeStream(is); //가져온 이미지 주소를 bitmap으로 변환

                                  } catch (MalformedURLException e) {
                                      e.printStackTrace();
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }
                              }
                          };

                          mThread.start();

                          try{
                              mThread.join();
                              foodImageView.setImageBitmap(bitmap);
                          }catch (InterruptedException e){
                              e.printStackTrace();
                          }

                        break;
                    }
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    }
