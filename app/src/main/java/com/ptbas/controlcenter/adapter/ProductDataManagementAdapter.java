package com.ptbas.controlcenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.ProductModel;
import com.ptbas.controlcenter.update.UpdateProductData;

import java.util.ArrayList;

public class ProductDataManagementAdapter extends RecyclerView.Adapter<ProductDataManagementAdapter.ItemViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelArrayList;
    DialogInterface dialogInterface;

    public ProductDataManagementAdapter(Context context, ArrayList<ProductModel> productModelArrayList) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
    }

    @NonNull
    @Override
    public ProductDataManagementAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_product_data, parent, false);
        
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDataManagementAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(productModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvBuyPrice, tvPriceSell;
        ImageView ivShowDetail;
        Button btn1;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvBuyPrice = itemView.findViewById(R.id.tvBuyPrice);
            tvPriceSell = itemView.findViewById(R.id.tvSellPrice);
            ivShowDetail = itemView.findViewById(R.id.ivShowDetail);
            btn1 = itemView.findViewById(R.id.btnDelete);
        }

        public void viewBind(ProductModel productModel) {
            dialogInterface = new DialogInterface();
            //DecimalFormat df = new DecimalFormat("0.00");

            String productName = productModel.getProductName();
            Double productBuyPrice = productModel.getPriceBuy();
            Double productSellPrice = productModel.getPriceSell();

            //Toast.makeText(context, productName, Toast.LENGTH_SHORT).show();
            tvProductName.setText(productName);
            tvBuyPrice.setText("Beli: "+productBuyPrice);
            tvPriceSell.setText("Jual: "+productSellPrice);

            ivShowDetail.setOnClickListener(view -> {
                String productDataUID=productName.replaceAll(" ", "").toLowerCase();
                Intent i = new Intent(context, UpdateProductData.class);
                i.putExtra("key", productDataUID);
                context.startActivity(i);
            });

            btn1.setOnClickListener(view ->
                    dialogInterface.deleteProductDataConfirmation(context, productName.replaceAll(" ", "").toLowerCase()));
        }
    }
}
