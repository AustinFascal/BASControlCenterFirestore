package com.ptbas.controlcenter.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.text.pdf.AcroFields;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.update.UpdateGoodIssueActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class GIManagementAdapter extends RecyclerView.Adapter<GIManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<GoodIssueModel> goodIssueModelArrayList;
    DialogInterface dialogInterface;
    Helper helper = new Helper();


    public GIManagementAdapter(Context context, ArrayList<GoodIssueModel> goodIssueModelArrayList) {
        this.context = context;
        this.goodIssueModelArrayList = goodIssueModelArrayList;
    }

    /*public void setGoodIssueSelected(ArrayList<GoodIssueModel> goodIssueModelArrayList) {
        this.goodIssueModelArrayList = new ArrayList<>();
        this.goodIssueModelArrayList = goodIssueModelArrayList;
        notifyDataSetChanged();
    }*/

    @NonNull
    @Override
    public GIManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_good_issue, parent, false);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.viewBind(goodIssueModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return goodIssueModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llStatusApproved, llStatusInvoiced, llCashedOutStatus, llStatusPOAvailable, llRoNeedsUpdate, llHiddenView, llWrapGiStatus;
        TextView tvCubication, tvGiDateTime, tvGiUid, tvRoUid, tvGiMatDetail, tvGiVhlDetail,
                tvVhlUid, tvPoCustNumber;
        RelativeLayout btnDeleteGi, btnApproveGi;
        Button btn1, btn2, btn3;
        CardView cardView;
        CheckBox cbSelectItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            llHiddenView = itemView.findViewById(R.id.llHiddenView);
            llWrapGiStatus = itemView.findViewById(R.id.llWrapGiStatus);
            llStatusApproved = itemView.findViewById(R.id.ll_status_approved);
            llCashedOutStatus = itemView.findViewById(R.id.llCashedOutStatus);
            llStatusInvoiced = itemView.findViewById(R.id.ll_status_invoiced);
            llStatusPOAvailable = itemView.findViewById(R.id.ll_status_po_unvailable);
            llRoNeedsUpdate = itemView.findViewById(R.id.ll_ro_needs_update);
            tvCubication = itemView.findViewById(R.id.tv_cubication);
            tvGiDateTime = itemView.findViewById(R.id.tv_inv_date_created);
            tvGiUid = itemView.findViewById(R.id.tv_gi_uid);
            tvRoUid = itemView.findViewById(R.id.tv_ro_uid);
            tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
            tvGiMatDetail = itemView.findViewById(R.id.tv_gi_mat_detail);
            tvGiVhlDetail = itemView.findViewById(R.id.tv_gi_vhl_detail);
            tvVhlUid = itemView.findViewById(R.id.tv_vhl_uid);
            btnDeleteGi = itemView.findViewById(R.id.btn_delete_gi);
            btnApproveGi = itemView.findViewById(R.id.btn_approve_gi);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);
            btn3 = itemView.findViewById(R.id.btn3);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);



        }



        public void viewBind(final GoodIssueModel goodIssueModel) {
            cbSelectItem.setChecked(false);

            if (Objects.equals(helper.ACTIVITY_NAME, "COR")){
                btnDeleteGi.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goodIssueModel.setChecked(!goodIssueModel.isChecked());
                    cbSelectItem.setChecked(goodIssueModel.isChecked());
                                    }
            });

            cbSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goodIssueModel.setChecked(!goodIssueModel.isChecked());
                    cbSelectItem.setChecked(goodIssueModel.isChecked());
                }
            });




            DecimalFormat df = new DecimalFormat("0.00");
            float cubication = goodIssueModel.getGiVhlCubication();
            String dateNTime = goodIssueModel.getGiDateCreated()+" | "+goodIssueModel.getGiTimeCreted();
            String giUID = "GI-"+goodIssueModel.getGiUID();
            String roUID = "RO-"+goodIssueModel.getGiRoUID();
            String poCustNumb = "PO: "+goodIssueModel.getGiPoCustNumber();

            String matDetail = goodIssueModel.getGiMatType()+" | "+goodIssueModel.getGiMatName();
            String vhlDetail = "(P) "+goodIssueModel.getVhlLength().toString()+" (L) "+goodIssueModel.getVhlWidth().toString()+" (T) "+goodIssueModel.getVhlHeight().toString()+" | "+"(K) "+goodIssueModel.getVhlHeightCorrection().toString()+" (TK) "+goodIssueModel.getVhlHeightAfterCorrection().toString();
            String vhlUID = goodIssueModel.getVhlUID();
            boolean giStatus = goodIssueModel.getGiStatus();
            boolean giInvoiced = goodIssueModel.getGiInvoiced();
            boolean giCashedOut = goodIssueModel.getGiCashedOut();

            tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));
            tvGiDateTime.setText(dateNTime);
            tvGiUid.setText(giUID);
            tvRoUid.setText(roUID);
            tvPoCustNumber.setText(poCustNumb);
            tvGiMatDetail.setText(matDetail);
            tvGiVhlDetail.setText(vhlDetail);
            tvVhlUid.setText(vhlUID);

            String giUIDVal =goodIssueModel.getGiUID();
            String giRoUIDVal =goodIssueModel.getGiRoUID();

            if (goodIssueModel.getGiMatName().contentEquals("JASA ANGKUT")){
                tvVhlUid.setVisibility(View.GONE);
                tvGiVhlDetail.setVisibility(View.GONE);
            }

            DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("ReceivedOrderData").whereEqualTo("roUID", giRoUIDVal)
                    .addSnapshotListener((value, error) -> {
                        String poUIDUpdate = "";
                        for (DocumentSnapshot d : value.getDocuments()) {
                            ReceivedOrderModel receivedOrderModel = d.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(d.getId());
                            poUIDUpdate = receivedOrderModel.getRoPoCustNumber();
                        }
                        databaseReferenceGI.child("GoodIssueData").child(giUIDVal).child("giPoCustNumber").setValue(poUIDUpdate);
                    });

            if (giStatus){
                llWrapGiStatus.setVisibility(View.VISIBLE);
                llStatusApproved.setVisibility(View.VISIBLE);
                btnApproveGi.setVisibility(View.GONE);
            } else {
                llWrapGiStatus.setVisibility(View.GONE);
                llStatusApproved.setVisibility(View.GONE);
                btnApproveGi.setVisibility(View.VISIBLE);
            }

            if (giInvoiced){
                llStatusInvoiced.setVisibility(View.VISIBLE);
            } else {
                llStatusInvoiced.setVisibility(View.GONE);
            }

            if (giCashedOut){
                llCashedOutStatus.setVisibility(View.VISIBLE);
            } else {
                llCashedOutStatus.setVisibility(View.GONE);
            }

            if (tvPoCustNumber.getText().toString().equals("PO: -")){
                tvPoCustNumber.setVisibility(View.GONE);
                llStatusPOAvailable.setVisibility(View.VISIBLE);
            } else {
                tvPoCustNumber.setVisibility(View.VISIBLE);
                llStatusPOAvailable.setVisibility(View.GONE);
            }

            btn3.setOnClickListener(view -> {
                if (!helper.ACTIVITY_NAME.equals("COR")){
                    String giUID1 =goodIssueModel.getGiUID();
                    Intent i = new Intent(context, UpdateGoodIssueActivity.class);
                    i.putExtra("key", giUID1);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, "OPEN DETAIL", Toast.LENGTH_SHORT).show();
                }

            });

            btn2.setOnClickListener(view -> {
                if (tvPoCustNumber.getText().toString().equals("PO: -")){
                    //dialogInterface.noPoNumberInformation(context);
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
                    //dialogInterface.approveGiConfirmation(context, goodIssueModel.getGiUID());
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
            });

                    //dialogInterface.deleteGiConfirmation(context, goodIssueModel.getGiUID()));



        }
    }


    public ArrayList<GoodIssueModel> selectAll() {
        ArrayList<GoodIssueModel> selected = new ArrayList<>();
        for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
            if (!goodIssueModelArrayList.get(i).isChecked()) {
                selected.add(goodIssueModelArrayList.get(i));
            }
        }
        return selected;
    }

    public void clearSelection() {
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



}
