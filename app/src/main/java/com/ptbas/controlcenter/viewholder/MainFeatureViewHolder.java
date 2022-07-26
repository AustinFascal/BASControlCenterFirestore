package com.ptbas.controlcenter.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.DashboardActivity;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.userprofile.UserProfileActivity;


public class MainFeatureViewHolder extends RecyclerView.ViewHolder {

    public ImageView img;
    public TextView tv1, tv2;

    public MainFeatureViewHolder(@NonNull View itemView){
        super(itemView);
        img=(ImageView) itemView.findViewById(R.id.img_feature);
        tv1=(TextView) itemView.findViewById(R.id.tv_main_feature_1);
        tv2=(TextView) itemView.findViewById(R.id.tv_main_feature_2);
    }
}
