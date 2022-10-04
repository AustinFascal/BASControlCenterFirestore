package com.ptbas.controlcenter.management;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.InvoiceManagementAdapter;
import com.ptbas.controlcenter.adapter.SupplierDataManagementAdapter;
import com.ptbas.controlcenter.create.AddSupplierActivity;
import com.ptbas.controlcenter.create.AddVehicleActivity;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.InvoiceModel;
import com.ptbas.controlcenter.model.SupplierModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Collections;

public class ManageSupplierActivity extends AppCompatActivity {

    SupplierDataManagementAdapter supplierDataManagementAdapter;
    Helper helper = new Helper();
    LinearLayout llNoData;
    FloatingActionButton fabAddVhlData;
    NestedScrollView nestedScrollView;
    RecyclerView rv;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SupplierData");
    ArrayList<SupplierModel> supplierModelArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_supplier);

        context = this;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Supplier");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        fabAddVhlData = findViewById(R.id.fabAddVhlData);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        llNoData = findViewById(R.id.ll_no_data);
        rv = findViewById(R.id.rvList);

        showDataDefaultQuery();

        fabAddVhlData.setOnClickListener(view -> {
            Intent i = new Intent(this, AddSupplierActivity.class);
            startActivity(i);
        });
    }

    private void showDataDefaultQuery() {
        /*Query query = databaseReference;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                supplierModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        SupplierModel supplierModel = item.getValue(SupplierModel.class);
                        supplierModelArrayList.add(supplierModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                supplierDataManagementAdapter = new SupplierDataManagementAdapter(context, supplierModelArrayList);
                rv.setAdapter(supplierDataManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        /*if (supplierModelArrayList.size()<1){
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }*/

        db.collection("SupplierData").orderBy("supplierName")
                .addSnapshotListener((value, error) -> {
                    supplierModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            SupplierModel supplierModel = d.toObject(SupplierModel.class);
                            supplierModelArrayList.add(supplierModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(supplierModelArrayList);
                    supplierDataManagementAdapter = new SupplierDataManagementAdapter(context, supplierModelArrayList);
                    rv.setAdapter(supplierDataManagementAdapter);
                });
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
        super.onBackPressed();
    }
}