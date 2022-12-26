package com.ptbas.controlcenter.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.VhlModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdtVhlActivity extends AppCompatActivity {

    HelperUtils helperUtils = new HelperUtils();
    private static final String TAG = "AddVehicle";

    private TextInputEditText edtVhlRegistNumber, edtVhlIdentityNumber, edtVhlEngineNumber,
            edtVhlManufactureYear, edtVhlLength, edtVhlWidth, edtVhlHeight;
    private AutoCompleteTextView spinnerVhlBrand;
    private RadioGroup radioGroupStatus;
    private RadioButton radioStatusSelected;
    FloatingActionButton fabSaveVhlData;

    String vhlBrand="";
    String vhlUID="";

    DatabaseReference dbRefVehicleBrand = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbRefAddVehicle = FirebaseDatabase.getInstance().getReference("VehicleData");
    List<String> vhlBrandList;

    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upd_vhl);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Perbarui Data Armada");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);


        edtVhlRegistNumber = findViewById(R.id.edt_vhl_regist_number);
        radioGroupStatus = findViewById(R.id.radio_group_status);
        edtVhlLength = findViewById(R.id.edt_vhl_length);
        edtVhlWidth = findViewById(R.id.edt_vhl_width);
        edtVhlHeight = findViewById(R.id.edt_vhl_height);
        spinnerVhlBrand = findViewById(R.id.spinner_vhl_brand);
        edtVhlIdentityNumber = findViewById(R.id.edt_vhl_identity_number);
        edtVhlEngineNumber = findViewById(R.id.edt_vhl_engine_number);
        edtVhlManufactureYear = findViewById(R.id.edt_vhl_manufacture_year);

        edtVhlRegistNumber.setEnabled(false);

        fabSaveVhlData = findViewById(R.id.fab_save_vhl_data);
        vhlBrandList  = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            vhlUID = extras.getString("key");
            dbRefAddVehicle.child(vhlUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    VhlModel vhlModel = snapshot.getValue(VhlModel.class);

                    if (vhlModel == null){
                        finish();
                    } else {
                        edtVhlRegistNumber.setText(vhlModel.getVhlUID());
                        edtVhlLength.setText(String.valueOf(vhlModel.getVhlLength()));
                        edtVhlWidth.setText(String.valueOf(vhlModel.getVhlWidth()));
                        edtVhlHeight.setText(String.valueOf(vhlModel.getVhlHeight()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        //Set on itemclick for vhlBrand spinner
        spinnerVhlBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vhlBrand = (String) parent.getItemAtPosition(position);
                spinnerVhlBrand.setError(null);
            }
        });

        //Show vehicle brands list
        dbRefVehicleBrand.child("VehicleBrand").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerName = dataSnapshot.child("vhlBrandUID").getValue(String.class);
                        vhlBrandList.add(spinnerName);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdtVhlActivity.this, R.layout.style_spinner, vhlBrandList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerVhlBrand.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(UpdtVhlActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        //Handle fabSaveData when onClick
        fabSaveVhlData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //MANDATORY
                String vhlUID = Objects.requireNonNull(edtVhlRegistNumber.getText()).toString();
                int selectedStatusId = radioGroupStatus.getCheckedRadioButtonId();
                radioStatusSelected = findViewById(selectedStatusId);
                boolean vhlStatus;
                String vhlLength = Objects.requireNonNull(edtVhlLength.getText()).toString();
                String vhlWidth = Objects.requireNonNull(edtVhlWidth.getText()).toString();
                String vhlHeight = Objects.requireNonNull(edtVhlHeight.getText()).toString();

                //OPTIONAL
                String vhlBrand = spinnerVhlBrand.getText().toString();
                String vhlEngineNumber = Objects.requireNonNull(edtVhlEngineNumber.getText()).toString();
                String vhlIdentityNumber = Objects.requireNonNull(edtVhlIdentityNumber.getText()).toString();
                String vhlManufactureYear = Objects.requireNonNull(edtVhlManufactureYear.getText()).toString();

                //Validations
                if (TextUtils.isEmpty(vhlUID)){
                    edtVhlRegistNumber.setError("Mohon masukkan nomor TNKB dengan benar");
                    edtVhlRegistNumber.requestFocus();
                }

                if (radioStatusSelected.getText().toString().equals("Aktif")){
                    vhlStatus = true;
                } else {
                    vhlStatus = false;
                }

                if (TextUtils.isEmpty(vhlLength)){
                    edtVhlLength.setError("Mohon masukkan panjang kendaraan dengan benar");
                    edtVhlLength.requestFocus();
                }

                if (TextUtils.isEmpty(vhlWidth)){
                    edtVhlWidth.setError("Mohon masukkan lebar kendaraan dengan benar");
                    edtVhlWidth.requestFocus();
                }

                if (TextUtils.isEmpty(vhlHeight)){
                    edtVhlHeight.setError("Mohon masukkan tinggi kendaraan dengan benar");
                    edtVhlHeight.requestFocus();
                }

                if (spinnerVhlBrand.getText() == null){
                    vhlBrand = "";
                }

                if (edtVhlEngineNumber.getText() == null){
                    vhlEngineNumber = "";
                }

                if (edtVhlIdentityNumber.getText() == null){
                    vhlIdentityNumber = "";
                }

                if (edtVhlManufactureYear.getText() == null){
                    vhlManufactureYear = "";
                }

                if (!TextUtils.isEmpty(vhlUID)&&!TextUtils.isEmpty(vhlWidth)
                        &&!TextUtils.isEmpty(vhlLength)&&!TextUtils.isEmpty(vhlHeight)){

                    insertData(vhlUID, vhlStatus, Integer.parseInt(vhlLength),
                            Integer.parseInt(vhlWidth), Integer.parseInt(vhlHeight),
                            vhlBrand, vhlEngineNumber, vhlIdentityNumber, vhlManufactureYear);

                }
            }
        });
    }

    private void insertData(String vhlUID, Boolean vhlStatus, Integer vhlLength, Integer vhlWidth,
                            Integer vhlHeight, String vhlBrand, String vhlEngineNumb,
                            String vhlIdentityNumber, String vhlManufactureYear){

        VhlModel vhlModel =
                new VhlModel(vhlUID, vhlStatus, vhlLength, vhlWidth, vhlHeight,
                        vhlBrand, vhlEngineNumb, vhlIdentityNumber, vhlManufactureYear);

        dbRefAddVehicle.child(vhlUID).setValue(vhlModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialogInterfaceUtils.updatedInformation(UpdtVhlActivity.this);
                } else {
                    try{
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(UpdtVhlActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        finish();
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
}