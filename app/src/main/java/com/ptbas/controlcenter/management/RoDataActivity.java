package com.ptbas.controlcenter.management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.DragLinearLayoutUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.RoDataAdapter;
import com.ptbas.controlcenter.create.AddRoActivity;
import com.ptbas.controlcenter.model.RoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class RoDataActivity extends AppCompatActivity {

    private static final int LAUNCH_SECOND_ACTIVITY = 0;

    int countData=0;

    LinearLayout llNoData;
    Context context;
    CardView cdvFilter;

    NestedScrollView nestedScrollView;

    Button imgbtnExpandCollapseFilterLayout;

    TextInputEditText edtRoFilterDateStart, edtRoFilterDateEnd;

    DatePickerDialog datePicker;
    RecyclerView rvReceivedOrderList;
    LinearLayout wrapSearchBySpinner, wrapFilter, llWrapFilterByStatus, llWrapFilterByDateRange,
            llWrapFilterByNameType, llWrapFilterByCompany;
    HelperUtils helperUtils = new HelperUtils();
    ArrayList<RoModel> roModelArrayList = new ArrayList<>();

    RoDataAdapter roDataAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    View firstViewData;

    FloatingActionsMenu fabExpandMenu;
    FloatingActionButton fabActionCreateRo, fabActionArchivedData;

    List<String> arrayListMaterialType, arrayListCompanyName;

    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();


    AutoCompleteTextView spinnerApprovalStatus, spinnerSearchType,
            spinnerMaterialType, spinnerCompanyName;

    CoordinatorLayout coordinatorLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ChipGroup chipGroup;
    Chip chipFilterAll, chipFilterStatusValid, chipFilterStatusInvalid, chipFilterType0, chipFilterType1, chipFilterType2,
            chipFilterStatusTransportTypeCurah, chipFilterStatusTransportTypeBorong;

    String[] searchTypeValue = {"roCustName", "roUID", "giPoCustNumber"};
    String dateStart = "", dateEnd = "", searchTypeData="", monthStrVal, dayStrVal;

    LinearLayout llBottomSelectionOptions;
    TextView tvTotalSelectedItem;
    ImageButton btnExitSelection, btnDeleteSelected, btnSelectAll, btnVerifySelected;

    CollectionReference refRO = db.collection("ReceivedOrderData");

    ImageButton btnRoSearchByDateReset, btnRoSearchByTypeReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_ro);

        context = this;

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionCreateRo = findViewById(R.id.fab_action_create_ro);
        fabActionArchivedData = findViewById(R.id.fab_action_archived_data);

        cdvFilter = findViewById(R.id.cdv_filter);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        wrapFilter = findViewById(R.id.llWrapFilter);

        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);

        llNoData = findViewById(R.id.ll_no_data);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        spinnerSearchType = findViewById(R.id.spinner_search_type);
        spinnerMaterialType = findViewById(R.id.spinner_mat_type);

        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);

        rvReceivedOrderList = findViewById(R.id.rv_received_order_list);

        edtRoFilterDateStart = findViewById(R.id.edtRoFilterDateStart);
        edtRoFilterDateEnd = findViewById(R.id.edtRoFilterDateEnd);
        btnRoSearchByDateReset = findViewById(R.id.btnRoSearchDateReset);
        btnRoSearchByTypeReset = findViewById(R.id.btnRoSearchByTypeReset);

        llBottomSelectionOptions = findViewById(R.id.llBottomSelectionOptions);
        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        btnExitSelection = findViewById(R.id.btnExitSelection);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        btnVerifySelected = findViewById(R.id.btnVerifySelected);

        roDataAdapter = new RoDataAdapter(this, roModelArrayList);


        btnDeleteSelected.setOnClickListener(view -> {
            int size = roDataAdapter.getSelected().size();
            MaterialDialog md = new MaterialDialog.Builder(RoDataActivity.this)
                    .setTitle("Hapus Data Terpilih")
                    .setAnimation(R.raw.lottie_delete)
                    .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Received Order yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                    .setCancelable(true)
                    .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                        refRO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                                        String getDocumentID = documentSnapshot.getId();
                                        for (int i = 0; i < size; i++){
                                            db.collection("ReceivedOrderData").document(roDataAdapter.getSelected().get(i).getRoDocumentID()).delete();
                                            dialogInterface.dismiss();

                                            /*if (getDocumentID.equals(roManagementAdapter.getSelected().get(i).getRoDocumentID())){
                                            }*/
                                        }

                                    }
                                }
                            }
                        });
                        //dialogInterface.dismiss();
                        /*dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        for (int i = 0; i < roManagementAdapter.getSelected().size(); i++) {
                            //databaseReference.child("ReceivedOrderData").child(roManagementAdapter.getSelected().get(i).getGiUID()).removeValue();
                        }*/

                    })
                    .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();

            md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            md.show();
        });

        btnVerifySelected.setOnClickListener(view -> {
            int size = roDataAdapter.getSelected().size();
            MaterialDialog md = new MaterialDialog.Builder((Activity) context)
                    .setAnimation(R.raw.lottie_approval)
                    .setTitle("Validasi Data Terpilih")
                    .setMessage("Apakah Anda yakin ingin mengesahkan "+size+" data Received Order yang terpilih? Setelah disahkan, status tidak dapat dikembalikan.")
                    .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                        refRO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                                        String getDocumentID = documentSnapshot.getId();
                                        for (int i = 0; i < size; i++){
                                            db.collection("ReceivedOrderData").document(roDataAdapter.getSelected().get(i).getRoDocumentID()).update("roStatus", true);
                                            db.collection("ReceivedOrderData").document(roDataAdapter.getSelected().get(i).getRoDocumentID()).update("roVerifiedBy", helperUtils.getUserId());
                                            dialogInterface.dismiss();
                                           /* if (getDocumentID.equals(roManagementAdapter.getSelected().get(i).getRoDocumentID())){

                                                //roManagementAdapter.clearSelection();
                                            }*/
                                        }

                                    }
                                }
                            }
                        });
                    })
                    .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .setCancelable(true)
                    .build();

            md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            md.show();
        });

        btnSelectAll.setVisibility(View.GONE);

        btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                roDataAdapter.clearSelection();

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
                int itemSelectedSize = roDataAdapter.getSelected().size();
                if (roDataAdapter.getSelected().size()>0){

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

        chipGroup = findViewById(R.id.chip_group_filter_query);
        chipFilterAll  = findViewById(R.id.chipFilterAll);
        chipFilterStatusValid = findViewById(R.id.chipFilterStatusValid);
        chipFilterStatusInvalid = findViewById(R.id.chipFilterStatusInvalid);
        chipFilterType0 = findViewById(R.id.chipFilterType0);
        chipFilterType1 = findViewById(R.id.chipFilterType1);
        chipFilterType2 = findViewById(R.id.chipFilterType2);
        chipFilterStatusTransportTypeCurah = findViewById(R.id.chipFilterStatusTransportTypeCurah);
        chipFilterStatusTransportTypeBorong = findViewById(R.id.chipFilterStatusTransportTypeBorong);

        chipFilterAll.isChecked();

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(group.getCheckedChipId())){
                if (group.getCheckedChipId() == chipFilterAll.getId()){
                    showDataDefaultQuery();
                }
                if (group.getCheckedChipId() == chipFilterStatusValid.getId()){
                    showDataSearchByApprovalStatus(true);
                }
                if (group.getCheckedChipId() == chipFilterStatusInvalid.getId()){
                    showDataSearchByApprovalStatus(false);
                }
                if (group.getCheckedChipId() == chipFilterType0.getId()){
                    showDataSearchByRoType(0);
                }
                if (group.getCheckedChipId() == chipFilterType1.getId()){
                    showDataSearchByRoType(1);
                }
                if (group.getCheckedChipId() == chipFilterType2.getId()){
                    showDataSearchByRoType(2);
                }
                if (group.getCheckedChipId() == chipFilterStatusTransportTypeCurah.getId()){
                    showDataSearchByMaterialType("CURAH");
                }
                if (group.getCheckedChipId() == chipFilterStatusTransportTypeBorong.getId()){
                    showDataSearchByMaterialType("BORONG");
                }
            } else {
                showDataDefaultQuery();
            }
        });



        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ActionBar actionBar = getSupportActionBar();
        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Received Order");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // DRAGLINEARLAYOUT FOR FILTERING
        DragLinearLayoutUtils dragLinearLayoutUtils = findViewById(R.id.drag_linear_layout);
        for(int i = 0; i < dragLinearLayoutUtils.getChildCount(); i++){
            View child = dragLinearLayoutUtils.getChildAt(i);
            // the child will act as its own drag handle
            dragLinearLayoutUtils.setViewDraggable(child, child);
        }

        dragLinearLayoutUtils.setOnViewSwapListener((firstView, firstPosition,
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


        spinnerSearchType.setOnItemClickListener((adapterView, view, i, l) -> {
            btnRoSearchByTypeReset.setVisibility(View.VISIBLE);
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
                default:
                    break;
            }
        });

        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();

        fabActionCreateRo.setOnClickListener(view -> {
            //int LAUNCH_SECOND_ACTIVITY = 1;
            Intent i = new Intent(this, AddRoActivity.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            fabExpandMenu.collapse();
            /*Intent intent = new Intent(ReceivedOrderManagementActivity.this, AddReceivedOrder.class);
            startActivity(intent);*/
        });

        fabActionArchivedData.setOnClickListener(view ->
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show());


        // HANDLE FILTER COMPONENTS WHEN ON CLICK
        edtRoFilterDateStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(RoDataActivity.this,
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

                        edtRoFilterDateStart.setText(finalDate);
                        dateStart = finalDate;

                        checkSelectedChipFilter();

                        btnRoSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        // INIT ARRAYS AND ADAPTER FOR FILTERING
        String[] searchType = {"Nama Customer", "ID Received Order", "Nomor PO Customer"};
        ArrayList<String> arrayListSearchType = new ArrayList<>(Arrays.asList(searchType));
        ArrayAdapter<String> arrayAdapterSearchType = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListSearchType);
        spinnerSearchType.setAdapter(arrayAdapterSearchType);


        edtRoFilterDateEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(RoDataActivity.this,
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

                        edtRoFilterDateEnd.setText(finalDate);
                        dateEnd = finalDate;

                        checkSelectedChipFilter();
                        btnRoSearchByDateReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });

        btnRoSearchByDateReset.setOnClickListener(view -> {
            resetSearchByDate();
            checkSelectedChipFilter();
        });

        btnRoSearchByTypeReset.setOnClickListener(view -> {
            resetSearchByType();
            checkSelectedChipFilter();
        });

    }

    private void resetSearchByType() {
        spinnerSearchType.setText("");
        spinnerSearchType.clearFocus();
        btnRoSearchByTypeReset.setVisibility(View.GONE);
    }

    private void resetSearchByDate() {
        btnRoSearchByDateReset.setVisibility(View.GONE);
        edtRoFilterDateStart.setText(null);
        edtRoFilterDateEnd.setText(null);
        dateStart = "";
        dateEnd = "";
    }

    private void checkSelectedChipFilter() {
        if (chipFilterAll.isChecked()){
            showDataDefaultQuery();
        }
        if (chipFilterStatusValid.isChecked()){
            showDataSearchByApprovalStatus(true);
        }
        if (chipFilterStatusInvalid.isChecked()){
            showDataSearchByApprovalStatus(false);
        }
        if (chipFilterType0.isChecked()){
            showDataSearchByRoType(0);
        }
        if (chipFilterType1.isChecked()){
            showDataSearchByRoType(1);
        }
        if (chipFilterType2.isChecked()){
            showDataSearchByRoType(2);
        }
        if (chipFilterStatusTransportTypeCurah.isChecked()){
            showDataSearchByMaterialType("CURAH");
        }
        if (chipFilterStatusTransportTypeBorong.isChecked()){
            showDataSearchByMaterialType("BORONG");
        }
    }


    private void showDataSearchByApprovalStatus(boolean b) {
        db.collection("ReceivedOrderData").whereEqualTo("roStatus", b)
                .addSnapshotListener((value, error) -> {
                    roModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            RoModel roModel = d.toObject(RoModel.class);
                            roModelArrayList.add(roModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    //Collections.reverse(receivedOrderModelArrayList);
                    roDataAdapter = new RoDataAdapter(context, roModelArrayList);
                    rvReceivedOrderList.setAdapter(roDataAdapter);
                });
    }

    private void showDataSearchByMaterialType(String data) {
        db.collection("ReceivedOrderData").whereEqualTo("roMatType", data)
                .addSnapshotListener((value, error) -> {
                    roModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            RoModel roModel = d.toObject(RoModel.class);
                            roModelArrayList.add(roModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    //Collections.reverse(receivedOrderModelArrayList);
                    roDataAdapter = new RoDataAdapter(context, roModelArrayList);
                    rvReceivedOrderList.setAdapter(roDataAdapter);
                });
    }

    private void showDataSearchByType(String newText, String searchTypeData) {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        query.addSnapshotListener((value, error) -> {
            roModelArrayList.clear();
            if (!value.isEmpty()){
                nestedScrollView.setVisibility(View.VISIBLE);
                llNoData.setVisibility(View.GONE);
                for (DocumentSnapshot d : value.getDocuments()) {
                    RoModel roModel = d.toObject(RoModel.class);
                    if (searchTypeData.equals(searchTypeValue[0])){
                        if(Objects.requireNonNull(roModel).getCustDocumentID().contains(newText)) {
                            roModelArrayList.add(roModel);
                        }
                    }
                    if (searchTypeData.equals(searchTypeValue[1])){
                        if(Objects.requireNonNull(roModel).getRoUID().contains(newText)) {
                            roModelArrayList.add(roModel);
                        }
                    }

                    if (searchTypeData.equals(searchTypeValue[2])){
                        if(Objects.requireNonNull(roModel).getRoPoCustNumber().contains(newText)) {
                            roModelArrayList.add(roModel);
                        }
                    }
                }
                llNoData.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            } else{
                llNoData.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }
            //Collections.reverse(receivedOrderModelArrayList);
            roDataAdapter = new RoDataAdapter(context, roModelArrayList);
            rvReceivedOrderList.setAdapter(roDataAdapter);
        });
    }

    private void showDataSearchByRoType(int data) {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                roModelArrayList.clear();
                if (!value.isEmpty()){
                    for (DocumentSnapshot d : value.getDocuments()) {
                        RoModel roModel = d.toObject(RoModel.class);
                        if(Objects.requireNonNull(roModel).getRoType()==data) {
                            roModelArrayList.add(roModel);
                            llNoData.setVisibility(View.GONE);
                            nestedScrollView.setVisibility(View.VISIBLE);
                        }
                    }
                } else{
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                Collections.reverse(roModelArrayList);
                roDataAdapter = new RoDataAdapter(context, roModelArrayList);
                rvReceivedOrderList.setAdapter(roDataAdapter);

                if (roModelArrayList.size()<1){
                    nestedScrollView.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void showDataDefaultQuery() {
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = db.collection("ReceivedOrderData").orderBy("roDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        query.addSnapshotListener((value, error) -> {
            roModelArrayList.clear();
            if (!value.isEmpty()){
                for (DocumentSnapshot d : value.getDocuments()) {
                    RoModel roModel = d.toObject(RoModel.class);
                    roModelArrayList.add(roModel);
                }
                llNoData.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            } else{
                llNoData.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }
            Collections.reverse(roModelArrayList);
            roDataAdapter = new RoDataAdapter(context, roModelArrayList);
            rvReceivedOrderList.setAdapter(roDataAdapter);
        });


        /*Query query = databaseReference.child("ReceivedOrders").orderByChild("roDateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                receivedOrderModelArrayList.clear();

                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()){
                        ReceivedOrderModel receivedOrderModel = item.getValue(ReceivedOrderModel.class);
                        receivedOrderModelArrayList.add(receivedOrderModel);
                    }
                    llNoData.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);

                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                }
                Collections.reverse(receivedOrderModelArrayList);
                roManagementAdapter = new ROManagementAdapter(context, receivedOrderModelArrayList);
                rvReceivedOrderList.setAdapter(roManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


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
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(view -> {
            cdvFilter.setVisibility(View.VISIBLE);
            wrapSearchBySpinner.setVisibility(View.VISIBLE);
            wrapFilter.setVisibility(View.GONE);
            imgbtnExpandCollapseFilterLayout.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            if (expandStatus){
                expandStatus=false;
                menu.findItem(R.id.filter_data).setIcon(R.drawable.ic_outline_filter_alt);
            }
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
                        //showDataSearchByType(newText, searchTypeData);
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

        roDataAdapter.clearSelection();
        // HANDLE RESPONSIVE CONTENT
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            rvReceivedOrderList.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvReceivedOrderList.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rvReceivedOrderList.setLayoutManager(mLayoutManager);
        }

        //showDataDefaultQuery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String returnString = data.getStringExtra("addedStatus");
                String activityType = data.getStringExtra("activityType");
                if (returnString.equals("true")&&activityType.equals("RO")){
                    Snackbar.make(coordinatorLayout, "Berhasil! Mau menambah data lagi?", Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.dark_green))
                            .setBackgroundTint(getResources().getColor(R.color.pure_green))
                            .setAction("TAMBAH LAGI", view1 -> {
                               /* Intent intent = new Intent(ReceivedOrderManagementActivity.this, AddReceivedOrder.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);*/
                                Intent i = new Intent(this, AddRoActivity.class);
                                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
                            })
                            .setActionTextColor(getResources().getColor(R.color.black))
                            .show();
                }
                //String result=data.getStringExtra("result");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult

    @Override
    public void onBackPressed() {
        //firstViewDataFirstTimeStatus = true;
        //helper.refreshDashboard(this.getApplicationContext());
        super.onBackPressed();
    }
}