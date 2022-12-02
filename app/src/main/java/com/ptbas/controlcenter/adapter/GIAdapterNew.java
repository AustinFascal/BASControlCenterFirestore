package com.ptbas.controlcenter.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utility.Helper;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GIAdapterNew extends RecyclerView.Adapter<GIAdapterNew.ItemViewHolder> {

    Context context;
    ArrayList<GoodIssueModel> goodIssueModelArrayList;
    Helper helper = new Helper();
    ItemViewHolder itemViewHolder;
    public boolean isSelectedAll = false;
    GoodIssueModel[] goodIssueModels;

    GIAdapterNew giManagementAdapter;


    public GIAdapterNew(Context context, ArrayList<GoodIssueModel> goodIssueModelArrayList) {
        this.context = context;
        this.goodIssueModelArrayList = goodIssueModelArrayList;
    }

    @NonNull
    @Override
    public GIAdapterNew.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(context.getApplicationContext().getResources().getDisplayMetrics());
        int width = context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        if (width<=1080){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_good_issue, parent, false);
            itemViewHolder = new ItemViewHolder(itemView);
        }
        if (width>1080){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_good_issue_desktop, parent, false);
            itemViewHolder = new ItemViewHolder(itemView);
        }
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.viewBind(goodIssueModelArrayList.get(position));

        if (!isSelectedAll) {
            holder.cbSelectItem.setChecked(false);
        } else {
            holder.cbSelectItem.setChecked(true);
        }
        giManagementAdapter = new GIAdapterNew(context, goodIssueModelArrayList);

        final GoodIssueModel goodIssueModel = goodIssueModels[position];
        holder.setItemClickListener(new ItemViewHolder.ItemClickListener() {
            @Override
            public void onItemCLick(View v, int pos) {
                CheckBox cbSelectItem = (CheckBox) v;
                GoodIssueModel currentGoodIssue = goodIssueModels[pos];

                if(cbSelectItem.isChecked()){
                    currentGoodIssue.setChecked(true);
                    goodIssueModelArrayList.add(currentGoodIssue);
                } else if (!cbSelectItem.isChecked()){
                    currentGoodIssue.setChecked(false);
                    goodIssueModelArrayList.remove(currentGoodIssue);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodIssueModelArrayList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        GoodIssueModel goodIssueModel2;

        LinearLayout llStatusApproved, llStatusRecapped, llStatusInvoiced, llCashedOutStatus, llRoNeedsUpdate, llHiddenView, llWrapGiStatus;
        TextView tvCubication, tvGiDateTime, tvGiUid, tvRoUid, tvGiMatDetail, tvGiVhlDetail,
                tvVhlUid, tvPoCustNumber, tvCustomerName;
        RelativeLayout btnDeleteGi, btnApproveGi, btnCloneGi;
        Button btn1, btn2, btn3, btn4;
        CardView cardView;
        CheckBox cbSelectItem;
        String custDocumentID, coAccBy;

        DecimalFormat df = new DecimalFormat("0.00");

        //final View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);

        /*FloatingActionsMenu fabExpandMenu = rootView.findViewById(R.id.fab_expand_menu);
        TextView tvTotalSelectedItem = rootView.findViewById(R.id.tvTotalSelectedItem);
        TextView tvTotalSelectedItem2 = rootView.findViewById(R.id.tvTotalSelectedItem2);
        LinearLayout llBottomSelectionOptions = rootView.findViewById(R.id.llBottomSelectionOptions);*/




        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cdvItem);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            llWrapGiStatus = itemView.findViewById(R.id.llWrapItemStatus);
            llStatusApproved = itemView.findViewById(R.id.llStatusApproved);
            llCashedOutStatus = itemView.findViewById(R.id.llCashedOutStatus);
            llStatusRecapped = itemView.findViewById(R.id.llStatusRecapped);
            llStatusInvoiced = itemView.findViewById(R.id.ll_status_invoiced);
            llRoNeedsUpdate = itemView.findViewById(R.id.ll_ro_needs_update);
            tvCubication = itemView.findViewById(R.id.tv_cubication);
            tvGiDateTime = itemView.findViewById(R.id.tvDateCreated);
            tvGiUid = itemView.findViewById(R.id.tv_gi_uid);
            tvRoUid = itemView.findViewById(R.id.tvCoTotal);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            tvGiMatDetail = itemView.findViewById(R.id.tv_gi_mat_detail);
            tvGiVhlDetail = itemView.findViewById(R.id.tv_gi_vhl_detail);
            tvVhlUid = itemView.findViewById(R.id.tv_vhl_uid);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            btnDeleteGi = itemView.findViewById(R.id.btn_delete_gi);
            btnApproveGi = itemView.findViewById(R.id.btn_approve_gi);
            btnCloneGi = itemView.findViewById(R.id.btn_clone_gi);
            btn1 = itemView.findViewById(R.id.btnDeleteItem);
            btn2 = itemView.findViewById(R.id.btnApproveItem);
            btn3 = itemView.findViewById(R.id.btnOpenItemDetail);
            btn4 = itemView.findViewById(R.id.btnClone);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);


            cbSelectItem.setOnClickListener(this);
        }

        public void viewBind(final GoodIssueModel goodIssueModel) {
            goodIssueModel2 = goodIssueModel;

            cbSelectItem.setChecked(false);
            tvRoUid.setVisibility(View.INVISIBLE);

            /*if (Objects.equals(helper.ACTIVITY_NAME, "UPDATE")){
                btnDeleteGi.setVisibility(View.GONE);
            }

            if (helper.UPDATE_GOOD_ISSUE_IN_INVOICE){
                btnCloneGi.setVisibility(View.VISIBLE);
            } else{
                btnCloneGi.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(view -> {
                itemCount();
            });

            cbSelectItem.setOnClickListener(view -> {
                itemCount();
            });*/





            DecimalFormat df = new DecimalFormat("0.00");
            Double cubication = goodIssueModel.getGiVhlCubication();
            String dateNTime = goodIssueModel.getGiDateCreated()+" | "+goodIssueModel.getGiTimeCreted() + " WIB";

            String[] partGiUID = goodIssueModel.getGiUID().split("-");
            String giUID = partGiUID[0];

            String roDocumentID = goodIssueModel.getRoDocumentID();

            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID)
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            if (!value.isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {
                                    ReceivedOrderModel receivedOrderModel = d.toObject(ReceivedOrderModel.class);

                                    assert receivedOrderModel != null;
                                    String roNumber = receivedOrderModel.getRoUID();
                                    String poNumber = receivedOrderModel.getRoPoCustNumber();
                                    custDocumentID = receivedOrderModel.getCustDocumentID();

                                    tvPoCustNumber.setText("PO: " + poNumber);

                                    db1.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID)
                                            .addSnapshotListener((value2, error2) -> {
                                                if (value2 != null) {
                                                    if (!value2.isEmpty()) {
                                                        for (DocumentSnapshot e : value2.getDocuments()) {
                                                            CustomerModel customerModel = e.toObject(CustomerModel.class);
                                                            String customerName = customerModel.getCustName();
                                                            String customerAlias = customerModel.getCustUID();

                                                            tvCustomerName.setText(customerAlias+" - "+customerName);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });

            FirebaseFirestore db0 = FirebaseFirestore.getInstance();

            CollectionReference refCust = db0.collection("CustomerData");

            refCust.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String getDocumentID = documentSnapshot.getId();
                    }
                }
            });

            String matDetail = goodIssueModel.getGiMatType()+" - "+goodIssueModel.getGiMatName();
            String vhlDetail = "(P) "+goodIssueModel.getVhlLength().toString()+" (L) "+goodIssueModel.getVhlWidth().toString()+" (T) "+goodIssueModel.getVhlHeight().toString()+" | "+"(K) "+goodIssueModel.getVhlHeightCorrection().toString()+" (TK) "+goodIssueModel.getVhlHeightAfterCorrection().toString();
            String vhlUID = goodIssueModel.getVhlUID();
            boolean giStatus = goodIssueModel.getGiStatus();
            String giRecappedTo = goodIssueModel.getGiRecappedTo();
            boolean giInvoiced = goodIssueModel.getGiInvoiced();
            String giInvoicedTo = goodIssueModel.getGiInvoicedTo();
            boolean giCashedOut = goodIssueModel.getGiCashedOut();
            String giCashedOutTo = goodIssueModel.getGiCashedOutTo();

            tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));
            tvGiDateTime.setText(dateNTime);
            tvGiUid.setText(giUID);
            tvGiMatDetail.setText(matDetail);
            tvGiVhlDetail.setText(vhlDetail);
            tvVhlUid.setText(vhlUID);

            String giUIDVal =goodIssueModel.getGiUID();

            if (goodIssueModel.getGiMatName().contentEquals("JASA ANGKUT")){
                tvVhlUid.setVisibility(View.GONE);
                tvGiVhlDetail.setVisibility(View.GONE);
            }

            DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (giStatus){
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveGi.setVisibility(View.GONE);
            } else {
                llStatusApproved.setVisibility(View.GONE);
                btnApproveGi.setVisibility(View.VISIBLE);
            }

            if (!giRecappedTo.isEmpty()){
                llStatusRecapped.setVisibility(View.VISIBLE);
            } else {
                llStatusRecapped.setVisibility(View.GONE);
            }

            if (!giInvoicedTo.isEmpty()){
                llStatusInvoiced.setVisibility(View.VISIBLE);
            } else {
                llStatusInvoiced.setVisibility(View.GONE);
            }

            if (giCashedOut){
                llCashedOutStatus.setVisibility(View.VISIBLE);
            } else {
                llCashedOutStatus.setVisibility(View.GONE);
            }


            /*db.collection("CashOutData").whereEqualTo("coDocumentID", giCashedOutTo).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    CashOutModel cashOutModel = documentSnapshot.toObject(CashOutModel.class);
                                    cashOutModel.setCoDocumentID(documentSnapshot.getId());
                                    coAccBy = cashOutModel.getCoAccBy();
                                    if (!coAccBy.isEmpty()){
                                        llCashedOutStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.pill_green));
                                    } else {
                                        llCashedOutStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.pill_red));
                                    }
                                }
                            }
                    );

            btn4.setOnClickListener(view -> {



                if (tvGiUid.getText().toString().contains("CL")){
                    Toast.makeText(context, "Tidak dapat menduplikat GI karena GI ini merupakah hasil duplikasi.", Toast.LENGTH_SHORT).show();
                } else{
                    GoodIssueModel goodIssueModelClone = new GoodIssueModel("CL-"+goodIssueModel.getGiUID(), goodIssueModel.getGiCreatedBy(), goodIssueModel.getGiVerifiedBy(),goodIssueModel.getRoDocumentID(),
                            goodIssueModel.getGiMatName(), goodIssueModel.getGiMatType(), goodIssueModel.getGiNoteNumber(), vhlUID, goodIssueModel.getGiDateCreated(), goodIssueModel.getGiTimeCreted(), goodIssueModel.getVhlLength(),
                            goodIssueModel.getVhlWidth(), goodIssueModel.getVhlHeight(), goodIssueModel.getVhlHeightCorrection(), goodIssueModel.getVhlHeightAfterCorrection(), goodIssueModel.getGiVhlCubication(), giStatus, false, giInvoiced, goodIssueModel.getGiInvoicedTo(), goodIssueModel.getGiCashedOut(), goodIssueModel.getGiCashedOutTo(), goodIssueModel.getGiRecappedTo());
                    DatabaseReference refGI = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData");
                    refGI.child("CL-"+goodIssueModel.getGiUID()).setValue(goodIssueModelClone).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Berhasil diduplikat", Toast.LENGTH_SHORT).show();
                        } else {
                            try{
                                throw task.getException();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    GoodIssueModel goodIssueModelUpdate = new GoodIssueModel(goodIssueModel.getGiUID(), goodIssueModel.getGiCreatedBy(), goodIssueModel.getGiVerifiedBy(),goodIssueModel.getRoDocumentID(),
                            goodIssueModel.getGiMatName(), goodIssueModel.getGiMatType(), goodIssueModel.getGiNoteNumber(), vhlUID, goodIssueModel.getGiDateCreated(), goodIssueModel.getGiTimeCreted(), goodIssueModel.getVhlLength(),
                            goodIssueModel.getVhlWidth(), goodIssueModel.getVhlHeight(), goodIssueModel.getVhlHeightCorrection(), goodIssueModel.getVhlHeightAfterCorrection(), goodIssueModel.getGiVhlCubication(), giStatus, false, giInvoiced, "ARC-"+goodIssueModel.getGiInvoicedTo(), goodIssueModel.getGiCashedOut(), goodIssueModel.getGiCashedOutTo(), goodIssueModel.getGiRecappedTo());
                    refGI.child(goodIssueModel.getGiUID()).setValue(goodIssueModelUpdate).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Berhasil diduplikat", Toast.LENGTH_SHORT).show();
                        } else {
                            try{
                                throw task.getException();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            });

            btn3.setOnClickListener(view -> {
                String giUID1 =goodIssueModel.getGiUID();
                Intent i = new Intent(context, UpdateGoodIssueActivity.class);
                i.putExtra("key", giUID1);
                context.startActivity(i);
            });

            btn2.setOnClickListener(view -> {
                if (tvPoCustNumber.getText().toString().equals("PO: -")){
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }
                    MaterialDialog md = new MaterialDialog.Builder((Activity) context)
                            .setAnimation(R.raw.lottie_attention)
                            .setTitle("Perhatian!", TextAlignment.START)
                            .setMessage("Data Good Issue ini masih belum memiliki nomor PO. Mohon perbarui data tersebut agar dapat melakukan validasi dan dapat muncul saat direkapitulasi.", TextAlignment.START)
                            .setPositiveButton("OKE", (dialogInterface, which) -> dialogInterface.dismiss())
                            .setCancelable(true)
                            .build();
                    md.show();
                } else {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }

                    MaterialDialog md = new MaterialDialog.Builder((Activity) context)
                            .setAnimation(R.raw.lottie_approval)
                            .setTitle("Validasi Data")
                            .setMessage("Apakah Anda yakin ingin mengesahkan data Good Issue yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                            .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giStatus").setValue(true);
                                databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giVerifiedBy").setValue(helper.getUserId());
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                            .setCancelable(true)
                            .build();

                    md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    md.show();
                }
            });

            btn1.setOnClickListener(view -> {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }

                MaterialDialog md = new MaterialDialog.Builder((Activity) context)
                        .setTitle("Hapus Data")
                        .setAnimation(R.raw.lottie_delete)
                        .setMessage("Apakah Anda yakin ingin menghapus data Good Issue yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                        .setCancelable(true)
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).removeValue();
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            });*/

        }

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener ic){
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View v){
            this.itemClickListener.onItemCLick(v,getLayoutPosition());
        }

        interface ItemClickListener{
            void onItemCLick(View v, int pos);
        }

