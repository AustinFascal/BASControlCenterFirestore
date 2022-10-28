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
import com.ptbas.controlcenter.create.AddBankAccountActivity;
import com.ptbas.controlcenter.create.AddSupplierActivity;
import com.ptbas.controlcenter.management.ManageCustomerActivity;
import com.ptbas.controlcenter.management.ManageGoodIssueActivity;
import com.ptbas.controlcenter.management.ManageCashOutActivity;
import com.ptbas.controlcenter.management.ManageInvoiceActivity;
import com.ptbas.controlcenter.management.ManageProductDataActivity;
import com.ptbas.controlcenter.management.ManageRecapGoodIssueActivity;
import com.ptbas.controlcenter.management.ManageReceivedOrderActivity;
import com.ptbas.controlcenter.management.ManageSupplierActivity;
import com.ptbas.controlcenter.management.ManageUserActivity;
import com.ptbas.controlcenter.management.ManageVehicleActivity;
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
                    Intent intent2 = new Intent(context, ManageUserActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nArmada")){
                    Intent intent2 = new Intent(context, ManageVehicleActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nReceived Order")){
                    Intent intent2 = new Intent(context, ManageReceivedOrderActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nGood Issue")){
                    Intent intent2 = new Intent(context, ManageGoodIssueActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }

                if (holder.tv1.getText().toString().equals("Data Rekap\nGood Issue")){
                    Intent intent2 = new Intent(context, ManageRecapGoodIssueActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nCustomer")){
                    Intent intent2 = new Intent(context, ManageCustomerActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nCash Out")){
                    Intent intent2 = new Intent(context, ManageCashOutActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Manajemen\nMaterial")){
                    Intent intent2 = new Intent(context, ManageProductDataActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }

                if (holder.tv1.getText().toString().equals("Data\nInvoice")){
                    Intent intent2 = new Intent(context, ManageInvoiceActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nRekening Bank")){
                    Intent intent2 = new Intent(context, AddBankAccountActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
                if (holder.tv1.getText().toString().equals("Data\nSupplier")){
                    Intent intent2 = new Intent(context, ManageSupplierActivity.class);
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
