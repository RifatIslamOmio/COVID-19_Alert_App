package com.example.covid_19alertapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.models.MatchedLocation;


import java.util.ArrayList;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.MyViewHolder> {

    Context context;
    ArrayList<MatchedLocation> locationsList;
    public LocationListAdapter(Context context,ArrayList<MatchedLocation> locationsList)
    {
        this.context = context;
        this.locationsList = locationsList;
    }
    @NonNull
    @Override
    public LocationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.locationlist_view, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LocationListAdapter.MyViewHolder holder, final int position) {

        holder.location.setText(locationsList.get(position).getAddress());
        holder.dateTime.setText(locationsList.get(position).getMeaningfulDateTime());
        holder.count.setText("Infected: "+locationsList.get(position).getCount()+"");

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SHOW IN MAP FUNCTION

                Intent intent = new Intent(context, LocationShowMapsActivity.class);
                intent.putExtra("maps-latitude", locationsList.get(position).getLatitude());
                intent.putExtra("maps-longitude", locationsList.get(position).getLongitude());
                context.startActivity(intent);
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog Dialog = new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you want to remove this record?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //FUNCTION TO DELETE RECORD/LOCATION



                                dialog.dismiss();
                                Toast.makeText(context,"Record Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();




                return false; //DO NOT REMOVE THIS
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView count,dateTime,location;
        LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.locationList_Counter);
            dateTime = itemView.findViewById(R.id.locationList_dateText);
            location = itemView.findViewById(R.id.locationList_locationText);
            linearLayout = itemView.findViewById(R.id.locationList_linearLayout);
        }
    }


}
