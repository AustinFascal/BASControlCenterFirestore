package com.ptbas.controlcenter;

import android.app.Activity;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

public class DialogInterface {
    public void discardDialogConfirmation(Activity activity){
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(activity)
                .setTitle("Hapus?")
                .setAnimation(R.raw.discard)
                .setMessage("Apakah Anda yakin ingin membuang semua data yang telah Anda masukkan?")
                .setCancelable(true)
                .setPositiveButton("BATAL", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }

                })
                .setNegativeButton("HAPUS", R.drawable.ic_outline_delete_forever, new BottomSheetMaterialDialog.OnClickListener() {
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
}
