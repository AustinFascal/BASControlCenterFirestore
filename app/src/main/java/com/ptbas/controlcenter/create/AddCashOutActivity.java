package com.ptbas.controlcenter.create;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.ptbas.controlcenter.adapter.RecapGoodIssueManagementAdapter;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.helper.ImageAndPositionRenderer;
import com.ptbas.controlcenter.helper.NumberToWords;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RecapGIModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.model.SupplierModel;
import com.ptbas.controlcenter.update.UpdateCashOutActivity;
import com.ptbas.controlcenter.utils.LangUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class AddCashOutActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    float totalUnit;
    int invPoType;
    double matBuyPrice, transportServiceSellPrice;
    String rcpCoUID, rcpUIDVal, custDocumentID, roDocumentID, custIDVal, dateStartVal = "", dateEndVal = "", rouidVal= "", suppplieruidVal = "",
            currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal,
            invPoDate = "", invCustName = "", invPoUID = "", custNameVal = "",
            custAddressVal = "", coUID="", invPotypeVal = "", coCreatedBy="",
            supplierPayee, supplierBankAndAccountNumber, supplierAccountOwnerName,
            customerData = "", customerID ="", randomString="NULL", coDateDeliveryPeriod;
    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true, rcpGiStatus;

    LinearLayout llBottomSelectionOptions, llNoData, llWrapFilter, llShowSpinnerRoAndEdtPo, llWrapSupplierDetail;
    ImageButton btnExitSelection;
    TextView tvTotalSelectedItem, tvTotalSelectedItem2;
    ImageButton btnGiSearchByDateReset, btnResetRouid, btnResetCustomer, btnResetSupplier;
    ExtendedFloatingActionButton fabCreateCOR;
    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID, spinnerCustName, spinnerSupplierName;
    //spinnerRcpID
    TextInputEditText edtPoUID, edtDateStart, edtDateEnd, edtBankNameAndAccountNumber, edtAccountOwnerName, edtPayee;
    DatePickerDialog datePicker;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;
    CardView cdvFilter;
    View firstViewData;
    NestedScrollView nestedScrollView;

    Context context;
    Vibrator vibrator;
    Helper helper = new Helper();
    DialogInterface dialogInterface = new DialogInterface();
    List<String> arrayListRoUID, arrayListPoUID, arrayListSupplierUID;
    List<ProductItems> productItemsList;
    List<String> customerName, supplierName, recapID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontNormal, fontNormalSmall, fontNormalSmallItalic,
            fontMedium, fontMediumWhite, fontBold, fontTransparent;
    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");

    boolean isSelectedAll = false;

    private Menu menu;

    String getCoUIDFromCoDocumentID;

    RecapGoodIssueManagementAdapter recapGiManagementAdapter;
    ArrayList<RecapGIModel> recapGiModelArrayList = new ArrayList<>();

    List<String> receiveOrderNumberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash_out);

        LangUtils.setLocale(this, "en");

        helper.ACTIVITY_NAME = "UPDATE";

        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);

        btnSearchData = findViewById(R.id.caridata);
        btnExitSelection = findViewById(R.id.btnExitSelection);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnResetRouid = findViewById(R.id.btnResetRouid);
        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        btnResetSupplier = findViewById(R.id.btnResetSupplier);

        spinnerRoUID = findViewById(R.id.rouid);
        spinnerCustName = findViewById(R.id.spinnerCustName);
        spinnerSupplierName = findViewById(R.id.spinnerSupplierName);
        //spinnerRcpID = findViewById(R.id.spinnerRcpID);
        spinnerSupplierName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerCustName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        spinnerRoUID.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        //spinnerRcpID.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        edtPoUID = findViewById(R.id.pouid);
        edtDateStart = findViewById(R.id.edt_gi_date_filter_start);
        edtDateEnd = findViewById(R.id.edt_gi_date_filter_end);
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

        helper.ACTIVITY_NAME = "UPDATE";

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

       /* btnGiSearchByDateReset.setColorFilter(color);
        btnResetRouid.setColorFilter(color);
        btnResetCustomer.setColorFilter(color);
        btnResetSupplier.setColorFilter(color);*/

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Buat Cash Out");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCashOutActivity.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCustName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(AddCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCashOutActivity.this, R.layout.style_spinner, supplierName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerSupplierName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(AddCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
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


                //String selectedSpinnerCustomerName = (String) adapterView.getItemAtPosition(position);
                //customerData = selectedSpinnerCustomerName;
                //spinnerCustName.setError(null);
                //String[] custID =  selectedSpinnerCustomerName.split("-");
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
                                                        receiveOrderNumberList.add(spinnerPurchaseOrders);
                                                    }
                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCashOutActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                                                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                    spinnerRoUID.setAdapter(arrayAdapter);
                                                }
                                            });
                                }

                            }
                        });

            /*db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                    .addSnapshotListener((value, error) -> {
                        if (!Objects.requireNonNull(value).isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                db.collection("CustomerData").whereEqualTo("custDocumentID", Objects.requireNonNull(d.get("custDocumentID")).toString())
                                        .addSnapshotListener((value2, error2) -> {
                                            arrayListRoUID.clear();
                                            if (!Objects.requireNonNull(value2).isEmpty()) {
                                                for (DocumentSnapshot e : value2.getDocuments()) {
                                                    custIDVal = Objects.requireNonNull(e.get("custName")).toString();
                                                    db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                                                            .addSnapshotListener((value3, error3) -> {
                                                                if (!Objects.requireNonNull(value3).isEmpty()) {
                                                                    String spinnerPurchaseOrders = Objects.requireNonNull(d.get("roPoCustNumber")).toString();
                                                                    if (selectedSpinnerCustomerName.contains(custIDVal)) {
                                                                        arrayListRoUID.add(spinnerPurchaseOrders);
                                                                    }
                                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCashOutActivity.this, R.layout.style_spinner, arrayListRoUID);
                                                                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                                    spinnerRoUID.setAdapter(arrayAdapter);
                                                                }
                                                            });
                                                }
                                            } else {
                                                if (!isFinishing()) {
                                                    dialogInterface.roNotExistsDialog(AddCashOutActivity.this);
                                                }
                                            }
                                        });
                            }
                        }
                    });*/
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

        edtDateStart.setOnClickListener(view -> {
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
        });

        spinnerSupplierName.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerSupplierName.setError(null);
            String selectedSpinnerSupplierID = (String) adapterView.getItemAtPosition(i);

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
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(documentSnapshot.getId());
                            rouidVal = selectedSpinnerPoPtBasNumber;
                            roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                            roDocumentID = receivedOrderModel.getRoDocumentID();
                            custDocumentID = receivedOrderModel.getCustDocumentID();


                            /*db.collection("RecapData").whereEqualTo("roDocumentID", roDocumentID)
                                    .addSnapshotListener((value, error) -> {
                                        recapID.clear();
                                        if (value != null) {
                                            if (!value.isEmpty()) {
                                                for (DocumentSnapshot d : value.getDocuments()) {
                                                    String spinnerSupplierName = Objects.requireNonNull(d.get("rcpGiUID")).toString();
                                                    //String supplierID = Objects.requireNonNull(d.get("supplierID")).toString();
                                                    recapID.add(spinnerSupplierName);
                                                }
                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddCashOutActivity.this, R.layout.style_spinner, recapID);
                                                arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                spinnerRcpID.setAdapter(arrayAdapter);
                                            } else {
                                                Toast.makeText(AddCashOutActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });*/

                        }
                        edtPoUID.setText(roPoCustNumber);
                    });
        });



        recapGiManagementAdapter = new RecapGoodIssueManagementAdapter(this, recapGiModelArrayList);



        /*spinnerRcpID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRcpID.setError(null);
            String selectedSpinnerRecapID = (String) adapterView.getItemAtPosition(i);


            db.collection("RecapData").whereEqualTo("rcpGiUID", selectedSpinnerRecapID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            RecapGIModel recapGIModel = documentSnapshot.toObject(RecapGIModel.class);
                            recapGIModel.setRcpGiDocumentID(documentSnapshot.getId());
                            rcpUIDVal = recapGIModel.getRcpGiDocumentID();
                            rcpCoUID = recapGIModel.getRcpGiCoUID();

                            db.collection("CashOutData").whereEqualTo("coDocumentID", rcpCoUID).get()
                                    .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                        for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2) {
                                            CashOutModel cashOutModel = documentSnapshot2.toObject(CashOutModel.class);
                                            cashOutModel.setCoDocumentID(documentSnapshot2.getId());
                                            getCoUIDFromCoDocumentID = cashOutModel.getCoUID();

                                            if (!rcpCoUID.isEmpty()){
                                                MaterialDialog md = new MaterialDialog.Builder(this)
                                                        .setAnimation(R.raw.lottie_attention)
                                                        .setTitle("Cash Out Sudah Dibuat")
                                                        .setMessage("Rekap dengan ID "+selectedSpinnerRecapID+" telah dibuatkan Cash Out dengan ID Cash Out "+ getCoUIDFromCoDocumentID+".")
                                                        .setPositiveButton("Buka Detail CO", R.drawable.ic_outline_navigate_next, (dialogInterface, which) -> {
                                                            Intent intent = new Intent(context, UpdateCashOutActivity.class);
                                                            intent.putExtra("key", rcpCoUID);
                                                            context.startActivity(intent);
                                                            dialogInterface.dismiss();
                                                        })
                                                        .setNegativeButton("Batal", R.drawable.ic_outline_close, (dialogInterface, which) -> {
                                                            spinnerRcpID.setText(null);
                                                            dialogInterface.dismiss();
                                                        })
                                                        .setCancelable(false)
                                                        .build();

                                                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                                                md.show();
                                            }

                                        }
                                    });

                        }
                    });
        });*/


        //spinnerRoUID.setOnFocusChangeListener((view, b) -> spinnerRoUID.setText(rouidVal));

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
        btnGiSearchByDateReset.setOnClickListener(view -> {
            edtDateStart.setText(null);
            edtDateEnd.setText(null);
            edtDateStart.clearFocus();
            edtDateEnd.clearFocus();
            dateStartVal = "";
            dateEndVal = "";
            btnGiSearchByDateReset.setVisibility(View.GONE);
        });
        btnResetRouid.setOnClickListener(view -> {
            spinnerRoUID.setText(null);
            rouidVal = null;
            edtPoUID.setText(null);
            btnResetRouid.setVisibility(View.GONE);
            btnResetCustomer.setVisibility(View.GONE);
        });

        btnSearchData.setOnClickListener(view -> {
            View viewLayout = AddCashOutActivity.this.getCurrentFocus();
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

            /*if (Objects.requireNonNull(spinnerRcpID.getText()).toString().isEmpty()){
                spinnerRcpID.setError("");
            } else{
                spinnerRcpID.setError(null);
            }*/

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
                searchQuery();

            }
        });

        btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.findItem(R.id.select_all_data_recap).setIcon(ContextCompat.getDrawable(AddCashOutActivity.this, R.drawable.ic_outline_select_all));
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
        });

        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_add_recap_good_issue_data, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_date_range);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
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
                Toast.makeText(AddCashOutActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
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
                    /*List<String> datePeriod = new ArrayList<>();

                     *//*for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                        totalUnit += giManagementAdapter.getSelected().get(i).getGiVhlCubication();
                        datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
                    }*//*
                    HashSet<String> filter = new HashSet(datePeriod);
                    ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);
                    coDateDeliveryPeriod = String.valueOf(datePeriodFiltered);

                    double totalIDR = matBuyPrice *Double.parseDouble(df.format(totalUnit));
                    Toast.makeText(context, String.valueOf(totalIDR), Toast.LENGTH_SHORT).show();*/
                    //Toast.makeText(context, String.valueOf(totalUnit), Toast.LENGTH_SHORT).show();*/





                   /* for (int i = 0; i < rcpGiUID.size(); i++) {
                    }
*/





                    String coDateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

                    String coTimeCreated =
                            new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());


                    for (int l = 0; l<recapGiModelArrayList.size();l++){
                        if  (recapGiManagementAdapter.getSelected().get(l).getRcpGiCoUID().isEmpty()){
                            dialogInterface.confirmCreateCashOutProof(context, db,
                                    coUID, coDateCreated + " | " + coTimeCreated + " WIB",
                                    helper.getUserId(), "","", "", "",
                                    suppplieruidVal, roDocumentID, false, false, true, recapGiManagementAdapter);
                        } else  {


                                MaterialDialog materialDialog = new MaterialDialog.Builder((Activity) context)
                                        .setTitle("Cash Out Sudah Dibuat")
                                        .setAnimation(R.raw.lottie_generate_bill)
                                        .setMessage("Pembuatan Cash Out gagal karena Anda telah membuat Cash Out dari rekap ini. Lihat rekap?")
                                        .setCancelable(true)
                                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                                            dialogInterface.dismiss();
                                        })
                                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                                        .build();

                                materialDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                                materialDialog.show();

                        }
                    }


                    String custNameValReplace = custNameVal.replace(" - ","-");
                    int indexCustNameVal = custNameValReplace.lastIndexOf('-');

                    db.collection("CustomerData").whereEqualTo("custDocumentID",  custDocumentID).get()
                            .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                    CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                    custAddressVal = customerModel.getCustAddress();
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
        giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);

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

                    int itemSelectedSize = giManagementAdapter.getSelected().size();
                    float itemSelectedVolume = giManagementAdapter.getSelectedVolume();
                    //float itemSelectedBuyPrice = giManagementAdapter.getSelectedVolBuyPrice();
                    //String itemSelectedBuyPriceVal = df.format(itemSelectedBuyPrice);
                    String itemSelectedSizeVal = String.valueOf(itemSelectedSize).concat(" item terpilih");
                    String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
                    //.concat("IDR "+itemSelectedBuyPriceVal);

                    if (recapGiManagementAdapter.getSelected().size()>0){
                        fabCreateCOR.animate().translationY(0).setDuration(100).start();
                        fabCreateCOR.show();

                        //tvTotalSelectedItem.setText(itemSelectedSizeVal);
                        //tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);
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
                        //fabCreateCOR.animate().translationY(800).setDuration(100).start();
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

                   /* if (giManagementAdapter.getSelected().size()>0){

                        fabCreateCOR.animate().translationY(0).setDuration(100).start();
                        fabCreateCOR.show();

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

                        fabCreateCOR.animate().translationY(800).setDuration(100).start();
                        fabCreateCOR.hide();
                        //fabCreateCOR.animate().translationY(800).setDuration(100).start();
                        llBottomSelectionOptions.animate()
                                .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        llBottomSelectionOptions.setVisibility(View.GONE);
                                    }
                                });
                    }*/
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
    public void createCashOutProofPDF(String dest){
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
            addCoTtl(document);
            addCoMainContent(document);
            document.close();
            //dialogInterface.cashOutProofGeneratedInformation(context, dest);

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
    private void addCoMainContent(Document document) throws DocumentException{
        try {
            String invDateCreated =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
            String invTimeCreated =
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            Paragraph paragraphBlank = new Paragraph(" ");

            Paragraph paragraphInvDateCreated =
                    new Paragraph("Tanggal cetak: "
                            +invDateCreated+" "+invTimeCreated+" WIB, oleh: "+coCreatedBy, fontNormalSmallItalic);
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
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                totalUnitFinal += giManagementAdapter.getSelected().get(i).getGiVhlCubication();
            }

            // TOTAL AMOUNT CALCULATION
            double totalAmountForMaterials = matBuyPrice *totalUnitFinal;

            // INIT TABLE
            PdfPTable tblInvSection1 = new PdfPTable(7);
            PdfPTable tblInvSection2 = new PdfPTable(7);
            PdfPTable tblInvSection3 = new PdfPTable(7);
            PdfPTable tblInvSection4 = new PdfPTable(7);
            PdfPTable tblInvSection5 = new PdfPTable(4);
            PdfPTable tblInvSection6 = new PdfPTable(4);
            PdfPTable tblInvSection7 = new PdfPTable(2);
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
            tblInvSection7.setWidths(new float[]{1,1}); //2 COLS
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
                    new Paragraph(supplierBankAndAccountNumber, fontNormal),
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
                    new Paragraph(coUID, fontNormal),
                    Element.ALIGN_LEFT));

            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph("REK. A/N - DIBAYAR KE", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            tblInvSection3.addCell(cellTxtNoBrdrNrml(
                    new Paragraph(supplierAccountOwnerName + " - " + supplierPayee , fontNormal),
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
                    new Paragraph(pouidVal+"\n"+invPoDate+"\n"+invPotypeVal, fontNormal),
                    Element.ALIGN_LEFT));

            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Item# / Deskripsi", fontMedium), Element.ALIGN_LEFT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Harga Satuan", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Jumlah (Unit)", fontMedium), Element.ALIGN_RIGHT));
            tblInvSection5.addCell(cellColHeader(
                    new Paragraph("Jumlah", fontMedium), Element.ALIGN_RIGHT));

            List<String> datePeriod = new ArrayList<>();
            for (int i = 0; i < giManagementAdapter.getSelected().size(); i++) {
                datePeriod.add(giManagementAdapter.getSelected().get(i).getGiDateCreated());
            }
            HashSet<String> filter = new HashSet(datePeriod);
            ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);

            coDateDeliveryPeriod = String.valueOf(datePeriodFiltered).replace("[","").replace("]","").replace(" ","");

            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("Pengiriman Tanggal: "+datePeriodFiltered, fontNormal), Element.ALIGN_LEFT));
            tblInvSection7.addCell(cellTxtNoBrdrNrmlMainContent(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));

            List<String> deliveryPeriod = Arrays.asList(coDateDeliveryPeriod.split(","));

            for (int i = 0; i < deliveryPeriod.size(); i++) {
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(matNameVal, fontNormal), Element.ALIGN_LEFT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal + " " + currencyFormat(df.format(matBuyPrice)), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(df.format(totalUnitFinal), fontNormal), Element.ALIGN_RIGHT));
                tblInvSection6.addCell(cellTxtNoBrdrNrmlMainContent(
                        new Paragraph(currencyVal + " " + currencyFormat(df.format(totalAmountForMaterials)), fontNormal), Element.ALIGN_RIGHT));
            }

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
                    new Paragraph("\n\n\n\n", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontMedium),
                    Element.ALIGN_LEFT));
            tblInvSection13.addCell(cellTxtBrdrNrmlMainContent(
                    new Paragraph("\n\n\n\n", fontMedium),
                    Element.ALIGN_LEFT));


            document.add(tblInvSection1);
            document.add(tblInvSection2);
            document.add(tblInvSection3);
            document.add(paragraphBlank); // SPACE SEPARATOR
            document.add(tblInvSection5);
            document.add(tblInvSection7);
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
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());

        try {
            String roMatNameTypeStrVal = "Material: "+ matNameVal +" | "+ matTypeVal;
            String roCustNameStrVal = "Customer: "+custNameVal;
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

            float totalCubication = 0;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                String rowNumberStrVal = String.valueOf(i+1);
                String rowDateStrVal = goodIssueModelArrayList.get(i).getGiDateCreated();
                String rowIDStrVal = goodIssueModelArrayList.get(i).getGiUID().substring(0,5);
                String rowVhlUIDStrVal = goodIssueModelArrayList.get(i).getVhlUID();
                String rowVhLengthStrVal = goodIssueModelArrayList.get(i).getVhlLength().toString();
                String rowVhWidthStrVal = goodIssueModelArrayList.get(i).getVhlWidth().toString();
                String rowVhHeightStrVal = goodIssueModelArrayList.get(i).getVhlHeight().toString();
                String rowVhHeightCorrectionStrVal = goodIssueModelArrayList.get(i).getVhlHeightCorrection().toString();
                String rowVhHeightAfterCorrectionStrVal = goodIssueModelArrayList.get(i).getVhlHeightAfterCorrection().toString();
                String vhlCubicationStrVal = df.format(goodIssueModelArrayList.get(i).getGiVhlCubication());

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

                totalCubication += goodIssueModelArrayList.get(i).getGiVhlCubication();

            }

            String totalCubicationStrVal = df.format(totalCubication);
            double totalIDR = matBuyPrice *Double.parseDouble(df.format(totalCubication))    ;
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

    public static int math(double d) {
        int c = (int) ((d) + 0.5d);
        double n = d + 0.5d;
        return (n - c) % 2 == 0 ? (int) d : c;
    }

    private void searchQuery(){
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
        coUID = getRandomString2(5)+" - "+pouidVal;
/*
        //fabCreateCOR.animate().translationY(0).setDuration(100).start();

        db.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (productItemsList != null){
                        productItemsList.clear();
                    }

                    transportServiceSellPrice = 0;
                    matBuyPrice = 0;

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                        receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                        matTypeVal = receivedOrderModel.getRoMatType();
                        roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        //custNameVal = receivedOrderModel.getRoCustName();
                        currencyVal = receivedOrderModel.getRoCurrency();
                        invCustName = receivedOrderModel.getCustDocumentID();
                        invPoUID = receivedOrderModel.getRoUID();
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
                                    *//*transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();*//*
                                } else {
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matBuyPrice = productItemsList.get(i).getMatBuyPrice();
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
                            if (Objects.requireNonNull(item.child("roDocumentID").getValue()).toString().equals(roDocumentID)) {
                                if (Objects.equals(item.child("giStatus").getValue(), true)  &&
                                        Objects.equals(item.child("giRecapped").getValue(), true) &&
                                        Objects.equals(item.child("giRecappedTo").getValue(), rcpUIDVal)) {
                                    if (Objects.equals(item.child("giCashedOut").getValue(), false)) {
                                        menu.findItem(R.id.select_all_data_recap).setVisible(true);
                                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                        goodIssueModelArrayList.add(goodIssueModel);
                                        //fabCreateCOR.show();
                                        nestedScrollView.setVisibility(View.VISIBLE);
                                        llNoData.setVisibility(View.GONE);
                                    }
                                }
                            }

                        }

                    }
                    if (goodIssueModelArrayList.size()==0) {
                        //fabCreateCOR.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    fabCreateCOR.hide();
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
        });*/

        fabCreateCOR.animate().translationY(0).setDuration(100).start();

        db.collection("RecapData").orderBy("rcpGiDateAndTimeCreated")
                .addSnapshotListener((value, error) -> {
                    recapGiModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {

                            RecapGIModel recapGIModel = d.toObject(RecapGIModel.class);
                            if (recapGIModel.getRoDocumentID().contains(roDocumentID)) {
                                recapGiModelArrayList.add(recapGIModel);
                            }

                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        if (recapGiModelArrayList.size()==0) {
                            fabCreateCOR.hide();
                            nestedScrollView.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                        }
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(recapGiModelArrayList);
                    recapGiManagementAdapter = new RecapGoodIssueManagementAdapter(context, recapGiModelArrayList);
                    rvGoodIssueList.setAdapter(recapGiManagementAdapter);
                });


    }

    private void searchQueryAll(){
        /*Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (Objects.equals(item.child("giStatus").getValue(), true) &&
                                Objects.equals(item.child("giRecapped").getValue(), true)) {
                            if (Objects.equals(item.child("giCashedOut").getValue(), false)) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                goodIssueModelArrayList.add(goodIssueModel);
                                //fabCreateGiRecap.show();
                                nestedScrollView.setVisibility(View.VISIBLE);
                                llNoData.setVisibility(View.GONE);
                            }
                        }
                    }
                    if (goodIssueModelArrayList.size()==0) {
                        fabCreateCOR.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    fabCreateCOR.hide();
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
        });*/


        db.collection("RecapData").orderBy("rcpGiDateAndTimeCreated")
                .addSnapshotListener((value, error) -> {
                    recapGiModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            RecapGIModel recapGIModel = d.toObject(RecapGIModel.class);
                            recapGiModelArrayList.add(recapGIModel);
                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        if (recapGiModelArrayList.size()==0) {
                            fabCreateCOR.hide();
                            nestedScrollView.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                        }
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }

                    Collections.reverse(recapGiModelArrayList);
                    recapGiManagementAdapter = new RecapGoodIssueManagementAdapter(context, recapGiModelArrayList);
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
        onBackPressed();
        helper.ACTIVITY_NAME = null;
        custNameVal = "";
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helper.ACTIVITY_NAME = null;
        finish();
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