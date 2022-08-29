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

import com.ptbas.controlcenter.helper.DialogInterface;
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
        LinearLayout llStatusPaid, llStatusUnpaid;
        TextView tvInvDateCreated, tvInvUID, tvPoCustNumber, tvPoCustName;
        Button btnDeleteInv, btnApproveInv;
        RelativeLayout rlOpenInvDetail, wrapBtnApprove;
        ImageView ivShowDetail;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShowDetail = itemView.findViewById(R.id.iv_show_detail);
            rlOpenInvDetail = itemView.findViewById(R.id.open_detail);
            llStatusPaid = itemView.findViewById(R.id.ll_status_paid);
            llStatusUnpaid = itemView.findViewById(R.id.ll_status_unpaid);
            tvInvDateCreated = itemView.findViewById(R.id.tv_inv_date_created);
            tvInvUID = itemView.findViewById(R.id.tv_inv_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            tvPoCustName = itemView.findViewById(R.id.tv_po_cust_name);
            btnDeleteInv = itemView.findViewById(R.id.btn_delete_inv);
            btnApproveInv = itemView.findViewById(R.id.btn_approve_inv);
            wrapBtnApprove = itemView.findViewById(R.id.wrapBtnApprove);
        }

        public void viewBind(InvoiceModel invoiceModel) {
            dialogInterface = new DialogInterface();
            String invUID = invoiceModel.getInvUID();
            String invDocumentID = invoiceModel.getInvDocumentID();
            String invPoUID = "PO: "+invoiceModel.getInvPoUID();
            String invPoCustName = invoiceModel.getInvCustName();
            String invDateCreated = invoiceModel.getInvDateCreated();
            String invPoType = invoiceModel.getInvPoType();
            boolean invStatus = invoiceModel.getInvStatus();

            tvInvUID.setText(invUID);
            tvInvDateCreated.setText(invDateCreated);
            tvPoCustNumber.setText(invPoUID.concat(" | ").concat(invPoType));
            tvPoCustName.setText(invPoCustName);
            if (invStatus){
                llStatusPaid.setVisibility(View.VISIBLE);
                llStatusUnpaid.setVisibility(View.GONE);
                wrapBtnApprove.setVisibility(View.GONE);
            } else {
                llStatusPaid.setVisibility(View.GONE);
                llStatusUnpaid.setVisibility(View.VISIBLE);
                wrapBtnApprove.setVisibility(View.VISIBLE);
            }

            btnApproveInv.setOnClickListener(view ->
                    dialogInterface.approveInvConfirmation(context, invDocumentID));

            btnDeleteInv.setOnClickListener(view ->
                    dialogInterface.deleteInvConfirmation(context, invDocumentID));
        }
    }
}
