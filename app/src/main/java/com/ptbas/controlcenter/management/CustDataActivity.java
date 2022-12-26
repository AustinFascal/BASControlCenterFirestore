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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.databinding.ActivityDataCustBinding;
import com.ptbas.controlcenter.databinding.ActivityDataGiBinding;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.CustDataAdapter;
import com.ptbas.controlcenter.create.AddCustActivity;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.util.ArrayList;

public class CustDataActivity extends AppCompatActivity {

    CustDataAdapter custDataAdapter;
    HelperUtils helperUtils = new HelperUtils();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<CustModel> custModelArrayList = new ArrayList<>();
    Context context;

    ActivityDataCustBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDataCustBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        context = this;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Customer");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        showDataDefaultQuery();

        binding.fabAddCustomerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddCustActivity.class);
                startActivity(i);
            }
        });
    }

    private void showDataDefaultQuery() {
        db.collection("CustomerData").orderBy("custUID")
                .addSnapshotListener((value, error) -> {
                    custModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            CustModel custModel = d.toObject(CustModel.class);
                            custModelArrayList.add(custModel);
                        }
                        binding.llNoData.setVisibility(View.GONE);
                        binding.nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        binding.llNoData.setVisibility(View.VISIBLE);
                        binding.nestedScrollView.setVisibility(View.GONE);
                    }
                    //Collections.reverse(customerModelArrayList);
                    custDataAdapter = new CustDataAdapter(context, custModelArrayList);
                    binding.recyclerView.setAdapter(custDataAdapter);
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
            binding.recyclerView.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            binding.recyclerView.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            binding.recyclerView.setLayoutManager(mLayoutManager);
        }

        showDataDefaultQuery();
    }

    @Override
    public void onBackPressed() {
        /*helper.refreshDashboard(this.getApplicationContext());
        finish();*/
        super.onBackPressed();
    }
}