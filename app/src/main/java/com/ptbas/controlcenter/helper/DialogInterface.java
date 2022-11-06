package com.ptbas.controlcenter.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.adapter.RecapGoodIssueManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.create.AddInvoiceActivity;
import com.ptbas.controlcenter.create.AddReceivedOrder;
import com.ptbas.controlcenter.management.ManageReceivedOrderActivity;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.InvoiceModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RecapGIModel;
import com.ptbas.controlcenter.create.AddRecapGoodIssueDataActivity;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.update.UpdateCashOutActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class DialogInterface {

    MaterialDialog md;
    Helper helper = new Helper();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refRO = db.collection("ReceivedOrderData");
    CollectionReference refCO = db.collection("CashOutData");
    CollectionReference refInv = db.collection("InvoiceData");
    CollectionReference refCust = db.collection("CustomerData");
    CollectionReference refSupplier = db.collection("SupplierData");
    CollectionReference refRecap = db.collection("RecapData");

    public void fillSearchFilter(Activity activity, SearchView searchView) {
        md = new MaterialDialog.Builder(activity)
                .setMessage("Mohon pilih tipe pencarian terlebih dahulu.", TextAlignment.START)
                .setPositiveButton("OKE", R.drawable.ic_outline_check,
                        (dialogInterface, which) -> {
                            searchView.setQuery(null,false);
                            dialogInterface.dismiss();
                        })
                .setCancelable(false)
                .build();
        md.show();
    }

    public void discardDialogConfirmation(Activity activity) {
        MaterialDialog mBottomSheetDialog = new MaterialDialog.Builder(activity)
                .setTitle("Batalkan?")
                .setAnimation(R.raw.lottie_cancel)
                .setMessage("Apakah Anda yakin ingin membatalkan proses?")
                .setCancelable(true)
                .setPositiveButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setNegativeButton("IYA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .build();

        mBottomSheetDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mBottomSheetDialog.show();
    }

    public void savedInformation(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        MaterialDialog mBottomSheetDialog = new MaterialDialog.Builder(activity)
                .setTitle("Sukses!")
                .setAnimation(R.raw.lottie_success_2)
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setCancelable(false)
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, (dialogInterface, which) -> dialogInterface.dismiss())
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    helper.refreshDashboard(activity.getApplicationContext());
                })
                .build();

        mBottomSheetDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mBottomSheetDialog.show();
    }

    public void savedROInformation(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        MaterialDialog mBottomSheetDialog = new MaterialDialog.Builder(activity)
                .setTitle("Sukses!")
                .setAnimation(R.raw.lottie_success_2)
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setCancelable(false)
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                    Intent intent = new Intent(activity, AddReceivedOrder.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    activity.startActivity(intent);
                })
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    helper.refreshDashboard(activity.getApplicationContext());
                })
                .build();

        mBottomSheetDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mBottomSheetDialog.show();
    }

    public void savedInformationFromManagement(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder(activity)
                .setAnimation(R.raw.lottie_success_2)
                .setTitle("Sukses!")
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .setCancelable(false)
                .build();
        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void savedGIInformationFromManagement(Activity activity, List<String> vhlUIDList) {

        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder(activity)
                .setAnimation(R.raw.lottie_success_2)
                .setTitle("Sukses!")
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    vhlUIDList.clear();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("VehicleData").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    if (Objects.equals(dataSnapshot.child("vhlStatus").getValue(), true)){
                                        String spinnerVhlRegistNumber = dataSnapshot.child("vhlUID").getValue(String.class);
                                        vhlUIDList.add(spinnerVhlRegistNumber);
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                })
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .setCancelable(false)
                .build();
        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void updatedInformation(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder(activity)
                .setAnimation(R.raw.lottie_success)
                .setTitle("Sukses!", TextAlignment.START)
                .setMessage("Data berhasil diperbarui", TextAlignment.START)
                .setPositiveButton("OKE", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .setCancelable(false)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    /*public void noPoNumberInformation(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_attention)
                .setTitle("Perhatian!", TextAlignment.START)
                .setMessage("Data Good Issue ini masih belum memiliki nomor PO. Mohon perbarui data tersebut agar dapat melakukan validasi dan dapat muncul saat direkapitulasi.", TextAlignment.START)
                .setPositiveButton("OKE", (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();
        md.show();
    }*/

    public void noRoPoNumberInformation(Context context, String roDocumentId) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_attention)
                .setTitle("Perhatian!", TextAlignment.START)
                .setMessage("Data Received Order ini masih belum memiliki nomor PO. Mohon perbarui data tersebut agar dapat melakukan validasi dan dapat muncul saat menambahkan Good Issue.", TextAlignment.START)
                .setPositiveButton("TAMBAHKAN NOMOR PO", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .setNegativeButton("SAHKAN TANPA NOMOR PO", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refRO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(roDocumentId)){
                                        db.collection("ReceivedOrderData").document(roDocumentId).update("roStatus", true);
                                        db.collection("ReceivedOrderData").document(roDocumentId).update("roVerifiedBy", helper.getUserId());
                                        dialogInterface.dismiss();
                                    }
                                }
                            }
                        }
                    });
                })
                .setCancelable(true)
                .build();
        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void dataCannotBeChangedInformation(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder(activity)
                .setMessage("Anda tidak dapat mengubah data ini", TextAlignment.START)
                .setPositiveButton("OKE", (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void mustAddDateRangeInformation(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder(activity)
                .setMessage("Mohon masukkan rentang tanggal dan pilih ID Received Order atau ID Purchase Order Customer", TextAlignment.START)
                .setPositiveButton("OKE", (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void roNotActiveYet(Activity activity, String roUIDVal) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        MaterialDialog mBottomSheetDialog = new MaterialDialog.Builder(activity)
                .setTitle("RO Belum Aktif")
                .setAnimation(R.raw.lottie_attention)
                .setMessage("Anda tidak dapat membuat Good Issue karena ID Received Order yang Anda pilih belum disahkan. Sahkan sekarang?")
                .setCancelable(false)
                .setPositiveButton("SAHKAN RO", R.drawable.ic_outline_check, (dialogInterface, which) -> {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ReceivedOrders");
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                String key = ds.getKey();
                                String roUID = dataSnapshot.child(key).child("roUID").getValue(String.class);
                                if (Objects.equals(roUID, roUIDVal)) {
                                    databaseReference.child(key).child("roStatus").setValue(true);
                                    databaseReference.child(key).child("roVerifiedBy").setValue(helper.getUserId());
                                    dialogInterface.dismiss();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    };
                    databaseReference.addListenerForSingleValueEvent(valueEventListener);
                })
                .setNegativeButton("LAIN KALI", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        mBottomSheetDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mBottomSheetDialog.show();
    }

    public void roNotExistsDialog(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        md = new MaterialDialog.Builder(activity)
                .setAnimation(R.raw.lottie_attention)
                .setTitle("Tidak Ada Received Order")
                .setMessage("Anda tidak dapat membuat Good Issue karena tidak memiliki Received Order yang aktif dan sah. Validasi atau tambah Received Order sekarang?")
                .setPositiveButton("YA", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(activity, ManageReceivedOrderActivity.class);
                    activity.startActivity(intent);
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .setCancelable(false)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }


    public void roNotExistsDialogForInvoice(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        md = new MaterialDialog.Builder(activity)
                .setAnimation(R.raw.lottie_attention)
                .setTitle("Tidak Ada Received Order")
                .setMessage("Anda tidak dapat membuat Invoice karena tidak memiliki Received Order yang aktif dan sah. Validasi atau tambah Received Order sekarang?")
                .setPositiveButton("YA", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(activity, ManageReceivedOrderActivity.class);
                    activity.startActivity(intent);
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .setCancelable(false)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    /*public void approveGiConfirmation(Context context, String giUID) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_approval)
                .setTitle("Validasi Data")
                .setMessage("Apakah Anda yakin ingin mengesahkan data Good Issue yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("GoodIssueData").child(giUID).child("giStatus").setValue(true);
                    databaseReference.child("GoodIssueData").child(giUID).child("giVerifiedBy").setValue(helper.getUserId());
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }*/

    public void approveGiConfirmationFromUpdateActivity(Context context, String giUID, MenuItem item) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_approval)
                .setTitle("Validasi Data")
                .setMessage("Apakah Anda yakin ingin mengesahkan data Good Issue yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("GoodIssueData").child(giUID).child("giStatus").setValue(true);
                    databaseReference.child("GoodIssueData").child(giUID).child("giVerifiedBy").setValue(helper.getUserId());
                    if (item.getItemId()==R.id.menu_verify){
                        item.setVisible(false);
                    }

                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void approveRoConfirmation(Context context, String roDocumentId) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_approval)
                .setTitle("Validasi Data")
                .setMessage("Apakah Anda yakin ingin mengesahkan data Received Order yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refRO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(roDocumentId)){
                                        db.collection("ReceivedOrderData").document(roDocumentId).update("roStatus", true);
                                        db.collection("ReceivedOrderData").document(roDocumentId).update("roVerifiedBy", helper.getUserId());
                                        dialogInterface.dismiss();
                                    }
                                }
                            }
                        }
                    });
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void coPaidConfirm(Context context, String coDocumentID) {
        String coDateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String coTimeCreated = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_approval)
                .setTitle("Ubah Status Pembayaran")
                .setMessage("Apakah Anda yakin ingin mengubah sttaus pembayaran Cash Out yang Anda pilih menjadi SUDAH DIBAYAR? Setelah disahkan, status tidak dapat dikembalikan.")
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refCO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(coDocumentID)){
                                        db.collection("CashOutData").document(coDocumentID).update("coStatusPayment", true);
                                        db.collection("CashOutData").document(coDocumentID).update("coAccBy", helper.getUserId());
                                        db.collection("CashOutData").document(coDocumentID).update("coDateAndTimeACC", coDateCreated + " | " + coTimeCreated + " WIB");

                                        dialogInterface.dismiss();
                                    }
                                }
                            }
                        }
                    });
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void approveCoConfirm(Context context, String coDocumentID) {
        String coDateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String coTimeCreated = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_approval)
                .setTitle("Validasi Data")
                .setMessage("Apakah Anda yakin ingin mengesahkan data Cash Out yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refCO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String getDocumentID = documentSnapshot.getId();
                                    if (getDocumentID.equals(coDocumentID)){
                                        db.collection("CashOutData").document(coDocumentID).update("coStatusApproval", true);
                                        db.collection("CashOutData").document(coDocumentID).update("coApprovedBy", helper.getUserId());
                                        db.collection("CashOutData").document(coDocumentID).update("coDateAndTimeApproved", coDateCreated + " | " + coTimeCreated + " WIB");
                                        dialogInterface.dismiss();
                                    }
                                }
                            }
                        }
                    });
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteCoConfirm(Context context, String coDocumentID) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Cash Out yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refCO.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                String getDocumentID = documentSnapshot.getId();
                                if (getDocumentID.equals(coDocumentID)){
                                    db.collection("CashOutData").document(coDocumentID).delete();
                                    dialogInterface.dismiss();
                                }
                            }
                        }
                    });
                    // TODO DETECT USER MODEL - IF SUPER ADMIN, ABLE TO VERIFY
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

