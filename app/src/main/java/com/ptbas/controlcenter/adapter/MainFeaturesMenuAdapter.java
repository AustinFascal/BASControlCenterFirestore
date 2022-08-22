package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.create.AddCustomerActivity;
import com.ptbas.controlcenter.management.GoodIssueManagementActivity;
import com.ptbas.controlcenter.management.InvoiceManagementActivity;
import com.ptbas.controlcenter.management.ReceivedOrderManagementActivity;
import com.ptbas.controlcenter.management.UserManagementActivity;
import com.ptbas.controlcenter.management.VehicleManagementActivity;
import com.ptbas.controlcenter.model.MainFeatureModel;
import com.ptbas.controlcenter.viewholder.MainFeatureViewHolder;

import java.util.ArrayList;

public class MainFeaturesMenuAdapter extends RecyclerView.Adapter<MainFeatureViewHolder> {

    ArrayList<MainFeatureModel> mainFeatureData;
    Context context;

    public MainFeaturesMenuAdapter(ArrayList<MainFeatureModel> mainFeatureData, Context context) {
        this.mainFeatureData = mainFeatureData;
        this.context = context;
    }

    @NonNull
    @Override
    public MainFeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_main_feature, parent, false);
        return new MainFeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainFeatureViewHolder holder, int position) {
        int pos = position;

        holder.img.setImageResource(mainFeatureData.get(position).getImgName());
        holder.tv1.setText(mainFeatureData.get(position).getHeader());
        //holder.tv2.setText(mainFeatureData.get(position).getDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos == 0){
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();

                    /*Intent intent1 = new Intent(context, UserManagementActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);*/
                } else if (pos == 1){
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();

                    /*Intent intent2 = new Intent(context, VehicleManagementActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);*/
                } else if (pos == 2){
                    Intent intent2 = new Intent(context, ReceivedOrderManagementActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                } else if (pos == 3){
                    Intent intent2 = new Intent(context, GoodIssueManagementActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                } else if (pos == 4){
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();

                    /*Intent intent2 = new Intent(context, AddCustomerActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);*/
                } else if (pos == 5){
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();

                    /*Intent intent2 = new Intent(context, InvoiceManagementActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);*/
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainFeatureData.size();
    }
}
