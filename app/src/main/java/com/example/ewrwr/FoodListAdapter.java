package com.example.ewrwr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foodList;

    public FoodListAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }
    @Override
    public int getCount() { return foodList.size(); }
    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, R.layout.foodlist, null);

        ImageView foodImage = (ImageView)v.findViewById(R.id.foodImageView);
        TextView foodName = (TextView)v.findViewById(R.id.foodNameTxt);
        TextView foodDate = (TextView)v.findViewById(R.id.foodDateTxt);
        TextView foodDday = (TextView)v.findViewById(R.id.foodDdayTxt);

        foodImage.setImageBitmap(foodList.get(position).getFoodImage());
        foodName.setText(foodList.get(position).getFoodName());
        foodDate.setText(foodList.get(position).getFoodDate());
        foodDday.setText(foodList.get(position).getFoodDday());

        v.setTag(foodList.get(position).getFoodImage());

        return v;

        /*Context context = parent.getContext();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.foodlist, parent, false);
        }

        ImageView foodImage = (ImageView)convertView.findViewById(R.id.foodImageView);
        TextView foodNameText = (TextView)convertView.findViewById(R.id.foodNameTxt);
        TextView foodDateText = (TextView)convertView.findViewById(R.id.foodDateTxt);
        TextView foodDdayText = (TextView)convertView.findViewById(R.id.foodDdayTxt);


        Food item = (Food)foodList.get(position);

        foodImage.setImageBitmap(item.getFoodImage());
        foodImage.setImageBitmap(item.getFoodImage());
        foodNameText.setText(item.getFoodName());
        foodDateText.setText(item.getFoodDate());
        foodDdayText.setText(item.getFoodDday());


        return convertView;*/
    }

  /*  public void addItem(String foodImage, String foodName, String foodDate, String foodDday){

        Food item = new Food();

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(foodImage, options);

        item.setFoodImage(originalBm);
        item.setFoodName(foodName);
        item.setFoodDate(foodName);
        item.setFoodDday(foodDday);

        foodList.add(item);
    }*/

}
