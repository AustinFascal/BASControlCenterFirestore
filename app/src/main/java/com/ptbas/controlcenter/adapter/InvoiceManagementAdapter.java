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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.InvoiceModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.update.UpdateInvoiceActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        TextView tvInvDateCreated, tvInvUID, tvPoCustNumber, tvPoCustName, tvRoMatSellPriceCubicAndTaxType, tvPoTransportTypeAndMatName;
        Button btnDeleteInv, btnShowItemDetail;
        RelativeLayout rlOpenInvDetail, rlBtnPrintItem;
        CheckBox cbSelectItem;
        //ImageView` ivExpandLlHiddenView;
        CardView cardView;
        String invPoType, custDocmentID, invPoUID, currency, matNameVal;
        double matSellPrice, matQuantity;
        List<ProductItems> productItemsList;

        DecimalFormat df = new DecimalFormat("0.00");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
            btnShowItemDetail = itemView.findViewById(R.id.btnOpenItemDetail);
            tvRoMatSellPriceCubicAndTaxType = itemView.findViewById(R.id.tvRoMatSellPriceCubicAndTaxType);
            tvPoTransportTypeAndMatName = itemView.findViewById(R.id.tvPoTransportTypeAndMatName);

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
            String invDocumentID = invoiceModel.getInvDocumentUID();

            String invPoCustName = invoiceModel.getCustDocumentID();
            String invDateCreated = invoiceModel.getInvDateNTimeCreated();
            //String invPoType = "TEST";
            String invStatus = invoiceModel.getInvVerifiedBy();

            CollectionReference refRO = db.collection("ReceivedOrderData");

            refRO.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String getDocumentID = documentSnapshot.getId();
                        if (getDocumentID.equals(invoiceModel.getRoDocumentID())){

                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);

                            custDocmentID = receivedOrderModel.getCustDocumentID();
                            invPoUID = receivedOrderModel.getRoPoCustNumber();
                            currency = receivedOrderModel.getRoCurrency();

                            if (receivedOrderModel.getRoType().equals(0)){
                                invPoType = "JASA ANGKUT + MATERIAL";
                            }
                            if (receivedOrderModel.getRoType().equals(1)){
                                invPoType = "MATERIAL SAJA";
                            }
                            if (receivedOrderModel.getRoType().equals(2)){
                                invPoType = "JASA ANGKUT SAJA";
                            }



                            /*HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                productItemsList = e.getValue();
                                for (int i = 0; i<productItemsList.size();i++){
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matSellPrice = productItemsList.get(i).getMatBuyPrice();
                                    matQuantity = productItemsList.get(i).getMatQuantity();
                                }
                            }*/

                            HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                productItemsList = e.getValue();
                                for (int i = 0; i<productItemsList.size();i++){
                                    if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    /*transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();*/
                                    } else {
                                        matNameVal = productItemsList.get(i).getMatName();
                                        matSellPrice = productItemsList.get(i).getMatSellPrice();
                                        matQuantity = productItemsList.get(i).getMatQuantity();
                                    }
                                }

                            }

                            tvPoTransportTypeAndMatName.setText(invPoType.concat(" | "+matNameVal));
                            tvPoCustNumber.setText("PO: "+invPoUID);

                            CollectionReference refCust = db.collection("CustomerData");

                            refCust.get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()){
                                    for(DocumentSnapshot documentSnapshot2 : task2.getResult()){
                                        String getDocumentID2 = documentSnapshot2.getId();
                                        if (getDocumentID2.equals(custDocmentID)){
                                            CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                            tvPoCustName.setText(customerModel.getCustName());

                                            String custTaxStatus;
                                            if (customerModel.getCustNPWP().equals("")||customerModel.getCustNPWP().isEmpty()){
                                                custTaxStatus = "NON PKP";
                                            } else {
                                                custTaxStatus = "PKP";
                                            }

                                            tvRoMatSellPriceCubicAndTaxType.setText(custTaxStatus + " | " + currency
                                                    + " " +
                                                    currencyFormat(df.format(matSellPrice))
                                                    + "/m3 | " +
                                                    currencyFormat(df.format(matQuantity))+ " m3");
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });







            tvInvUID.setText(invUID);
            tvInvDateCreated.setText(invDateCreated);

            tvPoCustName.setText(invPoCustName);
            if (!invStatus.isEmpty()){
                llStatusPaid.setVisibility(View.VISIBLE);
                llStatusUnpaid.setVisibility(View.GONE);
            } else {
                llStatusPaid.setVisibility(View.GONE);
                llStatusUnpaid.setVisibility(View.VISIBLE);
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
                    i.putExtra("key", invDocumentID);
                    context.startActivity(i);
                    //Toast.makeText(context, coDocumentID, Toast.LENGTH_SHORT).show();
                }
            });

            /*btnApproveInv.setOnClickListener(view ->
                    dialogInterface.approveInvConfirmation(context, invDocumentID));*/

            btnDeleteInv.setOnClickListener(view ->
                    dialogInterface.deleteInvConfirmation(context, invDocumentID));
        }
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
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
