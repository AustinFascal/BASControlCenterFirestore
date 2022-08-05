package com.ptbas.controlcenter.create;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.ptbas.controlcenter.management.GoodIssueManagementActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecapGoodIssueDataActivity extends AppCompatActivity {

    String dateStartVal = "", dateEndVal = "", rouidVal= "", pouidVal = "";
    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID, spinnerPoUID;
    TextInputEditText edtDateStart, edtDateEnd;
    DatePickerDialog datePicker;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;
    Context context;
    Helper helper = new Helper();
    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    CardView cdvFilter;
    View firstViewData;

    List<String> arrayListRoUID, arrayListPoUID;

    LinearLayout llWrapFilterByDateRange, llWrapFilterByRouid, llWrapFilterByPoCustNumb;

    ImageButton btnGiSearchByDateReset, btnGiSearchByRoUIDReset, btnGiSearchByPoUIDReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_good_issue_data);

        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);
        btnSearchData = findViewById(R.id.caridata);
        spinnerRoUID = findViewById(R.id.rouid);
        spinnerPoUID = findViewById(R.id.pouid);
        edtDateStart = findViewById(R.id.edt_gi_date_filter_start);
        edtDateEnd = findViewById(R.id.edt_gi_date_filter_end);
        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtn_expand_collapse_filter_layout);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llWrapFilterByRouid = findViewById(R.id.ll_wrap_filter_by_rouid);
        llWrapFilterByPoCustNumb = findViewById(R.id.ll_wrap_filter_by_po_cust_numb);

        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByRoUIDReset = findViewById(R.id.btn_gi_search_rouid_reset);
        btnGiSearchByPoUIDReset = findViewById(R.id.btn_gi_search_pouid_reset);

        ActionBar actionBar = getSupportActionBar();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Rekap Data Good Issue");

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

        edtDateStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(RecapGoodIssueDataActivity.this,
                    (datePicker, year12, month12, dayOfMonth) -> {
                        edtDateStart.setText(dayOfMonth + "/" + (month12 + 1) + "/" + year12);
                        dateStartVal = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, year, month, day);
            datePicker.show();
        });

        edtDateEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(RecapGoodIssueDataActivity.this,
                    (datePicker, year12, month12, dayOfMonth) -> {
                        edtDateEnd.setText(dayOfMonth + "/" + (month12 + 1) + "/" + year12);
                        dateEndVal = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, year, month, day);
            datePicker.show();
        });

        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_recap_good_issue_data, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_date_range);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        arrayListRoUID = new ArrayList<>();
        arrayListPoUID = new ArrayList<>();
        databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerRoUIDVal = dataSnapshot.child("roUID").getValue(String.class);
                        arrayListRoUID.add(spinnerRoUIDVal);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, arrayListRoUID);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerRoUID.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(RecapGoodIssueDataActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerPoUIDVal = dataSnapshot.child("roPoCustNumber").getValue(String.class);
                        if (!spinnerPoUIDVal.equals("-")){
                            arrayListPoUID.add(spinnerPoUIDVal);
                        }
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, arrayListPoUID);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoUID.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(RecapGoodIssueDataActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinnerPoUID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btnGiSearchByPoUIDReset.setVisibility(View.VISIBLE);
                spinnerRoUID.setText(null);
                spinnerRoUID.clearFocus();
                btnGiSearchByRoUIDReset.setVisibility(View.GONE);
                spinnerPoUID.setError(null);
            }
        });

        spinnerRoUID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btnGiSearchByRoUIDReset.setVisibility(View.VISIBLE);
                spinnerPoUID.setText(null);
                spinnerPoUID.clearFocus();
                btnGiSearchByPoUIDReset.setVisibility(View.GONE);
                spinnerRoUID.setError(null);
            }
        });

        btnGiSearchByDateReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDateStart.setText(null);
                edtDateEnd.setText(null);
                edtDateStart.clearFocus();
                edtDateEnd.clearFocus();
                btnGiSearchByDateReset.setVisibility(View.GONE);
            }
        });

        btnGiSearchByPoUIDReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerPoUID.setText(null);
                spinnerPoUID.clearFocus();
                btnGiSearchByPoUIDReset.setVisibility(View.GONE);
            }
        });

        btnGiSearchByRoUIDReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerRoUID.setText(null);
                spinnerRoUID.clearFocus();
                btnGiSearchByRoUIDReset.setVisibility(View.GONE);
            }
        });



        btnSearchData.setOnClickListener(view -> {
            rouidVal = spinnerRoUID.getText().toString();
            pouidVal = spinnerPoUID.getText().toString();



                Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStartVal).endAt(dateEndVal);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        goodIssueModelArrayList.clear();
                        for (DataSnapshot item : snapshot.getChildren()){


                                if(item.child("giRoUID").getValue().toString().equals(rouidVal)
                                        ||item.child("giPoCustNumber").getValue().toString().equals(pouidVal)) {
                                    if (item.child("giStatus").getValue().equals(true)){
                                        if (!item.child("giPoCustNumber").getValue().toString().equals("-")){
                                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                            goodIssueModelArrayList.add(goodIssueModel);
                                        }

                                    }
                                }

                        }
                        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                        rvGoodIssueList.setAdapter(giManagementAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        });
    }

    private void expandFilterViewValidation() {
        if (expandStatus){
            showHideFilterComponents(true);
            expandStatus=false;
            imgbtnExpandCollapseFilterLayout.setText("Tampilkan lebih banyak");
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_down, 0);
        } else {
            showHideFilterComponents(false);
            expandStatus=true;
            imgbtnExpandCollapseFilterLayout.setText("Tampilkan lebih sedikit");
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_up, 0);
        }
    }

    private void showHideFilterComponents(Boolean expandStatus) {
        if (expandStatus){
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByRouid.setVisibility(View.GONE);
                llWrapFilterByPoCustNumb.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_rouid){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByPoCustNumb.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_po_cust_numb){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByRouid.setVisibility(View.GONE);
            }

        } else {
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByRouid.setVisibility(View.VISIBLE);
                llWrapFilterByPoCustNumb.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_rouid){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByPoCustNumb.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_po_cust_numb){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByRouid.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recap_gi_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_gi_data) {
            imgbtnExpandCollapseFilterLayout.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            if (cdvFilter.getVisibility() == View.GONE) {
                cdvFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            } else {
                cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            }
            return true;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        firstViewDataFirstTimeStatus = true;
        super.onBackPressed();
    }
}