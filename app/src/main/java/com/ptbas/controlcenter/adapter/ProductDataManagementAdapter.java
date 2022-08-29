package com.ptbas.controlcenter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.ProductModel;
import com.ptbas.controlcenter.update.UpdateProductData;

import java.util.ArrayList;
import java.util.Objects;

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
        RelativeLayout btnDeleteWrap;
        TextView tvProductName, tvBuyPrice, tvPriceSell, tvStatus;
        ImageView ivShowDetail;
        SwitchCompat statusSwitch;
        Button btn1;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeleteWrap = itemView.findViewById(R.id.btnDeleteWrap);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvBuyPrice = itemView.findViewById(R.id.tvBuyPrice);
            tvPriceSell = itemView.findViewById(R.id.tvSellPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivShowDetail = itemView.findViewById(R.id.ivShowDetail);
            statusSwitch = itemView.findViewById(R.id.statusSwitch);
            btn1 = itemView.findViewById(R.id.btnDelete);
        }

        public void viewBind(ProductModel productModel) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


            dialogInterface = new DialogInterface();
            //DecimalFormat df = new DecimalFormat("0.00");


            String productName = productModel.getProductName();
            String productUID = productName.replaceAll(" ", "").toLowerCase();
            Double productBuyPrice = productModel.getPriceBuy();
            Double productSellPrice = productModel.getPriceSell();
            boolean productStatus = productModel.getProductStatus();

            if (productStatus){
                statusSwitch.setChecked(true);
                tvStatus.setText("Aktif");
            } else{
                statusSwitch.setChecked(false);
                tvStatus.setText("Tidak Aktif");
            }

            //Toast.makeText(context, productName, Toast.LENGTH_SHORT).show();
            tvProductName.setText(productName);
            tvBuyPrice.setText("Beli: "+productBuyPrice);
            tvPriceSell.setText("Jual: "+productSellPrice);

            statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    pd.show();
                    if (statusSwitch.isChecked()){
                        databaseReference.child("ProductData").child(productUID).child("productStatus").setValue(true).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                pd.dismiss();
                            }
                        });
                    } else {
                        databaseReference.child("ProductData").child(productUID).child("productStatus").setValue(false).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                pd.dismiss();
                            }
                        });
                    }
                }
            });

            ivShowDetail.setOnClickListener(view -> {
                String productDataUID=productName.replaceAll(" ", "").toLowerCase();
                Intent i = new Intent(context, UpdateProductData.class);
                i.putExtra("key", productDataUID);
                context.startActivity(i);
            });


            if (Objects.equals(productName, "JASA ANGKUT")){
                btnDeleteWrap.setVisibility(View.GONE);
            }

            btn1.setOnClickListener(view ->
                    dialogInterface.deleteProductDataConfirmation(context, productName.replaceAll(" ", "").toLowerCase()));
        }
    }
}
