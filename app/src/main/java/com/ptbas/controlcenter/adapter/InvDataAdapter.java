package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.InvModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.update.UpdtInvActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvDataAdapter extends RecyclerView.Adapter<InvDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<InvModel> invModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;

    public InvDataAdapter(Context context, ArrayList<InvModel> invModelArrayList) {
        this.context = context;
        this.invModelArrayList = invModelArrayList;
    }

    @NonNull
    @Override
    public InvDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_invoice_desktop, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InvDataAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(invModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return invModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llStatusPaid, llStatusUnpaid, llHiddenView;
        TextView tvVolume, tvPoMaterial, tvRoType, tvInvoiceID, tvDateCreated, tvCustomerName, tvDateDeliveryPeriod, tvTotalDue;
        Button btnDeleteItem, btnOpenItemDetail;
        RelativeLayout rlBtnDeleteItem, rlBtnOpenItemDetail;
        CheckBox cbSelectItem;
        String invPoType, custDocmentID, invPoUID, currency, matNameVal;
        double matQuantity;
        List<ProductItems> productItemsList;

        DecimalFormat df = new DecimalFormat("0.00");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            rlBtnDeleteItem = itemView.findViewById(R.id.rlBtnDeleteItem);
            rlBtnOpenItemDetail = itemView.findViewById(R.id.rlBtnOpenItemDetail);
            llStatusPaid = itemView.findViewById(R.id.llStatusPaid);
            llStatusUnpaid = itemView.findViewById(R.id.llStatusUnpaid);

            tvVolume = itemView.findViewById(R.id.tvVolume);
            tvPoMaterial = itemView.findViewById(R.id.tvPoMaterialName);
            tvRoType = itemView.findViewById(R.id.tvRoType);
            tvInvoiceID = itemView.findViewById(R.id.tvInvoiceID);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDateDeliveryPeriod = itemView.findViewById(R.id.tvDateDeliveryPeriod);
            tvTotalDue = itemView.findViewById(R.id.tvTotalDue);

            btnOpenItemDetail = itemView.findViewById(R.id.btnOpenItemDetail);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);

        }

        public void viewBind(InvModel invModel) {
            cbSelectItem.setChecked(false);
            dialogInterfaceUtils = new DialogInterfaceUtils();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invModel.setChecked(!invModel.isChecked());
                    cbSelectItem.setChecked(invModel.isChecked());
                }
            });

            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invModel.setChecked(!invModel.isChecked());
                    cbSelectItem.setChecked(invModel.isChecked());
                }
            });

            String invUID = invModel.getInvUID();
            String invDocumentID = invModel.getInvDocumentUID();
            String invRoUID = invModel.getRoDocumentID();
            String invDateCreated = invModel.getInvDateNTimeCreated();
            String invVolume = invModel.getInvTotalVol();
            String invTotalDue = invModel.getInvTotalDue();
            String invDateDeliveryPeriod = invModel.getInvDateDeliveryPeriod();
            String invPoCustName = invModel.getCustDocumentID();
            boolean recalculateInvStatus = invModel.getInvRecalculate();

            String invDateHandover = invModel.getInvDateHandover();

            CollectionReference refRO = db.collection("ReceivedOrderData");

            refRO.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String getDocumentID = documentSnapshot.getId();
                        if (getDocumentID.equals(invRoUID)){

                            RoModel roModel = documentSnapshot.toObject(RoModel.class);

                            custDocmentID = roModel.getCustDocumentID();
                            invPoUID = roModel.getRoPoCustNumber();
                            currency = roModel.getRoCurrency();

                            if (roModel.getRoType().equals(0)){
                                invPoType = "JASA ANGKUT + MATERIAL";
                            }
                            if (roModel.getRoType().equals(1)){
                                invPoType = "MATERIAL SAJA";
                            }
                            if (roModel.getRoType().equals(2)){
                                invPoType = "JASA ANGKUT SAJA";
                            }


                            tvRoType.setText(invPoType);

                            HashMap<String, List<ProductItems>> map = roModel.getRoOrderedItems();
                            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                productItemsList = e.getValue();
                                for (int i = 0; i<productItemsList.size();i++){
                                    if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                        //transportServiceNameVal = productItemsList.get(0).getMatName();
                                        //transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();
                                        matNameVal = productItemsList.get(0).getMatName();
                                        //matQuantity = productItemsList.get(i).getMatQuantity();
                                    } else {
                                        matNameVal = productItemsList.get(i).getMatName();
                                        //matQuantity = productItemsList.get(i).getMatQuantity();
                                    }
                                }

                            }

                            tvPoMaterial.setText(matNameVal);


                            /*tvPoTransportTypeAndMatName.setText(invPoType.concat(" | "+matNameVal));
                            tvPoCustNumber.setText("PO: "+invPoUID);*/

                            CollectionReference refCust = db.collection("CustomerData");

                            refCust.get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()){
                                    for(DocumentSnapshot documentSnapshot2 : task2.getResult()){
                                        String getDocumentID2 = documentSnapshot2.getId();
                                        if (getDocumentID2.equals(custDocmentID)){
                                            CustModel custModel = documentSnapshot2.toObject(CustModel.class);
                                            tvCustomerName.setText(custModel.getCustName());

                                            String custTaxStatus;
                                            if (custModel.getCustNPWP().equals("")|| custModel.getCustNPWP().isEmpty()){
                                                custTaxStatus = "NON PKP";
                                            } else {
                                                custTaxStatus = "PKP";
                                            }

                                            /*tvRoMatSellPriceCubicAndTaxType.setText(custTaxStatus + " | " + currency
                                                    + " " +
                                                    currencyFormat(df.format(matSellPrice))
                                                    + "/m3 | " +
                                                    currencyFormat(df.format(matQuantity))+ " m3");*/
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });

            tvVolume.setText(invVolume);

            tvDateCreated.setText(invDateCreated);
            tvInvoiceID.setText(invUID);

            tvDateDeliveryPeriod.setText(invDateDeliveryPeriod);
            tvTotalDue.setText(invTotalDue);

            if (recalculateInvStatus){
                tvTotalDue.setTextColor(AppCompatResources.getColorStateList(context, R.color.pure_orange));
            } else{
                tvTotalDue.setTextColor(AppCompatResources.getColorStateList(context, R.color.black));
            }

            if (!invDateHandover.isEmpty()){
                llStatusPaid.setVisibility(View.VISIBLE);
                llStatusUnpaid.setVisibility(View.GONE);
            } else {
                llStatusPaid.setVisibility(View.GONE);
                llStatusUnpaid.setVisibility(View.VISIBLE);
            }

            btnOpenItemDetail.setOnClickListener(view -> {
                Intent i = new Intent(context, UpdtInvActivity.class);
                i.putExtra("key", invDocumentID);
                context.startActivity(i);
            });
            btnDeleteItem.setOnClickListener(view ->
                    dialogInterfaceUtils.deleteInvConfirmation(context, invDocumentID));
        }
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public void clearSelection() {
        for (int i = 0; i < invModelArrayList.size(); i++) {
            invModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<InvModel> getSelected() {
        ArrayList<InvModel> selected = new ArrayList<>();
        for (int i = 0; i < invModelArrayList.size(); i++) {
            if (invModelArrayList.get(i).isChecked()) {
                selected.add(invModelArrayList.get(i));
            }
        }
        return selected;
    }
}
