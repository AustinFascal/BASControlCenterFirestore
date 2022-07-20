package com.ptbas.controlcenter.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.R;


public class StatisticsViewHolder extends RecyclerView.ViewHolder {

    public TextView tv1, tv2;

    public StatisticsViewHolder(@NonNull View itemView){
        super(itemView);
        tv1=(TextView) itemView.findViewById(R.id.tv_main_feature_1);
        tv2=(TextView) itemView.findViewById(R.id.tv_main_feature_2);
    }
}
