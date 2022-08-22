package com.ptbas.controlcenter.create;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class AddGoodIssueActivity extends AppCompatActivity {

    String vhlData = "", matName = "", matType = "", roNumber ="", roKey ="";
    String monthStrVal, dayStrVal;
    Integer giYear = 0, giMonth = 0, giDay = 0;

    TextView tvHeightCorrection, tvVhlVolume;
    TextInputEditText edtGiDate, edtGiTime, edtPoNumberCust, edtVhlLength, edtVhlWidth, edtVhlHeight,
            edtHeightCorrection;
    AutoCompleteTextView spinnerRoNumber, spinnerMatName,
            spinnerMatType, spinnerVhlUID;

    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    RadioGroup radioGroupOperation;
    RadioButton radioOperationSelected;

    FloatingActionButton fabSaveGIData;

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    DialogInterface dialogInterface = new DialogInterface();
    Helper helper = new Helper();
    List<String> vhlUIDList, matNameList, matTypeNameList, receiveOrderNumberList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_good_issue);

        LangUtils.setLocale(this, "en");

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("Buat Good Issue");
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

        DecimalFormat df = new DecimalFormat("0.00");

        float finalVolumeDefault =
                (Float.parseFloat(edtVhlLength.getText().toString())*
                        Float.parseFloat(edtVhlWidth.getText().toString())*
                        Float.parseFloat(edtVhlHeight.getText().toString()))/1000000;
        tvVhlVolume.setText(Html.fromHtml(df.format(finalVolumeDefault))+" m\u00B3");

        fabSaveGIData = findViewById(R.id.fab_save_gi_data);

        edtGiDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String year = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(AddGoodIssueActivity.this,
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

            timePicker = new TimePickerDialog(AddGoodIssueActivity.this,
                    (timePicker, selectedHour, selectedMinute) ->
                            edtGiTime.setText(String.valueOf(selectedHour+":"+selectedMinute)),
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
            DecimalFormat df1 = new DecimalFormat("0.00");
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
                tvVhlVolume.setText(Html.fromHtml(String.valueOf(df1.format(finalVolume))+" m\u00B3"));
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
                    DecimalFormat df12 = new DecimalFormat("0.00");
                    VehicleModel vehicleModel = snapshot.getValue(VehicleModel.class);

                    if (vehicleModel!=null){
                        edtVhlLength.setText(String.valueOf(vehicleModel.getVhlLength()));
                        edtVhlWidth.setText(String.valueOf(vehicleModel.getVhlWidth()));
                        edtVhlHeight.setText(String.valueOf(vehicleModel.getVhlHeight()));
                        tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi (TK): "+ vehicleModel.getVhlHeight() +" cm"));

                        float finalVolumeDefault1 =
                                (Float.parseFloat(edtVhlLength.getText().toString())*
                                        Float.parseFloat(edtVhlWidth.getText().toString())*
                                        Float.parseFloat(edtVhlHeight.getText().toString()))/1000000;
                        tvVhlVolume.setText(Html.fromHtml(String.valueOf(df12.format(finalVolumeDefault1))+" m\u00B3"));

                    } else {
                        Toast.makeText(AddGoodIssueActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        spinnerRoNumber.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(position);
            spinnerMatName.setText("");
            spinnerMatName.setError(null);
            spinnerMatName.requestFocus();
            db.collection("ReceivedOrderData").whereEqualTo("roUID", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            String matNameStr = "";
                            matNameList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                                //String documentID = receivedOrderModel.getRoDocumentID();

                                String roMatType = receivedOrderModel.getRoMatType();
                                String roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                                spinnerMatType.setText(roMatType);
                                edtPoNumberCust.setText(roPoCustNumber);

                                HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                                for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                    for (ProductItems productItems : e.getValue()) {
                                        matNameStr = productItems.getMatName();
                                    }
                                }

                                matNameList.addAll(Collections.singleton(matNameStr));
                                matNameList.remove("JASA ANGKUT");

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, matNameList);
                                arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                spinnerMatName.setAdapter(arrayAdapter);
                            }
                        }
                    });

            /*db.collection("ReceivedOrderData").whereEqualTo("roUID", selectedSpinnerPoPtBasNumber)
                    .addSnapshotListener((value, error) -> {
                        //receiveOrderNumberList.clear();
                        if (value != null) {
                            if (!value.isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {

                                    *//*String roMatType = Objects.requireNonNull(d.get("roMatType")).toString();
                                    //String roMatType = Objects.requireNonNull(d.get("roOrderedItems")).toString();
                                    spinnerMatType.setText(roMatType);*//*



                                }

                            } else {

                            }
                        }

                    });*/

            /*db.collection("ReceivedOrderData").orderBy("roDateCreated")
                    .addSnapshotListener((value, error) -> {

                        if (!value.isEmpty()){
                            for (DocumentSnapshot d : value.getDocuments()) {
                                ReceivedOrderModel receivedOrderModel = d.toObject(ReceivedOrderModel.class);
                            }
                        } else{
                        }
                    });*/

           /* databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            String key = dataSnapshot.getKey();
                            assert key != null;
                            String roUID = snapshot.child(key).child("roUID").getValue(String.class);
                            if (Objects.equals(roUID, selectedSpinnerPoPtBasNumber)){
                                roNumber = roUID;
                                ReceivedOrderModel receivedOrderModel = snapshot.child(key).getValue(ReceivedOrderModel.class);
                                if (Objects.equals(snapshot.child(key).child("roStatus").getValue(), true)) {
                                    if (receivedOrderModel != null) {
                                        matNameList.clear();
                                        roKey = key;
                                        edtPoNumberCust.setText(String.valueOf(receivedOrderModel.getRoPoCustNumber()));
                                        if (receivedOrderModel.getRoMatType().contains("CUR")) {
                                            spinnerMatType.setText("CURAH");
                                            matType = "CURAH";
                                        } else {
                                            spinnerMatType.setText("BORONG");
                                            matType = "BORONG";
                                        }
                                        edtPoNumberCust.setOnClickListener(view1 ->
                                                dialogInterface.changePoNumberCustomer(
                                                        AddGoodIssueActivity.this, roNumber));
                                    } else {
                                        Toast.makeText(AddGoodIssueActivity.this, "Null", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //dialogInterface.roNotActiveYet(AddGoodIssueActivity.this, roNumber);

                                }
                            }
                        }
                        DatabaseReference databaseReferenceMaterial = FirebaseDatabase.getInstance().getReference("ReceivedOrders");
                        databaseReferenceMaterial.child(roKey).child("OrderedItems").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                matNameList.clear();
                                if (snapshot.exists()){
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        String spinnerMaterialData = dataSnapshot.child("matName").getValue(String.class);
                                        matNameList.add(spinnerMaterialData);
                                        matNameList.remove("JASA ANGKUT");
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, matNameList);
                                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                    spinnerMatName.setAdapter(arrayAdapter);
                                } else {
                                    Toast.makeText(AddGoodIssueActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    } else {
                        //dialogInterface.roNotExistsDialog(AddGoodIssueActivity.this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });*/

            spinnerRoNumber.setError(null);
            edtPoNumberCust.setError(null);
            matNameList.clear();
        });

        spinnerMatName.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerMaterialName = (String) adapterView.getItemAtPosition(position);
            matName = selectedSpinnerMaterialName;
            spinnerMatName.setError(null);
        });

        spinnerMatName.setOnKeyListener((view, i, keyEvent) -> {
            spinnerMatName.setError(null);
            spinnerMatName.clearFocus();
            return false;
        });

        //spinnerMatName.setOnFocusChangeListener((view, b) -> spinnerMatName.setText(matName));

        spinnerMatType.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
            matType = selectedSpinnerTransportType;
            spinnerMatType.setError(null);
        });

        //spinnerMatType.setOnFocusChangeListener((view, b) -> spinnerMatType.setText(matType));

        db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                .addSnapshotListener((value, error) -> {
                    receiveOrderNumberList.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String spinnerPurchaseOrders = Objects.requireNonNull(d.get("roUID")).toString();
                                receiveOrderNumberList.add(spinnerPurchaseOrders);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerRoNumber.setAdapter(arrayAdapter);
                        } else {
                            if(!this.isFinishing()) {
                                dialogInterface.roNotExistsDialog(AddGoodIssueActivity.this);
                            }
                        }
                    }

                });

        /*databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receiveOrderNumberList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerPurchaseOrders = dataSnapshot.child("roUID").getValue(String.class);
                        receiveOrderNumberList.add(spinnerPurchaseOrders);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerRoNumber.setAdapter(arrayAdapter);
                } else {
                    //dialogInterface.roNotExistsDialog(AddGoodIssueActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        databaseReference.child("VehicleData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerVhlRegistNumber = dataSnapshot.child("vhlUID").getValue(String.class);
                        vhlUIDList.add(spinnerVhlRegistNumber);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, vhlUIDList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerVhlUID.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddGoodIssueActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fabSaveGIData.setOnClickListener(view -> {
            String giDate = Objects.requireNonNull(edtGiDate.getText()).toString();
            String giTime = Objects.requireNonNull(edtGiTime.getText()).toString();

            String giRONumber = Objects.requireNonNull(spinnerRoNumber.getText()).toString();
            String giPOCustomerNumber = Objects.requireNonNull(edtPoNumberCust.getText()).toString();

            String giMatName = Objects.requireNonNull(spinnerMatName.getText()).toString();
            String giMatType = Objects.requireNonNull(spinnerMatType.getText()).toString();

            String giVhlUID = Objects.requireNonNull(spinnerVhlUID.getText()).toString();
            String giHeightCorrection = Objects.requireNonNull(edtHeightCorrection.getText()).toString();
            String giCreatedBy = helper.getUserId();
            String giVerifiedBy = null;

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
                edtGiTime.setText("-");
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

            if (!TextUtils.isEmpty(giDate)
                    &&!TextUtils.isEmpty(giRONumber)&&!TextUtils.isEmpty(giPOCustomerNumber)
                    &&!TextUtils.isEmpty(giMatName)&&!TextUtils.isEmpty(giMatType)
                    &&!TextUtils.isEmpty(giVhlUID)&&!TextUtils.isEmpty(giVhlWidth)
                    &&!TextUtils.isEmpty(giVhlLength)&&!TextUtils.isEmpty(giVhlHeight)
                    &&!TextUtils.isEmpty(giHeightCorrection))

                for(int i = 0; i<matNameList.size(); i++){
                    if (!spinnerMatName.getText().toString().equals(matNameList.get(i))){
                        spinnerMatName.setError("Nama material tidak ditemukan");
                        spinnerMatName.requestFocus();
                    } else{
                        spinnerMatName.setError(null);
                        spinnerMatName.clearFocus();
                        String giUID = "";
                        giUID = getRandomString(5)+"-"+ giMatType.substring(0, 3)+"-"+giDay.toString()+"-"+giMonth.toString()+"-"+giYear.toString();

                        insertData(giUID, giCreatedBy, giVerifiedBy, giRONumber, giPOCustomerNumber, giMatName, giMatType,
                                giVhlUID, giDate, giTime,
                                Integer.parseInt(giVhlLength),
                                Integer.parseInt(giVhlWidth),
                                Integer.parseInt(giVhlHeight),
                                Integer.parseInt(radioOperation+giHeightCorrection.replaceAll("[^0-9]", "")),
                                Integer.parseInt(tvHeightCorrection.getText().toString().replaceAll("[^0-9]", "")),
                                Float.parseFloat(df.format(Float.parseFloat(giVhlCubication.replaceAll("[^0-9.]", "")))),
                                false, false);
                    }
                }
        });
    }

    private void insertData(String giUID, String giCreatedBy, String giVerifiedBy, String giRoUID,
                            String giPoCustNumber, String giMatName, String giMatType,
                            String vhlUID, String giDateCreated, String giTimeCreted,
                            int vhlLength, int vhlWidth, int vhlHeight,
                            int vhlHeightCorrection, int vhlHeightAfterCorrection,
                            float giVhlCubication, Boolean giStatus, Boolean giInvoiced) {

        GoodIssueModel goodIssueModel = new GoodIssueModel(giUID, giCreatedBy, giVerifiedBy, giRoUID, giPoCustNumber,
                giMatName, giMatType, vhlUID, giDateCreated, giTimeCreted, vhlLength,
                vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection, giVhlCubication, giStatus, giInvoiced);

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
                    Toast.makeText(AddGoodIssueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference refGIExists = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData/"+giUID);
        refGIExists.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(AddGoodIssueActivity.this, "Terjadi kesalahan pembuatan UID, coba tutup dan buka kembali aplikasi", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference refGI = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData");
                    refGI.child(giUID).setValue(goodIssueModel).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogInterface.savedInformationFromManagement(AddGoodIssueActivity.this);
                        } else {
                            try{
                                throw task.getException();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(AddGoodIssueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            tvVhlVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolumeDefault))+" m\u00B3"));
            //tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi: "+tvHeightCorrection.getText().toString())+" cm");
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

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        dialogInterface.discardDialogConfirmation(AddGoodIssueActivity.this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        dialogInterface.discardDialogConfirmation(AddGoodIssueActivity.this);
    }

}