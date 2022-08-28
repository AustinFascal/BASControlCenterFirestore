package com.ptbas.controlcenter.create;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.RecapGoodIssueDataActivity;
import com.ptbas.controlcenter.adapter.PreviewProductItemAdapter;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ProductModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utils.LangUtils;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenuItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AddReceivedOrder extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    LinearLayout llList,llAddItem, llInputAllData, llPoNumberAvailability;
    String monthStrVal, dayStrVal;
    Button btnAddRow, btnLockRow, btnUnlockRow, btnNoPoNumber, btnPoNumberAvailable;
    TextInputEditText edtPoDate, edtPoTOP, edtPoNumberCustomer, edtPoNumberPtbas;
    TextInputLayout wrapEdtPoNumberPtBas, txtInputEdtPoNumberCustomer;
    AutoCompleteTextView spinnerPoTransportType, spinnerPoCustName, spinnerPoCurrency, spinnerRoType;
    List<String> productName, transportTypeName, customerName, currencyName;
    String transportData = "", customerData = "", customerID ="", randomString="NULL", currencyData="";
    Integer poYear = 0, poMonth = 0, poDay = 0;
    Integer roType;

    double totalSellPrice = 0, totalBuyPrice = 0;

    private DatePickerDialog datePicker;

    FloatingActionButton fabProceed;
    FloatingActionsMenu fabExpandMenu;
    com.getbase.floatingactionbutton.FloatingActionButton fabActionSaveCloud, fabActionUpdateData,
            fabActionGenerateQrCode, fabActionSaveToPdf;

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    ArrayList<ProductItems> productItemsArrayList = new ArrayList<>();

    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ConstraintLayout bottomSheet;

    DialogInterface dialogInterface = new DialogInterface();
    Helper helper = new Helper();

    Button btnChooseRoType;

    PreviewProductItemAdapter previewProductItemAdapter;

    String[] roTypeStr = {"JASA ANGKUT + MATERIAL", "MATERIAL SAJA", "JASA ANGKUT SAJA"};
    Integer[] roTypeVal = {0, 1, 2};
    ArrayList<String> arrayListRoType = new ArrayList<>(Arrays.asList(roTypeStr));

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference refRO = db.collection("ReceivedOrderData").document();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_received_order);

        LangUtils.setLocale(this, "en");

        ArrayAdapter<String> arrayAdapterRoType = new ArrayAdapter<>(this, R.layout.style_spinner, arrayListRoType);

        spinnerRoType = findViewById(R.id.spinner_ro_type);
        spinnerRoType.setAdapter(arrayAdapterRoType);

        spinnerRoType.setOnItemClickListener((adapterView, view, i, l) -> {
            //spinnerRoType.setText(adapterView.indexOfChild(i));
            llList.removeAllViews();
            btnLockRow.setVisibility(View.GONE);
            llAddItem.setVisibility(View.VISIBLE);
            llList.requestFocus();
            switch (i) {
                case 0:
                    roType = roTypeVal[0];
                    addViewInit();
                    btnAddRow.setVisibility(View.VISIBLE);
                    fabProceed.setVisibility(View.GONE);
                    break;
                case 1:
                    roType = roTypeVal[1];
                    addView();
                    btnAddRow.setVisibility(View.GONE);
                    fabProceed.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    roType = roTypeVal[2];
                    addViewInit();
                    btnAddRow.setVisibility(View.GONE);
                    fabProceed.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        });

        bottomSheet = findViewById(R.id.bottomSheetPODetails);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        transportTypeName  = new ArrayList<>();
        customerName  = new ArrayList<>();
        currencyName = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Buat Received Order (RO)");
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


        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        llInputAllData = findViewById(R.id.ll_input_all_data);
        llList = findViewById(R.id.layout_list);
        llAddItem = findViewById(R.id.ll_add_item);
        btnAddRow = findViewById(R.id.btn_add_list);
        btnLockRow = findViewById(R.id.btn_lock_row);
        btnUnlockRow = findViewById(R.id.btn_unlock_row);
        edtPoNumberPtbas = findViewById(R.id.edt_po_number_ptbas);
        wrapEdtPoNumberPtBas = findViewById(R.id.wrap_edt_po_number_ptbas);
        edtPoDate = findViewById(R.id.edt_po_date);
        edtPoTOP = findViewById(R.id.edt_po_TOP);
        edtPoNumberCustomer = findViewById(R.id.edt_po_number_customer);
        spinnerPoTransportType = findViewById(R.id.spinner_po_transport_type);
        spinnerPoCustName = findViewById(R.id.spinner_po_cust_name);
        spinnerPoCurrency = findViewById(R.id.spinner_po_currency);
        fabProceed = findViewById(R.id.fab_save_po_data);

        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionSaveCloud = findViewById(R.id.fab_action_save_cloud);
        fabActionUpdateData = findViewById(R.id.fab_action_update_data);
        /*fabActionGenerateQrCode = findViewById(R.id.fab_action_generate_qr_code);
        fabActionSaveToPdf = findViewById(R.id.fab_action_save_to_pdf);*/

        fabExpandMenu.setVisibility(View.GONE);
        llAddItem.setVisibility(View.GONE);
        spinnerPoCustName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerPoTransportType.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        txtInputEdtPoNumberCustomer = findViewById(R.id.txt_input_edt_po_number_customer);
        btnPoNumberAvailable = findViewById(R.id.btn_po_number_available);

        txtInputEdtPoNumberCustomer.setVisibility(View.GONE);

        btnPoNumberAvailable.setOnClickListener(view -> {
            edtPoNumberCustomer.setText("");
            btnPoNumberAvailable.setVisibility(View.GONE);
            txtInputEdtPoNumberCustomer.setVisibility(View.VISIBLE);
            //llPoNumberAvailability.setVisibility(View.GONE);
        });

        spinnerPoCurrency.setText(R.string.default_currency);

        databaseReference.child("TransportTypeData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("name").getValue(String.class);
                        transportTypeName.add(spinnerMaterialData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddReceivedOrder.this, R.layout.style_spinner, transportTypeName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoTransportType.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.collection("CustomerData").whereEqualTo("custStatus", true)
                .addSnapshotListener((value, error) -> {
                    customerName.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String spinnerCustUID = Objects.requireNonNull(d.get("custUID")).toString();
                                String spinnerCustName = Objects.requireNonNull(d.get("custName")).toString();
                                customerName.add(spinnerCustUID+" - "+spinnerCustName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddReceivedOrder.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerPoCustName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        databaseReference.child("CurrencyData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerCurrencyData = dataSnapshot.child("currencyName").getValue(String.class);
                        currencyName.add(spinnerCurrencyData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddReceivedOrder.this, R.layout.style_spinner, currencyName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoCurrency.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edtPoDate.setOnClickListener(view -> {
            edtPoDate.setError(null);
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String year = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(AddReceivedOrder.this,
                    (datePicker, year1, month, dayOfMonth) -> {
                        poYear = year1;
                        poMonth = month + 1;
                        poDay = dayOfMonth;

                        if(month < 10){
                            monthStrVal = "0" + (month+1);
                        } else {
                            monthStrVal = String.valueOf(month+1);
                        }
                        if(dayOfMonth < 10){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = poYear + "-" +monthStrVal + "-" + dayStrVal;

                        edtPoDate.setText(finalDate);
                    }, Integer.parseInt(year), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();
        });

        edtPoTOP.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(AddReceivedOrder.this,
                    (datePicker, year12, month1, dayOfMonth) -> edtPoTOP.setText(year12 + "-" +(month1 + 1) + "-" + dayOfMonth), year, month, day);
            datePicker.show();
        });

        btnAddRow.setOnClickListener(view -> addView());

        spinnerPoTransportType.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
            transportData = selectedSpinnerTransportType;
            spinnerPoTransportType.setError(null);
        });

        spinnerPoTransportType.setOnFocusChangeListener((view, b) -> spinnerPoTransportType.setText(transportData));

        spinnerPoCustName.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerCustomerName = (String) adapterView.getItemAtPosition(position);
            customerData = selectedSpinnerCustomerName;
            spinnerPoCustName.setError(null);
            String[] custID =  selectedSpinnerCustomerName.split("-");
            customerID = custID[0];
            randomString = getRandomString(5);
        });

        spinnerPoCustName.setOnFocusChangeListener((view, b) -> spinnerPoCustName.setText(customerData));

        spinnerPoCurrency.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedCurrency = (String) adapterView.getItemAtPosition(i);
            currencyData = selectedCurrency;
            spinnerPoCurrency.setError(null);
        });

        spinnerPoCurrency.setOnFocusChangeListener((view, b) ->
                spinnerPoCurrency.setText(currencyData));

        //TODO Make handler as onkeychangelistener
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (transportData.isEmpty() || poMonth==0 || poYear==0 || customerID.isEmpty() || spinnerPoCurrency.getText().toString().isEmpty()){
                    edtPoNumberPtbas.setText("");
                } else{
                    String poNumberCustomer = Objects.requireNonNull(edtPoNumberCustomer.getText()).toString();
                    if (poNumberCustomer.isEmpty() || poNumberCustomer.equals("-")){
                        edtPoNumberPtbas.setText(customerID +"- "+transportData.substring(0, 3)+" - "+randomString+" "+poYear+monthStrVal);
                    } else {
                        edtPoNumberPtbas.setText(customerID +"- "+transportData.substring(0, 3)+" - "+poNumberCustomer);
                    }

                }
                handler.postDelayed(this, 500);
            }
        };

        runnable.run();
        //addViewInit();

        btnLockRow.setOnClickListener(view -> {
            fabProceed.setVisibility(View.VISIBLE);
            btnUnlockRow.setVisibility(View.VISIBLE);
            btnLockRow.setVisibility(View.GONE);
            llAddItem.setVisibility(View.GONE);
            //llList.setVisibility(false);
        });

        btnUnlockRow.setOnClickListener(view -> {
            fabProceed.setVisibility(View.GONE);
            btnUnlockRow.setVisibility(View.GONE);
            btnLockRow.setVisibility(View.VISIBLE);
            llAddItem.setVisibility(View.VISIBLE);
            //llList.setEnabled(true);
        });

        fabProceed.setVisibility(View.GONE);

        fabProceed.setOnClickListener(view -> {
            String roCreatedBy = helper.getUserId();
            String roDateCreated = Objects.requireNonNull(edtPoDate.getText()).toString();
            String roTOP = "";
            String roMatTransport = Objects.requireNonNull(spinnerPoTransportType.getText()).toString();
            String roCurrency = Objects.requireNonNull(spinnerPoCurrency.getText()).toString();
            String roCustName = Objects.requireNonNull(spinnerPoCustName.getText()).toString();
            String roPoCustNumber = Objects.requireNonNull(edtPoNumberCustomer.getText()).toString();
            String roUID = Objects.requireNonNull(edtPoNumberPtbas.getText()).toString();

            if (TextUtils.isEmpty(roDateCreated)) {
                edtPoDate.setError("Mohon masukkan tanggal order");
                edtPoDate.requestFocus();
            }

            if (edtPoTOP.getText().toString().equals("")) {
                roTOP = "-";
            } else {
                roTOP = Objects.requireNonNull(edtPoTOP.getText()).toString();
            }

            if (edtPoNumberCustomer.getText().toString().equals("")) {
                edtPoNumberCustomer.setText("-");
                roPoCustNumber = "-";
            }

            if (TextUtils.isEmpty(roMatTransport)) {
                spinnerPoTransportType.setError("Mohon masukkan jenis material");
                spinnerPoTransportType.requestFocus();
            }

            if (TextUtils.isEmpty(roCurrency)) {
                spinnerPoCurrency.setError("Mohon pilih mata uang yang digunakan untuk transaksi");
                spinnerPoCurrency.requestFocus();
            }

            if (TextUtils.isEmpty(roCustName)) {
                spinnerPoCustName.setError("Mohon masukkan nama customer");
                spinnerPoCustName.requestFocus();
            }

            if (!TextUtils.isEmpty(roDateCreated)&&!TextUtils.isEmpty(roMatTransport)&&!TextUtils.isEmpty(roCurrency)&&
                    !TextUtils.isEmpty(roCustName)&&!TextUtils.isEmpty(roUID)){
                insertData(roUID, roCreatedBy, roDateCreated, roTOP, roMatTransport, roCurrency, roPoCustNumber,
                        roCustName, roType,false);
            }

        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomSheetExpanded();
                        fabExpandMenu.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottomSheetCollapsed();
                        fabExpandMenu.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }


    private void bottomSheetExpanded() {
        llInputAllData.setVisibility(View.INVISIBLE);
        fabProceed.setVisibility(View.GONE);
        fabExpandMenu.setVisibility(View.VISIBLE);
        View viewLayout = AddReceivedOrder.this.getCurrentFocus();
        if (viewLayout != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
        }
    }

    private void bottomSheetCollapsed() {
        llInputAllData.setVisibility(View.VISIBLE);
        fabProceed.setVisibility(View.VISIBLE);
        fabExpandMenu.setVisibility(View.GONE);
        fabExpandMenu.collapse();
    }

    // Get passed data and save to cloud
    private void insertData(String roUID, String roCreatedBy, String roDateCreated,
                            String roTOP, String roMatTransport, String roCurrency,
                            String roPoCustNumber, String roCustName, Integer roType, Boolean roSatus) {

        RelativeLayout wrapTitle = bottomSheet.findViewById(R.id.wrapTitle);
        TextView tvPoCurrency = bottomSheet.findViewById(R.id.tvPoCurrency);
        TextView tvPoPtBasNumber = bottomSheet.findViewById(R.id.tvPoPtBasNumber);
        TextView tvPoDate = bottomSheet.findViewById(R.id.tvPoDate);
        TextView tvPoTOP = bottomSheet.findViewById(R.id.tvPoTOP);
        TextView tvPoTransportType = bottomSheet.findViewById(R.id.tvPoTransportType);
        TextView tvPoCustomerName = bottomSheet.findViewById(R.id.tvPoCustomerName);
        TextView tvPoCustomerNumber = bottomSheet.findViewById(R.id.tvPoCustomerNumber);
        ImageView ivCloseBottomSheetDetail = bottomSheet.findViewById(R.id.ivCloseBottomSheetDetail);

        TextView tvSubTotalBuy = bottomSheet.findViewById(R.id.tvSubTotalBuy);
        TextView tvSubTotalSell = bottomSheet.findViewById(R.id.tvSubTotalSell);
        TextView tvTotalVAT = bottomSheet.findViewById(R.id.tvTotalVAT);
        TextView tvTotalSellFinal = bottomSheet.findViewById(R.id.tvTotalSellFinal);
        TextView tvEstProfit = bottomSheet.findViewById(R.id.tvEstProfit);

        double poSubTotalBuy = 0, poSubTotalSell = 0, poVAT = 0, poTotalSellFinal = 0, poEstProfit = 0;

        ivCloseBottomSheetDetail.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetCollapsed();
        });

        wrapTitle.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetCollapsed();
        });

        if (checkIfValidAndProceed()) {
            tvPoCurrency.setText(roCurrency);
            tvPoPtBasNumber.setText(roUID);
            tvPoDate.setText(roDateCreated);
            tvPoTOP.setText(roTOP);
            tvPoTransportType.setText(roMatTransport);
            tvPoCustomerName.setText(roCustName);
            tvPoCustomerNumber.setText(roPoCustNumber);

            try{
                double sumSubTotalBuy = 0, sumSubTotalSell = 0, sumTotalVAT = 0, sumTotalSellFinal = 0, sumEstProfit = 0;
                for(ProductItems productItems : productItemsArrayList) {
                    sumSubTotalBuy += productItems.matTotalBuyPrice;
                    sumSubTotalSell += productItems.matTotalSellPrice;
                    sumTotalVAT = (0.11)*(sumSubTotalSell);
                    sumTotalSellFinal = sumSubTotalSell+sumTotalVAT;
                    sumEstProfit = sumSubTotalSell-sumSubTotalBuy;

                    poSubTotalBuy = sumSubTotalBuy;
                    poSubTotalSell = sumSubTotalSell;
                    poVAT = sumTotalVAT;
                    poTotalSellFinal = sumTotalSellFinal;
                    poEstProfit = sumEstProfit;
                }

                tvSubTotalBuy.setText(currencyFormat(String.valueOf(sumSubTotalBuy)));
                tvSubTotalSell.setText(currencyFormat(String.valueOf(sumSubTotalSell)));
                tvTotalVAT.setText(currencyFormat(String.valueOf(sumTotalVAT)));
                tvTotalSellFinal.setText(currencyFormat(String.valueOf(sumTotalSellFinal)));
                tvEstProfit.setText(currencyFormat(String.valueOf(sumEstProfit)));

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                RecyclerView rvItems = bottomSheet.findViewById(R.id.rvItems);
                rvItems.setHasFixedSize(true);
                rvItems.setLayoutManager(new LinearLayoutManager(this));

                previewProductItemAdapter = new PreviewProductItemAdapter(this, getList());
                rvItems.setAdapter(previewProductItemAdapter);

                HashMap<String, List<ProductItems>> productItemsHashMap = new HashMap<>();
                for(int i=0; i<productItemsArrayList.size(); i++) {
                    String sortID = productItemsArrayList.get(i).getMatName();
                    List<ProductItems> objectList = productItemsHashMap.get(sortID);
                    if(objectList == null) {
                        objectList = new ArrayList<>();
                    }
                    objectList.add(productItemsArrayList.get(i));
                    productItemsHashMap.put(sortID, objectList);
                }

                // Create RO object
                String roDocumentID = refRO.getId();
                ReceivedOrderModel receivedOrderModel = new ReceivedOrderModel(
                        roDocumentID, roUID, roCreatedBy, roDateCreated, roTOP, roMatTransport, roCurrency,
                        roPoCustNumber, roCustName, roType, poSubTotalBuy, poSubTotalSell, poVAT,
                        poTotalSellFinal, poEstProfit, roSatus, productItemsHashMap);

                fabActionSaveCloud.setOnClickListener(view -> {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                        refRO.set(receivedOrderModel)
                                .addOnSuccessListener(unused -> {
                                    Intent intent = new Intent();
                                    intent.putExtra("addedStatus", "true");
                                    intent.putExtra("activityType", "RO");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }).addOnFailureListener(e ->
                                        Toast.makeText(AddReceivedOrder.this, "FAILED", Toast.LENGTH_SHORT).show());

                    }
                });

                fabActionUpdateData.setOnClickListener(view -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetCollapsed();
                });

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(AddReceivedOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private List<ProductItems> getList() {
        return productItemsArrayList;
    }

    private boolean checkIfValidAndProceed() {

        DecimalFormat df = new DecimalFormat("0.00");

        productItemsArrayList.clear();
        boolean result = true;

        for (int i=0; i<llList.getChildCount();i++){
            View productItemView = llList.getChildAt(i);

            AutoCompleteTextView spinnerMaterialName = productItemView.findViewById(R.id.spinner_po_material_name);
            TextInputEditText edtSalePrice = productItemView.findViewById(R.id.edt_po_sale_price);
            TextInputEditText edtBuyPrice = productItemView.findViewById(R.id.edt_po_buy_price);
            TextInputEditText edtPoQuantity = productItemView.findViewById(R.id.edt_po_quantity);
            TextInputEditText edtPoTotalSellPrice = productItemView.findViewById(R.id.edt_po_total_sell_price);
            TextInputEditText edtPoTotalBuyPrice = productItemView.findViewById(R.id.edt_po_total_buy_price);

            ProductItems productItems = new ProductItems();

            if (!spinnerMaterialName.getText().toString().equals("")){
                productItems.setMatName(spinnerMaterialName.getText().toString());
            } else{
                result = false;
                break;
            }

            if (!edtPoQuantity.getText().toString().equals("")){
                productItems.setMatQuantity(Integer.parseInt(edtPoQuantity.getText().toString()));
            }else{
                result = false;
                break;
            }

            if (!edtPoQuantity.getText().toString().equals("0")){
                productItems.setMatQuantity(Integer.parseInt(edtPoQuantity.getText().toString()));
            }else{
                result = false;
                break;
            }

            if (!edtSalePrice.getText().toString().equals("")){
                productItems.setMatSellPrice(Double.valueOf(edtSalePrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            if (!edtBuyPrice.getText().toString().equals("")){
                productItems.setMatBuyPrice(Double.valueOf(edtBuyPrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            if (!edtPoTotalSellPrice.getText().toString().equals("")){
                productItems.setMatTotalSellPrice(Double.valueOf(edtPoTotalSellPrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            if (!edtPoTotalBuyPrice.getText().toString().equals("")){
                productItems.setMatTotalBuyPrice(Double.valueOf(edtPoTotalBuyPrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            productItemsArrayList.add(productItems);
        }

        if (productItemsArrayList.size()==0){
            //btnAddRow.setVisibility(View.VISIBLE);
            result = false;
            Toast.makeText(this, "Tambahkan item terlebih dahulu.", Toast.LENGTH_SHORT).show();
        } else if (!result){
            Toast.makeText(this, "Masukkan semua detail item dengan benar.", Toast.LENGTH_SHORT).show();
        } else{
            if (roType == 0){
                fabProceed.setVisibility(View.GONE);
            } else if (roType == 1){
                fabProceed.setVisibility(View.VISIBLE);
            } else if (roType == 2){
                fabProceed.setVisibility(View.VISIBLE);
            }
            /*if (llList.getChildCount()==1){
                btnAddRow.setVisibility(View.GONE);
                btnLockRow.setVisibility(View.VISIBLE);
                //fabProceed.setVisibility(View.VISIBLE);
            } else if (llList.getChildCount()<1){
                btnAddRow.setVisibility(View.VISIBLE);
                btnLockRow.setVisibility(View.GONE);
                btnUnlockRow.setVisibility(View.GONE);
                fabProceed.setVisibility(View.GONE);
            }*/
        }

        return result;
    }


    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void addView() {
        DecimalFormat df = new DecimalFormat("0.00");
        View materialView = getLayoutInflater().inflate(R.layout.row_add_material, null, false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        productName  = new ArrayList<>();

        AutoCompleteTextView spinnerMaterialName = materialView.findViewById(R.id.spinner_po_material_name);
        TextInputEditText edtSalePrice = materialView.findViewById(R.id.edt_po_sale_price);
        TextInputEditText edtBuyPrice = materialView.findViewById(R.id.edt_po_buy_price);
        TextInputEditText edtPoQuantity = materialView.findViewById(R.id.edt_po_quantity);
        TextInputEditText edtPoTotalSellPrice = materialView.findViewById(R.id.edt_po_total_sell_price);
        TextInputEditText edtPoTotalBuyPrice = materialView.findViewById(R.id.edt_po_total_buy_price);
        ImageView imgDeleteRow = materialView.findViewById(R.id.img_remove_row);

        databaseReference.child("ProductData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("productName").getValue(String.class);
                        productName.add(spinnerMaterialData);
                        productName.remove("JASA ANGKUT");
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddReceivedOrder.this, R.layout.style_spinner, productName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerMaterialName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (llList.getChildCount()==1){
            btnAddRow.setVisibility(View.GONE);
            btnLockRow.setVisibility(View.VISIBLE);
        } else if (llList.getChildCount()<1){
            btnAddRow.setVisibility(View.VISIBLE);
            btnLockRow.setVisibility(View.GONE);
            btnUnlockRow.setVisibility(View.GONE);
            fabProceed.setVisibility(View.GONE);
        }

        spinnerMaterialName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductData/"+spinnerMaterialName.getText().toString().replaceAll(" ", "").toLowerCase());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProductModel productModel = snapshot.getValue(ProductModel.class);

                        if (productModel!=null){
                            edtPoQuantity.setText(String.valueOf(0));
                            edtBuyPrice.setText(String.valueOf(df.format(productModel.getPriceBuy())));
                            edtSalePrice.setText(String.valueOf(df.format(productModel.getPriceSell())));

                            final Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    if(!edtPoQuantity.getText().toString().equals("")){
                                        double quantity = Double.parseDouble(edtPoQuantity.getText().toString());
                                        if (edtBuyPrice.getText().toString().equals("")){
                                            totalBuyPrice = quantity*0;
                                        } else {
                                            double buyPrice = Double.parseDouble(edtBuyPrice.getText().toString());
                                            totalBuyPrice = quantity*buyPrice;
                                        }

                                        if (edtSalePrice.getText().toString().equals("")){
                                            totalSellPrice = quantity*0;
                                        } else {
                                            double salePrice = Double.parseDouble(edtSalePrice.getText().toString());
                                            totalSellPrice = quantity*salePrice;
                                        }

                                    } else {
                                        totalSellPrice = 0;
                                        totalBuyPrice = 0;
                                    }

                                   /* edtBuyPrice.setOnKeyListener((view1, i1, keyEvent) -> {
                                        if (edtBuyPrice.getText().toString().equals("")){
                                            edtBuyPrice.setText("");
                                        }
                                        return false;
                                    });

                                    edtSalePrice.setOnKeyListener((view12, i12, keyEvent) -> {
                                        if (edtSalePrice.getText().toString().equals("")){
                                            edtSalePrice.setText("");
                                        }
                                        return false;
                                    });*/

                                    edtPoTotalBuyPrice.setText(String.format("%.2f", totalBuyPrice));
                                    edtPoTotalSellPrice.setText(String.format("%.2f", totalSellPrice));
                                    handler.postDelayed(this, 500);

                                }
                            };

                            runnable.run();

                        } else {
                            Toast.makeText(AddReceivedOrder.this, "Null", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        imgDeleteRow.setOnClickListener(view -> {
            llList.requestFocus();
            removeView(materialView);
            btnAddRow.setVisibility(View.VISIBLE);
            btnLockRow.setVisibility(View.GONE);
            fabProceed.setVisibility(View.GONE);
            if (roType==1){
                btnAddRow.setOnClickListener(view1 -> {
                    addView();
                    btnAddRow.setVisibility(View.GONE);
                    fabProceed.setVisibility(View.VISIBLE);
                });
            }
        });

        llList.addView(materialView);
    }

    private void addViewInit() {
        DecimalFormat df = new DecimalFormat("0.00");
        View materialView = getLayoutInflater().inflate(R.layout.row_add_material, null, false);

        productName  = new ArrayList<>();

        AutoCompleteTextView spinnerMaterialName = materialView.findViewById(R.id.spinner_po_material_name);
        TextInputEditText edtSalePrice = materialView.findViewById(R.id.edt_po_sale_price);
        TextInputEditText edtBuyPrice = materialView.findViewById(R.id.edt_po_buy_price);
        TextInputEditText edtPoQuantity = materialView.findViewById(R.id.edt_po_quantity);
        TextInputEditText edtPoTotalSellPrice = materialView.findViewById(R.id.edt_po_total_sell_price);
        TextInputEditText edtPoTotalBuyPrice = materialView.findViewById(R.id.edt_po_total_buy_price);
        ImageView imgDeleteRow = materialView.findViewById(R.id.img_remove_row);
        imgDeleteRow.setEnabled(false);
        imgDeleteRow.setColorFilter(ContextCompat.getColor(this, R.color.light_grey));
        spinnerMaterialName.setInputType(InputType.TYPE_NULL);
        spinnerMaterialName.setFocusable(false);

        spinnerMaterialName.setText("JASA ANGKUT");

        DatabaseReference databaseReferenceJasaAngkut = FirebaseDatabase.getInstance().getReference("ProductData/jasaangkut");
        databaseReferenceJasaAngkut.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel productModel = snapshot.getValue(ProductModel.class);

                if (productModel!=null){
                    edtPoQuantity.setText(String.valueOf(0));
                    edtBuyPrice.setText(String.valueOf(df.format(productModel.getPriceBuy())));
                    edtSalePrice.setText(String.valueOf(df.format(productModel.getPriceSell())));

                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        public void run() {
                            if(!edtPoQuantity.getText().toString().equals("")){
                                double quantity = Double.parseDouble(edtPoQuantity.getText().toString());
                                if (edtBuyPrice.getText().toString().equals("")){
                                    totalBuyPrice = quantity*0;
                                } else {
                                    double buyPrice = Double.parseDouble(edtBuyPrice.getText().toString());
                                    totalBuyPrice = quantity*buyPrice;
                                }

                                if (edtSalePrice.getText().toString().equals("")){
                                    totalSellPrice = quantity*0;
                                } else {
                                    double salePrice = Double.parseDouble(edtSalePrice.getText().toString());
                                    totalSellPrice = quantity*salePrice;
                                }

                            } else {
                                totalSellPrice = 0;
                                totalBuyPrice = 0;
                            }

                            /*edtBuyPrice.setOnKeyListener((view, i, keyEvent) -> {
                                if (edtBuyPrice.getText().toString().equals("")){
                                    edtBuyPrice.setText("");
                                }
                                return false;
                            });

                            edtSalePrice.setOnKeyListener((view, i, keyEvent) -> {
                                if (edtSalePrice.getText().toString().equals("")){
                                    edtSalePrice.setText("");
                                }
                                return false;
                            });*/

                            edtPoTotalBuyPrice.setText(String.format("%.2f", totalBuyPrice));
                            edtPoTotalSellPrice.setText(String.format("%.2f", totalSellPrice));
                            handler.postDelayed(this, 500);

                        }
                    };

                    runnable.run();

                } else {
                    Toast.makeText(AddReceivedOrder.this, "Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        llList.addView(materialView);
    }

    private void removeView(View v){
        llList.removeView(v);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetCollapsed();
        } else {
            dialogInterface.discardDialogConfirmation(AddReceivedOrder.this);
        }

    }
}