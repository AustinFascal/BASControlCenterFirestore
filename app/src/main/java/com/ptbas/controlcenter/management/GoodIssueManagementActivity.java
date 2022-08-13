package com.ptbas.controlcenter.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.DragLinearLayout;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.RecapGoodIssueDataActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoodIssueManagementActivity extends AppCompatActivity {

    String dateStart = "", dateEnd = "", searchTypeData="";
    String monthStrVal, dayStrVal;

    LinearLayout llNoData;
    Context context;
    CardView cdvFilter;
    FloatingActionsMenu fabExpandMenu;
    FloatingActionButton fabActionCreateGi, fabActionRecapData;
    NestedScrollView nestedScrollView;
    TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;
    ImageButton btnGiSearchByDateReset, btnGiSearchByStatusReset, btnGiSearchByTypeReset,
            btnGiSearchByNameTypeReset, btnGiSearchByCompanyReset;
    AutoCompleteTextView spinnerApprovalStatus, spinnerInvoicedStatus, spinnerSearchType,
            spinnerMaterialName, spinnerMaterialType, spinnerCompanyName;
    LinearLayout wrapSearchBySpinner, wrapFilter, llWrapFilterByStatus, llWrapFilterByDateRange,
            llWrapFilterByNameType, llWrapFilterByCompany;
    Button imgbtnExpandCollapseFilterLayout;
    DatePickerDialog datePicker;
    RecyclerView rvGoodIssueList;

    Helper helper = new Helper();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();

    GIManagementAdapter giManagementAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    View firstViewData;

    List<String> arrayListMaterialName, arrayListCompanyName;

    String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID"};


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_issue_management);

        context = this;

        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionCreateGi = findViewById(R.id.fab_action_create_ro);
        fabActionRecapData = findViewById(R.id.fab_action_recap_data);

        cdvFilter = findViewById(R.id.cdv_filter);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        spinnerApprovalStatus = findViewById(R.id.spinner_approval_status);
        spinnerInvoicedStatus = findViewById(R.id.spinner_invoiced_status);
        spinnerSearchType = findViewById(R.id.spinner_search_type);
        spinnerMaterialName = findViewById(R.id.spinner_mat_name);
        spinnerMaterialType = findViewById(R.id.spinner_mat_type);
        spinnerCompanyName = findViewById(R.id.spinner_company_name);
        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);
        wrapFilter = findViewById(R.id.wrap_filter);
        llWrapFilterByStatus = findViewById(R.id.ll_wrap_filter_by_status);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llWrapFilterByNameType = findViewById(R.id.ll_wrap_filter_by_name_and_type);
        llWrapFilterByCompany = findViewById(R.id.ll_wrap_filter_by_company);
        llNoData = findViewById(R.id.ll_no_data);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtn_expand_collapse_filter_layout);
        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByStatusReset = findViewById(R.id.btn_gi_search_status_reset);
        btnGiSearchByTypeReset = findViewById(R.id.btn_gi_search_by_type_reset);
        btnGiSearchByNameTypeReset = findViewById(R.id.btn_gi_search_nametype_reset);
        btnGiSearchByCompanyReset = findViewById(R.id.btn_gi_search_company_reset);

        ActionBar actionBar = getSupportActionBar();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Manajemen Good Issue");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // DRAGLINEARLAYOUT FOR FILTERING
        DragLinearLayout dragLinearLayout = findViewById(R.id.drag_linear_layout);
        for(int i = 0; i < dragLinearLayout.getChildCount(); i++){
            View child = dragLinearLayout.getChildAt(i);
            // the child will act as its own drag handle
            dragLinearLayout.setViewDraggable(child, child);
        }

        dragLinearLayout.setOnViewSwapListener((firstView, firstPosition,
                                                secondView, secondPosition) -> {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100,
                        VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(100);
            }
            firstViewData = firstView;
        });

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        // INIT ARRAYS AND ADAPTER FOR FILTERING
        String[] approvalStatus = {"Valid", "Belum Valid"};
        String[] invoicedStatus = {"Sudah", "Belum"};
        String[] searchType = {"ID Good Issue", "ID Received Order", "Nomor PO Customer", "NOPOL Kendaraan"};
        String[] materialType = {"CURAH", "BORONG"};
        ArrayList<String> arrayListApprovalStatus = new ArrayList<>(Arrays.asList(approvalStatus));
        ArrayList<String> arrayListInvoicedStatus = new ArrayList<>(Arrays.asList(invoicedStatus));
        ArrayList<String> arrayListSearchType = new ArrayList<>(Arrays.asList(searchType));
        ArrayList<String> arrayListMaterialType = new ArrayList<>(Arrays.asList(materialType));
        ArrayAdapter<String> arrayAdapterApprovalStatus = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListApprovalStatus);
        ArrayAdapter<String> arrayAdapterInvoicedStatus = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListInvoicedStatus);
        ArrayAdapter<String> arrayAdapterSearchType = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListSearchType);
        ArrayAdapter<String> arrayAdapterMaterialType = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListMaterialType);
        spinnerApprovalStatus.setAdapter(arrayAdapterApprovalStatus);
        spinnerInvoicedStatus.setAdapter(arrayAdapterInvoicedStatus);
        spinnerSearchType.setAdapter(arrayAdapterSearchType);
        spinnerMaterialType.setAdapter(arrayAdapterMaterialType);

        arrayListMaterialName = new ArrayList<>();
        arrayListCompanyName = new ArrayList<>();
        databaseReference.child("ProductData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialName = dataSnapshot.child("productName").getValue(String.class);
                        arrayListMaterialName.add(spinnerMaterialName);
                        arrayListMaterialName.remove("JASA ANGKUT");
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GoodIssueManagementActivity.this, R.layout.style_spinner, arrayListMaterialName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerMaterialName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(GoodIssueManagementActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("CustomerData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerCompanyName = dataSnapshot.child("custUID").getValue(String.class)+" - "+dataSnapshot.child("custName").getValue(String.class);
                        arrayListCompanyName.add(spinnerCompanyName);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GoodIssueManagementActivity.this, R.layout.style_spinner, arrayListCompanyName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerCompanyName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(GoodIssueManagementActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // GO TO ADD GOOD ISSUE ACTIVITY
        fabActionCreateGi.setOnClickListener(view -> {
            Intent intent = new Intent(GoodIssueManagementActivity.this, AddGoodIssueActivity.class);
            startActivity(intent);
        });

        // GO TO RECAP GOOD ISSUE ACTIVITY
        fabActionRecapData.setOnClickListener(view -> {
            Intent intent = new Intent(GoodIssueManagementActivity.this, RecapGoodIssueDataActivity.class);
            startActivity(intent);
        });

        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();

        // HANDLE RECYCLERVIEW GI WHEN SCROLLING
        rvGoodIssueList.setOnTouchListener((v, event) -> {
            switch ( event.getAction( ) ) {
                case MotionEvent.ACTION_SCROLL:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_DOWN:
                    fabExpandMenu.animate().translationY(800).setDuration(100).start();
                    fabExpandMenu.collapse();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    fabExpandMenu.animate().translationY(0).setDuration(100).start();
                    break;
            }
            return false;
        });



        // HANDLE FILTER COMPONENTS WHEN ON CLICK
        edtGiDateFilterStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(GoodIssueManagementActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {
                        int monthInt = month + 1;

                        if(month < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }
                        if(dayOfMonth < 10){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtGiDateFilterStart.setText(finalDate);
                        dateStart = finalDate;

                        showDataSearchByDateRange();
                        resetSearchByStatus();
                        resetSearchByCompany();
                        resetSearchByNameType();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();
        });
        edtGiDateFilterEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(GoodIssueManagementActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {

                        int monthInt = month + 1;

                        if(month < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }
                        if(dayOfMonth < 10){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtGiDateFilterEnd.setText(finalDate);
                        dateEnd = finalDate;

                        resetSearchByCompany();
                        resetSearchByNameType();
                        showDataSearchByDateRange();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                        resetSearchByStatus();

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();
        });

        spinnerSearchType.setOnItemClickListener((adapterView, view, i, l) -> {
            btnGiSearchByTypeReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    searchTypeData = searchTypeValue[0];
                    break;
                case 1:
                    searchTypeData = searchTypeValue[1];
                    break;
                case 2:
                    searchTypeData = searchTypeValue[2];
                    break;
                case 3:
                    searchTypeData = searchTypeValue[3];
                    break;
                default:
                    break;
            }
        });
        spinnerApprovalStatus.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerInvoicedStatus.setText(null);
            resetSearchByDate();
            resetSearchByCompany();
            resetSearchByNameType();
            btnGiSearchByStatusReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    showDataSearchByApprovalStatus(true);
                    break;
                case 1:
                    showDataSearchByApprovalStatus(false);
                    break;
                default:
                    break;
            }
        });
        spinnerInvoicedStatus.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerApprovalStatus.setText(null);
            resetSearchByDate();
            resetSearchByCompany();
            resetSearchByNameType();
            btnGiSearchByStatusReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    showDataSearchByInvoicedStatus(true);
                    break;
                case 1:
                    showDataSearchByInvoicedStatus(false);
                    break;
                default:
                    break;
            }
        });
        spinnerMaterialName.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedMaterialName = (String) adapterView.getItemAtPosition(i);
            spinnerMaterialType.setText(null);
            resetSearchByDate();
            resetSearchByCompany();
            resetSearchByStatus();
            btnGiSearchByNameTypeReset.setVisibility(View.VISIBLE);
            showDataSearchByMaterialNameType("MaterialName", selectedMaterialName);
        });
        spinnerMaterialType.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedMaterialType = (String) adapterView.getItemAtPosition(i);
            spinnerMaterialName.setText(null);
            resetSearchByDate();
            resetSearchByCompany();
            resetSearchByStatus();
            btnGiSearchByNameTypeReset.setVisibility(View.VISIBLE);
            showDataSearchByMaterialNameType("MaterialType", selectedMaterialType);
        });
        spinnerCompanyName.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedCompanyID = (String) adapterView.getItemAtPosition(i);
            String companyID = selectedCompanyID.substring(0,8);
            resetSearchByDate();
            resetSearchByNameType();
            resetSearchByStatus();
            btnGiSearchByCompanyReset.setVisibility(View.VISIBLE);
            showDataSearchByCompanyID(companyID);
        });

        btnGiSearchByTypeReset.setOnClickListener(view -> resetSearchByType());
        btnGiSearchByDateReset.setOnClickListener(view -> {
            resetSearchByDate();
            showDataDefaultQuery();
        });
        btnGiSearchByStatusReset.setOnClickListener(view -> {
            resetSearchByStatus();
            showDataDefaultQuery();
        });
        btnGiSearchByNameTypeReset.setOnClickListener(view -> {
            resetSearchByNameType();
            showDataDefaultQuery();
        });
        btnGiSearchByCompanyReset.setOnClickListener(view -> {
            resetSearchByCompany();
            showDataDefaultQuery();
        });

        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_good_issue_management, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_status);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();

            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });
    }

    private void expandFilterViewValidation() {
        if (expandStatus){
            showHideFilterComponents(true);
            expandStatus=false;
            imgbtnExpandCollapseFilterLayout.setText(R.string.showMore);
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_down, 0);
        } else {
            showHideFilterComponents(false);
            expandStatus=true;
            imgbtnExpandCollapseFilterLayout.setText(R.string.showLess);
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_up, 0);
        }
    }

    private void showHideFilterComponents(Boolean expandStatus) {
        if (expandStatus){
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_status){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByNameType.setVisibility(View.GONE);
                llWrapFilterByCompany.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByStatus.setVisibility(View.GONE);
                llWrapFilterByNameType.setVisibility(View.GONE);
                llWrapFilterByCompany.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_name_and_type){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByStatus.setVisibility(View.GONE);
                llWrapFilterByCompany.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_company){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByStatus.setVisibility(View.GONE);
                llWrapFilterByNameType.setVisibility(View.GONE);
            }
        } else {
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_status){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByNameType.setVisibility(View.VISIBLE);
                llWrapFilterByCompany.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByStatus.setVisibility(View.VISIBLE);
                llWrapFilterByNameType.setVisibility(View.VISIBLE);
                llWrapFilterByCompany.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_name_and_type){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByStatus.setVisibility(View.VISIBLE);
                llWrapFilterByCompany.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_company){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByStatus.setVisibility(View.VISIBLE);
                llWrapFilterByNameType.setVisibility(View.VISIBLE);
            }
        }
    }

    private void resetSearchByType() {
        spinnerSearchType.setText("");
        spinnerSearchType.clearFocus();
        btnGiSearchByTypeReset.setVisibility(View.GONE);
    }

    private void resetSearchByStatus() {
        spinnerApprovalStatus.setText(null);
        spinnerInvoicedStatus.setText(null);
        spinnerApprovalStatus.clearFocus();
        spinnerInvoicedStatus.clearFocus();
        btnGiSearchByStatusReset.setVisibility(View.GONE);
    }

    private void resetSearchByNameType(){
        spinnerMaterialName.setText(null);
        spinnerMaterialType.setText(null);
        spinnerMaterialName.clearFocus();
        spinnerMaterialType.clearFocus();
        btnGiSearchByNameTypeReset.setVisibility(View.GONE);
    }

    private void resetSearchByCompany(){
        spinnerCompanyName.setText(null);
        spinnerCompanyName.clearFocus();
        btnGiSearchByCompanyReset.setVisibility(View.GONE);
    }

    private void resetSearchByDate() {
        btnGiSearchByDateReset.setVisibility(View.GONE);
        edtGiDateFilterStart.setText(null);
        edtGiDateFilterEnd.setText(null);
        dateStart = "";
        dateEnd = "";
    }

    private void showDataSearchByInvoicedStatus(boolean b) {
        Query query = databaseReference.child("GoodIssueData").orderByChild("giInvoiced").startAt(b).endAt(b);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        goodIssueModelArrayList.add(goodIssueModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSearchByApprovalStatus(boolean b) {
        Query query = databaseReference.child("GoodIssueData").orderByChild("giStatus").startAt(b).endAt(b);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if  (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        goodIssueModelArrayList.add(goodIssueModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSearchByMaterialNameType(String type, String data) {
        Query query1 = databaseReference.child("GoodIssueData").orderByChild("giMatName").startAt(data).endAt(data);
        Query query2 = databaseReference.child("GoodIssueData").orderByChild("giMatType").startAt(data).endAt(data);
        if (type.equals("MaterialName")){
            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    goodIssueModelArrayList.clear();
                    if  (snapshot.exists()){
                        for (DataSnapshot item : snapshot.getChildren()){
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else {
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(goodIssueModelArrayList);
                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                    rvGoodIssueList.setAdapter(giManagementAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (type.equals("MaterialType")){
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    goodIssueModelArrayList.clear();
                    if  (snapshot.exists()){
                        for (DataSnapshot item : snapshot.getChildren()){
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                        }llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else {
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(goodIssueModelArrayList);
                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                    rvGoodIssueList.setAdapter(giManagementAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void showDataSearchByDateRange(){
        if (dateStart.isEmpty()||dateEnd.isEmpty()){
            showDataDefaultQuery();
        }

        if (!dateStart.isEmpty()&&dateEnd.isEmpty()) {
            Query query = databaseReference.child("GoodIssueData")
                    .orderByChild("giDateCreated").startAt(dateStart);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    showListener(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            Toast.makeText(context, "Mohon masukkan tanggal awal terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            Toast.makeText(context, "Mohon masukkan tanggal awal dan dan tanggal akhir", Toast.LENGTH_SHORT).show();
        }
        if (!dateStart.isEmpty()&&!dateEnd.isEmpty()){
            Query query = databaseReference.child("GoodIssueData")
                    .orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    showListener(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void showDataSearchByType(String newText, String searchTypeData) {
        Query query = databaseReference.child("GoodIssueData").orderByChild(searchTypeData);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if  (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);

                        if (searchTypeData.equals(searchTypeValue[0])){
                            if(goodIssueModel.getGiUID().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[1])){
                            if(goodIssueModel.getGiRoUID().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[2])){
                            if(goodIssueModel.getGiPoCustNumber().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[3])){
                            if(goodIssueModel.getVhlUID().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                    }

                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                }else{
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }


                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSearchByCompanyID(String selectedCompanyID) {

        Query query = databaseReference.child("GoodIssueData").orderByChild("giRoUID");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        if (item.child("giRoUID").getValue(String.class).contains(selectedCompanyID)){
                            llNoData.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                        } else {
                            llNoData.setVisibility(View.VISIBLE);
                            nestedScrollView.setVisibility(View.GONE);
                        }
                    }

                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }


                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showListener(DataSnapshot snapshot) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()) {
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                goodIssueModelArrayList.add(goodIssueModel);
            }
            llNoData.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
        } else{
            llNoData.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
        }

        if (snapshot.getChildrenCount()==0){
            llNoData.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }

        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);
    }

    private void showDataDefaultQuery() {
        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                        }
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }

                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.query_search_menu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        };

        menu.findItem(R.id.search_data).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_data).getActionView();
        searchView.setQueryHint("Kata Kunci");
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchView.setOnSearchClickListener(view -> {
            cdvFilter.setVisibility(View.VISIBLE);
            wrapSearchBySpinner.setVisibility(View.VISIBLE);
            wrapFilter.setVisibility(View.GONE);
            imgbtnExpandCollapseFilterLayout.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        searchView.setOnCloseListener(() -> {
            cdvFilter.setVisibility(View.GONE);
            wrapSearchBySpinner.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (spinnerSearchType.getText().toString().isEmpty()){
                    spinnerSearchType.setError("Mohon pilih kategori pencarian terlebih dahulu");
                } else {
                    spinnerSearchType.setError(null);
                    if (searchView.getQuery().toString().isEmpty()){
                        showDataDefaultQuery();
                    } else {
                        showDataSearchByType(newText, searchTypeData);
                    }
                }

                return true;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_data) {
            imgbtnExpandCollapseFilterLayout.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            if (cdvFilter.getVisibility() == View.GONE) {
                cdvFilter.setVisibility(View.VISIBLE);
                wrapFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            } else {
                cdvFilter.setVisibility(View.GONE);
                wrapFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            }
            return true;
        }
        helper.refreshDashboard(this.getApplicationContext());
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDataDefaultQuery();
    }

    @Override
    public void onBackPressed() {
        firstViewDataFirstTimeStatus = true;
        helper.refreshDashboard(this.getApplicationContext());
        super.onBackPressed();
    }
}