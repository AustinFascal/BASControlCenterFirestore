package com.ptbas.controlcenter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.update.UpdateVehicleData;

import java.util.ArrayList;

public class VehicleDataManagementAdapter extends RecyclerView.Adapter<VehicleDataManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<VehicleModel> vehicleModelArrayList;
    DialogInterface dialogInterface;

    public VehicleDataManagementAdapter(Context context, ArrayList<VehicleModel> vehicleModelArrayList) {
        this.context = context;
        this.vehicleModelArrayList = vehicleModelArrayList;
    }

    @NonNull
    @Override
    public VehicleDataManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_vehicle_data, parent, false);
        
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleDataManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(vehicleModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return vehicleModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout btnDeleteWrap;
        TextView tvVhlUID, tvVhlDetails, tvStatus;
        ImageView ivShowDetail;
        SwitchCompat statusSwitch;
        Button btn1;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeleteWrap = itemView.findViewById(R.id.btnDeleteWrap);
            tvVhlUID = itemView.findViewById(R.id.tvVhlUID);
            tvVhlDetails = itemView.findViewById(R.id.tvVhlDetails);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivShowDetail = itemView.findViewById(R.id.ivShowDetail);
            statusSwitch = itemView.findViewById(R.id.statusSwitch);
            btn1 = itemView.findViewById(R.id.btnDeleteItem);
        }

        public void viewBind(VehicleModel vehicleModel) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");
            pd.setCancelable(false);

            dialogInterface = new DialogInterface();

            String vhlUID = vehicleModel.getVhlUID();
            String vhlDetails = "P: "+vehicleModel.getVhlLength()+" cm | L: "+vehicleModel.getVhlWidth()+" cm | T: "+vehicleModel.getVhlHeight()+" cm";
            Boolean vhlStatus = vehicleModel.getVhlStatus();

            tvVhlUID.setText(vhlUID);
            tvVhlDetails.setText(vhlDetails);

            if (vhlStatus){
                statusSwitch.setChecked(true);
                tvStatus.setText("Aktif");
            } else{
                statusSwitch.setChecked(false);
                tvStatus.setText("Tidak Aktif");
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                pd.show();
                if (statusSwitch.isChecked()) {
                    databaseReference.child("VehicleData").child(vhlUID).child("vhlStatus").setValue(true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            pd.dismiss();
                        }
                    });
                } else{
                    databaseReference.child("VehicleData").child(vhlUID).child("vhlStatus").setValue(false).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            pd.dismiss();
                        }
                    });
                }

            });

            ivShowDetail.setOnClickListener(view -> {
                Intent i = new Intent(context, UpdateVehicleData.class);
                i.putExtra("key", vhlUID);
                context.startActivity(i);
            });

            btn1.setOnClickListener(view ->
                    dialogInterface.deleteVehicleDataConfirmation(context, vhlUID));
        }
    }
}
