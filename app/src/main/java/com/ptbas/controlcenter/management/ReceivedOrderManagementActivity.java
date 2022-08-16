package com.ptbas.controlcenter.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;
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
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.adapter.ROManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.create.AddReceivedOrder;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceivedOrderManagementActivity extends AppCompatActivity {

    String dateStart = "", dateEnd = "", searchTypeData="";

    private static final int LAUNCH_SECOND_ACTIVITY = 0;

    int countData=0;

    LinearLayout llNoData;
    Context context;
    CardView cdvFilter;

    NestedScrollView nestedScrollView;

    Button imgbtnExpandCollapseFilterLayout;

    TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;

    DatePickerDialog datePicker;
    RecyclerView rvReceivedOrderList;
    LinearLayout wrapSearchBySpinner, wrapFilter, llWrapFilterByStatus, llWrapFilterByDateRange,
            llWrapFilterByNameType, llWrapFilterByCompany;
    Helper helper = new Helper();
    ArrayList<ReceivedOrderModel> receivedOrderModelArrayList = new ArrayList<>();

    ROManagementAdapter roManagementAdapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    View firstViewData;

    FloatingActionsMenu fabExpandMenu;
    FloatingActionButton fabActionCreateRo, fabActionArchivedData;

    List<String> arrayListMaterialType, arrayListCompanyName;

    DialogInterface dialogInterface = new DialogInterface();


    AutoCompleteTextView spinnerApprovalStatus, spinnerSearchType,
            spinnerMaterialType, spinnerCompanyName;

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_order_management);

        context = this;

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionCreateRo = findViewById(R.id.fab_action_create_ro);
        fabActionArchivedData = findViewById(R.id.fab_action_archived_data);

        cdvFilter = findViewById(R.id.cdv_filter);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        wrapFilter = findViewById(R.id.wrap_filter);

        wrapSearchBySpinner = findViewById(R.id.wrap_search_by_spinner);

        llNoData = findViewById(R.id.ll_no_data);
        llWrapFilterByStatus = findViewById(R.id.ll_wrap_filter_by_status);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llWrapFilterByCompany = findViewById(R.id.ll_wrap_filter_by_company);


        spinnerApprovalStatus = findViewById(R.id.spinner_approval_status);
        spinnerSearchType = findViewById(R.id.spinner_search_type);
        spinnerMaterialType = findViewById(R.id.spinner_mat_type);
        spinnerCompanyName = findViewById(R.id.spinner_company_name);

        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtn_expand_collapse_filter_layout);


        rvReceivedOrderList = findViewById(R.id.rv_received_order_list);

        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ActionBar actionBar = getSupportActionBar();
        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Manajemen Received Order");

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

        // INIT ARRAYS AND ADAPTER FOR FILTERING
        /*String[] approvalStatus = {"Valid", "Belum Valid"};
        String[] invoicedStatus = {"Sudah", "Belum"};
        String[] searchType = {"ID Good Issue", "ID Received Order", "Nomor PO Customer", "NOPOL Kendaraan"};
        String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID"};
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
        spinnerMaterialType.setAdapter(arrayAdapterMaterialType);*/

        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();


        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_good_issue_management, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_status);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();

            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        fabActionCreateRo.setOnClickListener(view -> {
            //int LAUNCH_SECOND_ACTIVITY = 1;
            Intent i = new Intent(this, AddReceivedOrder.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            fabExpandMenu.collapse();
            /*Intent intent = new Intent(ReceivedOrderManagementActivity.this, AddReceivedOrder.class);
            startActivity(intent);*/
        });

        fabActionArchivedData.setOnClickListener(view ->
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show());
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
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_status){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByCompany.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByStatus.setVisibility(View.GONE);
                llWrapFilterByCompany.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_company){
                llWrapFilterByDateRange.setVisibility(View.GONE);
                llWrapFilterByStatus.setVisibility(View.GONE);
            }
        } else {
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_status){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByCompany.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByStatus.setVisibility(View.VISIBLE);
                llWrapFilterByCompany.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_company){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
                llWrapFilterByStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDataDefaultQuery() {

        Query query = databaseReference.child("ReceivedOrders").orderByChild("roDateCreated");
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
                    //dialogInterface.roNotExistsDialog(ReceivedOrderManagementActivity.this);
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
        });


       /* databaseReference.child("GoodIssueData").orderByChild("giDateCreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    if (item.child("giPoCustNumber").getValue().toString().equals("-")){
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        goodIssueModelArrayList.add(goodIssueModel);
                    } else {
                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                        goodIssueModelArrayList.add(goodIssueModel);
                    }
                }

                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);

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

    private void showDataSearchByType(String newText, String searchTypeData) {
        Query query = databaseReference.child("ReceivedOrders").orderByChild(searchTypeData).startAt(newText).endAt(newText+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receivedOrderModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    ReceivedOrderModel receivedOrderModel = item.getValue(ReceivedOrderModel.class);
                    receivedOrderModelArrayList.add(receivedOrderModel);

                }
                llNoData.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                Collections.reverse(receivedOrderModelArrayList);
                roManagementAdapter = new ROManagementAdapter(context, receivedOrderModelArrayList);
                rvReceivedOrderList.setAdapter(roManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                                Intent i = new Intent(this, AddReceivedOrder.class);
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
        firstViewDataFirstTimeStatus = true;
        helper.refreshDashboard(this.getApplicationContext());
        super.onBackPressed();
    }
}