/*
    public void approveInvConfirmation(Context context, String invDocumentID) {
        String dateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String timeCreated = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        md = new MaterialDialog.Builder((Activity) context)
                .setAnimation(R.raw.lottie_approval)
                .setTitle("Invoice Lunas?")
                .setMessage("Apakah Anda yakin ingin mengubah status Invoice yang Anda pilih menjadi lunas? Setelah diubah, status tidak dapat dikembalikan.")
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refInv.document(invDocumentID).update("invStatus", true);
                    refInv.document(invDocumentID).update("invVerifiedBy", helper.getUserId());
                    refInv.document(invDocumentID).update("invTransferReference", invTransferReference);
                    refInv.document(invDocumentID).update("invDateNTimeVerified", dateCreated + " | " + timeCreated + " WIB");
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .setCancelable(true)
                .build();
        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }
*/

    public void changePoNumberCustomer(Context context, String roUIDVal) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Ubah Nomor PO")
                .setAnimation(R.raw.lottie_attention)
                .setMessage("Untuk mengubah nomor PO, Anda harus memperbarui data nomor PO di Received Order terpilih. Perbarui sekarang?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {


                    //TODO ABLE TO UPDATE RO DETAIL


                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    /*public void deleteGiConfirmation(Context context, String giUID) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Good Issue yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("GoodIssueData").child(giUID).removeValue();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }*/

    public void deleteProductDataConfirmation(Context context, String productDataUID) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Material yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("ProductData").child(productDataUID).removeValue();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteVehicleDataConfirmation(Context context, String vhlUID) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Armada yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("VehicleData").child(vhlUID).removeValue();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteSupplierDataConfirmation(Context context, String supplierID) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Supplier yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refSupplier.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                String getDocumentID = documentSnapshot.getId();
                                if (getDocumentID.equals(supplierID)){
                                    db.collection("SupplierData").document(supplierID).delete();
                                    dialogInterface.dismiss();
                                }
                            }
                        }
                    });
                    // TODO DETECT USER MODEL - IF SUPER ADMIN, ABLE TO VERIFY
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteGiFromActivityConfirmation(Activity activity, String giUID) {
        md = new MaterialDialog.Builder(activity)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Good Issue yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("GoodIssueData").child(giUID).removeValue();
                    activity.finish();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteGiFromSelectedList(Activity activity, String giUID, int size) {
        md = new MaterialDialog.Builder(activity)
                .setTitle("Hapus Data Terpilih")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Good Issue yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("GoodIssueData").child(giUID).removeValue();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteRoConfirmation(Context context, String roDocumentId) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Received Order yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refRO.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                String getDocumentID = documentSnapshot.getId();
                                if (getDocumentID.equals(roDocumentId)){
                                    db.collection("ReceivedOrderData").document(roDocumentId).delete();
                                    dialogInterface.dismiss();
                                }
                            }
                        }
                    });
                    // TODO DETECT USER MODEL - IF SUPER ADMIN, ABLE TO VERIFY
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }

    public void deleteInvConfirmation(Context context, String invDocumentID) {
        MaterialDialog mBottomSheetDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Invoice yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    removeAllItemsFromInvoice(invDocumentID);
                    refInv.document(invDocumentID).delete();
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        mBottomSheetDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        mBottomSheetDialog.show();
    }

    public void removeAllItemsFromInvoice(String invUID) {
        db.collection("InvoiceData").document(invUID).collection("GoodIssueData")
                .get()
                .addOnSuccessListener((querySnapshot) -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener((result) -> {
                            })
                            .addOnFailureListener((error) -> {
                            });
                })
                .addOnFailureListener((error) -> {
                });
    }

    public void deleteCustConfirmation(Context context, String custDocumentId) {
        md = new MaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Customer yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    refCust.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                String getDocumentID = documentSnapshot.getId();
                                if (getDocumentID.equals(custDocumentId)){
                                    db.collection("CustomerData").document(custDocumentId).delete();
                                    dialogInterface.dismiss();
                                }
                            }
                        }
                    });
                    // TODO DETECT USER MODEL - IF SUPER ADMIN, ABLE TO VERIFY
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        md.show();
    }


    public void confirmCreateInvoice(Context context, FirebaseFirestore db,
                                     ArrayList<GoodIssueModel> goodIssueModelArrayList,
                                     String invUID, String invCreatedBy,
                                     String invDateNTimeCreated, String invDueDateNTime, String invVerifiedBy, String invTransferReference,
                                     String invDateNTimeVerified, String invDateDeliveryPeriod,
                                     String custDocumentID, String bankDocumentID, String roDocumentID, String invDateHandover, String invHandOverBy,
                                     String invTotalVol,String invSubTotal,String invDiscount,String invTaxPPN,String invTaxPPH,String invTotalDue) {
        MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Buat Invoice")
                .setAnimation(R.raw.lottie_generate_bill)
                .setMessage("Apakah Anda yakin ingin membuat Invoice dari Good Issue yang terpilih?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {

                    generatingInvoice(context, db,
                            goodIssueModelArrayList,
                            invUID, invCreatedBy, invDateNTimeCreated, invDueDateNTime, invVerifiedBy, invTransferReference,
                            invDateNTimeVerified, invDateDeliveryPeriod, custDocumentID, bankDocumentID, roDocumentID, invDateHandover, invHandOverBy,
                            invTotalVol, invSubTotal, invDiscount, invTaxPPN, invTaxPPH, invTotalDue);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        materialDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        materialDialog.show();
    }



    public void generatingInvoice(Context context, FirebaseFirestore db,
                                  ArrayList<GoodIssueModel> goodIssueModelArrayList,
                                  String invUID, String invCreatedBy,
                                  String invDateNTimeCreated, String invDueDateNTime, String invVerifiedBy, String invTransferReference,
                                  String invDateNTimeVerified, String invDateDeliveryPeriod,
                                  String custDocumentID, String bankDocumentID, String roDocumentID, String invDateHandover,  String invHandOverBy,
                                  String invTotalVol,String invSubTotal,String invDiscount,String invTaxPPN,String invTaxPPH,String invTotalDue) {

        GIManagementAdapter giManagementAdapter;

        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        //int itemSelectedSize = giManagementAdapter.getSelected().size();


        MaterialDialog generatingInvoiceDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Memproses Permintaan")
                .setMessage("Invoice sedang diproses. Harap tunggu ...")
                .setAnimation(R.raw.lottie_generate_bill)
                .setCancelable(false)
                .build();

        generatingInvoiceDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        generatingInvoiceDialog.show();

        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
                DocumentReference ref = db.collection("InvoiceData").document();
                String invDocumentID = ref.getId();

                InvoiceModel invoiceModel = new InvoiceModel(
                        invDocumentID, invUID, invCreatedBy, invDateNTimeCreated, invDueDateNTime, invVerifiedBy, invTransferReference,
                        invDateNTimeVerified, invDateDeliveryPeriod, custDocumentID, bankDocumentID, roDocumentID, invDateHandover, invHandOverBy, false,
                        invTotalVol, invSubTotal, invDiscount, invTaxPPN, invTaxPPH, invTotalDue);

                ref.set(invoiceModel);

                /*
                DocumentReference refGI = db.collection("InvoiceData").document(invDocumentID);
                for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                    GoodIssueModel goodIssueModel = goodIssueModelArrayList.get(i);
                    refGI.collection("GoodIssueData").document(goodIssueModelArrayList.get(i).getGiUID()).set(goodIssueModel);
                    databaseReferenceGI.child("GoodIssueData").child(goodIssueModelArrayList.get(i).getGiUID()).child("giInvoiced").setValue(true);
                }
                AddInvoiceActivity addInvoiceActivity = (AddInvoiceActivity) context;
                addInvoiceActivity.createInvPDF(Helper.getAppPath(context)+invUID+".pdf");
                generatingInvoiceDialog.dismiss();*/

                for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                    databaseReferenceGI.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).child("giInvoiced").setValue(true);
                    databaseReferenceGI.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).child("giInvoicedTo").setValue(invDocumentID);
                }
                AddInvoiceActivity addInvoiceActivity = (AddInvoiceActivity) context;
                addInvoiceActivity.createInvPDF(Helper.getAppPathInvoice(context)+invUID+".pdf");
                generatingInvoiceDialog.dismiss();
            }
        }.start();
    }

    public void invoiceGeneratedInformation(Context context, String filepath) {
        MaterialDialog invoiceGeneratedInformationDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Berhasil!")
                .setAnimation(R.raw.lottie_bill_generated)
                .setMessage("Data invoice telah berhasil disimpan ke database dan diekspor menjadi berkas PDF di " + filepath + ". Buka berkas sekarang?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    Helper.openFilePDF(context, new File(filepath));
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        invoiceGeneratedInformationDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        invoiceGeneratedInformationDialog.show();
    }

    public void confirmCreateCashOutProof(Context context, FirebaseFirestore db,

                                          String coUID, String coDateAndTimeCreated, String coCreatedBy,
                                          String coDateAndTimeApproved, String coApprovedBy,
                                          String coDateAndTimeACC, String coAccBy, String coSupplier,
                                          String roDocumentID, Boolean coStatusApproval,
                                          Boolean coStatusPayment, Boolean rcpStatus, RecapGoodIssueManagementAdapter recapGiManagementAdapter) {

        MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Buat Cash Out")
                .setAnimation(R.raw.lottie_generate_bill)
                .setMessage("Apakah Anda yakin ingin membuat Cash Out dari data Good Issue terpilih?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    generatingCashOut(context, db,

                            coUID, coDateAndTimeCreated, coCreatedBy,
                            coDateAndTimeApproved, coApprovedBy, coDateAndTimeACC, coAccBy, coSupplier,
                            roDocumentID, coStatusApproval, coStatusPayment, rcpStatus, recapGiManagementAdapter);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        materialDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        materialDialog.show();
    }

    public void generatingCashOut(Context context, FirebaseFirestore db,
                                                                    String coUID, String coDateAndTimeCreated, String coCreatedBy,
                                  String coDateAndTimeApproved, String coApprovedBy,
                                  String coDateAndTimeACC, String coAccBy, String coSupplier,
                                  String roDocumentID, Boolean coStatusApproval,
                                  Boolean coStatusPayment, Boolean rcpStatus, RecapGoodIssueManagementAdapter recapGiManagementAdapter) {


        /*GIManagementAdapter giManagementAdapter;
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);*/

        MaterialDialog generatingCashOutProofDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Memproses Permintaan")
                .setMessage("Pembuatan Cash Out sedang diproses. Harap tunggu ...")
                .setAnimation(R.raw.lottie_generate_bill)
                .setCancelable(false)
                .build();

        generatingCashOutProofDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        generatingCashOutProofDialog.show();
        ArrayList<GoodIssueModel> goodIssueModelArrayList  = new ArrayList<>();
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
                DocumentReference refCO = db.collection("CashOutData").document();
                String coDocumentID = refCO.getId();

                List<String> rcpGiUID = new ArrayList<>();
                Query query = databaseReferenceGI.child("GoodIssueData");

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (int k = 0; k < recapGiManagementAdapter.getSelected().size(); k++) {
                                rcpGiUID.add(recapGiManagementAdapter.getSelected().get(k).getRcpGiDocumentID());
                            }
                            for (int l = 0; l < rcpGiUID.size(); l++) {
                                for (DataSnapshot item : snapshot.getChildren()) {
                                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);

                                    if (goodIssueModel.getGiRecappedTo().equals(rcpGiUID.get(l))) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        goodIssueModelArrayList.add(goodIssueModel);
                                        databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giCashedOutTo").setValue(coDocumentID);
                                        databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giCashedOut").setValue(true);
                                    }
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DecimalFormat df = new DecimalFormat("0.00");


                db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            List<ProductItems> productItemsList;
                            double transportServiceSellPrice = 0;
                            double matBuyPrice = 0;
                            String matNameVal;

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                                /*matTypeVal = receivedOrderModel.getRoMatType();
                                roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                                currencyVal = receivedOrderModel.getRoCurrency();
                                invCustName = receivedOrderModel.getCustDocumentID();
                                invPoUID = receivedOrderModel.getRoUID();
                                invPoDate = receivedOrderModel.getRoDateCreated();
                                invPoType = receivedOrderModel.getRoType();*/

                                HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                                for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                    productItemsList = e.getValue();
                                    for (int i = 0; i<productItemsList.size();i++){
                                        if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                            transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();
                                        } else {
                                            matNameVal = productItemsList.get(i).getMatName();
                                            matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                        }
                                    }

                                }


                            }

                            float totalUnit = 0;

                            List<String> datePeriod = new ArrayList<>();

                            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                                totalUnit += goodIssueModelArrayList.get(i).getGiVhlCubication();
                                datePeriod.add(goodIssueModelArrayList.get(i).getGiDateCreated());
                            }

                            HashSet<String> filter = new HashSet(datePeriod);
                            ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);
                            String coDateDeliveryPeriod = String.valueOf(datePeriodFiltered);

                            double totalIDR = matBuyPrice *Double.parseDouble(df.format(totalUnit));

                            refRecap.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                                            String getDocumentID = documentSnapshot.getId();
                                            RecapGIModel recapGIModel = documentSnapshot.toObject(RecapGIModel.class);
                                            recapGIModel.setRcpGiDocumentID(documentSnapshot.getId());

                                            String rcpGiUIDVal = recapGIModel.getRcpGiDocumentID();
                                            String roDocumentIdVal = recapGIModel.getRoDocumentID();

                                            if (recapGIModel.getRoDocumentID().equals(roDocumentID)){
                                                db.collection("RecapData").document(rcpGiUIDVal).update("rcpGiStatus", true);
                                                db.collection("RecapData").document(rcpGiUIDVal).update("rcpGiCoUID", coDocumentID);
                                            }
                                        }
                                    }
                                }
                            });

                            CashOutModel cashOutModel = new CashOutModel(
                                    coDocumentID, coUID, coDateAndTimeCreated, coCreatedBy,
                                    coDateAndTimeApproved, coApprovedBy, coDateAndTimeACC, coAccBy, coSupplier,
                                    roDocumentID, coDateDeliveryPeriod, coStatusApproval, coStatusPayment, totalIDR,
                                    "", "", "", "", "", "");

                            refCO.set(cashOutModel);
                        });









                /*for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                    databaseReferenceGI.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).child("giCashedOut").setValue(true);
                    databaseReferenceGI.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).child("giCashedOutTo").setValue(coDocumentID);
                }*/
                cashOutProofGeneratedInformation(context);
                /*AddCashOutActivity addCashOutActivity = (AddCashOutActivity) context;
                addCashOutActivity.createCashOutProofPDF(Helper.getAppPathCashOut(context)+coUID+".pdf");*/
                generatingCashOutProofDialog.dismiss();
            }
        }.start();
    }

    public void cashOutProofGeneratedInformation(Context context) {
        MaterialDialog invoiceGeneratedInformationDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Berhasil!")
                .setAnimation(R.raw.lottie_bill_generated)
                .setMessage("Data Cash Out telah berhasil disimpan ke database.")
                .setCancelable(true)
                .setPositiveButton("OKE", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    //Helper.openFilePDF(context, new File(filepath));
                    dialogInterface.dismiss();
                })
                .build();

        invoiceGeneratedInformationDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        invoiceGeneratedInformationDialog.show();
    }






    public void confirmCreateRecap(Context context, String rcpGiUID, String rcpGiDateAndTimeCreated,
                                   String rcpGiCreatedBy, String roUIDVal, String poUID, String rcpDateDeliveryPeriod, float totalUnit,
                                   ArrayList<GoodIssueModel> goodIssueModelArrayList) {

        MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Buat Rekap")
                .setAnimation(R.raw.lottie_generate_bill)
                .setMessage("Apakah Anda yakin ingin membuat rekapitulasi data Good Issue terpilih?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    GIManagementAdapter giManagementAdapter;

                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                    //int itemSelectedSize = giManagementAdapter.getSelected().size();

                    MaterialDialog generatingInvoiceDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Memproses Permintaan")
                            .setMessage("Data rekap sedang diproses. Harap tunggu ...")
                            .setAnimation(R.raw.lottie_generate_bill)
                            .setCancelable(false)
                            .build();

                    generatingInvoiceDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    generatingInvoiceDialog.show();

                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {



                            DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
                            DocumentReference refRCPGI = db.collection("RecapData").document();
                            String rcpGiDocumentID = refRCPGI.getId();

                            RecapGIModel recapGIModel = new RecapGIModel(rcpGiDocumentID, rcpGiUID, rcpGiDateAndTimeCreated, rcpGiCreatedBy, roUIDVal, totalUnit, "");

                            refRCPGI.set(recapGIModel);

                            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                                databaseReferenceGI.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).child("giRecapped").setValue(true);
                                databaseReferenceGI.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).child("giRecappedTo").setValue(rcpGiDocumentID);
                            }
                            AddRecapGoodIssueDataActivity addRecapGoodIssueDataActivity = (AddRecapGoodIssueDataActivity) context;
                            addRecapGoodIssueDataActivity.createPDF(Helper.getAppPath(context)+"RKP-PO-"+poUID+" Periode Pengiriman "+rcpDateDeliveryPeriod+".pdf");
                            generatingInvoiceDialog.dismiss();
                        }
                    }.start();

                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        materialDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        materialDialog.show();
    }

    public void recapGeneratedInfo(Context context, String filepath) {
        MaterialDialog invoiceGeneratedInformationDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Berhasil!")
                .setAnimation(R.raw.lottie_bill_generated)
                .setMessage("Data rekap telah berhasil diekspor menjadi berkas PDF di " + filepath + ". Buka berkas sekarang?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    Helper.openFilePDF(context, new File(filepath));
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        invoiceGeneratedInformationDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        invoiceGeneratedInformationDialog.show();
    }













    public void confirmPrintCo(Context context, FirebaseFirestore db,
                               ArrayList<GoodIssueModel> goodIssueModelArrayList,
                               String coUID, String coDateAndTimeCreated, String coCreatedBy,
                               String coDateAndTimeApproved, String coApprovedBy,
                               String coDateAndTimeACC, String coAccBy, String coSupplier,
                               String coPoNumber, String coDateDeliveryPeriod, Boolean coStatusApproval,
                               Boolean coStatusPayment, Double coTotal) {

        MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Buat Cash Out")
                .setAnimation(R.raw.lottie_generate_bill)
                .setMessage("Apakah Anda yakin ingin membuat Cash Out dari data Good Issue terpilih?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {


                    GIManagementAdapter giManagementAdapter;
                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);

                    MaterialDialog generatingCashOutProofDialog = new MaterialDialog.Builder((Activity) context)
                            .setTitle("Memproses Permintaan")
                            .setMessage("Cetak Cash Out sedang diproses. Harap tunggu ...")
                            .setAnimation(R.raw.lottie_generate_bill)
                            .setCancelable(false)
                            .build();

                    generatingCashOutProofDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    generatingCashOutProofDialog.show();

                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            UpdateCashOutActivity updateCashOutActivity = (UpdateCashOutActivity) context;

                            updateCashOutActivity.createCashOutProofPDF(Helper.getAppPathCashOut(context)+coUID+" Periode Pengiriman "+coDateDeliveryPeriod+".pdf");

                            generatingCashOutProofDialog.dismiss();
                        }
                    }.start();


                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        materialDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        materialDialog.show();
    }

    public void coPrintedInfo(Context context, String filepath) {
        MaterialDialog invoiceGeneratedInformationDialog = new MaterialDialog.Builder((Activity) context)
                .setTitle("Berhasil!")
                .setAnimation(R.raw.lottie_bill_generated)
                .setMessage("Data Cash-Out telah berhasil dicetak dan diekspor menjadi berkas PDF di " + filepath + ". Buka berkas sekarang?")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                    Helper.openFilePDF(context, new File(filepath));
                    dialogInterface.dismiss();
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                .build();

        invoiceGeneratedInformationDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
        invoiceGeneratedInformationDialog.show();
    }


}
