package com.ptbas.controlcenter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.CustModel;

import java.util.ArrayList;

public class CustDataAdapter extends RecyclerView.Adapter<CustDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<CustModel> custModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;

    public CustDataAdapter(Context context, ArrayList<CustModel> custModelArrayList) {
        this.context = context;
        this.custModelArrayList = custModelArrayList;
    }

    @NonNull
    @Override
    public CustDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_customer_data, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustDataAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(custModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return custModelArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        SwitchCompat statusSwitch;
        TextView tvStatus, tvCustUID, tvCustName, tvCustPhone;
        Button btn1;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            statusSwitch = itemView.findViewById(R.id.statusSwitch);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCustUID = itemView.findViewById(R.id.tvCustUID);
            tvCustName = itemView.findViewById(R.id.tvCustName);
            tvCustPhone = itemView.findViewById(R.id.tvCustPhone);
            btn1 = itemView.findViewById(R.id.btnDeleteItem);
        }

        public void viewBind(CustModel custModel) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");


            dialogInterfaceUtils = new DialogInterfaceUtils();

            String custDocumentId = custModel.getCustDocumentID();
            boolean status = custModel.getCustStatus();
            String custUID = custModel.getCustUID();
            String custName = custModel.getCustName();
            String custPhone = custModel.getCustPhone();

            if (status){
                statusSwitch.setChecked(true);
                tvStatus.setText("Aktif");
            } else{
                statusSwitch.setChecked(false);
                tvStatus.setText("Tidak Aktif");
            }

            tvCustUID.setText(custUID);
            tvCustName.setText(custName);
            if (custPhone.isEmpty()){
                tvCustPhone.setText("No. Telp: -");
            }else {
                tvCustPhone.setText("No. Telp: "+custPhone);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference refCust = db.collection("CustomerData");

            statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    pd.show();
                    if (statusSwitch.isChecked()){
                        refCust.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(custDocumentId)){
                                        db.collection("CustomerData").document(custDocumentId).update("custStatus", true);
                                        pd.dismiss();
                                    }
                                }
                            }
                        });
                    } else {
                        refCust.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(custDocumentId)){
                                        db.collection("CustomerData").document(custDocumentId).update("custStatus", false);
                                        pd.dismiss();
                                    }
                                }
                            }
                        });
                    }
                }
            });

            btn1.setOnClickListener(view -> dialogInterfaceUtils.deleteCustConfirmation(context, custDocumentId));
        }
    }
}
