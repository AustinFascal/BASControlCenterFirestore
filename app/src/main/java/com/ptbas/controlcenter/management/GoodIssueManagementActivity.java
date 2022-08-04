package com.ptbas.controlcenter.management;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class GoodIssueManagementActivity extends AppCompatActivity {


    public String dateStart = "", dateEnd = "", searchTypeData="";

    CardView cdvFilter;
    ExtendedFloatingActionButton fabAddGi;

    NestedScrollView nestedScrollView;
    private TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;
    ImageButton btnGiSearchByDateReset, btnGiSearchByStatusReset, btnGiSearchByReset;
    DatePickerDialog datePicker;

    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Helper helper = new Helper();
    Context context;

    AutoCompleteTextView spinnerApprovalStatus, spinnerInvoicedStatus, spinnerSearchType;

    LinearLayout wrapSearchBySpinner, wrapFilter;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_issue_management);

        LangUtils.setLocale(this, "en");

        context = this;
        nestedScrollView = findViewById(R.id.nestedScrollView);

        spinnerApprovalStatus = findViewById(R.id.spinner_approval_status);
        spinnerInvoicedStatus = findViewById(R.id.spinner_invoiced_status);
        spinnerSearchType = findViewById(R.id.spinner_search_type);

        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);
        wrapFilter = findViewById(R.id.wrap_filter);

        String[] approvalStatus = {"Valid", "Belum Valid"};
        String[] invoicedStatus = {"Sudah", "Belum"};
        String[] searchBy = {"ID Good Issue", "ID Received Order", "Nomor PO Customer", "NOPOL Kendaraan"};
        String[] searchByValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID"};
        ArrayList<String> arrayListApprovalStatus = new ArrayList<>(Arrays.asList(approvalStatus));
        ArrayList<String> arrayListInvoicedStatus = new ArrayList<>(Arrays.asList(invoicedStatus));
        ArrayList<String> arrayListSearchType = new ArrayList<>(Arrays.asList(searchBy));
        ArrayAdapter<String> arrayAdapterApprovalStatus = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListApprovalStatus);
        ArrayAdapter<String> arrayAdapterInvoicedStatus = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListInvoicedStatus);
        ArrayAdapter<String> arrayAdapterSearchType = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListSearchType);
        spinnerApprovalStatus.setAdapter(arrayAdapterApprovalStatus);
        spinnerInvoicedStatus.setAdapter(arrayAdapterInvoicedStatus);
        spinnerSearchType.setAdapter(arrayAdapterSearchType);

        fabAddGi = findViewById(R.id.fab_add_gi);
        cdvFilter = findViewById(R.id.cdv_filter);

        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByStatusReset = findViewById(R.id.btn_gi_search_status_reset);
        btnGiSearchByReset = findViewById(R.id.btn_gi_search_by_reset);

        fabAddGi.setOnClickListener(view -> {
            Intent intent = new Intent(GoodIssueManagementActivity.this, AddGoodIssueActivity.class);
            startActivity(intent);
        });

        showData();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Manajemen Good Issue");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.white)));

        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.black)));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.white)));
                break;
        }

        rvGoodIssueList.setOnTouchListener((v, event) -> {
            switch ( event.getAction( ) ) {
                case MotionEvent.ACTION_SCROLL:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_DOWN:
                    fabAddGi.hide();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    fabAddGi.show();
                    break;
            }
            return false;
        });

        edtGiDateFilterStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(GoodIssueManagementActivity.this,
                    (datePicker, year12, month12, dayOfMonth) -> {
                        edtGiDateFilterStart.setText(dayOfMonth + "/" + (month12 + 1) + "/" + year12);
                        dateStart = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
                        searchDate();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, year, month, day);
            datePicker.show();

        });

        edtGiDateFilterEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(GoodIssueManagementActivity.this,
                    (datePicker, year1, month1, dayOfMonth) -> {
                        edtGiDateFilterEnd.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                        dateEnd = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        searchDate();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, year, month, day);
            datePicker.show();

        });

        btnGiSearchByDateReset.setOnClickListener(view -> {
            edtGiDateFilterStart.setText(null);
            edtGiDateFilterEnd.setText(null);
            dateStart = "";
            dateEnd = "";
            showData();
            btnGiSearchByDateReset.setVisibility(View.GONE);
        });

        btnGiSearchByReset.setOnClickListener(view -> {
            spinnerSearchType.setText("");
            spinnerSearchType.clearFocus();
            btnGiSearchByReset.setVisibility(View.GONE);
        });

        btnGiSearchByStatusReset.setOnClickListener(view -> {
            spinnerApprovalStatus.setText("");
            spinnerInvoicedStatus.setText("");
            spinnerApprovalStatus.clearFocus();
            spinnerInvoicedStatus.clearFocus();
            btnGiSearchByStatusReset.setVisibility(View.GONE);
        });

        spinnerSearchType.setOnItemClickListener((adapterView, view, i, l) -> {
            btnGiSearchByReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    searchTypeData = searchByValue[0];
                    break;
                case 1:
                    searchTypeData = searchByValue[1];
                    break;
                case 2:
                    searchTypeData = searchByValue[2];
                    break;
                case 3:
                    searchTypeData = searchByValue[3];
                    break;
                default:
                    break;
            }
        });

        spinnerApprovalStatus.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerInvoicedStatus.setText("");
            btnGiSearchByStatusReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    showDataSearchApprovalStatus(true);
                    break;
                case 1:
                    showDataSearchApprovalStatus(false);
                    break;
                default:
                    break;
            }
        });

        spinnerInvoicedStatus.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerApprovalStatus.setText("");
            btnGiSearchByStatusReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    showDataSearchInvoicedStatus(true);
                    break;
                case 1:
                    showDataSearchInvoicedStatus(false);
                    break;
                default:
                    break;
            }
        });

    }

    private void showDataSearchInvoicedStatus(boolean b) {
        Query query = databaseReference.child("GoodIssueData").orderByChild("giInvoiced").startAt(b).endAt(b);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    goodIssueModelArrayList.add(goodIssueModel);
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

    private void showDataSearchApprovalStatus(boolean b) {
        Query query = databaseReference.child("GoodIssueData").orderByChild("giStatus").startAt(b).endAt(b);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    goodIssueModelArrayList.add(goodIssueModel);
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

    private void searchDate(){
        if (dateStart.isEmpty()||dateEnd.isEmpty()){
            showData();
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

    private void showListener(DataSnapshot snapshot) {
        goodIssueModelArrayList.clear();
        for (DataSnapshot item: snapshot.getChildren()){
            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
            goodIssueModelArrayList.add(goodIssueModel);
        }

        if (snapshot.getChildrenCount()==0){
            //TODO Make bottomsheet or embedded label with lottie
            Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);
    }

    private void showData() {
        databaseReference.child("GoodIssueData").orderByChild("giDateCreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    goodIssueModelArrayList.add(goodIssueModel);
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
        inflater.inflate(R.menu.gi_search_menu, menu);

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

        menu.findItem(R.id.search_gi_data).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_gi_data).getActionView();
        searchView.setQueryHint("Kata Kunci");
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchView.setOnSearchClickListener(view -> {
            cdvFilter.setVisibility(View.VISIBLE);
            wrapSearchBySpinner.setVisibility(View.VISIBLE);
            wrapFilter.setVisibility(View.GONE);
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
                        showData();
                    } else {
                        showDataSearchByType(newText, searchTypeData);
                    }
                }

                return true;
            }
        });
        return true;
    }

    private void showDataSearchByType(String newText, String searchTypeData) {
        Query query = databaseReference.child("GoodIssueData").orderByChild(searchTypeData).startAt(newText).endAt(newText+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    goodIssueModelArrayList.add(goodIssueModel);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_gi_data:

                return true;
            case R.id.filter_gi_data:
                TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
                if (cdvFilter.getVisibility()==View.GONE) {
                    cdvFilter.setVisibility(View.VISIBLE);
                    wrapFilter.setVisibility(View.VISIBLE);
                    item.setIcon(R.drawable.ic_outline_filter_alt_off);
                } else {
                    cdvFilter.setVisibility(View.GONE);
                    wrapFilter.setVisibility(View.GONE);
                    item.setIcon(R.drawable.ic_outline_filter_alt);
                }
                return true;
            default:
                break;
        }
        helper.refreshDashboard(this.getApplicationContext());
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    @Override
    public void onBackPressed() {
        helper.refreshDashboard(this.getApplicationContext());
        super.onBackPressed();
    }
}