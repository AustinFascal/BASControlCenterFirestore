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
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.update.UpdateRecapActivity;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RecapGIModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RecapGoodIssueManagementAdapter extends RecyclerView.Adapter<RecapGoodIssueManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<RecapGIModel> recapGoodIssueModelArrayList;
    DialogInterface dialogInterface;
    public boolean isSelectedAll = false;

    public RecapGoodIssueManagementAdapter(Context context, ArrayList<RecapGIModel> recapGoodIssueModelArrayList) {
        this.context = context;
        this.recapGoodIssueModelArrayList = recapGoodIssueModelArrayList;
    }

    @NonNull
    @Override
    public RecapGoodIssueManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_recap_gi_desktop, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecapGoodIssueManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(recapGoodIssueModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return recapGoodIssueModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView tvCubication, tvTotalRecap, tvMatName, tvDateCreated,tvDateDeliveryPeriod, tvRcpGiUID, tvCustomerName, tvRoType;

        CheckBox cbSelectItem;
        String roPoUID, roCustName, poType, currency;
        Button btnDeleteItem, btnOpenItemDetail;

        Helper helper;

        List<ProductItems> productItemsList;
        double matBuyPrice, matQuantity, transportServiceSellPrice;
        String matNameVal, matDetail;

        float totalUnit = 0;

        LinearLayout llWrapItemStatus, llStatusApproved;

        ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            tvMatName = itemView.findViewById(R.id.tvMatName);
            tvCubication = itemView.findViewById(R.id.tvCubication);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvDateDeliveryPeriod = itemView.findViewById(R.id.tvDateDeliveryPeriod);
            tvRcpGiUID = itemView.findViewById(R.id.tvRcpGiUID);
            tvRoType = itemView.findViewById(R.id.tvRoType);
            tvTotalRecap = itemView.findViewById(R.id.tvTotalRecap);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);

            llWrapItemStatus = itemView.findViewById(R.id.llWrapItemStatus);
            llStatusApproved = itemView.findViewById(R.id.llStatusApproved);

            btnOpenItemDetail = itemView.findViewById(R.id.btnOpenItemDetail);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        }

        public void viewBind(RecapGIModel recapGIModel) {
            dialogInterface = new DialogInterface();

            cbSelectItem.setChecked(false);

            String rcpDocumentID = recapGIModel.getRcpGiDocumentID();
            String rcpGiCoUID = recapGIModel.getRcpGiCoUID();

            DecimalFormat df = new DecimalFormat("0.00");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            Query query = databaseReference.child("GoodIssueData");
            query.keepSynced(false);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot item : snapshot.getChildren()) {
                            if (Objects.equals(item.child("giRecappedTo").getValue(), rcpDocumentID)) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                goodIssueModelArrayList.add(goodIssueModel);

                                if (!rcpGiCoUID.isEmpty()){
                                    databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giCashedOutTo").setValue(rcpGiCoUID);
                                    databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giCashedOut").setValue(true);
                                }

                            }
                        }
                    }
                    for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                        totalUnit += goodIssueModelArrayList.get(i).getGiVhlCubication();
                    }
                    tvCubication.setText(Html.fromHtml(df.format(totalUnit) +" m\u00B3"));

                    CollectionReference refCust = db.collection("CustomerData");

                    db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", recapGIModel.getRoDocumentID()).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                    ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                    roPoUID = receivedOrderModel.getRoPoCustNumber();
                                    roCustName = receivedOrderModel.getCustDocumentID();
                                    currency = receivedOrderModel.getRoCurrency();
                                    HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                                    for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                        productItemsList = e.getValue();
                                        for (int i = 0; i<productItemsList.size();i++){
                                            if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){

                                            } else {
                                                matNameVal = productItemsList.get(i).getMatName();
                                                matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                                matQuantity = productItemsList.get(i).getMatQuantity();
                                            }
                                            if (productItemsList.size()>1){
                                                matNameVal = productItemsList.get(1).getMatName();
                                            }
                                        }

                                        double totalRecap = matBuyPrice *Double.parseDouble(df.format(totalUnit));
                                        tvTotalRecap.setText(currency +" "+ currencyFormat(df.format(totalRecap)));

                                    }

                                    if (receivedOrderModel.getRoType().equals(0)){
                                        poType = "JASA ANGKUT + MATERIAL";
                                    }
                                    if (receivedOrderModel.getRoType().equals(1)){
                                        poType = "MATERIAL SAJA";
                                    }
                                    if (receivedOrderModel.getRoType().equals(2)){
                                        poType = "JASA ANGKUT SAJA";
                                    }

                                    tvRoType.setText(receivedOrderModel.getRoMatType()+ " | " +poType);
                                    tvMatName.setText(matNameVal);
                                }

                                refCust.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()){
                                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                                            String getDocumentID = documentSnapshot.getId();
                                            if (getDocumentID.equals(roCustName)){
                                                CustomerModel customerModel = documentSnapshot.toObject(CustomerModel.class);
                                                tvCustomerName.setText(customerModel.getCustName());
                                            }
                                        }
                                    }
                                });

                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            tvRcpGiUID.setText(recapGIModel.getRcpGiUID());

            tvDateCreated.setText(recapGIModel.getRcpGiDateAndTimeCreated());

            tvDateDeliveryPeriod.setText(recapGIModel.getRcpDateDeliveryPeriod());
            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recapGIModel.setChecked(!recapGIModel.isChecked());
                    cbSelectItem.setChecked(recapGIModel.isChecked());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recapGIModel.setChecked(!recapGIModel.isChecked());
                    cbSelectItem.setChecked(recapGIModel.isChecked());
                }
            });

            if (!rcpGiCoUID.isEmpty()){
                llStatusApproved.setVisibility(View.VISIBLE);
                llStatusApproved.setVisibility(View.VISIBLE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                llStatusApproved.setVisibility(View.GONE);
            }

            btnOpenItemDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, UpdateRecapActivity.class);
                    i.putExtra("key", rcpDocumentID);
                    context.startActivity(i);
                }
            });

            btnDeleteItem.setOnClickListener(view ->
                    dialogInterface.deleteRcpConfirmation(context, rcpDocumentID));


        }
    }



    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public void selectAll(){
        isSelectedAll=true;
        for (int i = 0; i < recapGoodIssueModelArrayList.size(); i++) {
            recapGoodIssueModelArrayList.get(i).setChecked(true);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        for (int i = 0; i < recapGoodIssueModelArrayList.size(); i++) {
            recapGoodIssueModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<RecapGIModel> getSelected() {
        ArrayList<RecapGIModel> selected = new ArrayList<>();
        for (int i = 0; i < recapGoodIssueModelArrayList.size(); i++) {
            if (recapGoodIssueModelArrayList.get(i).isChecked()) {
                selected.add(recapGoodIssueModelArrayList.get(i));
            }
        }
        return selected;
    }

    public float getSelectedVolume() {
        float selected = 0;
        for (int i = 0; i < recapGoodIssueModelArrayList.size(); i++) {
            if (recapGoodIssueModelArrayList.get(i).isChecked()) {
                selected += recapGoodIssueModelArrayList.get(i).getRcpGiCubication();
                if (!recapGoodIssueModelArrayList.get(i).isChecked()){
                    selected -= recapGoodIssueModelArrayList.get(i).getRcpGiCubication();
                }
            }
        }
        return selected;
    }
}
