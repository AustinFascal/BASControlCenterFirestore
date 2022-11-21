package com.ptbas.controlcenter.create;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
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
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.utility.ImageAndPositionRenderer;
import com.ptbas.controlcenter.utility.NumberToWords;
import com.ptbas.controlcenter.model.BankAccountModel;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class AddInvoiceActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    double matSellPrice, transportServiceSellPrice, invTax1 = 0, invTax2 =0;
    String invDueDate, invDateNTimeCreated, invTimeCreated, custIDVal, dateStartVal = "", dateEndVal = "", rouidVal= "", currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal, transportServiceNameVal,
            invPoDate = "", invCustName = "", invPoUID = "", custNameVal = "", roDocumentID = "", coDocumentID, coUID, coAccBy,
            custAddressVal = "", invUID="", invPotypeVal = "", customerData = "", customerID ="", bankAccountID = "", bankNameVal, bankAccountNumberVal, bankAccountOwnerNameVal;
    int invPoType, invPoTOP;

    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID, spinnerCustName, spinnerBankAccount, spinnerCoUID;
    TextInputEditText edtPoUID, edtDateStart, edtDateEnd, edtAccountOwnerName;
    DatePickerDialog datePicker;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;
    Context context;
    Helper helper = new Helper();
    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    CardView cdvFilter;
    View firstViewData;
    NestedScrollView nestedScrollView;

    TextView tvTotalSelectedItem, tvTotalSelectedItem2;

    List<String> bankAccountDocumentID, bankAccount, arrayListRoUID, arrayListPoUID, arrayListCoUID;
    List<ProductItems> productItemsList;
    List<String> customerName, arrayListCustDocumentID;

    LinearLayout llStatusCo, ll_wrap_filter_by_couid, llShowSpinnerRoAndEdtPo, llWrapFilterByDateRange, llWrapFilterByRouid, llNoData, llWrapFilter, llBottomSelectionOptions;

    ImageButton btnResetBankAccount, btnResetCustomer, btnGiSearchByDateReset, btnGiSearchByRoUIDReset, btnExitSelection, btnResetCoUID;

    ExtendedFloatingActionButton fabCreateDocument;

    DialogInterface dialogInterface = new DialogInterface();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontNormal, fontNormalSmall, fontNormalSmallItalic,
            fontMedium, fontMediumWhite, fontBold, fontTransparent;
    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat dfRound = new DecimalFormat("0");

    Vibrator vibrator;

    float totalUnit;

    boolean isSelectedAll = false;

    private Menu menu;

    String invDateDeliveryPeriod, invCreatedBy="", custDocumentID ="";

    private MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    double totalAmountForMaterials, totalAmountForTransportService, taxPPN, taxPPH, totalDue, totalDueForTransportService;
    String totalUnitFinalFinal, invSubTotalFinal, invDiscountFinal, invTaxPPNFinal, invTaxPPHFinal, invTotalDueFinal;


    List<String> receiveOrderNumberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invoice);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LangUtils.setLocale(this, "en");

        helper.ACTIVITY_NAME = "UPDATE";

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


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

        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);
        btnSearchData = findViewById(R.id.caridata);

        spinnerCoUID = findViewById(R.id.spinnerCoUID);
        spinnerBankAccount = findViewById(R.id.spinnerBankAccount);
        spinnerCustName = findViewById(R.id.spinnerCustName);
        spinnerRoUID = findViewById(R.id.rouid);
        edtAccountOwnerName = findViewById(R.id.edtAccountOwnerName);
        edtPoUID = findViewById(R.id.pouid);
        edtDateStart = findViewById(R.id.edt_gi_date_filter_start);
        edtDateEnd = findViewById(R.id.edt_gi_date_filter_end);
        rvGoodIssueList = findViewById(R.id.rvItemList);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);
        llStatusCo = findViewById(R.id.llStatusCo);
        ll_wrap_filter_by_couid = findViewById(R.id.ll_wrap_filter_by_couid);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llWrapFilterByRouid = findViewById(R.id.ll_wrap_filter_by_rouid);
        llWrapFilter = findViewById(R.id.llWrapFilter);
        llShowSpinnerRoAndEdtPo = findViewById(R.id.llShowSpinnerRoAndEdtPo);

        llNoData = findViewById(R.id.ll_no_data);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        tvTotalSelectedItem2 = findViewById(R.id.tvTotalSelectedItem2);
        btnExitSelection = findViewById(R.id.btnExitSelection);
        llBottomSelectionOptions = findViewById(R.id.llBottomSelectionOptions);

        btnResetCoUID = findViewById(R.id.btnResetCoUID);
        btnResetBankAccount = findViewById(R.id.btnResetBankAccount);
        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByRoUIDReset = findViewById(R.id.btnResetRouid);
        fabCreateDocument = findViewById(R.id.fabCreateCOR);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        /*btnGiSearchByDateReset.setColorFilter(color);
        btnGiSearchByRoUIDReset.setColorFilter(color);
        btnResetBankAccount.setColorFilter(color);
        btnResetCustomer.setColorFilter(color);
        btnResetCoUID.setColorFilter(color);*/

        ActionBar actionBar = getSupportActionBar();



        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Buat Invoice");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);


        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        edtDateStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(AddInvoiceActivity.this,
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

                        String finalDate = dayStrVal + "-" +monthStrVal + "-" +  year;

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

            datePicker = new DatePickerDialog(AddInvoiceActivity.this,
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

                        String finalDate = dayStrVal + "-" +monthStrVal + "-" + year;

                        edtDateEnd.setText(finalDate);
                        dateEndVal = finalDate;



                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.show();
        });

        final String userId = helper.getUserId();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invCreatedBy = snapshot.child(userId).child("fullName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddInvoiceActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });

        // CREATE GI MANAGEMENT ADAPTER
        giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);

        // HIDE FAB CREATE COR ON CREATE
        fabCreateDocument.animate().translationY(800).setDuration(100).start();

        // NOTIFY REAL-TIME CHANGES AS USER CHOOSE THE ITEM
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                // CHECK IF DATE AND RO/PO NUMBER IS SELECTED
                if (!spinnerRoUID.getText().toString().isEmpty()
                        && !Objects.requireNonNull(edtPoUID.getText()).toString().isEmpty()){

                    int itemSelectedSize = giManagementAdapter.getSelected().size();
                    float itemSelectedVolume = giManagementAdapter.getSelectedVolume();
                    String itemSelectedSizeVal = String.valueOf(itemSelectedSize).concat(" item terpilih");
                    String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
                    if (giManagementAdapter.getSelected().size()>0){

                        fabCreateDocument.animate().translationY(0).setDuration(100).start();

                        tvTotalSelectedItem.setText(itemSelectedSizeVal);
                        tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);
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
                        totalUnit = 0;
                        fabCreateDocument.animate().translationY(800).setDuration(100).start();
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


        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_add_recap_good_issue_data, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_date_range);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        bankAccountDocumentID = new ArrayList<>();
        bankAccount = new ArrayList<>();
        customerName = new ArrayList<>();
        arrayListCustDocumentID = new ArrayList<>();
        arrayListRoUID = new ArrayList<>();
        arrayListPoUID = new ArrayList<>();
        arrayListCoUID = new ArrayList<>();
        receiveOrderNumberList = new ArrayList<>();


        db.collection("CustomerData").whereEqualTo("custStatus", true)
                .addSnapshotListener((value, error) -> {
                    customerName.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                //String custDocumentID = Objects.requireNonNull(d.get("custDocumentID")).toString();
                                String spinnerCustUID = Objects.requireNonNull(d.get("custUID")).toString();
                                String spinnerCustName = Objects.requireNonNull(d.get("custName")).toString();
                                customerName.add(spinnerCustUID+" - "+spinnerCustName);
                                //arrayListCustDocumentID.add(custDocumentID);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddInvoiceActivity.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCustName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(AddInvoiceActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /*db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                .addSnapshotListener((value, error) -> {
                    arrayListRoUID.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String spinnerPurchaseOrders = Objects.requireNonNull(d.get("roUID")).toString();
                                arrayListRoUID.add(spinnerPurchaseOrders);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddInvoiceActivity.this, R.layout.style_spinner, arrayListRoUID);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerRoUID.setAdapter(arrayAdapter);
                        } else {
                            if(!this.isFinishing()) {
                                dialogInterface.roNotExistsDialogForInvoice(AddInvoiceActivity.this);
                            }
                        }
                    }
                });*/

        spinnerCustName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedCustomer = (String) adapterView.getItemAtPosition(i);
                String[] custNameSplit = selectedCustomer.split(" - ");
                String custNameSplit1 = custNameSplit[1];

                //String selectedSpinnerCustomerName = (String) adapterView.getItemAtPosition(position);
                customerData = selectedCustomer;
                spinnerCustName.setError(null);

                //String[] custID = selectedCustomer.split("-");
                customerID = custNameSplit[0];

                btnResetCustomer.setVisibility(View.VISIBLE);
                //clearRoPoData();

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
                                                        receiveOrderNumberList.add(spinnerPurchaseOrders);
                                                    }
                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddInvoiceActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                                                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                    spinnerRoUID.setAdapter(arrayAdapter);
                                                }
                                            });
                                }

                            }
                        });


                llShowSpinnerRoAndEdtPo.setVisibility(View.VISIBLE);
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
        //spinnerCustName.setOnFocusChangeListener((view, b) -> spinnerCustName.setText(customerData));


        /*spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            btnGiSearchByRoUIDReset.setVisibility(View.VISIBLE);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(documentSnapshot.getId());
                            rouidVal = selectedSpinnerPoPtBasNumber;
                            roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        }
                        edtPoUID.setText(roPoCustNumber);
                    });
        });*/



        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            btnGiSearchByRoUIDReset.setVisibility(View.VISIBLE);
            ll_wrap_filter_by_couid.setVisibility(View.VISIBLE);

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(documentSnapshot.getId());
                            rouidVal = selectedSpinnerPoPtBasNumber;
                            roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                            roDocumentID = receivedOrderModel.getRoDocumentID();

                            db.collection("CashOutData").whereEqualTo("roDocumentID", roDocumentID)
                                    .addSnapshotListener((value, error) -> {
                                        arrayListCoUID.clear();
                                        if (value != null) {
                                            if (!value.isEmpty()) {
                                                for (DocumentSnapshot d : value.getDocuments()) {
                                                    //coDocumentID = Objects.requireNonNull(d.get("coDocumentID")).toString();
                                                    coUID = Objects.requireNonNull(d.get("coUID")).toString();
                                                    arrayListCoUID.add(coUID);
                                                }
                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddInvoiceActivity.this, R.layout.style_spinner, arrayListCoUID);
                                                arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                spinnerCoUID.setAdapter(arrayAdapter);
                                            } else {
                                                Toast.makeText(AddInvoiceActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        edtPoUID.setText(roPoCustNumber);
                    });

        });

        spinnerCoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerCoUID.setError(null);
            String selectedCoUID = (String) adapterView.getItemAtPosition(i);

            btnResetCoUID.setVisibility(View.VISIBLE);
            ll_wrap_filter_by_couid.setVisibility(View.VISIBLE);

            db.collection("CashOutData").whereEqualTo("coUID", selectedCoUID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            CashOutModel cashOutModel = documentSnapshot.toObject(CashOutModel.class);
                            cashOutModel.setCoDocumentID(documentSnapshot.getId());
                            coDocumentID = cashOutModel.getCoDocumentID();
                            coUID = cashOutModel.getCoUID();
                            coAccBy = cashOutModel.getCoAccBy();

                            if (coAccBy.isEmpty()){
                                MaterialDialog md = new MaterialDialog.Builder(this)
                                        .setAnimation(R.raw.lottie_attention)
                                        .setTitle("Cash Out Belum Lunas")
                                        .setMessage("Cash Out belum dibayar ke supplier. Apakah Anda yakin ingin melanjutkan penagihan?")
                                        .setPositiveButton("LANJUT", R.drawable.ic_outline_navigate_next, (dialogInterface, which) -> {
                                            dialogInterface.dismiss();
                                            llStatusCo.setVisibility(View.VISIBLE);
                                        })
                                        .setNegativeButton("BATAL", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                                            dialogInterface.dismiss();
                                            spinnerCoUID.setText(null);
                                            llStatusCo.setVisibility(View.GONE);
                                            btnResetCoUID.setVisibility(View.GONE);
                                        })
                                        .setCancelable(false)
                                        .build();

                                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                                md.show();
                            } else {
                                llStatusCo.setVisibility(View.GONE);
                            }
                        }
                        //spinnerCoUID.setText(coUID);
                    });
        });


        db.collection("BankAccountData").whereEqualTo("bankType", "PERUSAHAAN")
                .addSnapshotListener((value, error) -> {
                    bankAccount.clear();
                    bankAccountDocumentID.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                //String bankAccountID = Objects.requireNonNull(d.get("bankAccountID")).toString();
                                String bankName = Objects.requireNonNull(d.get("bankName")).toString();
                                String bankAccountNumber = Objects.requireNonNull(d.get("bankAccountNumber")).toString();
                                //String bankAccountOwnerName = Objects.requireNonNull(d.get("bankAccountOwnerName")).toString();

                                String a = bankName.replace(" - ","-");
                                int b = a.lastIndexOf('-');

                                bankAccount.add(a.substring(0,b)+" - "+bankAccountNumber);
                                //bankAccountDocumentID.add(bankAccountID);
                                //edtAccountOwnerName.setText(bankAccountOwnerName);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddInvoiceActivity.this, R.layout.style_spinner, bankAccount);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerBankAccount.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(AddInvoiceActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
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
                                //bankAccountModel.setBankAccountID(documentSnapshot.getId());
                                //bankAccountID = selectedSpinnerBankAccount;
                                bankAccountID = bankAccountModel.getBankAccountID();
                                bankNameVal = bankAccountModel.getBankName();
                                bankAccountNumberVal = bankAccountModel.getBankAccountNumber();
                                bankAccountOwnerNameVal = bankAccountModel.getBankAccountOwnerName();

                                edtAccountOwnerName.setText(bankAccountModel.getBankAccountOwnerName());
                            }
                            edtPoUID.setText(roPoCustNumber);
                        });

            }
        });


        btnGiSearchByDateReset.setOnClickListener(view -> {
            clearSelection();
            menu.findItem(R.id.select_all_data_recap).setVisible(false);
            edtDateStart.setText(null);
            edtDateEnd.setText(null);
            edtDateStart.clearFocus();
            edtDateEnd.clearFocus();
            btnGiSearchByDateReset.setVisibility(View.GONE);
        });

        btnGiSearchByRoUIDReset.setOnClickListener(view -> {
            llStatusCo.setVisibility(View.GONE);
            spinnerRoUID.setText(null);
            spinnerRoUID.requestFocus();
            edtPoUID.setText(null);
            edtPoUID.clearFocus();
            spinnerCoUID.setText(null);
            spinnerCoUID.clearFocus();
            coUID = "";
            coDocumentID = "";
            roDocumentID = "";
            rouidVal = "";
            pouidVal = "";

            ll_wrap_filter_by_couid.setVisibility(View.GONE);
            btnGiSearchByRoUIDReset.setVisibility(View.GONE);
            btnResetCoUID.setVisibility(View.GONE);
        });

        btnResetBankAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.findItem(R.id.select_all_data_recap).setVisible(false);
                clearSelection();
                spinnerBankAccount.setText(null);
                spinnerBankAccount.requestFocus();
                edtAccountOwnerName.setText(null);
            }
        });

        btnResetCoUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.findItem(R.id.select_all_data_recap).setVisible(false);
                clearSelection();
                spinnerCoUID.setText(null);
                spinnerCoUID.requestFocus();
                btnResetCoUID.setVisibility(View.GONE);
                llStatusCo.setVisibility(View.GONE);
            }
        });

        //fabCreateGiRecap.hide();

        btnResetCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.findItem(R.id.select_all_data_recap).setVisible(false);
                clearSelection();
                llStatusCo.setVisibility(View.GONE);
                coUID = "";
                coDocumentID = "";
                rouidVal = "";
                pouidVal = "";
                custNameVal = "";
                spinnerCustName.setText(null);
                llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                spinnerCustName.requestFocus();
                edtPoUID.setText(null);
                spinnerRoUID.setText(null);
                edtPoUID.clearFocus();
                spinnerRoUID.clearFocus();

                spinnerCoUID.setText(null);
                spinnerCoUID.clearFocus();
                ll_wrap_filter_by_couid.setVisibility(View.GONE);
                btnResetCustomer.setVisibility(View.GONE);
            }
        });

        btnSearchData.setOnClickListener(view -> {
            /*View viewLayout = AddInvoiceActivity.this.getCurrentFocus();
            if (viewLayout != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
            }
            if (!Objects.requireNonNull(edtDateStart.getText()).toString().isEmpty()&&
                    !Objects.requireNonNull(edtDateEnd.getText()).toString().isEmpty()){
                searchQuery();
            } else {
                nestedScrollView.setVisibility(View.GONE);
                dialogInterface.mustAddDateRangeInformation(this);
            }*/

            View viewLayout = AddInvoiceActivity.this.getCurrentFocus();
            if (viewLayout != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
            }

            if (spinnerCustName.getText().toString().isEmpty()){
                spinnerCustName.setError("Mohon pilih pelanggan");
            } else{
                spinnerCustName.setError(null);
            }

            if (spinnerRoUID.getText().toString().isEmpty()){
                spinnerRoUID.setError("Mohon pilih nomor RO dan PO");
            } else{
                spinnerRoUID.setError(null);
            }

            if (Objects.requireNonNull(edtPoUID.getText()).toString().isEmpty()){
                edtPoUID.setError("Mohon pilih nomor RO dan PO");
            } else{
                edtPoUID.setError(null);
            }

            if (Objects.requireNonNull(spinnerCoUID.getText()).toString().isEmpty()){
                spinnerCoUID.setError("Mohon pilih nomor ID Cash Out");
            } else{
                spinnerCoUID.setError(null);
            }

            if (Objects.requireNonNull(spinnerBankAccount.getText()).toString().isEmpty()){
                spinnerBankAccount.setError("Mohon pilih rekening pembayaran");
            } else{
                spinnerBankAccount.setError(null);
            }

            if (!spinnerCustName.getText().toString().isEmpty()&&
                    !spinnerRoUID.getText().toString().isEmpty()&&
                    !spinnerCoUID.getText().toString().isEmpty()&&
                    !edtPoUID.getText().toString().isEmpty()&&
                    !spinnerBankAccount.getText().toString().isEmpty()&&
                    !edtAccountOwnerName.getText().toString().isEmpty()){
                searchQuery();
            }
        });



        fabCreateDocument.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                    totalUnit += giManagementAdapter.getSelected().get(i).getGiVhlCubication();
                }

                double totalIDR = matSellPrice *Double.parseDouble(df.format(totalUnit));

                List<String> datePeriod = new ArrayList<>();
                for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                    datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
                }
                HashSet<String> filter = new HashSet(datePeriod);
                ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);
                Collections.sort(datePeriodFiltered);
                invDateDeliveryPeriod = String.valueOf(datePeriodFiltered);

                String roUIDValNoSpace = rouidVal.replaceAll("\\s","");
                invUID = getRandomString()+"-INV-"+roUIDValNoSpace;


                String invCreatedBy = helper.getUserId();


                invTimeCreated =
                        new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                invDateNTimeCreated =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()).concat(" | "+invTimeCreated+" WIB");


                // TOTAL AMOUNT CALCULATION
                totalAmountForMaterials = matSellPrice*totalUnit;
                totalAmountForTransportService = transportServiceSellPrice*totalUnit;
                taxPPN = (0.11)*totalAmountForMaterials;
                taxPPH = (0.02)*totalAmountForTransportService;
                totalDue = totalAmountForMaterials+totalAmountForTransportService+taxPPN-taxPPH;
                totalDueForTransportService = totalAmountForTransportService-taxPPH;

                //Toast.makeText(context, String.valueOf(totalAmountForMaterials), Toast.LENGTH_SHORT).show();

                if (invPoType == 2){
                    taxPPN = 0;
                    String totalUnitFinalFinal = totalUnit+" m3";
                    String invSubTotalFinal = currencyVal+" "+currencyFormat(df.format(totalAmountForTransportService));
                    String invDiscountFinal = currencyVal+" "+"0";
                    String invTaxPPNFinal = currencyVal+" " +currencyFormat(df.format(taxPPN));
                    String  invTaxPPHFinal = "("+currencyVal+" " +currencyFormat(df.format(taxPPH))+")";
                    String invTotalDueFinal = currencyVal+" " +currencyFormat(df.format(totalDueForTransportService));
                    dialogInterface.confirmCreateInvoice(context, db,
                            goodIssueModelArrayList,
                            invUID,  invCreatedBy,
                            invDateNTimeCreated,  "-", "", "",
                            "", invDateDeliveryPeriod,
                            custDocumentID,  bankAccountID,  roDocumentID, "", "",
                            totalUnitFinalFinal, invSubTotalFinal, invDiscountFinal, invTaxPPNFinal, invTaxPPHFinal, invTotalDueFinal, coUID);

                } else if (invPoType == 1){
                    String totalUnitFinalFinal = totalUnit+" m3";
                    String invSubTotalFinal = currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials));
                    String invDiscountFinal = currencyVal+" "+"0";
                    String invTaxPPNFinal = currencyVal+" " +currencyFormat(df.format(taxPPN));
                    String invTaxPPHFinal = "("+currencyVal+" " +currencyFormat(df.format(taxPPH))+")";
                    String invTotalDueFinal = currencyVal+" " +currencyFormat(df.format(totalDue));
                    dialogInterface.confirmCreateInvoice(context, db,
                            goodIssueModelArrayList,
                            invUID,  invCreatedBy,
                            invDateNTimeCreated,  "-", "", "",
                            "", invDateDeliveryPeriod,
                            custDocumentID,  bankAccountID,  roDocumentID, "", "",
                            totalUnitFinalFinal, invSubTotalFinal, invDiscountFinal, invTaxPPNFinal, invTaxPPHFinal, invTotalDueFinal, coUID);

                } else if (invPoType == 0){
                    String totalUnitFinalFinal = totalUnit+" m3";
                    String invSubTotalFinal = currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials+totalAmountForTransportService));
                    String invDiscountFinal = currencyVal+" "+"0";
                    String invTaxPPNFinal = currencyVal+" " +currencyFormat(df.format(taxPPN));
                    String invTaxPPHFinal = "("+currencyVal+" " +currencyFormat(df.format(taxPPH))+")";
                    String invTotalDueFinal = currencyVal+" " +currencyFormat(df.format(totalDue));

                    dialogInterface.confirmCreateInvoice(context, db,
                            goodIssueModelArrayList,
                            invUID,  invCreatedBy,
                            invDateNTimeCreated,  "-", "", "",
                            "", invDateDeliveryPeriod,
                            custDocumentID,  bankAccountID,  roDocumentID, "", "",
                            totalUnitFinalFinal, invSubTotalFinal, invDiscountFinal, invTaxPPNFinal, invTaxPPHFinal, invTotalDueFinal, coUID);
                }



            }
        });

        searchQueryAll();




        btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSelection();
            }
        });
    }

    private  void clearSelection(){
        isSelectedAll = false;
        menu.findItem(R.id.select_all_data_recap).setIcon(ContextCompat.getDrawable(AddInvoiceActivity.this, R.drawable.ic_outline_select_all));

        giManagementAdapter.clearSelection();
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

    private static String getRandomString() {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(5);
        for(int i = 0; i< 5; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
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
            dialogInterface.invoiceGeneratedInformation(context, dest);

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
        preface0.setSpacingAfter(120);
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
        cell.setRowspan(5);
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
        cell.setPadding(6);
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
        cell.setCellEvent(new ImageAndPositionRenderer(img));
        cell.setPaddingTop(1);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(7);
        return cell;
    }
    private void addInvMainContent(Document document) throws DocumentException{
        try {
            /*String invDateCreated =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
            String invTimeCreated =
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());*/



            Paragraph paragraphBlank = new Paragraph(" ");

            Paragraph paragraphInvDateCreated =
                    new Paragraph("Terakhir diperbarui: "
                            +invDateNTimeCreated+", oleh: "+invCreatedBy, fontNormalSmallItalic);
            paragraphInvDateCreated.setAlignment(Element.ALIGN_RIGHT);
            paragraphInvDateCreated.setSpacingAfter(5);

            Image img = null;

            try {

                BitMatrix bitMatrix = multiFormatWriter.encode(invUID, BarcodeFormat.QR_CODE, 100, 95);

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


            // INIT IMAGE BCA QR CODE
            //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bca_qr_bas);


            // TOTAL UNIT CALCULATION
            /*float totalUnit = 0;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                totalUnit += goodIssueModelArrayList.get(i).getGiVhlCubication();
            }*/

            float totalUnitFinal = 0;
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                totalUnitFinal += giManagementAdapter.getSelected().get(i).getGiVhlCubication();
            }

            // TOTAL AMOUNT CALCULATION
            double totalAmountForMaterials = matSellPrice*totalUnitFinal;
            double totalAmountForTransportService = transportServiceSellPrice*totalUnitFinal;
            double taxPPN = (0.11)*totalAmountForMaterials;
            double taxPPH = (0.02)*totalAmountForTransportService;
            double totalDue = totalAmountForMaterials+totalAmountForTransportService+taxPPN-taxPPH;
            double totalDueForTransportService = totalAmountForTransportService-taxPPH;

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
                    new Paragraph(custNameVal, fontNormal),
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
                    new Paragraph(invUID, fontNormal),
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
                    new Paragraph(pouidVal+"\n"+invPoDate+"\n"+invPotypeVal+"\n"+"-", fontNormal),
                    Element.ALIGN_LEFT));

            List<String> datePeriod = new ArrayList<>();
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
            }
            HashSet<String> filter = new HashSet(datePeriod);
            ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);

            invDateDeliveryPeriod = String.valueOf(datePeriodFiltered);

            tblInvSectionDatePeriod.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("Pengiriman Tanggal: "+datePeriodFiltered, fontNormal), Element.ALIGN_LEFT));
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

            for (int i = 0; i<productItemsList.size();i++){
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(matNameVal, fontNormal), Element.ALIGN_LEFT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(matSellPrice)), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(taxPPN)), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials)), fontNormal), Element.ALIGN_RIGHT));
            }

            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(transportServiceNameVal, fontNormal), Element.ALIGN_LEFT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(transportServiceSellPrice)), fontNormal), Element.ALIGN_RIGHT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(taxPPH)), fontNormal), Element.ALIGN_RIGHT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmountForTransportService)), fontNormal), Element.ALIGN_RIGHT));

            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                    new Paragraph("Sub Total :"+"\n"+"Diskon :"+"\n"+"PPN 11% :"+"\n"+"PPh 23 :"+"\n"+"Total Tagihan:", fontNormal), Element.ALIGN_RIGHT));
            if (invPoType == 2){
                taxPPN = 0;
                tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmountForTransportService))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(df.format(taxPPN))+"\n"+"("+currencyVal+" "+currencyFormat(df.format(taxPPH))+")"+"\n"+currencyVal+" "+currencyFormat(df.format(totalDueForTransportService)), fontNormal), Element.ALIGN_RIGHT));
            } else if (invPoType == 1){
                tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(df.format(taxPPN))+"\n"+"("+currencyVal+" "+currencyFormat(df.format(taxPPH))+")"+"\n"+currencyVal+" "+currencyFormat(df.format(totalDue)), fontNormal), Element.ALIGN_RIGHT));
            } else if (invPoType == 0){
                tblInvSection8.addCell(cellTxtBrdrTopNrmlMainContent(
                        new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmountForMaterials+totalAmountForTransportService   ))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(df.format(taxPPN))+"\n"+"("+currencyVal+" "+currencyFormat(df.format(taxPPH))+")"+"\n"+currencyVal+" "+currencyFormat(df.format(totalDue)), fontNormal), Element.ALIGN_RIGHT));
            }


            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph("TOTAL TAGIHAN (PEMBULATAN)", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection9.addCell(cellColHeader(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(math(totalDue))), fontMedium), Element.ALIGN_RIGHT));

            tblInvSection10.addCell(cellColHeaderNoBrdr(
                    new Paragraph("TERBILANG", fontMediumWhite),
                    Element.ALIGN_LEFT));
            tblInvSection10.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));

            tblInvSection11.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(NumberToWords.convert(math(totalDue))+" Rupiah", fontMedium),
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
                    new Paragraph(bankNameVal, fontNormalSmall),
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
                    new Paragraph(bankAccountOwnerNameVal, fontNormalSmall),
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
                    new Paragraph(bankAccountNumberVal, fontNormalSmall),
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

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Status", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontMedium),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("BELUM LUNAS", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            tblInvSection12.addCell(cellTxtNoBrdrNrmlWthPadLft(
                    new Paragraph("Tanggal Lunas", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontMedium),
                    Element.ALIGN_RIGHT));
            tblInvSection12.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("-", fontMedium),
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
            String roCustNameStrVal = "Customer: "+custNameVal;
            String roPoCustNumberStrVal = "Nomor PO: "+roPoCustNumber;
            String roRecapDateCreatedStrVal = "Tanggal rekap dibuat: "+invDateNTimeCreated;

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
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++){
                String rowNumberStrVal = String.valueOf(i+1);
                String rowDateStrVal = giManagementAdapter.getSelected().get(i).getGiDateCreated();
                String rowIDStrVal = giManagementAdapter.getSelected().get(i).getGiUID().substring(0,5);
                String rowVhlUIDStrVal = giManagementAdapter.getSelected().get(i).getVhlUID();
                String rowVhLengthStrVal = giManagementAdapter.getSelected().get(i).getVhlLength().toString();
                String rowVhWidthStrVal = giManagementAdapter.getSelected().get(i).getVhlWidth().toString();
                String rowVhHeightStrVal = giManagementAdapter.getSelected().get(i).getVhlHeight().toString();
                String rowVhHeightCorrectionStrVal = giManagementAdapter.getSelected().get(i).getVhlHeightCorrection().toString();
                String rowVhHeightAfterCorrectionStrVal = giManagementAdapter.getSelected().get(i).getVhlHeightAfterCorrection().toString();
                String vhlCubicationStrVal = df.format(giManagementAdapter.getSelected().get(i).getGiVhlCubication());

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

                totalCubication += giManagementAdapter.getSelected().get(i).getGiVhlCubication();

            }

            String totalCubicationStrVal = df.format(totalCubication);
            double totalIDR = matSellPrice*Double.parseDouble(df.format(totalCubication))    ;
            String totalIDRStrVal = currencyVal+" "+currencyFormat(df.format(totalIDR));

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

    private void searchQuery(){
        showHideFilterComponents(false);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(100);
        }

        expandFilterViewValidation();
        TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());

        rouidVal = spinnerRoUID.getText().toString();
        pouidVal = Objects.requireNonNull(edtPoUID.getText()).toString();

        db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", pouidVal).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (productItemsList != null){
                        productItemsList.clear();
                    }
                    transportServiceSellPrice = 0;
                    matSellPrice = 0;

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                        receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                        roDocumentID = receivedOrderModel.getRoDocumentID();
                        matTypeVal = receivedOrderModel.getRoMatType();
                        roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        //custNameVal = receivedOrderModel.getRoCustName();
                        currencyVal = receivedOrderModel.getRoCurrency();
                        invCustName = receivedOrderModel.getCustDocumentID();
                        invPoUID = receivedOrderModel.getRoPoCustNumber();
                        invPoDate = receivedOrderModel.getRoDateCreated();
                        invPoType = receivedOrderModel.getRoType();
                        invPoTOP = receivedOrderModel.getRoTOP();

                        if (invPoType == 0){
                            invPotypeVal = "MATERIAL + JASA ANGKUT";
                        }
                        if (invPoType == 1){
                            invPotypeVal = "MATERIAL SAJA";
                        }
                        if (invPoType == 2){
                            invPotypeVal = "JASA ANGKUT SAJA";
                        }

                        HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            productItemsList = e.getValue();
                            for (int i = 0; i<productItemsList.size();i++){
                                if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatSellPrice();
                                } else {
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matSellPrice = productItemsList.get(i).getMatSellPrice();
                                }
                            }

                        }
                    }
                });

        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        if (!dateEndVal.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEndVal);
        }
        if (!dateStartVal.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStartVal);
        }
        if (!dateStartVal.isEmpty()&&!dateEndVal.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStartVal).endAt(dateEndVal);
        }
        if (dateStartVal.isEmpty()&&dateEndVal.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (!rouidVal.isEmpty()){
                            if (Objects.equals(item.child("giCashedOutTo").getValue(), coDocumentID)) {
                                if (Objects.equals(item.child("giStatus").getValue(), true)
                                        && Objects.equals(item.child("giCashedOut").getValue(), true)
                                        && Objects.equals(item.child("giInvoiced").getValue(), false)
                                        && Objects.equals(item.child("giRecapped").getValue(), true)) {
                                    menu.findItem(R.id.select_all_data_recap).setVisible(true);
                                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                    goodIssueModelArrayList.add(goodIssueModel);
                                    //fabCreateGiRecap.show();
                                    nestedScrollView.setVisibility(View.VISIBLE);
                                    llNoData.setVisibility(View.GONE);
                                }

                            }
                        }

                    }
                    if (goodIssueModelArrayList.size()==0) {
                        fabCreateDocument.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    fabCreateDocument.hide();
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

        /*Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStartVal).endAt(dateEndVal);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (!rouidVal.isEmpty()){
                            if (Objects.requireNonNull(item.child("giRoUID").getValue()).toString().equals(rouidVal) &&
                                    !pouidVal.equals("-")) {
                                if (Objects.equals(item.child("giStatus").getValue(), true)
                                        && Objects.equals(item.child("giCashedOut").getValue(), true)
                                        && Objects.equals(item.child("giInvoiced").getValue(), false)
                                        && Objects.equals(item.child("giRecapped").getValue(), true)) {

                                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                    goodIssueModelArrayList.add(goodIssueModel);
                                    fabCreateGiRecap.show();
                                    nestedScrollView.setVisibility(View.VISIBLE);
                                    llNoData.setVisibility(View.GONE);

                                }
                            }

                        }

                    }
                    if (goodIssueModelArrayList.size()==0) {
                        fabCreateGiRecap.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    fabCreateGiRecap.hide();
                    nestedScrollView.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                }

                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void searchQueryAll(){
/*
        db.collection("ReceivedOrderData").orderBy("invDateCreated").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (productItemsList != null){
                        productItemsList.clear();
                    }
                    transportServiceSellPrice = 0;
                    matSellPrice = 0;

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                        receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                        matTypeVal = receivedOrderModel.getRoMatType();
                        roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        custNameVal = receivedOrderModel.getRoCustName();
                        currencyVal = receivedOrderModel.getRoCurrency();
                        invCustName = receivedOrderModel.getRoCustName();
                        invPoUID = receivedOrderModel.getRoPoCustNumber();
                        invPoDate = receivedOrderModel.getRoDateCreated();
                        invPoType = receivedOrderModel.getRoType();

                        if (invPoType == 0){
                            invPotypeVal = "MATERIAL + JASA ANGKUT";
                        }
                        if (invPoType == 1){
                            invPotypeVal = "MATERIAL SAJA";
                        }
                        if (invPoType == 2){
                            invPotypeVal = "JASA ANGKUT SAJA";
                        }

                        HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            productItemsList = e.getValue();
                            for (int i = 0; i<productItemsList.size();i++){
                                if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatSellPrice();
                                } else {
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matSellPrice = productItemsList.get(i).getMatSellPrice();
                                }
                            }

                        }
                    }
                });
*/

        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (Objects.equals(item.child("giStatus").getValue(), true)
                                && Objects.equals(item.child("giCashedOut").getValue(), true)
                                && Objects.equals(item.child("giInvoiced").getValue(), false)
                                && Objects.equals(item.child("giRecapped").getValue(), true)) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                            fabCreateDocument.show();
                            nestedScrollView.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);
                        }

                    }
                    if (goodIssueModelArrayList.size()==0) {
                        fabCreateDocument.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    fabCreateDocument.hide();
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
        if (item.getItemId() == R.id.filter_data_recap) {
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
                giManagementAdapter.selectAll();
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
                giManagementAdapter.clearSelection();
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

        helper.ACTIVITY_NAME = null;
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helper.ACTIVITY_NAME = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.ACTIVITY_NAME = null;
    }
}