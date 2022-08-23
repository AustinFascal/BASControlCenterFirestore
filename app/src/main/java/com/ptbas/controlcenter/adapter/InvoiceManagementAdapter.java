package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.InvoiceModel;

import java.util.ArrayList;

public class InvoiceManagementAdapter extends RecyclerView.Adapter<InvoiceManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<InvoiceModel> invoiceModelArrayList;
    DialogInterface dialogInterface;

    public InvoiceManagementAdapter(Context context, ArrayList<InvoiceModel> invoiceModelArrayList) {
        this.context = context;
        this.invoiceModelArrayList = invoiceModelArrayList;
    }

    @NonNull
    @Override
    public InvoiceManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_invoice, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(invoiceModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return invoiceModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llStatusApproved;
        TextView tvInvDateCreated, tvInvUID, tvPoCustNumber;
        Button btnDeleteInv, btnApproveInv;
        RelativeLayout rlOpenInvDetail;
        ImageView ivShowDetail;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShowDetail = itemView.findViewById(R.id.iv_show_detail);
            rlOpenInvDetail = itemView.findViewById(R.id.open_detail);
            llStatusApproved = itemView.findViewById(R.id.ll_status_approved);
            tvInvDateCreated = itemView.findViewById(R.id.tv_inv_date_created);
            tvInvUID = itemView.findViewById(R.id.tv_inv_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            btnDeleteInv = itemView.findViewById(R.id.btn_delete_inv);
            btnApproveInv = itemView.findViewById(R.id.btn_approve_inv);
        }

        public void viewBind(InvoiceModel invoiceModel) {
            dialogInterface = new DialogInterface();
            String invUID = invoiceModel.getInvUID();
            String invPoUID = "PO: "+invoiceModel.getInvPoUID();
            String invDateCreated = invoiceModel.getInvDateCreated();
            //String invCustName = invoiceModel.getInvCustName();
            boolean invStatus = invoiceModel.getInvStatus();

            tvInvUID.setText(invUID);
            tvInvDateCreated.setText(invDateCreated);
            tvPoCustNumber.setText(invPoUID);
            if (invStatus){
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveInv.setVisibility(View.GONE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                btnApproveInv.setVisibility(View.VISIBLE);
            }
            /*if (tvPoCustNumber.getText().toString().equals("PO: -")){
                tvPoCustNumber.setVisibility(View.GONE);
                llStatusPOAvailable.setVisibility(View.VISIBLE);
            } else {
                tvPoCustNumber.setVisibility(View.VISIBLE);
                llStatusPOAvailable.setVisibility(View.GONE);
            }

            /*rlOpenInvDetail.setOnClickListener(view -> {
                String roUID1 =receivedOrderModel.getRoUID();
                Intent i = new Intent(context, UpdateGoodIssueActivity.class);
                i.putExtra("key", roUID1);
                context.startActivity(i);
            });*/

//            btnApproveInv.setOnClickListener(view -> {
//                if (tvPoCustNumber.getText().toString().equals("PO: -")){
//                    dialogInterface.noRoPoNumberInformation(context, invoiceModel.getRoDocumentID());
//                } else {
//                    dialogInterface.approveRoConfirmation(context, invoiceModel.getRoDocumentID());
//                }
//            });

//            btnDeleteInv.setOnClickListener(view ->
//                    dialogInterface.deleteRoConfirmation(context, invoiceModel.getRoDocumentID()));
        }
    }
}
