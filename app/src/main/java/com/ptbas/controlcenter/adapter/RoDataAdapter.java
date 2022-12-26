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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RoModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RoDataAdapter extends RecyclerView.Adapter<RoDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<RoModel> roModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;
    HelperUtils helperUtils = new HelperUtils();
    String custName, taxStatus, custTaxStatus;

    Boolean taxTypeVal;

    public RoDataAdapter(Context context, ArrayList<RoModel> roModelArrayList) {
        this.context = context;
        this.roModelArrayList = roModelArrayList;
    }

    @NonNull
    @Override
    public RoDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_received_order, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoDataAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(roModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return roModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        //llStatusPOAvailable
        LinearLayout llStatusApproved, llHiddenView;
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
            //llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_unvailable);
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
        }

        public void viewBind(RoModel roModel) {



            cbSelectItem.setChecked(false);

            if (Objects.equals(helperUtils.ACTIVITY_NAME, "UPDATE")){
                btnDeleteRo.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roModel.setChecked(!roModel.isChecked());
                    cbSelectItem.setChecked(roModel.isChecked());
                }
            });

            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    roModel.setChecked(!roModel.isChecked());
                    cbSelectItem.setChecked(roModel.isChecked());
                }
            });

            dialogInterfaceUtils = new DialogInterfaceUtils();
            String dateNTime = roModel.getRoDateCreated();
            String currency = roModel.getRoCurrency();


            String partRoUIDVal = roModel.getRoUID();
            String roUID = "RO: " + partRoUIDVal;
            String poCustNummVal = roModel.getRoPoCustNumber();
            String poCustNumb = "PO: " + poCustNummVal;
            boolean giStatus = roModel.getRoStatus();

            taxTypeVal = roModel.getCustTaxType();
            tvRoDateTime.setText(dateNTime + " | TOP: " + roModel.getRoTOP() + " hari");
            tvRoUid.setText(roUID);
            tvPoCustNumber.setText(poCustNumb);



            String roType = null;
            if (roModel.getRoType().equals(0)){
                roType = "JASA ANGKUT + MATERIAL";
            }
            if (roModel.getRoType().equals(1)){
                roType = "MATERIAL SAJA";
            }
            if (roModel.getRoType().equals(2)){
                roType = "JASA ANGKUT SAJA";
            }

            HashMap<String, List<ProductItems>> map = roModel.getRoOrderedItems();
            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                productItemsList = e.getValue();
                for (int i = 0; i<productItemsList.size();i++){
                    matNameVal = productItemsList.get(i).getMatName();
                    matSellPrice = productItemsList.get(i).getMatBuyPrice();
                    matQuantity = productItemsList.get(i).getMatQuantity();
                }

            }

            tvRoTypeAndMatName.setText(roType + " | " + matNameVal);

            CollectionReference refCust = db.collection("CustomerData");

            if (taxTypeVal){
                custTaxStatus = "PKP";
            } else {
                custTaxStatus = "NON PKP";
            }

            tvCustName.setText(custTaxStatus);

            refCust.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String getDocumentID = documentSnapshot.getId();
                        if (getDocumentID.equals(roModel.getCustDocumentID())){
                            CustModel custModel = documentSnapshot.toObject(CustModel.class);
                            custName = custModel.getCustName();
                            //taxStatus = customerModel.getCustNPWP();


                            tvCustName.append(" | "+custName);

                            tvRoMatSellPriceCubicAndTaxType.setText(currency
                                    + " " +
                                    currencyFormat(df.format(matSellPrice))
                                    + "/m3 | " +
                                    currencyFormat(df.format(matQuantity))+ " m3");
                        }
                    }
                }
            });

            if (giStatus){
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveRo.setVisibility(View.GONE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                btnApproveRo.setVisibility(View.VISIBLE);
            }
           /* if (partRoUIDVal.contains(poCustNummVal)){
                //tvPoCustNumber.setVisibility(View.GONE);
                llStatusPOAvailable.setVisibility(View.VISIBLE);
            } else {
                //tvPoCustNumber.setVisibility(View.VISIBLE);
                llStatusPOAvailable.setVisibility(View.GONE);
            }*/

            btn3.setOnClickListener(view -> {
                Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show();
            });

            btn2.setOnClickListener(view -> {
                /*if (llStatusPOAvailable.getVisibility() == View.VISIBLE){
                    dialogInterface.noRoPoNumberInformation(context, receivedOrderModel.getRoDocumentID());
                } else {*/
                dialogInterfaceUtils.approveRoConfirmation(context, roModel.getRoDocumentID());
                //}
            });

            btn1.setOnClickListener(view ->
                    dialogInterfaceUtils.deleteRoConfirmation(context, roModel.getRoDocumentID()));
        }
    }


    public ArrayList<RoModel> selectAll() {
        ArrayList<RoModel> selected = new ArrayList<>();
        for (int i = 0; i < roModelArrayList.size(); i++) {
            if (!roModelArrayList.get(i).isChecked()) {
                selected.add(roModelArrayList.get(i));
            }
        }
        return selected;
    }

    public void clearSelection() {
        for (int i = 0; i < roModelArrayList.size(); i++) {
            roModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<RoModel> getSelected() {
        ArrayList<RoModel> selected = new ArrayList<>();
        for (int i = 0; i < roModelArrayList.size(); i++) {
            if (roModelArrayList.get(i).isChecked()) {
                selected.add(roModelArrayList.get(i));
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
