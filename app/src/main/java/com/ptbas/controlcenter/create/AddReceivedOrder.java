package com.ptbas.controlcenter.create;

import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.PreviewProductItemAdapter;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ProductModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AddReceivedOrder extends AppCompatActivity {

    LinearLayout llList,llAddItem, llInputAllData;
    Button btnAddRow;
    TextInputEditText edtPoDate, edtPoTOP, edtPoNumberCustomer, edtPoNumberPtbas;
    TextInputLayout wrapEdtPoNumberPtBas;
    AutoCompleteTextView spinnerPoTransportType, spinnerPoCustName, spinnerPoCurrency;
    List<String> productName, transportTypeName, customerName, currencyName;
    String transportData = "", customerData = "", customerAlias="", randomString="NULL", currencyData="";
    Integer poYear = 0, poMonth = 0, poDay = 0;

    double totalSellPrice = 0, totalBuyPrice = 0, sumTotalSellPrice = 0;

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

    PreviewProductItemAdapter previewProductItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_received_order);

        LangUtils.setLocale(this, "en");

        bottomSheet = findViewById(R.id.bottomSheetPODetails);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        transportTypeName  = new ArrayList<>();
        customerName  = new ArrayList<>();
        currencyName = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
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

        llInputAllData = findViewById(R.id.ll_input_all_data);
        llList = findViewById(R.id.layout_list);
        llAddItem = findViewById(R.id.ll_add_item);
        btnAddRow = findViewById(R.id.btn_add_list);
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
        fabActionGenerateQrCode = findViewById(R.id.fab_action_generate_qr_code);
        fabActionSaveToPdf = findViewById(R.id.fab_action_save_to_pdf);

        fabExpandMenu.setVisibility(View.GONE);

        spinnerPoCurrency.setText("IDR");

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

        databaseReference.child("CustomerData").orderByChild("custName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerCustUID = dataSnapshot.child("custUID").getValue(String.class);
                        String spinnerCustName = dataSnapshot.child("custName").getValue(String.class);
                        customerName.add(spinnerCustUID+" - "+spinnerCustName);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddReceivedOrder.this, R.layout.style_spinner, customerName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoCustName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        edtPoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPoDate.setError(null);
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(AddReceivedOrder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                edtPoDate.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                                poYear = year;
                                poMonth = month + 1;
                                poDay = dayOfMonth;
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        edtPoTOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(AddReceivedOrder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                edtPoTOP.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        btnAddRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView();
            }
        });

        spinnerPoTransportType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
                transportData = selectedSpinnerTransportType;
                spinnerPoTransportType.setError(null);
            }
        });

        spinnerPoTransportType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerPoTransportType.setText(transportData);
            }
        });

        spinnerPoCustName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerCustomerName = (String) adapterView.getItemAtPosition(position);
                customerData = selectedSpinnerCustomerName;
                spinnerPoCustName.setError(null);
                customerAlias = selectedSpinnerCustomerName.replaceAll("[^0-9]", "");

                /*databaseReference.child("CustomerData").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                String selectedSpinnerCustomerName = dataSnapshot.child("custName").getValue(String.class);
                                //customerName.add(spinnerMaterialData);
                            }
                        } else {
                            Toast.makeText(AddReceivedOrder.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("CustomerData/".concat(databaseReference.getKey())+selectedSpinnerCustomerName.replaceAll(" ", "").toLowerCase());
                databaseReference2.child(get).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String alias = snapshot.child("custUID").getValue(String.class);
                        customerAlias = alias;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

                randomString = getRandomString(5);
            }
        });

        spinnerPoCustName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerPoCustName.setText(customerData);
            }
        });

        spinnerPoCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCurrency = (String) adapterView.getItemAtPosition(i);
                currencyData = selectedCurrency;
                spinnerPoCurrency.setError(null);
            }
        });

        spinnerPoCurrency.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerPoCurrency.setText(currencyData);
            }
        });

        //TODO Make handler as onkeychangelistener
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (transportData.isEmpty()){
                    if (poMonth==0||poYear==0){
                        edtPoNumberPtbas.setText(customerAlias+"-"+transportData+"--");
                    } else {
                        edtPoNumberPtbas.setText(customerAlias+"-"+transportData+"-"+poMonth + "-" + poYear);
                    }
                } else {
                    if (poMonth==0||poYear==0){
                        edtPoNumberPtbas.setText(customerAlias+"-"+transportData.substring(0, 3) + "--");
                        if (!customerData.isEmpty() &&!Objects.requireNonNull(edtPoDate.getText()).toString().equals("")){
                            edtPoNumberPtbas.setText(customerAlias+"-"+randomString+"-"+transportData.substring(0, 3)+"--");
                        }
                    } else {
                        edtPoNumberPtbas.setText(customerAlias+"-"+transportData.substring(0, 3) + "-" + poMonth + "-" + poYear);
                        if (!customerData.isEmpty() &&!Objects.requireNonNull(edtPoDate.getText()).toString().equals("")){
                            edtPoNumberPtbas.setText(customerAlias+"-"+randomString+"-"+transportData.substring(0, 3)+"-"+poMonth + "-" + poYear);
                        }
                    }

                }
                handler.postDelayed(this, 500);
            }
        };

        runnable.run();
        addView();

        fabProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roCreatedBy = helper.getUserId();
                String roDateCreated = Objects.requireNonNull(edtPoDate.getText()).toString();
                String roTOP = "";
                String roMatType = Objects.requireNonNull(spinnerPoTransportType.getText()).toString();
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

                if (TextUtils.isEmpty(roMatType)) {
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

                if (!TextUtils.isEmpty(roDateCreated)&&!TextUtils.isEmpty(roMatType)&&!TextUtils.isEmpty(roCurrency)&&
                        !TextUtils.isEmpty(roCustName)){
                    insertData(roUID, roCreatedBy, roDateCreated, roTOP, roMatType, roCurrency, roPoCustNumber,
                            roCustName, false);
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
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void bottomSheetExpanded() {
        fabProceed.setVisibility(View.GONE);
        fabExpandMenu.setVisibility(View.VISIBLE);
        View viewLayout = AddReceivedOrder.this.getCurrentFocus();
        if (viewLayout != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
        }
    }

    private void bottomSheetCollapsed() {
        fabProceed.setVisibility(View.VISIBLE);
        fabExpandMenu.setVisibility(View.GONE);
        fabExpandMenu.collapse();
    }

    // Get passed data and save to cloud
    private void insertData(String roUID, String roCreatedBy, String roDateCreated,
                            String roTOP, String roMatType, String roCurrency,
                            String roPoCustNumber, String roCustName, Boolean roSatus) {

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

        ivCloseBottomSheetDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_HIDDEN);
                bottomSheetCollapsed();
            }
        });

        wrapTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_HIDDEN);
                bottomSheetCollapsed();
            }
        });

        if (checkIfValidAndProceed()) {
            tvPoCurrency.setText(roCurrency);
            tvPoPtBasNumber.setText(roUID);
            tvPoDate.setText(roDateCreated);
            tvPoTOP.setText(roTOP);
            tvPoTransportType.setText(roMatType);
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

                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_EXPANDED);
                RecyclerView rvItems = bottomSheet.findViewById(R.id.rvItems);
                rvItems.setHasFixedSize(true);
                rvItems.setLayoutManager(new LinearLayoutManager(this));

                previewProductItemAdapter = new PreviewProductItemAdapter(this, getList());
                rvItems.setAdapter(previewProductItemAdapter);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("ReceivedOrders" + "/" + roUID);
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ReceivedOrders" + "/" +
                        roUID + "/" + "OrderedItems");

                // Create PO object
                ReceivedOrderModel receivedOrderModel = new ReceivedOrderModel(
                        roUID, roCreatedBy, roDateCreated, roTOP, roMatType, roCurrency,
                        roPoCustNumber, roCustName, poSubTotalBuy, poSubTotalSell, poVAT,
                        poTotalSellFinal, poEstProfit, roSatus);

                fabActionSaveCloud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                            ref1.setValue(receivedOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        ref2.setValue(productItemsArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    dialogInterface.savedROInformation(AddReceivedOrder.this);
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    }
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

    private void setPreviewItems() {

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
            result = false;
            Toast.makeText(this, "Tambahkan item terlebih dahulu.", Toast.LENGTH_SHORT).show();
        } else if (!result){
            Toast.makeText(this, "Masukkan semua detail item dengan benar.", Toast.LENGTH_SHORT).show();
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
                                        Double quantity = Double.valueOf(edtPoQuantity.getText().toString());
                                        Double salePrice = Double.valueOf(edtSalePrice.getText().toString());
                                        Double buyPrice = Double.valueOf(edtBuyPrice.getText().toString());
                                        totalSellPrice = quantity*salePrice;
                                        totalBuyPrice = quantity*buyPrice;
                                    } else {
                                        totalSellPrice = 0;
                                        totalBuyPrice = 0;
                                    }

                                    edtBuyPrice.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                                            if (edtBuyPrice.getText().toString().equals("")){
                                                edtBuyPrice.setText("0");
                                            }
                                            return false;
                                        }
                                    });

                                    edtSalePrice.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                                            if (edtSalePrice.getText().toString().equals("")){
                                                edtSalePrice.setText("0");
                                            }
                                            return false;
                                        }
                                    });

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

        imgDeleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(materialView);
            }
        });

        llList.addView(materialView);
    }

    private void removeView(View v){
        llList.removeView(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        //dialogInterface.discardDialogConfirmation(AddReceivedOrder.this);
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