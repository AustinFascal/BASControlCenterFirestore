package com.ptbas.controlcenter.utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ptbas.controlcenter.R;

import java.io.IOException;

public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layoutDialogNoInternet = LayoutInflater.from(context).inflate(R.layout.alert_no_connection, null);
            builder.setView(layoutDialogNoInternet);

            Button btnRetry = layoutDialogNoInternet.findViewById(R.id.btnRetry);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(view -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}

