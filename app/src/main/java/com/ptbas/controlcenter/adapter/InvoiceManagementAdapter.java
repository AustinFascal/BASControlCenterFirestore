package com.ptbas.controlcenter.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.InvoiceModel;
import com.ptbas.controlcenter.update.UpdateCashOutActivity;
import com.ptbas.controlcenter.update.UpdateInvoiceActivity;

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
        LinearLayout llStatusPaid, llStatusUnpaid, llHiddenView;
        TextView tvInvDateCreated, tvInvUID, tvPoCustNumber, tvPoCustName;
        Button btnDeleteInv, btnApproveInv, btnShowItemDetail;
        RelativeLayout rlOpenInvDetail, wrapBtnApprove, rlBtnPrintItem;
        CheckBox cbSelectItem;
        //ImageView` ivExpandLlHiddenView;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cdvItem);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);
            rlOpenInvDetail = itemView.findViewById(R.id.rlBtnOpenItemDetail);
            rlBtnPrintItem = itemView.findViewById(R.id.rlBtnPrintItem);
            llStatusPaid = itemView.findViewById(R.id.ll_status_paid);
            llStatusUnpaid = itemView.findViewById(R.id.ll_status_unpaid);
            tvInvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvInvUID = itemView.findViewById(R.id.tv_inv_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            tvPoCustName = itemView.findViewById(R.id.tv_po_cust_name);
            btnDeleteInv = itemView.findViewById(R.id.rlBtnDeleteItem);
            btnApproveInv = itemView.findViewById(R.id.rlBtnApproveItem);
            btnShowItemDetail = itemView.findViewById(R.id.btnOpenItemDetail);
            wrapBtnApprove = itemView.findViewById(R.id.wrapBtnApprove);

            //llHiddenView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        }

        public void viewBind(InvoiceModel invoiceModel) {
            cbSelectItem.setChecked(false);
            dialogInterface = new DialogInterface();

            rlBtnPrintItem.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invoiceModel.setChecked(!invoiceModel.isChecked());
                    cbSelectItem.setChecked(invoiceModel.isChecked());
                }
            });

            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invoiceModel.setChecked(!invoiceModel.isChecked());
                    cbSelectItem.setChecked(invoiceModel.isChecked());
                }
            });

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

            /*ivExpandLlHiddenView.setOnClickListener(view -> {
                if (llHiddenView.getVisibility() == View.VISIBLE) {
                    llHiddenView.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    ivExpandLlHiddenView.setImageResource(R.drawable.ic_outline_keyboard_arrow_down);
                }
                else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    llHiddenView.setVisibility(View.VISIBLE);
                    ivExpandLlHiddenView.setImageResource(R.drawable.ic_outline_keyboard_arrow_up);
                }
            });*/

            btnShowItemDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, UpdateInvoiceActivity.class);
                    i.putExtra("key", invUID);
                    context.startActivity(i);
                    //Toast.makeText(context, coDocumentID, Toast.LENGTH_SHORT).show();
                }
            });

            btnApproveInv.setOnClickListener(view ->
                    dialogInterface.approveInvConfirmation(context, invDocumentID));

            btnDeleteInv.setOnClickListener(view ->
                    dialogInterface.deleteInvConfirmation(context, invDocumentID));
        }


    }
    public void clearSelection() {
        for (int i = 0; i < invoiceModelArrayList.size(); i++) {
            invoiceModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<InvoiceModel> getSelected() {
        ArrayList<InvoiceModel> selected = new ArrayList<>();
        for (int i = 0; i < invoiceModelArrayList.size(); i++) {
            if (invoiceModelArrayList.get(i).isChecked()) {
                selected.add(invoiceModelArrayList.get(i));
            }
        }
        return selected;
    }
}
