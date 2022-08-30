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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.CustomerManagementAdapter;
import com.ptbas.controlcenter.create.AddCustomerActivity;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Collections;

public class CustomerManagementActivity extends AppCompatActivity {

    CustomerManagementAdapter customerManagementAdapter;
    Helper helper = new Helper();
    LinearLayout llNoData;
    FloatingActionButton fabAddCustomerData;
    NestedScrollView nestedScrollView;
    RecyclerView rv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<CustomerModel> customerModelArrayList = new ArrayList<>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_management);

        context = this;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Manajemen Customer");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        fabAddCustomerData = findViewById(R.id.fabAddCustomerData);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        llNoData = findViewById(R.id.ll_no_data);
        rv = findViewById(R.id.rvList);

        showDataDefaultQuery();

        fabAddCustomerData.setOnClickListener(view -> {
            Intent i = new Intent(this, AddCustomerActivity.class);
            startActivity(i);
        });
    }

    private void showDataDefaultQuery() {
        db.collection("CustomerData").orderBy("custUID")
                .addSnapshotListener((value, error) -> {
                    customerModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            CustomerModel customerModel = d.toObject(CustomerModel.class);
                            customerModelArrayList.add(customerModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(customerModelArrayList);
                    customerManagementAdapter = new CustomerManagementAdapter(context, customerModelArrayList);
                    rv.setAdapter(customerManagementAdapter);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        helper.refreshDashboard(this.getApplicationContext());
        finish();
        super.onBackPressed();
    }
}