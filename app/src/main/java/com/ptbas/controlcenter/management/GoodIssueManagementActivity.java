package com.ptbas.controlcenter.management;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.DragLinearLayout;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.RecapGoodIssueDataActivity;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoodIssueManagementActivity extends AppCompatActivity {

    String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID", "giMatName"};
    String dateStart = "", dateEnd = "", searchTypeData="", monthStrVal, dayStrVal;

    ChipGroup chipGroup;
    Chip chip_filter_all, chip_filter_status_valid, chip_filter_status_invalid,
            chip_filter_status_invoiced, chip_filter_status_not_yet_invoiced,
            chip_filter_status_transport_type_curah, chip_filter_status_transport_type_borong;
    LinearLayout llNoData, ll_wrap_filter_chip_group, wrapSearchBySpinner, wrapFilter,
            llWrapFilterByDateRange;
    Context context;
    CardView cdvFilter;
    FloatingActionsMenu fabExpandMenu;
    FloatingActionButton fabActionCreateGi, fabActionRecapData;
    NestedScrollView nestedScrollView;
    TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;
    AutoCompleteTextView spinnerSearchType;
    ImageButton btnGiSearchByDateReset, btnGiSearchByTypeReset;
    DatePickerDialog datePicker;
    RecyclerView rvGoodIssueList;

    Boolean expandStatus = true;
    List<String> arrayListMaterialName, arrayListCompanyName;
    Helper helper = new Helper();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();

    GIManagementAdapter giManagementAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DialogInterface dialogInterface = new DialogInterface();

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
        spinnerSearchType = findViewById(R.id.spinner_search_type);
        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);
        wrapFilter = findViewById(R.id.wrap_filter);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llNoData = findViewById(R.id.ll_no_data);
        ll_wrap_filter_chip_group = findViewById(R.id.ll_wrap_filter_chip_group);
        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByTypeReset = findViewById(R.id.btn_gi_search_by_type_reset);

        chipGroup = findViewById(R.id.chip_group_filter_query);
        chip_filter_all  = findViewById(R.id.chip_filter_all);
        chip_filter_status_valid = findViewById(R.id.chip_filter_status_valid);
        chip_filter_status_invalid = findViewById(R.id.chip_filter_status_invalid);
        chip_filter_status_invoiced = findViewById(R.id.chip_filter_status_invoiced);
        chip_filter_status_not_yet_invoiced = findViewById(R.id.chip_filter_status_not_yet_invoiced);
        chip_filter_status_transport_type_curah = findViewById(R.id.chip_filter_status_transport_type_curah);
        chip_filter_status_transport_type_borong = findViewById(R.id.chip_filter_status_transport_type_borong);

        ActionBar actionBar = getSupportActionBar();

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

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        // INIT ARRAYS AND ADAPTER FOR FILTERING
        String[] searchType = {"ID Good Issue", "ID Received Order", "Nomor PO Customer", "NOPOL Kendaraan", "Nama Material"};
        ArrayList<String> arrayListSearchType = new ArrayList<>(Arrays.asList(searchType));
        ArrayAdapter<String> arrayAdapterSearchType = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListSearchType);
        spinnerSearchType.setAdapter(arrayAdapterSearchType);

        arrayListMaterialName = new ArrayList<>();
        arrayListCompanyName = new ArrayList<>();

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

                        checkSelectedChipFilter();

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

                        checkSelectedChipFilter();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);

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
                case 4:
                    searchTypeData = searchTypeValue[4];
                    break;
                default:
                    break;
            }
        });

        btnGiSearchByDateReset.setOnClickListener(view -> {
            resetSearchByDate();
            checkSelectedChipFilter();
        });

        btnGiSearchByTypeReset.setOnClickListener(view -> resetSearchByType());

        chip_filter_all.isChecked();
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(group.getCheckedChipId())){
                if (group.getCheckedChipId() == chip_filter_all.getId()){
                    showDataDefaultQuery();
                }
                if (group.getCheckedChipId() == chip_filter_status_valid.getId()){
                    showDataSearchByApprovalStatus(true);
                }
                if (group.getCheckedChipId() == chip_filter_status_invalid.getId()){
                    showDataSearchByApprovalStatus(false);
                }
                if (group.getCheckedChipId() == chip_filter_status_invoiced.getId()){
                    showDataSearchByInvoicedStatus(true);
                }
                if (group.getCheckedChipId() == chip_filter_status_not_yet_invoiced.getId()){
                    showDataSearchByInvoicedStatus(false);
                }
                if (group.getCheckedChipId() == chip_filter_status_transport_type_curah.getId()){
                    showDataSearchByMaterialType("CURAH");
                }
                if (group.getCheckedChipId() == chip_filter_status_transport_type_borong.getId()){
                    showDataSearchByMaterialType("BORONG");
                }
            } else {
                showDataDefaultQuery();
            }
        });
    }

    private void checkSelectedChipFilter() {
        if (chip_filter_all.isChecked()){
            showDataDefaultQuery();
        }
        if (chip_filter_status_valid.isChecked()){
            showDataSearchByApprovalStatus(true);
        }
        if (chip_filter_status_invalid.isChecked()){
            showDataSearchByApprovalStatus(false);
        }
        if (chip_filter_status_invoiced.isChecked()){
            showDataSearchByInvoicedStatus(true);
        }
        if (chip_filter_status_not_yet_invoiced.isChecked()){
            showDataSearchByInvoicedStatus(false);
        }
        if (chip_filter_status_transport_type_curah.isChecked()){
            showDataSearchByMaterialType("CURAH");
        }
        if (chip_filter_status_transport_type_borong.isChecked()){
            showDataSearchByMaterialType("BORONG");
        }
    }

    private void resetSearchByType() {
        spinnerSearchType.setText("");
        spinnerSearchType.clearFocus();
        btnGiSearchByTypeReset.setVisibility(View.GONE);
    }

    private void resetSearchByDate() {
        btnGiSearchByDateReset.setVisibility(View.GONE);
        edtGiDateFilterStart.setText(null);
        edtGiDateFilterEnd.setText(null);
        dateStart = "";
        dateEnd = "";
    }

    private void showDataSearchByInvoicedStatus(boolean b) {
        Query query;
        if (dateStart.isEmpty()||dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    invoicedStatusOnDataChange(snapshot, b);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else{
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    invoicedStatusOnDataChange(snapshot, b);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void invoicedStatusOnDataChange(DataSnapshot snapshot, boolean b) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                if (Objects.equals(item.child("giInvoiced").getValue(), b)) {
                    goodIssueModelArrayList.add(goodIssueModel);
                    nestedScrollView.setVisibility(View.VISIBLE);
                    llNoData.setVisibility(View.GONE);
                }
            }
        } else  {
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(goodIssueModelArrayList);
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDataSearchByApprovalStatus(boolean b) {
        Query query;
        if (dateStart.isEmpty()||dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    approvalStatusOnDataChange(snapshot, b);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else{
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    approvalStatusOnDataChange(snapshot, b);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void approvalStatusOnDataChange(DataSnapshot snapshot, boolean b) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                    if (Objects.equals(item.child("giStatus").getValue(), b)) {
                        goodIssueModelArrayList.add(goodIssueModel);
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else  {
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(goodIssueModelArrayList);
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDataSearchByMaterialType(String data) {
        Query query;
        if (dateStart.isEmpty()||dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    materialTypeOnDataChange(snapshot, data);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else{
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    materialTypeOnDataChange(snapshot, data);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void materialTypeOnDataChange(DataSnapshot snapshot, String data) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                    if (Objects.equals(item.child("giMatType").getValue(), data)) {
                        goodIssueModelArrayList.add(goodIssueModel);
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else  {
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(goodIssueModelArrayList);
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDataSearchByType(String newText, String searchTypeData) {
        Query query;
        query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*searchByTypeOnDataChange(snapshot, newText, searchTypeData);*/
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    nestedScrollView.setVisibility(View.VISIBLE);
                    llNoData.setVisibility(View.GONE);
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);

                        if (searchTypeData.equals(searchTypeValue[0])){
                            if(Objects.requireNonNull(goodIssueModel).getGiUID().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[1])){
                            if(Objects.requireNonNull(goodIssueModel).getGiRoUID().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[2])){
                            if(Objects.requireNonNull(goodIssueModel).getGiPoCustNumber().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[3])){
                            if(Objects.requireNonNull(goodIssueModel).getVhlUID().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }

                        if (searchTypeData.equals(searchTypeValue[4])){
                            if(Objects.requireNonNull(goodIssueModel).getGiMatName().contains(newText)) {
                                goodIssueModelArrayList.add(goodIssueModel);
                            }
                        }
                    }
                    Collections.reverse(goodIssueModelArrayList);
                } else  {
                    nestedScrollView.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*if (dateStart.isEmpty()||dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    searchByTypeOnDataChange(snapshot, newText, searchTypeData);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {

        }*/

        /*giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);*/
    }

    private void searchByTypeOnDataChange(DataSnapshot snapshot, String newText, String searchTypeData) {

    }

    private void showDataDefaultQuery() {
        Query query;
        if (dateStart.isEmpty()||dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        } else{
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
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

        if (goodIssueModelArrayList.size()<1){
            nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
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
            ll_wrap_filter_chip_group.setVisibility(View.GONE);
            wrapSearchBySpinner.setVisibility(View.VISIBLE);
            cdvFilter.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        searchView.setOnCloseListener(() -> {
            ll_wrap_filter_chip_group.setVisibility(View.VISIBLE);
            wrapSearchBySpinner.setVisibility(View.GONE);
            cdvFilter.setVisibility(View.GONE);
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
                if (spinnerSearchType.getText().toString().isEmpty() || edtGiDateFilterStart.getText().toString().isEmpty() || edtGiDateFilterEnd.getText().toString().isEmpty()){
                    if (!searchView.getQuery().toString().isEmpty()){
                        dialogInterface.fillSearchFilter(GoodIssueManagementActivity.this, searchView);
                    }
                } else {
                    showDataSearchByType(newText, searchTypeData);
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
            if (expandStatus){
                expandStatus=false;
                //wrapFilter.setVisibility(View.GONE);
                cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            } else {
                expandStatus=true;
                //wrapFilter.setVisibility(View.VISIBLE);
                cdvFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            }
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            return true;
        }
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
            rvGoodIssueList.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvGoodIssueList.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rvGoodIssueList.setLayoutManager(mLayoutManager);
        }
        chip_filter_all.isChecked();
        showDataDefaultQuery();
    }

    @Override
    public void onBackPressed() {
        helper.refreshDashboard(this.getApplicationContext());
        finish();
    }
}