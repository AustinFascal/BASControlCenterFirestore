package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.StatisticsModel;
import com.ptbas.controlcenter.viewholder.StatisticsViewHolder;

import java.util.ArrayList;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsViewHolder> {

    ArrayList<StatisticsModel> statisticsModels;
    Context context;

    public StatisticsAdapter(ArrayList<StatisticsModel> statisticsModels, Context context) {
        this.statisticsModels = statisticsModels;
        this.context = context;
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cardview_statistic, parent, false);
        return new StatisticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        holder.tv1.setText(statisticsModels.get(position).getHeader());
        holder.tv2.setText(statisticsModels.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return statisticsModels.size();
    }
}
