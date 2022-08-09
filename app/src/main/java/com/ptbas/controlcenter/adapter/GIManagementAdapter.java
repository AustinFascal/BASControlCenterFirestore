package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.update.UpdateGoodIssueActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GIManagementAdapter extends RecyclerView.Adapter<GIManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<GoodIssueModel> goodIssueModelArrayList;
    DialogInterface dialogInterface;

    public GIManagementAdapter(Context context, ArrayList<GoodIssueModel> goodIssueModelArrayList) {
        this.context = context;
        this.goodIssueModelArrayList = goodIssueModelArrayList;
    }

    @NonNull
    @Override
    public GIManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_good_issue, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GIManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(goodIssueModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return goodIssueModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llStatusApproved, llStatusInvoiced, llStatusPOAvailable;
        TextView tvCubication, tvGiDateTime, tvGiUid, tvRoUid, tvGiMatDetail, tvGiVhlDetail,
                tvVhlUid, tvPoCustNumber;
        Button btnDeleteGi, btnApproveGi;
        RelativeLayout rlOpenGiDetail;
        String updatedCustNumb;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rlOpenGiDetail = itemView.findViewById(R.id.rl_open_ro_detail);
            llStatusApproved = itemView.findViewById(R.id.ll_status_approved);
            llStatusInvoiced = itemView.findViewById(R.id.ll_status_invoiced);
            llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_vailable);
            tvCubication = itemView.findViewById(R.id.tv_cubication);
            tvGiDateTime = itemView.findViewById(R.id.tv_gi_date_time);
            tvGiUid = itemView.findViewById(R.id.tv_gi_uid);
            tvRoUid = itemView.findViewById(R.id.tv_ro_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            tvGiMatDetail = itemView.findViewById(R.id.tv_gi_mat_detail);
            tvGiVhlDetail = itemView.findViewById(R.id.tv_gi_vhl_detail);
            tvVhlUid = itemView.findViewById(R.id.tv_vhl_uid);
            btnDeleteGi = itemView.findViewById(R.id.btn_delete_gi);
            btnApproveGi = itemView.findViewById(R.id.btn_approve_gi);
        }

        public void viewBind(GoodIssueModel goodIssueModel) {
            dialogInterface = new DialogInterface();
            DecimalFormat df = new DecimalFormat("0.00");
            float cubication = goodIssueModel.getGiVhlCubication();
            String dateNTime = goodIssueModel.getGiDateCreated()+" | "+goodIssueModel.getGiTimeCreted();
            String giUID = "GI-"+goodIssueModel.getGiUID();
            String roUID = "RO-"+goodIssueModel.getGiRoUID();
            String poCustNumb = "PO: "+goodIssueModel.getGiPoCustNumber();

            String matDetail = goodIssueModel.getGiMatType()+" | "+goodIssueModel.getGiMatName();
            String vhlDetail = "(P) "+goodIssueModel.getVhlLength().toString()+" (L) "+goodIssueModel.getVhlWidth().toString()+" (T) "+goodIssueModel.getVhlHeight().toString()+" | "+"(K) "+goodIssueModel.getVhlHeightCorrection().toString()+" (TK) "+goodIssueModel.getVhlHeightAfterCorrection().toString();
            String vhlUID = goodIssueModel.getVhlUID();
            boolean giStatus = goodIssueModel.getGiStatus();
            boolean giInvoiced = goodIssueModel.getGiInvoiced();

            tvCubication.setText(Html.fromHtml(String.valueOf(df.format(cubication))+" m\u00B3"));
            tvGiDateTime.setText(dateNTime);
            tvGiUid.setText(giUID);
            tvRoUid.setText(roUID);
            tvPoCustNumber.setText(poCustNumb);
            tvGiMatDetail.setText(matDetail);
            tvGiVhlDetail.setText(vhlDetail);
            tvVhlUid.setText(vhlUID);
            if (giStatus){
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveGi.setVisibility(View.GONE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                btnApproveGi.setVisibility(View.VISIBLE);
            }

            if (giInvoiced){
                llStatusInvoiced.setVisibility(View.VISIBLE);
            } else {
                llStatusInvoiced.setVisibility(View.GONE);
            }

            if (tvPoCustNumber.getText().toString().equals("PO: -")){
                tvPoCustNumber.setVisibility(View.GONE);
                llStatusPOAvailable.setVisibility(View.VISIBLE);
            } else {
                tvPoCustNumber.setVisibility(View.VISIBLE);
                llStatusPOAvailable.setVisibility(View.GONE);
            }

            rlOpenGiDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String giUID=goodIssueModel.getGiUID();
                    Intent i = new Intent(context, UpdateGoodIssueActivity.class);
                    i.putExtra("key",giUID);
                    context.startActivity(i);
                }
            });

            btnApproveGi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvPoCustNumber.getText().toString().equals("PO: -")){
                        dialogInterface.noPoNumberInformation(context);
                    } else {
                        dialogInterface.approveGiConfirmation(context, goodIssueModel.getGiUID());
                    }
                }
            });

            btnDeleteGi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.deleteGiConfirmation(context, goodIssueModel.getGiUID());
                }
            });
        }
    }
}
