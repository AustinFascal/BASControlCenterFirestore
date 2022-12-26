package com.ptbas.controlcenter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.ProdModel;
import com.ptbas.controlcenter.update.UpdtProdActivity;

import java.util.ArrayList;
import java.util.Objects;

public class ProdDataAdapter extends RecyclerView.Adapter<ProdDataAdapter.ItemViewHolder> {

    Context context;
    ArrayList<ProdModel> prodModelArrayList;
    DialogInterfaceUtils dialogInterfaceUtils;

    public ProdDataAdapter(Context context, ArrayList<ProdModel> prodModelArrayList) {
        this.context = context;
        this.prodModelArrayList = prodModelArrayList;
    }

    @NonNull
    @Override
    public ProdDataAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_product_data, parent, false);
        
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdDataAdapter.ItemViewHolder holder, int position) {
        holder.viewBind(prodModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return prodModelArrayList.size();
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

        public void viewBind(ProdModel prodModel) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");
            pd.setCancelable(false);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


            dialogInterfaceUtils = new DialogInterfaceUtils();
            //DecimalFormat df = new DecimalFormat("0.00");


            String productName = prodModel.getProductName();
            String productUID = productName.replaceAll(" ", "").toLowerCase();
            Double productBuyPrice = prodModel.getPriceBuy();
            Double productSellPrice = prodModel.getPriceSell();
            boolean productStatus = prodModel.getProductStatus();

            if (productStatus){
                statusSwitch.setChecked(true);
                tvStatus.setText("Aktif");
            } else{
                statusSwitch.setChecked(false);
                tvStatus.setText("Tidak Aktif");
            }

            tvProductName.setText(productName);
            tvBuyPrice.setText("Beli: "+productBuyPrice);
            tvPriceSell.setText("Jual: "+productSellPrice);

            statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
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
            });

            ivShowDetail.setOnClickListener(view -> {
                String productDataUID=productName.replaceAll(" ", "").toLowerCase();
                Intent i = new Intent(context, UpdtProdActivity.class);
                i.putExtra("key", productDataUID);
                context.startActivity(i);
            });


            if (Objects.equals(productName, "JASA ANGKUT")){
                btnDeleteWrap.setVisibility(View.GONE);
                statusSwitch.setEnabled(false);
            }

            btn1.setOnClickListener(view ->
                    dialogInterfaceUtils.deleteProductDataConfirmation(context, productName.replaceAll(" ", "").toLowerCase()));
        }
    }
}
