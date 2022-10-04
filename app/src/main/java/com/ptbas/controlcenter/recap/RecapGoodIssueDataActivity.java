package com.ptbas.controlcenter.recap;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.ptbas.controlcenter.create.AddInvoiceActivity;
import com.ptbas.controlcenter.helper.DialogInterface;
import com.ptbas.controlcenter.helper.DragLinearLayout;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.helper.Helper;
import com.ptbas.controlcenter.model.CustomerModel;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utils.LangUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class RecapGoodIssueDataActivity extends AppCompatActivity {

    float totalUnit;
    double matBuyPrice;
    String dateStartVal = "", dateEndVal = "", rouidVal= "", currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal, selectedCustName = "";
    public String custNameVal = "";

    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID, spinnerCustUID;
    TextInputEditText edtPoUID, edtDateStart, edtDateEnd;
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

    ImageButton btnExitSelection;
    TextView tvTotalSelectedItem, tvTotalSelectedItem2;

    List<String> arrayListRoUID, customerList;

    LinearLayout llWrapFilter, llWrapFilterByDateRange, llWrapFilterByRouid, llNoData, llBottomSelectionOptions, llShowSpinnerRoAndEdtPo;

    ImageButton btnGiSearchByDateReset, btnGiSearchByRoUIDReset, btnResetCustomer;

    ExtendedFloatingActionButton fabCreateGiRecap;

    DialogInterface dialogInterface = new DialogInterface();

    private static final Font fontNormal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font fontBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font fontBigBold = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DecimalFormat df = new DecimalFormat("0.00");

    Vibrator vibrator;

    //List<String> receiveOrderNumberList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_good_issue_data);

        context = this;

        vibrator  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //receiveOrderNumberList = new ArrayList<>();

        cdvFilter = findViewById(R.id.cdv_filter);
        btnSearchData = findViewById(R.id.caridata);
        btnExitSelection = findViewById(R.id.btnExitSelection);

        spinnerCustUID = findViewById(R.id.spinnerCustName);
        spinnerRoUID = findViewById(R.id.rouid);
        edtPoUID = findViewById(R.id.pouid);
        edtDateStart = findViewById(R.id.edt_gi_date_filter_start);
        edtDateEnd = findViewById(R.id.edt_gi_date_filter_end);
        rvGoodIssueList = findViewById(R.id.rvItemList);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtnExpandCollapseFilterLayout);
        llWrapFilter = findViewById(R.id.llWrapFilter);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llWrapFilterByRouid = findViewById(R.id.ll_wrap_filter_by_rouid);
        llBottomSelectionOptions = findViewById(R.id.llBottomSelectionOptions);
        llShowSpinnerRoAndEdtPo = findViewById(R.id.llShowSpinnerRoAndEdtPo);

        llNoData = findViewById(R.id.ll_no_data);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        btnResetCustomer = findViewById(R.id.btnResetCustomer);
        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByRoUIDReset = findViewById(R.id.btnResetRouid);
        fabCreateGiRecap = findViewById(R.id.fabCreateCOR);

        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        tvTotalSelectedItem2 = findViewById(R.id.tvTotalSelectedItem2);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        btnGiSearchByDateReset.setColorFilter(color);
        btnGiSearchByRoUIDReset.setColorFilter(color);

        ActionBar actionBar = getSupportActionBar();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Rekap GI Untuk Ditagihkan");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // DRAGLINEARLAYOUT FOR FILTERING
        /*DragLinearLayout dragLinearLayout = findViewById(R.id.drag_linear_layout);
        for(int i = 0; i < dragLinearLayout.getChildCount(); i++){
            View child = dragLinearLayout.getChildAt(i);
            // the child will act as its own drag handle
            dragLinearLayout.setViewDraggable(child, child);
        }*/

        /*dragLinearLayout.setOnViewSwapListener((firstView, firstPosition,
                                                secondView, secondPosition) -> {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100,
                        VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(100);
            }
            firstViewData = firstView;
        });*/

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        edtDateStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(RecapGoodIssueDataActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {
                        int monthInt = month + 1;

                        if(month < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }
                        if(dayOfMonth < 10){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtDateStart.setText(finalDate);
                        dateStartVal = finalDate;

                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePicker.show();
        });

        edtDateEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(RecapGoodIssueDataActivity.this,
                    (datePicker, year, month, dayOfMonth) -> {
                        int monthInt = month + 1;

                        if(month < 10){
                            monthStrVal = "0" + monthInt;
                        } else {
                            monthStrVal = String.valueOf(monthInt);
                        }
                        if(dayOfMonth < 10){
                            dayStrVal = "0" + dayOfMonth;
                        } else {
                            dayStrVal = String.valueOf(dayOfMonth);
                        }

                        String finalDate = year + "-" +monthStrVal + "-" + dayStrVal;

                        edtDateEnd.setText(finalDate);
                        dateEndVal = finalDate;

                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePicker.show();
        });

        imgbtnExpandCollapseFilterLayout.setOnClickListener(view -> {
            if (firstViewDataFirstTimeStatus){
                view = View.inflate(context, R.layout.activity_recap_good_issue_data, null);
                firstViewData = view.findViewById(R.id.ll_wrap_filter_by_date_range);
                firstViewDataFirstTimeStatus = false;
            }
            expandFilterViewValidation();
            TransitionManager.beginDelayedTransition(cdvFilter, new AutoTransition());
        });

        arrayListRoUID = new ArrayList<>();
        customerList = new ArrayList<>();
        //arrayListPoUID = new ArrayList<>();

        /*db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                .addSnapshotListener((value, error) -> {
                    arrayListRoUID.clear();
                    if (value != null) {
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot d : value.getDocuments()) {
                                String spinnerPurchaseOrders = Objects.requireNonNull(d.get("roUID")).toString();
                                arrayListRoUID.add(spinnerPurchaseOrders);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, arrayListRoUID);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerRoUID.setAdapter(arrayAdapter);
                        } else {
                            if(!this.isFinishing()) {
                                dialogInterface.roNotExistsDialog(RecapGoodIssueDataActivity.this);
                            }
                        }
                    }
                });*/

        // INIT DATA SPINNER CUSTOMER DATA
        db.collection("CustomerData").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    if (!value.isEmpty()) {
                        for (DocumentSnapshot d : value.getDocuments()) {
                            String custList = Objects.requireNonNull(d.get("custUID")).toString().concat(" - " + Objects.requireNonNull(d.get("custName")).toString());
                            customerList.add(custList);

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, customerList);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            spinnerCustUID.setAdapter(arrayAdapter);
                        }
                    }
                }
            }
        });

        spinnerCustUID.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedCustomer = (String) adapterView.getItemAtPosition(i);
            selectedCustName = selectedCustomer;
            spinnerCustUID.setError(null);
            spinnerRoUID.setText(null);
            edtPoUID.setText(null);

            btnResetCustomer.setVisibility(View.VISIBLE);
            clearRoPoData();

            db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
                    .addSnapshotListener((value, error) -> {
                        arrayListRoUID.clear();
                        if (value != null) {
                            if (!value.isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {
                                    String spinnerPurchaseOrders = Objects.requireNonNull(d.get("roPoCustNumber")).toString();
                                    if (Objects.requireNonNull(d.get("roCustName")).toString().equals(spinnerCustUID.getText().toString())) {
                                        arrayListRoUID.add(spinnerPurchaseOrders);
                                    }
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, arrayListRoUID);
                                arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                spinnerRoUID.setAdapter(arrayAdapter);
                            } else {
                                if(!isFinishing()) {
                                    dialogInterface.roNotExistsDialog(RecapGoodIssueDataActivity.this);
                                }
                            }
                        }

                    });
            llShowSpinnerRoAndEdtPo.setVisibility(View.VISIBLE);
        });
        spinnerCustUID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (spinnerCustUID.getText().toString().equals("")){
                    llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                }
                return false;
            }
        });
        spinnerCustUID.setOnFocusChangeListener((view, b) -> spinnerCustUID.setText(selectedCustName));


        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                            receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                            //String documentID = receivedOrderModel.getRoDocumentID();
                            rouidVal = selectedSpinnerPoPtBasNumber;
                            roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        }
                        edtPoUID.setText(roPoCustNumber);
                    });

        });

        btnGiSearchByDateReset.setOnClickListener(view -> {
            edtDateStart.setText(null);
            edtDateEnd.setText(null);
            edtDateStart.clearFocus();
            edtDateEnd.clearFocus();
            btnGiSearchByDateReset.setVisibility(View.GONE);
        });

        btnGiSearchByRoUIDReset.setOnClickListener(view -> {
            spinnerRoUID.setText(null);
            spinnerRoUID.clearFocus();
            btnGiSearchByRoUIDReset.setVisibility(View.GONE);
        });

        //fabCreateGiRecap.hide();

        // CREATE GI MANAGEMENT ADAPTER
        giManagementAdapter = new GIManagementAdapter(this, goodIssueModelArrayList);

        // HIDE FAB CREATE COR ON CREATE
        fabCreateGiRecap.animate().translationY(800).setDuration(100).start();

        // NOTIFY REAL-TIME CHANGES AS USER CHOOSE THE ITEM
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                // CHECK IF DATE AND RO/PO NUMBER IS SELECTED
                if (!spinnerRoUID.getText().toString().isEmpty()){

                    int itemSelectedSize = giManagementAdapter.getSelected().size();
                    float itemSelectedVolume = giManagementAdapter.getSelectedVolume();

                    //float itemSelectedBuyPrice = giManagementAdapter.getSelectedVolBuyPrice();
                    //String itemSelectedBuyPriceVal = df.format(itemSelectedBuyPrice);
                    String itemSelectedSizeVal = String.valueOf(itemSelectedSize).concat(" item terpilih");
                    String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");
                    //.concat("IDR "+itemSelectedBuyPriceVal);

                    if (giManagementAdapter.getSelected().size()>0){

                        fabCreateGiRecap.animate().translationY(0).setDuration(100).start();

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
                        fabCreateGiRecap.animate().translationY(800).setDuration(100).start();
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

        btnSearchData.setOnClickListener(view -> {
            /*View viewLayout = RecapGoodIssueDataActivity.this.getCurrentFocus();
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

            View viewLayout = RecapGoodIssueDataActivity.this.getCurrentFocus();
            if (viewLayout != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
            }

            if (spinnerCustUID.getText().toString().isEmpty()){
                spinnerCustUID.setError("");
            } else{
                spinnerCustUID.setError(null);
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

            if (!spinnerCustUID.getText().toString().isEmpty()&&
                    !spinnerRoUID.getText().toString().isEmpty()&&
                    !edtPoUID.getText().toString().isEmpty()){
                searchQuery();
            }
        });

        fabCreateGiRecap.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                dialogInterface.confirmCreateRecap(context, pouidVal, goodIssueModelArrayList);
            }
        });

        searchQueryAll();
    }

    private void clearRoPoData(){
        rouidVal = null;
        pouidVal = null;

        spinnerRoUID.setText(null);
        edtPoUID.setText(null);
    }

    public void createPDF(String dest){

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
            addGiRcpTtl(document);
            addGiRcpMainContent(document);
            document.close();

            dialogInterface.recapGeneratedInfo(context, dest);
            //dialogInterface.confirmCreateRecap(context, dest);
            //Helper.openFilePDF(context, new File(Helper.getAppPath(context)+rouidVal+".pdf"));
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void addGiRcpTtl(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        Chunk title = new Chunk("REKAP GI PO-"+rouidVal, fontBigBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraphTitle);
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.setSpacingAfter(15);
        document.add(preface);
        document.add(new LineSeparator());
    }

    public static PdfPCell createTextCellNoBorderNormal(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell createTextColumnHeader(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(BaseColor.YELLOW);
        return cell;
    }

    public static PdfPCell createTextNormalCell(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
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

            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph(roPoCustNumberStrVal, fontNormal),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph(roRecapDateCreatedStrVal, fontNormal),
                    Element.ALIGN_RIGHT));

            table1.addCell(createTextColumnHeader(
                    new Paragraph("No", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Tanggal", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("ID", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("NOPOL", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("P", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("L", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("T", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("K", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("TK", fontBold), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph(String.valueOf(Html.fromHtml("M\u00B3")), fontBold), Element.ALIGN_CENTER));

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

                table1.addCell(createTextNormalCell(
                        new Paragraph(rowNumberStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowDateStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowIDStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowVhlUIDStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowVhLengthStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowVhWidthStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowVhHeightStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowVhHeightCorrectionStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(rowVhHeightAfterCorrectionStrVal, fontNormal), Element.ALIGN_CENTER));
                table1.addCell(createTextNormalCell(
                        new Paragraph(vhlCubicationStrVal, fontNormal), Element.ALIGN_CENTER));

                totalCubication += giManagementAdapter.getSelected().get(i).getGiVhlCubication();



                // TODO FOR INVOICE WHEN FEW ITEMS HAS BEEN SELECTED
                /*DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("GoodIssueData").child(goodIssueModelArrayList.get(i).getGiUID());
                rootRef.child("giInvoiced").setValue(true);*/
                // SET VALUE TOTAL SELL





            }

            String totalCubicationStrVal = df.format(totalCubication);
            //String totalCubicationStrVal = String.valueOf(totalCubication);
            double totalIDR = matBuyPrice*Double.parseDouble(df.format(totalCubication));
            String totalIDRStrVal = currencyVal+" "+currencyFormat(df.format(totalIDR));

            table2.addCell(createTextColumnHeader(
                    new Paragraph("Total", fontBold), Element.ALIGN_CENTER));
            table2.addCell(createTextNormalCell(
                    new Paragraph(totalCubicationStrVal, fontNormal), Element.ALIGN_CENTER));
            table3.addCell(createTextColumnHeader(
                    new Paragraph("Total Biaya Beli", fontBold), Element.ALIGN_CENTER));
            table3.addCell(createTextNormalCell(
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
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (Objects.equals(item.child("giStatus").getValue(), true) &&
                                Objects.equals(item.child("giCashedOut").getValue(), true) &&
                                Objects.equals(item.child("giInvoiced").getValue(), false) &&
                                Objects.equals(item.child("giRecapped").getValue(), false)) {
                            GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                            goodIssueModelArrayList.add(goodIssueModel);
                            //fabCreateGiRecap.show();
                            nestedScrollView.setVisibility(View.VISIBLE);
                            llNoData.setVisibility(View.GONE);
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
                Collections.reverse(goodIssueModelArrayList);
                giManagementAdapter = new GIManagementAdapter(context, goodIssueModelArrayList);
                rvGoodIssueList.setAdapter(giManagementAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

        Toast.makeText(context, rouidVal, Toast.LENGTH_SHORT).show();

        db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", pouidVal).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                        receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                        //String documentID = receivedOrderModel.getRoDocumentID();

                        matTypeVal = receivedOrderModel.getRoMatType();
                        roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        custNameVal = receivedOrderModel.getRoCustName();
                        currencyVal = receivedOrderModel.getRoCurrency();

                        HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            for (ProductItems productItems : e.getValue()) {
                                matNameVal = productItems.getMatName();
                                matBuyPrice = productItems.getMatBuyPrice();
                            }
                        }
                    }
                });

        //Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStartVal).endAt(dateEndVal);
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

                            if (Objects.requireNonNull(item.child("giPoCustNumber").getValue()).toString().contains(pouidVal) &&
                                    !pouidVal.equals("-")) {
                                if (Objects.equals(item.child("giStatus").getValue(), true) &&
                                        Objects.equals(item.child("giCashedOut").getValue(), true) &&
                                        Objects.equals(item.child("giInvoiced").getValue(), false) &&
                                        Objects.equals(item.child("giRecapped").getValue(), false)) {

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

        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // HANDLE RESPONSIVE CONTENT
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (width<=1080){
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
        }
    }

    @Override
    public void onBackPressed() {

        firstViewDataFirstTimeStatus = true;
        super.onBackPressed();
    }
}