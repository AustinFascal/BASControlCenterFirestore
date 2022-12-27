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
import android.widget.ImageButton;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.PreviewProductItemAdapter;
import com.ptbas.controlcenter.utility.NumberTextWatcherUtils;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ProdModel;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AddRoActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    LinearLayout llList,llAddItem, llInputAllData, llShowSpinnerRoAndEdtPo;
    String monthStrVal, dayStrVal;
    Button btnAddRow, btnLockRow, btnUnlockRow;
    TextInputEditText edtPoDate, edtPoNumberCustomer, edtRoNumber, edtConnectingRONumber;
    TextInputLayout wrapEdtPoNumberPtBas, txtInputEdtPoNumberCustomer;
    AutoCompleteTextView spinnerPoTransportType, spinnerPoCustName, spinnerPoCurrency, spinnerRoType, spinnerPoTOP, spinnerTaxType, spinnerRoUID;
    List<String> productName, transportTypeName, customerName, currencyName, arrayListCustDocumentID;
    String transportData = "", customerData = "", customerID ="", randomString="NULL", currencyData="", custDocumentID = "";
    Integer poYear = 0, poMonth = 0, poDay = 0;
    Integer roType;

    double totalSellPrice = 0, totalBuyPrice = 0;

    double roCountQuantity;

    private DatePickerDialog datePicker;

    FloatingActionButton fabProceed;
    FloatingActionsMenu fabExpandMenu;
    com.getbase.floatingactionbutton.FloatingActionButton fabActionSaveCloud, fabActionUpdateData;
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    ArrayList<ProductItems> productItemsArrayList = new ArrayList<>();


    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ConstraintLayout bottomSheet;

    TextInputLayout spinnerTaxTypeWrap;

    HelperUtils helperUtils = new HelperUtils();

    PreviewProductItemAdapter previewProductItemAdapter;

    Boolean taxTypeVal;

    String[] roTOPVal = {"14", "30", "60"};
    ArrayList<String> arrayListRoTOPVal = new ArrayList<>(Arrays.asList(roTOPVal));

    String[] roTypeStr = {"JASA ANGKUT + MATERIAL", "MATERIAL SAJA", "JASA ANGKUT SAJA"};
    String[] taxTypeStr = {"PKP", "NON PKP"};
    Integer[] roTypeVal = {0, 1, 2};
    ArrayList<String> arrayListRoType = new ArrayList<>(Arrays.asList(roTypeStr));
    ArrayList<String> arrayListTaxType = new ArrayList<>(Arrays.asList(taxTypeStr));

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //DocumentReference refRO = db.collection("ReceivedOrderData").document();

    ImageButton btnResetRouid, btnResetCustomer;

    //TextInputEditText edtPoUID;

    String roPoCustNumber, roDocumentID, roUID;

    List<String> receiveOrderNumberList;
    List<ProductItems> productItemsList;

    int matOnlyQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ro);

        LangUtils.setLocale(this, "en");

        ArrayAdapter<String> arrayAdapterRoType = new ArrayAdapter<>(this, R.layout.style_spinner, arrayListRoType);
        ArrayAdapter<String> arrayAdapterTaxType = new ArrayAdapter<>(this, R.layout.style_spinner, arrayListTaxType);
        ArrayAdapter<String> arrayAdapterRoTOP = new ArrayAdapter<>(this, R.layout.style_spinner, arrayListRoTOPVal);

        spinnerRoType = findViewById(R.id.spinner_ro_type);
        spinnerRoType.setAdapter(arrayAdapterRoType);

        spinnerPoTOP = findViewById(R.id.edt_po_TOP);
        spinnerPoTOP.setAdapter(arrayAdapterRoTOP);

        spinnerTaxType = findViewById(R.id.spinnerTaxType);
        spinnerTaxTypeWrap = findViewById(R.id.spinnerTaxTypeWrap);
        spinnerRoUID = findViewById(R.id.spinnerRoUID);

        //spinnerTaxTypeWrap.setVisibility(View.GONE);


        spinnerTaxType.setAdapter(arrayAdapterTaxType);

        spinnerTaxType.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0:
                    taxTypeVal = true;
                    break;
                case 1:
                    taxTypeVal = false;
                    break;
                default:
                    break;
            }
        });

        spinnerRoType.setOnItemClickListener((adapterView, view, i, l) -> {
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
                    llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                    break;
                case 1:
                    roType = roTypeVal[1];
                    addView();
                    btnAddRow.setVisibility(View.GONE);
                    fabProceed.setVisibility(View.VISIBLE);
                    llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                    break;
                case 2:
                    roType = roTypeVal[2];
                    addViewInit();
                    btnAddRow.setVisibility(View.GONE);
                    fabProceed.setVisibility(View.VISIBLE);
                    llShowSpinnerRoAndEdtPo.setVisibility(View.VISIBLE);
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
        arrayListCustDocumentID = new ArrayList<>();
        receiveOrderNumberList = new ArrayList<>();

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

        edtConnectingRONumber = findViewById(R.id.edtConnectingRONumber);

        //edtPoUID = findViewById(R.id.edtPoUID);
        btnResetRouid = findViewById(R.id.btnResetRouid);
        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        llInputAllData = findViewById(R.id.ll_input_all_data);
        llList = findViewById(R.id.layout_list);
        llAddItem = findViewById(R.id.ll_add_item);
        btnAddRow = findViewById(R.id.btn_add_list);
        btnLockRow = findViewById(R.id.btn_lock_row);
        btnUnlockRow = findViewById(R.id.btn_unlock_row);
        edtRoNumber = findViewById(R.id.edt_po_number_ptbas);
        wrapEdtPoNumberPtBas = findViewById(R.id.wrap_edt_po_number_ptbas);
        edtPoDate = findViewById(R.id.edt_po_date);
        edtPoNumberCustomer = findViewById(R.id.edt_po_number_customer);
        spinnerPoTransportType = findViewById(R.id.spinner_po_transport_type);
        spinnerPoCustName = findViewById(R.id.spinner_po_cust_name);
        spinnerPoCurrency = findViewById(R.id.spinner_po_currency);
        fabProceed = findViewById(R.id.fab_save_po_data);

        fabExpandMenu = findViewById(R.id.fab_expand_menu);
        fabActionSaveCloud = findViewById(R.id.fab_action_save_cloud);
        fabActionUpdateData = findViewById(R.id.fab_action_update_data);

        llShowSpinnerRoAndEdtPo = findViewById(R.id.llShowSpinnerRoAndEdtPo);

        fabExpandMenu.setVisibility(View.GONE);
        llAddItem.setVisibility(View.GONE);
        spinnerPoCustName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerPoTransportType.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        txtInputEdtPoNumberCustomer = findViewById(R.id.txt_input_edt_po_number_customer);

        spinnerPoCurrency.setText(R.string.default_currency);

        databaseReference.child("TransportTypeData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("name").getValue(String.class);
                        transportTypeName.add(spinnerMaterialData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddRoActivity.this, R.layout.style_spinner, transportTypeName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoTransportType.setAdapter(arrayAdapter);
                } else {
                    //Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
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
                                String custDocumentID = Objects.requireNonNull(d.get("custDocumentID")).toString();
                                String spinnerCustUID = Objects.requireNonNull(d.get("custUID")).toString();
                                String spinnerCustName = Objects.requireNonNull(d.get("custName")).toString();
                                customerName.add(spinnerCustUID+" - "+spinnerCustName);
                                arrayListCustDocumentID.add(custDocumentID);

                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddRoActivity.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerPoCustName.setAdapter(arrayAdapter);
                        } else {
                            //Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddRoActivity.this, R.layout.style_spinner, currencyName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoCurrency.setAdapter(arrayAdapter);
                } else {
                    //Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
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

            datePicker = new DatePickerDialog(AddRoActivity.this,
                    (datePicker, year1, month, dayOfMonth) -> {
                        poYear = year1;
                        poMonth = month + 1;
                        poDay = dayOfMonth;

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

                        String finalDate =  poYear + "-" +monthStrVal + "-" +  dayStrVal;

                        edtPoDate.setText(finalDate);
                    }, Integer.parseInt(year), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
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

            //spinnerTaxTypeWrap.setVisibility(View.VISIBLE);

            custDocumentID = arrayListCustDocumentID.get(position);

            spinnerRoUID.setText(null);
            //edtPoUID.setText(null);
            btnResetRouid.setVisibility(View.GONE);

            btnResetCustomer.setVisibility(View.VISIBLE);
            spinnerRoUID.setAdapter(null);

            receiveOrderNumberList.clear();

            db.collection("ReceivedOrderData").whereEqualTo("custDocumentID", custDocumentID)
                    .addSnapshotListener((value, error) -> {
                        if (!Objects.requireNonNull(value).isEmpty()) {
                            for (DocumentSnapshot e : value.getDocuments()) {

                                String spinnerPurchaseOrders = Objects.requireNonNull(e.get("roPoCustNumber")).toString();
                                String roType = Objects.requireNonNull(e.get("roType")).toString();
                                if (roType.equals("1")){
                                    receiveOrderNumberList.add(spinnerPurchaseOrders);
                                }

                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddRoActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerRoUID.setAdapter(arrayAdapter);
                        }
                    });
        });

        spinnerPoCurrency.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedCurrency = (String) adapterView.getItemAtPosition(i);
            currencyData = selectedCurrency;
            spinnerPoCurrency.setError(null);
        });

        spinnerPoCurrency.setOnFocusChangeListener((view, b) ->
                spinnerPoCurrency.setText(currencyData));

        spinnerTaxType.setAdapter(arrayAdapterTaxType);
        //TODO Make handler as onkeychangelistener
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (transportData.isEmpty() || poMonth==0 || poYear==0 || customerID.isEmpty() || spinnerPoCurrency.getText().toString().isEmpty()){
                    edtRoNumber.setText("");
                } else{
                    String poNumberCustomer = Objects.requireNonNull(edtPoNumberCustomer.getText()).toString();
                    if (poNumberCustomer.isEmpty() || poNumberCustomer.equals("-")){
                        edtRoNumber.setText(customerID +"- "+transportData.substring(0, 3)+" - "+randomString+" "+poYear+monthStrVal);
                    } else {
                        edtRoNumber.setText(customerID +"- "+transportData.substring(0, 3)+" - "+poNumberCustomer);
                    }

                }

                arrayAdapterTaxType.notifyDataSetChanged();
                db.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID)
                        .addSnapshotListener((value2, error2) -> {
                            if (value2 != null) {
                                if (!value2.isEmpty()) {
                                    for (DocumentSnapshot e : value2.getDocuments()) {
                                        CustModel custModel = e.toObject(CustModel.class);
                                        if (custModel.getCustNPWP().isEmpty()){
                                            spinnerTaxType.setText("NON PKP");
                                            taxTypeVal = false;
                                        } else{
                                            spinnerTaxType.setText("PKP");
                                            taxTypeVal = true;
                                        }
                                    }
                                }
                            }
                        });
                handler.postDelayed(this, 500);
            }
        };

        runnable.run();

        btnLockRow.setOnClickListener(view -> {
            fabProceed.setVisibility(View.VISIBLE);
            btnUnlockRow.setVisibility(View.VISIBLE);
            btnLockRow.setVisibility(View.GONE);
            llAddItem.setVisibility(View.GONE);
        });

        btnUnlockRow.setOnClickListener(view -> {
            fabProceed.setVisibility(View.GONE);
            btnUnlockRow.setVisibility(View.GONE);
            btnLockRow.setVisibility(View.VISIBLE);
            llAddItem.setVisibility(View.VISIBLE);
        });

        fabProceed.setVisibility(View.GONE);

        fabProceed.setOnClickListener(view -> {
            String roCreatedBy = helperUtils.getUserId();
            String roDateCreated = Objects.requireNonNull(edtPoDate.getText()).toString();
            int roTOP = 0;
            String roMatTransport = Objects.requireNonNull(spinnerPoTransportType.getText()).toString();
            String roCurrency = Objects.requireNonNull(spinnerPoCurrency.getText()).toString();
            String roCustName = Objects.requireNonNull(spinnerPoCustName.getText()).toString();
            String roPoCustNumber = Objects.requireNonNull(edtPoNumberCustomer.getText()).toString();
            String roUID = Objects.requireNonNull(edtRoNumber.getText()).toString();

            String roConnectingRoDocumentUID = Objects.requireNonNull(edtConnectingRONumber.getText()).toString();

            String roUIDReplace = roUID.replace(" - ","-");
            int roUIDSize = roUIDReplace.length();
            int indexLastRoUIDVal = roUIDReplace.lastIndexOf('-');
            String poUID = roUIDReplace.substring(indexLastRoUIDVal+1, roUIDSize);

            if (TextUtils.isEmpty(roDateCreated)) {
                edtPoDate.setError("Mohon masukkan tanggal order");
                edtPoDate.requestFocus();
            }

            if (spinnerPoTOP.getText().toString().equals("")) {
                spinnerPoTOP.setError("Mohon masukkan T.O.P.");
                spinnerPoTOP.requestFocus();
            } else{
                spinnerPoTOP.setError(null);
                roTOP = Integer.parseInt(Objects.requireNonNull(spinnerPoTOP.getText()).toString());
            }

            if (edtPoNumberCustomer.getText().toString().equals("")) {
                edtPoNumberCustomer.setText(poUID);
                roPoCustNumber = poUID;
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

            if (!TextUtils.isEmpty(roDateCreated)&&roTOP!=0&&!TextUtils.isEmpty(roMatTransport)&&!TextUtils.isEmpty(roCurrency)&&
                    !TextUtils.isEmpty(roCustName)&&!TextUtils.isEmpty(roUID)){

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

                ivCloseBottomSheetDetail.setOnClickListener(view1 -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetCollapsed();
                });

                wrapTitle.setOnClickListener(view1 -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetCollapsed();
                });

                if (checkIfValidAndProceed()) {
                    tvPoCurrency.setText(roCurrency);
                    tvPoPtBasNumber.setText(roUID);
                    tvPoDate.setText(roDateCreated);
                    tvPoTOP.setText(String.valueOf(roTOP));
                    tvPoTransportType.setText(roMatTransport);
                    tvPoCustomerName.setText(roCustName);
                    tvPoCustomerNumber.setText(poUID);

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
                        //String roDocumentID = refRO.getId();
                        RoModel roModel =
                                new RoModel(roUID, roUID, roCreatedBy,
                                        roDateCreated, roMatTransport, roCurrency, roPoCustNumber,
                                        custDocumentID, roConnectingRoDocumentUID, roType, roTOP, poSubTotalBuy,
                                        poSubTotalSell, roCountQuantity, poTotalSellFinal, poEstProfit,
                                        false, productItemsHashMap, taxTypeVal);

                        fabActionSaveCloud.setOnClickListener(view1 -> {
                            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){

                                if (!edtConnectingRONumber.getText().toString().equals("")){
                                    db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", edtConnectingRONumber.getText().toString()).get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> db.collection("ReceivedOrderData").document(edtConnectingRONumber.getText().toString()).update("roConnectingRoDocumentUID", roUID));
                                }

                                DocumentReference refRO = db.collection("ReceivedOrderData").document(roUID);
                                refRO.set(roModel)
                                        .addOnSuccessListener(unused -> {
                                            Intent intent = new Intent();
                                            intent.putExtra("addedStatus", "true");
                                            intent.putExtra("activityType", "RO");
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }).addOnFailureListener(e ->
                                                Toast.makeText(AddRoActivity.this, "FAILED", Toast.LENGTH_SHORT).show());

                            }
                        });

                        fabActionUpdateData.setOnClickListener(view1 -> {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            bottomSheetCollapsed();
                        });

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(AddRoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
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


        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            btnResetRouid.setVisibility(View.VISIBLE);

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            RoModel roModel = documentSnapshot.toObject(RoModel.class);
                            roModel.setRoDocumentID(documentSnapshot.getId());
                            roPoCustNumber = roModel.getRoPoCustNumber();
                            roDocumentID = roModel.getRoDocumentID();
                            roUID = roModel.getRoUID();


                            HashMap<String, List<ProductItems>> map = roModel.getRoOrderedItems();
                            for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                productItemsList = e.getValue();
                                for (int j = 0; j<productItemsList.size();j++){
                                    if (!productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                        matOnlyQuantity = productItemsList.get(0).getMatQuantity();
                                    }
                                }

                            }
                        }
                        edtConnectingRONumber.setText(roUID);
                    });

        });


        btnResetRouid.setOnClickListener(view -> {
            btnResetRouid.setVisibility(View.GONE);
            spinnerRoUID.setText(null);
            //edtPoUID.setText(null);
        });

        btnResetCustomer.setOnClickListener(view -> {
            btnResetCustomer.setVisibility(View.GONE);
            spinnerRoUID.setAdapter(null);
            spinnerPoCustName.setText(null);
            btnResetRouid.setVisibility(View.GONE);
            spinnerRoUID.setText(null);
            //edtPoUID.setText(null);
            receiveOrderNumberList.clear();
        });

    }

    private void bottomSheetExpanded() {
        llInputAllData.setVisibility(View.INVISIBLE);
        fabProceed.setVisibility(View.GONE);
        fabExpandMenu.setVisibility(View.VISIBLE);
        View viewLayout = AddRoActivity.this.getCurrentFocus();
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

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private List<ProductItems> getList() {
        return productItemsArrayList;
    }

    private boolean checkIfValidAndProceed() {

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
                productItems.setMatQuantity(Integer.parseInt(NumberTextWatcherUtils.trimCommaOfString(edtPoQuantity.getText().toString())));
            }else{
                result = false;
                break;
            }

            if (!edtPoQuantity.getText().toString().equals("0")){
                productItems.setMatQuantity(Integer.parseInt(NumberTextWatcherUtils.trimCommaOfString(edtPoQuantity.getText().toString())));
            }else{
                result = false;
                break;
            }

            if (!edtSalePrice.getText().toString().equals("")){
                productItems.setMatSellPrice(Double.valueOf(NumberTextWatcherUtils.trimCommaOfString(edtSalePrice.getText().toString())));
            } else{
                result = false;
                break;
            }

            if (!edtBuyPrice.getText().toString().equals("")){
                productItems.setMatBuyPrice(Double.valueOf(NumberTextWatcherUtils.trimCommaOfString(edtBuyPrice.getText().toString())));
            } else{
                result = false;
                break;
            }

            if (!edtPoTotalSellPrice.getText().toString().equals("")){
                productItems.setMatTotalSellPrice(Double.valueOf(NumberTextWatcherUtils.trimCommaOfString(edtPoTotalSellPrice.getText().toString())));
            } else{
                result = false;
                break;
            }

            if (!edtPoTotalBuyPrice.getText().toString().equals("")){
                productItems.setMatTotalBuyPrice(Double.valueOf(NumberTextWatcherUtils.trimCommaOfString(edtPoTotalBuyPrice.getText().toString())));
            } else{
                result = false;
                break;
            }

            productItemsArrayList.add(productItems);
        }

        if (productItemsArrayList.size()==0){
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


        edtPoQuantity.addTextChangedListener(new NumberTextWatcherUtils(edtPoQuantity));
        edtBuyPrice.addTextChangedListener(new NumberTextWatcherUtils(edtBuyPrice));
        edtSalePrice.addTextChangedListener(new NumberTextWatcherUtils(edtSalePrice));
        edtPoTotalBuyPrice.addTextChangedListener(new NumberTextWatcherUtils(edtPoTotalBuyPrice));
        edtPoTotalSellPrice.addTextChangedListener(new NumberTextWatcherUtils(edtPoTotalSellPrice));


        databaseReference.child("ProductData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if (Objects.equals(dataSnapshot.child("productStatus").getValue(), true)){
                            String spinnerMaterialData = dataSnapshot.child("productName").getValue(String.class);
                            productName.add(spinnerMaterialData);
                            productName.remove("JASA ANGKUT");
                        }

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddRoActivity.this, R.layout.style_spinner, productName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerMaterialName.setAdapter(arrayAdapter);
                } else {
                    //Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
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
                        ProdModel prodModel = snapshot.getValue(ProdModel.class);

                        if (prodModel !=null){
                            edtPoQuantity.setText(String.valueOf(0));
                            edtBuyPrice.setText(String.valueOf(df.format(prodModel.getPriceBuy())));
                            edtSalePrice.setText(String.valueOf(df.format(prodModel.getPriceSell())));

                            final Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    if(!edtPoQuantity.getText().toString().equals("")){
                                        double quantity = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtPoQuantity.getText().toString()));
                                        roCountQuantity = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtPoQuantity.getText().toString()));
                                        if (edtBuyPrice.getText().toString().equals("")){
                                            totalBuyPrice = quantity*0;
                                        } else {
                                            double buyPrice = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtBuyPrice.getText().toString()));
                                            totalBuyPrice = quantity*buyPrice;
                                        }

                                        if (edtSalePrice.getText().toString().equals("")){
                                            totalSellPrice = quantity*0;
                                        } else {
                                            double salePrice = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtSalePrice.getText().toString()));
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

                                    edtPoTotalBuyPrice.setText(currencyFormat(String.format("%.2f", totalBuyPrice)));
                                    edtPoTotalSellPrice.setText(currencyFormat(String.format("%.2f", totalSellPrice)));
                                    handler.postDelayed(this, 500);

                                }
                            };

                            runnable.run();

                        } else {
                            Toast.makeText(AddRoActivity.this, "Null", Toast.LENGTH_SHORT).show();
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

        edtBuyPrice.setVisibility(View.GONE);
        edtPoTotalBuyPrice.setVisibility(View.GONE);



        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (spinnerRoType.getText().toString().equals("JASA ANGKUT SAJA")){
                    edtPoQuantity.setText(String.valueOf(matOnlyQuantity));
                }
                handler.postDelayed(this, 500);
            }
        };

        runnable.run();

        edtPoQuantity.addTextChangedListener(new NumberTextWatcherUtils(edtPoQuantity));
        edtBuyPrice.addTextChangedListener(new NumberTextWatcherUtils(edtBuyPrice));
        edtSalePrice.addTextChangedListener(new NumberTextWatcherUtils(edtSalePrice));
        edtPoTotalBuyPrice.addTextChangedListener(new NumberTextWatcherUtils(edtPoTotalBuyPrice));
        edtPoTotalSellPrice.addTextChangedListener(new NumberTextWatcherUtils(edtPoTotalSellPrice));

        DatabaseReference databaseReferenceJasaAngkut = FirebaseDatabase.getInstance().getReference("ProductData/jasaangkut");
        databaseReferenceJasaAngkut.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProdModel prodModel = snapshot.getValue(ProdModel.class);

                if (prodModel !=null){
                    edtPoQuantity.setText(String.valueOf(0));
                    edtBuyPrice.setText(String.valueOf(df.format(prodModel.getPriceBuy())));
                    edtSalePrice.setText(String.valueOf(df.format(prodModel.getPriceSell())));

                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        public void run() {
                            if (!edtSalePrice.getText().toString().isEmpty()){
                                edtBuyPrice.setText(edtSalePrice.getText().toString());
                            }
                            if(!edtPoQuantity.getText().toString().equals("")){
                                double quantity = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtPoQuantity.getText().toString()));
                                if (edtBuyPrice.getText().toString().equals("")){
                                    totalBuyPrice = quantity*0;
                                } else {
                                    double buyPrice = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtBuyPrice.getText().toString()));
                                    totalBuyPrice = quantity*buyPrice;
                                }

                                if (edtSalePrice.getText().toString().equals("")){
                                    totalSellPrice = quantity*0;
                                } else {
                                    double salePrice = Double.parseDouble(NumberTextWatcherUtils.trimCommaOfString(edtSalePrice.getText().toString()));
                                    totalSellPrice = quantity*salePrice;
                                }

                            } else {
                                totalSellPrice = 0;
                                totalBuyPrice = 0;
                            }

                            edtPoTotalBuyPrice.setText(currencyFormat(String.format("%.2f", totalBuyPrice)));
                            edtPoTotalSellPrice.setText(currencyFormat(String.format("%.2f", totalSellPrice)));
                            handler.postDelayed(this, 100);

                        }
                    };

                    runnable.run();

                } else {
                    Toast.makeText(AddRoActivity.this, "Null", Toast.LENGTH_SHORT).show();
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
            finish();
            //dialogInterface.discardDialogConfirmation(AddReceivedOrder.this);
        }

    }
}