package com.ptbas.controlcenter.create;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.ptbas.controlcenter.model.BankAccountModel;
import com.ptbas.controlcenter.model.SupplierModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddBankAccountActivity extends AppCompatActivity {

    private static final CharSequence ALLOWED_CHARACTERS = "1234567890";
    TextInputEditText edtAccountOwnerName, edtAccountNumber;
    AutoCompleteTextView spinnerBankName, spinnerBankType;
    FloatingActionButton fabProceed;
    DialogInterface dialogInterface = new DialogInterface();

    String selectedBankName = "";
    String selectedBankType = "";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    List<String> bankList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference refBankData = db.collection("BankAccountData").document();


    String[] bankTypeStr = {"PERUSAHAAN", "CUSTOMER", "SUPPLIER"};
    Integer[] bankTypeVal = {0, 1, 2};
    Integer bankType;
    ArrayList<String> arrayListBankType = new ArrayList<>(Arrays.asList(bankTypeStr));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Rekening Bank");
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
        spinnerBankName = findViewById(R.id.spinnerBankName);
        spinnerBankType = findViewById(R.id.spinnerBankType);

        fabProceed = findViewById(R.id.fabSaveData);

        edtAccountOwnerName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerBankName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerBankType.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        ArrayAdapter<String> arrayAdapterBankType = new ArrayAdapter<>(this, R.layout.style_spinner, arrayListBankType);

        spinnerBankType.setAdapter(arrayAdapterBankType);

        spinnerBankType.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedSpinnerBankName = (String) adapterView.getItemAtPosition(i);
            selectedBankName = selectedSpinnerBankName;
            spinnerBankName.setError(null);
            switch (i){
                case 0:
                    bankType = bankTypeVal[0];
                    break;
                case 1:
                    bankType = bankTypeVal[1];
                    break;
                case 2:
                    bankType = bankTypeVal[2];
                    break;
                default:
                    break;
            }
        });

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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddBankAccountActivity.this, R.layout.style_spinner, bankList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerBankName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddBankAccountActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
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

        spinnerBankName.setOnFocusChangeListener((view, b) -> spinnerBankName.setText(selectedBankName));

        spinnerBankType.setOnFocusChangeListener((view, b) -> spinnerBankType.setText(selectedBankType));

        fabProceed.setOnClickListener(view -> {
            String bankName = Objects.requireNonNull(spinnerBankName.getText()).toString();
            String bankType = Objects.requireNonNull(spinnerBankType.getText()).toString();
            String bankAccountOwnerName =  Objects.requireNonNull(edtAccountOwnerName.getText()).toString();
            String bankAccountNumber = Objects.requireNonNull(edtAccountNumber.getText()).toString();

            if (TextUtils.isEmpty(bankName)){
                spinnerBankName.setError("");
                spinnerBankName.requestFocus();
            }

            if (TextUtils.isEmpty(bankType)){
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

            if (!TextUtils.isEmpty(bankName)&&!TextUtils.isEmpty(bankType)&&
                    !TextUtils.isEmpty(bankAccountOwnerName)&&
                    !TextUtils.isEmpty(bankAccountNumber)){
                insertData(bankName, bankType, bankAccountOwnerName, bankAccountNumber);
            }

        });
    }

    private void insertData(String bankName, String bankType, String bankAccountOwnerName, String bankAccountNumber) {

        String bankAccountDocumentID = refBankData.getId();
        BankAccountModel bankAccountModel = new BankAccountModel(bankAccountDocumentID,
                bankName, bankType, bankAccountOwnerName, bankAccountNumber, true);


        refBankData.set(bankAccountModel)
                .addOnSuccessListener(unused -> {
                    dialogInterface.savedInformationFromManagement(AddBankAccountActivity.this);
                }).addOnFailureListener(e ->
                        Toast.makeText(AddBankAccountActivity.this, "FAILED", Toast.LENGTH_SHORT).show());

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