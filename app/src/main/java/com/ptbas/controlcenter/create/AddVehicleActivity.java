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
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.VehicleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class AddVehicleActivity extends AppCompatActivity {

    Helper helper = new Helper();
    private static final String TAG = "AddVehicle";

    private TextInputEditText edtVhlRegistNumber, edtVhlIdentityNumber, edtVhlEngineNumber,
            edtVhlManufactureYear, edtVhlLength, edtVhlWidth, edtVhlHeight;
    private AutoCompleteTextView spinnerVhlBrand;
    private RadioGroup radioGroupStatus;
    private RadioButton radioStatusSelected;
    FloatingActionButton fabSaveVhlData;

    String vhlBrand="";

    DatabaseReference dbRefVehicleBrand = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbRefAddVehicle = FirebaseDatabase.getInstance().getReference("VehicleData");
    List<String> vhlBrandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("Tambah Armada");
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

        edtVhlRegistNumber = findViewById(R.id.edt_vhl_regist_number);
        radioGroupStatus = findViewById(R.id.radio_group_status);
        edtVhlLength = findViewById(R.id.edt_vhl_length);
        edtVhlWidth = findViewById(R.id.edt_vhl_width);
        edtVhlHeight = findViewById(R.id.edt_vhl_height);
        spinnerVhlBrand = findViewById(R.id.spinner_vhl_brand);
        edtVhlIdentityNumber = findViewById(R.id.edt_vhl_identity_number);
        edtVhlEngineNumber = findViewById(R.id.edt_vhl_engine_number);
        edtVhlManufactureYear = findViewById(R.id.edt_vhl_manufacture_year);

        fabSaveVhlData = findViewById(R.id.fab_save_vhl_data);
        vhlBrandList  = new ArrayList<>();


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
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddVehicleActivity.this, R.layout.style_spinner, vhlBrandList);
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

        VehicleModel vehicleModel =
                new VehicleModel(vhlUID, vhlStatus, vhlLength, vhlWidth, vhlHeight,
                        vhlBrand, vhlEngineNumb, vhlIdentityNumber, vhlManufactureYear);

        dbRefAddVehicle.child(vhlUID).setValue(vehicleModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    infoSavedDialog();
                } else {
                    try{
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(AddVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void infoSavedDialog(){
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this)
                .setTitle("Sukses!", TextAlignment.START)
                .setAnimation(R.raw.lottie_saved)
                .setMessage("Berhasil menambahkan data. Mau tambah lagi?", TextAlignment.START)
                .setCancelable(false)
                .setPositiveButton("TAMBAH LAGI", R.drawable.ic_outline_add, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        startActivity(getIntent());
                        finish();
                        overridePendingTransition(0, 0);
                    }
                })
                .setNegativeButton("TIDAK", R.drawable.ic_outline_close, new BottomSheetMaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        finish();
                        helper.refreshDashboard(AddVehicleActivity.this);
                    }

                })
                .build();

        // Show Dialog
        mBottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        helper.refreshDashboard(this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        //helper.refreshDashboard(this);
        return super.onOptionsItemSelected(item);
    }
}