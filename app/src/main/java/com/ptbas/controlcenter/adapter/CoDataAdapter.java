package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.model.CoModel;
import com.ptbas.controlcenter.model.GiModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.model.SplrModel;
import com.ptbas.controlcenter.update.UpdtCoActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CoDataAdapter extends RecyclerView.Adapter<CoDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<CoModel> coModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;

    public CoDataAdapter(Context context, ArrayList<CoModel> coModelArrayList) {
        this.context = context;
        this.coModelArrayList = coModelArrayList;
    }

    @NonNull
    @Override
    public CoDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_cash_out_desktop, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoDataAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(coModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return coModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LinearLayout llWrapItemStatus, llStatusApproved, llStatusPaid;
        TextView tvCubication, tvCustomerName, tvMatName, tvDateCreated, tvTotalDue, tvCoUID, tvPoType, tvSupplierName, tvDateDeliveryPeriod;
        Button btnDeleteItem, btnShowItemDetail, btnItemPaid;
        RelativeLayout rlBtnDeleteItem, rlOpenItemDetail, rlBtnItemPaid, rlBtnApproveItem;
        CheckBox cbSelectItem;

        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat dfRound = new DecimalFormat("0");

        boolean coStatusApproval, coStatusPayment;
        String coAccBy, coSupplierName, coDateCreated, coUID, coDocumentID, coPoUID, coSupplierNameTemp, coTotal;

        double matBuyPrice;
        List<ProductItems> productItemsList;
        double totalUnit = 0;
        ArrayList<GiModel> giModelArrayList = new ArrayList<>();

        String matNameVal, poType, roCustName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            llWrapItemStatus = itemView.findViewById(R.id.llWrapItemStatus);
            llStatusApproved = itemView.findViewById(R.id.llStatusApproved);
            llStatusPaid = itemView.findViewById(R.id.llStatusPaid);

            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvTotalDue = itemView.findViewById(R.id.tvTotalDue);
            tvCoUID = itemView.findViewById(R.id.tvCoUID);
            tvPoType = itemView.findViewById(R.id.tvPoType);
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);

            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            btnShowItemDetail = itemView.findViewById(R.id.btnOpenItemDetail);
            btnItemPaid = itemView.findViewById(R.id.btnItemPaid);

            rlBtnDeleteItem = itemView.findViewById(R.id.rlBtnDeleteItem);
            rlOpenItemDetail = itemView.findViewById(R.id.rlBtnOpenItemDetail);
            rlBtnItemPaid  = itemView.findViewById(R.id.rlBtnItemPaid);
            rlBtnApproveItem = itemView.findViewById(R.id.rlBtnApproveItem);

            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);

            tvDateDeliveryPeriod = itemView.findViewById(R.id.tvDateDeliveryPeriod);
            tvCubication = itemView.findViewById(R.id.tvCubication);
            tvMatName = itemView.findViewById(R.id.tvMatName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
        }

        public void viewBind(CoModel coModel) {
            btnItemPaid.setVisibility(View.GONE);
            rlBtnItemPaid.setVisibility(View.GONE);
            rlBtnApproveItem.setVisibility(View.GONE);
            rlBtnApproveItem.setVisibility(View.GONE);
            cbSelectItem.setChecked(false);
            dialogInterfaceUtils = new DialogInterfaceUtils();

            dfRound.setRoundingMode(RoundingMode.HALF_UP);

            itemView.setOnClickListener(view -> {
                coModel.setChecked(!coModel.isChecked());
                cbSelectItem.setChecked(coModel.isChecked());
            });

            cbSelectItem.setOnClickListener(view -> {
                coModel.setChecked(!coModel.isChecked());
                cbSelectItem.setChecked(coModel.isChecked());
            });

            coDateCreated = coModel.getCoDateAndTimeCreated();
            coUID = coModel.getCoUID();

            tvDateDeliveryPeriod.setText(coModel.getCoDateDeliveryPeriod());

            CollectionReference refCust = db.collection("CustomerData");

            db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", coModel.getRoDocumentID()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            RoModel roModel = documentSnapshot.toObject(RoModel.class);
                            coPoUID = roModel.getRoPoCustNumber();
                            roCustName = roModel.getCustDocumentID();

                            HashMap<String, List<ProductItems>> map = roModel.getRoOrderedItems();
                            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                productItemsList = e.getValue();
                                for (int i = 0; i<productItemsList.size();i++){
                                    if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                        //transportServiceNameVal = productItemsList.get(0).getMatName();
                                        //transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();
                                    } else {
                                        matNameVal = productItemsList.get(i).getMatName();
                                        matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                        //matQuantity = productItemsList.get(i).getMatQuantity();
                                    }
                                    if (productItemsList.size()>1){
                                        matNameVal = productItemsList.get(1).getMatName();
                                    }
                                }
                            }

                            if (roModel.getRoType().equals(0)){
                                poType = "JASA ANGKUT + MATERIAL";
                            }
                            if (roModel.getRoType().equals(1)){
                                poType = "MATERIAL SAJA";
                            }
                            if (roModel.getRoType().equals(2)){
                                poType = "JASA ANGKUT SAJA";
                            }

                            tvPoType.setText(roModel.getRoMatType()+ " | " +poType);
                            tvMatName.setText(matNameVal);


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                            Query query = databaseReference.child("GoodIssueData");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot item : snapshot.getChildren()) {
                                            if (Objects.equals(item.child("giCashedOutTo").getValue(), coModel.getCoDocumentID())) {
                                                GiModel giModel = item.getValue(GiModel.class);
                                                giModelArrayList.add(giModel);
                                            }
                                        }
                                    }
                                    for (int i = 0; i < giModelArrayList.size(); i++) {
                                        totalUnit += giModelArrayList.get(i).getGiVhlCubication();
                                    }

                                    //Toast.makeText(context, "buy "+matBuyPrice+" - unit"+totalUnit, Toast.LENGTH_LONG).show();
                                    double totalIDR = matBuyPrice * totalUnit;
                                    coTotal = "IDR " + currencyFormat(dfRound.format(totalIDR));
                                    tvTotalDue.setText(coTotal);

                                    tvCubication.setText(Html.fromHtml(df.format(totalUnit) +" m\u00B3"));
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        tvSupplierName.setText(coSupplierName);





                        refCust.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(roCustName)){
                                        CustModel custModel = documentSnapshot.toObject(CustModel.class);
                                        tvCustomerName.setText(custModel.getCustName());
                                    }
                                }
                            }
                        });
                    });


            coDocumentID = coModel.getCoDocumentID();
            coSupplierNameTemp = coModel.getCoSupplier();
            coStatusApproval = coModel.getCoStatusApproval();
            coStatusPayment = coModel.getCoStatusPayment();
            coAccBy = coModel.getCoAccBy();

            tvDateCreated.setText(coDateCreated);
            tvCoUID.setText(coUID);
            db.collection("SupplierData").whereEqualTo("supplierID", coSupplierNameTemp).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            SplrModel splrModel = documentSnapshot.toObject(SplrModel.class);
                            splrModel.setSupplierID(documentSnapshot.getId());
                            coSupplierName = splrModel.getSupplierName();
                        }
                        tvSupplierName.setText(coSupplierName);
                    });



            if (!coAccBy.isEmpty()){
                llWrapItemStatus.setVisibility(View.VISIBLE);
                llStatusApproved.setVisibility(View.VISIBLE);

            } else {
                llWrapItemStatus.setVisibility(View.GONE);
                llStatusApproved.setVisibility(View.GONE);
            }

            btnDeleteItem.setOnClickListener(view -> dialogInterfaceUtils.deleteCoConfirm(context, coDocumentID));

            btnShowItemDetail.setOnClickListener(view -> {
                Intent i = new Intent(context, UpdtCoActivity.class);
                i.putExtra("key", coDocumentID);
                context.startActivity(i);
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
        for (int i = 0; i < coModelArrayList.size(); i++) {
            coModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<CoModel> getSelected() {
        ArrayList<CoModel> selected = new ArrayList<>();
        for (int i = 0; i < coModelArrayList.size(); i++) {
            if (coModelArrayList.get(i).isChecked()) {
                selected.add(coModelArrayList.get(i));
            }
        }
        return selected;
    }
}
