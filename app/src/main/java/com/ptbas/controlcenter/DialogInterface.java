package com.ptbas.controlcenter;

import android.app.Activity;
import android.content.Intent;

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
                        helper.refreshDashboard(activity.getApplicationContext());
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }

    public void savedInformation(Activity activity){
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

    public void savedROInformation(Activity activity){
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
}
