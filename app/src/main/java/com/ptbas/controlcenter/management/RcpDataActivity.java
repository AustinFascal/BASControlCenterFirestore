package com.ptbas.controlcenter.management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.RcpDataAdapter;
import com.ptbas.controlcenter.create.AddRcpActivity;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.DragLinearLayoutUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.model.RcpModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RcpDataActivity extends AppCompatActivity {

    String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID", "giMatName"};
    String dateStart = "", dateEnd = "", monthStrVal, dayStrVal;

    ChipGroup chipGroup;
   /* Chip chip_filter_all, chip_filter_status_valid, chip_filter_status_invalid,
            chip_filter_status_invoiced, chip_filter_status_not_yet_invoiced,
            chip_filter_status_transport_type_curah, chip_filter_status_transport_type_borong;*/
    LinearLayout llNoData, ll_wrap_filter_chip_group, wrapSearchBySpinner, wrapFilter,
            llWrapFilterByDateRange;
    Context context;
    CardView cdvFilter;
    FloatingActionsMenu fabExpandMenu;
    FloatingActionButton fabActionCreateCo, fabActionRecapData;
    NestedScrollView nestedScrollView;
    TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;
    AutoCompleteTextView spinnerSearchType;
    ImageButton btnGiSearchByDateReset, btnGiSearchByTypeReset;
    DatePickerDialog datePicker;
    RecyclerView rvItemList;

    Boolean expandStatus = true;
    List<String> arrayListMaterialName, arrayListCompanyName;
    HelperUtils helperUtils = new HelperUtils();
    ArrayList<RcpModel> rcpModelArrayList = new ArrayList<>();

    RcpDataAdapter recapGiManagementAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    LinearLayout llBottomSelectionOptions;
    TextView tvTotalSelectedItem;
    ImageButton btnExitSelection, btnDeleteSelected, btnSelectAll, btnVerifySelected;

    CollectionReference refRcp = db.collection("RecapData");

    ProgressDialog pd;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_rcp);

        context = this;

        pd = new ProgressDialog(RcpDataActivity.this);
        pd.setMessage("Memproses ...");
        pd.setCancelable(false);
        pd.show();

        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionCreateCo = findViewById(R.id.fabActionCreateCo);

        cdvFilter = findViewById(R.id.cdv_filter);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        spinnerSearchType = findViewById(R.id.spinner_search_type);
        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);
        wrapFilter = findViewById(R.id.llWrapFilter);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llNoData = findViewById(R.id.ll_no_data);
        ll_wrap_filter_chip_group = findViewById(R.id.llWrapFilterChipGroup);
        rvItemList = findViewById(R.id.rvItemList);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByTypeReset = findViewById(R.id.btn_gi_search_by_type_reset);

        chipGroup = findViewById(R.id.chipGroup);
        llBottomSelectionOptions = findViewById(R.id.llBottomSelectionOptions);
        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        btnExitSelection = findViewById(R.id.btnExitSelection);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        btnVerifySelected = findViewById(R.id.btnVerifySelected);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        //btnGiSearchByDateReset.setColorFilter(color);
        //btnGiSearchByTypeReset.setColorFilter(color);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Rekap Good Issue");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // DRAGLINEARLAYOUT FOR FILTERING
        DragLinearLayoutUtils dragLinearLayoutUtils = findViewById(R.id.drag_linear_layout);
        for(int i = 0; i < dragLinearLayoutUtils.getChildCount(); i++){
            View child = dragLinearLayoutUtils.getChildAt(i);
            // the child will act as its own drag handle
            dragLinearLayoutUtils.setViewDraggable(child, child);
        }

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        // GO TO ADD GOOD ISSUE ACTIVITY
        fabActionCreateCo.setOnClickListener(view -> {
            Intent intent = new Intent(RcpDataActivity.this, AddRcpActivity.class);
            startActivity(intent);
        });

        /*// GO TO RECAP GOOD ISSUE ACTIVITY
        fabActionRecapData.setOnClickListener(view -> {
            Intent intent = new Intent(CashOutManagementActivity.this, RecapGoodIssueDataActivity.class);
            startActivity(intent);
        });*/

        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();

        // HANDLE RECYCLERVIEW GI WHEN SCROLLING
        rvItemList.setOnTouchListener((v, event) -> {
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

            datePicker = new DatePickerDialog(RcpDataActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {
                        int monthInt = month + 1;

                        if(monthInt < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }

                        if(dayOfMonth <= 9){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtGiDateFilterStart.setText(finalDate);
                        dateStart = finalDate;

                        //checkSelectedChipFilter();

                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePicker.show();
        });

        edtGiDateFilterEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(RcpDataActivity.this,
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

                        //checkSelectedChipFilter();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePicker.show();
        });

        recapGiManagementAdapter = new RcpDataAdapter(this, rcpModelArrayList);

        btnSelectAll.setVisibility(View.GONE);


        /*btnDeleteSelected.setOnClickListener(view -> {
            int size = recapGiManagementAdapter.getSelected().size();
            MaterialDialog md = new MaterialDialog.Builder(ManageRecapGoodIssueActivity.this)
                    .setTitle("Hapus Data Terpilih")
                    .setAnimation(R.raw.lottie_delete)
                    .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Cash Out yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                    .setCancelable(true)
                    .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                        refRcp.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                                        String getDocumentID = documentSnapshot.getId();
                                        for (int i = 0; i < size; i++){
                                            db.collection("CashOutData").document(recapGiManagementAdapter.getSelected().get(i).getCoDocumentID()).delete();
                                            dialogInterface.dismiss();
                                           *//* if (getDocumentID.equals(roManagementAdapter.getSelected().get(i).getRoDocumentID())){

                                                //roManagementAdapter.clearSelection();
                                            }*//*
                                        }

                                    }
                                }
                            }
                        });
                    })
                    .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            md.show();
        });


        btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recapGiManagementAdapter.clearSelection();

                llBottomSelectionOptions.animate()
                        .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                llBottomSelectionOptions.setVisibility(View.GONE);
                            }
                        });
            }
        });*/

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                int itemSelectedSize = recapGiManagementAdapter.getSelected().size();
                if (recapGiManagementAdapter.getSelected().size()>0){

                    fabExpandMenu.animate().translationY(800).setDuration(100).start();
                    fabExpandMenu.collapse();

                    tvTotalSelectedItem.setText(itemSelectedSize+" item terpilih");

                    llBottomSelectionOptions.animate()
                            .translationY(0).alpha(1.0f)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    llBottomSelectionOptions.setVisibility(View.VISIBLE);
                                }
                            });

                } else {
                    fabExpandMenu.animate().translationY(0).setDuration(100).start();

                    llBottomSelectionOptions.animate()
                            .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    llBottomSelectionOptions.setVisibility(View.GONE);
                                }
                            });
                }
                handler.postDelayed(this, 100);
            }
        };
        runnable.run();
    }

    private void showDataDefaultQuery() {

        db.collection("RecapData").orderBy("rcpGiDateAndTimeCreated")
                .addSnapshotListener((value, error) -> {

                    pd.dismiss();
                    rcpModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            RcpModel rcpModel = d.toObject(RcpModel.class);
                            rcpModelArrayList.add(rcpModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(rcpModelArrayList);
                    recapGiManagementAdapter = new RcpDataAdapter(context, rcpModelArrayList);
                    rvItemList.setAdapter(recapGiManagementAdapter);
                });
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recap_gi, menu);

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
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchView.setOnSearchClickListener(view -> {
            ll_wrap_filter_chip_group.setVisibility(View.GONE);
            wrapSearchBySpinner.setVisibility(View.VISIBLE);
            cdvFilter.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            if (expandStatus){
                expandStatus=false;
                menu.findItem(R.id.filter_data).setIcon(R.drawable.ic_outline_filter_alt);
            }
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
                if (spinnerSearchType.getText().toString().isEmpty() ||
                        Objects.requireNonNull(edtGiDateFilterStart.getText()).toString().isEmpty() ||
                        Objects.requireNonNull(edtGiDateFilterEnd.getText()).toString().isEmpty()){
                    if (!searchView.getQuery().toString().isEmpty()){
                        dialogInterfaceUtils.fillSearchFilter(RcpDataActivity.this, searchView);
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
            if (expandStatus){
                expandStatus=false;
                cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            } else {
                expandStatus=true;
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
        /*if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            rvItemList.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvItemList.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rvItemList.setLayoutManager(mLayoutManager);
        }*/

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rvItemList.setLayoutManager(mLayoutManager);
        //chip_filter_all.isChecked();
        showDataDefaultQuery();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}