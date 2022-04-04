package com.example.ewrwr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;
    private FoodListAdapter adapter;
    private List<Food> foodList;

    private int tYear, tMonth, tDay; //오늘 날짜를 받을 변수
    private  long d,t,r;
    private int resultNumber=0;
    static final int DATE_DIALOG_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("냉장고");


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ScanResult.class);
                startActivity(intent);



                    /**********권한 요청************/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        /**
                         * 사용자 단말기의 권한 중 "카메라" 권한이 허용되어 있는지 확인한다.
                         *  Android는 C언어 기반으로 만들어졌기 때문에 Boolean 타입보다 Int 타입을 사용한다.
                         */
                        int permissionResult = checkSelfPermission(Manifest.permission.CAMERA);


                        /** * 패키지는 안드로이드 어플리케이션의 아이디이다. *
                         *  현재 어플리케이션이 카메라에 대해 거부되어있는지 확인한다. */
                        if (permissionResult == PackageManager.PERMISSION_DENIED) {


                            /** * 사용자가 CALL_PHONE 권한을 거부한 적이 있는지 확인한다. *
                             * 거부한적이 있으면 True를 리턴하고 *
                             * 거부한적이 없으면 False를 리턴한다. */
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog.setTitle("권한이 필요합니다.").setMessage("이 기능을 사용하기 위해서는 단말기의 \"카메라\" 권한이 필요합니다. 계속 하시겠습니까?")
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                /** * 새로운 인스턴스(onClickListener)를 생성했기 때문에 *
                                                 * 버전체크를 다시 해준다. */
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    // CALL_PHONE 권한을 Android OS에 요청한다.
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
                                                }
                                            }
                                        })
                                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(MainActivity.this, "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                            }
                                        }).create().show();
                            }
                            // 최초로 권한을 요청할 때
                            else {
                                // CALL_PHONE 권한을 Android OS에 요청한다.
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1000);
                            }
                        }
                        // CALL_PHONE의 권한이 있을 때
                        else {

                        }
                    }
                    /************권한요청 끝**************/


                }

                public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    if (requestCode == 1000) {
                        // 요청한 권한을 사용자가 "허용" 했다면...
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // 이곳에 허용했을때 실행할 코드를 넣는다
                            // 근데 난 안넣음

                        } else {
                            // 거부했을때 띄워줄
                            Toast.makeText(MainActivity.this, "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }



                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        /* ----------------------------------------------- 리스트뷰 --------------------------------------*/


        listView = (ListView)findViewById(R.id.lvfood);
        foodList = new ArrayList<Food>();

        /*
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(imgurl, options); //bitmap파일로 변경

        foodList.add(new Food(originalBm, "ㅎㅇㅎㅇ", "date", "day"));

        adapter = new FoodListAdapter(getApplicationContext(), foodList);
        listView.setAdapter(adapter);*/


        new BackgroundTask().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food item = (Food)parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), FoodInfo.class);






               /* intent.putExtra("foodImage",item.getFoodImage());
                intent.putExtra("foodName", item.getFoodName());
                intent.putExtra("foodDate",item.getFoodDate());
                startActivity(intent);
                finish();*/

            }
        });

        /* ------------------------------------------------------------------------------------------- */
    }//oncreate


    //bitmap을 string으로 형 변환
    public String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }



    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target;

      @Override
        protected  void onPreExecute(){
            target="http://qkfldk197.dothome.co.kr/food.php";
        }
        @Override
        protected  String doInBackground(Void... voids){

            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }
        @Override
        public void onPostExecute(String result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String foodName, foodDate, foodImage, foodDday;

                while(count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);
                    foodImage = object.getString("foodImage");
                    foodName = object.getString("foodName");
                    foodDate = object.getString("foodDate");

                    int dYear = Integer.parseInt(foodDate.substring(0, 4));
                    int dMonth = Integer.parseInt(foodDate.substring(5, 6));
                    int dDay = Integer.parseInt(foodDate.substring(7, 9));

                    Calendar calendar = Calendar.getInstance(); //현재 날짜 불러옴
                    tYear = calendar.get(Calendar.YEAR);
                    tMonth = calendar.get(Calendar.MONTH);
                    tDay = calendar.get(Calendar.DAY_OF_MONTH);

                    Calendar dCalendear = Calendar.getInstance();
                    dCalendear.set(dYear,dMonth-1,dDay);

                    t=calendar.getTimeInMillis(); //현재 날짜를 밀리타임으로 바꿈
                    d=dCalendear.getTimeInMillis(); //디데이 날짜를 밀리타임으로 바꿈
                    r=(d-t)/(24*60*60*1000); //디데이 날짜에서 오늘 날짜를 뺀 값을 '일' 단위로 바꿈

                    if(resultNumber>=0){
                        foodDday = String.format("D-%d",resultNumber);

                        resultNumber=(int)r+1;
                    }else{
                        int absR = Math.abs(resultNumber);
                        foodDday = String.format("D+%d",absR);
                    }

                    Log.e("????",foodName);
                    Log.e("????",foodDate);
                    Log.e("????",foodImage);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap originalBm = BitmapFactory.decodeFile(foodImage, options); //bitmap파일로 변경


                    Food food = new Food(originalBm,foodName,foodDate,foodDday);
                    foodList.add(food);
                    adapter = new FoodListAdapter(getApplicationContext(), foodList);
                    listView.setAdapter(adapter);

                    Log.e("test", String.valueOf(food));

                    count ++;

                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        TextView tvNickname = findViewById(R.id.userName);
        TextView tvEmail = findViewById(R.id.userMail);
        ImageView ivProfile = findViewById(R.id.userProfile);

        Intent in = getIntent();
        String nick = in.getStringExtra("name");
        String mail = in.getStringExtra("mail");
        String profile = in.getStringExtra("image");

        tvNickname.setText(nick);
        tvEmail.setText(mail);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), foodInsert.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Intent intent = new Intent(MainActivity.this, KakaoLoginMain.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
