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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.utils.LangUtils;

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

    String roDocumentID, custIDVal="", vhlData = "", selectedMatName = "", selectedCustName = "", matType = "", selectedPoNumber ="", roKey ="";
    String monthStrVal, dayStrVal;
    Integer giYear = 0, giMonth = 0, giDay = 0;

    LinearLayout llDetailTypeCurah;

    TextView tvHeightCorrection, tvVhlVolume;
    TextInputEditText edtGiDate, edtGiTime, edtPoNumberCust, edtVhlLength, edtVhlWidth, edtVhlHeight,
            edtHeightCorrection, edtGiNoteNumber;
    AutoCompleteTextView spinnerCustUID, spinnerRoNumber, spinnerMatName,
            spinnerMatType, spinnerVhlUID;

    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    RadioGroup radioGroupOperation;
    RadioButton radioOperationSelected;

    ImageButton btnResetCustomer;

    FloatingActionButton fabSaveGIData;

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    DialogInterface dialogInterface = new DialogInterface();
    Helper helper = new Helper();
    List<String> vhlUIDList, matNameList, matTypeNameList, receiveOrderNumberList, customerList;

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
        customerList = new ArrayList<>();

        edtGiDate = findViewById(R.id.edt_gi_date);
        edtGiTime = findViewById(R.id.edt_gi_time);

        edtGiNoteNumber = findViewById(R.id.edtGiNoteNumber);

        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        spinnerCustUID = findViewById(R.id.spinnerCustUID);
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

        llDetailTypeCurah = findViewById(R.id.ll_detail_type_curah);

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

                        //int monthInt = month + 1;

                        if(giMonth < 10){
                            monthStrVal = "0" + giMonth;
                        } else {
                            monthStrVal = String.valueOf(giMonth);
                        }
                        if(dayOfMonth <= 9){
                            dayStrVal = "0" + giDay;
                        } else {
                            dayStrVal = String.valueOf(giDay);
                        }

                        String finalDate = giYear   + "-" +monthStrVal + "-" + dayStrVal;

                        edtGiDate.setText(finalDate);
                    }, Integer.parseInt(year), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();

            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
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


        edtVhlLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processCountVolume(edtVhlLength);

            }
        });


        edtVhlWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processCountVolume(edtVhlWidth);

            }
        });

        edtVhlHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                processCountVolume(edtVhlHeight);

            }
        });

        /*edtVhlLength.setOnKeyListener((view, i, keyEvent) -> {
            processCountVolume(edtVhlLength);
            return false;
        });*/

        /*edtVhlWidth.setOnKeyListener((view, i, keyEvent) -> {
            processCountVolume(edtVhlWidth);
            return false;
        });*/

        /*edtVhlHeight.setOnKeyListener((view, i, keyEvent) -> {
            processCountVolume(edtVhlHeight);
            return false;
        });*/

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

        // INIT DATA SPINNER CUSTOMER DATA
        db.collection("CustomerData").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    if (!value.isEmpty()) {
                        for (DocumentSnapshot d : value.getDocuments()) {
                            String custList = Objects.requireNonNull(d.get("custUID")).toString().concat(" - " + Objects.requireNonNull(d.get("custName")).toString());
                            customerList.add(custList);

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, customerList);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCustUID.setAdapter(arrayAdapter);
                        }
                    }
                }
            }
        });

        // INIT DATA SPINNER VEHICLE DATA
        databaseReference.child("VehicleData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if (Objects.equals(dataSnapshot.child("vhlStatus").getValue(), true)){
                            String spinnerVhlRegistNumber = dataSnapshot.child("vhlUID").getValue(String.class);
                            vhlUIDList.add(spinnerVhlRegistNumber);
                        }
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

        btnResetCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerCustUID.setText(null);
                spinnerRoNumber.setText(null);
                edtPoNumberCust.setText(null);
                spinnerMatName.setText(null);
                spinnerMatType.setText(null);

                btnResetCustomer.setVisibility(View.GONE);
                selectedCustName = null;
            }
        });

        spinnerCustUID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCustomer = (String) adapterView.getItemAtPosition(i);
                selectedCustName = selectedCustomer;
                spinnerCustUID.setError(null);

                btnResetCustomer.setVisibility(View.VISIBLE);
                spinnerRoNumber.setText(null);
                edtPoNumberCust.setText(null);
                spinnerMatName.setText(null);
                spinnerMatType.setText(null);


                db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                        .addSnapshotListener((value, error) -> {
                            if (!Objects.requireNonNull(value).isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {
                                    db.collection("CustomerData").whereEqualTo("custDocumentID", Objects.requireNonNull(d.get("custDocumentID")).toString())
                                            .addSnapshotListener((value2, error2) -> {
                                                receiveOrderNumberList.clear();
                                                if (!Objects.requireNonNull(value2).isEmpty()) {
                                                    for (DocumentSnapshot e : value2.getDocuments()) {
                                                        custIDVal = Objects.requireNonNull(e.get("custName")).toString();
                                                        db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                                                                .addSnapshotListener((value3, error3) -> {
                                                                    if (!Objects.requireNonNull(value3).isEmpty()) {
                                                                        String spinnerPurchaseOrders = Objects.requireNonNull(d.get("roPoCustNumber")).toString();
                                                                        if (selectedCustomer.contains(custIDVal)) {
                                                                            receiveOrderNumberList.add(spinnerPurchaseOrders);
                                                                        }
                                                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                                                                        arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                                        spinnerRoNumber.setAdapter(arrayAdapter);
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    if (!isFinishing()) {
                                                        dialogInterface.roNotExistsDialog(AddGoodIssueActivity.this);
                                                    }
                                                }
                                            });
                                }
                            }
                        });

            }
        });

        spinnerRoNumber.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(position);
            spinnerMatName.setText("");
            spinnerMatName.setError(null);
            edtGiNoteNumber.requestFocus();

            selectedPoNumber = selectedSpinnerPoPtBasNumber;

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            //String matNameStr = "";
                            matNameList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                                //String documentID = receivedOrderModel.getRoDocumentID();

                                String roMatType = receivedOrderModel.getRoMatType();
                                String roUID = receivedOrderModel.getRoUID();
                                roDocumentID = receivedOrderModel.getRoDocumentID();
                                spinnerMatType.setText(roMatType);
                                edtPoNumberCust.setText(roUID);

                                /*HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                                for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                    for (ProductItems productItems : e.getValue()) {
                                        matNameStr = productItems.getMatName();
                                    }
                                }*/

                                matNameList.addAll(receivedOrderModel.getRoOrderedItems().keySet());

                                //matNameList.addAll(Collections.singleton(matNameStr));
                                int matNameListSize = matNameList.size();
                                if (matNameListSize > 1){
                                    matNameList.remove("JASA ANGKUT");
                                }

                                if (receivedOrderModel.getRoType().equals(2)){
                                    llDetailTypeCurah.setVisibility(View.GONE);
                                } else {
                                    llDetailTypeCurah.setVisibility(View.VISIBLE);
                                }
                                /*if (matNameList.get(0).equals("JASA ANGKUT")){

                                }*/

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddGoodIssueActivity.this, R.layout.style_spinner, matNameList);
                                arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                spinnerMatName.setAdapter(arrayAdapter);

                                spinnerMatName.setText(matNameList.get(0));


                            }
                        }
                    });

            spinnerRoNumber.setError(null);
            edtPoNumberCust.setError(null);
            matNameList.clear();
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

        spinnerMatName.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerMaterialName = (String) adapterView.getItemAtPosition(position);
            selectedMatName = selectedSpinnerMaterialName;
            spinnerMatName.setError(null);
        });

        spinnerMatName.setOnKeyListener((view, i, keyEvent) -> {
            spinnerMatName.setError(null);
            spinnerMatName.clearFocus();
            return false;
        });

        spinnerMatType.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
            matType = selectedSpinnerTransportType;
            spinnerMatType.setError(null);
        });


        spinnerCustUID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerCustUID.setText(selectedCustName);
            }
        });

        spinnerMatName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerMatName.setText(selectedMatName);
            }
        });

        fabSaveGIData.setOnClickListener(view -> {
            String giDate = Objects.requireNonNull(edtGiDate.getText()).toString();
            String giTime = Objects.requireNonNull(edtGiTime.getText()).toString();

            String giRONumber = Objects.requireNonNull(edtPoNumberCust.getText()).toString();
            String giPOCustomerNumber = Objects.requireNonNull(spinnerRoNumber.getText()).toString();

            String giMatName = Objects.requireNonNull(spinnerMatName.getText()).toString();
            String giMatType = Objects.requireNonNull(spinnerMatType.getText()).toString();

            String giVhlUID = Objects.requireNonNull(spinnerVhlUID.getText()).toString();
            String giHeightCorrection = Objects.requireNonNull(edtHeightCorrection.getText()).toString();
            String giCreatedBy = helper.getUserId();
            String giVerifiedBy = null;

            String giNoteNumber = Objects.requireNonNull(edtGiNoteNumber.getText()).toString();

            String giVhlLength = Objects.requireNonNull(edtVhlLength.getText()).toString();
            String giVhlWidth = Objects.requireNonNull(edtVhlWidth.getText()).toString();
            String giVhlHeight = Objects.requireNonNull(edtVhlHeight.getText()).toString();
            String giVhlCubication = tvVhlVolume.getText().toString();

            int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
            radioOperationSelected = findViewById(selectedStatusId);
            String radioOperation = radioOperationSelected.getText().toString();

            if  (giMatName.equals("JASA ANGKUT")){
                if (TextUtils.isEmpty(giDate)){
                    edtGiDate.setError("Mohon masukkan tanggal pembuatan");
                    edtGiDate.requestFocus();
                }
                if (giTime.equals("-")){
                    giTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                }
                if (TextUtils.isEmpty(giRONumber)){
                    spinnerRoNumber.setError("Mohon masukkan nomor PO customer");
                    spinnerRoNumber.requestFocus();
                }
                if (TextUtils.isEmpty(giNoteNumber)){
                    edtGiNoteNumber.setError("Mohon masukkan nomor nota");
                    edtGiNoteNumber.requestFocus();
                }
                if (TextUtils.isEmpty(giPOCustomerNumber)){
                    edtPoNumberCust.setError("Mohon masukkan nomor RO");
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
                        &&!TextUtils.isEmpty(giNoteNumber)
                        &&!TextUtils.isEmpty(giVhlUID)&&!TextUtils.isEmpty(giVhlWidth)
                        &&!TextUtils.isEmpty(giVhlLength)&&!TextUtils.isEmpty(giVhlHeight)
                        &&!TextUtils.isEmpty(giHeightCorrection)){
                    for(int i = 0; i<receiveOrderNumberList.size(); i++) {
                        if (!receiveOrderNumberList.contains(spinnerRoNumber.getText().toString())){
                            spinnerRoNumber.setError("Nomor PO tidak ditemukan");
                            spinnerRoNumber.requestFocus();
                        }
                    }
                    for(int i = 0; i<matNameList.size(); i++) {
                        if (!spinnerMatName.getText().toString().equals(matNameList.get(i))) {
                            spinnerMatName.setError("Nama material tidak ditemukan");
                            spinnerMatName.requestFocus();
                        }
                    }
                    if (spinnerRoNumber.getError() == null && spinnerMatName.getError() == null){
                        spinnerMatName.setError(null);
                        spinnerMatName.clearFocus();
                        String giUID = "";
                        giUID = giNoteNumber + "-" + giMatType.substring(0, 3) + "-" + giDay.toString() + "-" + giMonth.toString() + "-" + giYear.toString();

                        insertData(giUID, giCreatedBy, giVerifiedBy, roDocumentID, giMatName, giMatType,
                                giNoteNumber, giVhlUID, giDate, giTime,
                                Integer.parseInt(giVhlLength),
                                Integer.parseInt(giVhlWidth),
                                Integer.parseInt(giVhlHeight),
                                Integer.parseInt(radioOperation + giHeightCorrection.replaceAll("[^0-9]", "")),
                                Integer.parseInt(tvHeightCorrection.getText().toString().replaceAll("[^0-9]", "")),
                                Float.parseFloat(df.format(Float.parseFloat(giVhlCubication.replaceAll("[^0-9.]", "")))),
                                false, false, false);
                    }
                }

            } else{
                if (TextUtils.isEmpty(giDate)){
                    edtGiDate.setError("Mohon masukkan tanggal pembuatan");
                    edtGiDate.requestFocus();
                }
                if (giTime.equals("-")){
                    giTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                }
                if (TextUtils.isEmpty(giRONumber)){
                    spinnerRoNumber.setError("Mohon masukkan nomor PO customer");
                    spinnerRoNumber.requestFocus();
                }
                if (TextUtils.isEmpty(giNoteNumber)){
                    edtGiNoteNumber.setError("Mohon masukkan nomor nota");
                    edtGiNoteNumber.requestFocus();
                }
                if (TextUtils.isEmpty(giPOCustomerNumber)){
                    edtPoNumberCust.setError("Mohon masukkan nomor RO");
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
                        &&!TextUtils.isEmpty(giNoteNumber)
                        &&!TextUtils.isEmpty(giVhlUID)&&!TextUtils.isEmpty(giVhlWidth)
                        &&!TextUtils.isEmpty(giVhlLength)&&!TextUtils.isEmpty(giVhlHeight)
                        &&!TextUtils.isEmpty(giHeightCorrection)){
                    for(int i = 0; i<receiveOrderNumberList.size(); i++) {
                        if (!receiveOrderNumberList.contains(spinnerRoNumber.getText().toString())){
                            spinnerRoNumber.setError("Nomor PO tidak ditemukan");
                            spinnerRoNumber.requestFocus();
                        }
                    }
                    for(int i = 0; i<matNameList.size(); i++) {
                        if (!spinnerMatName.getText().toString().equals(matNameList.get(i))) {
                            spinnerMatName.setError("Nama material tidak ditemukan");
                            spinnerMatName.requestFocus();
                        }
                    }
                    if (spinnerRoNumber.getError() == null && spinnerMatName.getError() == null){
                        spinnerMatName.setError(null);
                        spinnerMatName.clearFocus();
                        String giUID = "";
                        giUID = giNoteNumber + "-" + giMatType.substring(0, 3) + "-" + giDay.toString() + "-" + giMonth.toString() + "-" + giYear.toString();

                        insertData(giUID, giCreatedBy, giVerifiedBy, roDocumentID, giMatName, giMatType,
                                giNoteNumber, giVhlUID, giDate, giTime,
                                Integer.parseInt(giVhlLength),
                                Integer.parseInt(giVhlWidth),
                                Integer.parseInt(giVhlHeight),
                                Integer.parseInt(radioOperation + giHeightCorrection.replaceAll("[^0-9]", "")),
                                Integer.parseInt(tvHeightCorrection.getText().toString().replaceAll("[^0-9]", "")),
                                Float.parseFloat(df.format(Float.parseFloat(giVhlCubication.replaceAll("[^0-9.]", "")))),
                                false, false, false);
                    }
                }

            }
        });
    }

    private void insertData(String giUID, String giCreatedBy, String giVerifiedBy, String roDocumentID, String giMatName, String giMatType,
                            String giNoteNumber, String vhlUID, String giDateCreated, String giTimeCreted,
                            int vhlLength,   int vhlWidth, int vhlHeight,
                            int vhlHeightCorrection, int vhlHeightAfterCorrection,
                            float giVhlCubication, Boolean giStatus, Boolean giRecapped, Boolean giInvoiced) {

        GoodIssueModel goodIssueModel = new GoodIssueModel(giUID, giCreatedBy, giVerifiedBy, roDocumentID,
                giMatName, giMatType, giNoteNumber, vhlUID, giDateCreated, giTimeCreted, vhlLength,
                vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection, giVhlCubication, giStatus, giRecapped, giInvoiced, false);

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
        onBackPressed();
        //dialogInterface.discardDialogConfirmation(AddGoodIssueActivity.this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        //dialogInterface.discardDialogConfirmation(AddGoodIssueActivity.this);
    }

}