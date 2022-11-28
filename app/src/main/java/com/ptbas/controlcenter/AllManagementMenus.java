package com.ptbas.controlcenter;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.ptbas.controlcenter.adapter.MainFeaturesMenuAdapter;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.model.MainFeatureModel;

import java.util.ArrayList;

public class AllManagementMenus extends AppCompatActivity {

    Helper helper = new Helper();
    RecyclerView rvOperationalFeatures, rvDataMasterFeatures;
    MainFeaturesMenuAdapter mainFeaturesMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_management_menus);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Menu Utama");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        rvOperationalFeatures = findViewById(R.id.rv_operational_features);
        rvDataMasterFeatures = findViewById(R.id.rv_data_master_features);


        // ADAPTER FOR MAIN FEATURES
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvOperationalFeatures.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
            rvOperationalFeatures.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 6);
            rvOperationalFeatures.setLayoutManager(mLayoutManager);
        }

        mainFeaturesMenuAdapter = new MainFeaturesMenuAdapter(dataQueue2(),getApplicationContext(),0);
        rvDataMasterFeatures.setAdapter(mainFeaturesMenuAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.CENTER);

        rvDataMasterFeatures.setLayoutManager(layoutManager);

        mainFeaturesMenuAdapter = new MainFeaturesMenuAdapter(dataQueue1(),getApplicationContext(),1);
        rvOperationalFeatures.setAdapter(mainFeaturesMenuAdapter);

        /*mainFeaturesMenuAdapter = new MainFeaturesMenuAdapter(dataQueue2(),getApplicationContext());
        rvDataMasterFeatures.setAdapter(mainFeaturesMenuAdapter);*/
    }

    public ArrayList<MainFeatureModel> dataQueue1(){

        ArrayList<MainFeatureModel> holder = new ArrayList<>();

        MainFeatureModel mRO = new MainFeatureModel();
        mRO.setHeader("Data\nReceived Order");
        mRO.setImgName(R.drawable.ic_purchase_order);
        holder.add(mRO);

        MainFeatureModel mGI = new MainFeatureModel();
        mGI.setHeader("Data\nGood Issue");
        mGI.setImgName(R.drawable.ic_good_issue);
        holder.add(mGI);

        MainFeatureModel mRKPGI = new MainFeatureModel();
        mRKPGI.setHeader("Data Rekap\nGood Issue");
        mRKPGI.setImgName(R.drawable.ic_recap_good_issue);
        holder.add(mRKPGI);

        MainFeatureModel mCO = new MainFeatureModel();
        mCO.setHeader("Data\nCash Out");
        mCO.setImgName(R.drawable.ic_bkk);
        holder.add(mCO);

        MainFeatureModel mInv = new MainFeatureModel();
        mInv.setHeader("Data\nInvoice");
        mInv.setImgName(R.drawable.ic_invoice);
        holder.add(mInv);

        MainFeatureModel mAioRprt = new MainFeatureModel();
        mAioRprt.setHeader("Data Laporan\nAll-in-One");
        mAioRprt.setImgName(R.drawable.ic_aioreport);
        holder.add(mAioRprt);

        return holder;
    }

    public ArrayList<MainFeatureModel> dataQueue2(){

        ArrayList<MainFeatureModel> holder = new ArrayList<>();

        MainFeatureModel mUsr = new MainFeatureModel();
        mUsr.setHeader("Data\nPengguna");
        mUsr.setImgName(R.drawable.ic_manage_user);

        MainFeatureModel mMat = new MainFeatureModel();
        mMat.setHeader("Data\nMaterial");
        mMat.setImgName(R.drawable.ic_material);

        MainFeatureModel mSup = new MainFeatureModel();
        mSup.setHeader("Data\nSupplier");
        mSup.setImgName(R.drawable.ic_supplier);

        MainFeatureModel mCust = new MainFeatureModel();
        mCust.setHeader("Data\nCustomer");
        mCust.setImgName(R.drawable.ic_manage_customers);

        MainFeatureModel mVhl = new MainFeatureModel();
        mVhl.setHeader("Data\nArmada");
        mVhl.setImgName(R.drawable.ic_manage_vehicle);

        MainFeatureModel mBank = new MainFeatureModel();
        mBank.setHeader("Data\nBank");
        mBank.setImgName(R.drawable.ic_bank);

        MainFeatureModel mBankAccount = new MainFeatureModel();
        mBankAccount.setHeader("Data\nRekening Bank");
        mBankAccount.setImgName(R.drawable.ic_bank_account);

        MainFeatureModel mCurrency = new MainFeatureModel();
        mCurrency.setHeader("Data\nMata Uang");
        mCurrency.setImgName(R.drawable.ic_currency);

        holder.add(mUsr);
        holder.add(mMat);
        holder.add(mSup);
        holder.add(mCust);
        holder.add(mVhl);
        holder.add(mBank);
        holder.add(mBankAccount);
        holder.add(mCurrency);
        return holder;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}