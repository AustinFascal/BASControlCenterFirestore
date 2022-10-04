package com.ptbas.controlcenter.adapter;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.UserManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.SupplierModel;
import com.ptbas.controlcenter.update.UpdateCashOutActivity;
import com.ptbas.controlcenter.update.UpdateGoodIssueActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CashOutManagementAdapter extends RecyclerView.Adapter<CashOutManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<CashOutModel> cashOutModelArrayList;
    DialogInterface dialogInterface;
    Helper helper = new Helper();

    public CashOutManagementAdapter(Context context, ArrayList<CashOutModel> cashOutModelArrayList) {
        this.context = context;
        this.cashOutModelArrayList = cashOutModelArrayList;
    }

    @NonNull
    @Override
    public CashOutManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_cash_out, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CashOutManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(cashOutModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return cashOutModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout llWrapItemStatus, llStatusApproved, llStatusPaid;
        TextView tvDateCreated, tvTotalDue, tvCoUID, tvPoNumber, tvSupplierName;
        Button btnDeleteItem, btnShowItemDetail, btnPrintItem, btnItemPaid, btnApproveItem;
        RelativeLayout rlBtnDeleteItem, rlOpenItemDetail, rlBtnPrintItem, rlBtnItemPaid, rlBtnApproveItem;
        CardView cdvItem;
        CheckBox cbSelectItem;

        DecimalFormat df = new DecimalFormat("0.00");

        Double coTotalTemp;
        boolean coStatusApproval, coStatusPayment;
        String coSupplierName, coDateCreated, coUID, coDocumentID, coPoUID, coSupplierNameTemp, coTotal;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            llWrapItemStatus = itemView.findViewById(R.id.llWrapItemStatus);
            llStatusApproved = itemView.findViewById(R.id.llStatusApproved);
            llStatusPaid = itemView.findViewById(R.id.llStatusPaid);

            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvTotalDue = itemView.findViewById(R.id.tvTotalDue);
            tvCoUID = itemView.findViewById(R.id.tvCoUID);
            tvPoNumber = itemView.findViewById(R.id.tvPoNumber);
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);

            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            btnShowItemDetail = itemView.findViewById(R.id.btnOpenItemDetail);
            btnPrintItem = itemView.findViewById(R.id.btnPrintItem);
            btnItemPaid = itemView.findViewById(R.id.btnItemPaid);
            btnApproveItem = itemView.findViewById(R.id.btnApproveItem);

            rlBtnDeleteItem = itemView.findViewById(R.id.rlBtnDeleteItem);
            rlOpenItemDetail = itemView.findViewById(R.id.rlBtnOpenItemDetail);
            rlBtnPrintItem = itemView.findViewById(R.id.rlBtnPrintItem);
            rlBtnItemPaid  = itemView.findViewById(R.id.rlBtnItemPaid);
            rlBtnApproveItem = itemView.findViewById(R.id.rlBtnApproveItem);

            cdvItem = itemView.findViewById(R.id.cdvItem);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
        }

        public void viewBind(CashOutModel cashOutModel) {
            cbSelectItem.setChecked(false);
            dialogInterface = new DialogInterface();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashOutModel.setChecked(!cashOutModel.isChecked());
                    cbSelectItem.setChecked(cashOutModel.isChecked());
                }
            });

            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashOutModel.setChecked(!cashOutModel.isChecked());
                    cbSelectItem.setChecked(cashOutModel.isChecked());
                }
            });

            cbSelectItem.setChecked(false);

            btnPrintItem.setVisibility(View.GONE);
            rlBtnPrintItem.setVisibility(View.GONE);

            coDateCreated = cashOutModel.getCoDateAndTimeCreated();
            coUID = cashOutModel.getCoUID();
            coDocumentID = cashOutModel.getCoDocumentID();
            coPoUID = "PO: "+cashOutModel.getCoPoNumber();
            coSupplierNameTemp = cashOutModel.getCoSupplier();
            coTotalTemp = cashOutModel.getCoTotal();
            coTotal = "IDR " + currencyFormat(String.valueOf(coTotalTemp));
            coStatusApproval = cashOutModel.getCoStatusApproval();
            coStatusPayment = cashOutModel.getCoStatusPayment();

            tvDateCreated.setText(coDateCreated);
            tvCoUID.setText(coUID);
            tvPoNumber.setText(coPoUID);
            tvPoNumber.setText(coPoUID);
            db.collection("SupplierData").whereEqualTo("supplierID", coSupplierNameTemp).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            SupplierModel supplierModel = documentSnapshot.toObject(SupplierModel.class);
                            supplierModel.setSupplierID(documentSnapshot.getId());
                            coSupplierName = supplierModel.getSupplierName();
                        }
                        tvSupplierName.setText(coSupplierName);
                    });
            tvTotalDue.setText(coTotal);
            if (coStatusApproval){
                llWrapItemStatus.setVisibility(View.VISIBLE);
                llStatusApproved.setVisibility(View.VISIBLE);
                rlBtnItemPaid.setVisibility(View.VISIBLE);
                rlBtnApproveItem.setVisibility(View.GONE);
                if (coStatusPayment){
                    llStatusPaid.setVisibility(View.VISIBLE);
                    rlBtnItemPaid.setVisibility(View.GONE);
                } else {
                    llStatusPaid.setVisibility(View.GONE);
                    rlBtnItemPaid.setVisibility(View.VISIBLE);
                }
            } else {
                llWrapItemStatus.setVisibility(View.GONE);
                llStatusApproved.setVisibility(View.GONE);
                rlBtnItemPaid.setVisibility(View.GONE);
                rlBtnApproveItem.setVisibility(View.VISIBLE);
            }

            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.deleteCoConfirm(context, coDocumentID);
                }
            });

            btnShowItemDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, UpdateCashOutActivity.class);
                    i.putExtra("key", coDocumentID);
                    context.startActivity(i);
                    //Toast.makeText(context, coDocumentID, Toast.LENGTH_SHORT).show();
                }
            });

            btnPrintItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> selectedItems = Arrays.asList(context.getResources().getStringArray(R.array.choosePrint));
                    boolean[] checkedItemArrayInit = new boolean[]{
                            true,
                            false
                    };

                    AlertDialog.Builder alertChoosePrint = new AlertDialog.Builder(context);
                    alertChoosePrint.setTitle("Pilih yang ingin dicetak");
                    alertChoosePrint.setCancelable(true);
                    alertChoosePrint.setMultiChoiceItems(R.array.choosePrint, checkedItemArrayInit, new android.content.DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialogInterface, int i, boolean b) {
                            checkedItemArrayInit[i] = b;
                            //String currentItem = selectedItems.get(i);
                        }
                    });

                    alertChoosePrint.setPositiveButton("CETAK", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialogInterface, int i) {
                            for (int j=0; j<checkedItemArrayInit.length; j++){
                                boolean checked = checkedItemArrayInit[j];
                                if (checked){
                                    if (selectedItems.get(j).equals("Cash Out")){
                                        if (ContextCompat.checkSelfPermission(context,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                                                PackageManager.PERMISSION_GRANTED){
                                            ActivityCompat.requestPermissions((Activity) context,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                                        } else{
                                            Toast.makeText(context, "Under development", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //Toast.makeText(context, selectedItems.get(j), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    alertChoosePrint.setNegativeButton("BATAL", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialogInterface, int i) {

                        }
                    });

                    alertChoosePrint.show();
                }
            });

            btnItemPaid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.coPaidConfirm(context, coDocumentID);
                }
            });

            btnApproveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.approveCoConfirm(context, coDocumentID);
                }
            });


            //!!TODO CREATE OPTION TO PRINT FROM LIST ITEM
           /* fabCreateCOR.setOnClickListener(view -> {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                } else {
                    if (!custNameVal.isEmpty()){
                        for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                            totalUnit += giManagementAdapter.getSelected().get(i).getGiVhlCubication();
                        }

                        double totalIDR = matBuyPrice *Double.parseDouble(df.format(totalUnit));

                        String coDateCreated = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());

                        String coTimeCreated =
                                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                        List<String> datePeriod = new ArrayList<>();
                        for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                            datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
                        }
                        HashSet<String> filter = new HashSet(datePeriod);
                        ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);

                        coDateDeliveryPeriod = String.valueOf(datePeriodFiltered);

                        dialogInterface.confirmCreateCashOutProof(context, db, goodIssueModelArrayList,
                                coUID, coDateCreated + " | " + coTimeCreated + " WIB",
                                helper.getUserId(), "-","-", "-", "-",
                                suppplieruidVal, roPoCustNumber, coDateDeliveryPeriod, false, false, totalIDR);

                        String custNameValReplace = custNameVal.replace(" - ","-");
                        int indexCustNameVal = custNameValReplace.lastIndexOf('-');
                        db.collection("CustomerData").whereEqualTo("custUID", custNameValReplace.substring(0, indexCustNameVal)).get()
                                .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                    for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                        CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                        custAddressVal = customerModel.getCustAddress();
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Mohon cari data Good Issue terlebih dahulu.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            // CREATE GI MANAGEMENT ADAPTER
            giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);
*/
        }
    }



    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public void clearSelection() {
        for (int i = 0; i < cashOutModelArrayList.size(); i++) {
            cashOutModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<CashOutModel> getSelected() {
        ArrayList<CashOutModel> selected = new ArrayList<>();
        for (int i = 0; i < cashOutModelArrayList.size(); i++) {
            if (cashOutModelArrayList.get(i).isChecked()) {
                selected.add(cashOutModelArrayList.get(i));
            }
        }
        return selected;
    }
}
