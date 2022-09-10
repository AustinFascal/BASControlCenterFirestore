package com.ptbas.controlcenter.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.ptbas.controlcenter.helper.DialogInterface;
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
        LinearLayout llStatusApproved, llStatusPOAvailable, llHiddenView;
        TextView tvRoDateTime, tvRoUid, tvPoCustNumber;
        RelativeLayout btnDeleteRo, btnApproveRo;
        RelativeLayout rlOpenRoDetail;
        Button btn1, btn2, btn3;
        //ImageView ivExpandLlHiddenView;
        CardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);
            rlOpenRoDetail = itemView.findViewById(R.id.open_detail);
            llStatusApproved = itemView.findViewById(R.id.ll_status_approved);
            llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_unvailable);
            tvRoDateTime = itemView.findViewById(R.id.tv_inv_date_created);
            tvRoUid = itemView.findViewById(R.id.tv_ro_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            btnDeleteRo = itemView.findViewById(R.id.btn_delete_inv);
            btnApproveRo = itemView.findViewById(R.id.btn_approve_inv);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            btn3 = itemView.findViewById(R.id.btn3);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);

            //llHiddenView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
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

            btn3.setOnClickListener(view -> {
                Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show();
            });

            btn2.setOnClickListener(view -> {
                if (tvPoCustNumber.getText().toString().equals("PO: -")){
                    dialogInterface.noRoPoNumberInformation(context, receivedOrderModel.getRoDocumentID());
                } else {
                    dialogInterface.approveRoConfirmation(context, receivedOrderModel.getRoDocumentID());
                }
            });

            btn1.setOnClickListener(view ->
                    dialogInterface.deleteRoConfirmation(context, receivedOrderModel.getRoDocumentID()));
        }
    }
}
