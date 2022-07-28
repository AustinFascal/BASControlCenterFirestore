package com.ptbas.controlcenter;

import android.app.Activity;

import com.google.android.material.transition.MaterialContainerTransform;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;

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
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(activity)
                .setTitle("Sukses!", TextAlignment.START)
                .setAnimation(R.raw.lottie_saved)
                .setMessage("Berhasil menambahkan data", TextAlignment.START)
                .setCancelable(false)
                .setPositiveButton("OKE", R.drawable.ic_outline_check, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        helper.swipeRefresh(activity.getApplicationContext());
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }
}
