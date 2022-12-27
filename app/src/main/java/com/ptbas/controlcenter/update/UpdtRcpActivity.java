package com.ptbas.controlcenter.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.MultiFormatWriter;
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
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GiDataAdapter;
import com.ptbas.controlcenter.databinding.ActivityUpdtRcpBinding;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.GiModel;
import com.ptbas.controlcenter.model.ProductItems;
import com.ptbas.controlcenter.model.RcpModel;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.utility.LangUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UpdtRcpActivity extends AppCompatActivity {

    Context context;
    HelperUtils helperUtils = new HelperUtils();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();
    ArrayList<GiModel> giModelArrayList = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    CollectionReference refInv = db.collection("InvoiceData");
    CollectionReference refRecap = db.collection("RecapData");
    CollectionReference refRO = db.collection("ReceivedOrderData");
    CollectionReference refCust = db.collection("CustomerData");
    CollectionReference refBankAccount = db.collection("BankAccountData");

    double matBuyPrice, matSellPrice, matCubication, transportServiceSellPrice;

    // ID
    String roPoCustNumber, currencyVal, custDocumentID, refUIDVal, roUIDVal, poUIDVal, rcpDocumentID, invPoUID, invDueDateNTimVal, connectingRODocumentUID;

    // RO
    String roDocumentID, matTypeVal, matNameVal, transportServiceNameVal;

    int poType;

    // INV
    String  custNameVal, coCreatedBy, transferProofReference, invTransferReference;

    // DATE
    String coCustomerNameVal;

    GiDataAdapter giDataAdapter;

    List<ProductItems> productItemsList;

    public static BaseFont baseNormal, baseMedium, baseBold;

    public static BaseColor baseColorBluePale, baseColorLightGrey;

    DecimalFormat df = new DecimalFormat("0.00");

    private static final Font fontNormal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font fontBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font fontBigBold = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

    private Menu menu;

    double totalUnitFinal;
    double totalAmountForMaterials, totalAmountForTransportService, taxPPN, taxPPNService, taxPPH, totalDue, totalDueForTransportService;

    private MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    Boolean invRecalculateStatus;

    String rcpDateDeliveryPeriod, publicRoDocumentID, rcpCreatedBy, rcpGiCreatedByUserID;

    //DecimalFormat dfRound = new DecimalFormat("0");
    DecimalFormat dfRound1 = new DecimalFormat("0");

    ProgressDialog pd;

    ActivityUpdtRcpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update_recap);



        binding = ActivityUpdtRcpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        dfRound1.setRoundingMode(RoundingMode.HALF_UP);

        LangUtils.setLocale(this, "en");

        context = this;
        //cdvFilter = findViewById(R.id.cdv_filter);

        helperUtils.ACTIVITY_NAME = "DETAIL";

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses");
        pd.setCancelable(false);

        loadRecapData();


        baseColorBluePale = new BaseColor(22,169,242);
        baseColorLightGrey = new BaseColor(237, 237, 237);

        try {
            baseNormal = BaseFont.createFont("/res/font/kanitregular.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            baseMedium = BaseFont.createFont("/res/font/kanitsemibold.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
            baseBold = BaseFont.createFont("/res/font/kanitextrabold.ttf", BaseFont.IDENTITY_H,BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }



        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Rincian Rekap GI");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");



        final String userId = helperUtils.getUserId();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coCreatedBy = snapshot.child(userId).child("fullName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdtRcpActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });




        // SHOW INIT DATA GI ON CREATE


        // CREATE GI MANAGEMENT ADAPTER
        giDataAdapter = new GiDataAdapter(this, giModelArrayList);



        //btnDatePaidReset.setColorFilter(color);

    }


    private void loadRecapData() {

        pd.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            rcpDocumentID = extras.getString("key");

            refRecap.whereEqualTo("rcpGiDocumentID", rcpDocumentID).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            refUIDVal = documentSnapshot.get("rcpGiDocumentID", String.class);
                            RcpModel rcpModel = documentSnapshot.toObject(RcpModel.class);

                            rcpDateDeliveryPeriod = rcpModel.getRcpDateDeliveryPeriod();
                            rcpGiCreatedByUserID = rcpModel.getRcpGiCreatedBy();
                            String rcpGiDateAndTimeCreated = rcpModel.getRcpGiDateAndTimeCreated();
                            String roDocumentID = rcpModel.getRoDocumentID();
                            double roCubication = rcpModel.getRcpGiCubication();

                            binding.tvUID.setText(rcpDocumentID);
                            binding.tvDateAndTimeCreated.setText(rcpGiDateAndTimeCreated);
                            binding.tvDateDeliveryPeriod.setText(rcpDateDeliveryPeriod);

                            publicRoDocumentID = roDocumentID;

                        }


                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
                        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                rcpCreatedBy = snapshot.child(rcpGiCreatedByUserID).child("fullName").getValue(String.class);
                                binding.tvCreatedBy.setText(rcpCreatedBy);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(UpdtRcpActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        refRO.whereEqualTo("roDocumentID", publicRoDocumentID).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        transportServiceSellPrice = 0;
                                        matBuyPrice = 0;

                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                            roUIDVal = documentSnapshot.get("roUID", String.class);
                                            poUIDVal = documentSnapshot.get("roPoCustNumber", String.class);
                                            custDocumentID = documentSnapshot.get("custDocumentID", String.class);
                                            currencyVal = documentSnapshot.get("roCurrency", String.class);
                                            poType = documentSnapshot.get("roType", Integer.class);

                                            //roPoCustNumber = poUIDVal;

                                            binding.tvRoUID.setText(roUIDVal);
                                            binding.tvPoUID.setText(poUIDVal);

                                            RoModel roModel = documentSnapshot.toObject(RoModel.class);
                                            roModel.setRoDocumentID(documentSnapshot.getId());

                                            if (poType == 0){
                                                matTypeVal = "MATERIAL + JASA ANGKUT";
                                            }
                                            if (poType == 1){
                                                matTypeVal = "MATERIAL SAJA";
                                            }
                                            if (poType == 2){
                                                matTypeVal = "JASA ANGKUT SAJA";
                                            }



                                            binding.tvPoTransportType.setText(matTypeVal);

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
                                                        matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                                    }
                                                }

                                            }

                                            binding.tvMatName.setText(matNameVal);

                                            refCust.whereEqualTo("custDocumentID", custDocumentID).get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                                                                CustModel custModel = documentSnapshot.toObject(CustModel.class);
                                                                custModel.setCustDocumentID(documentSnapshot.getId());

                                                                coCustomerNameVal = custModel.getCustName();
                                                                binding.tvCustomerName.setText(coCustomerNameVal);

                                                            }
                                                        }
                                                    });


                                        }
                                    }
                                });
                        searchQueryAll();
                    });
        } else {
            Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
        }
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

            dialogInterfaceUtils.recapGeneratedInfo(context, dest);
            //dialogInterface.confirmCreateRecap(context, dest);
            //Helper.openFilePDF(context, new File(Helper.getAppPath(context)+rouidVal+".pdf"));
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

