package com.econo21.pinning;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UriImageAdapter extends RecyclerView.Adapter<UriImageAdapter.ItemViewHolder> {
    public ArrayList<Uri> imgList;
    public Context mContext;

    public UriImageAdapter(ArrayList<Uri> imgList, Context mContext){
        this.imgList = imgList;
        this.mContext = mContext;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,  int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_uri,parent,false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position){
        //holder.imageView.setImageURI(imgList.get(position));
        Glide.with(holder.itemView)
                .load(imgList.get(position))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount(){
        return imgList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public ItemViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}
