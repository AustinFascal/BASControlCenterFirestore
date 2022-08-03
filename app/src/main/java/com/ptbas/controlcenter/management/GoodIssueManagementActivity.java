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

/*
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ConstraintLayout bottomSheet;
*/

    public String dateStart = "", dateEnd = "", searchTypeData="";

    LinearLayout llExpandFilter;
    ImageButton imgbtnExpandCollapseFilterLayout;
    CardView cdvFilter;
    ExtendedFloatingActionButton fabAddGi;

    private NestedScrollView nestedScrollView;
    private RelativeLayout wrapExpandCollapse;
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
    //SearchView edtKeywordSearchGi;

/*
    FloatingActionsMenu fabExpandMenu;
    com.getbase.floatingactionbutton.FloatingActionButton fabCreateGI, fabApprovedGI, fabInvoicedGI;
*/

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

        String[] approvalStatus = {"Semua", "Sudah Disetujui", "Belum Disetujui"};
        String[] invoicedStatus = {"Semua", "Sudah DITAGIHKAN", "Belum Ditagihkan"};
        String[] searchBy = {"ID Good Issue", "Nomor Received Order", "Nomor PO Customer", "NOPOL Kendaraan"};
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

        /*edtKeywordSearchGi = findViewById(R.id.edt_keyword_search_gi);
        edtKeywordSearchGi.setQueryHint("Kata Kunci");*/


/*
        bottomSheet = findViewById(R.id.bottomSheetGIMenu);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
*/
        fabAddGi = findViewById(R.id.fab_add_gi);
        cdvFilter = findViewById(R.id.cdv_filter);
        llExpandFilter = findViewById(R.id.ll_expand_filter);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtn_expand_collapse_filter_layout);

        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        //wrapExpandCollapse = findViewById(R.id.wrap_expand_collapse);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByStatusReset = findViewById(R.id.btn_gi_search_status_reset);
        btnGiSearchByReset = findViewById(R.id.btn_gi_search_by_reset);

/*
        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabCreateGI = findViewById(R.id.fab_create_gi);
        fabApprovedGI = findViewById(R.id.fab_approved_gi);
        fabInvoicedGI = findViewById(R.id.fab_invoiced_gi);
*/

        fabAddGi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodIssueManagementActivity.this, AddGoodIssueActivity.class);
                startActivity(intent);
            }
        });


        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (llExpandFilter.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
                llExpandFilter.setVisibility(View.GONE);
                imgbtnExpandCollapseFilterLayout.setImageResource(R.drawable.ic_outline_keyboard_arrow_down);
            } else{
                TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
                llExpandFilter.setVisibility(View.VISIBLE);
                imgbtnExpandCollapseFilterLayout.setImageResource(R.drawable.ic_outline_keyboard_arrow_up);

            }
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

        rvGoodIssueList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch( View v, MotionEvent event ) {
                switch ( event.getAction( ) ) {
                    case MotionEvent.ACTION_SCROLL:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        fabAddGi.hide();
                        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
/*
                        fabExpandMenu.collapse();
                        fabExpandMenu.animate()
                                .translationY(200)
                                .setDuration(50)
                                .alpha(0.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        //fabExpandMenu.setVisibility(View.GONE);
                                    }
                                });
*/
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        fabAddGi.show();
/*
                        fabExpandMenu.animate()
                                .translationY(0)
                                .setDuration(50)
                                .alpha(1.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        //fabExpandMenu.setVisibility(View.VISIBLE);
                                    }
                                });
*/
                        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                }
                return false;
            }
        });

        edtGiDateFilterStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
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

            }
        });

        edtGiDateFilterEnd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
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

            }
        });

        btnGiSearchByDateReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtGiDateFilterStart.setText(null);
                edtGiDateFilterEnd.setText(null);
                dateStart = "";
                dateEnd = "";
                showData();
                btnGiSearchByDateReset.setVisibility(View.GONE);
            }
        });

        btnGiSearchByReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSearchType.setText("");
                spinnerSearchType.clearFocus();
                btnGiSearchByReset.setVisibility(View.GONE);
            }
        });

        btnGiSearchByStatusReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerApprovalStatus.setText("");
                spinnerInvoicedStatus.setText("");
                btnGiSearchByStatusReset.setVisibility(View.GONE);
            }
        });

        /*LinearLayout llAddGi = bottomSheet.findViewById(R.id.ll_add_gi);

        llAddGi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodIssueManagementActivity.this, AddGoodIssueActivity.class);
                startActivity(intent);
            }
        });

        wrapExpandCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });*/

        spinnerSearchType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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

        //String roUID = spinnerSearchType.getText().toString();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.getQuery().toString().isEmpty()){
                    showData();
                } else {
                    showDataSearchByType(newText, searchTypeData);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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