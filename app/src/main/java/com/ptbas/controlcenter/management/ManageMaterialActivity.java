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
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.VehicleDataManagementAdapter;
import com.ptbas.controlcenter.create.AddVehicleActivity;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;

public class ManageMaterialActivity extends AppCompatActivity {

    VehicleDataManagementAdapter vehicleDataManagementAdapter;
    Helper helper = new Helper();
    LinearLayout llNoData;
    FloatingActionButton fabAddVhlData;
    NestedScrollView nestedScrollView;
    RecyclerView rv;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VehicleData");
    ArrayList<VehicleModel> vehicleModelArrayList = new ArrayList<>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_material);

        context = this;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Armada");

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
            Intent i = new Intent(this, AddVehicleActivity.class);
            startActivity(i);
        });
    }

    private void showDataDefaultQuery() {
        Query query = databaseReference;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vehicleModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        VehicleModel vehicleModel = item.getValue(VehicleModel.class);
                        vehicleModelArrayList.add(vehicleModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                vehicleDataManagementAdapter = new VehicleDataManagementAdapter(context, vehicleModelArrayList);
                rv.setAdapter(vehicleDataManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (vehicleModelArrayList.size()<1){
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