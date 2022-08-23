package com.ptbas.controlcenter.create;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.DragLinearLayout;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddInvoiceActivity extends AppCompatActivity {

    double matSellPrice;
    String dateStartVal = "", dateEndVal = "", rouidVal= "", currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal;
    public String custNameVal = "";

    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID;
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

    List<String> arrayListRoUID, arrayListPoUID;

    LinearLayout llWrapFilterByDateRange, llWrapFilterByRouid, llNoData;

    ImageButton btnGiSearchByDateReset, btnGiSearchByRoUIDReset;

    ExtendedFloatingActionButton fabCreateGiRecap;

    DialogInterface dialogInterface = new DialogInterface();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String invPoDate = "";
    String invCustName = "";
    String invPoUID = "";
    double invTotal = 0;
    double invTax1 = 0;
    double invTax2 =0;

    public static BaseFont baseNormal, baseMedium, baseBold;
    public static Font fontNormal;
    public static Font fontMedium;
    public static Font fontMediumWhite;
    public static Font fontBold;
    public static Font fontTransparent;

    public static BaseColor baseColorBluePale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invoice);

        LangUtils.setLocale(this, "en");

        baseColorBluePale = new BaseColor(22,169,242);

        try {
            baseNormal = BaseFont.createFont("/res/font/kanitregular.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            baseMedium = BaseFont.createFont("/res/font/kanitsemibold.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            baseBold = BaseFont.createFont("/res/font/kanitextrabold.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        //Typeface fontBaseNormal = ResourcesCompat.getFont(context, R.font.kanitregular);
        fontNormal = new Font(baseNormal, 10, Font.NORMAL, BaseColor.BLACK);
        fontMedium = new Font(baseMedium, 10, Font.NORMAL, BaseColor.BLACK);
        fontMediumWhite = new Font(baseMedium, 10, Font.NORMAL, BaseColor.WHITE);
        fontBold = new Font(baseBold, 20, Font.NORMAL, BaseColor.BLACK);
        fontTransparent = new Font(baseNormal, 20, Font.NORMAL, null);


        context = this;

        cdvFilter = findViewById(R.id.cdv_filter);
        btnSearchData = findViewById(R.id.caridata);
        spinnerRoUID = findViewById(R.id.rouid);
        edtPoUID = findViewById(R.id.pouid);
        edtDateStart = findViewById(R.id.edt_gi_date_filter_start);
        edtDateEnd = findViewById(R.id.edt_gi_date_filter_end);
        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        imgbtnExpandCollapseFilterLayout = findViewById(R.id.imgbtn_expand_collapse_filter_layout);
        llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        llWrapFilterByRouid = findViewById(R.id.ll_wrap_filter_by_rouid);

        llNoData = findViewById(R.id.ll_no_data);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByRoUIDReset = findViewById(R.id.btn_gi_search_rouid_reset);
        fabCreateGiRecap = findViewById(R.id.fab_create_gi_recap);

        ActionBar actionBar = getSupportActionBar();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Buat Invoice");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helper.handleUIModeForStandardActivity(this, actionBar);

        // DRAGLINEARLAYOUT FOR FILTERING
        DragLinearLayout dragLinearLayout = findViewById(R.id.drag_linear_layout);
        for(int i = 0; i < dragLinearLayout.getChildCount(); i++){
            View child = dragLinearLayout.getChildAt(i);
            // the child will act as its own drag handle
            dragLinearLayout.setViewDraggable(child, child);
        }

        dragLinearLayout.setOnViewSwapListener((firstView, firstPosition,
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
        });

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
        arrayListPoUID = new ArrayList<>();

        db.collection("ReceivedOrderData").whereEqualTo("roStatus", true)
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
                                dialogInterface.roNotExistsDialog(AddInvoiceActivity.this);
                            }
                        }
                    }
                });

        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

            db.collection("ReceivedOrderData").whereEqualTo("roUID", selectedSpinnerPoPtBasNumber).get()
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

        fabCreateGiRecap.hide();

        btnSearchData.setOnClickListener(view -> {
            View viewLayout = AddInvoiceActivity.this.getCurrentFocus();
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
            }
        });

        fabCreateGiRecap.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                DecimalFormat df = new DecimalFormat("0.00");

                float totalCubication = 0;
                for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                    //String rowNumberStrVal = String.valueOf(i+1);
                    //String vhlCubicationStrVal = df.format(goodIssueModelArrayList.get(i).getGiVhlCubication());
                    totalCubication += goodIssueModelArrayList.get(i).getGiVhlCubication();
                }
                //String totalCubicationStrVal = df.format(totalCubication);
                //String totalCubicationStrVal = String.valueOf(totalCubication);
                double totalIDR = matSellPrice *Double.parseDouble(df.format(totalCubication));
                //String totalIDRStrVal = currencyVal+" "+currencyFormat(df.format(totalIDR));
                //invTotal = totalIDR;

                dialogInterface.confirmCreateInvoice(context, db, goodIssueModelArrayList,
                        rouidVal, invPoDate, invPoUID, invCustName, totalIDR, invTax1, invTax2);
            }
        });
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
            addUpperSpace(document);
            addTitlePage(document);
            addContent(document);
            document.close();
            Helper.openFilePDF(context, new File(Helper.getAppPath(context)+rouidVal+".pdf"));
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void addTitlePage(Document document) throws DocumentException {
        Paragraph preface1 = new Paragraph();
        Chunk title = new Chunk("INVOICE", fontBold);
        //Chunk title = new Chunk("INV - RO-"+rouidVal, fontBigBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface1.setAlignment(Element.ALIGN_CENTER);
        preface1.setSpacingAfter(20);
        document.add(preface1);
    }
    private void addUpperSpace(Document document) throws DocumentException {
        Paragraph preface0 = new Paragraph();
        Chunk title = new Chunk(" ", fontTransparent);
        //Chunk title = new Chunk("INV - RO-"+rouidVal, fontBigBold);
        Paragraph paragraphTitle = new Paragraph(title);
        paragraphTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(paragraphTitle);
        preface0.setAlignment(Element.ALIGN_CENTER);
        preface0.setSpacingAfter(70);
        document.add(preface0);
    }

    public static PdfPCell createTextCellNoBorderNormal(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
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
        cell.setBackgroundColor(baseColorBluePale);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setPadding(5);
        return cell;
    }
    public static PdfPCell createTextColumnHeaderNoBorder(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(baseColorBluePale);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(5);
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

    private void addContent(Document document) throws DocumentException{
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

            Paragraph paragraphBlank = new Paragraph(" ");

            Paragraph preface2 = new Paragraph();
            preface2.setSpacingAfter(2);

            PdfPTable table0 = new PdfPTable(5);
            table0.setWidthPercentage(100);
            table0.setWidths(new float[]{7,1,8,1,5});

            PdfPTable table01 = new PdfPTable(5);
            table01.setWidthPercentage(100);
            table01.setWidths(new float[]{7,1,8,1,5});

            /*PdfPTable table02 = new PdfPTable(5);
            table02.setWidthPercentage(100);
            table02.setWidths(new float[]{5,1,5,1,4});

            PdfPTable table03 = new PdfPTable(5);
            table03.setWidthPercentage(100);
            table03.setWidths(new float[]{5,1,5,1,4});*/

            PdfPTable table1 = new PdfPTable(6);
            table1.setWidthPercentage(100);
            table1.setWidths(new float[]{1,4,2,2,2,3});

            PdfPTable table2 = new PdfPTable(6);
            table2.setWidthPercentage(100);
            table2.setWidths(new float[]{1,4,2,2,2,3});

            PdfPTable table3 = new PdfPTable(2);
            table3.setWidthPercentage(100);
            table3.setWidths(new float[]{5, 4});

            table0.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("Billed To", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("Invoice Details", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("Payment Record", fontMediumWhite),
                    Element.ALIGN_LEFT));

            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph(custNameVal+"\n"+"Address: UNAVAILABLE"+"\n", fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("NO. INV: INV - RO - "+rouidVal+"\n"+"No. PO: "+pouidVal+"\n"+"Invoice Date: " + invPoDate+"\n"+"Due Date: UNAVAILABLE", fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Paid Amount: 0"+"\n"+"Due Amount IDR:"+"\n"+"2,000,000", fontNormal),
                    Element.ALIGN_LEFT));

            /*table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("PO: "+pouidVal, fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Invoice Date:" + invPoDate, fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Due Amount IDR:", fontNormal),
                    Element.ALIGN_LEFT));

            table03.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Address: UNAVAILABLE", fontNormal),
                    Element.ALIGN_LEFT));
            table03.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table03.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Due Date: UNAVAILABLE", fontNormal),
                    Element.ALIGN_LEFT));
            table03.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table03.addCell(createTextCellNoBorderNormal(
                    new Paragraph("UNAVAILABLE", fontNormal),
                    Element.ALIGN_LEFT));*/



            table1.addCell(createTextColumnHeader(
                    new Paragraph("No", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Item# / Deskripsi", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Harga", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Qty", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Pajak", fontMedium), Element.ALIGN_CENTER));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Jumlah", fontMedium), Element.ALIGN_CENTER));

            /*float totalCubication = 0;
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

                totalCubication += goodIssueModelArrayList.get(i).getGiVhlCubication();






                // TODO FOR INVOICE WHEN FEW ITEMS HAS BEEN SELECTED
                *//*DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("GoodIssueData").child(goodIssueModelArrayList.get(i).getGiUID());
                rootRef.child("giInvoiced").setValue(true);*//*
                // SET VALUE TOTAL SELL





            }*/

            /*String totalCubicationStrVal = df.format(totalCubication);
            //String totalCubicationStrVal = String.valueOf(totalCubication);
            double totalIDR = matSellPrice *Double.parseDouble(df.format(totalCubication));
            String totalIDRStrVal = currencyVal+" "+currencyFormat(df.format(totalIDR));
            invTotal = totalIDR;*/

            /*table2.addCell(createTextColumnHeader(
                    new Paragraph("Total", fontMedium), Element.ALIGN_CENTER));
            table2.addCell(createTextNormalCell(
                    new Paragraph(totalCubicationStrVal, fontNormal), Element.ALIGN_CENTER));
            table3.addCell(createTextColumnHeader(
                    new Paragraph("Total Biaya Beli", fontMedium), Element.ALIGN_CENTER));
            table3.addCell(createTextNormalCell(
                    new Paragraph(totalIDRStrVal, fontNormal), Element.ALIGN_CENTER));*/

            document.add(table0);
            document.add(table01);
            document.add(paragraphBlank);
            //document.add(table02);
            //document.add(table03);
            /*document.add(paragraphROMatNameType);
            document.add(paragraphROCustName);*/
            //document.add(new LineSeparator());
            //document.add(preface2);
            document.add(table1);
            //document.add(table2);
            //document.add(table3);
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
        rouidVal = spinnerRoUID.getText().toString();
        pouidVal = Objects.requireNonNull(edtPoUID.getText()).toString();

        db.collection("ReceivedOrderData").whereEqualTo("roUID", rouidVal).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                        receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                        //String documentID = receivedOrderModel.getRoDocumentID();

                        matTypeVal = receivedOrderModel.getRoMatType();
                        roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        custNameVal = receivedOrderModel.getRoCustName();
                        currencyVal = receivedOrderModel.getRoCurrency();

                        invCustName = receivedOrderModel.getRoCustName();
                        invPoUID = receivedOrderModel.getRoPoCustNumber();
                        invPoDate = receivedOrderModel.getRoDateCreated();

                        HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            for (ProductItems productItems : e.getValue()) {
                                matNameVal = productItems.getMatName();
                                matSellPrice = productItems.getMatSellPrice();
                            }
                        }


                    }
                });

        DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
        //databaseReferenceGI.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giInvoiced").setValue(true);


        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStartVal).endAt(dateEndVal);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (!rouidVal.isEmpty()){
                            if (Objects.requireNonNull(item.child("giRoUID").getValue()).toString().equals(rouidVal) &&
                                    !pouidVal.equals("-")) {
                                if (Objects.equals(item.child("giStatus").getValue(), true)) {
                                    if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                                        GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                        goodIssueModelArrayList.add(goodIssueModel);
                                        fabCreateGiRecap.show();
                                        nestedScrollView.setVisibility(View.VISIBLE);
                                        llNoData.setVisibility(View.GONE);
                                        //databaseReferenceGI.child("GoodIssueData").child(goodIssueModel.getGiUID()).child("giInvoiced").setValue(true);
                                    }
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
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByRouid.setVisibility(View.GONE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_rouid){
                llWrapFilterByDateRange.setVisibility(View.GONE);
            }
        } else {
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_date_range){
                llWrapFilterByRouid.setVisibility(View.VISIBLE);
            }
            if (firstViewData.getId()==R.id.ll_wrap_filter_by_rouid){
                llWrapFilterByDateRange.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
}