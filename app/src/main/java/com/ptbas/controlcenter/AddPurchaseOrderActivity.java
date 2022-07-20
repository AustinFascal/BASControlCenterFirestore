package com.ptbas.controlcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AddPurchaseOrderActivity extends AppCompatActivity {

    LinearLayout llList;
    Button btnAddRow, btnGeneratePoNumber;
    TextInputEditText edtPoDate, edtPoNumberPtbas;

    List<String> productName, transportTypeName, customerName;

    String vhlData = "", materialData = "", transportData = "", customerData = "", customerAlias="";
    Integer poYear = 0, poMonth = 0, poDay = 0;

    private DatePickerDialog datePicker;

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase_order);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        transportTypeName  = new ArrayList<>();
        customerName  = new ArrayList<>();

        AutoCompleteTextView spinnerTransportType = findViewById(R.id.spinner_po_transport_type);
        AutoCompleteTextView spinnerCustomerName = findViewById(R.id.spinner_po_cust_name);

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
                    spinnerTransportType.setAdapter(arrayAdapter);
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
                    spinnerCustomerName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddPurchaseOrderActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Buat Purchase Order");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.white)));

        llList = findViewById(R.id.layout_list);
        btnAddRow = findViewById(R.id.btn_add_list);
        btnGeneratePoNumber = findViewById(R.id.btn_generate_po_number);
        edtPoNumberPtbas = findViewById(R.id.edt_po_number_ptbas);
        edtPoDate = findViewById(R.id.edt_po_date);

        edtPoDate.setOnClickListener(new View.OnClickListener() {
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
                                edtPoDate.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                                poYear = year;
                                poMonth = month + 1;
                                poDay = dayOfMonth;
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

        spinnerTransportType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
                transportData = selectedSpinnerTransportType;
                spinnerTransportType.setError(null);
            }
        });

        spinnerTransportType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerTransportType.setText(transportData);
            }
        });

        spinnerCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
                customerData = selectedSpinnerTransportType;
                spinnerCustomerName.setError(null);
            }
        });

        spinnerCustomerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerCustomerName.setText(customerData);
            }
        });

        databaseReference.child("CustomerData").child(customerData).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String alias = dataSnapshot.child("alias").getValue(String.class);
                        customerAlias = alias;
                    }
                } else {
                    Toast.makeText(AddPurchaseOrderActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnGeneratePoNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPoNumberPtbas.setText(transportData.substring(0,3)+"-"+customerAlias+"-"+poYear+"-"+poMonth+"-"+getRandomString(5));
            }
        });

        addView();
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
        View materialView = getLayoutInflater().inflate(R.layout.row_add_material, null, false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        productName  = new ArrayList<>();

        AutoCompleteTextView spinnerMaterialName = materialView.findViewById(R.id.spinner_po_material_name);
        TextInputEditText edtSalePrice = materialView.findViewById(R.id.edt_po_sale_price);
        TextInputEditText edtBuyPrice = materialView.findViewById(R.id.edt_po_buy_price);
        ImageView imgDeleteRow = materialView.findViewById(R.id.img_remove_row);

        databaseReference.child("ProductData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("name").getValue(String.class);
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