/*
        private void itemCount() {
            goodIssueModel2.setChecked(!goodIssueModel2.isChecked());
            cbSelectItem.setChecked(goodIssueModel2.isChecked());

            int itemSelectedSize = giManagementAdapter.getSelected().size();
            float itemSelectedVolume = giManagementAdapter.getSelectedVolume();

            String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
            if (giManagementAdapter.getSelected().size() > 0) {

                fabExpandMenu.animate().translationY(800).setDuration(100).start();
                fabExpandMenu.collapse();

                tvTotalSelectedItem.setText(itemSelectedSize + " item terpilih");
                tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);

                llBottomSelectionOptions.animate()
                        .translationY(0).alpha(1.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                llBottomSelectionOptions.setVisibility(View.VISIBLE);
                            }
                        });

            } else {
                fabExpandMenu.animate().translationY(0).setDuration(100).start();
                fabExpandMenu.animate().translationY(0).setDuration(100).start();

                llBottomSelectionOptions.animate()
                        .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                llBottomSelectionOptions.setVisibility(View.GONE);
                            }
                        });
            }
        }
*/
    }



    public void selectAll(){
        isSelectedAll=true;
        for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
            goodIssueModelArrayList.get(i).setChecked(true);
        }
        notifyDataSetChanged();
    }



    public void clearSelection() {
        isSelectedAll=false;
        for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
            goodIssueModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<GoodIssueModel> getSelected() {
        ArrayList<GoodIssueModel> selected = new ArrayList<>();
        for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
            if (goodIssueModelArrayList.get(i).isChecked()) {
                selected.add(goodIssueModelArrayList.get(i));
            }
        }
        return selected;
    }

    public float getSelectedVolume() {
        float selected = 0;
        //ArrayList<GoodIssueModel> selected = new ArrayList<>();
        for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
            if (goodIssueModelArrayList.get(i).isChecked()) {
                selected += goodIssueModelArrayList.get(i).getGiVhlCubication();
                if (!goodIssueModelArrayList.get(i).isChecked()){
                    selected -= goodIssueModelArrayList.get(i).getGiVhlCubication();
                }
            }
        }
        return selected;
    }





}
