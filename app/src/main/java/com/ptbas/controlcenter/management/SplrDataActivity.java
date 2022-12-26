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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.SplrDataAdapter;
import com.ptbas.controlcenter.create.AddSplrActivity;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.model.SplrModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.util.ArrayList;
import java.util.Collections;

public class SplrDataActivity extends AppCompatActivity {

    SplrDataAdapter splrDataAdapter;
    HelperUtils helperUtils = new HelperUtils();
    LinearLayout llNoData;
    FloatingActionButton fabAddVhlData;
    NestedScrollView nestedScrollView;
    RecyclerView rv;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SupplierData");
    ArrayList<SplrModel> splrModelArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_splr);

        context = this;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Supplier");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        fabAddVhlData = findViewById(R.id.fabAddVhlData);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        llNoData = findViewById(R.id.ll_no_data);
        rv = findViewById(R.id.rvList);

        showDataDefaultQuery();

        fabAddVhlData.setOnClickListener(view -> {
            Intent i = new Intent(this, AddSplrActivity.class);
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
                    splrModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            SplrModel splrModel = d.toObject(SplrModel.class);
                            splrModelArrayList.add(splrModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(splrModelArrayList);
                    splrDataAdapter = new SplrDataAdapter(context, splrModelArrayList);
                    rv.setAdapter(splrDataAdapter);
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