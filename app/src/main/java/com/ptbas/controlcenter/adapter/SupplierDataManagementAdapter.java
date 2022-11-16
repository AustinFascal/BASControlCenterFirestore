package com.ptbas.controlcenter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.model.SupplierModel;

import java.util.ArrayList;

public class SupplierDataManagementAdapter extends RecyclerView.Adapter<SupplierDataManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<SupplierModel> supplierModelArrayList;
    DialogInterface dialogInterface;

    public SupplierDataManagementAdapter(Context context, ArrayList<SupplierModel> supplierModelArrayList) {
        this.context = context;
        this.supplierModelArrayList = supplierModelArrayList;
    }

    @NonNull
    @Override
    public SupplierDataManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_supplier_data, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierDataManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(supplierModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return supplierModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlBtnDeleteItem, rlBtnOpenItemDetail;
        TextView tvStatus, tvSupplierName, tvBankNameAndAccountNumber, tvBankName, tvBankAccountOwnerName;
        CheckBox cbSelectItem;
        SwitchCompat statusSwitch;
        Button btn1, btn2;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rlBtnDeleteItem = itemView.findViewById(R.id.rlBtnDeleteItem);
            rlBtnOpenItemDetail = itemView.findViewById(R.id.rlBtnOpenItemDetail);

            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);
            tvBankNameAndAccountNumber = itemView.findViewById(R.id.tvBankNameAndAccountNumber);
            tvBankAccountOwnerName = itemView.findViewById(R.id.tvBankAccountOwnerName);

            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);

            btn1 = itemView.findViewById(R.id.btnDeleteItem);
            btn2 = itemView.findViewById(R.id.btnOpenItemDetail);

            statusSwitch = itemView.findViewById(R.id.statusSwitch);
        }

        public void viewBind(SupplierModel supplierModel) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");
            pd.setCancelable(false);

            dialogInterface = new DialogInterface();

            String supplierUID = supplierModel.getSupplierID();

            String supplierName = supplierModel.getSupplierName();

            String bankName = supplierModel.getBankName();
            String bankAccountNumber = supplierModel.getBankAccountNumber();

            String bankAccountOwnerName = supplierModel.getBankAccountOwnerName();

            String a = bankName.replace(" - ","-");
            int c = a.lastIndexOf('-');

            String bankNameAndAccountNumber = a.substring(0,c)+" - "+bankAccountNumber;

            tvSupplierName.setText(supplierName);
            tvBankNameAndAccountNumber.setText(bankNameAndAccountNumber);
            tvBankAccountOwnerName.setText(bankAccountOwnerName);

            Boolean supplierStatus = supplierModel.getSupplierStatus();

            if (supplierStatus){
                statusSwitch.setChecked(true);
                tvStatus.setText("Aktif");
            } else{
                statusSwitch.setChecked(false);
                tvStatus.setText("Tidak Aktif");
            }


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference refSupplier = db.collection("SupplierData");

            statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    pd.show();
                    if (statusSwitch.isChecked()){
                        refSupplier.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(supplierUID)){
                                        db.collection("SupplierData").document(supplierUID).update("supplierStatus", true);
                                        pd.dismiss();
                                    }
                                }
                            }
                        });
                    } else {
                        refSupplier.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(supplierUID)){
                                        db.collection("SupplierData").document(supplierUID).update("supplierStatus", false);
                                        pd.dismiss();
                                    }
                                }
                            }
                        });
                    }
                }
            });

            btn1.setOnClickListener(view ->
                    dialogInterface.deleteSupplierDataConfirmation(context, supplierUID));

            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
