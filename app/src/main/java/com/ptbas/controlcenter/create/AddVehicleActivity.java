package com.ptbas.controlcenter.create;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.VehicleModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddVehicleActivity extends AppCompatActivity {

    private static final String TAG = "AddVehicle";
    private TextInputEditText edtVhlRegistNumber, edtVhlIdentityNumber, edtVhlEngineNumber,
            edtVhlManufactureYear, edtVhlLength, edtVhlWidth, edtVhlHeight;
    private AutoCompleteTextView spinnerVhlBrand;
    private RadioGroup radioGroupStatus;
    private RadioButton radioStatusSelected;
    private FloatingActionButton fabSaveVhlData;

    String vhlBrand="", vhlIdentityNumber="", vhlEngineNumber="", vhlManufactureYear="";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    List<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        edtVhlRegistNumber = findViewById(R.id.edt_vhl_regist_number);
        edtVhlIdentityNumber = findViewById(R.id.edt_vhl_identity_number);
        edtVhlEngineNumber = findViewById(R.id.edt_vhl_engine_number);
        edtVhlManufactureYear = findViewById(R.id.edt_vhl_manufacture_year);
        edtVhlLength = findViewById(R.id.edt_vhl_length);
        edtVhlWidth = findViewById(R.id.edt_vhl_width);
        edtVhlHeight = findViewById(R.id.edt_vhl_height);
        fabSaveVhlData = findViewById(R.id.fab_save_vhl_data);

        spinnerVhlBrand = findViewById(R.id.spinner_vhl_brand);
        radioGroupStatus = findViewById(R.id.radio_group_status);

        names  = new ArrayList<>();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Armada");
        // showing the back button in action bar
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

        spinnerVhlBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpinnerVhlBrandItem = (String) parent.getItemAtPosition(position);
                vhlBrand = selectedSpinnerVhlBrandItem;
                spinnerVhlBrand.setError(null);
            }
        });

        databaseReference.child("VehicleBrand").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerName = dataSnapshot.child("name").getValue(String.class);
                        names.add(spinnerName);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddVehicleActivity.this, R.layout.style_spinner, names);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerVhlBrand.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddVehicleActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fabSaveVhlData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedStatusId = radioGroupStatus.getCheckedRadioButtonId();
                radioStatusSelected = findViewById(selectedStatusId);

                String vhlRegistNumber = edtVhlRegistNumber.getText().toString();
                String vhlIdentityNumber = Objects.requireNonNull(edtVhlIdentityNumber.getText()).toString();
                String vhlEngineNumber = Objects.requireNonNull(edtVhlEngineNumber.getText()).toString();
                String vhlManufactureYear = Objects.requireNonNull(edtVhlManufactureYear.getText()).toString();
                String vhlLength = edtVhlLength.getText().toString();
                String vhlWidth = edtVhlWidth.getText().toString();
                String vhlHeight = edtVhlHeight.getText().toString();

                String finalVhlBrand = vhlBrand;
                String vhlStatus = "";
                String dateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String dateModified = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                if (TextUtils.isEmpty(vhlRegistNumber)){
                    edtVhlRegistNumber.setError("Mohon masukkan nomor TNKB dengan benar");
                    edtVhlRegistNumber.requestFocus();
                } else if (TextUtils.isEmpty(vhlLength)){
                    edtVhlLength.setError("Mohon masukkan panjang kendaraan dengan benar");
                    edtVhlLength.requestFocus();
                } else if (TextUtils.isEmpty(vhlWidth)){
                    edtVhlWidth.setError("Mohon masukkan lebar kendaraan dengan benar");
                    edtVhlWidth.requestFocus();
                } else if (TextUtils.isEmpty(vhlHeight)){
                    edtVhlHeight.setError("Mohon masukkan tinggi kendaraan dengan benar");
                    edtVhlHeight.requestFocus();
                } else if (edtVhlIdentityNumber.getText() == null){
                    vhlIdentityNumber.equals("");
                } else if (edtVhlEngineNumber.getText() == null){
                    vhlEngineNumber.equals("");
                } else if (edtVhlManufactureYear.getText() == null){
                    vhlManufactureYear.equals("");
                } else{
                    vhlStatus = radioStatusSelected.getText().toString();
                    insertData(vhlRegistNumber, finalVhlBrand, vhlIdentityNumber, vhlEngineNumber, dateModified, dateCreated, vhlManufactureYear, Integer.parseInt(vhlLength), Integer.parseInt(vhlWidth), Integer.parseInt(vhlHeight), vhlStatus);
                }
            }
        });
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

    private void insertData(String vhlRegistNumber, String finalVhlBrand, String vhlIdentityNumber, String vhlEngineNumber, String dateCreated, String dateModified, String vhlManufactureYear, Integer vhlLength, Integer vhlWidth, Integer vhlHeight, String vhlStatus){
        VehicleModel vehicleModel = new VehicleModel(vhlRegistNumber, finalVhlBrand, vhlIdentityNumber, vhlEngineNumber, dateCreated, dateModified, vhlManufactureYear, vhlLength, vhlWidth, vhlHeight, vhlStatus);
        DatabaseReference referenceVehicle = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("VehicleData");
        referenceVehicle.child(vhlRegistNumber).setValue(vehicleModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddVehicleActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try{
                        throw task.getException();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(AddVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}