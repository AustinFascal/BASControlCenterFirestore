package com.ptbas.controlcenter;

import android.content.Context;
import android.content.Intent;

public class Helper {

    public void refreshDashboard(Context context){
        Intent intent=new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

}
