package com.ptbas.controlcenter.create;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.SupplierModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AddSupplierActivity extends AppCompatActivity {

    private static final CharSequence ALLOWED_CHARACTERS = "1234567890";
    TextInputEditText edtAccountOwnerName, edtAccountNumber, edtSupplierPhoneNumber, edtPayeeName, edtSupplierName;
    AutoCompleteTextView spinnerBankName;
    FloatingActionButton fabProceed;
    DialogInterface dialogInterface = new DialogInterface();

    String selectedBankName = "";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    List<String> bankList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference refSupplier = db.collection("SupplierData").document();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Supplier");
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

        edtAccountOwnerName = findViewById(R.id.edtAccountOwnerName);
        edtAccountNumber = findViewById(R.id.edtAccountNumber);
        edtSupplierPhoneNumber = findViewById(R.id.edtSupplierPhoneNumber);
        edtPayeeName = findViewById(R.id.edtPayeeName);
        edtSupplierName = findViewById(R.id.edtSupplierName);
        spinnerBankName = findViewById(R.id.spinnerBankName);

        fabProceed = findViewById(R.id.fabSaveData);

        edtAccountOwnerName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        edtPayeeName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        edtSupplierName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerBankName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        bankList = new ArrayList<>();

        databaseReference.child("BankData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerCorporateType1 = dataSnapshot.child("alias").getValue(String.class);
                        String spinnerCorporateType2 = dataSnapshot.child("name").getValue(String.class);
                        bankList.add(spinnerCorporateType1+" - "+spinnerCorporateType2);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddSupplierActivity.this, R.layout.style_spinner, bankList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerBankName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddSupplierActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinnerBankName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSpinnerBankName = (String) adapterView.getItemAtPosition(i);
                selectedBankName = selectedSpinnerBankName;
                spinnerBankName.setError(null);
            }
        });

        spinnerBankName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerBankName.setText(selectedBankName);
            }
        });

        fabProceed.setOnClickListener(view -> {
            String bankName = Objects.requireNonNull(spinnerBankName.getText()).toString();
            String bankAccountOwnerName =  Objects.requireNonNull(edtAccountOwnerName.getText()).toString();
            String bankAccountNumber = Objects.requireNonNull(edtAccountNumber.getText()).toString();
            String supplierPhoneNumber = Objects.requireNonNull(edtSupplierPhoneNumber.getText()).toString();
            String supplierPayeeName = Objects.requireNonNull(edtPayeeName.getText()).toString();
            String supplierName = Objects.requireNonNull(edtSupplierName.getText()).toString();

            if (TextUtils.isEmpty(bankName)){
                spinnerBankName.setError("");
                spinnerBankName.requestFocus();
            }

            if (TextUtils.isEmpty(bankAccountOwnerName)){
                edtAccountOwnerName.setError("");
                edtAccountOwnerName.requestFocus();
            }

            if (TextUtils.isEmpty(bankAccountNumber)){
                edtAccountNumber.setError("");
                edtAccountNumber.requestFocus();
            }

            if (TextUtils.isEmpty(supplierPhoneNumber)){
                supplierPhoneNumber = "";
            }

            if (TextUtils.isEmpty(supplierPayeeName)){
                edtPayeeName.setError("");
                edtPayeeName.requestFocus();
            }

            if (TextUtils.isEmpty(supplierName)){
                edtSupplierName.setError("");
                edtSupplierName.requestFocus();
            }


            if (!TextUtils.isEmpty(bankName)&&
                    !TextUtils.isEmpty(bankAccountOwnerName)&&
                    !TextUtils.isEmpty(bankAccountNumber)&&
                    !TextUtils.isEmpty(supplierPayeeName)&&
                    !TextUtils.isEmpty(supplierName)){
                insertData(bankName, bankAccountOwnerName, bankAccountNumber,
                        supplierPhoneNumber, supplierPayeeName, supplierName);
            }

        });
    }

    private void insertData(String bankName, String bankAccountOwnerName, String bankAccountNumber,
                            String supplierPhoneNumber, String supplierPayeeName, String supplierName) {

        String supplierDocumentID = refSupplier.getId();
        SupplierModel supplierModel = new SupplierModel(supplierDocumentID,
                bankName, bankAccountOwnerName, bankAccountNumber, supplierPhoneNumber,
                supplierPayeeName, supplierName, true);


        refSupplier.set(supplierModel)
                .addOnSuccessListener(unused -> {
                    dialogInterface.savedInformationFromManagement(AddSupplierActivity.this);
                }).addOnFailureListener(e ->
                        Toast.makeText(AddSupplierActivity.this, "FAILED", Toast.LENGTH_SHORT).show());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        //dialogInterface.discardDialogConfirmation(AddCustomerActivity.this);
    }
}