/*
    private void searchQuery(){

        TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());

       */
/* rouidVal = spinnerRoUID.getText().toString();
        pouidVal = Objects.requireNonNull(edtPoUID.getText()).toString();*//*


        //Toast.makeText(context, rouidVal, Toast.LENGTH_SHORT).show();

        db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", roPoCustNumber).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ReceivedOrderModel receivedOrderModel = documentSnapshot.toObject(ReceivedOrderModel.class);
                        receivedOrderModel.setRoDocumentID(documentSnapshot.getId());

                        //String documentID = receivedOrderModel.getRoDocumentID();

                        matTypeVal = receivedOrderModel.getRoMatType();
                        roPoCustNumber = receivedOrderModel.getRoPoCustNumber();
                        String custDocumentID = receivedOrderModel.getCustDocumentID();
                        currencyVal = receivedOrderModel.getRoCurrency();
                        //roDocumentID = receivedOrderModel.getRoDocumentID();

                        db.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID).get()
                                .addOnSuccessListener(queryDocumentSnapshots2 -> {
                                    for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2){
                                        CustomerModel customerModel = documentSnapshot2.toObject(CustomerModel.class);
                                        custNameVal = customerModel.getCustName();
                                    }
                                });

                        */
/*HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            for (ProductItems productItems : e.getValue()) {
                                matNameVal = productItems.getMatName();
                                matBuyPrice = productItems.getMatBuyPrice();
                            }
                        }*//*


                        HashMap<String, List<ProductItems>> map = receivedOrderModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            productItemsList = e.getValue();
                            for (int i = 0; i<productItemsList.size();i++){
                                if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    */
