package com.ptbas.controlcenter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.VehicleModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AddGoodIssueActivity extends AppCompatActivity {

    private TextView tvGiVhlLengthVal, tvGiVhlWidthVal, tvGiVhlHeightVal,
            tvGiVhlFinalVolume, tvGiVhlHeightCorrection;
    private AutoCompleteTextView spinnerGiVhlRegistNumber, spinnerGiProductName, spinnerGiTransportType,
            edtGiPoNumberBas;
    String vhlData = "", materialData = "", transportData = "";
    Integer giYear = 0, giMonth = 0, giDay = 0;
    private LinearLayout llHeightCorrectionFeature;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private TextInputEditText edtGiDate, edtGiTime, edtGiHeightCorrection, spinnerGiPoNumberCust;
    private FloatingActionButton fabSaveGIData;

    private RadioGroup radioGroupOperation;
    private RadioButton radioOperationSelected;

    List<String> vhlRegistNumber, materialName, transportTypeName;

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_good_issue);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Buat Good Issue");
        actionBar.setDisplayHomeAsUpEnabled(true);
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


        edtGiDate = findViewById(R.id.edt_gi_date);
        edtGiTime = findViewById(R.id.edt_gi_time);
        edtGiHeightCorrection = findViewById(R.id.edt_gi_vhl_height_correction);
        edtGiPoNumberBas = findViewById(R.id.edt_gi_po_number_bas);

        spinnerGiPoNumberCust = findViewById(R.id.spinner_gi_po_number_cust);
        spinnerGiVhlRegistNumber = findViewById(R.id.spinner_gi_vhl_regist_number);
        spinnerGiProductName = findViewById(R.id.spinner_gi_product_name);
        spinnerGiTransportType = findViewById(R.id.spinner_gi_transport_type);

        tvGiVhlFinalVolume = findViewById(R.id.tv_gi_vhl_final_volume);
        tvGiVhlLengthVal = findViewById(R.id.tv_gi_vhl_length_val);
        tvGiVhlWidthVal = findViewById(R.id.tv_gi_vhl_width_val);
        tvGiVhlHeightVal = findViewById(R.id.tv_gi_vhl_height_val);
        tvGiVhlHeightCorrection = findViewById(R.id.tv_gi_vhl_height_correction);
        llHeightCorrectionFeature = findViewById(R.id.ll_height_correction_feature);
        fabSaveGIData = findViewById(R.id.fab_save_gi_data);

        vhlRegistNumber  = new ArrayList<>();
        materialName  = new ArrayList<>();
        transportTypeName  = new ArrayList<>();

        radioGroupOperation = findViewById(R.id.radio_group_operation);
        llHeightCorrectionFeature.setVisibility(View.GONE);

        tvGiVhlFinalVolume.setText(Html.fromHtml("0 m\u00B3"));

        edtGiDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(AddGoodIssueActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                edtGiDate.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                                giYear = year;
                                giMonth = month + 1;
                                giDay = dayOfMonth;
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        edtGiTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                timePicker = new TimePickerDialog(AddGoodIssueActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edtGiTime.setText(selectedHour+":"+selectedMinute);
                    }
                }, hour, minute, true);
                timePicker.show();
            }
        });

        edtGiHeightCorrection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DecimalFormat df = new DecimalFormat("0.00");
                int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
                radioOperationSelected = findViewById(selectedStatusId);
                String radioOperation = radioOperationSelected.getText().toString();

                if (edtGiHeightCorrection.getText().toString().equals("")){
                    float finalVolumeDefault =
                            (Float.parseFloat(tvGiVhlLengthVal.getText().toString())*
                                    Float.parseFloat(tvGiVhlWidthVal.getText().toString())*
                                    Float.parseFloat(tvGiVhlHeightVal.getText().toString()))/1000000;
                    tvGiVhlFinalVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolumeDefault))+" m\u00B3"));
                    tvGiVhlHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi: "+tvGiVhlHeightVal.getText().toString())+" cm");
                } else {
                    float finalVolume;
                    int finalHeightCorrection;
                    if (radioOperation.equals("+")){
                        finalVolume = (Float.parseFloat(tvGiVhlLengthVal.getText().toString()) *
                                Float.parseFloat(tvGiVhlWidthVal.getText().toString()) *
                                (Float.parseFloat(tvGiVhlHeightVal.getText().toString()) +
                                        Float.parseFloat(edtGiHeightCorrection.getText().toString()))) / 1000000;
                        finalHeightCorrection = Integer.parseInt(tvGiVhlHeightVal.getText().toString()) + Integer.parseInt(edtGiHeightCorrection.getText().toString());
                    } else{
                        finalVolume = (Float.parseFloat(tvGiVhlLengthVal.getText().toString()) *
                                Float.parseFloat(tvGiVhlWidthVal.getText().toString()) *
                                (Float.parseFloat(tvGiVhlHeightVal.getText().toString()) -
                                        Float.parseFloat(edtGiHeightCorrection.getText().toString()))) / 1000000;
                        finalHeightCorrection = Integer.parseInt(tvGiVhlHeightVal.getText().toString()) - Integer.parseInt(edtGiHeightCorrection.getText().toString());
                    }
                    tvGiVhlFinalVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolume))+" m\u00B3"));
                    tvGiVhlHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi: "+finalHeightCorrection)+" cm");
                }

            }
        });

        radioGroupOperation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                DecimalFormat df = new DecimalFormat("0.00");
                int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
                radioOperationSelected = findViewById(selectedStatusId);
                String radioOperation = radioOperationSelected.getText().toString();

                if (!edtGiHeightCorrection.getText().toString().isEmpty()){
                    float finalVolume;
                    int finalHeightCorrection;
                    if (radioOperation.equals("+")){
                        finalVolume = (Float.parseFloat(tvGiVhlLengthVal.getText().toString()) *
                                Float.parseFloat(tvGiVhlWidthVal.getText().toString()) *
                                (Float.parseFloat(tvGiVhlHeightVal.getText().toString()) +
                                        Float.parseFloat(edtGiHeightCorrection.getText().toString()))) / 1000000;
                        finalHeightCorrection = Integer.parseInt(tvGiVhlHeightVal.getText().toString()) + Integer.parseInt(edtGiHeightCorrection.getText().toString());
                    } else{
                        finalVolume = (Float.parseFloat(tvGiVhlLengthVal.getText().toString()) *
                                Float.parseFloat(tvGiVhlWidthVal.getText().toString()) *
                                (Float.parseFloat(tvGiVhlHeightVal.getText().toString()) -
                                        Float.parseFloat(edtGiHeightCorrection.getText().toString()))) / 1000000;
                        finalHeightCorrection = Integer.parseInt(tvGiVhlHeightVal.getText().toString()) - Integer.parseInt(edtGiHeightCorrection.getText().toString());
                    }
                    tvGiVhlFinalVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolume))+" m\u00B3"));
                    tvGiVhlHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi: "+finalHeightCorrection)+" cm");
                }

            }
        });

        spinnerGiVhlRegistNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerVhlRegistNumber = (String) adapterView.getItemAtPosition(position);
                vhlData = selectedSpinnerVhlRegistNumber;
                spinnerGiVhlRegistNumber.setError(null);
                llHeightCorrectionFeature.setVisibility(View.VISIBLE);
                edtGiHeightCorrection.setText("");
                radioGroupOperation.check(R.id.radio_minus_operation);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VehicleData/"+vhlData);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        VehicleModel vehicleModel = snapshot.getValue(VehicleModel.class);

                        if (vehicleModel!=null){
                            tvGiVhlLengthVal.setText(String.valueOf(vehicleModel.getVhlLength()));
                            tvGiVhlWidthVal.setText(String.valueOf(vehicleModel.getVhlWidth()));
                            tvGiVhlHeightVal.setText(String.valueOf(vehicleModel.getVhlHeight()));

                            float finalVolumeDefault =
                                    (Float.parseFloat(tvGiVhlLengthVal.getText().toString())*
                                            Float.parseFloat(tvGiVhlWidthVal.getText().toString())*
                                            Float.parseFloat(tvGiVhlHeightVal.getText().toString()))/1000000;
                            tvGiVhlFinalVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolumeDefault))+" m\u00B3"));

                        } else {
                            Toast.makeText(AddGoodIssueActivity.this, "Null", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        spinnerGiVhlRegistNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerGiVhlRegistNumber.setText(vhlData);
            }
        });

        spinnerGiProductName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerMaterialName = (String) adapterView.getItemAtPosition(position);
                materialData = selectedSpinnerMaterialName;
                spinnerGiProductName.setError(null);
            }
        });

        spinnerGiProductName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerGiProductName.setText(materialData);
            }
        });

        spinnerGiTransportType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
                transportData = selectedSpinnerTransportType;
                spinnerGiTransportType.setError(null);
            }
        });

        spinnerGiTransportType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerGiTransportType.setText(transportData);
            }
        });

        databaseReference.child("VehicleData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerVhlRegistNumber = dataSnapshot.child("vhlRegistNumber").getValue(String.class);
                        vhlRegistNumber.add(spinnerVhlRegistNumber);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, vhlRegistNumber);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerGiVhlRegistNumber.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("MaterialData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerMaterialData = dataSnapshot.child("name").getValue(String.class);
                        materialName.add(spinnerMaterialData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, materialName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerGiProductName.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("TransportTypeData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerTransportTypeData = dataSnapshot.child("name").getValue(String.class);
                        transportTypeName.add(spinnerTransportTypeData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, transportTypeName);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerGiTransportType.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fabSaveGIData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String giDate = edtGiDate.getText().toString();
                String giTime = Objects.requireNonNull(edtGiTime.getText()).toString();
                String giPOCustomer = Objects.requireNonNull(spinnerGiPoNumberCust.getText()).toString();
                String giPOBAS = Objects.requireNonNull(edtGiPoNumberBas.getText()).toString();
                String giProductName = Objects.requireNonNull(spinnerGiProductName.getText()).toString();
                String giTransportType = Objects.requireNonNull(spinnerGiTransportType.getText()).toString();
                String giVhlRegistNumber = Objects.requireNonNull(spinnerGiVhlRegistNumber.getText()).toString();
                String giHeightCorrection = Objects.requireNonNull(edtGiHeightCorrection.getText()).toString();

                String giVhlLength = tvGiVhlLengthVal.getText().toString();
                String giVhlWidth = tvGiVhlWidthVal.getText().toString();
                String giVhlHeight = tvGiVhlHeightVal.getText().toString();
                String giVhlCubication = tvGiVhlFinalVolume.getText().toString();

                // String finalVhlBrand = vhlBrand;
                //String vhlStatus = "";
                Boolean giStatus = true;
                String giInputDateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                String giInputDateModified = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                if (TextUtils.isEmpty(giDate)){
                    edtGiDate.setError("Mohon masukkan tanggal pembuatan");
                    edtGiDate.requestFocus();
                } else if (TextUtils.isEmpty(giTime)){
                    edtGiTime.setError("Mohon masukkan waktu pembuatan");
                    edtGiTime.requestFocus();
                } else if (TextUtils.isEmpty(giPOCustomer)){
                    spinnerGiPoNumberCust.setError("Mohon masukkan nomor PO customer");
                    spinnerGiPoNumberCust.requestFocus();
                } else if (TextUtils.isEmpty(giProductName)){
                    spinnerGiProductName.setError("Mohon masukkan nama material");
                    spinnerGiProductName.requestFocus();
                } else if (TextUtils.isEmpty(giTransportType)){
                    spinnerGiTransportType.setError("Mohon masukkan jenis transport");
                    spinnerGiTransportType.requestFocus();
                } else if (TextUtils.isEmpty(giVhlRegistNumber)){
                    spinnerGiVhlRegistNumber.setError("Mohon masukkan NOPOL kendaraan");
                    spinnerGiVhlRegistNumber.requestFocus();
                } else if (edtGiHeightCorrection.getText().toString().isEmpty()){
                    giHeightCorrection.equals(0);
                } else{
                    String giUID = "";
                    if (spinnerGiTransportType.getText().toString().equals("CURAH")){
                        giUID = "GI-CRH-"+giYear.toString()+"-"+giMonth.toString()+"-"+giDay.toString()+"-"+getRandomString(4);
                    } else {
                        giUID = "GI-BRG-"+giYear.toString()+"-"+giMonth.toString()+"-"+giDay.toString()+"-"+getRandomString(4);
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    insertData(giUID, giPOCustomer, giPOBAS, giProductName, giTransportType, giVhlRegistNumber,
                            Integer.parseInt(giHeightCorrection.replaceAll("[^0-9]", "")), giTime, giDate, giInputDateCreated, giInputDateModified,
                            Integer.parseInt(giVhlLength), Integer.parseInt(giVhlWidth),
                            Integer.parseInt(giVhlHeight), Float.parseFloat(df.format(Float.parseFloat(giVhlCubication.replaceAll("[^0-9.]", "")))),
                            giStatus);

                   // vhlRegistNumber, finalVhlBrand, vhlIdentityNumber, vhlEngineNumber, dateModified, dateCreated, vhlManufactureYear, Integer.parseInt(vhlLength), Integer.parseInt(vhlWidth), Integer.parseInt(vhlHeight), vhlStatus);

                }
            }
        });
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private void insertData(String giUID, String giPOCustomer, String giPOBAS, String giProductName,
                            String giTransportType, String giVhlRegistNumber, int parseInt,
                            String giTime, String giDate, String giInputDateCreated,
                            String giInputDateModified, int parseInt1, int parseInt2, int parseInt3,
                            float parseFloat, Boolean giStatus) {

        GoodIssueModel goodIssueModel = new GoodIssueModel(giUID, giPOCustomer, giPOBAS, giProductName,
                giTransportType, giVhlRegistNumber, parseInt, giTime, giDate, giInputDateCreated,
                giInputDateModified, parseInt1, parseInt2, parseInt3, parseFloat, giStatus);

        DatabaseReference referenceGoodIssueDetectExists = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData/"+giUID);
        referenceGoodIssueDetectExists.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(AddGoodIssueActivity.this, "Terjadi kesalahan pembuatan UID, coba tutup dan buka kembali aplikasi", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference referenceGoodIssue = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData");
                    referenceGoodIssue.child(giUID).setValue(goodIssueModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddGoodIssueActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                try{
                                    throw task.getException();
                                } catch (Exception e){
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(AddGoodIssueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}