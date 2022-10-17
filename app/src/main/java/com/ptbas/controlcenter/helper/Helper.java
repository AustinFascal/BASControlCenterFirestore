package com.ptbas.controlcenter.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ptbas.controlcenter.DashboardActivity;
import com.ptbas.controlcenter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class Helper {

    public static String ACTIVITY_NAME = null;

    public FirebaseAuth authProfile;

    public static String getAppPath(Context context) {
        String fullPath = context.getExternalFilesDir(null) + File.separator
                +"Rekap Good Issue"
                +File.separator ;

        File dir = new File(fullPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath() + File.separator;
    }

    public static String getAppPathInvoice(Context context) {
        String fullPath = context.getExternalFilesDir(null) + File.separator
                +"Invoice"
                +File.separator ;

        File dir = new File(fullPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath() + File.separator;
    }

    public static String getAppPathCashOut(Context context) {
        String fullPath = context.getExternalFilesDir(null) + File.separator
                +"Cash Out"
                +File.separator ;

        File dir = new File(fullPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getPath() + File.separator;
    }

    public static void openFilePDF(Context context, File url){
        Uri uri = FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".fileprovider",
                url
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
        );


        for (ResolveInfo resolveInfo : resolveInfoList){
            String name = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(name, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (url.exists()) {
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void refreshDashboard(Context context){
        Intent intent=new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    public String getUserId(){
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        return firebaseUser.getUid();
    }

    public void handleUIModeForStandardActivity(Activity activity, ActionBar actionBar){
        int nightModeFlags =
                activity.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                        .getColor(activity, R.color.black)));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                        .getColor(activity, R.color.white)));
                break;
        }
    }

    public void handleActionBarConfigForStandardActivity(Activity activity, ActionBar actionBar, String activityTitle) {
        actionBar.setTitle(activityTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                .getColor(activity, R.color.white)));
    }
}
