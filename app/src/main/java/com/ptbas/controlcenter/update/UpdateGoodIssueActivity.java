package com.ptbas.controlcenter.update;

import static android.content.ContentValues.TAG;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class UpdateGoodIssueActivity extends AppCompatActivity {

    String supportPhoneNumber, roDocumentID, vhlData, matName, matType, roNumber, giNoteNumber;
    String monthStrVal, dayStrVal;
    Integer giYear = 0, giMonth = 0, giDay = 0;

    TextView tvHeightCorrection, tvVhlVolume;
    TextInputEditText edtGiDate, edtGiTime, edtPoNumberCust, edtVhlLength, edtVhlWidth, edtVhlHeight,
            edtHeightCorrection, edtGiNoteNumber, spinnerRoNumber, spinnerMatName, spinnerMatType;
    TextInputEditText edtCreatedBy, edtApprovedBy, edtRequestedToCo, edtInvoicedTo;
    AutoCompleteTextView spinnerVhlUID;

    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    RadioGroup radioGroupOperation;
    RadioButton radioOperationSelected;

    FloatingActionButton fabSaveGIData;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    DialogInterface dialogInterface = new DialogInterface();
    List<String> vhlUIDList, matNameList, matTypeNameList, receiveOrderNumberList;

    String giUIDVal, giCreatedBy, giVerifiedBy, giCashedOutTo, giRecappedTo;

    Boolean giStatus, giRecapped, giInvoiced, giCashedOut;

    public FirebaseAuth authProfile;

    Helper helper = new Helper();

    private FirebaseRemoteConfig firebaseRemoteConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_good_issue);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                        .build();
        firebaseRemoteConfig.setConfigSettingsAsync(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaultsAsync(R.xml.default_values);

        getValueFromFirebaseConfig();

        LangUtils.setLocale(this, "en");

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("Rincian Data Good Issue");
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

        edtGiNoteNumber = findViewById(R.id.edtGiNoteNumber);
        edtVhlLength = findViewById(R.id.edt_vhl_length);
        edtVhlWidth = findViewById(R.id.edt_vhl_width);
        edtVhlHeight = findViewById(R.id.edt_vhl_height);

        edtCreatedBy = findViewById(R.id.edtCreatedBy);
        edtApprovedBy = findViewById(R.id.edtApprovedBy);
        edtRequestedToCo = findViewById(R.id.edtRequestedToCo);
        edtInvoicedTo = findViewById(R.id.edtInvoicedTo);

        radioGroupOperation = findViewById(R.id.radio_group_operation);
        edtHeightCorrection = findViewById(R.id.edt_vhl_height_correction);
        tvHeightCorrection = findViewById(R.id.tv_vhl_height_correction);
        tvVhlVolume = findViewById(R.id.edtVhlVol);

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
                        roDocumentID = goodIssueModel.getRoDocumentID();
                        matName = goodIssueModel.getGiMatName();
                        matType = goodIssueModel.getGiMatType();
                        vhlData = goodIssueModel.getVhlUID();
                        giNoteNumber = goodIssueModel.getGiNoteNumber();
                        Integer giVhlHeight = goodIssueModel.getVhlHeight();
                        Integer giVhlWidth = goodIssueModel.getVhlWidth();
                        Integer giVhlLength = goodIssueModel.getVhlLength();
                        Integer giVhlHeightCorrection = goodIssueModel.getVhlHeightCorrection();
                        Integer giVhlHeightAfterCorrection = goodIssueModel.getVhlHeightAfterCorrection();
                        Double giVhlCubication = goodIssueModel.getGiVhlCubication();
                        giStatus = goodIssueModel.getGiStatus();
                        giInvoiced = goodIssueModel.getGiInvoiced();
                        giCashedOut = goodIssueModel.getGiCashedOut();
                        giRecapped = goodIssueModel.getGiRecapped();
                        giRecappedTo = goodIssueModel.getGiRecappedTo();
                        giCashedOutTo = goodIssueModel.getGiCashedOutTo();


                        db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID).get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                        edtPoNumberCust.setText(receivedOrderModel.getRoPoCustNumber());
                                        spinnerRoNumber.setText(receivedOrderModel.getRoUID());
                                    }
                                });

                        db.collection("CashOutData").whereEqualTo("coDocumentID", snapshot.child("giCashedOutTo").getValue(String.class)).get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                        String coAccBy = documentSnapshot.get("coAccBy", String.class);
                                        if (giCashedOut.equals(true)){
                                            edtRequestedToCo.setText(documentSnapshot.get("coUID", String.class));
                                        }
                                        if (!coAccBy.isEmpty()){
                                            fabSaveFormIsEmpty();
                                        }
                                    }
                                });

                        db.collection("InvoiceData").whereEqualTo("invDocumentUID", snapshot.child("giInvoicedTo").getValue(String.class)).get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                        String invUID = documentSnapshot.get("invUID", String.class);
                                        if (giInvoiced.equals(true)){
                                            edtInvoicedTo.setText(invUID);
                                        }
                                    }
                                });

                        edtPoNumberCust.setOnClickListener(view -> dialogInterface.changePoNumberCustomer(UpdateGoodIssueActivity.this, roNumber));
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
                        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                edtCreatedBy.setText(snapshot.child(giCreatedBy).child("fullName").getValue(String.class));

                                if (giVerifiedBy != null){
                                    edtApprovedBy.setText(snapshot.child(giVerifiedBy).child("fullName").getValue(String.class));
                                }

                                if (Objects.equals(giVerifiedBy, "") || giVerifiedBy == null || giVerifiedBy.isEmpty()) {
                                    edtApprovedBy.setText("BELUM DISETUJUI");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UpdateGoodIssueActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        if (giInvoiced.equals(false)) {
                            edtInvoicedTo.setText("BELUM DITAGIHKAN");
                            fabSaveBtnFormIsNotEmpty();
                        }

                        if (giCashedOut.equals(false)){
                            edtRequestedToCo.setText("BELUM DIAJUKAN");
                            fabSaveBtnFormIsNotEmpty();
                        }

                        edtGiNoteNumber.setText(giNoteNumber);
                        edtGiDate.setText(giDateCreated);
                        edtGiTime.setText(giTimeCreated);

                        spinnerMatName.setText(matName);
                        spinnerMatType.setText(matType);
                        spinnerVhlUID.setText(vhlData);
                        edtVhlHeight.setText(String.valueOf(giVhlHeight));
                        edtVhlWidth.setText(String.valueOf(giVhlWidth));
                        edtVhlLength.setText(String.valueOf(giVhlLength));
                        tvVhlVolume.setText(Html.fromHtml(df.format(giVhlCubication) +" m\u00B3"));

                        tvHeightCorrection.setText("Tinggi Hasil Koreksi (TK): "+giVhlHeightAfterCorrection+" cm");

                        if (giVhlHeightCorrection.toString().contains("-")){
                            edtHeightCorrection.setText(String.valueOf(giVhlHeightCorrection).substring(1));
                            radioGroupOperation.check(R.id.radio_minus_operation);
                        }
                        if (giVhlHeight<giVhlHeightAfterCorrection) {
                            int correction =  giVhlHeightAfterCorrection-giVhlHeight;
                            edtHeightCorrection.setText(String.valueOf(correction));
                            tvHeightCorrection.setText("Tinggi Hasil Koreksi (TK): "+giVhlHeightAfterCorrection+" cm");
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
            vhlData = (String) adapterView.getItemAtPosition(position);
            spinnerVhlUID.setError(null);
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
                        tvVhlVolume.setText(Html.fromHtml(df.format(finalVolumeDefault)+" m\u00B3"));

                    } else {
                        Toast.makeText(UpdateGoodIssueActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        edtGiNoteNumber.setOnClickListener(view -> dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this));
        spinnerMatName.setOnClickListener(view -> dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this));
        edtPoNumberCust.setOnClickListener(view -> dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this));
        spinnerRoNumber.setOnClickListener(view -> dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this));
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
    }

    private void getValueFromFirebaseConfig() {
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        supportPhoneNumber = firebaseRemoteConfig.getString("support_phone_number");
                    } else {
                        Toast.makeText(UpdateGoodIssueActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fabSaveFormIsEmpty(){
        fabSaveGIData.setOnClickListener(view -> {
            MaterialDialog md = new MaterialDialog.Builder(UpdateGoodIssueActivity.this)
                    .setAnimation(R.raw.lottie_attention)
                    .setTitle("Gagal")
                    .setMessage("Anda tidak dapat mengubah data yang sudah dibayarkan ke supplier. Hubungi administrator untuk bantuan.")
                    .setPositiveButton("BANTUAN", R.drawable.ic_outline_support, (dialogInterface, which) -> {
                        String url = "https://api.whatsapp.com/send?phone=" + supportPhoneNumber;
                        try {
                            PackageManager pm = context.getPackageManager();
                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(UpdateGoodIssueActivity.this, "Whatsapp tidak terpasang di perangkat Anda", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("TUTUP", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                    })
                    .setCancelable(false)
                    .build();

            md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            md.show();
        });
    }

    private void fabSaveBtnFormIsNotEmpty(){
        fabSaveGIData.setOnClickListener(view -> {
            String giDate = Objects.requireNonNull(edtGiDate.getText()).toString();
            String giTime = Objects.requireNonNull(edtGiTime.getText()).toString();

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
                    &&!TextUtils.isEmpty(giVhlUID)&&!TextUtils.isEmpty(giVhlWidth)
                    &&!TextUtils.isEmpty(giVhlLength)&&!TextUtils.isEmpty(giVhlHeight)
                    &&!TextUtils.isEmpty(giHeightCorrection)){
                DecimalFormat df = new DecimalFormat("0.00");
                insertData(giUIDVal, giCreatedBy, giVerifiedBy,
                        giVhlUID, giDate, giTime,
                        Integer.parseInt(giVhlLength),
                        Integer.parseInt(giVhlWidth),
                        Integer.parseInt(giVhlHeight),
                        Integer.parseInt(radioOperation+giHeightCorrection.replaceAll("[^0-9]", "")),
                        Integer.parseInt(tvHeightCorrection.getText().toString().replaceAll("[^0-9]", "")),
                        Double.parseDouble(giVhlCubication.replaceAll("[^0-9.]", "")),
                        giStatus, giRecapped, giInvoiced, giCashedOut, giCashedOutTo, giRecappedTo);
            }

        });
    }

    private void insertData(String giUID, String giCreatedBy, String giVerifiedBy,
                            String vhlUID, String giDateCreated, String giTimeCreted,
                            int vhlLength, int vhlWidth, int vhlHeight,
                            int vhlHeightCorrection, int vhlHeightAfterCorrection,
                            Double giVhlCubication, Boolean giStatus, Boolean giRecapped, Boolean giInvoiced, Boolean giCashedOut, String giCashedOutTo, String giRecappedTo) {
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

        GoodIssueModel goodIssueModel = new GoodIssueModel(giUID, giCreatedBy, giVerifiedBy, roDocumentID,
                matName, matType, giNoteNumber, vhlUID, giDateCreated, giTimeCreted, vhlLength,
                vhlWidth, vhlHeight, vhlHeightCorrection, vhlHeightAfterCorrection, giVhlCubication, giStatus, giRecapped, giInvoiced, giCashedOut, giCashedOutTo, giRecappedTo);
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
        inflater.inflate(R.menu.delete_update_status, menu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            giUIDVal = extras.getString("key");
            databaseReference.child("GoodIssueData").child(giUIDVal).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GoodIssueModel goodIssueModel = snapshot.getValue(GoodIssueModel.class);
                    if (goodIssueModel == null){
                        finish();
                    } else {
                        assert goodIssueModel != null;
                        giStatus = goodIssueModel.getGiStatus();
                        menu.findItem(R.id.menu_verify).setVisible(giStatus.equals(false));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            dialogInterface.deleteGiFromActivityConfirmation(UpdateGoodIssueActivity.this, giUIDVal);
        } else if (item.getItemId() == R.id.menu_verify) {
            dialogInterface.approveGiConfirmationFromUpdateActivity(UpdateGoodIssueActivity.this, giUIDVal, item);
        } else {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}