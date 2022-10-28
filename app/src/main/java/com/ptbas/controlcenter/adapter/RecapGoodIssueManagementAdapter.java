package com.ptbas.controlcenter.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Html;
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
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RecapGIModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.SupplierModel;
import com.ptbas.controlcenter.update.UpdateCashOutActivity;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RecapGoodIssueManagementAdapter extends RecyclerView.Adapter<RecapGoodIssueManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<RecapGIModel> recapGoodIssueModelArrayList;
    DialogInterface dialogInterface;

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
        TextView tvCubication, tvDateCreated, tvPoCustNumber, tvRcpGiUID, tvMatNTransportType, tvCustomerName;
        CheckBox cbSelectItem;
        String roPoUID, roCustName;

        List<ProductItems> productItemsList;
        double matSellPrice, matQuantity, transportServiceSellPrice;
        String matNameVal, matDetail;

        float totalUnit = 0;

        ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            tvCubication = itemView.findViewById(R.id.tvCubication);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvRcpGiUID = itemView.findViewById(R.id.tvRcpGiUID);
            tvPoCustNumber = itemView.findViewById(R.id.tvPoCustNumber);
            tvMatNTransportType = itemView.findViewById(R.id.tvMatNTransportType);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
        }

        public void viewBind(RecapGIModel recapGIModel) {
            dialogInterface = new DialogInterface();

            cbSelectItem.setChecked(false);

            String rcpDocumentID = recapGIModel.getRcpGiDocumentID();

            DecimalFormat df = new DecimalFormat("0.00");


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            Query query = databaseReference.child("GoodIssueData");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        for (DataSnapshot item : snapshot.getChildren()) {
                            if (Objects.equals(item.child("giRecappedTo").getValue(), rcpDocumentID)) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                goodIssueModelArrayList.add(goodIssueModel);
                            }

                        }
                        for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                            totalUnit += goodIssueModelArrayList.get(i).getGiVhlCubication();
                        }

                        tvCubication.setText(Html.fromHtml(df.format(totalUnit) +" m\u00B3"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });







            /*DecimalFormat df = new DecimalFormat("0.00");
            float cubication = recapGIModel.getRoCubication();
            tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));*/



            tvRcpGiUID.setText(recapGIModel.getRcpGiUID());

            CollectionReference refCust = db.collection("CustomerData");

            db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", recapGIModel.getRoDocumentID()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            //receivedOrderModel.setRoDocumentID(documentSnapshot.getId());
                            roPoUID = receivedOrderModel.getRoPoCustNumber();
                            roCustName = receivedOrderModel.getCustDocumentID();


                            HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                productItemsList = e.getValue();
                                for (int i = 0; i<productItemsList.size();i++){
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matSellPrice = productItemsList.get(i).getMatBuyPrice();
                                    matQuantity = productItemsList.get(i).getMatQuantity();
                                }

                            }

                            matDetail = receivedOrderModel.getRoMatType()+" - "+matNameVal;
                            tvMatNTransportType.setText(matDetail);
                        }
                        tvPoCustNumber.setText("PO: "+roPoUID);

                        refCust.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(roCustName)){
                                        CustomerModel customerModel = documentSnapshot.toObject(CustomerModel.class);
                                        tvCustomerName.setText(customerModel.getCustName());

                            /*String custTaxStatus;
                            if (customerModel.getCustNPWP().equals("")||customerModel.getCustNPWP().isEmpty()){
                                custTaxStatus = "NON PKP";
                            } else {
                                custTaxStatus = "PKP";
                            }*/

                           /* tvRoMatSellPriceCubicAndTaxType.setText(custTaxStatus + " | " + currency
                                    + " " +
                                    currencyFormat(df.format(matSellPrice))
                                    + "/m3 | " +
                                    currencyFormat(df.format(matQuantity))+ " m3");*/
                                    }
                                }
                            }
                        });

                    });





            tvDateCreated.setText(recapGIModel.getRcpGiDateAndTimeCreated());

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



        }
    }



    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
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
}
