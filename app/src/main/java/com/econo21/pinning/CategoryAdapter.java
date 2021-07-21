package com.econo21.pinning;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    public Context context;
    TextView textView;
    ArrayList<Category> items;
    RecyclerView recyclerView;
    private String dbCategory;

    public CategoryAdapter(ArrayList<Category> items, Context context, TextView textView, RecyclerView recyclerView){
        this.context = context;
        this.items = items;
        this.textView = textView;
        this.recyclerView = recyclerView;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_category,viewGroup,false);
        CategoryViewHolder viewHolder = new CategoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int i){
        final Category category = items.get(i);
        Log.d("@@@", "CategoryAdapter: category" + category.getName());
        holder.categoryName.setText(category.getName());
        switch (category.getColor()){
            case "black":
                holder.categoryImage.setImageResource(R.drawable.pin_black_64);
                break;
            case "gray":
                holder.categoryImage.setImageResource(R.drawable.pin_gray_64);
                break;
            case "492f10":
                holder.categoryImage.setImageResource(R.drawable.pin_492f10);
                break;
            case "595b83":
                holder.categoryImage.setImageResource(R.drawable.pin_595b83);
                break;
            case "333456":
                holder.categoryImage.setImageResource(R.drawable.pin_333456);
                break;
            case "a7c5eb":
                holder.categoryImage.setImageResource(R.drawable.pin_a7c5eb);
                break;
            case "DF5E5E":
                holder.categoryImage.setImageResource(R.drawable.pin_df5e5e);
                break;
            case "E98580":
                holder.categoryImage.setImageResource(R.drawable.pin_e98580);
                break;
            case "FDD2BF":
                holder.categoryImage.setImageResource(R.drawable.pin_fdd2bf);
                break;
            default:
                holder.categoryImage.setImageResource(R.drawable.pin_blue_64);
                break;
        }
        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(category.getName());
                recyclerView.setVisibility(View.GONE);
                dbCategory = category.getColor();
            }
        });
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public String getDbCategory() {
        return dbCategory;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        ImageView categoryImage;
        TextView categoryName;

        public CategoryViewHolder(@NonNull final View itemView){
            super(itemView);
            categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            categoryName = (TextView)itemView.findViewById(R.id.category_text);
        }
    }
}
