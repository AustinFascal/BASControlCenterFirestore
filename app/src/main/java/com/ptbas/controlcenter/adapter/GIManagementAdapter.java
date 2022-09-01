package com.ptbas.controlcenter.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
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
        LinearLayout llStatusApproved, llStatusInvoiced, llStatusPOAvailable, llRoNeedsUpdate, llHiddenView;
        TextView tvCubication, tvGiDateTime, tvGiUid, tvRoUid, tvGiMatDetail, tvGiVhlDetail,
                tvVhlUid, tvPoCustNumber;
        RelativeLayout btnDeleteGi, btnApproveGi;
        Button btn1, btn2, btn3;
        ImageView ivExpandLlHiddenView;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            llStatusApproved = itemView.findViewById(R.id.ll_status_approved);
            llStatusInvoiced = itemView.findViewById(R.id.ll_status_invoiced);
            llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_unvailable);
            llRoNeedsUpdate = itemView.findViewById(R.id.ll_ro_needs_update);
            tvCubication = itemView.findViewById(R.id.tv_cubication);
            tvGiDateTime = itemView.findViewById(R.id.tv_inv_date_created);
            tvGiUid = itemView.findViewById(R.id.tv_gi_uid);
            tvRoUid = itemView.findViewById(R.id.tv_ro_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            tvGiMatDetail = itemView.findViewById(R.id.tv_gi_mat_detail);
            tvGiVhlDetail = itemView.findViewById(R.id.tv_gi_vhl_detail);
            tvVhlUid = itemView.findViewById(R.id.tv_vhl_uid);
            btnDeleteGi = itemView.findViewById(R.id.btn_delete_gi);
            btnApproveGi = itemView.findViewById(R.id.btn_approve_gi);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            btn3 = itemView.findViewById(R.id.btn3);
            ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);

            llHiddenView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
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

            tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));
            tvGiDateTime.setText(dateNTime);
            tvGiUid.setText(giUID);
            tvRoUid.setText(roUID);
            tvPoCustNumber.setText(poCustNumb);
            tvGiMatDetail.setText(matDetail);
            tvGiVhlDetail.setText(vhlDetail);
            tvVhlUid.setText(vhlUID);

            String giUIDVal =goodIssueModel.getGiUID();
            String giRoUIDVal =goodIssueModel.getGiRoUID();

            DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("ReceivedOrderData").whereEqualTo("roUID", giRoUIDVal)
                    .addSnapshotListener((value, error) -> {
                        String poUIDUpdate = "";
                        for (DocumentSnapshot d : value.getDocuments()) {
                            ReceivedOrderModel receivedOrderModel = d.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(d.getId());
                            poUIDUpdate = receivedOrderModel.getRoPoCustNumber();
                        }
                        databaseReferenceGI.child("GoodIssueData").child(giUIDVal).child("giPoCustNumber").setValue(poUIDUpdate);
                    });

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


            ivExpandLlHiddenView.setOnClickListener(view -> {
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
            });

            btn3.setOnClickListener(view -> {
                String giUID1 =goodIssueModel.getGiUID();
                Intent i = new Intent(context, UpdateGoodIssueActivity.class);
                i.putExtra("key", giUID1);
                context.startActivity(i);
            });

            btn2.setOnClickListener(view -> {
                if (tvPoCustNumber.getText().toString().equals("PO: -")){
                    dialogInterface.noPoNumberInformation(context);
                } else {
                    dialogInterface.approveGiConfirmation(context, goodIssueModel.getGiUID());
                }
            });

            btn1.setOnClickListener(view ->
                    dialogInterface.deleteGiConfirmation(context, goodIssueModel.getGiUID()));
        }
    }
}
