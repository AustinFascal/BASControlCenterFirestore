package com.ptbas.controlcenter.create;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.CashOutManagementAdapter;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.adapter.InvoiceManagementAdapter;
import com.ptbas.controlcenter.model.CashOutModel;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.InvoiceModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utility.DialogInterface;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.utils.LangUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AddAIOReportActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    double matQuantity, matSellPrice, transportServiceSellPrice;
    String invDateNTimeCreated, invTimeCreated, dateStartVal = "", dateEndVal = "", rouidVal= "", currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal, transportServiceNameVal,
            invPoDate = "", invCustName = "", invPoUID = "", custNameVal = "", roDocumentID = "", coDocumentID, coUID, invUID="", bankAccountID = "";
    int invPoType;

    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID, spinnerCustName;
    TextInputEditText edtPoUID, edtDateStart, edtDateEnd;
    DatePickerDialog datePicker;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    ArrayList<CashOutModel> cashOutModelArrayList = new ArrayList<>();
    ArrayList<InvoiceModel> invoiceModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    CashOutManagementAdapter cashOutManagementAdapter;
    InvoiceManagementAdapter invoiceManagementAdapter;
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

    LinearLayout llShowSpinnerRoAndEdtPo, llWrapFilterByDateRange, llWrapFilterByRouid, llNoData, llWrapFilter, llBottomSelectionOptions;

    ImageButton btnResetCustomer, btnGiSearchByDateReset, btnGiSearchByRoUIDReset, btnExitSelection;

    ExtendedFloatingActionButton fabCreateDocument;

    DialogInterface dialogInterface = new DialogInterface();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontMediumSmall, fontNormal, fontNormalSmall, fontNormalSmallItalic,
            fontMedium, fontMediumWhite, fontBold, fontTransparent;
    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat dfRound = new DecimalFormat("0");

    Vibrator vibrator;

    boolean isSelectedAll = false;

    private Menu menu;

    public String invDateDeliveryPeriodVal;
    String invDateDeliveryPeriod, invCreatedBy="", custDocumentID ="", selectedCustName;

    double totalUnitAmountForMaterials;

    double matBuyPrice, coTotal;

    public String[] coDateAndTimeACC, deliveryPeriod;

    public String coTransferDate, coInvDocumentUID;

    public Double coTotalDue;
    public int showOccurances;

    String custNameFinal;

    StringBuilder s4;

    Document document;




    double priceListTest;
    String datePaid;

    private static final ArrayList<InvoiceModel> mArrayList = new ArrayList<>();
    private static final ArrayList<CashOutModel> mArrayListCO = new ArrayList<>();
    private static final ArrayList<Double> mArrayListTotalDue = new ArrayList<>();
    private static final ArrayList<String> mArrayListDatePaid = new ArrayList<>();

    String invTransferDate, invTotalDue, coDocumentUID, testID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aio_report);

        s4 = new StringBuilder(100);

        dfRound.setRoundingMode(RoundingMode.HALF_UP);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LangUtils.setLocale(this, "en");

        helper.ACTIVITY_NAME = "UPDATE";

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
        fontMediumSmall = new Font(baseMedium, 7, Font.NORMAL, BaseColor.BLACK);
        fontNormalSmall = new Font(baseNormal, 7, Font.NORMAL, BaseColor.BLACK);
        fontNormalSmallItalic = new Font(baseNormal, 8, Font.ITALIC, BaseColor.BLACK);
        fontMedium = new Font(baseMedium, 10, Font.NORMAL, BaseColor.BLACK);
        fontMediumWhite = new Font(baseMedium, 7, Font.NORMAL, BaseColor.WHITE);
        fontBold = new Font(baseBold, 9, Font.NORMAL, BaseColor.BLACK);
        fontTransparent = new Font(baseNormal, 20, Font.NORMAL, null);

        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);
        btnSearchData = findViewById(R.id.caridata);

        spinnerCustName = findViewById(R.id.spinnerCustName);
        spinnerRoUID = findViewById(R.id.rouid);
        edtPoUID = findViewById(R.id.pouid);
        edtDateStart = findViewById(R.id.edt_gi_date_filter_start);
        edtDateEnd = findViewById(R.id.edt_gi_date_filter_end);
        rvGoodIssueList = findViewById(R.id.rvItemList);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);
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

        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByRoUIDReset = findViewById(R.id.btnResetRouid);
        fabCreateDocument = findViewById(R.id.fabCreateCOR);

        /*TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;*/

        /*btnGiSearchByDateReset.setColorFilter(color);
        btnGiSearchByRoUIDReset.setColorFilter(color);
        btnResetBankAccount.setColorFilter(color);
        btnResetCustomer.setColorFilter(color);
        btnResetCoUID.setColorFilter(color);*/

        ActionBar actionBar = getSupportActionBar();



        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Buat Laporan All-In-One");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);


        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        edtDateStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(AddAIOReportActivity.this,
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

            datePicker = new DatePickerDialog(AddAIOReportActivity.this,
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
                Toast.makeText(AddAIOReportActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });

        // CREATE GI MANAGEMENT ADAPTER
        giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);
        //invoiceManagementAdapter = new InvoiceManagementAdapter(this, invoiceModelArrayList);
        cashOutManagementAdapter = new CashOutManagementAdapter(this, cashOutModelArrayList);
        invoiceManagementAdapter = new InvoiceManagementAdapter(this, invoiceModelArrayList);

        // HIDE FAB CREATE COR ON CREATE
        //fabCreateDocument.animate().translationY(800).setDuration(100).start();

        // NOTIFY REAL-TIME CHANGES AS USER CHOOSE THE ITEM
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {

                // CHECK IF DATE AND RO/PO NUMBER IS SELECTED
                /*if (!spinnerRoUID.getText().toString().isEmpty()
                        && !Objects.requireNonNull(edtPoUID.getText()).toString().isEmpty()){

                    int itemSelectedSize = invoiceManagementAdapter.getSelected().size();
                    //float itemSelectedVolume = invoiceManagementAdapter.getSelectedVolume();
                    String itemSelectedSizeVal = String.valueOf(itemSelectedSize).concat(" item terpilih");
                    //String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
                    if (giManagementAdapter.getSelected().size()>0){

                        fabCreateDocument.animate().translationY(0).setDuration(100).start();

                        tvTotalSelectedItem.setText(itemSelectedSizeVal);
                       // tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);
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
                }*/

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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAIOReportActivity.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCustName.setAdapter(arrayAdapter);
                        } else {
                            Toast.makeText(AddAIOReportActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        spinnerCustName.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedCustomer = (String) adapterView.getItemAtPosition(position);
            String[] custNameSplit = selectedCustomer.split(" - ");
            String custNameSplit1 = custNameSplit[1];

            selectedCustName = selectedCustomer;
            spinnerCustName.setError(null);
            spinnerRoUID.setText(null);
            edtPoUID.setText(null);

            btnResetCustomer.setVisibility(View.VISIBLE);
            clearRoPoData();

            llShowSpinnerRoAndEdtPo.setVisibility(View.VISIBLE);

            arrayListRoUID.clear();
            spinnerRoUID.setAdapter(null);

            db.collection("CustomerData").whereEqualTo("custName", custNameSplit1)
                    .addSnapshotListener((value2, error2) -> {
                        arrayListRoUID.clear();
                        if (!Objects.requireNonNull(value2).isEmpty()) {
                            for (DocumentSnapshot d : value2.getDocuments()) {
                                custDocumentID = Objects.requireNonNull(d.get("custDocumentID")).toString();

                                db.collection("ReceivedOrderData").whereEqualTo("custDocumentID", custDocumentID)
                                        .addSnapshotListener((value, error) -> {
                                            if (!Objects.requireNonNull(value).isEmpty()) {
                                                for (DocumentSnapshot e : value.getDocuments()) {
                                                    String spinnerPurchaseOrders = Objects.requireNonNull(e.get("roPoCustNumber")).toString();
                                                    arrayListRoUID.add(spinnerPurchaseOrders);
                                                }
                                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAIOReportActivity.this, R.layout.style_spinner, arrayListRoUID);
                                                arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                spinnerRoUID.setAdapter(arrayAdapter);
                                            }
                                        });
                            }

                        }
                    });

        });
        spinnerCustName.setOnKeyListener((view, i, keyEvent) -> {
            if (spinnerCustName.getText().toString().equals("")){
                llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
            }
            return false;
        });
        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            btnGiSearchByRoUIDReset.setVisibility(View.VISIBLE);

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(documentSnapshot.getId());
                            rouidVal = selectedSpinnerPoPtBasNumber;
                            roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                            roDocumentID = receivedOrderModel.getRoDocumentID();


                        }
                        edtPoUID.setText(roPoCustNumber);
                    });

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
            spinnerRoUID.setText(null);
            spinnerRoUID.requestFocus();
            edtPoUID.setText(null);
            edtPoUID.clearFocus();
            coUID = "";
            coDocumentID = "";
            roDocumentID = "";
            rouidVal = "";
            pouidVal = "";

            btnGiSearchByRoUIDReset.setVisibility(View.GONE);
        });

        btnResetCustomer.setOnClickListener(view -> {
            menu.findItem(R.id.select_all_data_recap).setVisible(false);
            clearSelection();
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

            arrayListRoUID.clear();
            spinnerRoUID.setAdapter(null);

            btnResetCustomer.setVisibility(View.GONE);
            btnGiSearchByRoUIDReset.setVisibility(View.GONE);
        });

        btnSearchData.setOnClickListener(view -> {
            mArrayListCO.clear();
            mArrayList.clear();
            mArrayListTotalDue.clear();
            mArrayListDatePaid.clear();

            coInvDocumentUID = "";
            totalUnitAmountForMaterials = 0;

            View viewLayout = AddAIOReportActivity.this.getCurrentFocus();
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


            if (!spinnerCustName.getText().toString().isEmpty()&&
                    !spinnerRoUID.getText().toString().isEmpty()&&
                    !edtPoUID.getText().toString().isEmpty()){
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

                String roUIDValNoSpace = rouidVal.replaceAll("\\s","");
                invUID = getRandomString()+"-INV-"+roUIDValNoSpace;
                StringBuilder s0 = new StringBuilder(100);

               /* for (int i=0; i<cashOutManagementAdapter.getSelected().size();i++) {
                    s0.append(cashOutManagementAdapter.getSelected().get(i).getCoDateDeliveryPeriod()).append(",");
                }*/
                for (int i=0; i<invoiceManagementAdapter.getSelected().size();i++) {
                    s0.append(invoiceManagementAdapter.getSelected().get(i).getInvDateDeliveryPeriod()).append(",");
                }

                String invDateDeliveryPeriod = s0.toString().replace("[","").replace("]","").replace(" ","");
                invDateDeliveryPeriod = removeDuplicates(invDateDeliveryPeriod, "\\,");

                String[] strings = invDateDeliveryPeriod.split(",");
                List<String> list = Arrays.asList(strings);
                Collections.sort(list);

                invDateDeliveryPeriodVal = list.toString().replace("[","").replace("]","").replace(" ","");

                String invCreatedBy = helper.getUserId();

                invTimeCreated =
                        new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                invDateNTimeCreated =
                        new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()).concat(" | "+invTimeCreated+" WIB");

                dialogInterface.confirmCreateAIO(context, db,
                        goodIssueModelArrayList,
                        invUID,  invCreatedBy,
                        invDateNTimeCreated,  "-", "", "",
                        "", invDateDeliveryPeriod,
                        custDocumentID,  bankAccountID,  roDocumentID, "", "");

            }
        });

        btnExitSelection.setOnClickListener(view -> clearSelection());

        searchQueryAll();
    }

    public static String removeDuplicates(String txt, String splitterRegex)
    {
        List<String> values = new ArrayList<String>();
        String[] splitted = txt.split(splitterRegex);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < splitted.length; ++i)
        {
            if (!values.contains(splitted[i]))
            {
                values.add(splitted[i]);
                sb.append(',');
                sb.append(splitted[i]);

            }

        }
        return sb.substring(1);
    }

    private void clearRoPoData() {
        rouidVal = null;
        pouidVal = null;

        spinnerRoUID.setText(null);
        edtPoUID.setText(null);
    }

    private  void clearSelection(){
        isSelectedAll = false;
        menu.findItem(R.id.select_all_data_recap).setIcon(ContextCompat.getDrawable(AddAIOReportActivity.this, R.drawable.ic_outline_select_all));

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

    public void createAIOPDF(String dest){

        if (new File(dest).exists()){
            new File(dest).deleteOnExit();
        }


        db.collection("InvoiceData").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<InvoiceModel> invoiceModels = queryDocumentSnapshots.toObjects(InvoiceModel.class);
                        mArrayList.addAll(invoiceModels);
                        db.collection("CashOutData").get()
                                .addOnSuccessListener(queryDocumentSnapshotsCo -> {
                                    if (!queryDocumentSnapshotsCo.isEmpty()) {
                                        List<CashOutModel> cashOutModels = queryDocumentSnapshotsCo.toObjects(CashOutModel.class);
                                        mArrayListCO.addAll(cashOutModels);
                                    }
                                    try {
                                        Rectangle f4Landscape = new Rectangle(936, 596);
                                        document = new Document(f4Landscape, 10, 10, 10, 10);
                                        PdfWriter.getInstance(document, new FileOutputStream(dest));
                                        document.open();
                                        document.addAuthor("PT BAS");
                                        document.addCreator("BAS Control Center");
                                        document.addCreationDate();
                                        addAIOTtl(document);
                                        addSpace(document);

                                        Paragraph paragraphBlank = new Paragraph(" ");

                                        Paragraph paragraphInvDateCreated =
                                                new Paragraph("Terakhir diperbarui: "
                                                        +invDateNTimeCreated+", oleh: "+invCreatedBy, fontNormalSmallItalic);
                                        paragraphInvDateCreated.setAlignment(Element.ALIGN_RIGHT);
                                        paragraphInvDateCreated.setSpacingAfter(5);

                                        Paragraph paragraphInvNote =
                                                new Paragraph("Mat.: Material; Sup.: Supplier; HJ: Harga Jual; HB: Harga Beli; PPN = 11%; PPH23 = 2%.", fontNormalSmall);
                                        paragraphInvDateCreated.setAlignment(Element.ALIGN_RIGHT);
                                        paragraphInvDateCreated.setSpacingAfter(5);

                                        // INIT TABLE
                                        PdfPTable tblInvSection0 = new PdfPTable(3);
                                        PdfPTable tblInvSection1 = new PdfPTable(18);
                                        PdfPTable tblInvSection2 = new PdfPTable(18);
                                        PdfPTable tblInvSection3 = new PdfPTable(18);

                                        // WIDTH PERCENTAGE CONFIG
                                        tblInvSection0.setWidthPercentage(100);
                                        tblInvSection1.setWidthPercentage(100);
                                        tblInvSection2.setWidthPercentage(100);
                                        tblInvSection3.setWidthPercentage(100);

                                        // WIDTH FLOAT CONFIG
                                        tblInvSection0.setWidths(new float[]{1,42,16});
                                        tblInvSection1.setWidths(new float[]{1,3,2,3,4,4,3,4,4,4,4,4,3,3,4,4,3,2});
                                        tblInvSection2.setWidths(new float[]{1,3,2,3,4,4,3,4,4,4,4,4,3,3,4,4,3,2});
                                        tblInvSection3.setWidths(new float[]{1,3,2,3,4,4,3,4,4,4,4,4,3,3,4,4,3,2});


                                        tblInvSection0.addCell(cellTxtNrml(
                                                new Paragraph("", fontMediumSmall),
                                                Element.ALIGN_CENTER));

                                        tblInvSection0.addCell(cellTxtNrml(
                                                new Paragraph("PENJUALAN", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection0.addCell(cellTxtNrml(
                                                new Paragraph("PEMBELIAN", fontMediumSmall),
                                                Element.ALIGN_CENTER));

                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("No", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Tgl. Kirim", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Unit", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("HJ Mat.", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Total Mat.", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("PPN Mat.", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("HJ Jasa", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Total Jasa", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("PPH23 Jasa", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("PPN Jasa", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Total Jual", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Dibayar", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Tgl. Masuk", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("HB", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Total Beli", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Tf. Sup.", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("Tgl. Keluar", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("", fontMediumSmall),
                                                Element.ALIGN_CENTER));


                                        for (int t = 0; t<18; t++){
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph("-", fontMediumWhite), Element.ALIGN_CENTER));
                                        }

                                        deliveryPeriod = invDateDeliveryPeriodVal.split(",");

                                        for (int i = 0; i < deliveryPeriod.length; i++) {
                                            for (int j = 0; j < goodIssueModelArrayList.size(); j++) {
                                                for (int k = 0; k < invoiceManagementAdapter.getSelected().size(); k++) {
                                                    if (goodIssueModelArrayList.get(j).getGiDateCreated().equals(deliveryPeriod[i])
                                                            && goodIssueModelArrayList.get(j).getGiInvoicedTo().equals(invoiceManagementAdapter.getSelected().get(k).getInvDocumentUID())) {
                                                        totalUnitAmountForMaterials += goodIssueModelArrayList.get(j).getGiVhlCubication();
                                                        invTransferDate = invoiceManagementAdapter.getSelected().get(k).getInvDateVerified();
                                                        invTotalDue = invoiceManagementAdapter.getSelected().get(k).getInvTotalDue();
                                                        coDocumentUID = invoiceManagementAdapter.getSelected().get(k).getInvDocumentUID();
                                                        testID = goodIssueModelArrayList.get(j).getGiCashedOutTo();
                                                    }
                                                }
                                            }





                                            double totalAmountForMaterials;
                                            double totalAmountMatBuyPrice;
                                            double totalAmountForTransportService;
                                            double taxPPH;
                                            double taxPPN;
                                            double taxPPNService;

                                            totalAmountForMaterials = matSellPrice * Double.parseDouble(df.format(totalUnitAmountForMaterials));
                                            totalAmountMatBuyPrice = matBuyPrice * Double.parseDouble(df.format(totalUnitAmountForMaterials));
                                            totalAmountForTransportService = transportServiceSellPrice * totalUnitAmountForMaterials;

                                            taxPPH = Double.parseDouble(df.format(0.02 * totalAmountForTransportService));
                                            taxPPN = Double.parseDouble(df.format(0.11 * totalAmountForMaterials));
                                            taxPPNService = Double.parseDouble(df.format(0.11 * totalAmountForTransportService));

                                            double totalDue = totalAmountForMaterials + taxPPN + totalAmountForTransportService - taxPPH + taxPPNService;

                                            String rowNumberStrVal = String.valueOf(i + 1);
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(rowNumberStrVal, fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(deliveryPeriod[i], fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(df.format(totalUnitAmountForMaterials), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(matSellPrice)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalAmountForMaterials)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(taxPPN)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(transportServiceSellPrice)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalAmountForTransportService)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph("(" + currencyFormat(df.format(taxPPH)) + ")", fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(taxPPNService)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalDue)), fontNormalSmall), Element.ALIGN_CENTER));

                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(invTotalDue, fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(invTransferDate, fontNormalSmall), Element.ALIGN_CENTER));




                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(matBuyPrice)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalAmountMatBuyPrice)), fontNormalSmall), Element.ALIGN_CENTER));


                                            for (int z = 0; z < mArrayListCO.size(); z++){

                                                if (mArrayListCO.get(z).getCoDocumentID().equals(testID)){
                                                    priceListTest = mArrayListCO.get(z).getCoTotal();
                                                    datePaid = mArrayListCO.get(z).getCoDateAndTimeACC();
                                                    mArrayListTotalDue.add(priceListTest);
                                                    mArrayListDatePaid.add(datePaid);
                                                }

                                            }
                                            if (!mArrayListCO.isEmpty()){
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph(currencyFormat(dfRound.format(priceListTest)), fontNormalSmall), Element.ALIGN_CENTER));

                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph(datePaid, fontNormalSmall), Element.ALIGN_CENTER));
                                            } else{
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                            }

                                            /*tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(datePaid, fontNormalSmall), Element.ALIGN_CENTER));*/

                                               /* if (!mArrayListTotalDue.isEmpty()){
                                                    tblInvSection2.addCell(cellTxtNrml(
                                                            new Paragraph(mArrayListTotalDue.get(z).toString(), fontNormalSmall), Element.ALIGN_CENTER));
                                                } else {
                                                    tblInvSection2.addCell(cellTxtNrml(
                                                            new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                                }
                                                if (!mArrayListDatePaid.isEmpty()) {
                                                    tblInvSection2.addCell(cellTxtNrml(
                                                            new Paragraph(mArrayListDatePaid.get(z), fontNormalSmall), Element.ALIGN_CENTER));
                                                } else{
                                                    tblInvSection2.addCell(cellTxtNrml(
                                                            new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                                }*/

                                            //Toast.makeText(context, mArrayListDatePaid.toString(), Toast.LENGTH_SHORT).show();
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));

                                                /*tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));*/

                                            totalUnitAmountForMaterials = 0;
                                            showOccurances = 0;

                                            coInvDocumentUID = "";

                                            for (int t = 0; t<18; t++){
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("-", fontMediumWhite), Element.ALIGN_CENTER));
                                            }
                                        }

                                        document.add(tblInvSection0);
                                        document.add(tblInvSection1);
                                        document.add(tblInvSection2);
                                        document.add(paragraphBlank); // SPACE SEPARATOR
                                        document.add(paragraphInvNote);
                                        document.add(paragraphInvDateCreated);

                                        document.close();
                                        dialogInterface.aioDocumentGeneratedInformation(context, dest);


                                    } catch (DocumentException | FileNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                    }

                                });
                    }

                });
    }

    private void addAIOTtl(Document document) throws DocumentException {
        Paragraph preface1 = new Paragraph();
        Chunk title = new Chunk("LAPORAN PENGIRIMAN "+matNameVal+" "+matTypeVal+"\n"+custNameFinal+" | PO: "+pouidVal+" | "+currencyFormat(df.format(matQuantity)) +" m3", fontBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface1.setAlignment(Element.ALIGN_CENTER);
        document.add(preface1);
    }

    private void addSpace(Document document) throws DocumentException {
        Paragraph preface1 = new Paragraph();
        Chunk title = new Chunk("-", fontMediumWhite);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface1.setAlignment(Element.ALIGN_CENTER);
        preface1.setSpacingAfter(1);
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

/*

    public static int math(double d) {
        int c = (int) ((d) + 0.5d);
        double n = d + 0.5d;
        return (n - c) % 2 == 0 ? (int) d : c;
    }
*/

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private void searchQuery(){
        showHideFilterComponents(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }

        expandFilterViewValidation();
        TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());

        rouidVal = spinnerRoUID.getText().toString();
        pouidVal = Objects.requireNonNull(edtPoUID.getText()).toString();

        CollectionReference refRO = db.collection("ReceivedOrderData");

        refRO.whereEqualTo("roDocumentID", roDocumentID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
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

                        HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            productItemsList = e.getValue();
                            for (int i = 0; i<productItemsList.size();i++){
                                if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();
                                } else {
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matQuantity = productItemsList.get(i).getMatQuantity();
                                    matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                    matSellPrice = productItemsList.get(i).getMatSellPrice();
                                }
                            }
                        }

                        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                goodIssueModelArrayList.clear();
                                if (snapshot.exists()){
                                    for (DataSnapshot item : snapshot.getChildren()) {
                                        if (Objects.equals(item.child("roDocumentID").getValue(), roDocumentID)) {
                                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                            goodIssueModelArrayList.add(goodIssueModel);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        CollectionReference refCO = db.collection("CashOutData");
                        refCO.whereEqualTo("roDocumentID", roDocumentID).get()
                                .addOnSuccessListener(queryDocumentSnapshotsCO -> {
                                    for (QueryDocumentSnapshot documentSnapshotCO : queryDocumentSnapshotsCO){
                                        CashOutModel cashOutModel = documentSnapshotCO.toObject(CashOutModel.class);
                                        coTotal =  cashOutModel.getCoTotal();
                                        coDateAndTimeACC = cashOutModel.getCoDateAndTimeACC().split(" ");
                                    }
                                });

                        CollectionReference refCust = db.collection("CustomerData");
                        refCust.whereEqualTo("custDocumentID", custNameVal).get()
                                .addOnSuccessListener(queryDocumentSnapshotsCust -> {
                                    for (QueryDocumentSnapshot documentSnapshotCust : queryDocumentSnapshotsCust){
                                        CustomerModel customerModel = documentSnapshotCust.toObject(CustomerModel.class);
                                        custNameFinal = customerModel.getCustName();
                                    }
                                });

                    }
                });

        /*db.collection("CashOutData")
                .addSnapshotListener((value, error) -> {
                    cashOutModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            CashOutModel cashOutModel = d.toObject(CashOutModel.class);
                            if (cashOutModel.getRoDocumentID().contains(roDocumentID)) {
                                cashOutModelArrayList.add(cashOutModel);
                            }

                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        if (cashOutModelArrayList.size()==0) {
                            fabCreateDocument.hide();
                            nestedScrollView.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                        }
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(cashOutModelArrayList);
                    cashOutManagementAdapter = new CashOutManagementAdapter(context, cashOutModelArrayList);
                    rvGoodIssueList.setAdapter(cashOutManagementAdapter);
                });*/
        db.collection("InvoiceData")
                .addSnapshotListener((value, error) -> {
                    invoiceModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            InvoiceModel invoiceModel = d.toObject(InvoiceModel.class);
                            if (invoiceModel.getRoDocumentID().contains(roDocumentID)) {
                                invoiceModelArrayList.add(invoiceModel);
                            }

                        }
                        llNoData.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        if (invoiceModelArrayList.size()==0) {
                            fabCreateDocument.hide();
                            nestedScrollView.setVisibility(View.GONE);
                            llNoData.setVisibility(View.VISIBLE);
                        }
                    } else{
                        llNoData.setVisibility(View.VISIBLE);
                        nestedScrollView.setVisibility(View.GONE);
                    }
                    Collections.reverse(invoiceModelArrayList);
                    invoiceManagementAdapter = new InvoiceManagementAdapter(context, invoiceModelArrayList);
                    rvGoodIssueList.setAdapter(invoiceManagementAdapter);
                });
    }

    private void searchQueryAll(){
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