package com.ptbas.controlcenter.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;

import java.util.ArrayList;
import java.util.Objects;

public class ROManagementAdapter extends RecyclerView.Adapter<ROManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<ReceivedOrderModel> receivedOrderModelArrayList;
    DialogInterface dialogInterface;
    Helper helper = new Helper();

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
        Button btn1, btn2, btn3;
        //ImageView ivExpandLlHiddenView;
        CardView cardView;
        CheckBox cbSelectItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);
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
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);

            //llHiddenView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        public void viewBind(ReceivedOrderModel receivedOrderModel) {
            cbSelectItem.setChecked(false);

            if (Objects.equals(helper.ACTIVITY_NAME, "UPDATE")){
                btnDeleteRo.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receivedOrderModel.setChecked(!receivedOrderModel.isChecked());
                    cbSelectItem.setChecked(receivedOrderModel.isChecked());
                }
            });

            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receivedOrderModel.setChecked(!receivedOrderModel.isChecked());
                    cbSelectItem.setChecked(receivedOrderModel.isChecked());
                }
            });

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


    public ArrayList<ReceivedOrderModel> selectAll() {
        ArrayList<ReceivedOrderModel> selected = new ArrayList<>();
        for (int i = 0; i < receivedOrderModelArrayList.size(); i++) {
            if (!receivedOrderModelArrayList.get(i).isChecked()) {
                selected.add(receivedOrderModelArrayList.get(i));
            }
        }
        return selected;
    }

    public void clearSelection() {
        for (int i = 0; i < receivedOrderModelArrayList.size(); i++) {
            receivedOrderModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<ReceivedOrderModel> getSelected() {
        ArrayList<ReceivedOrderModel> selected = new ArrayList<>();
        for (int i = 0; i < receivedOrderModelArrayList.size(); i++) {
            if (receivedOrderModelArrayList.get(i).isChecked()) {
                selected.add(receivedOrderModelArrayList.get(i));
            }
        }
        return selected;
    }
    /*public float getSelectedVolume() {
        float selected = 0;
        //ArrayList<GoodIssueModel> selected = new ArrayList<>();
        for (int i = 0; i < receivedOrderModelArrayList.size(); i++) {
            if (receivedOrderModelArrayList.get(i).isChecked()) {
                selected += receivedOrderModelArrayList.get(i).getGiVhlCubication();
                if (!receivedOrderModelArrayList.get(i).isChecked()){
                    selected -= receivedOrderModelArrayList.get(i).getGiVhlCubication();
                }
            }
        }
        return selected;
    }*/
}
