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
import com.ptbas.controlcenter.create.AddAioReportActivity;
import com.ptbas.controlcenter.create.AddBankAccActivity;
import com.ptbas.controlcenter.management.CustDataActivity;
import com.ptbas.controlcenter.management.GiDataActivity;
import com.ptbas.controlcenter.management.CoDataActivity;
import com.ptbas.controlcenter.management.InvDataActivity;
import com.ptbas.controlcenter.management.ProdDataActivity;
import com.ptbas.controlcenter.management.RcpDataActivity;
import com.ptbas.controlcenter.management.RoDataActivity;
import com.ptbas.controlcenter.management.SplrDataActivity;
import com.ptbas.controlcenter.management.UserDataActivity;
import com.ptbas.controlcenter.management.VhlDataActivity;
import com.ptbas.controlcenter.model.MainFeatureModel;
import com.ptbas.controlcenter.viewholder.MainFeatureViewHolder;

import java.util.ArrayList;

public class MainFeaturesMenuAdapter extends RecyclerView.Adapter<MainFeatureViewHolder> {

    ArrayList<MainFeatureModel> mainFeatureData;
    Context context;
    int viewTypeVal;

    public MainFeaturesMenuAdapter(ArrayList<MainFeatureModel> mainFeatureData, Context context, int viewTypeVal) {
        this.mainFeatureData = mainFeatureData;
        this.context = context;
        this.viewTypeVal = viewTypeVal;
    }

    @NonNull
    @Override
    public MainFeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewTypeVal == 0){
            view = inflater.inflate(R.layout.item_layout_main_feature, parent, false);
        }else {
            view = inflater.inflate(R.layout.item_layout_main_feature_2, parent, false);
        }
        return new MainFeatureViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MainFeatureViewHolder holder, int position) {
        //int pos = position;

        holder.img.setImageResource(mainFeatureData.get(position).getImgName());
        holder.tv1.setText(mainFeatureData.get(position).getHeader());
        //holder.tv2.setText(mainFeatureData.get(position).getDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tv1.getText().toString().equals("Data\nPengguna")){
                    Intent intent2 = new Intent(context, UserDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nArmada")){
                    Intent intent2 = new Intent(context, VhlDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nReceived Order")){
                    Intent intent2 = new Intent(context, RoDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nGood Issue")){
                    Intent intent2 = new Intent(context, GiDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data Laporan\nAll-in-One")){
                    Intent intent2 = new Intent(context, AddAioReportActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }


                if (holder.tv1.getText().toString().equals("Data Rekap\nGood Issue")){
                    Intent intent2 = new Intent(context, RcpDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nCustomer")){
                    Intent intent2 = new Intent(context, CustDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nCash Out")){
                    Intent intent2 = new Intent(context, CoDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Manajemen\nMaterial")){
                    Intent intent2 = new Intent(context, ProdDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }

                if (holder.tv1.getText().toString().equals("Data\nInvoice")){
                    Intent intent2 = new Intent(context, InvDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nRekening Bank")){
                    Intent intent2 = new Intent(context, AddBankAccActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nSupplier")){
                    Intent intent2 = new Intent(context, SplrDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }

                if (holder.tv1.getText().toString().equals("Data\nBank")){
                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainFeatureData.size();
    }
}
