package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.hardware.lights.LightState;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.ProductItems;

import java.util.List;

public class PreviewProductItemAdapter extends RecyclerView.Adapter<PreviewProductItemAdapter.ViewHolder> {

    Context context;
    List<ProductItems> productItemsList;

    public PreviewProductItemAdapter(Context context, List<ProductItems> productItemsList){
        this.context = context;
        this.productItemsList = productItemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_layout_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (productItemsList != null && productItemsList.size() > 0){
            ProductItems productItems = productItemsList.get(position);

            holder.tvItemName.setText(productItems.getProductName());
            holder.tvItemQty.setText(String.valueOf(productItems.getProductQuantity()));
            holder.tvItemTotalBuy.setText(String.valueOf(productItems.getProductTotalBuyPrice()));
            holder.tvItemTotalSell.setText(String.valueOf(productItems.getProductTotalSellPrice()));
        } else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return productItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQty, tvItemTotalBuy, tvItemTotalSell;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQty = itemView.findViewById(R.id.tvItemQty);
            tvItemTotalBuy = itemView.findViewById(R.id.tvItemTotalBuy);
            tvItemTotalSell = itemView.findViewById(R.id.tvItemTotalSell);
        }
    }
}
