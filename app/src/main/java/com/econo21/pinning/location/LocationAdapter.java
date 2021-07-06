package com.econo21.pinning.location;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.econo21.pinning.AddActivity;
import com.econo21.pinning.LocationActivity;
import com.econo21.pinning.R;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    Context context;
    ArrayList<Document> items;
    EditText editText;
    RecyclerView recyclerView;
    String x;
    String y;

    public LocationAdapter(ArrayList<Document> items, Context context, EditText editText, RecyclerView recyclerView) {
        this.context = context;
        this.items = items;
        this.editText = editText;
        this.recyclerView = recyclerView;

    }

    public String getX(){
        return x;
    }

    public String getY(){
        return y;
    }

    public String getAddress(){
        return editText.getText().toString();
    }
    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(Document item) {
        items.add(item);
    }


    public void clear() {
        items.clear();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(items.get(position).getId());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_location, viewGroup, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int i) {
        final Document model = items.get(i);
        holder.placeNameText.setText(model.getPlaceName());
        holder.addressText.setText(model.getAddressName());
        holder.placeNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText(model.getPlaceName());
                recyclerView.setVisibility(View.GONE);
                x = model.getX();
                y = model.getY();
                Log.d("@@@","x: " + x + " y: " + y);
            }
        });
        /*
        holder.itemView.setTag(i);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = model.getX();
                y = model.getY();
                Log.d("@@@","x: " + x + " y: " + y);
            }
        });
         */
    }


    public class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameText;
        TextView addressText;

        public LocationViewHolder(@NonNull final View itemView) {
            super(itemView);
            placeNameText = itemView.findViewById(R.id.ltem_location_tv_placename);
            addressText = itemView.findViewById(R.id.ltem_location_tv_address);
        }
    }
}