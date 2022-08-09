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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.management.GoodIssueManagementActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.VehicleModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UpdateGoodIssueActivity extends AppCompatActivity {

    String vhlData, matName, matType, roNumber;
    Integer giYear = 0, giMonth = 0, giDay = 0;

    TextView tvHeightCorrection, tvVhlVolume;
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

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    DialogInterface dialogInterface = new DialogInterface();
    Helper helper = new Helper();
    List<String> vhlUIDList, matNameList, matTypeNameList, receiveOrderNumberList;

    String giUIDVal, giCreatedBy, giVerifiedBy;

    Boolean giStatus, giInvoiced;

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

                    assert goodIssueModel != null;
                    giUIDVal = goodIssueModel.getGiUID();
                    giCreatedBy = goodIssueModel.getGiCreatedBy();
                    giVerifiedBy = goodIssueModel.getGiVerifiedBy();
                    String giDateCreated = goodIssueModel.getGiDateCreated();
                    String giTimeCreated = goodIssueModel.getGiTimeCreted();
                    roNumber = goodIssueModel.getGiRoUID();
                    //String giPoCustNumber = goodIssueModel.getGiPoCustNumber();
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

                    DatabaseReference databaseReferencePO = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ roNumber);
                    databaseReferencePO.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String spinnerPoNumberData = snapshot.child("roPoCustNumber").getValue(String.class);
                            edtPoNumberCust.setText(spinnerPoNumberData);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    edtPoNumberCust.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogInterface.changePoNumberCustomer(UpdateGoodIssueActivity.this, roNumber);
                        }
                    });

                    edtGiDate.setText(giDateCreated);
                    edtGiTime.setText(giTimeCreated);
                    spinnerRoNumber.setText(roNumber);
                    //edtPoNumberCust.setText(giPoCustNumber);
                    spinnerMatName.setText(matName);
                    spinnerMatType.setText(matType);
                    spinnerVhlUID.setText(vhlData);
                    edtVhlHeight.setText(String.valueOf(giVhlHeight));
                    edtVhlWidth.setText(String.valueOf(giVhlWidth));
                    edtVhlLength.setText(String.valueOf(giVhlLength));
                    tvVhlVolume.setText(Html.fromHtml(String.valueOf(df.format(giVhlCubication))+" m\u00B3"));

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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




        edtGiDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(UpdateGoodIssueActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                edtGiDate.setText(String.valueOf(dayOfMonth + "/" +(month + 1) + "/" + year));
                                giYear = year;
                                giMonth = month + 1;
                                giDay = dayOfMonth;
                            }
                        }, year, month, day);
                datePicker.show();

                edtGiDate.setError(null);
            }
        });

        edtGiTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                timePicker = new TimePickerDialog(UpdateGoodIssueActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                edtGiTime.setText(String.valueOf(selectedHour+":"+selectedMinute));
                            }
                        }, hour, minute, true);
                timePicker.show();
                edtGiTime.setError(null);
            }
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

                /*DecimalFormat df = new DecimalFormat("0.00");
                int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
                radioOperationSelected = findViewById(selectedStatusId);
                String radioOperation = radioOperationSelected.getText().toString();

                if (edtHeightCorrection.getText().toString().equals("")){
                    float finalVolumeDefault =
                            (Float.parseFloat(edtVhlLength.getText().toString())*
                                    Float.parseFloat(edtVhlWidth.getText().toString())*
                                    Float.parseFloat(edtVhlHeight.getText().toString()))/1000000;
                    tvVhlVolume.setText(Html.fromHtml(String.valueOf(df.format(finalVolumeDefault))+" m\u00B3"));
                    tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi: "+tvHeightCorrection.getText().toString())+" cm");
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
                    tvHeightCorrection.setText(Html.fromHtml("Tinggi Hasil Koreksi: "+finalHeightCorrection)+" cm");
                }*/

            }
        });

        edtVhlLength.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                processCountVolume(edtVhlLength);
                return false;
            }
        });

        edtVhlWidth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                processCountVolume(edtVhlWidth);
                return false;
            }
        });

        edtVhlHeight.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                processCountVolume(edtVhlHeight);
                return false;
            }
        });

        radioGroupOperation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
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

            }
        });

        edtHeightCorrection.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (edtHeightCorrection.getText().toString().equals("")){
                    edtHeightCorrection.setText("0");
                }
                return false;
            }
        });





        spinnerVhlUID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
            }
        });

        spinnerRoNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(position);
                roNumber = selectedSpinnerPoPtBasNumber;
                spinnerRoNumber.setError(null);
                edtPoNumberCust.setError(null);
                matNameList.clear();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ roNumber);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReceivedOrderModel receivedOrderModel = snapshot.getValue(ReceivedOrderModel.class);

                        if (receivedOrderModel !=null){
                            edtPoNumberCust.setText(String.valueOf(receivedOrderModel.getRoPoCustNumber()));
                            if (receivedOrderModel.getRoMatType().contains("CUR")){
                                spinnerMatType.setText("CURAH");
                                matType = "CURAH";
                            } else{
                                spinnerMatType.setText("BORONG");
                                matType = "BORONG";

                            }
                        } else {
                            Toast.makeText(UpdateGoodIssueActivity.this, "Null", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                DatabaseReference databaseReferencePO = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ roNumber +"/OrderedItems");
                databaseReferencePO.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                String spinnerMaterialData = dataSnapshot.child("matName").getValue(String.class);
                                matNameList.add(spinnerMaterialData);
                                matNameList.remove("JASA ANGKUT");
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateGoodIssueActivity.this, R.layout.style_spinner, matNameList);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerMatName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(UpdateGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        spinnerMatName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /*String selectedSpinnerMaterialName = (String) adapterView.getItemAtPosition(position);
                matName = selectedSpinnerMaterialName;
                spinnerMatName.setError(null);*/

                Toast.makeText(UpdateGoodIssueActivity.this, "Anda tidak dapat merubah data ini", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerMatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this);
            }
        });

        spinnerMatName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                spinnerMatName.setError(null);
                spinnerMatName.clearFocus();
                return false;
            }
        });

        spinnerMatName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerMatName.setText(matName);
            }
        });

        spinnerMatType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.dataCannotBeChangedInformation(UpdateGoodIssueActivity.this);
            }
        });

        spinnerMatType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /*String selectedSpinnerTransportType = (String) adapterView.getItemAtPosition(position);
                matType = selectedSpinnerTransportType;
                spinnerMatType.setError(null);*/

                Toast.makeText(UpdateGoodIssueActivity.this, "Anda tidak dapat merubah data ini", Toast.LENGTH_SHORT).show();

            }
        });

        spinnerMatType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                spinnerMatType.setText(matType);
            }
        });

        databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if (Objects.equals(dataSnapshot.child("roStatus").getValue(), true)){
                            String spinnerPurchaseOrders = dataSnapshot.child("roUID").getValue(String.class);
                            receiveOrderNumberList.add(spinnerPurchaseOrders);
                        }

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateGoodIssueActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerRoNumber.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(UpdateGoodIssueActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        /*databaseReference.child("MaterialData").addValueEventListener(new ValueEventListener() {
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
        });*/


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

        fabSaveGIData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String giDate = Objects.requireNonNull(edtGiDate.getText()).toString();
                String giTime = Objects.requireNonNull(edtGiTime.getText()).toString();

                String giRONumber = Objects.requireNonNull(spinnerRoNumber.getText()).toString();
                String giPOCustomerNumber = Objects.requireNonNull(edtPoNumberCust.getText()).toString();

                String giMatName = Objects.requireNonNull(spinnerMatName.getText()).toString();
                String giMatType = Objects.requireNonNull(spinnerMatType.getText()).toString();

                String giVhlUID = Objects.requireNonNull(spinnerVhlUID.getText()).toString();
                String giHeightCorrection = Objects.requireNonNull(edtHeightCorrection.getText()).toString();
                //String giCreatedBy = helper.getUserId();

                String giVhlLength = Objects.requireNonNull(edtVhlLength.getText()).toString();
                String giVhlWidth = Objects.requireNonNull(edtVhlWidth.getText()).toString();
                String giVhlHeight = Objects.requireNonNull(edtVhlHeight.getText()).toString();
                String giVhlCubication = tvVhlVolume.getText().toString();

                int selectedStatusId = radioGroupOperation.getCheckedRadioButtonId();
                radioOperationSelected = findViewById(selectedStatusId);
                String radioOperation = radioOperationSelected.getText().toString();

                //boolean giStatus = false;
                //String giInputDateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

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
                            giStatus, giInvoiced);

                }

                    /*for(int i = 0; i<matNameList.size(); i++){
                        if (!spinnerMatName.getText().toString().equals(matNameList.get(i).toString())){
                            spinnerMatName.setError("Nama material tidak ditemukan");
                            spinnerMatName.requestFocus();
                        } else{
                            spinnerMatName.setError(null);
                            spinnerMatName.clearFocus();
                            //String giUID = giUIDVal;
                            //giUID = getRandomString(5)+"-"+ matType.substring(0, 3)+"-"+giDay.toString()+"-"+giMonth.toString()+"-"+giYear.toString();

                        }
                    }*/



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

        DatabaseReference refGIExists = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData/"+giUID);
        refGIExists.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DatabaseReference refGI = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("GoodIssueData");
                refGI.child(giUID).setValue(goodIssueModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialogInterface.updatedInformation(UpdateGoodIssueActivity.this);
                        } else {
                            try{
                                throw task.getException();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(UpdateGoodIssueActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //dialogInterface.discardDialogConfirmation(UpdateGoodIssueActivity.this);
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //dialogInterface.discardDialogConfirmation(UpdateGoodIssueActivity.this);
    }

}