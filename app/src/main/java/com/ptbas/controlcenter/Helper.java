package com.ptbas.controlcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.management.GoodIssueManagementActivity;
import com.ptbas.controlcenter.model.UserModel;
import com.ptbas.controlcenter.userprofile.UserProfileActivity;
import com.squareup.picasso.Picasso;

public class Helper {

    public FirebaseAuth authProfile;


    public void refreshDashboard(Context context){
        Intent intent=new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

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
