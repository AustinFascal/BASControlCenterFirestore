package com.ptbas.controlcenter.update;

import static android.content.ContentValues.TAG;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class UpdateGoodIssueActivity extends AppCompatActivity {

    String vhlData, matName, matType, roNumber,  roKey ="";
    String monthStrVal, dayStrVal;
    Integer giYear = 0, giMonth = 0, giDay = 0;

    TextView tvHeightCorrection, tvVhlVolume, tvGiCreatedBy, tvGiModifiedBy, tvGiCashedOutStatus, tvGiInvoicedStatus;
    TextInputEditText edtGiDate, edtGiTime, edtPoNumberCust, edtVhlLength, edtVhlWidth, edtVhlHeight,
            edtHeightCorrection;
    AutoCompleteTextView spinnerRoNumber, spinnerMatName,
            spinnerMatType, spinnerVhlUID;

    //LinearLayout llHeightCorrectionFeature;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    RadioGroup radioGroupOperation;
    RadioButton radioOperationSelected;

    FloatingActionButton fabSaveGIData;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    DialogInterface dialogInterface = new DialogInterface();
    //Helper helper = new Helper();
    List<String> vhlUIDList, matNameList, matTypeNameList, receiveOrderNumberList;

    String giUIDVal, giCreatedBy, giVerifiedBy;

    Boolean giStatus, giInvoiced, giCashedOut;

    public FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_good_issue);

        LangUtils.setLocale(this, "en");

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("Perbarui Data Good Issue");
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

        vhlUIDList  = new ArrayList<>();
        matNameList  = new ArrayList<>();
        matTypeNameList  = new ArrayList<>();
        receiveOrderNumberList = new ArrayList<>();

        edtGiDate = findViewById(R.id.edt_gi_date);
        edtGiTime = findViewById(R.id.edt_gi_time);

        spinnerRoNumber = findViewById(R.id.spinner_ro_number);
        edtPoNumberCust = findViewById(R.id.edt_po_number_cust);
        spinnerMatName = findViewById(R.id.spinner_mat_name);
        spinnerMatType = findViewById(R.id.spinner_mat_type);
        spinnerVhlUID = findViewById(R.id.spinner_vhl_uid);

        edtVhlLength = findViewById(R.id.edt_vhl_length);
        edtVhlWidth = findViewById(R.id.edt_vhl_width);
        edtVhlHeight = findViewById(R.id.edt_vhl_height);
        radioGroupOperation = findViewById(R.id.radio_group_operation);
        edtHeightCorrection = findViewById(R.id.edt_vhl_height_correction);
        tvHeightCorrection = findViewById(R.id.tv_vhl_height_correction);
        tvVhlVolume = findViewById(R.id.tv_vhl_volume);


        tvGiCreatedBy = findViewById(R.id.tv_gi_created_by);
        tvGiModifiedBy = findViewById(R.id.tv_gi_modified_by);
        tvGiInvoicedStatus = findViewById(R.id.tv_gi_invoiced_status);
        tvGiCashedOutStatus = findViewById(R.id.tv_gi_cashed_out_tatus);


        fabSaveGIData = findViewById(R.id.fab_save_gi_data);

        tvVhlVolume.setText(Html.fromHtml("0 m\u00B3"));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            DecimalFormat df = new DecimalFormat("0.00");

            giUIDVal = extras.getString("key");
            databaseReference.child("GoodIssueData").child(giUIDVal).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GoodIssueModel goodIssueModel = snapshot.getValue(GoodIssueModel.class);

                    if (goodIssueModel == null){
                        finish();
                    } else {
                        assert goodIssueModel != null;
                        giUIDVal = goodIssueModel.getGiUID();
                        giCreatedBy = goodIssueModel.getGiCreatedBy();
                        giVerifiedBy = goodIssueModel.getGiVerifiedBy();
                        String giDateCreated = goodIssueModel.getGiDateCreated();
                        String giTimeCreated = goodIssueModel.getGiTimeCreted();
                        roNumber = goodIssueModel.getGiRoUID();
                        matName = goodIssueModel.getGiMatName();
                        matType = goodIssueModel.getGiMatType();
                        vhlData = goodIssueModel.getVhlUID();
                        Integer giVhlHeight = goodIssueModel.getVhlHeight();
                        Integer giVhlWidth = goodIssueModel.getVhlWidth();
                        Integer giVhlLength = goodIssueModel.getVhlLength();
                        Integer giVhlHeightCorrection = goodIssueModel.getVhlHeightCorrection();
                        Integer giVhlHeightAfterCorrection = goodIssueModel.getVhlHeightAfterCorrection();
                        float giVhlCubication = goodIssueModel.getGiVhlCubication();
                        giStatus = goodIssueModel.getGiStatus();
                        giInvoiced = goodIssueModel.getGiInvoiced();
                        giCashedOut = goodIssueModel.getGiCashedOut();

                        db.collection("ReceivedOrderData").whereEqualTo("roUID", roNumber).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                            //receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                                            //String documentID = receivedOrderModel.getRoDocumentID();

                                            edtPoNumberCust.setText(receivedOrderModel.getRoPoCustNumber());
                                        }
                                    }
                                });

                        edtPoNumberCust.setOnClickListener(view -> dialogInterface.changePoNumberCustomer(UpdateGoodIssueActivity.this, roNumber));
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
                        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tvGiCreatedBy.setText("Dibuat oleh: "+snapshot.child(giCreatedBy).child("fullName").getValue(String.class));

                                if (giVerifiedBy != null){
                                    tvGiModifiedBy.setText("Disetujui oleh: "+snapshot.child(giVerifiedBy).child("fullName").getValue(String.class));
                                }

                                if (Objects.equals(giVerifiedBy, "") || giVerifiedBy == null || giVerifiedBy.isEmpty()) {
                                    tvGiModifiedBy.setText("Disetujui oleh: BELUM DISETUJUI");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UpdateGoodIssueActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        if (giInvoiced.equals(true)){
                            tvGiInvoicedStatus.setText("Status Ditagihkan ke Customer: SUDAH");
                            fabSaveFormIsEmpty();
                        } else{
                            tvGiInvoicedStatus.setText("Status Ditagihkan ke Customer: BELUM");
                            fabSaveBtnFormIsNotEmpty();
                        }

                        if (giCashedOut.equals(true)){
                            tvGiCashedOutStatus.setText("Status BKK: SUDAH");
                            fabSaveFormIsEmpty();
                        } else{
                            tvGiCashedOutStatus.setText("Status BKK: BELUM");
                            fabSaveBtnFormIsNotEmpty();
                        }

                        edtGiDate.setText(giDateCreated);
                        edtGiTime.setText(giTimeCreated);
                        spinnerRoNumber.setText(roNumber);
                        spinnerMatName.setText(matName);
                        spinnerMatType.setText(matType);
                        spinnerVhlUID.setText(vhlData);
                        edtVhlHeight.setText(String.valueOf(giVhlHeight));
                        edtVhlWidth.setText(String.valueOf(giVhlWidth));
                        edtVhlLength.setText(String.valueOf(giVhlLength));
                        tvVhlVolume.setText(Html.fromHtml(df.format(giVhlCubication) +" m\u00B3"));

                        tvHeightCorrection.setText("Tinggi Hasil Koreksi (TK): "+String.valueOf(giVhlHeightAfterCorrection)+" cm");

                        if (giVhlHeightCorrection.toString().contains("-")){
                            edtHeightCorrection.setText(String.valueOf(giVhlHeightCorrection).substring(1));
                            radioGroupOperation.check(R.id.radio_minus_operation);
                        }
                        if (giVhlHeight<giVhlHeightAfterCorrection) {
                            int correction =  giVhlHeightAfterCorrection-giVhlHeight;
                            edtHeightCorrection.setText(String.valueOf(correction));
                            tvHeightCorrection.setText("Tinggi Hasil Koreksi (TK): "+String.valueOf(giVhlHeightAfterCorrection)+" cm");
                            radioGroupOperation.check(R.id.radio_plus_operation);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        edtGiDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String year = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(UpdateGoodIssueActivity.this,
                    (datePicker, year1, month, dayOfMonth) -> {
                        giYear = year1;
                        giMonth = month + 1;
                        giDay = dayOfMonth;

                        if(month < 10){
                            monthStrVal = "0" + giMonth;
                        } else {
                            monthStrVal = String.valueOf(giMonth);
                        }
                        if(dayOfMonth < 10){
                            dayStrVal = "0" + giDay;
                        } else {
                            dayStrVal = String.valueOf(giDay);
                        }

                        String finalDate = giYear + "-" +monthStrVal + "-" + dayStrVal;

                        edtGiDate.setText(finalDate);
                    }, Integer.parseInt(year), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();

            edtGiDate.setError(null);
        });

        edtGiTime.setOnClickListener(view -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            timePicker = new TimePickerDialog(UpdateGoodIssueActivity.this,
                    (timePicker, selectedHour, selectedMinute) ->
                            edtGiTime.setText(selectedHour + ":" + selectedMinute),
                    hour, minute, true);
            timePicker.show();
            edtGiTime.setError(null);
        });

        edtHeightCorrection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processCountVolume(edtHeightCorrection);
            }
        });

        edtVhlLength.setOnKeyListener((view, i, keyEvent) -> {
            processCountVolume(edtVhlLength);
            return false;
        });

        edtVhlWidth.setOnKeyListener((view, i, keyEvent) -> {
            processCountVolume(edtVhlWidth);
            return false;
        });

        edtVhlHeight.setOnKeyListener((view, i, keyEvent) -> {
            processCountVolume(edtVhlHeight);
            return false;
        });

        radioGroupOperation.setOnCheckedChangeListener((radioGroup, i) -> {
            DecimalFormat df = new DecimalFormat("0.00");
            int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
            radioOperationSelected = findViewById(selectedStatusId);
            String radioOperation = radioOperationSelected.getText().toString();

            if (!edtHeightCorrection.getText().toString().isEmpty()){
                float finalVolume;
                int finalHeightCorrection;
                if (radioOperation.equals("+")){
                    finalVolume =
                            (Float.parseFloat(edtVhlLength.getText().toString()) * Float.parseFloat(edtVhlWidth.getText().toString()) * (Float.parseFloat(edtVhlHeight.getText().toString()) + Float.parseFloat(edtHeightCorrection.getText().toString()))) / 1000000;
                    finalHeightCorrection = Integer.parseInt(edtVhlHeight.getText().toString()) + Integer.parseInt(edtHeightCorrection.getText().toString());
                } else{
                    finalVolume =
                            (Float.parseFloat(edtVhlLength.getText().toString()) * Float.parseFloat(edtVhlWidth.getText().toString()) * (Float.parseFloat(edtVhlHeight.getText().toString()) - Float.parseFloat(edtHeightCorrection.getText().toString()))) / 1000000;
                    finalHeightCorrection = Integer.parseInt(edtVhlHeight.getText().toString()) - Integer.parseInt(edtHeightCorrection.getText().toString());
                }
                tvVhlVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolume))+" m\u00B3"));
                tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi (TK): "+finalHeightCorrection)+" cm");
            }

        });

        edtHeightCorrection.setOnKeyListener((view, i, keyEvent) -> {
            if (edtHeightCorrection.getText().toString().equals("")){
                edtHeightCorrection.setText("0");
            }
            return false;
        });

        spinnerVhlUID.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerVhlRegistNumber = (String) adapterView.getItemAtPosition(position);
            vhlData = selectedSpinnerVhlRegistNumber;
            spinnerVhlUID.setError(null);
            //llHeightCorrectionFeature.setVisibility(View.VISIBLE);
            edtHeightCorrection.setText("0");

            radioGroupOperation.check(R.id.radio_minus_operation);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VehicleData/"+vhlData);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    VehicleModel vehicleModel = snapshot.getValue(VehicleModel.class);

                    if (vehicleModel!=null){
                        edtVhlLength.setText(String.valueOf(vehicleModel.getVhlLength()));
                        edtVhlWidth.setText(String.valueOf(vehicleModel.getVhlWidth()));
                        edtVhlHeight.setText(String.valueOf(vehicleModel.getVhlHeight()));
                        tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi (TK): "+ vehicleModel.getVhlHeight() +" cm"));

                        float finalVolumeDefault =
                                (Float.parseFloat(edtVhlLength.getText().toString())*
                                        Float.parseFloat(edtVhlWidth.getText().toString())*
                                        Float.parseFloat(edtVhlHeight.getText().toString()))/1000000;
                        tvVhlVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolumeDefault))+" m\u00B3"));

                    } else {
                        Toast.makeText(UpdateGoodIssueActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        spinnerMatName.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerMaterialName = (String) adapterView.getItemAtPosition(position);
            matName = selectedSpinnerMaterialName;
            spinnerMatName.setError(null);
        });

        spinnerMatName.setOnFocusChangeListener((view, b) -> spinnerMatName.setText(matName));

        spinnerMatType.setOnClickListener(view -> dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this));

        databaseReference.child("VehicleData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerVhlRegistNumber = dataSnapshot.child("vhlUID").getValue(String.class);
                        vhlUIDList.add(spinnerVhlRegistNumber);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateGoodIssueActivity.this, R.layout.style_spinner, vhlUIDList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerVhlUID.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(UpdateGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
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
                        matTypeNameList.add(spinnerTransportTypeData);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateGoodIssueActivity.this, R.layout.style_spinner, matTypeNameList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerMatType.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(UpdateGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fabSaveFormIsEmpty(){
        fabSaveGIData.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "Anda tidak dapat mengubah data yang sudah ditagihkan.", Toast.LENGTH_SHORT).show());
    }

    private void fabSaveBtnFormIsNotEmpty(){
        fabSaveGIData.setOnClickListener(view -> {
            String giDate = Objects.requireNonNull(edtGiDate.getText()).toString();
            String giTime = Objects.requireNonNull(edtGiTime.getText()).toString();

            String giRONumber = Objects.requireNonNull(spinnerRoNumber.getText()).toString();
            String giPOCustomerNumber = Objects.requireNonNull(edtPoNumberCust.getText()).toString();

            String giMatName = Objects.requireNonNull(spinnerMatName.getText()).toString();
            String giMatType = Objects.requireNonNull(spinnerMatType.getText()).toString();

            String giVhlUID = Objects.requireNonNull(spinnerVhlUID.getText()).toString();
            String giHeightCorrection = Objects.requireNonNull(edtHeightCorrection.getText()).toString();

            String giVhlLength = Objects.requireNonNull(edtVhlLength.getText()).toString();
            String giVhlWidth = Objects.requireNonNull(edtVhlWidth.getText()).toString();
            String giVhlHeight = Objects.requireNonNull(edtVhlHeight.getText()).toString();
            String giVhlCubication = tvVhlVolume.getText().toString();

            int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
            radioOperationSelected = findViewById(selectedStatusId);
            String radioOperation = radioOperationSelected.getText().toString();

            if (TextUtils.isEmpty(giDate)){
                edtGiDate.setError("Mohon masukkan tanggal pembuatan");
                edtGiDate.requestFocus();
            }
            if (TextUtils.isEmpty(giTime)){
                edtGiTime.setError("Mohon masukkan waktu pembuatan");
                edtGiTime.requestFocus();
            }
            if (TextUtils.isEmpty(giRONumber)){
                spinnerRoNumber.setError("Mohon masukkan nomor RO");
                spinnerRoNumber.requestFocus();
            }
            if (TextUtils.isEmpty(giPOCustomerNumber)){
                edtPoNumberCust.setError("Mohon masukkan nomor PO customer");
                edtPoNumberCust.requestFocus();
            }
            if (TextUtils.isEmpty(giMatName)){
                spinnerMatName.setError("Mohon masukkan nama material");
                spinnerMatName.requestFocus();
            }
            if (TextUtils.isEmpty(giMatType)){
                spinnerMatType.setError("Mohon masukkan jenis transport");
                spinnerMatType.requestFocus();
            }
            if (TextUtils.isEmpty(giVhlUID)){
                spinnerVhlUID.setError("Mohon masukkan NOPOL kendaraan");
                spinnerVhlUID.requestFocus();
            }

            if (TextUtils.isEmpty(giVhlLength)||giVhlLength.equals("0")){
                edtVhlLength.setError("Mohon masukkan lebar kendaraan");
                edtVhlLength.requestFocus();
            }
            if (TextUtils.isEmpty(giVhlWidth)||giVhlWidth.equals("0")){
                edtVhlWidth.setError("Mohon masukkan lebar kendaraan");
                edtVhlWidth.requestFocus();
            }
            if (TextUtils.isEmpty(giVhlHeight)||giVhlHeight.equals("0")){
                edtVhlHeight.setError("Mohon masukkan lebar kendaraan");
                edtVhlHeight.requestFocus();
            }
            if (edtHeightCorrection.getText().toString().isEmpty()){
                giHeightCorrection.equals(0);
                edtHeightCorrection.setText("0");
            }

            if (!TextUtils.isEmpty(giDate)&&!TextUtils.isEmpty(giTime)
                    &&!TextUtils.isEmpty(giRONumber)&&!TextUtils.isEmpty(giPOCustomerNumber)
                    &&!TextUtils.isEmpty(giMatName)&&!TextUtils.isEmpty(giMatType)
                    &&!TextUtils.isEmpty(giVhlUID)&&!TextUtils.isEmpty(giVhlWidth)
                    &&!TextUtils.isEmpty(giVhlLength)&&!TextUtils.isEmpty(giVhlHeight)
                    &&!TextUtils.isEmpty(giHeightCorrection)){
                DecimalFormat df = new DecimalFormat("0.00");
                insertData(giUIDVal, giCreatedBy, giVerifiedBy, giRONumber, giPOCustomerNumber, giMatName, giMatType,
                        giVhlUID, giDate, giTime,
                        Integer.parseInt(giVhlLength),
                        Integer.parseInt(giVhlWidth),
                        Integer.parseInt(giVhlHeight),
                        Integer.parseInt(radioOperation+giHeightCorrection.replaceAll("[^0-9]", "")),
                        Integer.parseInt(tvHeightCorrection.getText().toString().replaceAll("[^0-9]", "")),
                        Float.parseFloat(df.format(Float.parseFloat(giVhlCubication.replaceAll("[^0-9.]", "")))),
                        giStatus, giInvoiced, giCashedOut);
            }

        });
    }

    private void insertData(String giUID, String giCreatedBy, String giVerifiedBy, String giRoUID,
                            String giPoCustNumber, String giMatName, String giMatType,
                            String vhlUID, String giDateCreated, String giTimeCreted,
                            int vhlLength, int vhlWidth, int vhlHeight,
                            int vhlHeightCorrection, int vhlHeightAfterCorrection,
                            float giVhlCubication, Boolean giStatus, Boolean giInvoiced, Boolean giCashedOut) {
        VehicleModel vehicleModel =
                new VehicleModel(vhlUID, true, vhlLength, vhlWidth, vhlHeight,
                        "", "", "", "");

        DatabaseReference dbRefAddVehicle = FirebaseDatabase.getInstance().getReference("VehicleData");
        dbRefAddVehicle.child(vhlUID).setValue(vehicleModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Toast.makeText(AddGoodIssueActivity.this, "Data ", Toast.LENGTH_SHORT).show();
            } else {
                try{
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(UpdateGoodIssueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoodIssueModel goodIssueModel = new GoodIssueModel(giUID, giCreatedBy, giVerifiedBy, giRoUID, giPoCustNumber,
                giMatName, giMatType, vhlUID, giDateCreated, giTimeCreted, vhlLength,
                vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection, giVhlCubication, giStatus, giInvoiced, giCashedOut);

        DatabaseReference refGI = FirebaseDatabase.getInstance().getReference("GoodIssueData");
        refGI.child(giUID).setValue(goodIssueModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialogInterface.updatedInformation(UpdateGoodIssueActivity.this);
            } else {
                try{
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(UpdateGoodIssueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void processCountVolume(TextInputEditText textInputEditText){
        DecimalFormat df = new DecimalFormat("0.00");
        int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
        radioOperationSelected = findViewById(selectedStatusId);
        String radioOperation = radioOperationSelected.getText().toString();
        if (textInputEditText.getText().toString().equals("")){
            textInputEditText.setText("0");
            float finalVolumeDefault =
                    (Float.parseFloat(edtVhlLength.getText().toString())*
                            Float.parseFloat(edtVhlWidth.getText().toString())*
                            Float.parseFloat(edtVhlHeight.getText().toString()))/1000000;
            tvVhlVolume.setText(Html.fromHtml(df.format(finalVolumeDefault)+" m\u00B3"));
        } else {
            float finalVolume;
            int finalHeightCorrection;
            if (radioOperation.equals("+")){
                finalVolume =
                        (Float.parseFloat(edtVhlLength.getText().toString()) * Float.parseFloat(edtVhlWidth.getText().toString()) * (Float.parseFloat(edtVhlHeight.getText().toString()) + Float.parseFloat(edtHeightCorrection.getText().toString()))) / 1000000;
                finalHeightCorrection = Integer.parseInt(edtVhlHeight.getText().toString()) + Integer.parseInt(edtHeightCorrection.getText().toString());
            } else{
                finalVolume =
                        (Float.parseFloat(edtVhlLength.getText().toString()) * Float.parseFloat(edtVhlWidth.getText().toString()) * (Float.parseFloat(edtVhlHeight.getText().toString()) - Float.parseFloat(edtHeightCorrection.getText().toString()))) / 1000000;
                finalHeightCorrection = Integer.parseInt(edtVhlHeight.getText().toString()) - Integer.parseInt(edtHeightCorrection.getText().toString());
            }
            tvVhlVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolume))+" m\u00B3"));
            tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi (TK): "+finalHeightCorrection)+" cm");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            dialogInterface.deleteGiFromActivityConfirmation(UpdateGoodIssueActivity.this, giUIDVal);
        } else {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}