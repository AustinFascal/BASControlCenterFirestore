package com.ptbas.controlcenter.management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.update.UpdateGoodIssueActivity;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.utility.DragLinearLayout;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.create.AddRecapGoodIssueDataActivity;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class ManageGoodIssueActivity extends AppCompatActivity {

    String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID", "giMatName"};
    String dateStart = "", dateEnd = "", searchTypeData="", monthStrVal, dayStrVal;

    ChipGroup chipGroup;
    Chip chip_filter_all, chip_filter_status_valid, chip_filter_status_invalid,
            chip_filter_status_recapped, chip_filter_status_not_recapped,
            chip_filter_status_invoiced, chip_filter_status_not_yet_invoiced,
            chip_filter_status_cash_out, chip_filter_status_not_yet_cash_out,
            chip_filter_status_transport_type_curah, chip_filter_status_transport_type_borong;
    LinearLayout llNoData, ll_wrap_filter_chip_group, wrapSearchBySpinner, wrapFilter,
            llWrapFilterByDateRange, llBottomSelectionOptions;
    Context context;
    CardView cdvFilter;
    FloatingActionsMenu fabExpandMenu;
    FloatingActionButton fabActionCreateGi, fabActionRecapData;
    TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;
    AutoCompleteTextView spinnerSearchType;
    ImageButton btnGiSearchByDateReset, btnGiSearchByTypeReset, btnExitSelection, btnDeleteSelected, btnSelectAll, btnVerifySelected;
    DatePickerDialog datePicker;
    FastScrollRecyclerView rvGoodIssueList;
    TextView tvTotalSelectedItem, tvTotalSelectedItem2;

    Boolean expandStatus = true;
    List<String> arrayListMaterialName, arrayListCompanyName;
    Helper helper = new Helper();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DialogInterface dialogInterface = new DialogInterface();

    DecimalFormat df = new DecimalFormat("0.00");

    LinearLayout wrapSpinnerRoUID;
    AutoCompleteTextView spinnerRoUID;
    ImageButton btnResetSpinnerRoUID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> receiveOrderNumberList;

    SearchView searchView;

    ProgressDialog pd;

    public String roUID;

    int countFinalAllGI, countFinalAllGIValid, countFinalAllGIInvalid, countFinalAllGICashOut,
            countFinalAllGINotYetCashOut, countFinalAllGIRecapped, countFinalAllGINotRecapped,
            countFinalAllGIInvoiced, countFinalAllGINotYetInvoiced, countFinalAllGITypeCurah, countFinalAllGITypeBorong;

    ValueEventListener velAll, velValid, velInvalid, velCashedOutTrue, velCashedOutFalse, velRecappedTrue, velRecappedFalse,
            velInvoicedTrue, velInvoicedFalse, velCurah, velBorong;


    GIAdapter giAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_good_issue);

        pd = new ProgressDialog(ManageGoodIssueActivity.this);
        pd.setMessage("Memproses");
        pd.setCancelable(false);
        pd.show();

        context = this;

        helper.ACTIVITY_NAME = "GIM";

        giAdapter = new GIAdapter(this, goodIssueModelArrayList, giAdapter);

        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionCreateGi = findViewById(R.id.fab_action_create_ro);
        fabActionRecapData = findViewById(R.id.fab_action_recap_data);

        llBottomSelectionOptions = findViewById(R.id.llBottomSelectionOptions);
        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        tvTotalSelectedItem2 = findViewById(R.id.tvTotalSelectedItem2);
        btnExitSelection = findViewById(R.id.btnExitSelection);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        btnVerifySelected = findViewById(R.id.btnVerifySelected);

        cdvFilter = findViewById(R.id.cdv_filter);
        spinnerSearchType = findViewById(R.id.spinner_search_type);
        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);
        wrapFilter = findViewById(R.id.llWrapFilter);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llNoData = findViewById(R.id.ll_no_data);
        ll_wrap_filter_chip_group = findViewById(R.id.ll_wrap_filter_chip_group);
        rvGoodIssueList = findViewById(R.id.rvItemList);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByTypeReset = findViewById(R.id.btn_gi_search_by_type_reset);

        wrapSpinnerRoUID = findViewById(R.id.wrapSpinnerRoUID);
        spinnerRoUID = findViewById(R.id.spinnerRoUID);
        btnResetSpinnerRoUID = findViewById(R.id.btnResetSpinnerRoUID);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        /*btnGiSearchByDateReset.setColorFilter(color);
        btnGiSearchByTypeReset.setColorFilter(color);*/

        cdvFilter.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        chipGroup = findViewById(R.id.chip_group_filter_query);
        chip_filter_all  = findViewById(R.id.chip_filter_all);
        chip_filter_status_valid = findViewById(R.id.chip_filter_status_valid);
        chip_filter_status_invalid = findViewById(R.id.chip_filter_status_invalid);
        chip_filter_status_recapped = findViewById(R.id.chip_filter_status_recapped);
        chip_filter_status_not_recapped = findViewById(R.id.chip_filter_status_not_yet_recapped);
        chip_filter_status_invoiced = findViewById(R.id.chip_filter_status_invoiced);
        chip_filter_status_not_yet_invoiced = findViewById(R.id.chip_filter_status_not_yet_invoiced);
        chip_filter_status_cash_out = findViewById(R.id.chip_filter_status_cash_out);
        chip_filter_status_not_yet_cash_out = findViewById(R.id.chip_filter_status_not_yet_cash_out);
        chip_filter_status_transport_type_curah = findViewById(R.id.chip_filter_status_transport_type_curah);
        chip_filter_status_transport_type_borong = findViewById(R.id.chip_filter_status_transport_type_borong);
        chip_filter_all.isChecked();

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Good Issue");





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
            Intent intent = new Intent(ManageGoodIssueActivity.this, AddGoodIssueActivity.class);
            startActivity(intent);
        });

        // GO TO RECAP GOOD ISSUE ACTIVITY
        fabActionRecapData.setOnClickListener(view -> {
            Intent intent = new Intent(ManageGoodIssueActivity.this, AddRecapGoodIssueDataActivity.class);
            startActivity(intent);
        });


        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();


        // HANDLE RECYCLERVIEW GI WHEN SCROLLING
       /* rvGoodIssueList.setOnTouchListener((v, event) -> {
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
        });*/

        // HANDLE FILTER COMPONENTS WHEN ON CLICK
        edtGiDateFilterStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(ManageGoodIssueActivity.this,
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

                        checkSelectedChipFilter();

                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        edtGiDateFilterEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(ManageGoodIssueActivity.this,
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

                        edtGiDateFilterEnd.setText(finalDate);
                        dateEnd = finalDate;

                        checkSelectedChipFilter();
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });

        receiveOrderNumberList = new ArrayList<>();

        db.collection("ReceivedOrderData")
                .addSnapshotListener((value, error) -> {
                    receiveOrderNumberList.clear();
                    if (!Objects.requireNonNull(value).isEmpty()) {
                        for (DocumentSnapshot e : value.getDocuments()) {
                            String spinnerPurchaseOrders = Objects.requireNonNull(e.get("roUID")).toString();
                            receiveOrderNumberList.add(spinnerPurchaseOrders);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ManageGoodIssueActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                        arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                        spinnerRoUID.setAdapter(arrayAdapter);
                    }
                });

        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);
            btnResetSpinnerRoUID.setVisibility(View.VISIBLE);

            db.collection("ReceivedOrderData").whereEqualTo("roUID", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                            roUID = receivedOrderModel.getRoDocumentID();

                            Toast.makeText(context, roUID, Toast.LENGTH_SHORT).show();

                            Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    goodIssueModelArrayList.clear();
                                    if (snapshot.exists()){
                                        for (DataSnapshot item : snapshot.getChildren()){
                                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                            if (goodIssueModel.getRoDocumentID().equals(roUID)){
                                                goodIssueModelArrayList.add(goodIssueModel);
                                            }

                                        }
                                        llNoData.setVisibility(View.GONE);
                                        // nestedScrollView.setVisibility(View.VISIBLE);
                                    } else {
                                        llNoData.setVisibility(View.VISIBLE);
                                        //nestedScrollView.setVisibility(View.GONE);
                                    }
                                    Collections.reverse(goodIssueModelArrayList);
                                    giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                                    rvGoodIssueList.setAdapter(giAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    });
        });

        btnResetSpinnerRoUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerRoUID.setText(null);
                btnResetSpinnerRoUID.setVisibility(View.GONE);
            }
        });

        spinnerSearchType.setOnItemClickListener((adapterView, view, i, l) -> {
            btnGiSearchByTypeReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    searchTypeData = searchTypeValue[0];
                    wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    //dialogInterface.cannotFillHere(this, searchView);
                    break;
                case 1:
                    searchTypeData = searchTypeValue[1];
                    wrapSpinnerRoUID.setVisibility(View.VISIBLE);
                    searchView.setEnabled(false);
                    //searchView.setVisibility(View.GONE);
                    break;
                case 2:
                    searchTypeData = searchTypeValue[2];
                    wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    //searchView.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    searchTypeData = searchTypeValue[3];
                    wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    //searchView.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    searchTypeData = searchTypeValue[4];
                    wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    //searchView.setVisibility(View.VISIBLE);
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

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            dismissBottomInfo();

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
                if (group.getCheckedChipId() == chip_filter_status_recapped.getId()){
                    recappedStatusTrue();
                }
                if (group.getCheckedChipId() == chip_filter_status_not_recapped.getId()){
                    recappedStatusFalse();
                }
                if (group.getCheckedChipId() == chip_filter_status_invoiced.getId()){
                    invoicedTrue();
                }
                if (group.getCheckedChipId() == chip_filter_status_not_yet_invoiced.getId()){
                    invoicedFalse();
                }

                if (group.getCheckedChipId() == chip_filter_status_cash_out.getId()){
                    cashOutStatusTrue();
                }
                if (group.getCheckedChipId() == chip_filter_status_not_yet_cash_out.getId()){
                    cashOutStatusFalse();
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













        //new FastScrollerBuilder(rvGoodIssueList).build();




        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                velAll = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chip_filter_all.setText("SEMUA (" + Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount())) + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                velValid = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chip_filter_status_valid.setText("VALID (" + Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount())) + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                velInvalid = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chip_filter_status_invalid.setText("BELUM VALID (" + Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount())) + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                ArrayList<GoodIssueModel> arrayGICashedOutTrue = new ArrayList<>();
                ArrayList<GoodIssueModel> arrayGICashedOutFalse = new ArrayList<>();
                ArrayList<GoodIssueModel> arrayGIRecappedTrue = new ArrayList<>();
                ArrayList<GoodIssueModel> arrayGIRecappedFalse = new ArrayList<>();
                ArrayList<GoodIssueModel> arrayGIInvoicedTrue = new ArrayList<>();
                ArrayList<GoodIssueModel> arrayGIInvoicedFalse = new ArrayList<>();

                velCashedOutTrue = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            if (!Objects.requireNonNull(goodIssueModel).getGiCashedOutTo().equals("")) {
                                arrayGICashedOutTrue.add(goodIssueModel);
                            }
                        }
                        chip_filter_status_cash_out.setText("SUDAH DIAJUKAN (" + arrayGICashedOutTrue.size() + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                velCashedOutFalse = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            if (Objects.requireNonNull(goodIssueModel).getGiCashedOutTo().equals("")) {
                                arrayGICashedOutFalse.add(goodIssueModel);
                            }
                        }
                        chip_filter_status_not_yet_cash_out.setText("BELUM DIAJUKAN (" + arrayGICashedOutFalse.size() + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                velRecappedTrue = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            if (!Objects.requireNonNull(goodIssueModel).getGiRecappedTo().equals("")) {
                                arrayGIRecappedTrue.add(goodIssueModel);
                            }
                        }
                        chip_filter_status_recapped.setText("SUDAH DIREKAP (" + arrayGIRecappedTrue.size() + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                velRecappedFalse = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            if (Objects.requireNonNull(goodIssueModel).getGiRecappedTo().equals("")) {
                                arrayGIRecappedFalse.add(goodIssueModel);
                            }
                        }
                        chip_filter_status_not_recapped.setText("BELUM DIREKAP (" + arrayGIRecappedFalse.size() + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                velInvoicedTrue = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            if (!Objects.requireNonNull(goodIssueModel).getGiInvoicedTo().equals("")) {
                                arrayGIInvoicedTrue.add(goodIssueModel);
                            }
                        }
                        chip_filter_status_invoiced.setText("SUDAH DITAGIHKAN (" + arrayGIInvoicedTrue.size() + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                velInvoicedFalse = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            if (Objects.requireNonNull(goodIssueModel).getGiInvoicedTo().equals("")) {
                                arrayGIInvoicedFalse.add(goodIssueModel);
                            }
                        }
                        chip_filter_status_not_yet_invoiced.setText("BELUM DITAGIHKAN (" + arrayGIInvoicedFalse.size() + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                velCurah = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chip_filter_status_transport_type_curah.setText("CURAH (" + Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount())) + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                velBorong = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chip_filter_status_transport_type_borong.setText("BORONG (" + Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount())) + ")");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                databaseReference.child("GoodIssueData").addValueEventListener(velAll);
                databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(true).addValueEventListener(velValid);
                databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(false).addValueEventListener(velInvalid);
                databaseReference.child("GoodIssueData").addValueEventListener(velCashedOutTrue);
                databaseReference.child("GoodIssueData").addValueEventListener(velCashedOutFalse);
                databaseReference.child("GoodIssueData").addValueEventListener(velRecappedTrue);
                databaseReference.child("GoodIssueData").addValueEventListener(velRecappedFalse);
                databaseReference.child("GoodIssueData").addValueEventListener(velInvoicedTrue);
                databaseReference.child("GoodIssueData").addValueEventListener(velInvoicedFalse);
                databaseReference.child("GoodIssueData").orderByChild("giMatType").equalTo("CURAH").addValueEventListener(velCurah);
                databaseReference.child("GoodIssueData").orderByChild("giMatType").equalTo("BORONG").addValueEventListener(velBorong);

                pd.dismiss();
            }
        }, 1000);
    }

    static class GIAdapter extends RecyclerView.Adapter<GIAdapter.GIHolder>{

        Context c;
        ArrayList<GoodIssueModel> goodIssueModelArrayList;
        ArrayList<GoodIssueModel> checkedGoodIssue = new ArrayList<>();

        GIAdapter giAdapter;

        String custDocumentID;

        public boolean isSelectedAll = false;

        public GIAdapter(Context c, ArrayList<GoodIssueModel> goodIssueModelArrayList, GIAdapter giAdapter){
            this.c = c;
            this.goodIssueModelArrayList = goodIssueModelArrayList;
            this.giAdapter = giAdapter;
        }

        @NonNull
        @Override
        public GIHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_good_issue_desktop, null);
            GIHolder giHolder = new GIHolder(v);
            return giHolder;
        }

        public Activity getActivity(Context context) {
            if (context == null) {
                return null;
            } else if (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                else {
                    return getActivity(((ContextWrapper) context).getBaseContext());
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull GIHolder holder, int pos) {
            final GoodIssueModel goodIssueModel = goodIssueModelArrayList.get(pos);

            Helper helper = new Helper();

            final View rootView = getActivity(c).getWindow().getDecorView().findViewById(android.R.id.content);

            FloatingActionsMenu fabExpandMenu = rootView.findViewById(R.id.fab_expand_menu);
            TextView tvTotalSelectedItem = rootView.findViewById(R.id.tvTotalSelectedItem);
            TextView tvTotalSelectedItem2 = rootView.findViewById(R.id.tvTotalSelectedItem2);
            LinearLayout llBottomSelectionOptions = rootView.findViewById(R.id.llBottomSelectionOptions);

            ImageButton btnDeleteSelected = rootView.findViewById(R.id.btnDeleteSelected);
            ImageButton btnSelectAll = rootView.findViewById(R.id.btnSelectAll);
            ImageButton btnVerifySelected = rootView.findViewById(R.id.btnVerifySelected);
            ImageButton btnExitSelection = rootView.findViewById(R.id.btnExitSelection);

            DecimalFormat df = new DecimalFormat("0.00");
            Double cubication = goodIssueModel.getGiVhlCubication();
            String dateNTime = goodIssueModel.getGiDateCreated()+" | "+goodIssueModel.getGiTimeCreted() + " WIB";

            String[] partGiUID = goodIssueModel.getGiUID().split("-");
            String giUID = partGiUID[0];

            String roDocumentID = goodIssueModel.getRoDocumentID();

            String matDetail = goodIssueModel.getGiMatType()+" - "+goodIssueModel.getGiMatName();
            String vhlDetail = "(P) "+goodIssueModel.getVhlLength().toString()+" (L) "+goodIssueModel.getVhlWidth().toString()+" (T) "+goodIssueModel.getVhlHeight().toString()+" | "+"(K) "+goodIssueModel.getVhlHeightCorrection().toString()+" (TK) "+goodIssueModel.getVhlHeightAfterCorrection().toString();
            String vhlUID = goodIssueModel.getVhlUID();
            boolean giStatus = goodIssueModel.getGiStatus();
            String giRecappedTo = goodIssueModel.getGiRecappedTo();
            String giInvoicedTo = goodIssueModel.getGiInvoicedTo();
            String giCashedOutTo = goodIssueModel.getGiCashedOutTo();

            holder.tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));
            holder.tvGiDateTime.setText(dateNTime);
            holder.tvGiUid.setText(giUID);
            holder.tvGiMatDetail.setText(matDetail);
            holder.tvGiVhlDetail.setText(vhlDetail);
            holder.tvVhlUid.setText(vhlUID);

            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID)
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            if (!value.isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {
                                    ReceivedOrderModel receivedOrderModel = d.toObject(ReceivedOrderModel.class);

                                    assert receivedOrderModel != null;
                                    String roNumber = receivedOrderModel.getRoUID();
                                    String poNumber = receivedOrderModel.getRoPoCustNumber();
                                    custDocumentID = receivedOrderModel.getCustDocumentID();

                                    holder.tvPoCustNumber.setText("PO: " + poNumber);

                                    db1.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID)
                                            .addSnapshotListener((value2, error2) -> {
                                                if (value2 != null) {
                                                    if (!value2.isEmpty()) {
                                                        for (DocumentSnapshot e : value2.getDocuments()) {
                                                            CustomerModel customerModel = e.toObject(CustomerModel.class);
                                                            String customerName = customerModel.getCustName();
                                                            String customerAlias = customerModel.getCustUID();

                                                            holder.tvCustomerName.setText(customerAlias+" - "+customerName);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });

            FirebaseFirestore db0 = FirebaseFirestore.getInstance();

            CollectionReference refCust = db0.collection("CustomerData");

            refCust.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String getDocumentID = documentSnapshot.getId();
                    }
                }
            });

            btnSelectAll.setOnClickListener(v -> {
                if(!isSelectedAll){
                    giAdapter.selectAll();
                    btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_deselect));
                    isSelectedAll = true;
                }else{
                    isSelectedAll = false;
                    giAdapter.clearSelection();
                    btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_select_all));
                }

                int itemSelectedSize = giAdapter.getSelected().size();
                float itemSelectedVolume = giAdapter.getSelectedVolume();

                String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");

                tvTotalSelectedItem.setText(itemSelectedSize + " item terpilih");
                tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);

                if (itemSelectedSize == 0){
                    exitSelection(fabExpandMenu, llBottomSelectionOptions, btnSelectAll);
                }

                notifyDataSetChanged();
            });
            btnDeleteSelected.setOnClickListener(view -> {
                int size = giAdapter.getSelected().size();
                MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                        .setTitle("Hapus Data Terpilih")
                        .setAnimation(R.raw.lottie_delete)
                        .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Good Issue yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                        .setCancelable(true)
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            for (int i = 0; i < giAdapter.getSelected().size(); i++) {
                                databaseReference.child("GoodIssueData").child(giAdapter.getSelected().get(i).getGiUID()).removeValue();
                            }

                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();

                notifyDataSetChanged();
            });
            btnVerifySelected.setOnClickListener(view -> {
                int size = giAdapter.getSelected().size();
                MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                        .setAnimation(R.raw.lottie_approval)
                        .setTitle("Validasi Data Terpilih")
                        .setMessage("Apakah Anda yakin ingin mengesahkan "+size+" data Good Issue yang terpilih? Setelah disahkan, status tidak dapat dikembalikan.")
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            String giUIDVal;
                            for (int i = 0; i < giAdapter.getSelected().size(); i++) {
                                giUIDVal = giAdapter.getSelected().get(i).getGiUID();
                                databaseReference.child("GoodIssueData").child(giUIDVal).child("giStatus").setValue(true);
                                databaseReference.child("GoodIssueData").child(giUIDVal).child("giVerifiedBy").setValue(helper.getUserId());
                            }
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .setCancelable(true)
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();

                notifyDataSetChanged();
            });
            btnExitSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exitSelection(fabExpandMenu, llBottomSelectionOptions, btnSelectAll);
                }
            });

            holder.setItemClickListener(new GIHolder.ItemClickListener(){
                @Override
                public void onItemClick(View v, int pos){
                    CheckBox cbSelectItem = (CheckBox) v;
                    final GoodIssueModel currentGoodIssue = goodIssueModelArrayList.get(pos);

                    if (cbSelectItem.isChecked()){
                        goodIssueModelArrayList.get(pos).setChecked(true);
                        checkedGoodIssue.add(currentGoodIssue);
                    }else if (!cbSelectItem.isChecked()){
                        goodIssueModelArrayList.get(pos).setChecked(false);
                        checkedGoodIssue.remove(currentGoodIssue);
                    }

                    int itemSelectedSize = giAdapter.getSelected().size();
                    float itemSelectedVolume = giAdapter.getSelectedVolume();

                    String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");

                    if (giAdapter.getSelected().size() > 0) {
                        fabExpandMenu.animate().translationY(800).setDuration(100).start();
                        fabExpandMenu.collapse();

                        tvTotalSelectedItem.setText(itemSelectedSize + " item terpilih");
                        tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);

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


                }
            });

            holder.cbSelectItem.setChecked(goodIssueModelArrayList.get(pos).isChecked());

            if (goodIssueModelArrayList.size()<0){
                exitSelection(fabExpandMenu, llBottomSelectionOptions, btnSelectAll);
            }

            if (giStatus){
                holder.llStatusApproved.setVisibility(View.VISIBLE);
                holder.btnApproveGi.setVisibility(View.GONE);
            } else {
                holder.llStatusApproved.setVisibility(View.GONE);
                holder.btnApproveGi.setVisibility(View.VISIBLE);
            }

            if (!giRecappedTo.isEmpty()){
                holder.llStatusRecapped.setVisibility(View.VISIBLE);
            } else {
                holder.llStatusRecapped.setVisibility(View.GONE);
            }

            if (!giInvoicedTo.isEmpty()){
                holder.llStatusInvoiced.setVisibility(View.VISIBLE);
            } else {
                holder.llStatusInvoiced.setVisibility(View.GONE);
            }

            if (!giCashedOutTo.isEmpty()){
                holder.llCashedOutStatus.setVisibility(View.VISIBLE);
            } else {
                holder.llCashedOutStatus.setVisibility(View.GONE);
            }



            holder.btn3.setOnClickListener(view -> {
                String giUID1 =goodIssueModel.getGiUID();
                Intent i = new Intent(c, UpdateGoodIssueActivity.class);
                i.putExtra("key", giUID1);
                c.startActivity(i);
            });

            holder.btn2.setOnClickListener(view -> {
                if (holder.tvPoCustNumber.getText().toString().equals("PO: -")){
                    Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }
                    MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                            .setAnimation(R.raw.lottie_attention)
                            .setTitle("Perhatian!", TextAlignment.START)
                            .setMessage("Data Good Issue ini masih belum memiliki nomor PO. Mohon perbarui data tersebut agar dapat melakukan validasi dan dapat muncul saat direkapitulasi.", TextAlignment.START)
                            .setPositiveButton("OKE", (dialogInterface, which) -> dialogInterface.dismiss())
                            .setCancelable(true)
                            .build();
                    md.show();
                } else {
                    Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }

                    MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                            .setAnimation(R.raw.lottie_approval)
                            .setTitle("Validasi Data")
                            .setMessage("Apakah Anda yakin ingin mengesahkan data Good Issue yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                            .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giStatus").setValue(true);
                                databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giVerifiedBy").setValue(helper.getUserId());
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                            .setCancelable(true)
                            .build();

                    md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    md.show();
                }
            });

            holder.btn1.setOnClickListener(view -> {
                Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }

                MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                        .setTitle("Hapus Data")
                        .setAnimation(R.raw.lottie_delete)
                        .setMessage("Apakah Anda yakin ingin menghapus data Good Issue yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                        .setCancelable(true)
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("GoodIssueData").child(goodIssueModel.getGiUID()).removeValue();
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            });

        }

        public void exitSelection(FloatingActionsMenu fabExpandMenu, LinearLayout llBottomSelectionOptions, ImageButton btnSelectAll) {
            giAdapter.clearSelection();
            isSelectedAll = false;
            fabExpandMenu.animate().translationY(0).setDuration(100).start();
            btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_select_all));

            llBottomSelectionOptions.animate()
                    .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            llBottomSelectionOptions.setVisibility(View.GONE);
                        }
                    });

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return goodIssueModelArrayList.size();
        }

        static class GIHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            LinearLayout llStatusApproved, llStatusRecapped, llStatusInvoiced, llCashedOutStatus, llRoNeedsUpdate, llHiddenView, llWrapGiStatus;
            TextView tvCubication, tvGiDateTime, tvGiUid, tvRoUid, tvGiMatDetail, tvGiVhlDetail,
                    tvVhlUid, tvPoCustNumber, tvCustomerName;
            RelativeLayout btnDeleteGi, btnApproveGi, btnCloneGi;
            Button btn1, btn2, btn3, btn4;
            CardView cardView;
            CheckBox cbSelectItem;

            ItemClickListener itemClickListener;


            public GIHolder(@NonNull View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.cdvItem);
                llHiddenView = itemView.findViewById(R.id.llHiddenView);
                llWrapGiStatus = itemView.findViewById(R.id.llWrapItemStatus);
                llStatusApproved = itemView.findViewById(R.id.llStatusApproved);
                llCashedOutStatus = itemView.findViewById(R.id.llCashedOutStatus);
                llStatusRecapped = itemView.findViewById(R.id.llStatusRecapped);
                llStatusInvoiced = itemView.findViewById(R.id.ll_status_invoiced);
                llRoNeedsUpdate = itemView.findViewById(R.id.ll_ro_needs_update);
                tvCubication = itemView.findViewById(R.id.tv_cubication);
                tvGiDateTime = itemView.findViewById(R.id.tvDateCreated);
                tvGiUid = itemView.findViewById(R.id.tv_gi_uid);
                tvRoUid = itemView.findViewById(R.id.tvCoTotal);
                tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
                tvGiMatDetail = itemView.findViewById(R.id.tv_gi_mat_detail);
                tvGiVhlDetail = itemView.findViewById(R.id.tv_gi_vhl_detail);
                tvVhlUid = itemView.findViewById(R.id.tv_vhl_uid);
                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                btnDeleteGi = itemView.findViewById(R.id.btn_delete_gi);
                btnApproveGi = itemView.findViewById(R.id.btn_approve_gi);
                btnCloneGi = itemView.findViewById(R.id.btn_clone_gi);
                btn1 = itemView.findViewById(R.id.btnDeleteItem);
                btn2 = itemView.findViewById(R.id.btnApproveItem);
                btn3 = itemView.findViewById(R.id.btnOpenItemDetail);
                btn4 = itemView.findViewById(R.id.btnClone);
                cbSelectItem = itemView.findViewById(R.id.cbSelectItem);

                cbSelectItem.setOnClickListener(this);
            }

            public void setItemClickListener(ItemClickListener ic){
                this.itemClickListener = ic;
            }

            @Override
            public void onClick(View v) {
                this.itemClickListener.onItemClick(v,getLayoutPosition());
            }

            interface ItemClickListener{
                void onItemClick(View v, int pos);
            }
        }


        public void selectAll(){
            isSelectedAll=true;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                goodIssueModelArrayList.get(i).setChecked(true);
            }
            notifyDataSetChanged();
        }


        public void clearSelection() {
            isSelectedAll=false;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                goodIssueModelArrayList.get(i).setChecked(false);
            }
            notifyDataSetChanged();
        }


        public ArrayList<GoodIssueModel> getSelected() {
            ArrayList<GoodIssueModel> selected = new ArrayList<>();
            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                if (goodIssueModelArrayList.get(i).isChecked()) {
                    selected.add(goodIssueModelArrayList.get(i));
                }
            }
            return selected;
        }

        public float getSelectedVolume() {
            float selected = 0;
            //ArrayList<GoodIssueModel> selected = new ArrayList<>();
            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                if (goodIssueModelArrayList.get(i).isChecked()) {
                    selected += goodIssueModelArrayList.get(i).getGiVhlCubication();
                    if (!goodIssueModelArrayList.get(i).isChecked()){
                        selected -= goodIssueModelArrayList.get(i).getGiVhlCubication();
                    }
                }
            }
            return selected;
        }
    }





    private int getCountAllGoodIssueTypeBorong() {
        databaseReference.child("GoodIssueData").orderByChild("giMatName").equalTo("BORONG").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFinalAllGITypeBorong = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGITypeBorong;
    }

    private int getCountAllGoodIssueTypeCurah() {
        databaseReference.child("GoodIssueData").orderByChild("giMatName").equalTo("CURAH").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFinalAllGITypeCurah = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return countFinalAllGITypeCurah;
    }

    private int getCountAllGoodIssueNotYetInvoiced() {


        ArrayList<GoodIssueModel> arrayList = new ArrayList<>();
        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    if (Objects.requireNonNull(goodIssueModel).getGiInvoicedTo().equals("")) {
                        arrayList.add(goodIssueModel);
                    }
                }
                countFinalAllGINotYetInvoiced = arrayList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGINotYetInvoiced;
    }

    private int getCountAllGoodIssueInvoiced() {


        ArrayList<GoodIssueModel> arrayList = new ArrayList<>();
        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    if (!Objects.requireNonNull(goodIssueModel).getGiInvoicedTo().equals("")) {
                        arrayList.add(goodIssueModel);
                    }
                }
                countFinalAllGIInvoiced = arrayList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return countFinalAllGIInvoiced;
    }

    private int getCountAllGoodIssueNotRecapped() {

        ArrayList<GoodIssueModel> arrayList = new ArrayList<>();
        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    if (Objects.requireNonNull(goodIssueModel).getGiRecappedTo().equals("")) {
                        arrayList.add(goodIssueModel);
                    }
                }
                countFinalAllGINotRecapped = arrayList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGINotRecapped;
    }

    private int getCountAllGoodIssueRecapped() {

        ArrayList<GoodIssueModel> arrayList = new ArrayList<>();
        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    if (!Objects.requireNonNull(goodIssueModel).getGiRecappedTo().equals("")) {
                        arrayList.add(goodIssueModel);
                    }
                }
                countFinalAllGIRecapped = arrayList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGIRecapped;
    }

    private int getCountAllGoodIssueNotYetCashOut() {
        ArrayList<GoodIssueModel> arrayList = new ArrayList<>();
        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    if (Objects.requireNonNull(goodIssueModel).getGiCashedOutTo().equals("")) {
                        arrayList.add(goodIssueModel);
                    }
                }
                countFinalAllGINotYetCashOut = arrayList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGINotYetCashOut;

    }

    private int getCountAllGoodIssueCashOut() {
        ArrayList<GoodIssueModel> arrayGICashedOut = new ArrayList<>();
        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    if (!Objects.requireNonNull(goodIssueModel).getGiCashedOutTo().equals("")) {
                        arrayGICashedOut.add(goodIssueModel);
                    }
                }
                countFinalAllGICashOut = arrayGICashedOut.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGICashOut;
    }

    private int getCountAllGoodIssueInvalid() {
        databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFinalAllGIInvalid = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return countFinalAllGIInvalid;
    }

    private int getCountAllGoodIssueValid() {



        return countFinalAllGIValid;
    }

    private int getCountAllGoodIssue() {

        databaseReference.child("GoodIssueData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFinalAllGI = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return countFinalAllGI;
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
            invoicedTrue();
        }
        if (chip_filter_status_not_yet_invoiced.isChecked()){
            invoicedFalse();
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

    private void showDataSearchByType(String newText, String searchTypeData) {

        if (searchTypeData.equals(searchTypeValue[1])){
            dialogInterface.cannotFillHere(ManageGoodIssueActivity.this, searchView);
            searchView.setEnabled(false);
        }

        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        //query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        if (!searchTypeData.equals(searchTypeValue[1])){
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    /*searchByTypeOnDataChange(snapshot, newText, searchTypeData);*/
                    goodIssueModelArrayList.clear();
                    if (snapshot.exists()){
                        //nestedScrollView.setVisibility(View.VISIBLE);
                        llNoData.setVisibility(View.GONE);
                        for (DataSnapshot item : snapshot.getChildren()) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);

                            if (searchTypeData.equals(searchTypeValue[0])){
                                if(Objects.requireNonNull(goodIssueModel).getGiUID().contains(newText)) {
                                    goodIssueModelArrayList.add(goodIssueModel);
                                }
                                wrapSpinnerRoUID.setVisibility(View.GONE);
                            }

                            if (searchTypeData.equals(searchTypeValue[3])){
                                if(Objects.requireNonNull(goodIssueModel).getVhlUID().contains(newText)) {
                                    goodIssueModelArrayList.add(goodIssueModel);
                                }
                                wrapSpinnerRoUID.setVisibility(View.GONE);
                            }

                            if (searchTypeData.equals(searchTypeValue[4])){
                                if(Objects.requireNonNull(goodIssueModel).getGiMatName().contains(newText)) {
                                    goodIssueModelArrayList.add(goodIssueModel);
                                }
                                wrapSpinnerRoUID.setVisibility(View.GONE);
                            }
                        }
                        Collections.reverse(goodIssueModelArrayList);
                    } else  {
                        //nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }
                    giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                    rvGoodIssueList.setAdapter(giAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }





    private void showDataDefaultQuery() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        //query = databaseReference.child("GoodIssueData").orderByChild("giStatus");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //pd.show();
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    pd.dismiss();
                    for (DataSnapshot item : snapshot.getChildren()){
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        goodIssueModelArrayList.add(goodIssueModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    //nestedScrollView.setVisibility(View.VISIBLE);
                    //showCountDataGI();
                    Collections.reverse(goodIssueModelArrayList);
                    giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                    rvGoodIssueList.setAdapter(giAdapter);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    //nestedScrollView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (goodIssueModelArrayList.size()<1){
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }

    }


    private void showDataSearchByApprovalStatus(boolean b) {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }
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
    private void approvalStatusOnDataChange(DataSnapshot snapshot, boolean b) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                //if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                if (Objects.equals(item.child("giStatus").getValue(), b)) {
                    goodIssueModelArrayList.add(goodIssueModel);
                    llNoData.setVisibility(View.GONE);
                    //nestedScrollView.setVisibility(View.VISIBLE);
                }
                //}
            }
        } else  {
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(goodIssueModelArrayList);
        giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
        rvGoodIssueList.setAdapter(giAdapter);

        if (goodIssueModelArrayList.size()<1){
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }

    }

    private void recappedStatusTrue() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (!Objects.equals(item.child("giRecappedTo").getValue(), "")) {
                            goodIssueModelArrayList.add(goodIssueModel);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                rvGoodIssueList.setAdapter(giAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void recappedStatusFalse() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (Objects.equals(item.child("giRecappedTo").getValue(), "")) {
                            goodIssueModelArrayList.add(goodIssueModel);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                rvGoodIssueList.setAdapter(giAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cashOutStatusTrue() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (!Objects.equals(item.child("giCashedOutTo").getValue(), "")) {
                            goodIssueModelArrayList.add(goodIssueModel);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                rvGoodIssueList.setAdapter(giAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void cashOutStatusFalse() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (Objects.equals(item.child("giCashedOutTo").getValue(), "")) {
                            goodIssueModelArrayList.add(goodIssueModel);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                rvGoodIssueList.setAdapter(giAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void invoicedTrue() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (!Objects.equals(item.child("giInvoicedTo").getValue(), "")) {
                            goodIssueModelArrayList.add(goodIssueModel);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                rvGoodIssueList.setAdapter(giAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void invoicedFalse() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        if (Objects.equals(item.child("giInvoicedTo").getValue(), "")) {
                            goodIssueModelArrayList.add(goodIssueModel);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
                rvGoodIssueList.setAdapter(giAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSearchByMaterialType(String data) {

        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

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
    private void materialTypeOnDataChange(DataSnapshot snapshot, String data) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                    if (Objects.equals(item.child("giMatType").getValue(), data)) {
                        goodIssueModelArrayList.add(goodIssueModel);
                        llNoData.setVisibility(View.GONE);
                        //nestedScrollView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else  {
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(goodIssueModelArrayList);
        giAdapter = new GIAdapter(context, goodIssueModelArrayList, giAdapter);
        rvGoodIssueList.setAdapter(giAdapter);

        if (goodIssueModelArrayList.size()<1){
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }

    }

    public void dismissBottomInfo(){
        fabExpandMenu.animate().translationY(0).setDuration(100).start();
        btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_outline_select_all));
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
        searchView = (SearchView) menu.findItem(R.id.search_data).getActionView();
        searchView.setQueryHint("Kata Kunci");

        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchView.setMaxWidth(Integer.MAX_VALUE);
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
            btnResetSpinnerRoUID.setVisibility(View.GONE);
            spinnerRoUID.setText(null);
            wrapSpinnerRoUID.setVisibility(View.GONE);










            showDataDefaultQuery();







            //cdvFilter.setVisibility(View.GONE);
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
                //View viewLayout = ManageGoodIssueActivity.this.getCurrentFocus();
                if (spinnerSearchType.getText().toString().isEmpty()){
                    dialogInterface.fillSearchFilter(ManageGoodIssueActivity.this, searchView);
                    /*if (!searchView.getQuery().toString().isEmpty()){

                        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
                    }*/
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

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);


        rvGoodIssueList.setLayoutManager(mLayoutManager);
        //rvGoodIssueList.setLayoutManager(new LinearLayoutManager(this));
        //rvGoodIssueList.setItemAnimator(new DefaultItemAnimator());
        rvGoodIssueList.addItemDecoration(new MemberItemDecoration());
        rvGoodIssueList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        rvGoodIssueList.setAdapter(giAdapter);





    }
    public static class MemberItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            // only for the last one
            if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = 120/* set your margin here */;
            }

        }
    }
    @Override
    public void onBackPressed() {
        rvGoodIssueList.setAdapter(null);
        goodIssueModelArrayList.clear();
        this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*rvGoodIssueList.setAdapter(null);
        goodIssueModelArrayList.clear();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeValue();
        //databaseReference.removeEventListener(velAll);
        //databaseReference.removeEventListener(velValid);
        //databaseReference.removeEventListener(velInvalid);
    }
}