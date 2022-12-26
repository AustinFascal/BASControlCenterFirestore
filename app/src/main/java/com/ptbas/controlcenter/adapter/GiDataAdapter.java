package com.ptbas.controlcenter.adapter;


import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.model.CoModel;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.GiModel;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.update.UpdtGiActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class GiDataAdapter extends RecyclerView.Adapter<GiDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<GiModel> giModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;
    HelperUtils helperUtils = new HelperUtils();
    ItemViewHolder itemViewHolder;
    public boolean isSelectedAll = false;


    public GiDataAdapter(Context context, ArrayList<GiModel> giModelArrayList) {
        this.context = context;
        this.giModelArrayList = giModelArrayList;
    }

    public void setItems(ArrayList<GiModel> emp)
    {
        giModelArrayList.addAll(emp);
    }

    @NonNull
    @Override
    public GiDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        holder.viewBind(giModelArrayList.get(position));
        if (!isSelectedAll) {
            holder.cbSelectItem.setChecked(false);
        } else {
            holder.cbSelectItem.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return giModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llStatusApproved, llStatusRecapped, llStatusInvoiced, llCashedOutStatus, llRoNeedsUpdate, llHiddenView, llWrapGiStatus;
        TextView tvCubication, tvGiDateTime, tvGiUid, tvGiMatDetail, tvGiVhlDetail,
                tvVhlUid, tvPoCustNumber, tvCustomerName;

        //tvRoUid
        RelativeLayout btnDeleteGi, btnApproveGi, btnCloneGi;
        Button btn1, btn2, btn3, btn4;
        ConstraintLayout cardView;
        CheckBox cbSelectItem;
        String customerNameVal, custDocumentID, coAccBy;

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
            //tvRoUid = itemView.findViewById(R.id.tvCoTotal);
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
        }


        public void viewBind(final GiModel giModel) {
            cbSelectItem.setChecked(false);
           // tvRoUid.setVisibility(View.INVISIBLE);

            if (Objects.equals(helperUtils.ACTIVITY_NAME, "UPDATE")){
                btnDeleteGi.setVisibility(View.GONE);
            }

            if (Objects.equals(helperUtils.ACTIVITY_NAME, "DETAIL")){
                btnDeleteGi.setVisibility(View.GONE);
                cbSelectItem.setVisibility(View.GONE);
            }

            if (helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE){
                btnCloneGi.setVisibility(View.VISIBLE);
            } else{
                btnCloneGi.setVisibility(View.GONE);
            }

            /*if (tvGiUid.getText().toString().contains("CL")){
                btnCloneGi.setVisibility(View.GONE);
            } else{
                btnCloneGi.setVisibility(View.VISIBLE);
            }*/

            itemView.setOnClickListener(view -> {
                giModel.setChecked(!giModel.isChecked());
                cbSelectItem.setChecked(giModel.isChecked());
            });

            cbSelectItem.setOnClickListener(view -> {
                giModel.setChecked(!giModel.isChecked());
                cbSelectItem.setChecked(giModel.isChecked());
            });

            DecimalFormat df = new DecimalFormat("0.00");
            Double cubication = giModel.getGiVhlCubication();
            String dateNTime = giModel.getGiDateCreated()+" "+ giModel.getGiTimeCreted();

            String[] partGiUID = giModel.getGiUID().split("-");
            String giUID = partGiUID[0];

            String roDocumentID = giModel.getRoDocumentID();

            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID)
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            if (!value.isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {
                                    RoModel roModel = d.toObject(RoModel.class);

                                    assert roModel != null;
                                    String roNumber = roModel.getRoUID();
                                    String poNumber = roModel.getRoPoCustNumber();
                                    custDocumentID = roModel.getCustDocumentID();

                                    tvPoCustNumber.setText("PO: " + poNumber);

                                    db1.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID)
                                            .addSnapshotListener((value2, error2) -> {
                                                if (value2 != null) {
                                                    if (!value2.isEmpty()) {
                                                        for (DocumentSnapshot e : value2.getDocuments()) {
                                                            CustModel custModel = e.toObject(CustModel.class);
                                                            String customerName = custModel.getCustName();
                                                            String customerAlias = custModel.getCustUID();

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

            String matDetail = giModel.getGiMatType()+" - "+ giModel.getGiMatName();
            String vhlDetail = "(P) "+ giModel.getVhlLength().toString()+" (L) "+ giModel.getVhlWidth().toString()+" (T) "+ giModel.getVhlHeight().toString()+" | "+"(K) "+ giModel.getVhlHeightCorrection().toString()+" (TK) "+ giModel.getVhlHeightAfterCorrection().toString();
            String vhlUID = giModel.getVhlUID();
            boolean giStatus = giModel.getGiStatus();
            String giRecappedTo = giModel.getGiRecappedTo();
            boolean giInvoiced = giModel.getGiInvoiced();
            String giInvoicedTo = giModel.getGiInvoicedTo();
            boolean giCashedOut = giModel.getGiCashedOut();
            String giCashedOutTo = giModel.getGiCashedOutTo();

            tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));
            tvGiDateTime.setText(dateNTime);
            tvGiUid.setText(giUID);
            tvGiMatDetail.setText(matDetail);
            tvGiVhlDetail.setText(vhlDetail);
            tvVhlUid.setText(vhlUID);

            String giUIDVal = giModel.getGiUID();

            if (giModel.getGiMatName().contentEquals("JASA ANGKUT")){
                tvVhlUid.setVisibility(View.GONE);
                tvGiVhlDetail.setVisibility(View.GONE);
            }

            DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (giStatus){
                //llWrapGiStatus.setVisibility(View.VISIBLE);
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveGi.setVisibility(View.GONE);
            } else {
                //llWrapGiStatus.setVisibility(View.GONE);
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


            db.collection("CashOutData").whereEqualTo("coDocumentID", giCashedOutTo).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    CoModel coModel = documentSnapshot.toObject(CoModel.class);
                                    coModel.setCoDocumentID(documentSnapshot.getId());
                                    coAccBy = coModel.getCoAccBy();
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
                    GiModel giModelClone = new GiModel("CL-"+ giModel.getGiUID(), giModel.getGiCreatedBy(), giModel.getGiVerifiedBy(), giModel.getRoDocumentID(),
                            giModel.getGiMatName(), giModel.getGiMatType(), giModel.getGiNoteNumber(), vhlUID, giModel.getGiDateCreated(), giModel.getGiTimeCreted(), giModel.getVhlLength(),
                            giModel.getVhlWidth(), giModel.getVhlHeight(), giModel.getVhlHeightCorrection(), giModel.getVhlHeightAfterCorrection(), giModel.getGiVhlCubication(), giStatus, false, giInvoiced, giModel.getGiInvoicedTo(), giModel.getGiCashedOut(), giModel.getGiCashedOutTo(), giModel.getGiRecappedTo());
                    DatabaseReference refGI = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData");
                    refGI.child("CL-"+ giModel.getGiUID()).setValue(giModelClone).addOnCompleteListener(task -> {
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

                    GiModel giModelUpdate = new GiModel(giModel.getGiUID(), giModel.getGiCreatedBy(), giModel.getGiVerifiedBy(), giModel.getRoDocumentID(),
                            giModel.getGiMatName(), giModel.getGiMatType(), giModel.getGiNoteNumber(), vhlUID, giModel.getGiDateCreated(), giModel.getGiTimeCreted(), giModel.getVhlLength(),
                            giModel.getVhlWidth(), giModel.getVhlHeight(), giModel.getVhlHeightCorrection(), giModel.getVhlHeightAfterCorrection(), giModel.getGiVhlCubication(), giStatus, false, giInvoiced, "ARC-"+ giModel.getGiInvoicedTo(), giModel.getGiCashedOut(), giModel.getGiCashedOutTo(), giModel.getGiRecappedTo());
                    refGI.child(giModel.getGiUID()).setValue(giModelUpdate).addOnCompleteListener(task -> {
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
                String giUID1 = giModel.getGiUID();
                Intent i = new Intent(context, UpdtGiActivity.class);
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
                                databaseReference.child("GoodIssueData").child(giModel.getGiUID()).child("giStatus").setValue(true);
                                databaseReference.child("GoodIssueData").child(giModel.getGiUID()).child("giVerifiedBy").setValue(helperUtils.getUserId());
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
                            databaseReference.child("GoodIssueData").child(giModel.getGiUID()).removeValue();
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            });

        }
    }



    public void selectAll(){
        isSelectedAll=true;
        for (int i = 0; i < giModelArrayList.size(); i++) {
            giModelArrayList.get(i).setChecked(true);

        }
        notifyDataSetChanged();
    }



    public void clearSelection() {
        isSelectedAll=false;
        for (int i = 0; i < giModelArrayList.size(); i++) {
            giModelArrayList.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }


    public ArrayList<GiModel> getSelected() {
        ArrayList<GiModel> selected = new ArrayList<>();
        for (int i = 0; i < giModelArrayList.size(); i++) {
            if (giModelArrayList.get(i).isChecked()) {
                selected.add(giModelArrayList.get(i));
            }
        }
        return selected;
    }
    public float getSelectedVolume() {
        float selected = 0;
        //ArrayList<GoodIssueModel> selected = new ArrayList<>();
        for (int i = 0; i < giModelArrayList.size(); i++) {
            if (giModelArrayList.get(i).isChecked()) {
                selected += giModelArrayList.get(i).getGiVhlCubication();
                if (!giModelArrayList.get(i).isChecked()){
                    selected -= giModelArrayList.get(i).getGiVhlCubication();
                }
            }
        }
        return selected;
    }





}