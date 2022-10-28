package com.ptbas.controlcenter.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.helper.ImageAndPositionRenderer;
import com.ptbas.controlcenter.helper.NumberToWords;
import com.ptbas.controlcenter.model.BankAccountModel;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.SupplierModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class UpdateCashOutActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    Context context;
    Helper helper = new Helper();
    Vibrator vibrator;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    CollectionReference refCO = db.collection("CashOutData");
    CollectionReference refRO = db.collection("ReceivedOrderData");
    CollectionReference refSupplier = db.collection("SupplierData");
    CollectionReference refBankAccount = db.collection("BankAccountData");

    double matBuyPrice, transportServiceSellPrice;
    String custDocumentID, rouidVal= "", suppplieruidVal = "", currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal, transportServiceNameVal,
            invPoDate = "", invCustName = "", invPoUID = "", custNameVal = "",
            custAddressVal = "", invPotypeVal = "", coCreatedBy="", coApprovedBy, coAccBy,
            supplierPayee, supplierBankAndAccountNumber, supplierAccountOwnerName, coID, supplierUID;

    String bankDocumentID, coUIDVal, coCreatedByVal, coApprovedByVal, coAccByVal,
            coDateAndTimeCreatedVal, coDateAndTimeApprovedVal, coDateAndTimeAccVal, coDateDeliveryPeriodVal, coPoUIDVal, coSupplierUIDVal,
            coCustomerNameVal, coSupplierNameVal, coAccountOwnerNameVal, coBankNameAndAccountNumberVal, coPayeeVal;

    String bookedStep1By, bookedStep2By, coVerifiedBy;
    Boolean coStatusApprovalVal, coStatusPaymentVal;

    int invPoType;
    float totalUnit;
    Boolean expandStatus = true;

    DialogInterface dialogInterface = new DialogInterface();

    CardView cdvFilter;
    NestedScrollView nestedScrollView;
    TextInputEditText edtSupplierBankNameAndAccountNumber, edtSupplierAccountOwnerName, edtPayee;

    // edtBankNameAndAccountNumber
    TextView tvCreatedBy, tvDateAndTimeCreated,
            tvCoUID, tvCustomerName, tvRoUID, tvPoUID, tvDateDeliveryPeriod;
    AutoCompleteTextView spinnerSupplierName;
    Button imgbtnExpandCollapseFilterLayout;
    LinearLayout llNoData, llWrapFilter, llWrapSupplierDetail;

    ImageButton btnResetSupplier;

    ExtendedFloatingActionButton fabPrint;

    DatePickerDialog datePicker;

    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;

    List<String> arrayListRoUID, arrayListPoUID, arrayListSupplierUID;
    List<ProductItems> productItemsList;
    List<String> customerName, supplierName;

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontNormal, fontNormalSmall, fontNormalSmall2, fontNormalSmallItalic,
            fontMedium, fontMediumWhite, fontBold, fontTransparent;
    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");


    TextView tvMatName,tvCubicationTotal, tvTotalDue, tvStatusBookedStep1, tvStatusBookedStep2, tvStatus;
    TextInputEditText edtBookedStep1By, edtBookedStep1Date, edtBookedStep2By, edtBookedStep2Date;
    SwitchCompat statusSwitchBookedStep1, statusSwitchBookedStep2, statusSwitch;
    AutoCompleteTextView spinnerBankAccount;
    TextInputEditText edtAccountOwnerName, edtVerifiedBy, edtTransferProofReference, edtDatePaid;


    float totalUnitFinal;
    double totalAmountForMaterials, totalAmountForTransportService, totalDue, totalDueForTransportService;

    String finalPaidDate, finalBookedStep1Date, finalBookedStep2Date, transferProofReference;

    ImageButton btnResetBankAccount, btnDatePaidReset, btnBookedStep1DateReset, btnBookedStep2DateReset;

    List<String> bankAccountDocumentID, bankAccount;

    String bankAccountID = "", bankNameVal, bankAccountNumberVal, bankAccountOwnerNameVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cash_out);

        bankAccountDocumentID = new ArrayList<>();
        bankAccount = new ArrayList<>();

        LangUtils.setLocale(this, "en");

        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);

        btnResetSupplier = findViewById(R.id.btnResetSupplier);

        spinnerSupplierName = findViewById(R.id.spinnerSupplierName);

        llWrapSupplierDetail = findViewById(R.id.llWrapSupplierDetail);
        llWrapFilter = findViewById(R.id.llWrapFilter);
        llNoData = findViewById(R.id.ll_no_data);
        edtSupplierBankNameAndAccountNumber = findViewById(R.id.edtSupplierBankNameAndAccountNumber);
        edtSupplierAccountOwnerName = findViewById(R.id.edtSupplierAccountOwnerName);
        edtPayee = findViewById(R.id.edtPayee);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        fabPrint = findViewById(R.id.fabPrint);

        tvCoUID = findViewById(R.id.tvCoUID);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvRoUID = findViewById(R.id.tvRoUID);
        tvPoUID = findViewById(R.id.tvPoUID);
        tvDateDeliveryPeriod = findViewById(R.id.tvDateDeliveryPeriod);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvDateAndTimeCreated = findViewById(R.id.tvDateAndTimeCreated);

        rvGoodIssueList = findViewById(R.id.rvItemList);

        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);

        vibrator  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        helper.ACTIVITY_NAME = "UPDATE";

        spinnerSupplierName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


        tvMatName = findViewById(R.id.tvMatName);
        tvCubicationTotal = findViewById(R.id.tvCubicationTotal);
        tvTotalDue = findViewById(R.id.tvTotalDue);
        tvStatusBookedStep1 = findViewById(R.id.tvStatusBookedStep1);
        tvStatusBookedStep2 = findViewById(R.id.tvStatusBookedStep2);
        tvStatus = findViewById(R.id.tvStatus);
        edtBookedStep1By = findViewById(R.id.edtBookedStep1By);
        edtBookedStep1Date = findViewById(R.id.edtBookedStep1Date);
        statusSwitchBookedStep1 = findViewById(R.id.statusSwitchBookedStep1);
        edtBookedStep2By = findViewById(R.id.edtBookedStep2By);
        edtBookedStep2Date = findViewById(R.id.edtBookedStep2Date);
        statusSwitchBookedStep2 = findViewById(R.id.statusSwitchBookedStep2);
        spinnerBankAccount = findViewById(R.id.spinnerBankAccount);
        edtAccountOwnerName = findViewById(R.id.edtAccountOwnerName);
        edtVerifiedBy = findViewById(R.id.edtVerifiedBy);
        edtTransferProofReference = findViewById(R.id.edtTransferProofReference);
        edtDatePaid = findViewById(R.id.edtDatePaid);
        statusSwitch = findViewById(R.id.statusSwitch);

        btnResetBankAccount = findViewById(R.id.btnResetBankAccount);

        btnDatePaidReset = findViewById(R.id.btnDatePaidReset);
        btnBookedStep1DateReset = findViewById(R.id.btnBookedStep1DateReset);
        btnBookedStep2DateReset = findViewById(R.id.btnBookedStep2DateReset);

        edtDatePaid.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(UpdateCashOutActivity.this,
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

                        finalPaidDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtDatePaid.setText(finalPaidDate);
                        edtDatePaid.setError(null);
                        btnDatePaidReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });
        edtBookedStep1Date.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(UpdateCashOutActivity.this,
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

                        finalBookedStep1Date = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtBookedStep1Date.setText(finalBookedStep1Date);
                        edtBookedStep1Date.setError(null);
                        btnBookedStep1DateReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });
        edtBookedStep2Date.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(UpdateCashOutActivity.this,
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

                        finalBookedStep2Date = year + "-" +monthStrVal + "-" + dayStrVal;
                        edtBookedStep2Date.setText(finalBookedStep2Date);
                        edtBookedStep2Date.setError(null);
                        btnBookedStep2DateReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });

        btnDatePaidReset.setOnClickListener(view -> resetDate());
        btnBookedStep1DateReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBookedStep1Date();
            }
        });
        btnBookedStep2DateReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBookedStep2Date();
            }
        });

        loadCashOutData();

        db.collection("BankAccountData").whereEqualTo("bankType", "PERUSAHAAN")
                .addSnapshotListener((value, error) -> {
                    bankAccount.clear();
                    bankAccountDocumentID.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String bankName = Objects.requireNonNull(d.get("bankName")).toString();
                                String bankAccountNumber = Objects.requireNonNull(d.get("bankAccountNumber")).toString();

                                String a = bankName.replace(" - ","-");
                                int b = a.lastIndexOf('-');

                                bankAccount.add(a.substring(0,b)+" - "+bankAccountNumber);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateCashOutActivity.this, R.layout.style_spinner, bankAccount);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerBankAccount.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(UpdateCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        customerName = new ArrayList<>();
        supplierName = new ArrayList<>();
        arrayListRoUID = new ArrayList<>();
        arrayListPoUID = new ArrayList<>();
        arrayListSupplierUID = new ArrayList<>();

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
        fontNormalSmall2 = new Font(baseNormal, 6, Font.NORMAL, BaseColor.BLACK);
        fontNormalSmallItalic = new Font(baseNormal, 8, Font.ITALIC, BaseColor.BLACK);
        fontMedium = new Font(baseMedium, 10, Font.NORMAL, BaseColor.BLACK);
        fontMediumWhite = new Font(baseMedium, 10, Font.NORMAL, BaseColor.WHITE);
        fontBold = new Font(baseBold, 20, Font.NORMAL, BaseColor.BLACK);
        fontTransparent = new Font(baseNormal, 20, Font.NORMAL, null);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        btnResetSupplier.setColorFilter(color);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Rincian Cash Out");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateCashOutActivity.this, R.layout.style_spinner, supplierName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerSupplierName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(UpdateCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnResetSupplier.setOnClickListener(view -> {
            btnResetSupplier.setVisibility(View.GONE);
            llWrapSupplierDetail.setVisibility(View.GONE);
            spinnerSupplierName.setText(null);
            edtSupplierBankNameAndAccountNumber.setText(null);
            edtSupplierAccountOwnerName.setText(null);
            edtPayee.setText(null);
            spinnerSupplierName.requestFocus();
        });

        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            expandFilterViewValidation();
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        spinnerBankAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerBankAccount.setError(null);
                btnResetBankAccount.setVisibility(View.VISIBLE);
                String selectedSpinnerBankAccount = (String) adapterView.getItemAtPosition(i);

                String a = selectedSpinnerBankAccount.replace(" - ","-");
                int b = a.lastIndexOf('-');

                db.collection("BankAccountData").whereEqualTo("bankAccountNumber", a.substring(b+1)).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                BankAccountModel bankAccountModel = documentSnapshot.toObject(BankAccountModel.class);
                                bankAccountID = bankAccountModel.getBankAccountID();
                                bankNameVal = bankAccountModel.getBankName();
                                bankAccountNumberVal = bankAccountModel.getBankAccountNumber();
                                bankAccountOwnerNameVal = bankAccountModel.getBankAccountOwnerName();

                                edtAccountOwnerName.setText(bankAccountModel.getBankAccountOwnerName());
                            }
                        });

            }
        });

        spinnerSupplierName.setOnItemClickListener((adapterView, view, i, l) -> {
            View viewLayout = UpdateCashOutActivity.this.getCurrentFocus();
            if (viewLayout != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
            }
            spinnerSupplierName.setError(null);
            String selectedSpinnerSupplierID = (String) adapterView.getItemAtPosition(i);

            //Toast.makeText(context, selectedSpinnerSupplierID, Toast.LENGTH_SHORT).show();

            llWrapSupplierDetail.setVisibility(View.VISIBLE);
            btnResetSupplier.setVisibility(View.VISIBLE);

            db.collection("SupplierData").whereEqualTo("supplierName", selectedSpinnerSupplierID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            SupplierModel supplierModel = documentSnapshot.toObject(SupplierModel.class);
                            supplierModel.setSupplierID(documentSnapshot.getId());
                            suppplieruidVal = supplierModel.getSupplierID();
                            supplierPayee = supplierModel.getSupplierPayeeName();
                            String bankAlias = supplierModel.getBankName();
                            String bankAliasReplace = bankAlias.replace(" - ","-");
                            int indexBankAliasVal = bankAliasReplace.lastIndexOf('-');
                            supplierBankAndAccountNumber = bankAliasReplace.substring(0, indexBankAliasVal) + " - " + supplierModel.getBankAccountNumber() ;
                            supplierAccountOwnerName = supplierModel.getBankAccountOwnerName();
                        }
                        edtSupplierBankNameAndAccountNumber.setText(supplierBankAndAccountNumber);
                        edtSupplierAccountOwnerName.setText(supplierAccountOwnerName);
                        edtPayee.setText(supplierPayee);
                    });
        });

        final String userId = helper.getUserId();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coCreatedBy = snapshot.child(userId).child("fullName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateCashOutActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });

        fabPrint.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                    totalUnit += giManagementAdapter.getSelected().get(i).getGiVhlCubication();
                }

                double totalIDR = matBuyPrice *Double.parseDouble(df.format(totalUnit));

                dialogInterface.confirmPrintCo(context, db, goodIssueModelArrayList,
                        coUIDVal, coDateAndTimeCreatedVal, coCustomerNameVal, coDateAndTimeApprovedVal,coApprovedByVal, coDateAndTimeAccVal, coAccByVal,
                        coSupplierUIDVal, roPoCustNumber, coDateDeliveryPeriodVal, coStatusApprovalVal, coStatusPaymentVal, totalIDR);

            }
        });

        // SHOW INIT DATA ON CREATE
        searchQueryAll();

        // CREATE GI MANAGEMENT ADAPTER
        giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);
    }

    private void loadCashOutData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage("Memproses");
            pd.setCancelable(false);
            DecimalFormat df = new DecimalFormat("0.00");

            coID = extras.getString("key");

            refCO.whereEqualTo("coDocumentID", coID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            CashOutModel cashOutModel = documentSnapshot.toObject(CashOutModel.class);

                            coUIDVal = cashOutModel.getCoUID();
                            coCreatedBy = cashOutModel.getCoCreatedBy();
                            coApprovedBy = cashOutModel.getCoApprovedBy();
                            coAccBy = cashOutModel.getCoAccBy();
                            tvCoUID.setText(coUIDVal);

                            edtBookedStep1By.setText(cashOutModel.getCoBookedStep1By());
                            edtBookedStep2By.setText(cashOutModel.getCoBookedStep2By());
                            edtBookedStep1Date.setText(cashOutModel.getCoBookedStep1Date());
                            edtBookedStep2Date.setText(cashOutModel.getCoBookedStep2Date());
                            bankDocumentID = cashOutModel.getBankDocumentID();

                            edtTransferProofReference.setText(cashOutModel.getCoTransferReference());
                            edtDatePaid.setText(cashOutModel.getCoDateAndTimeACC());


                            tvTotalDue.setText("IDR "+currencyFormat(df.format(cashOutModel.getCoTotal())));

                            coVerifiedBy = cashOutModel.getCoAccBy();
                            bookedStep1By = cashOutModel.getCoBookedStep1By();
                            bookedStep2By = cashOutModel.getCoBookedStep2By();

                            if (!coVerifiedBy.isEmpty()){
                                edtVerifiedBy.setFocusable(false);
                                statusSwitch.setEnabled(false);
                                statusSwitch.setChecked(true);
                                tvStatus.setText("Lunas");
                            } else{
                                edtVerifiedBy.setText(null);
                                statusSwitch.setEnabled(true);
                                statusSwitch.setChecked(false);
                                tvStatus.setText("Belum Lunas");
                            }

                            statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                                pd.show();
                                if (statusSwitch.isChecked()) {
                                    refCO.document(coID).update("coAccBy", helper.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                pd.dismiss();
                                                loadCashOutData();
                                            }
                                        }
                                    });
                                } else {
                                    refCO.document(coID).update("coAccBy", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                pd.dismiss();
                                                loadCashOutData();
                                            }
                                        }
                                    });
                                }
                            });

                            if (!bookedStep1By.isEmpty()){
                                edtBookedStep1Date.setFocusable(false);
                                edtBookedStep1Date.setOnClickListener(null);

                                edtDatePaid.setFocusable(false);
                                edtDatePaid.setOnClickListener(null);

                                spinnerBankAccount.setFocusable(false);
                                spinnerBankAccount.setAdapter(null);

                                edtAccountOwnerName.setFocusable(false);
                                edtTransferProofReference.setFocusable(false);
                                btnDatePaidReset.setVisibility(View.GONE);
                                edtBookedStep1By.setFocusable(false);
                                statusSwitchBookedStep1.setChecked(true);
                                statusSwitchBookedStep1.setEnabled(false);

                                tvStatusBookedStep1.setText("Selesai");
                                btnBookedStep1DateReset.setVisibility(View.GONE);
                                btnResetBankAccount.setFocusable(false);

                            } else{
                                statusSwitchBookedStep1.setChecked(false);
                                tvStatusBookedStep1.setText("Belum Selesai");
                            }

                            statusSwitchBookedStep1.setOnCheckedChangeListener((compoundButton, b) -> {
                                pd.show();
                                if (edtBookedStep1Date.getText().toString().isEmpty()){
                                    edtBookedStep1Date.setError("Tanggal pembukuan tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitchBookedStep1.setChecked(false);
                                } else if (edtDatePaid.getText().toString().isEmpty()){
                                    edtDatePaid.requestFocus();
                                    edtDatePaid.setError("Tanggal lunas tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitchBookedStep1.setChecked(false);
                                } else if (spinnerBankAccount.getText().toString().isEmpty()){
                                    spinnerBankAccount.requestFocus();
                                    spinnerBankAccount.setError("Nomor rekening tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitchBookedStep1.setChecked(false);
                                } else if (edtTransferProofReference.getText().toString().isEmpty()){
                                    edtTransferProofReference.requestFocus();
                                    edtTransferProofReference.setError("Nomor referensi tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitchBookedStep1.setChecked(false);
                                } else {
                                    transferProofReference = edtTransferProofReference.getText().toString();
                                    //coBankNameAndAccountNumberVal = edtBankNameAndAccountNumber.getText().toString();

                                    if (statusSwitchBookedStep1.isChecked()) {
                                        refCO.document(coID).update("coBookedStep1Date", finalBookedStep1Date);
                                        refCO.document(coID).update("bankDocumentID", bankAccountID);
                                        refCO.document(coID).update("coDateAndTimeACC", finalPaidDate);
                                        refCO.document(coID).update("coTransferReference", transferProofReference);
                                        refCO.document(coID).update("coBookedStep1By", helper.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    edtBookedStep1Date.setError(null);
                                                    edtDatePaid.setError(null);
                                                    spinnerBankAccount.setError(null);
                                                    edtTransferProofReference.setError(null);
                                                    btnResetBankAccount.setVisibility(View.GONE);
                                                    pd.dismiss();
                                                    loadCashOutData();
                                                }
                                            }
                                        });
                                    } else {
                                        refCO.document(coID).update("coBookedStep1Date", "");
                                        refCO.document(coID).update("bankDocumentID", "");
                                        refCO.document(coID).update("coDateAndTimeACC", "");
                                        refCO.document(coID).update("coTransferReference", "");
                                        refCO.document(coID).update("coBookedStep1By", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    loadCashOutData();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            if (!bookedStep2By.isEmpty()){
                                edtBookedStep2Date.setFocusable(false);
                                btnBookedStep2DateReset.setVisibility(View.GONE);
                                edtBookedStep2By.setFocusable(false);
                                statusSwitchBookedStep2.setChecked(true);
                                statusSwitchBookedStep2.setEnabled(false);
                                tvStatusBookedStep2.setText("Selesai");
                            } else{
                                statusSwitchBookedStep2.setChecked(false);
                                statusSwitchBookedStep2.setEnabled(true);
                                tvStatusBookedStep2.setText("Belum Selesai");
                                /*edtBookedStep2By.setText(null);
                                edtBookedStep2Date.setText(null);*/
                            }

                            statusSwitchBookedStep2.setOnCheckedChangeListener((compoundButton, b) -> {
                                pd.show();
                                if (edtBookedStep2Date.getText().toString().isEmpty()){
                                    edtBookedStep2Date.setError("Tanggal pembukuan tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitchBookedStep2.setChecked(false);
                                } else {
                                    if (statusSwitchBookedStep2.isChecked()) {
                                        refCO.document(coID).update("coBookedStep2Date", finalBookedStep2Date);
                                        refCO.document(coID).update("coBookedStep2By", helper.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    edtBookedStep2Date.setError(null);
                                                    pd.dismiss();
                                                    loadCashOutData();
                                                }
                                            }
                                        });
                                    } else {
                                        refCO.document(coID).update("coBookedStep2Date", finalBookedStep2Date);
                                        refCO.document(coID).update("coBookedStep2By", helper.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    loadCashOutData();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    coAccByVal = snapshot.child(coAccBy).child("fullName").getValue(String.class);
                                    coCreatedByVal = snapshot.child(coCreatedBy).child("fullName").getValue(String.class);
                                    coApprovedByVal = snapshot.child(coApprovedBy).child("fullName").getValue(String.class);

                                    tvCreatedBy.setText(coCreatedByVal);

                                    bookedStep1By = snapshot.child(Objects.requireNonNull(documentSnapshot.get("coBookedStep1By", String.class))).child("fullName").getValue(String.class);
                                    edtBookedStep1By.setText(bookedStep1By);

                                    bookedStep2By = snapshot.child(Objects.requireNonNull(documentSnapshot.get("coBookedStep2By", String.class))).child("fullName").getValue(String.class);
                                    edtBookedStep2By.setText(bookedStep2By);

                                    coAccBy = snapshot.child(Objects.requireNonNull(documentSnapshot.get("coAccBy", String.class))).child("fullName").getValue(String.class);
                                    edtVerifiedBy.setText(coAccBy);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UpdateCashOutActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            coDateAndTimeCreatedVal = cashOutModel.getCoDateAndTimeCreated();
                            tvDateAndTimeCreated.setText(coDateAndTimeCreatedVal);

                            coDateAndTimeApprovedVal = cashOutModel.getCoDateAndTimeApproved();
                            coDateAndTimeAccVal = cashOutModel.getCoDateAndTimeACC();
                            coDateDeliveryPeriodVal = cashOutModel.getCoDateDeliveryPeriod();
                            coPoUIDVal = cashOutModel.getRoDocumentID();

                            tvDateDeliveryPeriod.setText(coDateDeliveryPeriodVal);

                            supplierUID = cashOutModel.getCoSupplier();

                            coStatusApprovalVal = cashOutModel.getCoStatusApproval();
                            coStatusPaymentVal = cashOutModel.getCoStatusPayment();

                            refRO.whereEqualTo("roDocumentID", coPoUIDVal).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (productItemsList != null){
                                                productItemsList.clear();
                                            }

                                            transportServiceSellPrice = 0;
                                            matBuyPrice = 0;

                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                                                receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                                                custDocumentID = receivedOrderModel.getRoDocumentID();

                                                matTypeVal = receivedOrderModel.getRoMatType();
                                                roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                                                custNameVal = receivedOrderModel.getCustDocumentID();
                                                currencyVal = receivedOrderModel.getRoCurrency();
                                                invCustName = receivedOrderModel.getCustDocumentID();
                                                invPoUID = receivedOrderModel.getRoUID();
                                                invPoDate = receivedOrderModel.getRoDateCreated();
                                                invPoType = receivedOrderModel.getRoType();

                                                tvPoUID.setText(roPoCustNumber);
                                                tvRoUID.setText(invPoUID);

                                                db.collection("CustomerData").whereEqualTo("custDocumentID", invCustName).get()
                                                        .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                                            for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                                                CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                                                custNameVal = customerModel.getCustName();
                                                                custAddressVal = customerModel.getCustAddress();
                                                                tvCustomerName.setText(custNameVal);
                                                            }
                                                        });

                                                if (invPoType == 0){
                                                    invPotypeVal = "MATERIAL + JASA ANGKUT";
                                                }
                                                if (invPoType == 1){
                                                    invPotypeVal = "MATERIAL SAJA";
                                                }
                                                if (invPoType == 2){
                                                    invPotypeVal = "JASA ANGKUT SAJA";
                                                }

                                                /*HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                                                for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                                    productItemsList = e.getValue();
                                                    for (int i = 0; i<productItemsList.size();i++){
                                                        matNameVal = productItemsList.get(i).getMatName();
                                                        matBuyPrice = productItemsList.get(i).getMatBuyPrice();

                                                    }

                                                }*/

                                                HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                                                for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                                    productItemsList = e.getValue();
                                                    for (int i = 0; i<productItemsList.size();i++){
                                                        if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                                            transportServiceNameVal = productItemsList.get(0).getMatName();
                                                            transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();
                                                        } else {
                                                            matNameVal = productItemsList.get(i).getMatName();
                                                            //matCubication = productItemsList.get(i).getMatQuantity();
                                                            matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                                        }
                                                    }
                                                }

                                                tvMatName.setText(matNameVal);

                                                totalUnitFinal = 0;
                                                for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                                                    totalUnitFinal += goodIssueModelArrayList.get(i).getGiVhlCubication();
                                                }
                                                tvCubicationTotal.setText(totalUnitFinal+" m3");

                                                // TOTAL AMOUNT CALCULATION
                                                totalAmountForMaterials = matBuyPrice*totalUnitFinal;
                                                totalAmountForTransportService = transportServiceSellPrice*totalUnitFinal;
                                                totalDue = totalAmountForMaterials+totalAmountForTransportService;
                                                totalDueForTransportService = totalAmountForTransportService;
                                            }
                                        }
                                    });


                            refSupplier.whereEqualTo("supplierID", supplierUID).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                coSupplierNameVal = documentSnapshot.get("supplierName", String.class);
                                                coAccountOwnerNameVal = documentSnapshot.get("bankAccountOwnerName", String.class);
                                                spinnerSupplierName.setText(coSupplierNameVal);
                                                edtSupplierAccountOwnerName.setText(coAccountOwnerNameVal);

                                                String bankAlias = documentSnapshot.get("bankName", String.class);
                                                String bankAliasReplace = bankAlias.replace(" - ","-");
                                                int indexBankAliasVal = bankAliasReplace.lastIndexOf('-');
                                                coBankNameAndAccountNumberVal = bankAliasReplace.substring(0, indexBankAliasVal) + " - " + documentSnapshot.get("bankAccountNumber", String.class);
                                                edtSupplierBankNameAndAccountNumber.setText(coBankNameAndAccountNumberVal);
                                                //.setText(documentSnapshot.get("bankName", String.class) + " - " + documentSnapshot.get("bankAccountNumber", String.class));
                                                coPayeeVal = documentSnapshot.get("supplierPayeeName", String.class);
                                                edtPayee.setText(coPayeeVal);
                                            }
                                        }
                                    });

                            refBankAccount.whereEqualTo("bankAccountID", bankDocumentID).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                BankAccountModel bankAccountModel = documentSnapshot.toObject(BankAccountModel.class);
                                                bankAccountModel.setBankAccountID(documentSnapshot.getId());

                                                bankNameVal = bankAccountModel.getBankName();
                                                bankAccountNumberVal = bankAccountModel.getBankAccountNumber();
                                                bankAccountOwnerNameVal = bankAccountModel.getBankAccountOwnerName();

                                                String a = bankNameVal.replace(" - ","-");
                                                int b = a.lastIndexOf('-');

                                                spinnerBankAccount.setText(a.substring(0,b)+" - "+bankAccountNumberVal);
                                                edtAccountOwnerName.setText(bankAccountOwnerNameVal);
                                            }
                                        }
                                    });
                        }
                    });

        } else {
            Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetBookedStep1Date() {
        edtBookedStep1Date.setText(null);
        btnBookedStep1DateReset.setVisibility(View.GONE);
    }

    private void resetBookedStep2Date() {
        edtBookedStep2Date.setText(null);
        btnBookedStep2DateReset.setVisibility(View.GONE);
    }

    private void resetDate() {
        edtDatePaid.setText(null);
        btnDatePaidReset.setVisibility(View.GONE);
    }

    private void clearRoPoData(){
        rouidVal = null;
        pouidVal = null;

    }

    private static String getRandomString() {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(5);
        for(int i = 0; i< 5; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    private static String getRandomString2(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public void createCashOutProofPDF(String dest){
        if (new File(dest).exists()){
            new File(dest).deleteOnExit();
        }

        try {
            // RECAP DATA -- landscape
            /*float width = mmToPt(248);
            float height = mmToPt(157);
            Rectangle f4Landscape = new Rectangle(width, height);
            Document document = new Document(f4Landscape, 10, 10, 10, 10);
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();
            document.addAuthor("PT BAS");
            document.addCreator("BAS Control Center");
            document.addCreationDate();
            addInvTtl(document);
            addInvMainContent(document);*/
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();
            document.setPageSize(PageSize.A4);
            document.addAuthor("PT BAS");
            document.addCreator("BAS Control Center");
            document.addCreationDate();
            // CREATE INVOICE PAGE
            addCoTtl(document);
            addCoMainContent(document);
            document.close();
            dialogInterface.coPrintedInfo(context, dest);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void addCoTtl(Document document) throws DocumentException {
        Paragraph preface1 = new Paragraph();
        Chunk title = new Chunk("CASH-OUT (KEPERLUAN INTERNAL)", fontBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface1.setAlignment(Element.ALIGN_CENTER);
        preface1.setSpacingAfter(20);
        document.add(preface1);
    }
    public static PdfPCell cellTxtNrml(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
    public static PdfPCell cellTxtNoBrdrNrml(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
    public static PdfPCell cellTxtNoBrdrNrmlWthPadLft(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setPaddingLeft(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
    public static PdfPCell cellTxtNoBrdrNrmlMainContent(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
    public static PdfPCell cellTxtBrdrNrmlMainContent(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        return cell;
    }
    public static PdfPCell cellColHeader(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(baseColorLightGrey);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setPadding(5);
        return cell;
    }
    public PdfPCell cellColHeaderNoBrdr(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_table_column_blue_pale);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 30, stream);
        Image img = null;
        byte[] byteArray = stream.toByteArray();

        try {
            img = Image.getInstance(byteArray);
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        }
        cell.setCellEvent(new ImageAndPositionRenderer(img));
        cell.setPaddingTop(1);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(7);
        return cell;
    }
    private void addCoMainContent(Document document) throws DocumentException{
        try {
            String invDateCreated =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
            String invTimeCreated =
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            Paragraph paragraphBlank = new Paragraph(" ");

            Paragraph paragraphInvDateCreated =
                    new Paragraph("Tanggal cetak: "
                            +invDateCreated+" "+invTimeCreated+" WIB, oleh: "+coCreatedByVal, fontNormalSmallItalic);
            paragraphInvDateCreated.setAlignment(Element.ALIGN_RIGHT);
            paragraphInvDateCreated.setSpacingAfter(5);

            // INIT IMAGE BCA QR CODE
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bca_qr_bas);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();
            try {
                img = Image.getInstance(byteArray);
                img.setAlignment(Image.TEXTWRAP);
                img.scaleAbsolute(80f, 80f);
            } catch (BadElementException | IOException e) {
                e.printStackTrace();
            }

// TOTAL UNIT CALCULATION
            float totalUnitFinal = 0;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                totalUnitFinal += goodIssueModelArrayList.get(i).getGiVhlCubication();
            }
            // TOTAL UNIT CALCULATION
            //float totalUnitFinal = totalUnit;
            /*for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                totalUnitFinal += goodIssueModelArrayList.get(i).getGiVhlCubication();
            }*/

            // TOTAL AMOUNT CALCULATION
            double totalAmountForMaterials = matBuyPrice *totalUnitFinal;
            double totalAmountForTransportService = transportServiceSellPrice*totalUnitFinal;
            /*double taxPPN = (0.11)*totalAmountForMaterials;
            double taxPPH = (0.02)*totalAmountForTransportService;*/
            //double totalDue = totalAmountForMaterials+totalAmountForTransportService;
            //double totalDueForTransportService = totalAmountForTransportService;

            // INIT TABLE
            PdfPTable tblInvSection1 = new PdfPTable(7);
            PdfPTable tblInvSection2 = new PdfPTable(7);
            PdfPTable tblInvSection3 = new PdfPTable(7);
            PdfPTable tblInvSection4 = new PdfPTable(7);
            PdfPTable tblInvSection5 = new PdfPTable(4);
            PdfPTable tblInvSection6 = new PdfPTable(4);
            PdfPTable tblInvSectionDeliveryPeriod = new PdfPTable(2);
            PdfPTable tblInvSection7 = new PdfPTable(4);
            PdfPTable tblInvSection8 = new PdfPTable(4);
            PdfPTable tblInvSection9 = new PdfPTable(4);
            PdfPTable tblInvSection10 = new PdfPTable(2);
            PdfPTable tblInvSection11 = new PdfPTable(2);
            PdfPTable tblInvSection12 = new PdfPTable(5);
            PdfPTable tblInvSection13 = new PdfPTable(6);

            // WIDTH PERCENTAGE CONFIG
            tblInvSection1.setWidthPercentage(100);
            tblInvSection2.setWidthPercentage(100);
            tblInvSection3.setWidthPercentage(100);
            tblInvSection4.setWidthPercentage(100);
            tblInvSection5.setWidthPercentage(100);
            tblInvSection6.setWidthPercentage(100);
            tblInvSection7.setWidthPercentage(100);
            tblInvSectionDeliveryPeriod.setWidthPercentage(100);
            tblInvSection8.setWidthPercentage(100);
            tblInvSection9.setWidthPercentage(100);
            tblInvSection10.setWidthPercentage(100);
            tblInvSection11.setWidthPercentage(100);
            tblInvSection12.setWidthPercentage(100);
            tblInvSection13.setWidthPercentage(100);

            // WIDTH FLOAT CONFIG
            tblInvSection1.setWidths(new float[]{6,1,7,1,6,1,9}); //7 COLS
            tblInvSection2.setWidths(new float[]{4,1,9,1,4,1,11}); //7 COLS
            tblInvSection3.setWidths(new float[]{4,1,9,1,4,1,11}); //7 COLS
            tblInvSection4.setWidths(new float[]{4,1,9,1,4,1,11}); //5 COLS
            tblInvSection5.setWidths(new float[]{3,3,3,4}); //5 COLS
            tblInvSection6.setWidths(new float[]{3,3,3,4}); //5 COLS
            tblInvSection7.setWidths(new float[]{3,3,3,4}); //2 COLS
            tblInvSectionDeliveryPeriod.setWidths(new float[]{1,1}); //2 COLS
            tblInvSection8.setWidths(new float[]{3,3,3,4}); //5 COLS
            tblInvSection9.setWidths(new float[]{3,3,3,4}); //5 COLS
            tblInvSection10.setWidths(new float[]{2,10}); //2 COLS
            tblInvSection11.setWidths(new float[]{9,1}); //2 COLS
            tblInvSection12.setWidths(new float[]{4,4,4,4,4}); //6 COLS
            tblInvSection13.setWidths(new float[]{2,2,4,4,4,4,}); //6 COLS

            // ADD CELL TO RESPECTIVE TABLES
            tblInvSection1.addCell(cellColHeaderNoBrdr(
                    new Paragraph("PEMBAYARAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellColHeaderNoBrdr(
                    new Paragraph("DETAIL CO", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));

            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("KE REKENING", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(coBankNameAndAccountNumberVal, fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("No. CO", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(coUIDVal, fontNormal),
                    Element.ALIGN_LEFT));

            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("REK. A/N - DIBAYAR KE", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(coAccountOwnerNameVal + " - " + coPayeeVal , fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("No. PO"+"\n"+"Tanggal PO"+"\n"+"Jenis PO", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":"+"\n"+":"+"\n"+":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(roPoCustNumber+"\n"+invPoDate+"\n"+invPotypeVal, fontNormal),
                    Element.ALIGN_LEFT));

            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Item# / Deskripsi", fontMedium), Element.ALIGN_LEFT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Harga Satuan", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Jumlah (Unit)", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Jumlah", fontMedium), Element.ALIGN_RIGHT));

            /*for (int i = 0; i<productItemsList.size();i++){
            }*/

            /*List<String> datePeriod = new ArrayList<>();
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
            }*/
            /*HashSet<String> filter = new HashSet(datePeriod);
            ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);*/

            tblInvSectionDeliveryPeriod.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("Pengiriman Tanggal: "+coDateDeliveryPeriodVal, fontNormal), Element.ALIGN_LEFT));
            tblInvSectionDeliveryPeriod.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));


            for (int i = 0; i < productItemsList.size(); i++) {
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(matNameVal, fontNormal), Element.ALIGN_LEFT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal + " " + currencyFormat(df.format(matBuyPrice)), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal + " " + currencyFormat(df.format(totalAmountForMaterials)), fontNormal), Element.ALIGN_RIGHT));
            }

            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(transportServiceNameVal, fontNormal), Element.ALIGN_LEFT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(transportServiceSellPrice)), fontNormal), Element.ALIGN_RIGHT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmountForTransportService)), fontNormal), Element.ALIGN_RIGHT));





            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("JUMLAH PEMBAYARAN (PEMBULATAN)", fontMedium), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(math(totalAmountForMaterials))), fontMedium), Element.ALIGN_RIGHT));

            tblInvSection10.addCell(cellColHeaderNoBrdr(
                    new Paragraph("TERBILANG", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection10.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));




            //String totalDueVal = dfRound.format(totalDue);
            //int indexTotalDueComaVal = totalDueVal.lastIndexOf('.');


            tblInvSection11.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(
                            NumberToWords.convert(math(totalAmountForMaterials)) +
                                    " Rupiah", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection11.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));


            tblInvSection12.addCell(cellColHeader(
                    new Paragraph("PEMBUKUAN", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellColHeader(
                    new Paragraph("MENGETAHUI", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellColHeader(
                    new Paragraph("MENYETUJUI", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellColHeader(
                    new Paragraph("KASIR", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellColHeader(
                    new Paragraph("PENERIMA", fontMedium),
                    Element.ALIGN_LEFT));


            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph(bookedStep1By+"\n("+edtBookedStep1Date.getText()+")", fontNormalSmall2),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph(bookedStep2By+"\n("+edtBookedStep2Date.getText()+")", fontNormalSmall2),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontNormalSmall2),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontNormalSmall2),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph(coAccBy+"\n("+edtDatePaid.getText()+")", fontNormalSmall2),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontNormalSmall2),
                    Element.ALIGN_LEFT));


            document.add(tblInvSection1);
            document.add(tblInvSection2);
            document.add(tblInvSection3);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection5);
            //document.add(tblInvSection7);
            document.add(tblInvSectionDeliveryPeriod);
            document.add(tblInvSection6);
            document.add(tblInvSection9);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection10);
            document.add(tblInvSection11);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection12);
            document.add(tblInvSection13);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(paragraphInvDateCreated);

        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
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

    private void searchQueryAll(){
        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (Objects.equals(item.child("giCashedOutTo").getValue(), coID)) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                            nestedScrollView.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);

                        }
                    }
                    if (goodIssueModelArrayList.size()==0) {
                        fabPrint.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    fabPrint.hide();
                    nestedScrollView.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void expandFilterViewValidation() {
        if (expandStatus){
            showHideFilterComponents(true);
            expandStatus=false;
            imgbtnExpandCollapseFilterLayout.setText("Sembunyikan Daftar Good Issue");
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_down, 0);
        } else {
            showHideFilterComponents(false);
            expandStatus=true;
            imgbtnExpandCollapseFilterLayout.setText("Lihat Daftar Good Issue");
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            //imgbtnExpandCollapseFilterLayout.setVisibility(View.VISIBLE);
            //TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
            /*if (cdvFilter.getVisibility() == View.GONE) {
                cdvFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            } else {
                cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            }*/
            //!!TODO SAVE CHANGES TO
            return true;
        }
        finish();
        helper.ACTIVITY_NAME = null;
        custNameVal = "";
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helper.ACTIVITY_NAME = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.ACTIVITY_NAME = null;
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