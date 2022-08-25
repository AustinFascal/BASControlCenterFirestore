package com.ptbas.controlcenter.create;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import com.itextpdf.text.Anchor;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ptbas.controlcenter.DialogInterface;
import com.ptbas.controlcenter.DragLinearLayout;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.ImageAndPositionRenderer;
import com.ptbas.controlcenter.NumberToWords;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.model.CustomerModel;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AddInvoiceActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";

    double matSellPrice;
    String dateStartVal = "", dateEndVal = "", rouidVal= "", currencyVal = "", pouidVal = "",
            monthStrVal, dayStrVal, roPoCustNumber, matTypeVal, matNameVal;
    public String custNameVal = "", custAddressVal = "", invUID="";

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
    public static Font fontNormalSmall;
    public static Font fontNormalSmallItalic;
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
        fontNormalSmall = new Font(baseNormal, 8, Font.NORMAL, BaseColor.BLACK);
        fontNormalSmallItalic = new Font(baseNormal, 8, Font.ITALIC, BaseColor.BLACK);
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
                    totalCubication += goodIssueModelArrayList.get(i).getGiVhlCubication();
                }
                double totalIDR = matSellPrice *Double.parseDouble(df.format(totalCubication));

                String one = rouidVal.replaceAll("\\s","");
                invUID = getRandomString(5)+"-INV-"+one;

                String invDateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
                String invCreatedBy = helper.getUserId();

                dialogInterface.confirmCreateInvoice(context, db, goodIssueModelArrayList,
                        invUID, invCreatedBy, invDateCreated, invPoDate, invPoUID, invCustName, totalIDR, invTax1, invTax2);

                String two = custNameVal.replace(" - ","-");
                int indexcustnameval = two.lastIndexOf('-');
                db.collection("CustomerData").whereEqualTo("custUID", two.substring(0, indexcustnameval)).get()
                        .addOnSuccessListener(queryDocumentSnapshots2 -> {
                            for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                //customerModel.setCustAddress(documentSnapshot2.getId());
                                custAddressVal = customerModel.getCustAddress();
                            }
                        });
            }
        });
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public void createInvPDF(String dest){
        if (new File(dest).exists()){
            new File(dest).deleteOnExit();
        }

        try {
            /*String one = rouidVal.replaceAll("\\s","");
            String two = one.replaceAll("-", "/");
            int index=two.lastIndexOf('/');
            String invDateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
            String invUID = "INV/"+invDateCreated+two.substring(index)+"/"+two.substring(0,index);*/

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
            //addLogo(document);
            document.close();
            Helper.openFilePDF(context, new File(dest));
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
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
    public static PdfPCell createTextCellNoBorderNormalPaddingLeft(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setPaddingLeft(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell cellSpanRowImage(Image image) throws DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.addElement(image);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(1);
        cell.setRowspan(3);
        return cell;
    }
    public static PdfPCell cellSpanRowTextList() throws DocumentException {

        com.itextpdf.text.List ordered = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
        ordered.add(new ListItem("Invoice ini sah dan diproses oleh komputer.", fontNormalSmall));
        ordered.add(new ListItem("Bukti PPH23 dikirim ke email bintang.andalan.semesta@gmail.com (apabila tersedia).", fontNormalSmall));
        ordered.add(new ListItem("Apabila Anda membutuhkan bantuan, silakan hubungi kami melalui WA: 081335376111 / 085105164000", fontNormalSmall));
        /*document.add(ordered);

        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setLeading(0, 1);*/
        PdfPCell cell = new PdfPCell();
        cell.addElement(ordered);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(1);
        cell.setRowspan(3);
        return cell;
    }

    public static PdfPCell createTextCellNoBorderNormalMainContent(Paragraph paragraph, int alignment) throws DocumentException {
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

    public static PdfPCell createTextCellBorderTopNormalMainContent(Paragraph paragraph, int alignment) throws DocumentException {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
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
    public PdfPCell createTextColumnHeaderNoBorder(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //cell.setBackgroundColor(baseColorBluePale);
        cell.setBorder(PdfPCell.NO_BORDER);


        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_table_column_blue_pale);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 90, stream);
        Image img = null;
        byte[] byteArray = stream.toByteArray();

        try {
            img = Image.getInstance(byteArray);
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        }
        /*Image image = null;
        try {
            image = Image.getInstance("resources/images/dog.bmp");
        } catch (BadElementException | IOException e) {
            e.printStackTrace();
        }*/
        cell.setCellEvent(new ImageAndPositionRenderer(img));
        cell.setPaddingTop(1);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(7);
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

        /*String one = rouidVal.replaceAll("\\s","");
        String two = one.replaceAll("-", "/");
        int index=two.lastIndexOf('/');
        String invUID = "INV/"+currentDate+two.substring(index)+"/"+two.substring(0,index);*/


        try {
            String roMatNameTypeStrVal = "Material: "+ matNameVal +" | "+ matTypeVal;
            /*String roCustNameStrVal = "Customer: "+custNameVal;
            String roPoCustNumberStrVal = "Nomor PO: "+roPoCustNumber;
            String roRecapDateCreatedStrVal = "Tanggal rekap dibuat: "+currentDate;*/

            /*Chunk roMatNameType = new Chunk(roMatNameTypeStrVal, fontNormal);
            Chunk roCustName = new Chunk(roCustNameStrVal, fontNormal);*/




            Paragraph paragraphROMatNameType = new Paragraph("");
            paragraphROMatNameType.setAlignment(Element.ALIGN_LEFT);
            paragraphROMatNameType.setSpacingAfter(0);

            Paragraph paragraphROCustName = new Paragraph("");
            paragraphROCustName.setAlignment(Element.ALIGN_LEFT);
            paragraphROCustName.setSpacingAfter(5);

            String invDateCreated = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
            String invTimeCreated = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            Paragraph paragraphInvDateCreated = new Paragraph("Terakhir diperbarui: "+invDateCreated+" "+invTimeCreated+" WIB", fontNormalSmallItalic);
            paragraphInvDateCreated.setAlignment(Element.ALIGN_RIGHT);
            paragraphInvDateCreated.setSpacingAfter(5);




            Paragraph paragraphBlank = new Paragraph(" ");

            Paragraph preface2 = new Paragraph();
            preface2.setSpacingAfter(2);

            PdfPTable table0 = new PdfPTable(7);
            table0.setWidthPercentage(100);
            table0.setWidths(new float[]{5,1,7,1,6,1,9});

            PdfPTable table01 = new PdfPTable(7);
            table01.setWidthPercentage(100);
            table01.setWidths(new float[]{3,1,9,1,4,1,11});

            PdfPTable table02 = new PdfPTable(7);
            table02.setWidthPercentage(100);
            table02.setWidths(new float[]{3,1,9,1,4,1,11});

            PdfPTable table03 = new PdfPTable(7);
            table03.setWidthPercentage(100);
            table03.setWidths(new float[]{3,1,9,1,4,1,11});



            PdfPTable table1 = new PdfPTable(5);
            table1.setWidthPercentage(100);
            table1.setWidths(new float[]{3,2,2,3,4});

            PdfPTable table2 = new PdfPTable(5);
            table2.setWidthPercentage(100);
            table2.setWidths(new float[]{3,2,2,3,4});

            PdfPTable table3 = new PdfPTable(5);
            table3.setWidthPercentage(100);
            table3.setWidths(new float[]{3,2,2,3,4});

            PdfPTable table4 = new PdfPTable(5);
            table4.setWidthPercentage(100);
            table4.setWidths(new float[]{3,2,2,3,4});

            PdfPTable table5 = new PdfPTable(2);
            table5.setWidthPercentage(100);
            table5.setWidths(new float[]{3,10});

            PdfPTable table6 = new PdfPTable(2);
            table6.setWidthPercentage(100);
            table6.setWidths(new float[]{9,1});




            table0.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("DITAGIH KE", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("DETAIL TAGIHAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table0.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));


            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Nama", fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph(custNameVal, fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Kode Unik Tagihan", fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            table01.addCell(createTextCellNoBorderNormal(
                    new Paragraph(invUID, fontNormal),
                    Element.ALIGN_LEFT));



            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("Alamat", fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":", fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph(custAddressVal, fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph("No. PO"+"\n"+"Tanggal PO", fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":"+"\n"+":", fontNormal),
                    Element.ALIGN_LEFT));
            table02.addCell(createTextCellNoBorderNormal(
                    new Paragraph(pouidVal+"\n"+invPoDate, fontNormal),
                    Element.ALIGN_LEFT));


            table1.addCell(createTextColumnHeader(
                    new Paragraph("Item# / Deskripsi", fontMediumWhite), Element.ALIGN_LEFT));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Harga Satuan", fontMediumWhite), Element.ALIGN_RIGHT));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Jumlah (Unit)", fontMediumWhite), Element.ALIGN_RIGHT));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Pajak", fontMediumWhite), Element.ALIGN_RIGHT));
            table1.addCell(createTextColumnHeader(
                    new Paragraph("Jumlah", fontMediumWhite), Element.ALIGN_RIGHT));


            float totalCubication = 0;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                totalCubication += goodIssueModelArrayList.get(i).getGiVhlCubication();

               /* // TODO FOR INVOICE WHEN FEW ITEMS HAS BEEN SELECTED
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("GoodIssueData").child(goodIssueModelArrayList.get(i).getGiUID());
                rootRef.child("giInvoiced").setValue(true);
                // SET VALUE TOTAL SELL*/
            }

            double totalAmount = matSellPrice*totalCubication;
            double taxPPN = (0.11)*totalAmount;
            double totalDue = totalAmount+taxPPN;

            DecimalFormat dfRound = new DecimalFormat("0");

            table2.addCell(createTextCellNoBorderNormalMainContent(
                    new Paragraph(matNameVal, fontNormal), Element.ALIGN_LEFT));
            table2.addCell(createTextCellNoBorderNormalMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(matSellPrice)), fontNormal), Element.ALIGN_RIGHT));
            table2.addCell(createTextCellNoBorderNormalMainContent(
                    new Paragraph(String.valueOf(totalCubication), fontNormal), Element.ALIGN_RIGHT));
            table2.addCell(createTextCellNoBorderNormalMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(taxPPN)), fontNormal), Element.ALIGN_RIGHT));
            table2.addCell(createTextCellNoBorderNormalMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmount)), fontNormal), Element.ALIGN_RIGHT));


            table3.addCell(createTextCellBorderTopNormalMainContent(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            table3.addCell(createTextCellBorderTopNormalMainContent(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            table3.addCell(createTextCellBorderTopNormalMainContent(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            table3.addCell(createTextCellBorderTopNormalMainContent(
                    new Paragraph("Total Belanja :"+"\n"+"Diskon :"+"\n"+"PPN 11% :"+"\n"+"PPH23 :", fontNormal), Element.ALIGN_RIGHT));
            table3.addCell(createTextCellBorderTopNormalMainContent(
                    new Paragraph(currencyVal+" "+currencyFormat(df.format(totalAmount))+"\n"+currencyVal+" "+"0"+"\n"+currencyVal+" "+currencyFormat(df.format(taxPPN))+"\n"+currencyVal+" "+"0", fontNormal), Element.ALIGN_RIGHT));

            table4.addCell(createTextColumnHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            table4.addCell(createTextColumnHeader(
                    new Paragraph("", fontNormal), Element.ALIGN_LEFT));
            table4.addCell(createTextColumnHeader(
                    new Paragraph("", fontMedium), Element.ALIGN_LEFT));
            table4.addCell(createTextColumnHeader(
                    new Paragraph("TOTAL TAGIHAN", fontMediumWhite), Element.ALIGN_RIGHT));
            table4.addCell(createTextColumnHeader(
                    new Paragraph(currencyVal+" "+currencyFormat(dfRound.format(totalDue)), fontMediumWhite), Element.ALIGN_RIGHT));

            table5.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("TERBILANG", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table5.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));


            table6.addCell(createTextCellNoBorderNormal(
                    new Paragraph(NumberToWords.convert(Long.parseLong(dfRound.format(totalDue)))+" Rupiah", fontMedium),
                    Element.ALIGN_LEFT));
            table6.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontMediumWhite),
                    Element.ALIGN_LEFT));



            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bca_qr_bas);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();

            try {
                img = Image.getInstance(byteArray);
            } catch (BadElementException | IOException e) {
                e.printStackTrace();
            }
            img.setAlignment(Image.TEXTWRAP);
            img.scaleAbsolute(50f, 50f);

            PdfPTable table7 = new PdfPTable(6);
            table7.setWidthPercentage(100);
            table7.setWidths(new float[]{2,3,1,4,1,9});

            table7.addCell(cellSpanRowImage(img));
            table7.addCell(createTextCellNoBorderNormalPaddingLeft(
                    new Paragraph("Transfer ke", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph("BCA KCU VETERAN", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(cellSpanRowTextList());

            table7.addCell(createTextCellNoBorderNormalPaddingLeft(
                    new Paragraph("Atas Nama", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph("PT BINTANG ANDALAN SEMESTA", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));


            table7.addCell(createTextCellNoBorderNormalPaddingLeft(
                    new Paragraph("No. Rekening", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph(":", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph("010-556-5777", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table7.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            PdfPTable table8 = new PdfPTable(6);
            table8.setWidthPercentage(100);
            table8.setWidths(new float[]{5,2,4,3,1,5});
            table8.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("DETAIL PEMBAYARAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table8.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            table8.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_RIGHT));
            table8.addCell(createTextColumnHeaderNoBorder(
                    new Paragraph("CATATAN", fontMediumWhite),
                    Element.ALIGN_LEFT));
            table8.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));
            table8.addCell(createTextCellNoBorderNormal(
                    new Paragraph("", fontNormalSmall),
                    Element.ALIGN_LEFT));

            document.add(table0);
            document.add(table01);
            document.add(table02);
            document.add(paragraphBlank);
            document.add(table1);
            document.add(table2);
            document.add(table3);
            document.add(table4);
            document.add(paragraphBlank);
            document.add(table5);
            document.add(table6);
            document.add(paragraphBlank);
            document.add(table8);
            document.add(table7);
            document.add(paragraphBlank);
            document.add(paragraphInvDateCreated);
        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void addLogo(Document document) throws DocumentException {
        try { // Get user Settings GeneralSettings getUserSettings =

           /* Rectangle rectDoc = document.getPageSize();
            float width = rectDoc.getWidth();
            float height = rectDoc.getHeight();
            float imageStartX = width - document.rightMargin() - 500f;
            float imageStartY = height - document.topMargin() - 500f;*/

            //System.gc();

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bca_qr_bas);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();

            try {
                img = Image.getInstance(byteArray);
            } catch (BadElementException | IOException e) {
                e.printStackTrace();
            }

            //InputStream ims = getAssets().open("bca_qr_bas.png");
            //Bitmap bmp = BitmapFactory.decodeStream(ims);
            //ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);

            //byte[] byteArray = stream.toByteArray();
            //PdfImage img = new PdfImage(arg0, arg1, arg2)

            // Converting byte array into image Image img =
            //Image img = Image.getInstance(byteArray); // img.scalePercent(50);
            img.setAlignment(Image.TEXTWRAP);
            img.scaleAbsolute(100f, 100f);
            //img.setAbsolutePosition(imageStartX, imageStartY); // Adding Image
            document.add(img);

        } catch (Exception e) {
            e.printStackTrace();
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

                       // String one = receivedOrderModel.getRoCustName();
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