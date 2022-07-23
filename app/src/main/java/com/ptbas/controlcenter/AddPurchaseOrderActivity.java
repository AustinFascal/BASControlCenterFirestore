package com.ptbas.controlcenter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
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
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ProductModel;
import com.ptbas.controlcenter.model.PurchaseOrder;
import com.ptbas.controlcenter.model.VehicleModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AddPurchaseOrderActivity extends AppCompatActivity {

    LinearLayout llList,llAddItem, llInputAllData;
    Button btnAddRow;
    TextInputEditText edtPoDate, edtPoTOP, edtPoNumberCustomer, edtPoNumberPtbas;
    TextInputLayout wrapEdtPoNumberPtBas;
    AutoCompleteTextView spinnerPoTransportType, spinnerPoCustName;
    List<String> productName, transportTypeName, customerName;
    String transportData = "", customerData = "", customerAlias="", randomString="NULL";
    Integer poYear = 0, poMonth = 0, poDay = 0;

    TextView tvSumTotal;

    float totalSellPrice = 0, totalBuyPrice = 0, sumTotalSellPrice = 0;

    private DatePickerDialog datePicker;

    FloatingActionButton fabProceed;

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    ArrayList<ProductItems> productItemsArrayList = new ArrayList<>();

    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ConstraintLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase_order);

        bottomSheet = findViewById(R.id.bottomSheetPODetails);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        transportTypeName  = new ArrayList<>();
        customerName  = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Buat Purchase Order (PO)");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.white)));

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
        fabProceed = findViewById(R.id.fab_save_po_data);

        tvSumTotal = findViewById(R.id.tv_sum_total);

        databaseReference.child("TransportTypeData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("name").getValue(String.class);
                        transportTypeName.add(spinnerMaterialData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddPurchaseOrderActivity.this, R.layout.style_spinner, transportTypeName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoTransportType.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddPurchaseOrderActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("CustomerData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("name").getValue(String.class);
                        customerName.add(spinnerMaterialData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddPurchaseOrderActivity.this, R.layout.style_spinner, customerName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoCustName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddPurchaseOrderActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
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

                datePicker = new DatePickerDialog(AddPurchaseOrderActivity.this,
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

                datePicker = new DatePickerDialog(AddPurchaseOrderActivity.this,
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

                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("CustomerData/"+selectedSpinnerCustomerName.replaceAll(" ", "").toLowerCase());
                databaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String alias = snapshot.child("alias").getValue(String.class);
                        customerAlias = alias;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                randomString = getRandomString(5);
            }
        });

        spinnerPoCustName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerPoCustName.setText(customerData);
            }
        });


        //TODO Make handler as onkeychangelistener
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                if (transportData.isEmpty()){
                    edtPoNumberPtbas.setText(transportData+"-"+customerAlias+"-"+poMonth + "-" + poYear);
                } else {
                    edtPoNumberPtbas.setText(transportData.substring(0, 3) + "-" +customerAlias+"-"+ poMonth + "-" + poYear);
                    if (!Objects.requireNonNull(edtPoNumberCustomer.getText()).toString().equals("")
                            &&!customerData.isEmpty() &&!Objects.requireNonNull(edtPoDate.getText()).toString().equals("")){
                        edtPoNumberPtbas.setText(randomString+"-"+transportData.substring(0, 3)+"-"+customerAlias+"-"+poMonth + "-" + poYear);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();
        addView();


        fabProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(bottomSheetBehavior.STATE_HIDDEN);
                    bottomSheetCollapsed();
                } else{
                    if (checkIfValidAndProceed()){
                        bottomSheetBehavior.setState(bottomSheetBehavior.STATE_EXPANDED);
                        bottomSheetExpanded();
                    }

                }
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (checkIfValidAndProceed()) {
                            String poUID = Objects.requireNonNull(edtPoNumberPtbas.getText()).toString();
                            String poDateCreated = Objects.requireNonNull(edtPoDate.getText()).toString();
                            String poInputDateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                            String poTOP = "";
                            String poTransportType = Objects.requireNonNull(spinnerPoTransportType.getText()).toString();
                            String poCustomerName = Objects.requireNonNull(spinnerPoCustName.getText()).toString();
                            String poNumberCustomer = Objects.requireNonNull(edtPoNumberCustomer.getText()).toString();

                            if (TextUtils.isEmpty(poDateCreated)) {
                                edtPoDate.setError("Mohon masukkan tanggal order");
                                edtPoDate.requestFocus();
                            } else if (TextUtils.isEmpty(poTransportType)) {
                                spinnerPoTransportType.setError("Mohon masukkan jenis transport");
                                spinnerPoTransportType.requestFocus();
                            } else if (TextUtils.isEmpty(poCustomerName)) {
                                spinnerPoCustName.setError("Mohon masukkan nama customer");
                                spinnerPoCustName.requestFocus();
                            } else if (TextUtils.isEmpty(poNumberCustomer)) {
                                edtPoNumberCustomer.setError("Mohon masukkan nomor PO customer");
                                edtPoNumberCustomer.requestFocus();
                            } else {
                                if (TextUtils.isEmpty(poTOP)) {
                                    poTOP = "-";
                                } else {
                                    poTOP = Objects.requireNonNull(edtPoTOP.getText()).toString();
                                }
                                insertData(poUID, poDateCreated, poInputDateCreated, poTOP, poTransportType, poCustomerName, poNumberCustomer);
                            }

                            bottomSheetExpanded();
                        }

                        Toast.makeText(AddPurchaseOrderActivity.this, "EXPANDED", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomSheetCollapsed();
                        Toast.makeText(AddPurchaseOrderActivity.this, "COLLAPSED", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottomSheetCollapsed();
                        break;
                    /*case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;*/
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    private void bottomSheetExpanded() {
        fabProceed.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_save));
        fabProceed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_700)));
        View viewLayout = AddPurchaseOrderActivity.this.getCurrentFocus();
        if (viewLayout != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
        }
        llInputAllData.setVisibility(View.INVISIBLE);
    }

    private void bottomSheetCollapsed() {
        fabProceed.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_check));
        fabProceed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pure_orange)));
        llInputAllData.setVisibility(View.VISIBLE);
    }


    private void insertData(String poUID, String poDateCreated, String poInputDateCreated, String poTOP, String poTransportType, String poCustomerName, String poNumberCustomer) {
        PurchaseOrder purchaseOrder = new PurchaseOrder(poUID, poDateCreated, poInputDateCreated, poTOP, poTransportType,
                poCustomerName, poNumberCustomer);

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("PurchaseOrders" + "/" + poUID);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("PurchaseOrders" + "/" +
                poUID + "/" + "OrderedItems");

        TextView tvPoPtBasNumber = bottomSheet.findViewById(R.id.tvPoPtBasNumber);
        TextView tvPoDate = bottomSheet.findViewById(R.id.tvPoDate);
        TextView tvPoTOP = bottomSheet.findViewById(R.id.tvPoTOP);
        TextView tvPoTransportType = bottomSheet.findViewById(R.id.tvPoTransportType);
        TextView tvPoCustomerName = bottomSheet.findViewById(R.id.tvPoCustomerName);
        TextView tvPoCustomerNumber = bottomSheet.findViewById(R.id.tvPoCustomerNumber);

        tvPoPtBasNumber.setText(poUID);
        tvPoDate.setText(poDateCreated);
        tvPoTOP.setText(poTOP);
        tvPoTransportType.setText(poTransportType);
        tvPoCustomerName.setText(poCustomerName);
        tvPoCustomerNumber.setText(poNumberCustomer);


        /*ref1.setValue(purchaseOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                try{
                                    float sum = 0;
                                    for(ProductItems productItems : productItemsArrayList) {
                                        sum += Float.parseFloat(String.valueOf(productItems.productTotalBuyPrice));
                                    }
                                    Toast.makeText(AddPurchaseOrderActivity.this, String.valueOf(Float.parseFloat(String.valueOf(sum))), Toast.LENGTH_SHORT).show();

                                    Toast.makeText(AddPurchaseOrderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(AddPurchaseOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }
            }
        });*/
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
                productItems.setProductName(spinnerMaterialName.getText().toString());
            } else{
                result = false;
                break;
            }

            if (!edtPoQuantity.getText().toString().equals("")){
                productItems.setProductQuantity(Integer.parseInt(edtPoQuantity.getText().toString()));
            }else{
                result = false;
                break;
            }

            if (!edtPoQuantity.getText().toString().equals("0")){
                productItems.setProductQuantity(Integer.parseInt(edtPoQuantity.getText().toString()));
            }else{
                result = false;
                break;
            }

            if (!edtSalePrice.getText().toString().equals("")){
                productItems.setProductSellPrice(Float.parseFloat(edtSalePrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            if (!edtBuyPrice.getText().toString().equals("")){
                productItems.setProductBuyPrice(Float.parseFloat(edtBuyPrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            if (!edtPoTotalSellPrice.getText().toString().equals("")){
                productItems.setProductTotalSellPrice(Float.parseFloat(edtPoTotalSellPrice.getText().toString()));
            } else{
                result = false;
                break;
            }

            if (!edtPoTotalBuyPrice.getText().toString().equals("")){
                productItems.setProductTotalBuyPrice(Float.parseFloat(edtPoTotalBuyPrice.getText().toString()));
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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddPurchaseOrderActivity.this, R.layout.style_spinner, productName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerMaterialName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddPurchaseOrderActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
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
                            edtBuyPrice.setText(df.format(Float.parseFloat(String.valueOf(productModel.getPriceBuy()))));
                            edtSalePrice.setText(df.format(Float.parseFloat(String.valueOf(productModel.getPriceSell()))));

                            final Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    if(!edtPoQuantity.getText().toString().equals("")){
                                        totalSellPrice = Float.parseFloat(edtPoQuantity.getText().toString())*Float.parseFloat(edtSalePrice.getText().toString());
                                        totalBuyPrice = Float.parseFloat(edtPoQuantity.getText().toString())*Float.parseFloat(edtBuyPrice.getText().toString());
                                    } else {
                                        totalSellPrice = 0;
                                        totalBuyPrice = 0;
                                    }

                                    edtBuyPrice.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                                            if (edtBuyPrice.getText().toString().equals("")){
                                                edtBuyPrice.setText("0.00");
                                            }
                                            return false;
                                        }
                                    });

                                    edtSalePrice.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                                            if (edtSalePrice.getText().toString().equals("")){
                                                edtSalePrice.setText("0.00");
                                            }
                                            return false;
                                        }
                                    });

                                    edtPoTotalBuyPrice.setText(df.format(totalBuyPrice));
                                    edtPoTotalSellPrice.setText(df.format(totalSellPrice));
                                    handler.postDelayed(this, 1000);

                                }
                            };

                            runnable.run();
                            /*sumTotalSellPrice = sumTotalSellPrice+totalSellPrice;

                            tvSumTotal.setText(String.valueOf(sumTotalSellPrice));*/

                            // TODO: 21/07/2022 MAKE SUM TOTAL

                        } else {
                            Toast.makeText(AddPurchaseOrderActivity.this, "Null", Toast.LENGTH_SHORT).show();
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
        finish();
        return super.onOptionsItemSelected(item);
    }
}