/*transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatBuyPrice();*//*

                                } else {
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                }
                            }

                        }
                    }
                });

        });
    }
*/


    private void addGiRcpTtl(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        Chunk title = new Chunk("REKAP GI PO-"+poUIDVal, fontBigBold);
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
            String roCustNameStrVal = "Customer: "+coCustomerNameVal;
            String roPoCustNumberStrVal = "Nomor PO: "+poUIDVal;
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

                totalCubication += giModelArrayList.get(i).getGiVhlCubication();

                // TODO FOR INVOICE WHEN FEW ITEMS HAS BEEN SELECTED
                /*DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("GoodIssueData").child(goodIssueModelArrayList.get(i).getGiUID());
                rootRef.child("giInvoiced").setValue(true);*/
                // SET VALUE TOTAL SELL







            }


            /*List<String> datePeriod = new ArrayList<>();
            for (int i = 0; i < goodIssueModelArrayList.size(); i++) {
                datePeriod.add(goodIssueModelArrayList.get(i).getGiDateCreated());
            }*/
            /*HashSet<String> filter = new HashSet(datePeriod);
            ArrayList<String> datePeriodFiltered = new ArrayList<>(filter);
*/
            /*rcpDateDeliveryPeriod = String.valueOf(datePeriodFiltered);*/

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

    public static int math(double d) {
        int c = (int) ((d) + 0.5d);
        double n = d + 0.5d;
        return (n - c) % 2 == 0 ? (int) d : c;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private void searchQueryAll(){
        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {

                        if (Objects.equals(item.child("giRecappedTo").getValue(), rcpDocumentID)) {
                            GiModel giModel = item.getValue(GiModel.class);
                            giModelArrayList.add(giModel);
                            //binding.nestedScrollView.setVisibility(View.VISIBLE);
                            binding.llNoData.setVisibility(View.GONE);

                        }
                    }
                    if (giModelArrayList.size()==0) {
                        //binding.nestedScrollView.setVisibility(View.GONE);
                        binding.llNoData.setVisibility(View.VISIBLE);
                    }

                } else  {
                    //binding.nestedScrollView.setVisibility(View.GONE);
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giDataAdapter = new GiDataAdapter(context, giModelArrayList);
                binding.recyclerView.setAdapter(giDataAdapter);

                totalUnitFinal = 0;

                for (int i = 0; i < giModelArrayList.size(); i++) {
                    totalUnitFinal += giModelArrayList.get(i).getGiVhlCubication();
                }
                totalAmountForMaterials = matBuyPrice*Double.parseDouble(df.format(totalUnitFinal));

                binding.tvCubicationTotal.setText(df.format(totalUnitFinal)+" m3");
                binding.tvTotalDue.setText(currencyVal + " " + currencyFormat(df.format(totalAmountForMaterials)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recap_gi_menu, menu);
        menu.findItem(R.id.itemMenuPrint).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*if (item.getItemId() == R.id.itemMenuSave) {
            //TODO SAVE CHANGES TO
            return true;
        }*/

        if (item.getItemId() == R.id.itemMenuPrint){
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            } else {
                dialogInterfaceUtils.confirmCreateRecapFromUpdate(context, rcpDocumentID, rcpDateDeliveryPeriod, helperUtils.getUserId(), roDocumentID, roPoCustNumber, rcpDateDeliveryPeriod, df.format(totalUnitFinal), giModelArrayList);

            }
            return true;
        }
        if (item.getItemId() == R.id.itemShowHideFilter) {

            TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());
            if (binding.cdvFilter.getVisibility() == View.GONE) {
                binding.cdvFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            } else {
                binding.cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
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

        //loadRecapData();

        // HANDLE RESPONSIVE CONTENT
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
    }
}