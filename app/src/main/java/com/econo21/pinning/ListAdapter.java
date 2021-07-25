package com.econo21.pinning;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> implements OnListItemClickListener {
    public Context context;
    ArrayList<Pin> items;
    OnListItemClickListener listener;



    public ListAdapter(ArrayList<Pin> items, Context context){
        this.context = context;
        this.items = items;
    }

    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_list,viewGroup,false);
        ListViewHolder viewHolder = new ListViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int i){
        final Pin pin = items.get(i);
        if(pin.getPhoto() != null){
            Glide.with(holder.imageView).load(pin.getPhoto().get(0)).into(holder.imageView);
        }
        holder.list_name.setText(pin.getPin_name());
        holder.list_category.setText(pin.getCategory());

    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public void setOnItemClicklistener(OnListItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ListViewHolder holder, View view, int position) {
        if(listener != null){
            listener.onItemClick(holder,view,position);
        }
    }



    public class ListViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView list_name;
        TextView list_category;

        public ListViewHolder(@NonNull final View itemView, final OnListItemClickListener listener){
            super(itemView);
            imageView =(ImageView) itemView.findViewById(R.id.list_image);
            list_name =(TextView) itemView.findViewById(R.id.list_name);
            list_category=(TextView) itemView.findViewById(R.id.list_category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ListViewHolder.this, v, position);
                    }
                }
            });
        }
    }

    public Pin getItem(int position){
        return items.get(position);
    }
}
