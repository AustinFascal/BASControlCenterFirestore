package com.ptbas.controlcenter.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.ProductDataManagementAdapter;
import com.ptbas.controlcenter.create.AddProductData;
import com.ptbas.controlcenter.model.ProductModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;

public class ManageProductDataActivity extends AppCompatActivity {

    ProductDataManagementAdapter productDataManagementAdapter;
    Helper helper = new Helper();
    LinearLayout llNoData;
    FloatingActionButton fabAddMaterialData;
    NestedScrollView nestedScrollView;
    RecyclerView rv;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductData");
    ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product_data);

        context = this;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Material");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        fabAddMaterialData = findViewById(R.id.fabAddMaterialData);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        llNoData = findViewById(R.id.ll_no_data);
        rv = findViewById(R.id.rvList);

        showDataDefaultQuery();

        fabAddMaterialData.setOnClickListener(view -> {
            Intent i = new Intent(this, AddProductData.class);
            startActivity(i);
        });
    }

    private void showDataDefaultQuery() {
        Query query = databaseReference;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        ProductModel productModel = item.getValue(ProductModel.class);
                        productModelArrayList.add(productModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                productDataManagementAdapter = new ProductDataManagementAdapter(context, productModelArrayList);
                rv.setAdapter(productDataManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (productModelArrayList.size()<1){
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // HANDLE RESPONSIVE CONTENT
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rv.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rv.setLayoutManager(mLayoutManager);
        }

        showDataDefaultQuery();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        /*helper.refreshDashboard(this.getApplicationContext());
        finish();*/
        super.onBackPressed();
    }
}