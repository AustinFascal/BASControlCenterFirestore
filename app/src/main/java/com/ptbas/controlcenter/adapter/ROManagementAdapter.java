package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        TextView tvRoDateTime, tvRoUid, tvPoCustNumber, tvCustName, tvRoTypeAndMatName, tvRoMatSellPriceCubicAndTaxType;
        RelativeLayout btnDeleteRo, btnApproveRo;
        Button btn1, btn2, btn3;
        //ImageView ivExpandLlHiddenView;
        CardView cardView;
        CheckBox cbSelectItem;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<ProductItems> productItemsList;
        double matSellPrice, matQuantity, transportServiceSellPrice;
        String matNameVal;

        DecimalFormat df = new DecimalFormat("0.00");


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cdvItem);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);
            llStatusApproved = itemView.findViewById(R.id.llStatusApproved);
            llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_unvailable);
            tvRoDateTime = itemView.findViewById(R.id.tvDateCreated);
            tvRoUid = itemView.findViewById(R.id.tvCoTotal);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);

            tvCustName = itemView.findViewById(R.id.tvCustName);
            tvRoTypeAndMatName = itemView.findViewById(R.id.tvRoTypeAndMatName);
            tvRoMatSellPriceCubicAndTaxType = itemView.findViewById(R.id.tvRoMatSellPriceCubicAndTaxType);

            btnDeleteRo = itemView.findViewById(R.id.rlBtnDeleteItem);
            btnApproveRo = itemView.findViewById(R.id.rlBtnApproveItem);
            btn1 = itemView.findViewById(R.id.btnDeleteItem);
            btn2 = itemView.findViewById(R.id.btnApproveItem);
            btn3 = itemView.findViewById(R.id.btnOpenItemDetail);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            //ivExpandLlHiddenView = itemView.findViewById(R.id.ivExpandLlHiddenView);

            //llHiddenView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        }

        public void viewBind(ReceivedOrderModel receivedOrderModel) {

            HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                productItemsList = e.getValue();
                for (int i = 0; i<productItemsList.size();i++){
                                /*if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();
                                } else {*/
                    matNameVal = productItemsList.get(i).getMatName();
                    matSellPrice = productItemsList.get(i).getMatBuyPrice();
                    matQuantity = productItemsList.get(i).getMatQuantity();
                    //}
                }

            }

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
            String currency = receivedOrderModel.getRoCurrency();


            String custTaxStatus;
            if (receivedOrderModel.getRoVATPPN().equals(0)||receivedOrderModel.getRoVATPPN().toString().isEmpty()){
                custTaxStatus = "NON PKP";
            } else {
                custTaxStatus = "PKP";
            }

            String[] partRoUID = receivedOrderModel.getRoUID().split(" - ");
            String partRoUIDVal = partRoUID[2];
            String roUID = "RO: " + partRoUIDVal;

            //String roUID = "RO: "+receivedOrderModel.getRoUID();
            String poCustNummVal = receivedOrderModel.getRoPoCustNumber();
            String poCustNumb = "PO: " + poCustNummVal;
            boolean giStatus = receivedOrderModel.getRoStatus();

            tvRoDateTime.setText(dateNTime + " | " + receivedOrderModel.getRoTOP() + " hari");
            tvRoUid.setText(roUID);
            tvPoCustNumber.setText(poCustNumb);

            String roType = null;
            if (receivedOrderModel.getRoType().equals(0)){
                roType = "JASA ANGKUT + MATERIAL";
            }
            if (receivedOrderModel.getRoType().equals(1)){
                roType = "MATERIAL SAJA";
            }
            if (receivedOrderModel.getRoType().equals(2)){
                roType = "JASA ANGKUT SAJA";
            }
            tvCustName.setText(receivedOrderModel.getRoCustName());
            tvRoTypeAndMatName.setText(roType + " | " + matNameVal);
            tvRoMatSellPriceCubicAndTaxType.setText(custTaxStatus + " | " + currency
                    + " " +
                    currencyFormat(df.format(matSellPrice))
                    + "/m3 | " +
                    currencyFormat(df.format(matQuantity))+ " m3");

            if (giStatus){
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveRo.setVisibility(View.GONE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                btnApproveRo.setVisibility(View.VISIBLE);
            }
            if (partRoUIDVal.contains(" ")){
                //tvPoCustNumber.setVisibility(View.GONE);
                llStatusPOAvailable.setVisibility(View.VISIBLE);
            } else {
                //tvPoCustNumber.setVisibility(View.VISIBLE);
                llStatusPOAvailable.setVisibility(View.GONE);
            }

            btn3.setOnClickListener(view -> {
                Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show();
            });

            btn2.setOnClickListener(view -> {
                if (llStatusPOAvailable.getVisibility() == View.VISIBLE){
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

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
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
