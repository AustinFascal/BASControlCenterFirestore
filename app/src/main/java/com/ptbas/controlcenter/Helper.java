package com.ptbas.controlcenter;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.adapter.ROManagementAdapter;
import com.ptbas.controlcenter.management.GoodIssueManagementActivity;
import com.ptbas.controlcenter.management.ReceivedOrderManagementActivity;
import com.ptbas.controlcenter.model.UserModel;
import com.ptbas.controlcenter.userprofile.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class Helper {

    public FirebaseAuth authProfile;

    public static String getAppPath(Context context) {

        File dir = new File(Environment.getExternalStorageDirectory()
                +File.separator
                +"BASCC"
                +File.separator);

        if (!dir.exists()){
            dir.mkdir();
        }
        return dir.getPath() + File.separator;
    }

    public static void openFilePDF(Context context, File url){
       // Toast.makeText(context, "Berhasil menyimpan data rekap menjadi PDF di "+url, Toast.LENGTH_LONG).show();

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

    /*public void refreshRoManagementActivity(Context context){
        Intent intent=new Intent(context, ReceivedOrderManagementActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }*/

    public String getUserId(){
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        assert firebaseUser != null;
        return firebaseUser.getUid();
    }

    public void handleUIModeForStandardActivity(Activity activity, ActionBar actionBar){
        int nightModeFlags =
                activity.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources()
                        .getColor(R.color.black)));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources()
                        .getColor(R.color.white)));
                break;
        }
    }

    public void handleActionBarConfigForStandardActivity(Activity activity, ActionBar actionBar, String activityTitle) {
        actionBar.setTitle(activityTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources()
                .getColor(R.color.white)));
    }
}
