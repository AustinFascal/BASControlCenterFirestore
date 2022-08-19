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
import com.ptbas.controlcenter.model.ReceivedOrderModel;

import java.util.ArrayList;

public class ROManagementAdapter extends RecyclerView.Adapter<ROManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<ReceivedOrderModel> receivedOrderModelArrayList;
    DialogInterface dialogInterface;

    public ROManagementAdapter(Context context, ArrayList<ReceivedOrderModel> receivedOrderModelArrayList) {
        this.context = context;
        this.receivedOrderModelArrayList = receivedOrderModelArrayList;
    }

    @NonNull
    @Override
    public ROManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_received_order, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ROManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(receivedOrderModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return receivedOrderModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llStatusApproved, llStatusPOAvailable;
        TextView tvRoDateTime, tvRoUid, tvPoCustNumber;
        Button btnDeleteRo, btnApproveRo;
        RelativeLayout rlOpenRoDetail;
        ImageView ivShowDetail;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShowDetail = itemView.findViewById(R.id.iv_show_detail);
            rlOpenRoDetail = itemView.findViewById(R.id.rl_open_ro_detail);
            llStatusApproved = itemView.findViewById(R.id.ll_status_approved);
            llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_vailable);
            tvRoDateTime = itemView.findViewById(R.id.tv_ro_date_time);
            tvRoUid = itemView.findViewById(R.id.tv_ro_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            btnDeleteRo = itemView.findViewById(R.id.btn_delete_ro);
            btnApproveRo = itemView.findViewById(R.id.btn_approve_ro);
        }

        public void viewBind(ReceivedOrderModel receivedOrderModel) {
            dialogInterface = new DialogInterface();
            String dateNTime = receivedOrderModel.getRoDateCreated();
            String roUID = "RO-"+receivedOrderModel.getRoUID();
            String poCustNumb = "PO: "+receivedOrderModel.getRoPoCustNumber();
            boolean giStatus = receivedOrderModel.getRoStatus();

            tvRoDateTime.setText(dateNTime);
            tvRoUid.setText(roUID);
            tvPoCustNumber.setText(poCustNumb);
            if (giStatus){
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveRo.setVisibility(View.GONE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                btnApproveRo.setVisibility(View.VISIBLE);
            }
            if (tvPoCustNumber.getText().toString().equals("PO: -")){
                tvPoCustNumber.setVisibility(View.GONE);
                llStatusPOAvailable.setVisibility(View.VISIBLE);
            } else {
                tvPoCustNumber.setVisibility(View.VISIBLE);
                llStatusPOAvailable.setVisibility(View.GONE);
            }

            rlOpenRoDetail.setOnClickListener(view -> {
                /*String roUID1 =receivedOrderModel.getRoUID();
                Intent i = new Intent(context, UpdateGoodIssueActivity.class);
                i.putExtra("key", roUID1);
                context.startActivity(i);*/
            });

            btnApproveRo.setOnClickListener(view -> {
                if (tvPoCustNumber.getText().toString().equals("PO: -")){
                    dialogInterface.noRoPoNumberInformation(context, receivedOrderModel.getRoDocumentID());
                } else {
                    dialogInterface.approveRoConfirmation(context, receivedOrderModel.getRoDocumentID());
                }
            });

            btnDeleteRo.setOnClickListener(view ->
                    dialogInterface.deleteRoConfirmation(context, receivedOrderModel.getRoDocumentID()));
        }
    }
}
