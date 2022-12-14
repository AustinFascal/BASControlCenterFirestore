package com.ptbas.controlcenter.create;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.CustModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class AddCustActivity extends AppCompatActivity {

    private static final CharSequence ALLOWED_CHARACTERS = "1234567890";
    TextInputEditText edtCustomerName, edtCustomerAlias, edtCustomerAddress, edtCustomerNpwp, edtCustomerPhone;
    AutoCompleteTextView spinnerCustType;
    FloatingActionButton fabProceed;
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    String selectedCustType = "";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    List<String> corporateTypeList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference refCust = db.collection("CustomerData").document();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cust);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Customer");
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

        edtCustomerName = findViewById(R.id.edt_customer_name);
        edtCustomerAlias = findViewById(R.id.edt_customer_alias);
        edtCustomerAddress = findViewById(R.id.edt_customer_address);
        edtCustomerNpwp = findViewById(R.id.edt_customer_npwp);
        edtCustomerPhone = findViewById(R.id.edt_customer_phone);
        spinnerCustType = findViewById(R.id.spinner_cust_type);
        fabProceed = findViewById(R.id.fabSaveData);

        edtCustomerName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        corporateTypeList  = new ArrayList<>();

        databaseReference.child("CorporateType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerCorporateType1 = dataSnapshot.child("alias").getValue(String.class);
                        String spinnerCorporateType2 = dataSnapshot.child("name").getValue(String.class);
                        corporateTypeList.add(spinnerCorporateType1+"-"+spinnerCorporateType2);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCustActivity.this, R.layout.style_spinner, corporateTypeList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerCustType.setAdapter(arrayAdapter);
                } else {
                    //Toast.makeText(AddCustomerActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinnerCustType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSpinnerCustType = (String) adapterView.getItemAtPosition(i);
                selectedCustType = selectedSpinnerCustType;
                spinnerCustType.setError(null);
            }
        });

        spinnerCustType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerCustType.setText(selectedCustType);
            }
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                String str = Objects.requireNonNull(edtCustomerName.getText()).toString();
                String result = str.replaceAll("\\B.|\\P{L}", "").toUpperCase();
                edtCustomerAlias.setText(result);
                handler.postDelayed(this, 100);
            }
        };
        runnable.run();

        fabProceed.setOnClickListener(view -> {
            String custType = "";
            String custName = Objects.requireNonNull(edtCustomerName.getText()).toString();
            String custAlias = Objects.requireNonNull(edtCustomerAlias.getText()).toString();
            String custAddress = Objects.requireNonNull(edtCustomerAddress.getText()).toString();
            String custNPWP = Objects.requireNonNull(edtCustomerNpwp.getText()).toString();
            String custPhone = "-";

            if (spinnerCustType.getText().toString().equals("")) {
                spinnerCustType.setError("Mohon masukkan nama customer");
                spinnerCustType.requestFocus();
            } else{
                custType = selectedCustType.substring(0, 2);
            }

            if (TextUtils.isEmpty(custType)) {
                spinnerCustType.setError("Mohon masukkan nama customer");
                spinnerCustType.requestFocus();
            }

            if (TextUtils.isEmpty(custName)) {
                edtCustomerName.setError("Mohon masukkan nama customer");
                edtCustomerName.requestFocus();
            }

            if (TextUtils.isEmpty(custAddress)) {
                edtCustomerAddress.setError("Mohon masukkan alamat customer");
                edtCustomerAddress.requestFocus();
            }

            /*if (TextUtils.isEmpty(custNPWP)) {
                edtCustomerNpwp.setError("Mohon masukkan NPWP customer");
                edtCustomerNpwp.requestFocus();
            }*/

            if (TextUtils.isEmpty(custPhone)){
                custPhone = "-";
            } else{
                custPhone = Objects.requireNonNull(edtCustomerPhone.getText()).toString();
            }

            if (!TextUtils.isEmpty(custType)&&!TextUtils.isEmpty(custName)&&!TextUtils.isEmpty(custAddress)){
                insertData(custType, custName, custAlias, custAddress, custNPWP, custPhone);
            }

        });
    }

    private void insertData(String custType, String custName, String custAlias, String custAddress, String custNPWP, String custPhone) {
        /*String custUID = custAlias + " " + getRandomUID(3);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerData");
        CustomerModel customerModel = new CustomerModel(custUID, custName+", "+custType, custAlias, custAddress, custNPWP, custPhone);

        ref.push().setValue(customerModel).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                dialogInterface.savedInformation(AddCustomerActivity.this);
            } else {
                Toast.makeText(AddCustomerActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
            }
        });*/
        //String custDocumentID = refCust.getId();
        String custDocumentID = refCust.getId();
        String custAliasVal = custAlias + " " + getRandomUID(3);
        CustModel custModel = new CustModel(custDocumentID, custAliasVal, custName+", "+custType, custAddress, custNPWP, custPhone, true);


        refCust.set(custModel)
                .addOnSuccessListener(unused -> {
                    Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }

                    MaterialDialog mBottomSheetDialog = new MaterialDialog.Builder(this)
                            .setTitle("Sukses!")
                            .setAnimation(R.raw.lottie_success_2)
                            .setMessage("Berhasil menambahkan data. Mau menambah data lagi?")
                            .setCancelable(false)
                            .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, (dialogInterface, which) -> {
                                Intent intent = new Intent(this, AddCustActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                this.startActivity(intent);
                            })
                            .setNegativeButton("SELESAI", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                                finish();
                            })
                            .build();

                    mBottomSheetDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    mBottomSheetDialog.show();
                }).addOnFailureListener(e ->
                        Toast.makeText(AddCustActivity.this, "FAILED", Toast.LENGTH_SHORT).show());

    }

    private static String getRandomUID(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
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