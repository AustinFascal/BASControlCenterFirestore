package com.ptbas.controlcenter.management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.utility.DragLinearLayout;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.create.AddRecapGoodIssueDataActivity;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
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

public class ManageGoodIssueActivity extends AppCompatActivity {

    GIManagementAdapter.ItemViewHolder itemViewHolder;

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
    //NestedScrollView nestedScrollView;
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

    GIManagementAdapter giManagementAdapter;

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
        //nestedScrollView = findViewById(R.id.nestedScrollView);
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
                                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                                    rvGoodIssueList.setAdapter(giManagementAdapter);
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

        pd.show();
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
                if (group.getCheckedChipId() == chip_filter_status_recapped.getId()){
                    showDataSearchByRecappedStatus(true);
                }
                if (group.getCheckedChipId() == chip_filter_status_not_recapped.getId()){
                    showDataSearchByRecappedStatus(false);
                }
                if (group.getCheckedChipId() == chip_filter_status_invoiced.getId()){
                    showDataSearchByInvoiced();
                }
                if (group.getCheckedChipId() == chip_filter_status_not_yet_invoiced.getId()){
                    showDataSearchByNotYetInvoiced();
                }

                if (group.getCheckedChipId() == chip_filter_status_cash_out.getId()){
                    showDataSearchByCashOutStatus(true);
                }
                if (group.getCheckedChipId() == chip_filter_status_not_yet_cash_out.getId()){
                    showDataSearchByCashOutStatus(false);
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

        giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);

        btnDeleteSelected.setOnClickListener(view -> {
            int size = giManagementAdapter.getSelected().size();
            MaterialDialog md = new MaterialDialog.Builder(ManageGoodIssueActivity.this)
                    .setTitle("Hapus Data Terpilih")
                    .setAnimation(R.raw.lottie_delete)
                    .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Good Issue yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                    .setCancelable(true)
                    .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                            databaseReference.child("GoodIssueData").child(giManagementAdapter.getSelected().get(i).getGiUID()).removeValue();
                        }

                    })
                    .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            md.show();
        });

        btnVerifySelected.setOnClickListener(view -> {
            int size = giManagementAdapter.getSelected().size();
            MaterialDialog md = new MaterialDialog.Builder((Activity) context)
                    .setAnimation(R.raw.lottie_approval)
                    .setTitle("Validasi Data Terpilih")
                    .setMessage("Apakah Anda yakin ingin mengesahkan "+size+" data Good Issue yang terpilih? Setelah disahkan, status tidak dapat dikembalikan.")
                    .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        String giUIDVal;
                        for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                            giUIDVal = giManagementAdapter.getSelected().get(i).getGiUID();
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
        });

        btnSelectAll.setVisibility(View.VISIBLE);

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giManagementAdapter.selectAll();
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
            }
        });



        btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                giManagementAdapter.clearSelection();

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
        });


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                chip_filter_all.setText("SEMUA ("+getCountAllGoodIssue()+")");
                chip_filter_status_valid.setText("VALID ("+getCountAllGoodIssueValid()+")");
                chip_filter_status_invalid.setText("BELUM VALID ("+getCountAllGoodIssueInvalid()+")");
                chip_filter_status_cash_out.setText("SUDAH DIAJUKAN ("+getCountAllGoodIssueCashOut()+")");
                chip_filter_status_not_yet_cash_out.setText("BELUM DIAJUKAN ("+getCountAllGoodIssueNotYetCashOut()+")");
                chip_filter_status_recapped.setText("SUDAH DIREKAP ("+getCountAllGoodIssueRecapped()+")");
                chip_filter_status_not_recapped.setText("BELUM DIREKAP ("+getCountAllGoodIssueNotRecapped()+")");
                chip_filter_status_invoiced.setText("SUDAH DITAGIHKAN ("+getCountAllGoodIssueInvoiced()+")");
                chip_filter_status_not_yet_invoiced.setText("BELUM DITAGIHKAN ("+getCountAllGoodIssueNotYetInvoiced()+")");
                chip_filter_status_transport_type_curah.setText("CURAH ("+getCountAllGoodIssueTypeCurah()+")");
                chip_filter_status_transport_type_borong.setText("BORONG ("+getCountAllGoodIssueTypeBorong()+")");


                int itemSelectedSize = giManagementAdapter.getSelected().size();
                float itemSelectedVolume = giManagementAdapter.getSelectedVolume();

                String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
                if (giManagementAdapter.getSelected().size()>0){

                    fabExpandMenu.animate().translationY(800).setDuration(100).start();
                    fabExpandMenu.collapse();

                    tvTotalSelectedItem.setText(itemSelectedSize+" item terpilih");
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
                handler.postDelayed(this, 100);
            }
        };
        runnable.run();

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

    private int getCountAllGoodIssueValid() {

        databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countFinalAllGIValid = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return countFinalAllGIValid;
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
            showDataSearchByInvoiced();
        }
        if (chip_filter_status_not_yet_invoiced.isChecked()){
            showDataSearchByNotYetInvoiced();
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
                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                    rvGoodIssueList.setAdapter(giManagementAdapter);
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
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //pd.show();
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){

                    for (DataSnapshot item : snapshot.getChildren()){
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        goodIssueModelArrayList.add(goodIssueModel);
                    }
                    pd.dismiss();
                    llNoData.setVisibility(View.GONE);
                    //nestedScrollView.setVisibility(View.VISIBLE);

                    Collections.reverse(goodIssueModelArrayList);
                    giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                    rvGoodIssueList.setAdapter(giManagementAdapter);
                } else {
                    pd.dismiss();
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
            pd.dismiss();
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

    private void showDataSearchByRecappedStatus(boolean b) {
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
                recappedStatusOnDataChange(snapshot, b);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDataSearchByCashOutStatus(boolean b) {
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
                cashOutStatusOnDataChange(snapshot, b);
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
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        pd.dismiss();

    }

    private void recappedStatusOnDataChange(DataSnapshot snapshot, boolean b) {

        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                //if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                if (Objects.equals(item.child("giRecapped").getValue(), b)) {
                    goodIssueModelArrayList.add(goodIssueModel);
                    llNoData.setVisibility(View.GONE);
                    // nestedScrollView.setVisibility(View.VISIBLE);
                }
                //}
            }
        } else  {
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(goodIssueModelArrayList);
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }

        pd.dismiss();
    }

    private void cashOutStatusOnDataChange(DataSnapshot snapshot, boolean b) {
        goodIssueModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                //if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                if (Objects.equals(item.child("giCashedOut").getValue(), b)) {
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
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            // nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
        pd.dismiss();

    }

    private void showDataSearchByInvoiced() {
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
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
                pd.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSearchByNotYetInvoiced() {
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
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);

                if (goodIssueModelArrayList.size()<1){
                    llNoData.setVisibility(View.VISIBLE);
                }
                pd.dismiss();
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
        giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
        rvGoodIssueList.setAdapter(giManagementAdapter);

        if (goodIssueModelArrayList.size()<1){
            //nestedScrollView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }

        pd.dismiss();

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

    }

    @Override
    public void onBackPressed() {
        //helper.refreshDashboard(this.getApplicationContext());
        android.os.Process.killProcess(android.os.Process.myPid());
        this.finish();
        super.onBackPressed();
    }
}