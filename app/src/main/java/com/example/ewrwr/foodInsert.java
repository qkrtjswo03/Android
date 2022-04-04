package com.example.ewrwr;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class foodInsert extends AppCompatActivity {


    private ArrayAdapter adpater; //category array를 연결하기 위한 어댑터
    private Spinner spinner;


    private String foodName;
    private String foodDate;
    private String division;
    private String category;
    private String foodImage;
    private String Email;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private Boolean isPermission = true;
    private static final String TAG = "blackjin";
    private Boolean isCamera = false;

    int mYear, mMonth, mDay, mHour, mMinute;  //날짜를 받을 변수
    TextView foodDateTxt;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_insert);

        //텍스트뷰 연결
        foodDateTxt = (TextView)findViewById(R.id.foodDateText);

       //현재 날짜와 시간을 가져오기위한 Calendar 인스턴스 선언

       Calendar cal = new GregorianCalendar();
       mYear = cal.get(Calendar.YEAR);
       mMonth = cal.get(Calendar.MONTH);
       mDay = cal.get(Calendar.DAY_OF_MONTH);
       mHour = cal.get(Calendar.HOUR_OF_DAY);
       mMinute = cal.get(Calendar.MINUTE);

       UpdateNow();//화면에 텍스트뷰에 업데이트 해줌.

        //category 스피너 연결
        spinner = (Spinner)findViewById(R.id.categorySpinner);
        adpater = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adpater);

        final EditText foondNameTxt = (EditText)findViewById(R.id.foodNameText);
        final EditText foodDateTxt = (EditText)findViewById(R.id.foodDateText);

        RadioGroup divisionGroup = (RadioGroup)findViewById(R.id.divisionGroup);
        int divisionGroupID = divisionGroup.getCheckedRadioButtonId();
        division = ((RadioButton)findViewById(divisionGroupID)).getText().toString();

        divisionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton divisionBtn = (RadioButton)findViewById(i);
                division = divisionBtn.getText().toString(); //체크된 라디오박스 변수에 저장
            }
        });


       //등록하기 버튼
       Button foonInsertBtn = (Button)findViewById(R.id.insertBtn);
       foonInsertBtn.setOnClickListener(new View.OnClickListener() {


           @Override
           public void onClick(View v) {

               GlobalApplication mail = (GlobalApplication)getApplication(); //전역변수 email 가져오기

               String foodName = foondNameTxt.getText().toString();
               String foodDate = foodDateTxt.getText().toString();
               String category = spinner.getSelectedItem().toString();
               String Email =  mail.getEmail().toString();
               Log.e("saveEmail2", mail.getEmail());

               Log.e("error1", foodName);
               Log.e("error2", foodDate);
               Log.e("error3", division);
               Log.e("error4", category);
               Log.e("error5", foodImage);
               Log.e("error6", Email);

               //빈칸이 존재할 때
               if(foodName.equals("") || foodDate.equals("") || category.equals("--선택하세요--") || division.equals("")){

                   Dialog dialog;
                   AlertDialog.Builder builder = new AlertDialog.Builder(foodInsert.this);
                   dialog = builder.setMessage("빈칸이 있습니다.")
                           .setNegativeButton("확인",null)
                           .create();
                   dialog.show();
                   return;
               };

               //빈칸이 존재하지 않으면 계속 진행 (디비연결부분)
               Response.Listener<String> responseListener = new Response.Listener<String>() { //특정 요청 이후에 리스너에서 원하는 결과값을 다룰수 있게 함
                   @Override
                   public void onResponse(String response) {
                       Log.e("test","test1");
                       try {
                           //자바에서 서버로 데이터를 전달하여 클라이언트와 주고받기 위해 JSON 사용
                           JSONObject jsonResponse = new JSONObject(response);    //특정 response를 실행 했을 때 결과가 담길 수 있도록 한다.
                           Log.e("test","test2");
                           boolean success = jsonResponse.getBoolean("success");
                           if(success) { //데이터가 db에 저장됬을 때(회원가입 성공했을 때)
                               Log.e("test","test3");

                               AlertDialog.Builder builder = new AlertDialog.Builder(foodInsert.this);
                               builder.setMessage("등록 되었습니다!").setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       Log.e("test","test5");
                                       Intent intent = new Intent(foodInsert.this, MainActivity.class);
                                       foodInsert.this.startActivity(intent);
                                   }
                               });

                               AlertDialog dialog =  builder.create();
                               dialog.show();
                           }else {
                               AlertDialog.Builder builder = new AlertDialog.Builder(foodInsert.this);
                               builder.setMessage("등록에 실패했습니다.").setNegativeButton("다시시도",null).create().show();
                           }
                       }catch(JSONException e){
                           e.printStackTrace();
                       }
                   }
               };
               Log.e("test","test4");
               foodInsertRequest foodInsertRequest = new foodInsertRequest(foodName, foodDate, division, category, foodImage, Email, responseListener);  //StringRequest를 받은 객체를 부름
               RequestQueue queue = Volley.newRequestQueue(foodInsert.this);
               queue.add(foodInsertRequest);

           }
       });

    /* ----------------------------------------------------------- 카메라 연동 부분 ------------------------------------------------------------------------------------*/

       tedPermission();

       ImageView foodImage = (ImageView)findViewById(R.id.foodImageView);
       Button cameraBtn = (Button)findViewById(R.id.cameraBtn);
       cameraBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(final View v) {
               AlertDialog.Builder alertBuilder = new AlertDialog.Builder(foodInsert.this);
               alertBuilder.setMessage("이미지 등록");
               alertBuilder.setPositiveButton("앨범선택", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int ciew) {
                       if(isPermission) goToAlbum();
                       else Toast.makeText(v.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                   }
               });
               alertBuilder.setNegativeButton("카메라 촬영", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if(isPermission)  takePhoto();
                       else Toast.makeText(v.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                   }
               });

               alertBuilder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Toast.makeText(foodInsert.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                   }
               });

               AlertDialog dialog = alertBuilder.create();
               dialog.show();
           }
       });



   }// oncreate

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void goToAlbum() {

        isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {

                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {

                Uri photoUri = data.getData();

                cropImage(photoUri);

                break;
            }
            case PICK_FROM_CAMERA: {

                Uri photoUri = Uri.fromFile(tempFile);

                cropImage(photoUri);

                break;
            }
            case Crop.REQUEST_CROP: {

                setImage();
            }
        }
    }

    private void cropImage(Uri photoUri) {

        Log.d(TAG, "tempFile : " + tempFile);

        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        Uri savingUri = Uri.fromFile(tempFile);

        Crop.of(photoUri, savingUri).asSquare().start(this);
    }

    private void setImage() {

        ImageView imageView = findViewById(R.id.foodImageView);

        ImageResize.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.e(TAG, "setImage : " + tempFile.getAbsolutePath());
            //tempFile.getAbsolutePath()값을 전역변수로 받는다.

        foodImage = tempFile.getAbsolutePath().toString();
        Log.e("imgURL", foodImage);

        imageView.setImageBitmap(originalBm);

    }

    private void takePhoto() {

        isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "{package name2}.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    /* --------------------------------------------- 카메라 끝 ------------------------------------------------------------------------------*/


/*

    protected  void onStop() {
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
*/

   public void mOnClick(View v){

       switch (v.getId()){
            // 날짜선택 버튼이 클릭되면
           case R.id.databtn:
             new DatePickerDialog(foodInsert.this, mDateSetListener, mYear,
                   mMonth, mDay).show();

           break;
       }
   }

   //날짜 대화상자 리스너 부분
    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    //사용자가 입력한 날짜 값을 가져온다
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    GlobalApplication day = (GlobalApplication)getApplication();
                    day.setdYear(mYear);
                    day.setdMonth(mMonth);
                    day.setdDay(mDay);

                    UpdateNow();
                }
            };

        //유통기한 텍스트뷰의 값을 업데이트
         void UpdateNow() {
            foodDateTxt.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));

    }

}//foodInsert class



