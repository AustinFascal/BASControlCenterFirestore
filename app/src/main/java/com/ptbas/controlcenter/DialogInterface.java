package com.ptbas.controlcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ptbas.controlcenter.create.AddReceivedOrder;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class DialogInterface {
    Helper helper = new Helper();

    public void discardDialogConfirmation(Activity activity){
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(activity)
                .setTitle("Batalkan?")
                .setAnimation(R.raw.lottie_cancel)
                .setMessage("Apakah Anda yakin ingin membatalkan proses?")
                .setCancelable(true)
                .setPositiveButton("TIDAK", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }

                })
                .setNegativeButton("IYA", R.drawable.ic_outline_check, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void savedInformation(Activity activity){
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(activity)
                .setTitle("Sukses!")
                .setAnimation(R.raw.lottie_saved)
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setCancelable(false)
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }

                })
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        helper.refreshDashboard(activity.getApplicationContext());
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void savedInformationFromManagement(Activity activity){
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(activity)
                .setTitle("Sukses!")
                .setAnimation(R.raw.lottie_saved)
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setCancelable(false)
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }

                })
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void savedROInformation(Activity activity){
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(activity)
                .setTitle("Sukses!")
                .setAnimation(R.raw.lottie_saved)
                .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                .setCancelable(false)
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        Intent intent=new Intent(activity, AddReceivedOrder.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.startActivity(intent);
                    }

                })
                .setNegativeButton("SELESAI", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        helper.refreshDashboard(activity.getApplicationContext());
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void approveGiConfirmation(Context context, String giUID){
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                .setTitle("Validasi Data")
                .setAnimation(R.raw.lottie_approval)
                .setMessage("Apakah Anda yakin ingin mengesahkan data Good Issue yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("GoodIssueData").child(giUID).child("giStatus").setValue(true);
                    }

                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }

                })
                .build();

        mBottomSheetDialog.show();
    }


    public void deleteGiConfirmation(Context context, String giUID){
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                .setTitle("Hapus Data")
                .setAnimation(R.raw.lottie_delete)
                .setMessage("Apakah Anda yakin ingin menghapus data Good Issue yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                .setCancelable(true)
                .setPositiveButton("YA", R.drawable.ic_outline_check, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("GoodIssueData").child(giUID).removeValue();
                    }

                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }

                })
                .build();

        mBottomSheetDialog.show();
    }
}
