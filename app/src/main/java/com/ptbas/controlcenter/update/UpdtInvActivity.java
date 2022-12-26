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
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GiDataAdapter;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.utility.ImageAndPositionRendererUtils;
import com.ptbas.controlcenter.utility.NumberToWordsUtils;
import com.ptbas.controlcenter.model.BankAccModel;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.GiModel;
import com.ptbas.controlcenter.model.InvModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UpdtInvActivity extends AppCompatActivity {

    Context context;
    HelperUtils helperUtils = new HelperUtils();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();
    ArrayList<GiModel> giModelArrayList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    CollectionReference refInv = db.collection("InvoiceData");
    CollectionReference refRO = db.collection("ReceivedOrderData");
    CollectionReference refCust = db.collection("CustomerData");
    CollectionReference refBankAccount = db.collection("BankAccountData");

    int invPoType, invPoTOP;
    double matBuyPrice, matSellPrice, matCubication, transportServiceSellPrice;

    // ID
    String roPoCustNumber, currencyVal, custDocumentID, invUIDVal, coRoUIDVal, coPoUIDVal, invUID, invPoUID, invDueDateNTimVal, connectingRODocumentUID;

    // RO
    String roDocumentID, matTypeVal, matNameVal, transportServiceNameVal;

    // Bank
    String bankAccountID, bankName, bankAccountNumber, bankAccountOwnerName;

    // INV
    String invHandOverBy, invDateHandover, dateHandover, finalPaidDate, finalDateHandover, invDueDateNTime, invMainID, invVerifiedBy, invPrintedBy,
            invCreatedByVal, invApprovedByVal, invHandOverByVal, invPoDate, invCustName, custNameVal,
            custAddressVal, invPotypeVal, coCreatedBy, transferProofReference, invTransferReference;

    // DATE
    String dayStrVal, monthStrVal, invDateDeliveryPeriod, invDatePaid, invDateAndTimeCreatedVal,
            coDateDeliveryPeriodVal, coCustomerNameVal;

    Boolean coStatusApprovalVal, coStatusPaymentVal, expandStatus = true;

    // INIT Component
    CardView cdvFilter;
    NestedScrollView nestedScrollView;
    TextInputEditText edtDatePaid, edtTransferProofReference, edtVerifiedBy,
            edtAccountOwnerName, edtPayee;
    TextView tvSubTotal, tvDisc, tvPPN, tvPPH23, tvTotalDue, tvStatus, tvDueDateNTime, tvCreatedBy, tvDateAndTimeCreated, tvApprovedBy,
            tvUID, tvCoUID, tvCustomerName, tvRoUID, tvPoUID, tvDateDeliveryPeriod, tvPoDate, tvPoTransportType,
            tvCubicationTotal, tvMatName;
    AutoCompleteTextView spinnerBankAccount;
    Button imgbtnExpandCollapseFilterLayout;
    LinearLayout llNoData, llWrapFilter, llWrapSupplierDetail;
    ImageButton btnDatePaidReset;
    SwitchCompat statusSwitch;
    DatePickerDialog datePicker;
    GiDataAdapter giDataAdapter;
    RecyclerView rvGoodIssueList;

    List<ProductItems> productItemsList;

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontNormal, fontNormalSmall, fontNormalSmallItalic,
            fontMedium, fontMediumWhite, fontBold, fontTransparent;
    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");



    TextInputEditText edtDateHandover, edtHandoverBy;
    SwitchCompat statusSwitchHandover;

    TextView tvStatusHandover;
    ImageButton btnDateHandoverReset;


    SwitchCompat statusSwitchRecalculate, statusSwitchDefault;
    TextView tvCubicationTotalRev, tvSubTotalRev, tvDiscRev, tvPPNRev, tvPPH23Rev, tvTotalDueRev, tvTotalDueMinus, tvStatusRecalculate, tvStatusDefault;

    double totalUnitFinal;
    double totalAmountForMaterials, totalAmountForTransportService, taxPPN, taxPPNService, taxPPH, totalDue, totalDueForTransportService;

    private MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    Boolean invRecalculateStatus;


    //DecimalFormat dfRound = new DecimalFormat("0");
    DecimalFormat dfRound1 = new DecimalFormat("0");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updt_inv);

        dfRound1.setRoundingMode(RoundingMode.HALF_UP);

        LangUtils.setLocale(this, "en");

        context = this;
        cdvFilter = findViewById(R.id.cdv_filter);

        btnDatePaidReset = findViewById(R.id.btnDatePaidReset);
        btnDateHandoverReset = findViewById(R.id.btnDateHandoverReset);

        llWrapSupplierDetail = findViewById(R.id.llWrapSupplierDetail);
        llWrapFilter = findViewById(R.id.llWrapFilter);
        llNoData = findViewById(R.id.ll_no_data);

        edtAccountOwnerName = findViewById(R.id.edtAccountOwnerName);
        edtPayee = findViewById(R.id.edtPayee);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        statusSwitch = findViewById(R.id.statusSwitch);


        tvSubTotal  = findViewById(R.id.tvSubTotal);
        tvDisc = findViewById(R.id.tvDisc);
        tvPPN = findViewById(R.id.tvPPN);
        tvPPH23 = findViewById(R.id.tvPPH23);

        tvTotalDue = findViewById(R.id.tvTotalDue);
        tvCubicationTotal = findViewById(R.id.tvCubicationTotal);




        tvMatName = findViewById(R.id.tvMatName);
        tvStatus = findViewById(R.id.tvStatus);
        tvDueDateNTime = findViewById(R.id.tvDueDateNTime);
        tvPoDate = findViewById(R.id.tvPoDate);
        tvPoTransportType = findViewById(R.id.tvPoTransportType);
        tvUID = findViewById(R.id.tvUID);
        tvCoUID = findViewById(R.id.tvCoUID);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvRoUID = findViewById(R.id.tvRoUID);
        tvPoUID = findViewById(R.id.tvPoUID);
        tvDateDeliveryPeriod = findViewById(R.id.tvDateDeliveryPeriod);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvDateAndTimeCreated = findViewById(R.id.tvDateAndTimeCreated);
        //tvApprovedBy = findViewById(R.id.tvApprovedBy);

        spinnerBankAccount = findViewById(R.id.spinnerBankAccount);

        rvGoodIssueList = findViewById(R.id.rvItemList);

        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);

        edtVerifiedBy = findViewById(R.id.edtVerifiedBy);
        edtTransferProofReference = findViewById(R.id.edtTransferProofReference);
        edtDatePaid = findViewById(R.id.edtDatePaid);

        helperUtils.ACTIVITY_NAME = "UPDATE";



        statusSwitchRecalculate = findViewById(R.id.statusSwitchUseRecalculate);
        statusSwitchDefault = findViewById(R.id.statusSwitchUseDefault);

        tvCubicationTotalRev = findViewById(R.id.tvCubicationTotalRev);
        tvSubTotalRev = findViewById(R.id.tvSubTotalRev);
        tvDiscRev = findViewById(R.id.tvDiscRev);
        tvPPNRev = findViewById(R.id.tvPPNRev);
        tvPPH23Rev = findViewById(R.id.tvPPH23Rev);
        tvTotalDueRev = findViewById(R.id.tvTotalDueRev);
        tvTotalDueMinus = findViewById(R.id.tvTotalDueMinus);
        tvStatusRecalculate = findViewById(R.id.tvStatusRecalculate);
        tvStatusDefault = findViewById(R.id.tvStatusDefault);


        loadInvoiceData();


        edtDateHandover = findViewById(R.id.edtDateHandover);
        edtHandoverBy = findViewById(R.id.edtHandoverBy);
        statusSwitchHandover = findViewById(R.id.statusSwitchHandover);
        tvStatusHandover = findViewById(R.id.tvStatusHandover);

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
                this, actionBar, "Rincian Invoice");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");


        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
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
                Toast.makeText(UpdtInvActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });

        // SHOW INIT DATA GI ON CREATE
        searchQueryAll();

        // CREATE GI MANAGEMENT ADAPTER
        giDataAdapter = new GiDataAdapter(this, giModelArrayList);



        //btnDatePaidReset.setColorFilter(color);

        edtDatePaid.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(UpdtInvActivity.this,
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
                        btnDatePaidReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });

        edtDateHandover.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(UpdtInvActivity.this,
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

                        finalDateHandover = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtDateHandover.setText(finalDateHandover);
                        btnDateHandoverReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();

        });

        btnDatePaidReset.setOnClickListener(view -> resetDate());
        btnDateHandoverReset.setOnClickListener(view -> resetHandOverDate());
    }

    private void resetHandOverDate() {
        edtDateHandover.setText(null);
        btnDateHandoverReset.setVisibility(View.GONE);
    }

    private void resetDate() {
        edtDatePaid.setText(null);
        btnDatePaidReset.setVisibility(View.GONE);
    }

    private void loadInvoiceData() {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Memproses");
        pd.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {


            invUID = extras.getString("key");

            refInv.whereEqualTo("invDocumentUID", invUID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            invUIDVal = documentSnapshot.get("invUID", String.class);

                            InvModel invModel = documentSnapshot.toObject(InvModel.class);
                            invMainID = invModel.getInvUID();
                            invDueDateNTime = invModel.getInvDueDateNTime();
                            tvUID.setText(invMainID);

                            invTransferReference = invModel.getInvTransferReference();
                            edtTransferProofReference.setText(invTransferReference);

                            invDatePaid = invModel.getInvDateVerified();
                            edtDatePaid.setText(invDatePaid);

                            invVerifiedBy = invModel.getInvVerifiedBy();
                            invDateHandover = invModel.getInvDateHandover();
                            invHandOverBy = invModel.getInvHandoverBy();

                            edtDateHandover.setText(invDateHandover);

                            if (!invHandOverBy.isEmpty()){
                                statusSwitchHandover.setChecked(true);
                                tvStatusHandover.setText("Sudah Diterima");

                                edtDateHandover.setFocusable(false);
                                edtDateHandover.setOnClickListener(null);
                                edtHandoverBy.setFocusable(false);

                                statusSwitchHandover.setEnabled(false);
                                btnDatePaidReset.setVisibility(View.GONE);
                            } else{
                                statusSwitchHandover.setChecked(false);
                                tvStatusHandover.setText("Belum Diterima");

                                edtHandoverBy.setText(null);
                                edtDateHandover.setText(null);
                            }

                            String cubicationTotalDefault = documentSnapshot.get("invTotalVol", String.class);
                            String subTotalDefault = documentSnapshot.get("invSubTotal", String.class);
                            String discDefault = documentSnapshot.get("invDiscount", String.class);
                            String PPNDefault = documentSnapshot.get("invTaxPPN", String.class);
                            String PPH23Default = documentSnapshot.get("invTaxPPH", String.class);
                            String totalDueDefault = documentSnapshot.get("invTotalDue", String.class);




                            invRecalculateStatus = invModel.getInvRecalculate();

                            if (!invRecalculateStatus){
                                statusSwitchDefault.setChecked(true);
                                statusSwitchRecalculate.setChecked(false);
                            } else{
                                statusSwitchDefault.setChecked(false);
                                statusSwitchRecalculate.setChecked(true);
                            }



                            statusSwitchRecalculate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    pd.show();
                                    CollectionReference refInv = db.collection("InvoiceData");

                                    refInv.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                                    String getDocumentID = documentSnapshot.getId();

                                                    if (statusSwitchRecalculate.isChecked()){
                                                        db.collection("InvoiceData").document(invModel.getInvDocumentUID()).update("invRecalculate", true);
                                                        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = true;
                                                    } else{
                                                        db.collection("InvoiceData").document(invModel.getInvDocumentUID()).update("invRecalculate", false);
                                                        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = false;
                                                    }



                                                }

                                                loadInvoiceData();
                                                pd.hide();
                                            }
                                        }
                                    });
                                }
                            });

                            statusSwitchDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if (statusSwitchDefault.isChecked()){
                                        db.collection("InvoiceData").document(invModel.getInvDocumentUID()).update("invRecalculate", false);
                                        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = false;
                                        loadInvoiceData();
                                        pd.hide();
                                    } else{
                                        db.collection("InvoiceData").document(invModel.getInvDocumentUID()).update("invRecalculate", true);
                                        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = true;
                                        loadInvoiceData();
                                        pd.hide();
                                    }

                                    /*tvStatusDefault.setText("On");
                                    statusSwitchRecalculate.setChecked(false);
                                    tvStatusRecalculate.setText("Off");*/
                                }
                            });

                            statusSwitchHandover.setOnCheckedChangeListener((compoundButton, b) -> {
                                pd.show();
                                if (edtDateHandover.getText().toString().isEmpty()){
                                    edtDateHandover.requestFocus();
                                    edtDateHandover.setError("Tanggal serah terima masih belum diisi");
                                    pd.dismiss();
                                    statusSwitchHandover.setChecked(false);
                                } else {
                                    dateHandover = edtDateHandover.getText().toString();
                                    if (statusSwitchHandover.isChecked()) {
                                        refInv.document(invUID).update("invDueDateNTime", invDueDateNTimVal);
                                        refInv.document(invUID).update("invDateHandover", dateHandover);
                                        refInv.document(invUID).update("invHandoverBy", helperUtils.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    edtDateHandover.setError(null);
                                                    edtHandoverBy.setError(null);
                                                    btnDatePaidReset.setVisibility(View.GONE);
                                                    pd.dismiss();
                                                    loadInvoiceData();
                                                }
                                            }
                                        });
                                    } else {
                                        refInv.document(invUID).update("invDateHandover", "");
                                        refInv.document(invUID).update("invHandoverBy", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    loadInvoiceData();
                                                }
                                            }
                                        });
                                    }
                                }


                            });

                            if (!invVerifiedBy.isEmpty()){
                                spinnerBankAccount.setFocusable(false);
                                spinnerBankAccount.setOnItemClickListener(null);
                                spinnerBankAccount.setOnClickListener(null);
                                statusSwitch.setChecked(true);
                                tvStatus.setText("Lunas");
                                edtVerifiedBy.setFocusable(false);
                                edtTransferProofReference.setFocusable(false);
                                edtDatePaid.setFocusable(false);
                                statusSwitch.setEnabled(false);
                                btnDatePaidReset.setVisibility(View.GONE);
                            } else{
                                statusSwitch.setChecked(false);
                                tvStatus.setText("Belum Lunas");
                                //tvApprovedBy.setText(null);
                                edtVerifiedBy.setText(null);
                                edtDatePaid.setText(null);
                            }

                            statusSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                                pd.show();
                                if (edtTransferProofReference.getText().toString().isEmpty()){
                                    edtTransferProofReference.requestFocus();
                                    edtTransferProofReference.setError("Nomor referensi tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitch.setChecked(false);
                                } else if (edtDatePaid.getText().toString().isEmpty()){
                                    edtDatePaid.requestFocus();
                                    edtDatePaid.setError("Tanggal lunas tidak boleh kosong");
                                    pd.dismiss();
                                    statusSwitch.setChecked(false);
                                } else {
                                    transferProofReference = edtTransferProofReference.getText().toString();
                                    if (statusSwitch.isChecked()) {
                                        refInv.document(invUID).update("invDateVerified", finalPaidDate);
                                        refInv.document(invUID).update("invTransferReference", transferProofReference);
                                        refInv.document(invUID).update("invVerifiedBy", helperUtils.getUserId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    edtTransferProofReference.setError(null);
                                                    edtDatePaid.setError(null);
                                                    pd.dismiss();
                                                    loadInvoiceData();
                                                }
                                            }
                                        });
                                    } else {
                                        refInv.document(invUID).update("invDateVerified", "");
                                        refInv.document(invUID).update("invTransferReference", "");
                                        refInv.document(invUID).update("invVerifiedBy", "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    loadInvoiceData();
                                                }
                                            }
                                        });
                                    }
                                }


                            });




                            custDocumentID = invModel.getCustDocumentID();
                            invDateDeliveryPeriod = invModel.getInvDateDeliveryPeriod();

                            tvDueDateNTime.setText(invDueDateNTime);
                            roDocumentID = invModel.getRoDocumentID();

                            bankAccountID = invModel.getBankDocumentID();



















                            searchQueryAll();



                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    invPrintedBy = snapshot.child(helperUtils.getUserId()).child("fullName").getValue(String.class);
                                    invCreatedByVal = snapshot.child(Objects.requireNonNull(documentSnapshot.get("invCreatedBy", String.class))).child("fullName").getValue(String.class);
                                    invApprovedByVal = snapshot.child(Objects.requireNonNull(documentSnapshot.get("invVerifiedBy", String.class))).child("fullName").getValue(String.class);
                                    invHandOverByVal = snapshot.child(Objects.requireNonNull(documentSnapshot.get("invHandoverBy", String.class))).child("fullName").getValue(String.class);
                                    tvCreatedBy.setText(invCreatedByVal);
                                    edtVerifiedBy.setText(invApprovedByVal);
                                    edtHandoverBy.setText(invHandOverByVal);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UpdtInvActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            invDateAndTimeCreatedVal = documentSnapshot.get("invDateNTimeCreated", String.class);

                            tvDateAndTimeCreated.setText(invDateAndTimeCreatedVal);

                            invDatePaid = documentSnapshot.get("invDateVerified", String.class);
                            invTransferReference= documentSnapshot.get("invTransferReference", String.class);

                            edtDatePaid.setText(invDatePaid);

                            coDateDeliveryPeriodVal = documentSnapshot.get("invDateDeliveryPeriod", String.class);
                            tvDateDeliveryPeriod.setText(coDateDeliveryPeriodVal);

                            coStatusApprovalVal = documentSnapshot.get("coStatusApproval", Boolean.class);
                            coStatusPaymentVal = documentSnapshot.get("coStatusPayment", Boolean.class);



                            refRO.whereEqualTo("roDocumentID", roDocumentID).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            transportServiceSellPrice = 0;
                                            matBuyPrice = 0;

                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                coRoUIDVal = documentSnapshot.get("roUID", String.class);
                                                coPoUIDVal = documentSnapshot.get("roPoCustNumber", String.class);

                                                tvRoUID.setText(coRoUIDVal);
                                                tvPoUID.setText(coPoUIDVal);

                                                RoModel roModel = documentSnapshot.toObject(RoModel.class);
                                                roModel.setRoDocumentID(documentSnapshot.getId());

                                                matTypeVal = roModel.getRoMatType();
                                                roPoCustNumber = roModel.getRoPoCustNumber();
                                                custNameVal = roModel.getCustDocumentID();
                                                currencyVal = roModel.getRoCurrency();
                                                invPoTOP = roModel.getRoTOP();

                                                invPoUID = roModel.getRoPoCustNumber();
                                                invPoDate = roModel.getRoDateCreated();
                                                invPoType = roModel.getRoType();

                                                connectingRODocumentUID = roModel.getRoConnectingRoDocumentUID();


                                                tvPoDate.setText(invPoDate);


                                                if (invPoType == 0){
                                                    invPotypeVal = "MATERIAL + JASA ANGKUT";
                                                }
                                                if (invPoType == 1){
                                                    invPotypeVal = "MATERIAL SAJA";
                                                }
                                                if (invPoType == 2){
                                                    invPotypeVal = "JASA ANGKUT SAJA";
                                                }



                                                tvPoTransportType.setText(invPotypeVal);

                                                HashMap<String, List<ProductItems>> map = roModel.getRoOrderedItems();
                                                for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                                                    productItemsList = e.getValue();
                                                    for (int i = 0; i<productItemsList.size();i++){
                                                        if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                                            transportServiceNameVal = productItemsList.get(0).getMatName();
                                                            transportServiceSellPrice = productItemsList.get(0).getMatSellPrice();
                                                        } else {
                                                            matNameVal = productItemsList.get(i).getMatName();
                                                            matCubication = productItemsList.get(i).getMatQuantity();
                                                            matSellPrice = productItemsList.get(i).getMatSellPrice();
                                                        }
                                                    }

                                                }



                                                tvMatName.setText(matNameVal);



                                                totalUnitFinal = 0;

                                                for (int i = 0; i < giModelArrayList.size(); i++) {
                                                    totalUnitFinal += giModelArrayList.get(i).getGiVhlCubication();
                                                }
                                                // TOTAL AMOUNT CALCULATION
                                                totalAmountForMaterials = matSellPrice*Double.parseDouble(df.format(totalUnitFinal));
                                                totalAmountForTransportService = transportServiceSellPrice*Double.parseDouble(df.format(totalUnitFinal));
                                                taxPPN = Double.parseDouble(df.format(((0.11)*totalAmountForMaterials)));
                                                taxPPNService =Double.parseDouble(df.format(((0.11)*totalAmountForTransportService)));
                                                taxPPH = Double.parseDouble(df.format(0.02*totalAmountForTransportService));
                                                totalDue = totalAmountForMaterials+totalAmountForTransportService+taxPPN-taxPPH;
                                                totalDueForTransportService = totalAmountForTransportService+taxPPNService-taxPPH;

                                                Boolean invRecalculateStatus = invModel.getInvRecalculate();

                                                //tvCubicationTotal.setText(totalUnitFinal+" m3");
                                                tvCubicationTotal.setText(cubicationTotalDefault);
                                                tvSubTotal.setText(subTotalDefault);
                                                tvDisc.setText(discDefault);
                                                tvPPN.setText(PPNDefault);
                                                tvPPH23.setText(PPH23Default);
                                                tvTotalDue.setText(totalDueDefault);


                                                tvCubicationTotalRev.setText(df.format(totalUnitFinal)+" m3");
                                                if (invPoType == 2){
                                                    //taxPPN = 0;
                                                    tvSubTotalRev.setText(currencyVal+" "+currencyFormat(df.format(totalAmountForTransportService)));
                                                    tvDiscRev.setText(currencyVal+" "+"0");
                                                    tvPPNRev.setText(currencyVal+" " +currencyFormat(df.format(taxPPN)));
                                                    tvPPH23Rev.setText("("+currencyVal+" " +currencyFormat(df.format(taxPPH))+")");
                                                    tvTotalDueRev.setText(currencyVal+" " +currencyFormat(dfRound1.format(totalDueForTransportService)));
                                                } else if (invPoType == 1){
                                                    tvSubTotalRev.setText( currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials)));
                                                    tvDiscRev.setText(currencyVal+" "+"0");
                                                    tvPPNRev.setText(currencyVal+" "+currencyFormat(df.format(taxPPN)));
                                                    tvPPH23Rev.setText("("+currencyVal+" "+currencyFormat(df.format(taxPPH))+")");
                                                    tvTotalDueRev.setText(currencyVal+" "+currencyFormat(dfRound1.format(totalDue)));
                                                } else if (invPoType == 0){
                                                    tvSubTotalRev.setText(currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials+totalAmountForTransportService)));
                                                    tvDiscRev.setText(currencyVal+" "+"0");
                                                    tvPPNRev.setText(currencyVal+" "+currencyFormat(df.format(taxPPN)));
                                                    tvPPH23Rev.setText("("+currencyVal+" "+currencyFormat(df.format(taxPPH))+")");
                                                    tvTotalDueRev.setText(currencyVal+" "+currencyFormat(dfRound1.format(totalDue)));
                                                }

                                                double trimTotalDueDefault = Double.valueOf(tvTotalDue.getText().toString().replaceAll(",","").replaceAll("IDR", "").trim());
                                                double trimTotalDueRev = Double.valueOf(tvTotalDueRev.getText().toString().replaceAll(",","").replaceAll("IDR", "").trim());
                                                double minusTotalDueVal = trimTotalDueDefault-trimTotalDueRev;

                                                tvTotalDueMinus.setText(currencyVal+" "+currencyFormat(String.valueOf(minusTotalDueVal).replaceAll("-", "")));


                                                refCust.whereEqualTo("custDocumentID", custDocumentID).get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                                                                    CustModel custModel = documentSnapshot.toObject(CustModel.class);
                                                                    custModel.setCustDocumentID(documentSnapshot.getId());

                                                                    coCustomerNameVal = custModel.getCustName();
                                                                    tvCustomerName.setText(coCustomerNameVal);

                                                                }
                                                            }
                                                        });



                                                Calendar c = Calendar.getInstance();
                                                c.add(Calendar.DATE, invPoTOP);

                                                Date invDueDateVal = c.getTime();
                                                invDueDateNTimVal = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(invDueDateVal) +" (" + invPoTOP + " hari)";

                                            }
                                        }
                                    });




                            refBankAccount.whereEqualTo("bankAccountID", bankAccountID).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                                BankAccModel bankAccModel = documentSnapshot.toObject(BankAccModel.class);
                                                bankAccModel.setBankAccountID(documentSnapshot.getId());


                                                bankName = bankAccModel.getBankName();
                                                bankAccountNumber = bankAccModel.getBankAccountNumber();
                                                bankAccountOwnerName = bankAccModel.getBankAccountOwnerName();

                                                String a = bankName.replace(" - ","-");
                                                int b = a.lastIndexOf('-');

                                                spinnerBankAccount.setText(a.substring(0,b)+" - "+bankAccountNumber);
                                                edtAccountOwnerName.setText(bankAccountOwnerName);
                                            }
                                        }
                                    });
                        }

                        db.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID).get()
                                .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                    for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                        CustModel custModel = documentSnapshot2.toObject(CustModel.class);
                                        custAddressVal = custModel.getCustAddress();
                                        invCustName = custModel.getCustName();
                                    }
                                });
                    });
        } else {
            Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    public void createInvPDF(String dest){
        if (new File(dest).exists()){
            new File(dest).deleteOnExit();
        }

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();
            document.setPageSize(PageSize.A4);
            document.addAuthor("PT BAS");
            document.addCreator("BAS Control Center");
            document.addCreationDate();
            // CREATE INVOICE PAGE
            addUpperSpc(document);
            addInvTtl(document);
            addInvMainContent(document);
            // CREATE SUMMARY OF INVOICE PAGE IF PO TYPE != 2 (JASA ANGKUT SAJA)
            if (invPoType == 0 || invPoType == 1){
                document.newPage();
                addGiRcpTtl(document);
                addGiRcpMainContent(document);
            }
            document.close();

            // OPEN GENERATED FILE
            dialogInterfaceUtils.invoiceGeneratedInformation(context, dest);

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void addUpperSpc(Document document) throws DocumentException {
        Paragraph preface0 = new Paragraph();
        Chunk title = new Chunk(" ", fontTransparent);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface0.setAlignment(Element.ALIGN_CENTER);
        preface0.setSpacingAfter(100);
        document.add(preface0);
    }
    private void addInvTtl(Document document) throws DocumentException {
        Paragraph preface1 = new Paragraph();
        Chunk title = new Chunk("INVOICE", fontBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface1.setAlignment(Element.ALIGN_CENTER);
        preface1.setSpacingAfter(20);
        document.add(preface1);
    }
    public static PdfPCell cellImgQrSqr(Image image) throws DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.addElement(image);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(1);
        cell.setRowspan(6);
        return cell;
    }
    public static PdfPCell cellTxtSpan4RowList() throws DocumentException {
        com.itextpdf.text.List ordered = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
        ordered.add(new ListItem("Invoice ini sah dan diproses oleh komputer.", fontNormalSmall));
        ordered.add(new ListItem("Bukti transfer dan PPh 23 (apabila tersedia) dikirim ke email bintang.andalan.semesta@gmail.com.", fontNormalSmall));
        ordered.add(new ListItem("Apabila Anda membutuhkan bantuan, silakan hubungi kami melalui WA: 081335376111 / 085105164000.", fontNormalSmall));
        PdfPCell cell = new PdfPCell();
        cell.addElement(ordered);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setColspan(1);
        cell.setRowspan(6);
        return cell;
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
    public static PdfPCell cellTxtBrdrTopNrmlMainContent(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
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

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_table_column_grey);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 30, stream);
        Image img = null;
        byte[] byteArray = stream.toByteArray();

        try {
            img = Image.getInstance(byteArray);
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        }
        cell.setCellEvent(new ImageAndPositionRendererUtils(img));
        cell.setPaddingTop(1);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(7);
        return cell;
    }
    private void addInvMainContent(Document document) throws DocumentException{
        try {
            String invDateCreated =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            String invTimeCreated =
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            Paragraph paragraphBlank = new Paragraph(" ");

            Paragraph paragraphInvDateCreated =
                    new Paragraph("Terakhir diperbarui: "
                            +invDateCreated+" "+invTimeCreated+", oleh: "+invPrintedBy, fontNormalSmallItalic);
            paragraphInvDateCreated.setAlignment(Element.ALIGN_RIGHT);
            paragraphInvDateCreated.setSpacingAfter(5);

            Image img = null;

            try {

                BitMatrix bitMatrix = multiFormatWriter.encode(invUIDVal, BarcodeFormat.QR_CODE, 100, 95);

                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                byte[] byteArray = stream.toByteArray();
                try {
                    img = Image.getInstance(byteArray);
                    img.setAlignment(Image.TEXTWRAP);
                    img.scaleAbsolute(100f, 110f);
                } catch (BadElementException | IOException e) {
                    e.printStackTrace();
                }

            } catch (WriterException e){

            }

            // TOTAL UNIT CALCULATION
            /*float totalUnit = 0;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                totalUnit += goodIssueModelArrayList.get(i).getGiVhlCubication();
            }*/



            // INIT TABLE
            PdfPTable tblInvSection1 = new PdfPTable(7);
            PdfPTable tblInvSection2 = new PdfPTable(7);
            PdfPTable tblInvSection3 = new PdfPTable(7);
            PdfPTable tblInvSection4 = new PdfPTable(7);
            PdfPTable tblInvSectionDatePeriod = new PdfPTable(2);
            PdfPTable tblInvSection5 = new PdfPTable(5);
            PdfPTable tblInvSection6 = new PdfPTable(5);
            PdfPTable tblInvSection7 = new PdfPTable(5);
            PdfPTable tblInvSection8 = new PdfPTable(5);
            PdfPTable tblInvSection9 = new PdfPTable(5);
            PdfPTable tblInvSection10 = new PdfPTable(2);
            PdfPTable tblInvSection11 = new PdfPTable(2);
            PdfPTable tblInvSection12 = new PdfPTable(6);
            PdfPTable tblInvSection13 = new PdfPTable(6);

            // WIDTH PERCENTAGE CONFIG
            tblInvSection1.setWidthPercentage(100);
            tblInvSection2.setWidthPercentage(100);
            tblInvSection3.setWidthPercentage(100);
            tblInvSection4.setWidthPercentage(100);
            tblInvSectionDatePeriod.setWidthPercentage(100);
            tblInvSection5.setWidthPercentage(100);
            tblInvSection6.setWidthPercentage(100);
            tblInvSection7.setWidthPercentage(100);
            tblInvSection8.setWidthPercentage(100);
            tblInvSection9.setWidthPercentage(100);
            tblInvSection10.setWidthPercentage(100);
            tblInvSection11.setWidthPercentage(100);
            tblInvSection12.setWidthPercentage(100);
            tblInvSection13.setWidthPercentage(100);

            // WIDTH FLOAT CONFIG
            tblInvSection1.setWidths(new float[]{5,1,7,1,6,1,9}); //7 COLS
            tblInvSection2.setWidths(new float[]{3,1,9,1,4,1,11}); //7 COLS
            tblInvSection3.setWidths(new float[]{3,1,9,1,4,1,11}); //7 COLS
            tblInvSection4.setWidths(new float[]{3,1,9,1,4,1,11}); //5 COLS
            tblInvSectionDatePeriod.setWidths(new float[]{1,1}); //2 COLS
            tblInvSection5.setWidths(new float[]{3,2,2,3,4}); //5 COLS
            tblInvSection6.setWidths(new float[]{3,2,2,3,4}); //5 COLS
            tblInvSection7.setWidths(new float[]{3,2,2,3,4}); //5 COLS
            tblInvSection8.setWidths(new float[]{3,2,2,3,4}); //5 COLS
            tblInvSection9.setWidths(new float[]{3,2,2,3,4}); //5 COLS
            tblInvSection10.setWidths(new float[]{2,10}); //2 COLS
            tblInvSection11.setWidths(new float[]{9,1}); //2 COLS
            tblInvSection12.setWidths(new float[]{3,3,1,4,1,9}); //6 COLS
            tblInvSection13.setWidths(new float[]{5,2,5,3,1,5}); //6 COLS

            // ADD CELL TO RESPECTIVE TABLES
            tblInvSection1.addCell(cellColHeaderNoBrdr(
                    new Paragraph("DITAGIH KE", fontMediumWhite),
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
                    new Paragraph("DETAIL TAGIHAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection1.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));

            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("Nama", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(invCustName, fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("Nomor Tagihan", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection2.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(invMainID, fontNormal),
                    Element.ALIGN_LEFT));

            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("Alamat", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(custAddressVal, fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("No. PO"+"\n"+"Tanggal PO"+"\n"+"Jenis PO"+"\n"+"Jatuh Tempo", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":"+"\n"+":"+"\n"+":"+"\n"+":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(invPoUID+"\n"+invPoDate+"\n"+invPotypeVal+"\n"+invDueDateNTime, fontNormal),
                    Element.ALIGN_LEFT));

            List<String> datePeriod = new ArrayList<>();
            for (int i = 0; i < giModelArrayList.size(); i++) {
                datePeriod.add(giModelArrayList.get(i).getGiDateCreated());
            }
            HashSet<String> filter = new HashSet(datePeriod);
            ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);
            Collections.sort(datePeriodFiltered);

            //invDateDeliveryPeriod = String.valueOf(datePeriodFiltered);

            tblInvSectionDatePeriod.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("Pengiriman Tanggal: "+invDateDeliveryPeriod, fontNormal), Element.ALIGN_LEFT));
            tblInvSectionDatePeriod.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));

            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Item# / Deskripsi", fontMedium), Element.ALIGN_LEFT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Harga Satuan", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Jumlah (Unit)", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Pajak", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Jumlah", fontMedium), Element.ALIGN_RIGHT));

            float docValUnitTotal = Float.valueOf(tvCubicationTotal.getText().toString().replaceAll(",", "").replaceAll("m3", "").trim());
            float docValSubTotal = Float.valueOf(tvSubTotal.getText().toString().replaceAll(",", "").replaceAll("IDR", "").trim());
            float docValDisc = Float.valueOf(tvDisc.getText().toString().replaceAll(",", "").replaceAll("IDR", "").trim());
            float docValPPN = Float.valueOf(tvPPN.getText().toString().replaceAll(",", "").replaceAll("IDR", "").trim());
            float docValPPH = Float.valueOf(tvPPH23.getText().toString().replaceAll("IDR ","").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").trim());
            float docValTotalDue = Float.valueOf(tvTotalDue.getText().toString().replaceAll(",", "").replaceAll("IDR", "").trim());

            double totalAmountForMaterialsDefault = matSellPrice*docValUnitTotal;
            double totalAmountForTransportServiceDefault = transportServiceSellPrice*docValUnitTotal;
            double taxPPNDefault = (0.11)*totalAmountForMaterialsDefault+ ((0.11)*totalAmountForTransportServiceDefault);
            double taxPPHDefault = (0.02)*totalAmountForTransportServiceDefault;
            double totalDueDefault = totalAmountForMaterialsDefault+totalAmountForTransportServiceDefault+taxPPNDefault-taxPPHDefault;
            double totalDueForTransportServiceDefault = totalAmountForTransportServiceDefault-taxPPHDefault;



            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("Sub Total :"+"\n"+"Diskon :"+"\n"+"PPN 11% :"+"\n"+"PPh 23 :"+"\n"+"Total Tagihan:", fontNormal), Element.ALIGN_RIGHT));


            if (invRecalculateStatus){

                for (int i = 0; i<productItemsList.size();i++){
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(matNameVal, fontNormal), Element.ALIGN_LEFT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(df.format(matSellPrice)), fontNormal), Element.ALIGN_RIGHT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph("", fontNormal), Element.ALIGN_RIGHT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForMaterials)), fontNormal), Element.ALIGN_RIGHT));
                }



                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(transportServiceNameVal, fontNormal), Element.ALIGN_LEFT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(transportServiceSellPrice)), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph("", fontNormal), Element.ALIGN_RIGHT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForTransportService)), fontNormal), Element.ALIGN_RIGHT));

                if (invPoType == 2){
                    taxPPN = 0;
                    tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForTransportService))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(taxPPN))+"\n"+"("+currencyVal+" "+currencyFormat(dfRound1.format(taxPPH))+")"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(totalDueForTransportService)), fontNormal), Element.ALIGN_RIGHT));
                } else if (invPoType == 1){
                    tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForMaterials))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(taxPPN))+"\n"+"("+currencyVal+" "+currencyFormat(dfRound1.format(taxPPH))+")"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(totalDue)), fontNormal), Element.ALIGN_RIGHT));
                } else if (invPoType == 0){
                    tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForMaterials+totalAmountForTransportService   ))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(taxPPN))+"\n"+"("+currencyVal+" "+currencyFormat(dfRound1.format(taxPPH))+")"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(totalDue)), fontNormal), Element.ALIGN_RIGHT));
                }
            } else{

                for (int i = 0; i<productItemsList.size();i++){
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(matNameVal, fontNormal), Element.ALIGN_LEFT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(df.format(matSellPrice)), fontNormal), Element.ALIGN_RIGHT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(df.format(docValUnitTotal), fontNormal), Element.ALIGN_RIGHT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph("", fontNormal), Element.ALIGN_RIGHT));
                    tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForMaterialsDefault)), fontNormal), Element.ALIGN_RIGHT));
                }

                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(transportServiceNameVal, fontNormal), Element.ALIGN_LEFT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(transportServiceSellPrice)), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(df.format(docValUnitTotal), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph("", fontNormal), Element.ALIGN_RIGHT));
                tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForTransportServiceDefault)), fontNormal), Element.ALIGN_RIGHT));

                if (invPoType == 2){
                    taxPPN = 0;
                    tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForTransportServiceDefault))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(taxPPNDefault))+"\n"+"("+currencyVal+" "+currencyFormat(dfRound1.format(taxPPHDefault))+")"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(totalDueForTransportServiceDefault)), fontNormal), Element.ALIGN_RIGHT));
                } else if (invPoType == 1){
                    tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForMaterialsDefault))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(taxPPNDefault))+"\n"+"("+currencyVal+" "+currencyFormat(dfRound1.format(taxPPHDefault))+")"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(totalDueDefault)), fontNormal), Element.ALIGN_RIGHT));
                } else if (invPoType == 0){
                    tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                            new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalAmountForMaterialsDefault+totalAmountForTransportServiceDefault))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(taxPPNDefault))+"\n"+"("+currencyVal+" "+currencyFormat(dfRound1.format(taxPPHDefault))+")"+"\n"+currencyVal+" "+currencyFormat(dfRound1.format(totalDueDefault)), fontNormal), Element.ALIGN_RIGHT));
                }

            }







            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("TOTAL TAGIHAN (PEMBULATAN)", fontMedium), Element.ALIGN_RIGHT));
            /*tblInvSection9.addCell(cellColHeader(
                    new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(math(totalDue))), fontMedium), Element.ALIGN_RIGHT));*/
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph(currencyVal+" "+currencyFormat(dfRound1.format(totalDueDefault)), fontMedium), Element.ALIGN_RIGHT));

            tblInvSection10.addCell(cellColHeaderNoBrdr(
                    new Paragraph("TERBILANG", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection10.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));

            /*tblInvSection11.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(NumberToWords.convert(math(totalDue))+" Rupiah", fontMedium),
                    Element.ALIGN_LEFT));*/
            tblInvSection11.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(NumberToWordsUtils.convert(Long.parseLong(dfRound1.format(totalDueDefault)))+" Rupiah", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection11.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));




            tblInvSection12.addCell(cellImgQrSqr(img));
            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Transfer Melalui", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(bankName, fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtSpan4RowList());

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Atas Nama", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(bankAccountOwnerName, fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("No. Rekening", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(bankAccountNumber, fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Kode SWIFT", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("CENAIDJAXXX", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            String paidStatus;
            if (edtVerifiedBy.getText().toString().isEmpty()){
                paidStatus= "BELUM LUNAS";
            } else {
                paidStatus= "LUNAS";
            }

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Status", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontMedium),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(paidStatus, fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Tanggal Lunas", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(invDatePaid, fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            tblInvSection13.addCell(cellColHeaderNoBrdr(
                    new Paragraph("DETAIL PEMBAYARAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection13.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            tblInvSection13.addCell(cellColHeaderNoBrdr(
                    new Paragraph("CATATAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            document.add(tblInvSection1);
            document.add(tblInvSection2);
            document.add(tblInvSection3);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection5);
            document.add(tblInvSectionDatePeriod);
            if (invPoType == 0){
                document.add(tblInvSection6);
                document.add(tblInvSection7);
            }
            if (invPoType == 1){
                document.add(tblInvSection6);
            }
            if (invPoType == 2){
                document.add(tblInvSection7);
            }
            document.add(tblInvSection8);
            document.add(tblInvSection9);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection10);
            document.add(tblInvSection11);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection13);
            document.add(tblInvSection12);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(paragraphInvDateCreated);

        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static int math(double d) {
        int c = (int) ((d) + 0.5d);
        double n = d + 0.5d;
        return (n - c) % 2 == 0 ? (int) d : c;
    }

    private void addGiRcpTtl(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        Chunk title = new Chunk("Rekapitulasi Good Issue", fontBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphTitle);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.setSpacingAfter(15);
        document.add(preface);
        document.add(new LineSeparator());
    }
    private void addGiRcpMainContent(Document document) throws DocumentException{
        DecimalFormat df = new DecimalFormat("0.00");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        try {
            String roMatNameTypeStrVal = "Material: "+ matNameVal +" | "+ matTypeVal;
            String roCustNameStrVal = "Customer: "+invCustName;
            String roPoCustNumberStrVal = "Nomor PO: "+roPoCustNumber;
            String roRecapDateCreatedStrVal = "Tanggal rekap dibuat: "+currentDate;

            Chunk roMatNameType = new Chunk(roMatNameTypeStrVal, fontNormal);
            Chunk roCustName = new Chunk(roCustNameStrVal, fontNormal);

            Paragraph paragraphROMatNameType = new Paragraph(roMatNameType);
            paragraphROMatNameType.setAlignment(Element.ALIGN_LEFT);
            paragraphROMatNameType.setSpacingAfter(0);

            Paragraph paragraphROCustName = new Paragraph(roCustName);
            paragraphROCustName.setAlignment(Element.ALIGN_LEFT);
            paragraphROCustName.setSpacingAfter(5);

            Paragraph preface2 = new Paragraph();
            preface2.setSpacingAfter(10);

            PdfPTable table0 = new PdfPTable(2);
            table0.setWidthPercentage(100);

            PdfPTable table1 = new PdfPTable(10);
            table1.setWidthPercentage(100);
            table1.setWidths(new float[]{2,4,3,4,2,2,2,2,2,3});

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);
            table2.setWidths(new float[]{23,3});

            PdfPTable table3 = new PdfPTable(2);
            table3.setWidthPercentage(100);
            table3.setWidths(new float[]{5, 4});

            table0.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(roPoCustNumberStrVal, fontNormal),
                    Element.ALIGN_LEFT));
            table0.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(roRecapDateCreatedStrVal, fontNormal),
                    Element.ALIGN_RIGHT));

            table1.addCell(cellColHeader(
                    new Paragraph("No", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("Tanggal", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("ID", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("NOPOL", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("P", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("L", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("T", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("K", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph("TK", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(cellColHeader(
                    new Paragraph(String.valueOf(Html.fromHtml("M\u00B3")), fontMedium), Element.ALIGN_CENTER));

            /*List<String> datePeriod = new ArrayList<>();
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
            }*/
            float totalCubication = 0;
            Collections.reverse(giModelArrayList);
            for (int i = 0; i < giModelArrayList.size(); i++){
                String rowNumberStrVal = String.valueOf(i+1);
                String rowDateStrVal = giModelArrayList.get(i).getGiDateCreated();
                String rowIDStrVal = giModelArrayList.get(i).getGiNoteNumber();
                String rowVhlUIDStrVal = giModelArrayList.get(i).getVhlUID();
                String rowVhLengthStrVal = giModelArrayList.get(i).getVhlLength().toString();
                String rowVhWidthStrVal = giModelArrayList.get(i).getVhlWidth().toString();
                String rowVhHeightStrVal = giModelArrayList.get(i).getVhlHeight().toString();
                String rowVhHeightCorrectionStrVal = giModelArrayList.get(i).getVhlHeightCorrection().toString();
                String rowVhHeightAfterCorrectionStrVal = giModelArrayList.get(i).getVhlHeightAfterCorrection().toString();
                String vhlCubicationStrVal = df.format(giModelArrayList.get(i).getGiVhlCubication());

                table1.addCell(cellTxtNrml(
                        new Paragraph(rowNumberStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowDateStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowIDStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowVhlUIDStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowVhLengthStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowVhWidthStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowVhHeightStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowVhHeightCorrectionStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(rowVhHeightAfterCorrectionStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(cellTxtNrml(
                        new Paragraph(vhlCubicationStrVal, fontNormal), Element.ALIGN_CENTER));

                totalCubication += giModelArrayList.get(i).getGiVhlCubication();

            }

            String totalCubicationStrVal = df.format(totalCubication);
            double totalIDR = matSellPrice*Double.parseDouble(totalCubicationStrVal)   ;
            String totalIDRStrVal = currencyVal+" "+currencyFormat(dfRound1.format(totalIDR));

            table2.addCell(cellColHeader(
                    new Paragraph("Total Unit", fontMedium), Element.ALIGN_CENTER));
            table2.addCell(cellTxtNrml(
                    new Paragraph(totalCubicationStrVal, fontNormal), Element.ALIGN_CENTER));
            table3.addCell(cellColHeader(
                    new Paragraph("Total", fontMedium), Element.ALIGN_CENTER));
            table3.addCell(cellTxtNrml(
                    new Paragraph(totalIDRStrVal, fontNormal), Element.ALIGN_CENTER));

            document.add(table0);
            document.add(paragraphROMatNameType);
            document.add(paragraphROCustName);
            document.add(new LineSeparator());
            document.add(preface2);
            document.add(table1);
            document.add(table2);
            document.add(table3);
        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private void searchQueryAll(){
        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {

                        if (Objects.equals(item.child("giInvoicedTo").getValue(), invUID)) {
                            GiModel giModel = item.getValue(GiModel.class);
                            giModelArrayList.add(giModel);
                            nestedScrollView.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);
                        }
                    }
                    if (giModelArrayList.size()==0) {
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    nestedScrollView.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giDataAdapter = new GiDataAdapter(context, giModelArrayList);
                rvGoodIssueList.setAdapter(giDataAdapter);
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
        inflater.inflate(R.menu.menu_save_print_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMenuSave) {
            //TODO SAVE CHANGES TO
            return true;
        }

        if (item.getItemId() == R.id.itemMenuPrint){
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                createInvPDF(HelperUtils.getAppPathInvoice(context)+invMainID+".pdf");
            }
            return true;
        }
        finish();
        helperUtils.ACTIVITY_NAME = null;
        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = false;
        custNameVal = "";
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helperUtils.ACTIVITY_NAME = null;
        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helperUtils.ACTIVITY_NAME = null;
        helperUtils.UPDATE_GOOD_ISSUE_IN_INVOICE = false;
        custNameVal = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        custNameVal = "";

        loadInvoiceData();

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