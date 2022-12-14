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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_invoice_desktop, parent, false);
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

        public void viewBind(InvoiceModel invoiceModel) {
            cbSelectItem.setChecked(false);
            dialogInterface = new DialogInterface();

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
            String invRoUID = invoiceModel.getRoDocumentID();
            String invDateCreated = invoiceModel.getInvDateNTimeCreated();
            String invVolume = invoiceModel.getInvTotalVol();
            String invTotalDue = invoiceModel.getInvTotalDue();
            String invDateDeliveryPeriod = invoiceModel.getInvDateDeliveryPeriod();
            String invPoCustName = invoiceModel.getCustDocumentID();
            boolean recalculateInvStatus = invoiceModel.getInvRecalculate();

            String invDateVerified = invoiceModel.getInvDateVerified();

            CollectionReference refRO = db.collection("ReceivedOrderData");

            refRO.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String getDocumentID = documentSnapshot.getId();
                        if (getDocumentID.equals(invRoUID)){

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


                            tvRoType.setText(invPoType);

                            HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
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
                                            CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                            tvCustomerName.setText(customerModel.getCustName());

                                            String custTaxStatus;
                                            if (customerModel.getCustNPWP().equals("")||customerModel.getCustNPWP().isEmpty()){
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

            if (!invDateVerified.isEmpty()){
                llStatusPaid.setVisibility(View.VISIBLE);
                llStatusUnpaid.setVisibility(View.GONE);
            } else {
                llStatusPaid.setVisibility(View.GONE);
                llStatusUnpaid.setVisibility(View.VISIBLE);
            }

            btnOpenItemDetail.setOnClickListener(new View.OnClickListener() {
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

            btnDeleteItem.setOnClickListener(view ->
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
