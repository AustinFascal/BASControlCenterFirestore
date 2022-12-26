package com.ptbas.controlcenter.create;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.RcpDataAdapter;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.RcpModel;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.model.SplrModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AddCoActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    String  custDocumentID, roDocumentID, rouidVal= "", suppplieruidVal = "",
            pouidVal = "", roPoCustNumber, custNameVal = "",
            custAddressVal = "", coUID="", coCreatedBy="",
            supplierPayee, supplierBankAndAccountNumber, supplierAccountOwnerName,
            customerData = "", customerID ="", randomString="NULL";
    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;

    LinearLayout llBottomSelectionOptions, llNoData, llWrapFilter, llShowSpinnerRoAndEdtPo, llWrapSupplierDetail;
    ImageButton btnExitSelection;
    TextView tvTotalSelectedItem, tvTotalSelectedItem2;
    ImageButton btnResetRouid, btnResetCustomer, btnResetSupplier;
    ExtendedFloatingActionButton fabCreateCOR;
    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID, spinnerCustName, spinnerSupplierName;
    TextInputEditText edtPoUID, edtBankNameAndAccountNumber, edtAccountOwnerName, edtPayee;
    ArrayList<RcpModel> rcpModelArrayList = new ArrayList<>();
    RecyclerView rvGoodIssueList;
    CardView cdvFilter;
    View firstViewData;
    NestedScrollView nestedScrollView;

    Context context;
    Vibrator vibrator;
    HelperUtils helperUtils = new HelperUtils();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();
    List<String> arrayListRoUID, arrayListPoUID, arrayListSupplierUID;
    List<String> customerName, supplierName, recapID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontNormal, fontNormalSmall, fontNormalSmallItalic,
            fontMedium, fontMediumWhite, fontBold, fontTransparent;
    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");

    boolean isSelectedAll = false;

    private Menu menu;

    RcpDataAdapter recapGiManagementAdapter;

    List<String> receiveOrderNumberList;

    Boolean statusSelectedRecap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_co);

        LangUtils.setLocale(this, "en");

        helperUtils.ACTIVITY_NAME = "UPDATE";

        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);

        btnSearchData = findViewById(R.id.caridata);
        btnExitSelection = findViewById(R.id.btnExitSelection);
        btnResetRouid = findViewById(R.id.btnResetRouid);
        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        btnResetSupplier = findViewById(R.id.btnResetSupplier);

        spinnerRoUID = findViewById(R.id.rouid);
        spinnerCustName = findViewById(R.id.spinnerCustName);
        spinnerSupplierName = findViewById(R.id.spinnerSupplierName);
        spinnerSupplierName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerCustName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerRoUID.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        edtPoUID = findViewById(R.id.pouid);
        edtBankNameAndAccountNumber = findViewById(R.id.edtBankNameAndAccountNumber);
        edtAccountOwnerName = findViewById(R.id.edtAccountOwnerName);
        edtPayee = findViewById(R.id.edtPayee);

        rvGoodIssueList = findViewById(R.id.rvItemList);

        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);

        llWrapSupplierDetail = findViewById(R.id.llWrapSupplierDetail);
        llWrapFilter = findViewById(R.id.llWrapFilter);
        llShowSpinnerRoAndEdtPo = findViewById(R.id.llShowSpinnerRoAndEdtPo);
        llBottomSelectionOptions = findViewById(R.id.llBottomSelectionOptions);
        llNoData = findViewById(R.id.ll_no_data);

        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        tvTotalSelectedItem2 = findViewById(R.id.tvTotalSelectedItem2);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        fabCreateCOR = findViewById(R.id.fabCreateCOR);

        vibrator  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        helperUtils.ACTIVITY_NAME = "UPDATE";

        customerName = new ArrayList<>();
        supplierName = new ArrayList<>();
        arrayListRoUID = new ArrayList<>();
        arrayListPoUID = new ArrayList<>();
        recapID = new ArrayList<>();
        arrayListSupplierUID = new ArrayList<>();
        receiveOrderNumberList = new ArrayList<>();

        baseColorBluePale = new BaseColor(22,169,242);
        baseColorLightGrey = new BaseColor(237, 237, 237);

        try {
            baseNormal = BaseFont.createFont("/res/font/kanitregular.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            baseMedium = BaseFont.createFont("/res/font/kanitsemibold.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            baseBold = BaseFont.createFont("/res/font/kanitextrabold.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        fontNormal = new Font(baseNormal, 10, Font.NORMAL, BaseColor.BLACK);
        fontNormalSmall = new Font(baseNormal, 8, Font.NORMAL, BaseColor.BLACK);
        fontNormalSmallItalic = new Font(baseNormal, 8, Font.ITALIC, BaseColor.BLACK);
        fontMedium = new Font(baseMedium, 10, Font.NORMAL, BaseColor.BLACK);
        fontMediumWhite = new Font(baseMedium, 10, Font.NORMAL, BaseColor.WHITE);
        fontBold = new Font(baseBold, 20, Font.NORMAL, BaseColor.BLACK);
        fontTransparent = new Font(baseNormal, 20, Font.NORMAL, null);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Buat Cash Out");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");


        db.collection("CustomerData").whereEqualTo("custStatus", true)
                .addSnapshotListener((value, error) -> {
                    customerName.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String spinnerCustUID = Objects.requireNonNull(d.get("custUID")).toString();
                                String spinnerCustName = Objects.requireNonNull(d.get("custName")).toString();
                                customerName.add(spinnerCustUID+" - "+spinnerCustName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCoActivity.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCustName.setAdapter(arrayAdapter);
                        } else {
                            //Toast.makeText(AddCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("SupplierData").whereEqualTo("supplierStatus", true)
                .addSnapshotListener((value, error) -> {
                    supplierName.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String spinnerSupplierName = Objects.requireNonNull(d.get("supplierName")).toString();
                                String supplierID = Objects.requireNonNull(d.get("supplierID")).toString();
                                supplierName.add(spinnerSupplierName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCoActivity.this, R.layout.style_spinner, supplierName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerSupplierName.setAdapter(arrayAdapter);
                        } else {
                           // Toast.makeText(AddCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        spinnerCustName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCustomer = (String) adapterView.getItemAtPosition(i);
                String[] custNameSplit = selectedCustomer.split(" - ");
                String custNameSplit1 = custNameSplit[1];

                customerData = selectedCustomer;
                spinnerCustName.setError(null);

                customerID = custNameSplit[0];
                randomString = getRandomString2(5);

                btnResetCustomer.setVisibility(View.VISIBLE);
                clearRoPoData();

                llShowSpinnerRoAndEdtPo.setVisibility(View.VISIBLE);


                db.collection("CustomerData").whereEqualTo("custName", custNameSplit1)
                        .addSnapshotListener((value2, error2) -> {
                            receiveOrderNumberList.clear();
                            if (!Objects.requireNonNull(value2).isEmpty()) {
                                for (DocumentSnapshot d : value2.getDocuments()) {
                                    custDocumentID = Objects.requireNonNull(d.get("custDocumentID")).toString();

                                    db.collection("ReceivedOrderData").whereEqualTo("custDocumentID", custDocumentID)
                                            .addSnapshotListener((value, error) -> {
                                                if (!Objects.requireNonNull(value).isEmpty()) {
                                                    for (DocumentSnapshot e : value.getDocuments()) {
                                                        String spinnerPurchaseOrders = Objects.requireNonNull(e.get("roPoCustNumber")).toString();
                                                        long roType = (long) Objects.requireNonNull(e.get("roType"));
                                                        if (roType != 2){
                                                            receiveOrderNumberList.add(spinnerPurchaseOrders);
                                                        }
                                                    }
                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCoActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                                                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                    spinnerRoUID.setAdapter(arrayAdapter);
                                                }
                                            });
                                }

                            }
                        });
            }

        });

        spinnerCustName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (spinnerCustName.getText().toString().equals("")){
                    llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                }
                return false;
            }
        });

        /*edtDateStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(AddCashOutActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {
                        int monthInt = month + 1;

                        if(monthInt < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }

                        if(dayOfMonth <= 9){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtDateStart.setText(finalDate);
                        dateStartVal = finalDate;

                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();
        });

        edtDateEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(AddCashOutActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {
                        int monthInt = month + 1;

                        if(monthInt < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }

                        if(dayOfMonth <= 9){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtDateEnd.setText(finalDate);
                        dateEndVal = finalDate;

                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();
        });*/

        spinnerSupplierName.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerSupplierName.setError(null);
            String selectedSpinnerSupplierID = (String) adapterView.getItemAtPosition(i);

            llWrapSupplierDetail.setVisibility(View.VISIBLE);
            btnResetSupplier.setVisibility(View.VISIBLE);

            db.collection("SupplierData").whereEqualTo("supplierName", selectedSpinnerSupplierID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            SplrModel splrModel = documentSnapshot.toObject(SplrModel.class);
                            splrModel.setSupplierID(documentSnapshot.getId());
                            suppplieruidVal = splrModel.getSupplierID();
                            supplierPayee = splrModel.getSupplierPayeeName();
                            String bankAlias = splrModel.getBankName();
                            String bankAliasReplace = bankAlias.replace(" - ","-");
                            int indexBankAliasVal = bankAliasReplace.lastIndexOf('-');
                            supplierBankAndAccountNumber = bankAliasReplace.substring(0, indexBankAliasVal) + " - " + splrModel.getBankAccountNumber() ;
                            supplierAccountOwnerName = splrModel.getBankAccountOwnerName();
                        }
                        edtBankNameAndAccountNumber.setText(supplierBankAndAccountNumber);
                        edtAccountOwnerName.setText(supplierAccountOwnerName);
                        edtPayee.setText(supplierPayee);
                    });
        });

        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            btnResetRouid.setVisibility(View.VISIBLE);

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            RoModel roModel = documentSnapshot.toObject(RoModel.class);
                            roModel.setRoDocumentID(documentSnapshot.getId());
                            rouidVal = selectedSpinnerPoPtBasNumber;
                            roPoCustNumber = roModel.getRoPoCustNumber();
                            roDocumentID = roModel.getRoDocumentID();
                            custDocumentID = roModel.getCustDocumentID();

                        }
                        edtPoUID.setText(roPoCustNumber);
                    });
        });

        recapGiManagementAdapter = new RcpDataAdapter(this, rcpModelArrayList);

        btnResetCustomer.setOnClickListener(view -> {
            isSelectedAll = false;
            menu.findItem(R.id.select_all_data_recap).setVisible(false);
            btnResetCustomer.setVisibility(View.GONE);
            //btnResetRouid.setVisibility(View.GONE);
            spinnerCustName.setText(null);
            customerData = null;
            clearRoPoData();
            llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
        });
        btnResetSupplier.setOnClickListener(view -> {
            btnResetSupplier.setVisibility(View.GONE);
            llWrapSupplierDetail.setVisibility(View.GONE);
            spinnerSupplierName.setText(null);
            edtBankNameAndAccountNumber.setText(null);
            edtAccountOwnerName.setText(null);
            edtPayee.setText(null);
        });
       /* btnGiSearchByDateReset.setOnClickListener(view -> {
            edtDateStart.setText(null);
            edtDateEnd.setText(null);
            edtDateStart.clearFocus();
            edtDateEnd.clearFocus();
            dateStartVal = "";
            dateEndVal = "";
            btnGiSearchByDateReset.setVisibility(View.GONE);
        });*/
        btnResetRouid.setOnClickListener(view -> {
            spinnerRoUID.setText(null);
            rouidVal = null;
            edtPoUID.setText(null);
            btnResetRouid.setVisibility(View.GONE);
            btnResetCustomer.setVisibility(View.GONE);
        });
        btnSearchData.setOnClickListener(view -> {
            View viewLayout = AddCoActivity.this.getCurrentFocus();
            if (viewLayout != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
            }
            if (spinnerCustName.getText().toString().isEmpty()){
                spinnerCustName.setError("");
            } else{
                spinnerCustName.setError(null);
            }

            if (spinnerRoUID.getText().toString().isEmpty()){
                spinnerRoUID.setError("");
            } else{
                spinnerRoUID.setError(null);
            }

            if (Objects.requireNonNull(edtPoUID.getText()).toString().isEmpty()){
                edtPoUID.setError("");
            } else{
                edtPoUID.setError(null);
            }



            if (spinnerSupplierName.getText().toString().isEmpty()){
                spinnerSupplierName.setError("");
            } else{
                spinnerSupplierName.setError(null);
            }

            if (Objects.requireNonNull(edtBankNameAndAccountNumber.getText()).toString().isEmpty()){
                edtBankNameAndAccountNumber.setError("");
            } else{
                edtBankNameAndAccountNumber.setError(null);
            }

            if (Objects.requireNonNull(edtAccountOwnerName.getText()).toString().isEmpty()){
                edtAccountOwnerName.setError("");
            } else{
                edtAccountOwnerName.setError(null);
            }

            if (Objects.requireNonNull(edtPayee.getText()).toString().isEmpty()){
                edtPayee.setError("");
            } else{
                edtPayee.setError(null);
            }

//!spinnerRcpID.getText().toString().isEmpty()&&
            if (!spinnerCustName.getText().toString().isEmpty()&&
                    !spinnerRoUID.getText().toString().isEmpty()&&
                    !spinnerSupplierName.getText().toString().isEmpty()&&
                    !edtPoUID.getText().toString().isEmpty()&&

                    !edtBankNameAndAccountNumber.getText().toString().isEmpty()&&
                    !edtAccountOwnerName.getText().toString().isEmpty()&&
                    !edtPayee.getText().toString().isEmpty()){
                searchQueryRcpGi();

            }
        });
        btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.findItem(R.id.select_all_data_recap).setIcon(ContextCompat.getDrawable(AddCoActivity.this, R.drawable.ic_outline_select_all));
                recapGiManagementAdapter.clearSelection();
                llBottomSelectionOptions.animate()
                        .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                llBottomSelectionOptions.setVisibility(View.GONE);
                            }
                        });
            }
        });
        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_add_rcp, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_date_range);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        final String userId = helperUtils.getUserId();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coCreatedBy = snapshot.child(userId).child("fullName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddCoActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });

        fabCreateCOR.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                if (!custDocumentID.isEmpty()){

                    String coDateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

                    String coTimeCreated =
                            new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());


                    for (int l = 0; l<recapGiManagementAdapter.getSelected().size();l++){
                        if (recapGiManagementAdapter.getSelected().get(l).getRcpGiStatus().equals(false)){
                            statusSelectedRecap = false;
                        } else{
                            statusSelectedRecap = true;

                        }

                    }

                    if  (!statusSelectedRecap){
                        dialogInterfaceUtils.confirmCreateCashOutProof(context, db,
                                coUID, coDateCreated + " " + coTimeCreated,
                                helperUtils.getUserId(), "","", "", "",
                                suppplieruidVal, roDocumentID, false, false, true, recapGiManagementAdapter);
                    } else {
                        Toast.makeText(context, "Rekap GI yang dipilih telah di Cash Out-kan.", Toast.LENGTH_SHORT).show();
                    }



                    String custNameValReplace = custNameVal.replace(" - ","-");
                    int indexCustNameVal = custNameValReplace.lastIndexOf('-');

                    db.collection("CustomerData").whereEqualTo("custDocumentID",  custDocumentID).get()
                            .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                    CustModel custModel = documentSnapshot2.toObject(CustModel.class);
                                    custAddressVal = custModel.getCustAddress();
                                }
                            });
                } else {
                    Toast.makeText(context, "Mohon cari data Good Issue terlebih dahulu.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // SHOW INIT DATA ON CREATE
        searchQueryAll();

        // CREATE GI MANAGEMENT ADAPTER
        recapGiManagementAdapter = new RcpDataAdapter(this, rcpModelArrayList);

        // HIDE FAB CREATE COR ON CREATE
        fabCreateCOR.animate().translationY(800).setDuration(100).start();

        // NOTIFY REAL-TIME CHANGES AS USER CHOOSE THE ITEM
        //&& !Objects.requireNonNull(spinnerRcpID.getText()).toString().isEmpty()
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                // CHECK IF DATE AND RO/PO NUMBER IS SELECTED
                if (!Objects.requireNonNull(spinnerCustName.getText()).toString().isEmpty()
                        && !spinnerRoUID.getText().toString().isEmpty()
                        && !Objects.requireNonNull(edtPoUID.getText()).toString().isEmpty()

                        && !spinnerSupplierName.getText().toString().isEmpty()){

                    int itemSelectedSize = recapGiManagementAdapter.getSelected().size();
                    float itemSelectedVolume = recapGiManagementAdapter.getSelectedVolume();
                    String itemSelectedSizeVal = String.valueOf(itemSelectedSize).concat(" item terpilih");
                    String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
                    //.concat("IDR "+itemSelectedBuyPriceVal);

                    tvTotalSelectedItem.setText(itemSelectedSizeVal);
                    tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);

                    if (recapGiManagementAdapter.getSelected().size()>0){
                        fabCreateCOR.animate().translationY(0).setDuration(100).start();
                        fabCreateCOR.show();

                        llBottomSelectionOptions.animate()
                                .translationY(0).alpha(1.0f)
                                .setDuration(100)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        llBottomSelectionOptions.setVisibility(View.VISIBLE);
                                    }
                                });
                    } else{


                        fabCreateCOR.animate().translationY(800).setDuration(100).start();
                        fabCreateCOR.hide();
                        llBottomSelectionOptions.animate()
                                .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        llBottomSelectionOptions.setVisibility(View.GONE);
                                    }
                                });
                    }
                }

                handler.postDelayed(this, 100);
            }
        };
        runnable.run();
    }

    private void clearRoPoData(){
        rouidVal = null;
        pouidVal = null;

        spinnerRoUID.setText(null);
        edtPoUID.setText(null);
    }

    private static String getRandomString2(final int sizeOfRandomString) {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public static int math(double d) {
        int c = (int) ((d) + 0.5d);
        double n = d + 0.5d;
        return (n - c) % 2 == 0 ? (int) d : c;
    }

    public void searchQueryRcpGi(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(100);
        }

        showHideFilterComponents(false);

        expandFilterViewValidation();
        TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());

        rouidVal = spinnerRoUID.getText().toString();
        pouidVal = Objects.requireNonNull(edtPoUID.getText()).toString();
        coUID = pouidVal+" - CO - "+getRandomString2(5);


        fabCreateCOR.animate().translationY(0).setDuration(100).start();

        db.collection("RecapData").orderBy("rcpGiDateAndTimeCreated")
                .addSnapshotListener((value, error) -> {
                    rcpModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {

                            RcpModel rcpModel = d.toObject(RcpModel.class);
                            if (rcpModel.getRoDocumentID().contains(roDocumentID) && rcpModel.getRcpGiStatus().equals(false)) {
                                rcpModelArrayList.add(rcpModel);
                            }

                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        if (rcpModelArrayList.size()==0) {
                            fabCreateCOR.hide();
                            nestedScrollView.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                        }
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(rcpModelArrayList);
                    recapGiManagementAdapter = new RcpDataAdapter(context, rcpModelArrayList);
                    rvGoodIssueList.setAdapter(recapGiManagementAdapter);
                });


    }

    public void searchQueryAll(){


        db.collection("RecapData").orderBy("rcpGiDateAndTimeCreated")
                .addSnapshotListener((value, error) -> {
                    rcpModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            RcpModel rcpModel = d.toObject(RcpModel.class);
                            rcpModelArrayList.add(rcpModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        if (rcpModelArrayList.size()==0) {
                            fabCreateCOR.hide();
                            nestedScrollView.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                        }
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }

                    Collections.reverse(rcpModelArrayList);
                    recapGiManagementAdapter = new RcpDataAdapter(context, rcpModelArrayList);
                    rvGoodIssueList.setAdapter(recapGiManagementAdapter);
                });
    }

    private void expandFilterViewValidation() {
        if (expandStatus){
            showHideFilterComponents(true);
            expandStatus=false;
            imgbtnExpandCollapseFilterLayout.setText(R.string.showMore);
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_down, 0);
        } else {
            showHideFilterComponents(false);
            expandStatus=true;
            imgbtnExpandCollapseFilterLayout.setText(R.string.showLess);
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_up, 0);
        }
    }

    private void showHideFilterComponents(Boolean expandStatus) {
        if (expandStatus){
            llWrapFilter.setVisibility(View.GONE);
        } else {
            llWrapFilter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recap_gi_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemShowHideFilter) {
            imgbtnExpandCollapseFilterLayout.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            if (cdvFilter.getVisibility() == View.GONE) {
                cdvFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            } else {
                cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            }
            return true;
        }

        if (item.getItemId() == R.id.select_all_data_recap) {

            if (!isSelectedAll){
                isSelectedAll = true;
                item.setIcon(R.drawable.ic_outline_deselect);
                recapGiManagementAdapter.selectAll();
                llBottomSelectionOptions.animate()
                        .translationY(0).alpha(1.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                llBottomSelectionOptions.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                isSelectedAll = false;
                item.setIcon(R.drawable.ic_outline_select_all);
                recapGiManagementAdapter.clearSelection();
                llBottomSelectionOptions.animate()
                        .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                llBottomSelectionOptions.setVisibility(View.GONE);
                            }
                        });
            }

            return true;
        }
        onBackPressed();
        helperUtils.ACTIVITY_NAME = null;
        custNameVal = "";
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helperUtils.ACTIVITY_NAME = null;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helperUtils.ACTIVITY_NAME = null;
        custNameVal = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        custNameVal = "";

        // HANDLE RESPONSIVE CONTENT
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        /*if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            rvGoodIssueList.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvGoodIssueList.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rvGoodIssueList.setLayoutManager(mLayoutManager);
        }*/

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rvGoodIssueList.setLayoutManager(mLayoutManager);
    }
}