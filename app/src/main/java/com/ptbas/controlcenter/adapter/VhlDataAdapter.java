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
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.VhlModel;
import com.ptbas.controlcenter.update.UpdtVhlActivity;

import java.util.ArrayList;

public class VhlDataAdapter extends RecyclerView.Adapter<VhlDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<VhlModel> vhlModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;

    public VhlDataAdapter(Context context, ArrayList<VhlModel> vhlModelArrayList) {
        this.context = context;
        this.vhlModelArrayList = vhlModelArrayList;
    }

    @NonNull
    @Override
    public VhlDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_vehicle_data, parent, false);
        
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VhlDataAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(vhlModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return vhlModelArrayList.size();
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

        public void viewBind(VhlModel vhlModel) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");
            pd.setCancelable(false);

            dialogInterfaceUtils = new DialogInterfaceUtils();

            String vhlUID = vhlModel.getVhlUID();
            String vhlDetails = "P: "+ vhlModel.getVhlLength()+" cm | L: "+ vhlModel.getVhlWidth()+" cm | T: "+ vhlModel.getVhlHeight()+" cm";
            Boolean vhlStatus = vhlModel.getVhlStatus();

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
                Intent i = new Intent(context, UpdtVhlActivity.class);
                i.putExtra("key", vhlUID);
                context.startActivity(i);
            });

            btn1.setOnClickListener(view ->
                    dialogInterfaceUtils.deleteVehicleDataConfirmation(context, vhlUID));
        }
    }
}
