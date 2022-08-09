package com.ptbas.controlcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.model.ReceivedOrderModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecapGoodIssueDataActivity extends AppCompatActivity {

    double matBuyPrice;
    String dateStartVal = "", dateEndVal = "", rouidVal= "", pouidVal = "";
    Button btnSearchData, imgbtnExpandCollapseFilterLayout;
    AutoCompleteTextView spinnerRoUID;
    TextInputEditText edtPoUID;
    TextInputEditText edtDateStart, edtDateEnd;
    DatePickerDialog datePicker;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    ArrayList<ReceivedOrderModel> receivedOrderModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;
    Context context;
    Helper helper = new Helper();
    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    CardView cdvFilter;
    View firstViewData;
    NestedScrollView nestedScrollView;

    List<String> arrayListRoUID, arrayListPoUID;

    LinearLayout llWrapFilterByDateRange, llWrapFilterByRouid, llWrapFilterByPoCustNumb, llNoData;

    ImageButton btnGiSearchByDateReset, btnGiSearchByRoUIDReset, btnGiSearchByPoUIDReset;

    ExtendedFloatingActionButton fabCreateGiRecap;

    DialogInterface dialogInterface = new DialogInterface();
    String roPoCustNumber, roUID;

    public String custNameVal = "";

    List<String> matNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_good_issue_data);

        context = this;

        matNameList  = new ArrayList<>();

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
        //llWrapFilterByPoCustNumb = findViewById(R.id.ll_wrap_filter_by_po_cust_numb);

        llNoData = findViewById(R.id.ll_no_data);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        btnGiSearchByDateReset = findViewById(R.id.btn_gi_search_date_reset);
        btnGiSearchByRoUIDReset = findViewById(R.id.btn_gi_search_rouid_reset);
        //btnGiSearchByPoUIDReset = findViewById(R.id.btn_gi_search_pouid_reset);

        fabCreateGiRecap = findViewById(R.id.fab_create_gi_recap);

        ActionBar actionBar = getSupportActionBar();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helper.handleActionBarConfigForStandardActivity(
                this, actionBar, "Rekap Data Valid Good Issue");

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
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(RecapGoodIssueDataActivity.this,
                    (datePicker, year12, month12, dayOfMonth) -> {
                        edtDateStart.setText(dayOfMonth + "/" + (month12 + 1) + "/" + year12);
                        dateStartVal = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, year, month, day);
            datePicker.show();
        });

        edtDateEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(RecapGoodIssueDataActivity.this,
                    (datePicker, year12, month12, dayOfMonth) -> {
                        edtDateEnd.setText(dayOfMonth + "/" + (month12 + 1) + "/" + year12);
                        dateEndVal = dayOfMonth + "/" + (month12 + 1) + "/" + year12;
                        btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, year, month, day);
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
        databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerRoUIDVal = dataSnapshot.child("roUID").getValue(String.class);
                        arrayListRoUID.add(spinnerRoUIDVal);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, arrayListRoUID);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerRoUID.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(RecapGoodIssueDataActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*databaseReference.child("ReceivedOrders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String spinnerPoUIDVal = dataSnapshot.child("roPoCustNumber").getValue(String.class);
                        if (!spinnerPoUIDVal.equals("-")){
                            arrayListPoUID.add(spinnerPoUIDVal);
                        }
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RecapGoodIssueDataActivity.this, R.layout.style_spinner, arrayListPoUID);
                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                    spinnerPoUID.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(RecapGoodIssueDataActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        /*spinnerPoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            //btnGiSearchByPoUIDReset.setVisibility(View.VISIBLE);
            //spinnerRoUID.setText(null);
            //spinnerRoUID.clearFocus();
            //btnGiSearchByRoUIDReset.setVisibility(View.VISIBLE);
            spinnerPoUID.setError(null);

            spinnerRoUID.setText(null);
        });*/

        spinnerRoUID.setOnItemClickListener((adapterView, view, i, l) -> {
            //btnGiSearchByRoUIDReset.setVisibility(View.VISIBLE);
            //spinnerPoUID.setText(null);
            //spinnerPoUID.clearFocus();
            //btnGiSearchByPoUIDReset.setVisibility(View.GONE);
            spinnerRoUID.setError(null);

            DatabaseReference databaseReferencePO = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ spinnerRoUID.getText().toString());
            databaseReferencePO.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    roPoCustNumber = snapshot.child("roPoCustNumber").getValue(String.class);
                    edtPoUID.setText(roPoCustNumber);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        btnGiSearchByDateReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDateStart.setText(null);
                edtDateEnd.setText(null);
                edtDateStart.clearFocus();
                edtDateEnd.clearFocus();
                btnGiSearchByDateReset.setVisibility(View.GONE);
                //matNameList.clear();
            }
        });

        /*btnGiSearchByPoUIDReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPoUID.setText(null);
                edtPoUID.clearFocus();
                btnGiSearchByPoUIDReset.setVisibility(View.GONE);
            }
        });*/

        btnGiSearchByRoUIDReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerRoUID.setText(null);
                spinnerRoUID.clearFocus();
                btnGiSearchByRoUIDReset.setVisibility(View.GONE);
                //matNameList.clear();
            }
        });

        fabCreateGiRecap.hide();



        btnSearchData.setOnClickListener(view -> {
            View viewLayout = RecapGoodIssueDataActivity.this.getCurrentFocus();
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

        fabCreateGiRecap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //matNameList.clear();
                /*Intent intent = new Intent(this, SecondActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("mylist", arraylist);
                intent.putExtras(bundle);
                this.startActivity(intent);*/


                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                } else {

                    createPDF(Helper.getAppPath(context)+rouidVal+".pdf");


                    //Toast.makeText(context, String.valueOf(goodIssueModelArrayList.size()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*public void print(View view) {

     *//*
        PdfDocument recapDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pdfPageInfo = new PdfDocument.PageInfo.Builder(1200,2010)
*//*
    }*/

    private void createPDF(String dest){

        //Toast.makeText(context, dest, Toast.LENGTH_LONG).show();
        if (new File(dest).exists()){
            new File(dest).delete();
        }

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();
            document.setPageSize(PageSize.A4);
            document.addAuthor("PT BAS");
            document.addCreator("BAS Control Center");
            document.addCreationDate();

            Font fontNormal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
            Font fontBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
            Font fontBigBold = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

            Chunk title = new Chunk("RO-"+rouidVal, fontBigBold);
            Chunk roMatNameType = new Chunk("Material: "+matNameList +" | "+goodIssueModelArrayList.get(0).getGiMatType(), fontNormal);
            Chunk roCustName = new Chunk("Customer: "+custNameVal, fontNormal);


            Paragraph paragraphTitle = new Paragraph(title);
            paragraphTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraphTitle);

            Paragraph preface = new Paragraph();
            preface.setAlignment(Element.ALIGN_CENTER);
            preface.setSpacingAfter(15);
            document.add(preface);
            document.add(new LineSeparator());

            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());

            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);

            PdfPCell cell1;
            cell1 = new PdfPCell(new Phrase("Nomor PO: "+roPoCustNumber, fontNormal));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setPadding(0);
            cell1.setBorder(PdfPCell.NO_BORDER);
            table1.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Tanggal rekap dibuat: "+currentDate, fontNormal));
            cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell1.setPadding(0);
            cell1.setBorder(PdfPCell.NO_BORDER);
            table1.addCell(cell1);

            document.add(table1);


           /* Paragraph paragraphPONumber = new Paragraph(poNumberVal);
            paragraphPONumber.setAlignment(Element.ALIGN_LEFT);
            paragraphPONumber.setSpacingAfter(0);
            document.add(paragraphPONumber);*/

            Paragraph paragraphROMatNameType = new Paragraph(roMatNameType);
            paragraphROMatNameType.setAlignment(Element.ALIGN_LEFT);
            paragraphROMatNameType.setSpacingAfter(0);
            document.add(paragraphROMatNameType);

            Paragraph paragraphROCustName = new Paragraph(roCustName);
            paragraphROCustName.setAlignment(Element.ALIGN_LEFT);
            paragraphROCustName.setSpacingAfter(5);
            document.add(paragraphROCustName);
            document.add(new LineSeparator());

            Paragraph preface2 = new Paragraph();
            preface2.setAlignment(Element.ALIGN_CENTER);
            preface2.setSpacingAfter(10);
            document.add(preface2);


            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2,4,3,4,2,2,2,2,2,3});

            PdfPCell cell;
            cell = new PdfPCell(new Phrase("No", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Tanggal", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("ID", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("NOPOL", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("P", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("L", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("T", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("K", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("TK", fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(Html.fromHtml("M\u00B3")), fontBold));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(cell);

            float totalCubication = 0;
            for (int i = 0; i < goodIssueModelArrayList.size(); i++){
                cell = new PdfPCell(new Phrase(String.valueOf(i+1), fontBold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getGiDateCreated(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getGiUID().substring(0,5), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getVhlUID(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getVhlLength().toString(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getVhlWidth().toString(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getVhlHeight().toString(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getVhlHeightCorrection().toString(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getVhlHeightAfterCorrection().toString(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(goodIssueModelArrayList.get(i).getGiVhlCubication().toString(), fontNormal));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                totalCubication += goodIssueModelArrayList.get(i).getGiVhlCubication();
            }

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);
            table2.setWidths(new float[]{23,3});

            PdfPCell cell2;
            cell2 = new PdfPCell(new Phrase("Total", fontBold));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBackgroundColor(BaseColor.YELLOW);
            table2.addCell(cell2);

            cell2 = new PdfPCell(new Phrase(String.valueOf(totalCubication), fontBold));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBackgroundColor(BaseColor.YELLOW);
            table2.addCell(cell2);



            PdfPTable table3 = new PdfPTable(2);
            table3.setWidthPercentage(100);
            table3.setWidths(new float[]{5, 4});

            PdfPCell cell3;
            cell3 = new PdfPCell(new Phrase("Total IDR", fontBold));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setBackgroundColor(BaseColor.YELLOW);
            table3.addCell(cell3);

            DecimalFormat df = new DecimalFormat("0.00");
            double totalIDR = matBuyPrice*totalCubication;

            cell3 = new PdfPCell(new Phrase(currencyFormat(df.format(totalIDR)), fontNormal));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell3);

            document.add(table);
            document.add(table2);
            document.add(table3);
            document.close();
            Helper.openFilePDF(context, new File(Helper.getAppPath(context)+rouidVal+".pdf"));
        } catch (DocumentException | FileNotFoundException e) {
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
        pouidVal = edtPoUID.getText().toString();

        DatabaseReference databaseReferencePO = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ rouidVal +"/OrderedItems/0");
        databaseReferencePO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    matBuyPrice = Objects.requireNonNull(snapshot.child("matBuyPrice").getValue(Double.class));

                } else {
                    Toast.makeText(RecapGoodIssueDataActivity.this, "Not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                    goodIssueModelArrayList.add(goodIssueModel);
                                    fabCreateGiRecap.show();
                                    nestedScrollView.setVisibility(View.VISIBLE);
                                    llNoData.setVisibility(View.GONE);



                                    DatabaseReference databaseReferencePO = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ spinnerRoUID.getText().toString());
                                    databaseReferencePO.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            custNameVal = snapshot.child("roCustName").getValue(String.class);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                DatabaseReference databaseReferencePO2 = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ rouidVal +"/OrderedItems");
                                databaseReferencePO2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        matNameList.clear();
                                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        String spinnerMaterialData = dataSnapshot.child("matName").getValue(String.class);
                                        matNameList.add(spinnerMaterialData);
                                        matNameList.remove("JASA ANGKUT");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                        /*if (!pouidVal.isEmpty()&&rouidVal.isEmpty()){
                            if (Objects.requireNonNull(item.child("giPoCustNumber").getValue()).toString().equals(pouidVal) &&
                                    !Objects.requireNonNull(item.child("giPoCustNumber").getValue()).toString().equals("-")) {
                                if (Objects.equals(item.child("giStatus").getValue(), true)) {
                                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);

                                    ArrayList<GoodIssueModel> giPosGetROTemp = new ArrayList<>();
                                    giPosGetROTemp.add(goodIssueModel);
                                    goodIssueModelArrayList.add(goodIssueModel);
                                    fabCreateGiRecap.show();
                                    nestedScrollView.setVisibility(View.VISIBLE);
                                    llNoData.setVisibility(View.GONE);



                                    DatabaseReference databaseReferencePO = FirebaseDatabase.getInstance().getReference("ReceivedOrders/"+ giPosGetROTemp.get(0).getGiRoUID());
                                    databaseReferencePO.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            custNameVal = snapshot.child("roCustName").getValue(String.class);
                                            roUID = giPosGetROTemp.get(0).getGiRoUID();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }*/
                    }
                    if (goodIssueModelArrayList.size()==0) {
                        fabCreateGiRecap.hide();
                        nestedScrollView.setVisibility(View.GONE);
                        llNoData.setVisibility(View.VISIBLE);
                        //Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else  {
                    fabCreateGiRecap.hide();
                    nestedScrollView.setVisibility(View.GONE);
                    llNoData.setVisibility(View.VISIBLE);
                    //Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
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
            imgbtnExpandCollapseFilterLayout.setText("Tampilkan lebih banyak");
            imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_down, 0);
        } else {
            showHideFilterComponents(false);
            expandStatus=true;
            imgbtnExpandCollapseFilterLayout.setText("Tampilkan lebih sedikit");
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recap_gi_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_data) {
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
    public void onBackPressed() {
        firstViewDataFirstTimeStatus = true;
        super.onBackPressed();
    }
}