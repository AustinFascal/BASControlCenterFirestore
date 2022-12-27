package com.ptbas.controlcenter.create;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ptbas.controlcenter.adapter.CoDataAdapter;
import com.ptbas.controlcenter.adapter.InvDataAdapter;
import com.ptbas.controlcenter.databinding.ActivityAddAioReportBinding;
import com.ptbas.controlcenter.model.BankAccModel;
import com.ptbas.controlcenter.model.CoModel;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.GiModel;
import com.ptbas.controlcenter.model.InvModel;
import com.ptbas.controlcenter.model.ProductItems;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddAioReportActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    double matQuantity, matSellPrice, transportServiceSellPrice;
    //rouidVal= ""
    //pouidVal = "",
    String invDateNTimeCreated, invTimeCreated, currencyVal = "",
            roPoCustNumber, matTypeVal, matNameVal, transportServiceNameVal,
            invPoDate = "", invCustName = "", invPoUID = "", custNameVal = "", roDocumentID = "", coDocumentID, coUID,  bankAccountID = "", connectingRODocumentUID = "";
    int invPoType;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<GiModel> giModelArrayList = new ArrayList<>();
    ArrayList<CoModel> coModelArrayList = new ArrayList<>();
    ArrayList<InvModel> invModelArrayList = new ArrayList<>();
    ArrayList<InvModel> connectingInvModelArrayList = new ArrayList<>();
    ArrayList<BankAccModel> bankAccModelArrayList = new ArrayList<>();
    CoDataAdapter coDataAdapter;
    InvDataAdapter invDataAdapter;
    Context context;
    HelperUtils helperUtils = new HelperUtils();
    Boolean expandStatus = true, firstViewDataFirstTimeStatus = true;
    View firstViewData;

    List<String> bankAccountDocumentID, bankAccount, arrayListRoUID, arrayListPoUID, arrayListCoUID;
    List<ProductItems> productItemsList;
    List<ProductItems> productItemsList2;
    List<String> customerName, arrayListCustDocumentID;
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

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
    String invCreatedBy="", custDocumentID ="", selectedCustName;

    double totalUnitAmountForMaterials;

    double matBuyPrice, coTotal;

    public String[] coDateAndTimeACC, deliveryPeriod;

    public String coInvDocumentUID;

    public int showOccurances;

    String custNameFinal;

    StringBuilder s4;

    Document document;

    double priceListTest;
    String datePaid;

    private static final ArrayList<CoModel> mArrayListCO = new ArrayList<>();
    private static final ArrayList<Double> mArrayListTotalDue = new ArrayList<>();
    private static final ArrayList<String> mArrayListDatePaid = new ArrayList<>();
    private static final ArrayList<BankAccModel> mArrayListBankAlias = new ArrayList<>();

    String invTransferDate, invTotalDue, coDocumentUID, testID, bankAccountDocumentUID, coBankAccountDocumentUID, bankAccountAlias, coBankAccountAlias;

    double sumUnit, sumTotalMat, sumTotalPPNMat, sumTotalService, sumTotalPPH23Service, sumTotalPPNService, sumTotalSell, sumTotalPaid,
            sumTotalBuy, sumTotalTfSup;

    String connectingInvTotalDue = "0.00";

    ActivityAddAioReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddAioReportBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        s4 = new StringBuilder(100);

        dfRound.setRoundingMode(RoundingMode.HALF_UP);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        LangUtils.setLocale(this, "en");

        helperUtils.ACTIVITY_NAME = "UPDATE";

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

        /*binding.cdvFilter = findViewById(R.id.cdv_filter);
        binding.btnSearchData = findViewById(R.id.caridata);

        binding.spinnerCustName = findViewById(R.id.binding.spinnerCustName);
        binding.spinnerRoUID = findViewById(R.id.rouid);
        binding.edtPoUID = findViewById(R.id.pouid);
        binding.recyclerView = findViewById(R.id.rvItemList);
        binding.imgbtnExpandCollapseFilterLayout = findViewById(R.id.binding.imgbtnExpandCollapseFilterLayout);
        binding.llWrapFilterByDateRange = findViewById(R.id.ll_wrap_filter_by_date_range);
        binding.llWrapFilterByRouid = findViewById(R.id.ll_wrap_filter_by_rouid);
        binding.llWrapFilter = findViewById(R.id.binding.llWrapFilter);
        binding.llShowSpinnerRoAndEdtPo = findViewById(R.id.binding.llShowSpinnerRoAndEdtPo);

        binding.llNoData = findViewById(R.id.ll_no_data);
        binding.nestedScrollView = findViewById(R.id.binding.nestedScrollView);

        tvTotalSelectedItem = findViewById(R.id.tvTotalSelectedItem);
        tvTotalSelectedItem2 = findViewById(R.id.tvTotalSelectedItem2);
        binding.btnExitSelection = findViewById(R.id.binding.btnExitSelection);
        binding.llBottomSelectionOptions = findViewById(R.id.binding.llBottomSelectionOptions);

        binding.btnResetCustomer = findViewById(R.id.binding.btnResetCustomer);
        binding.btnResetRouid = findViewById(R.id.btnResetRouid);
        binding.fabCreateDocument = findViewById(R.id.fabCreateCOR);*/

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Buat Laporan All-In-One");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);


        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");


        final String userId = helperUtils.getUserId();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invCreatedBy = snapshot.child(userId).child("fullName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddAioReportActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });

        // CREATE GI MANAGEMENT ADAPTER
        coDataAdapter = new CoDataAdapter(this, coModelArrayList);
        invDataAdapter = new InvDataAdapter(this, invModelArrayList);

        // NOTIFY REAL-TIME CHANGES AS USER CHOOSE THE ITEM
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {


                handler.postDelayed(this, 100);
            }
        };
        runnable.run();


        binding.imgbtnExpandCollapseFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstViewDataFirstTimeStatus){
                    view = View.inflate(context, R.layout.activity_add_rcp, null);
                    firstViewData = view.findViewById(R.id.ll_wrap_filter_by_date_range);
                    firstViewDataFirstTimeStatus = false;
                }
                expandFilterViewValidation();
                TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());
            }
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
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAioReportActivity.this, R.layout.style_spinner, customerName);
                            arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                            binding.spinnerCustName.setAdapter(arrayAdapter);
                        } else {
                        }
                    }
                });

        binding.spinnerCustName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCustomer = (String) adapterView.getItemAtPosition(i);
                String[] custNameSplit = selectedCustomer.split(" - ");
                String custNameSplit1 = custNameSplit[1];

                selectedCustName = selectedCustomer;
                binding.spinnerCustName.setError(null);
                binding.spinnerRoUID.setText(null);
                binding.edtPoUID.setText(null);

                binding.btnResetCustomer.setVisibility(View.VISIBLE);
                clearRoPoData();

                binding.llShowSpinnerRoAndEdtPo.setVisibility(View.VISIBLE);

                arrayListRoUID.clear();
                binding.spinnerRoUID.setAdapter(null);

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
                                                        long roType = (long) Objects.requireNonNull(e.get("roType"));
                                                        if (roType != 2){
                                                            arrayListRoUID.add(spinnerPurchaseOrders);
                                                        }
                                                    }
                                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAioReportActivity.this, R.layout.style_spinner, arrayListRoUID);
                                                    arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                                                    binding.spinnerRoUID.setAdapter(arrayAdapter);
                                                }
                                            });
                                }

                            }
                        });
            }
        });
        binding.spinnerCustName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (binding.spinnerCustName.getText().toString().equals("")){
                    binding.llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                }
                return false;
            }
        });
        binding.spinnerRoUID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                binding.spinnerRoUID.setError(null);
                String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);

                binding.btnResetRouid.setVisibility(View.VISIBLE);

                db.collection("ReceivedOrderData").whereEqualTo("roPoCustNumber", selectedSpinnerPoPtBasNumber).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                RoModel roModel = documentSnapshot.toObject(RoModel.class);
                                roModel.setRoDocumentID(documentSnapshot.getId());
                                //rouidVal = selectedSpinnerPoPtBasNumber;
                                roPoCustNumber = roModel.getRoPoCustNumber();
                                roDocumentID = roModel.getRoDocumentID();
                            }
                            binding.edtPoUID.setText(roPoCustNumber);
                        });
            }
        });

        binding.btnResetRouid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.spinnerRoUID.setText(null);
                binding.spinnerRoUID.requestFocus();
                binding.edtPoUID.setText(null);
                binding.edtPoUID.clearFocus();
                coUID = "";
                coDocumentID = "";
                roDocumentID = "";
                //rouidVal = "";
                //pouidVal = "";

                binding.btnResetRouid.setVisibility(View.GONE);
            }
        });

        binding.btnResetCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.findItem(R.id.select_all_data_recap).setVisible(false);
                clearSelection();
                coUID = "";
                coDocumentID = "";
                //rouidVal = "";
                //pouidVal = "";
                custNameVal = "";
                binding.spinnerCustName.setText(null);
                binding.llShowSpinnerRoAndEdtPo.setVisibility(View.GONE);
                binding.spinnerCustName.requestFocus();
                binding.edtPoUID.setText(null);
                binding.spinnerRoUID.setText(null);
                binding.edtPoUID.clearFocus();
                binding.spinnerRoUID.clearFocus();

                arrayListRoUID.clear();
                binding.spinnerRoUID.setAdapter(null);

                binding.btnResetCustomer.setVisibility(View.GONE);
                binding.btnResetRouid.setVisibility(View.GONE);
            }
        });

        binding.btnSearchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayListCO.clear();
                //mArrayList.clear();
                mArrayListTotalDue.clear();
                mArrayListDatePaid.clear();

                coInvDocumentUID = "";
                totalUnitAmountForMaterials = 0;

                View viewLayout = AddAioReportActivity.this.getCurrentFocus();
                if (viewLayout != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
                }

                if (binding.spinnerCustName.getText().toString().isEmpty()){
                    binding.spinnerCustName.setError("Mohon pilih pelanggan");
                } else{
                    binding.spinnerCustName.setError(null);
                }

                if (binding.spinnerRoUID.getText().toString().isEmpty()){
                    binding.spinnerRoUID.setError("Mohon pilih nomor RO dan PO");
                } else{
                    binding.spinnerRoUID.setError(null);
                }

                if (Objects.requireNonNull(binding.edtPoUID.getText()).toString().isEmpty()){
                    binding.edtPoUID.setError("Mohon pilih nomor RO dan PO");
                } else{
                    binding.edtPoUID.setError(null);
                }


                if (!binding.spinnerCustName.getText().toString().isEmpty()&&
                        !binding.spinnerRoUID.getText().toString().isEmpty()&&
                        !binding.edtPoUID.getText().toString().isEmpty()){
                    searchQuery();
                }
            }
        });

        binding.fabCreateDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                } else {
                    StringBuilder s0 = new StringBuilder(100);
                    for (int i = 0; i< invDataAdapter.getSelected().size(); i++) {
                        s0.append(invDataAdapter.getSelected().get(i).getInvDateDeliveryPeriod()).append(",");
                    }

                    String invDateDeliveryPeriod = s0.toString().replace("[","").replace("]","").replace(" ","");
                    invDateDeliveryPeriod = removeDuplicates(invDateDeliveryPeriod, "\\,");

                    String[] strings = invDateDeliveryPeriod.split(",");
                    List<String> list = Arrays.asList(strings);
                    Collections.sort(list);

                    invDateDeliveryPeriodVal = list.toString().replace("[","").replace("]","").replace(" ","");

                    invTimeCreated =
                            new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    invDateNTimeCreated =
                            new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()).concat(" "+invTimeCreated);

                    dialogInterfaceUtils.confirmCreateAIO(context, invPoUID,
                            giModelArrayList);

                }
            }
        });

        binding.btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSelection();
            }
        });
    }

    public static String removeDuplicates(String txt, String splitterRegex) {
        List<String> values = new ArrayList<String>();
        String[] splitted = txt.split(splitterRegex);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < splitted.length; ++i) {
            if (!values.contains(splitted[i])) {
                values.add(splitted[i]);
                sb.append(',');
                sb.append(splitted[i]);
            }
        }
        return sb.substring(1);
    }

    private void clearRoPoData() {
        //rouidVal = null;
        //pouidVal = null;

        binding.spinnerRoUID.setText(null);
        binding.edtPoUID.setText(null);
    }

    private void clearSelection(){
        isSelectedAll = false;
        menu.findItem(R.id.select_all_data_recap).setIcon(ContextCompat.getDrawable(AddAioReportActivity.this, R.drawable.ic_outline_select_all));

        invDataAdapter.clearSelection();
        binding.llBottomSelectionOptions.animate()
                .translationY(binding.llBottomSelectionOptions.getHeight()).alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.llBottomSelectionOptions.setVisibility(View.GONE);
                    }
                });
    }

    public void createAIOPDF(String dest){

        if (new File(dest).exists()){
            new File(dest).deleteOnExit();
        }

       /* db.collection("InvoiceData").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<InvoiceModel> invoiceModels = queryDocumentSnapshots.toObjects(InvoiceModel.class);
                        mArrayList.addAll(invoiceModels);*/

        db.collection("CashOutData").get()
                .addOnSuccessListener(queryDocumentSnapshotsCo -> {
                    if (!queryDocumentSnapshotsCo.isEmpty()) {
                        List<CoModel> coModels = queryDocumentSnapshotsCo.toObjects(CoModel.class);
                        mArrayListCO.addAll(coModels);
                    }
                    db.collection("BankAccountData").get()
                            .addOnSuccessListener(queryDocumentSnapshots3 -> {
                                if (!queryDocumentSnapshots3.isEmpty()) {

                                    try {
                                        Rectangle f4Landscape = new Rectangle(936, 596);
                                        document = new Document(f4Landscape, 10, 10, 10, 10);
                                        PdfWriter.getInstance(document, new FileOutputStream(dest));
                                        document.open();
                                        document.addAuthor("PT BAS");
                                        document.addCreator("BAS Control Center");
                                        document.addCreationDate();
                                        //addAIOTtl(document);
                                        //addSpace(document);

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
                                        PdfPTable tblInvSection1 = new PdfPTable(16);
                                        PdfPTable tblInvSection2 = new PdfPTable(16);
                                        PdfPTable tblInvSection3 = new PdfPTable(15);
                                        PdfPTable tblInvSection4 = new PdfPTable(9);

                                        tblInvSection4.setHorizontalAlignment(Element.ALIGN_LEFT);

                                        // WIDTH PERCENTAGE CONFIG
                                        tblInvSection0.setWidthPercentage(100);
                                        tblInvSection1.setWidthPercentage(100);
                                        tblInvSection2.setWidthPercentage(100);
                                        tblInvSection3.setWidthPercentage(100);
                                        tblInvSection4.setWidthPercentage(100);

                                        // WIDTH FLOAT CONFIG
                                        tblInvSection0.setWidths(new float[]{1,36,14});
                                        tblInvSection1.setWidths(new float[]{1,3,2,4,3,4,3,3,4,4,3,3,4,4,3,3});
                                        tblInvSection2.setWidths(new float[]{1,3,2,4,3,4,3,3,4,4,3,3,4,4,3,3});
                                        tblInvSection3.setWidths(new float[]{4,2,4,3,4,3,3,4,4,3,3,4,4,3,3});
                                        tblInvSection4.setWidths(new float[]{10,15,2,1,3,1,5,1,4});

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
                                                new Paragraph("Total Mat.", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection1.addCell(cellTxtNrml(
                                                new Paragraph("PPN Mat.", fontMediumSmall),
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
                                                new Paragraph("Masuk Ke", fontMediumSmall),
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
                                                new Paragraph("Keluar Via", fontMediumSmall),
                                                Element.ALIGN_CENTER));

                                        for (int t = 0; t<16; t++){
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph("-", fontMediumWhite), Element.ALIGN_CENTER));
                                        }
                                        deliveryPeriod = invDateDeliveryPeriodVal.split(",");

                                        for (int i = 0; i < deliveryPeriod.length; i++) {
                                            for (int j = 0; j < giModelArrayList.size(); j++) {
                                                for (int k = 0; k < invDataAdapter.getSelected().size(); k++) {
                                                    if (giModelArrayList.get(j).getGiDateCreated().equals(deliveryPeriod[i])
                                                            && giModelArrayList.get(j).getGiInvoicedTo().equals(invDataAdapter.getSelected().get(k).getInvDocumentUID())) {
                                                        totalUnitAmountForMaterials += giModelArrayList.get(j).getGiVhlCubication();
                                                        invTransferDate = invDataAdapter.getSelected().get(k).getInvDateVerified();
                                                        invTotalDue = invDataAdapter.getSelected().get(k).getInvTotalDue();
                                                        coDocumentUID = invDataAdapter.getSelected().get(k).getInvDocumentUID();
                                                        testID = giModelArrayList.get(j).getGiCashedOutTo();

                                                        bankAccountDocumentUID = invDataAdapter.getSelected().get(k).getBankDocumentID();

                                                        if (connectingInvModelArrayList != null) {
                                                            if (!(connectingInvModelArrayList.size() < invDataAdapter.getSelected().size())) {
                                                                connectingInvTotalDue = connectingInvModelArrayList.get(k).getInvTotalDue();
                                                            }
                                                        } else{
                                                            connectingInvTotalDue = "0.00";
                                                        }
                                                    }
                                                }
                                            }


                                            for (int z = 0; z < mArrayListCO.size(); z++) {
                                                if (mArrayListCO.get(z).getCoDocumentID().equals(testID)) {
                                                    priceListTest = mArrayListCO.get(z).getCoTotal();
                                                    datePaid = mArrayListCO.get(z).getCoDateAndTimeACC();
                                                    mArrayListTotalDue.add(priceListTest);
                                                    mArrayListDatePaid.add(datePaid);
                                                    coBankAccountDocumentUID = mArrayListCO.get(z).getBankDocumentID();}
                                            }


                                            for (DocumentSnapshot d : queryDocumentSnapshots3) {
                                                BankAccModel bankAccModels = d.toObject(BankAccModel.class);
                                                if (Objects.equals(bankAccModels.getBankAccountID(), bankAccountDocumentUID)){
                                                    bankAccountAlias = bankAccModels.getBankAccountAlias();
                                                }
                                                if (Objects.equals(bankAccModels.getBankAccountID(), coBankAccountDocumentUID)){
                                                    coBankAccountAlias = bankAccModels.getBankAccountAlias();
                                                }
                                            }


                                            double totalAmountForMaterials;
                                            double totalAmountMatBuyPrice;
                                            double totalAmountForTransportService;
                                            double taxPPH;
                                            double taxPPN;
                                            double taxPPNService;

                                            sumUnit += totalUnitAmountForMaterials;


                                            totalAmountForMaterials = matSellPrice * Double.parseDouble(df.format(totalUnitAmountForMaterials));
                                            totalAmountMatBuyPrice = matBuyPrice * Double.parseDouble(df.format(totalUnitAmountForMaterials));
                                            totalAmountForTransportService = transportServiceSellPrice * totalUnitAmountForMaterials;

                                            sumTotalMat += totalAmountForMaterials;

                                            taxPPH = Double.parseDouble(df.format(0.02 * totalAmountForTransportService));
                                            taxPPN = Double.parseDouble(df.format(0.11 * totalAmountForMaterials));
                                            taxPPNService = Double.parseDouble(df.format(0.11 * totalAmountForTransportService));

                                            sumTotalPPNMat += taxPPN;
                                            sumTotalService += totalAmountForTransportService;
                                            sumTotalPPH23Service += taxPPH;
                                            sumTotalPPNService += taxPPNService;
                                            sumTotalBuy += totalAmountMatBuyPrice;


                                            double totalDue = totalAmountForMaterials + totalAmountForTransportService - taxPPH + taxPPN + taxPPNService;

                                            sumTotalSell += totalDue;

                                            String rowNumberStrVal = String.valueOf(i + 1);
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(rowNumberStrVal, fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(deliveryPeriod[i], fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(df.format(totalUnitAmountForMaterials), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalAmountForMaterials)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(taxPPN)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalAmountForTransportService)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph("(" + currencyFormat(df.format(taxPPH)) + ")", fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(taxPPNService)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalDue)), fontNormalSmall), Element.ALIGN_CENTER));

                                            double connectingInvTotalDueFinal = Double.parseDouble(connectingInvTotalDue.replace(",", "").replace("IDR ", ""));
                                            double invTotalDueFinal = Double.parseDouble(invTotalDue.replace(",", "").replace("IDR ", ""));

                                            double sumFinalTotalDue = connectingInvTotalDueFinal + invTotalDueFinal;

                                            /*for (int o = 0; o < connectingInvoiceModelArrayList.size(); o++){
                                                sumTotalPaid += sumFinalTotalDue;
                                            }*/

                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(sumFinalTotalDue)), fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(invTransferDate, fontNormalSmall), Element.ALIGN_CENTER));


                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(bankAccountAlias, fontNormalSmall), Element.ALIGN_CENTER));
                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(totalAmountMatBuyPrice)), fontNormalSmall), Element.ALIGN_CENTER));




                                            if (!mArrayListCO.isEmpty()) {
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph(currencyFormat(dfRound.format(priceListTest)), fontNormalSmall), Element.ALIGN_CENTER));
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph(datePaid, fontNormalSmall), Element.ALIGN_CENTER));
                                            } else {
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("", fontNormalSmall), Element.ALIGN_CENTER));
                                            }

                                            tblInvSection2.addCell(cellTxtNrml(
                                                    new Paragraph(coBankAccountAlias, fontNormalSmall), Element.ALIGN_CENTER));

                                            totalUnitAmountForMaterials = 0;
                                            showOccurances = 0;
                                            coInvDocumentUID = "";

                                            for (int t = 0; t < 16; t++) {
                                                tblInvSection2.addCell(cellTxtNrml(
                                                        new Paragraph("-", fontMediumWhite), Element.ALIGN_CENTER));
                                            }
                                        }



                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("TOTAL", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(df.format(sumUnit), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(sumTotalMat)), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(sumTotalPPNMat)), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(sumTotalService)), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("("+currencyFormat(df.format(sumTotalPPH23Service))+")", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(sumTotalPPNService)), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(sumTotalSell)), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("-", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("-", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("-", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(sumTotalBuy)), fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("-", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("-", fontNormalSmall), Element.ALIGN_CENTER));
                                        tblInvSection3.addCell(cellTxtNrml(
                                                new Paragraph("-", fontNormalSmall), Element.ALIGN_CENTER));

                                        double grossProfit, grossProfitMargin, profitPercentage;

                                        grossProfit = sumTotalSell-sumTotalBuy;
                                        grossProfitMargin = 100*(grossProfit/sumTotalSell);
                                        profitPercentage = 100*(grossProfit/sumTotalBuy);

                                        tblInvSection4.addCell(cellTxtNoBorderSpan2(
                                                new Paragraph("LAPORAN PENGIRIMAN "+matNameVal+" "+matTypeVal+" ("+currencyVal+")"+"\n"+
                                                        custNameFinal+" | PO: "+roPoCustNumber+" | "+currencyFormat(df.format(matQuantity)) +" m3", fontBold),
                                                Element.ALIGN_LEFT));
                                        tblInvSection4.addCell(cellTxtNoBorder(
                                                new Paragraph(" ", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph("HB Mat.", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(":", fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(matBuyPrice)), fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNoBorder(
                                                new Paragraph(" ", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph("Gross Profit", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(":", fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(grossProfit)), fontNormalSmall),
                                                Element.ALIGN_CENTER));


                                        tblInvSection4.addCell(cellTxtNoBorder(
                                                new Paragraph(" ", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph("HJ Mat.:", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(":", fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(matSellPrice)), fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNoBorder(
                                                new Paragraph(" ", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph("Gross Profit Margin", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(":", fontNormalSmall),
                                                Element.ALIGN_CENTER));


                                        if (grossProfitMargin<=5){
                                            tblInvSection4.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(grossProfitMargin))+"% (Low/Bad)", fontNormalSmall),
                                                    Element.ALIGN_CENTER));
                                        } else if (grossProfitMargin>5 && grossProfitMargin<20){
                                            tblInvSection4.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(grossProfitMargin))+"% (Average)", fontNormalSmall),
                                                    Element.ALIGN_CENTER));
                                        } else if (grossProfitMargin>=20 && grossProfitMargin<50){
                                            tblInvSection4.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(grossProfitMargin))+"% (High/Good)", fontNormalSmall),
                                                    Element.ALIGN_CENTER));
                                        } else if (grossProfitMargin>=50){
                                            tblInvSection4.addCell(cellTxtNrml(
                                                    new Paragraph(currencyFormat(df.format(grossProfitMargin))+"% (Great)", fontNormalSmall),
                                                    Element.ALIGN_CENTER));
                                        }

                                        /*

                                        Profit margin is calculated with selling price (or revenue) taken as base times 100.
                                        It is the percentage of selling price that is turned into profit, whereas "profit percentage"
                                        or "markup" is the percentage of cost price that one gets as profit on top of cost price.

                                         */





                                        tblInvSection4.addCell(cellTxtNoBorder(
                                                new Paragraph(" ", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph("HJ Jasa", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(":", fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(transportServiceSellPrice)), fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNoBorder(
                                                new Paragraph(" ", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph("Gross Profit Percentage", fontMediumSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(":", fontNormalSmall),
                                                Element.ALIGN_CENTER));
                                        tblInvSection4.addCell(cellTxtNrml(
                                                new Paragraph(currencyFormat(df.format(profitPercentage))+"%", fontNormalSmall),
                                                Element.ALIGN_CENTER));



                                        document.add(tblInvSection4);
                                        addSpace(document);
                                        document.add(tblInvSection0);
                                        document.add(tblInvSection1);
                                        document.add(tblInvSection2);
                                        document.add(tblInvSection3);
                                        document.add(paragraphBlank);
                                        document.add(paragraphInvNote);
                                        document.add(paragraphInvDateCreated);

                                        document.close();
                                        dialogInterfaceUtils.aioDocumentGeneratedInformation(context, dest);

                                        connectingRODocumentUID = null;
                                        connectingInvModelArrayList = null;
                                        connectingInvTotalDue = "0.00";
                                    } catch (DocumentException | FileNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                });


        //}

        //});
    }

    private void addAIOTtl(Document document) throws DocumentException {
        Paragraph preface1 = new Paragraph();
        Chunk title = new Chunk("LAPORAN PENGIRIMAN "+matNameVal+" "+matTypeVal+" ("+currencyVal+")"+"\n"+
                custNameFinal+" | PO: "+roPoCustNumber+" | "+currencyFormat(df.format(matQuantity)) +" m3", fontBold);
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

    public static PdfPCell cellTxtNoBorder(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    public static PdfPCell cellTxtNoBorderSpan2(Paragraph paragraph, int alignment) {
        paragraph.setAlignment(alignment);
        paragraph.setLeading(0, 1);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setRowspan(3);
        cell.addElement(paragraph);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

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
        TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());

        CollectionReference refRO = db.collection("ReceivedOrderData");
        refRO.whereEqualTo("roDocumentID", roDocumentID).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (productItemsList != null){
                        productItemsList.clear();
                    }

                    transportServiceSellPrice = 0;
                    matBuyPrice = 0;

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        RoModel roModel = documentSnapshot.toObject(RoModel.class);
                        roModel.setRoDocumentID(documentSnapshot.getId());

                        custDocumentID = roModel.getRoDocumentID();
                        matTypeVal = roModel.getRoMatType();
                        roPoCustNumber = roModel.getRoPoCustNumber();
                        custNameVal = roModel.getCustDocumentID();
                        currencyVal = roModel.getRoCurrency();
                        invCustName = roModel.getCustDocumentID();
                        invPoUID = roModel.getRoUID();
                        invPoDate = roModel.getRoDateCreated();
                        invPoType = roModel.getRoType();
                        connectingRODocumentUID = roModel.getRoConnectingRoDocumentUID();

                        HashMap<String, List<ProductItems>> map = roModel.getRoOrderedItems();
                        for (HashMap.Entry<String, List<ProductItems>> e : map.entrySet()) {
                            productItemsList = e.getValue();
                            for (int i = 0; i<productItemsList.size();i++){
                                if (productItemsList.get(0).getMatName().equals("JASA ANGKUT")){
                                    transportServiceNameVal = productItemsList.get(0).getMatName();
                                    transportServiceSellPrice = productItemsList.get(0).getMatSellPrice();
                                } else {
                                    matNameVal = productItemsList.get(i).getMatName();
                                    matQuantity = productItemsList.get(i).getMatQuantity();
                                    matBuyPrice = productItemsList.get(i).getMatBuyPrice();
                                    matSellPrice = productItemsList.get(i).getMatSellPrice();
                                }
                            }
                        }

                        if (connectingRODocumentUID!=null){
                            refRO.whereEqualTo("roDocumentID", connectingRODocumentUID).get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                                if (productItemsList2 != null) {
                                    productItemsList2.clear();
                                }

                                transportServiceSellPrice = 0;

                                for (QueryDocumentSnapshot documentSnapshot2 : queryDocumentSnapshots2) {
                                    RoModel roModel2 = documentSnapshot2.toObject(RoModel.class);
                                    roModel2.setRoDocumentID(documentSnapshot2.getId());

                                    HashMap<String, List<ProductItems>> map2 = roModel2.getRoOrderedItems();
                                    for (HashMap.Entry<String, List<ProductItems>> e : map2.entrySet()) {
                                        productItemsList2 = e.getValue();
                                        for (int i = 0; i<productItemsList2.size();i++){
                                            if (productItemsList2.get(0).getMatName().equals("JASA ANGKUT")){
                                                transportServiceNameVal = productItemsList2.get(0).getMatName();
                                                transportServiceSellPrice = productItemsList2.get(0).getMatSellPrice();
                                            }
                                        }
                                    }
                                }
                            });

                            db.collection("InvoiceData")
                                    .addSnapshotListener((value, error) -> {
                                        //connectingInvoiceModelArrayList.clear();
                                        if (!value.isEmpty()){
                                            for (DocumentSnapshot d : value.getDocuments()) {
                                                InvModel invModel = d.toObject(InvModel.class);
                                                if (invModel.getRoDocumentID().equals(connectingRODocumentUID)) {
                                                    connectingInvModelArrayList.add(invModel);

                                                }
                                            }
                                            Collections.reverse(connectingInvModelArrayList);
                                        }
                                    });

                        } else{
                            connectingInvModelArrayList = null;
                        }

                        // Get good issue data
                        Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                giModelArrayList.clear();
                                if (snapshot.exists()){
                                    for (DataSnapshot item : snapshot.getChildren()) {
                                        if (Objects.equals(item.child("roDocumentID").getValue(), roDocumentID)) {
                                            GiModel giModel = item.getValue(GiModel.class);
                                            giModelArrayList.add(giModel);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // Get cash out data
                        CollectionReference refCO = db.collection("CashOutData");
                        refCO.whereEqualTo("roDocumentID", roDocumentID).get()
                                .addOnSuccessListener(queryDocumentSnapshotsCO -> {
                                    for (QueryDocumentSnapshot documentSnapshotCO : queryDocumentSnapshotsCO){
                                        CoModel coModel = documentSnapshotCO.toObject(CoModel.class);
                                        coTotal =  coModel.getCoTotal();
                                        coDateAndTimeACC = coModel.getCoDateAndTimeACC().split(" ");
                                    }
                                });


                        // Get customer data
                        CollectionReference refCust = db.collection("CustomerData");
                        refCust.whereEqualTo("custDocumentID", custNameVal).get()
                                .addOnSuccessListener(queryDocumentSnapshotsCust -> {
                                    for (QueryDocumentSnapshot documentSnapshotCust : queryDocumentSnapshotsCust){
                                        CustModel custModel = documentSnapshotCust.toObject(CustModel.class);
                                        custNameFinal = custModel.getCustName();
                                    }
                                });

                    }
                });

        db.collection("InvoiceData").addSnapshotListener((value, error) -> {
            invModelArrayList.clear();
            if (!value.isEmpty()){
                for (DocumentSnapshot d : value.getDocuments()) {
                    InvModel invModel = d.toObject(InvModel.class);
                    if (invModel.getRoDocumentID().contains(roDocumentID)) {
                        invModelArrayList.add(invModel);
                    }
                }
                binding.llNoData.setVisibility(View.GONE);
                binding.nestedScrollView.setVisibility(View.VISIBLE);
                if (invModelArrayList.size()==0) {
                    binding.fabCreateDocument.hide();
                    binding.nestedScrollView.setVisibility(View.GONE);
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            } else{
                binding.llNoData.setVisibility(View.VISIBLE);
                binding.nestedScrollView.setVisibility(View.GONE);
            }
            Collections.reverse(invModelArrayList);
            invDataAdapter = new InvDataAdapter(context, invModelArrayList);
            binding.recyclerView.setAdapter(invDataAdapter);
        });
    }

    private void expandFilterViewValidation() {
        if (expandStatus){
            showHideFilterComponents(true);
            expandStatus=false;
            binding.imgbtnExpandCollapseFilterLayout.setText(R.string.showMore);
            binding.imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_down, 0);
        } else {
            showHideFilterComponents(false);
            expandStatus=true;
            binding.imgbtnExpandCollapseFilterLayout.setText(R.string.showLess);
            binding.imgbtnExpandCollapseFilterLayout.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_keyboard_arrow_up, 0);
        }
    }

    private void showHideFilterComponents(Boolean expandStatus) {
        if (expandStatus){
            binding.llWrapFilter.setVisibility(View.GONE);
        } else {
            binding.llWrapFilter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aio_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemShowHideFilter) {
            binding.imgbtnExpandCollapseFilterLayout.setVisibility(View.VISIBLE);
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

        if (item.getItemId() == R.id.select_all_data_recap) {

            if (!isSelectedAll){
                isSelectedAll = true;
                item.setIcon(R.drawable.ic_outline_deselect);
                binding.llBottomSelectionOptions.animate()
                        .translationY(0).alpha(1.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                binding.llBottomSelectionOptions.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                isSelectedAll = false;
                item.setIcon(R.drawable.ic_outline_select_all);
                binding.llBottomSelectionOptions.animate()
                        .translationY(binding.llBottomSelectionOptions.getHeight()).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                binding.llBottomSelectionOptions.setVisibility(View.GONE);
                            }
                        });
            }

            return true;
        }

        helperUtils.ACTIVITY_NAME = null;
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helperUtils.ACTIVITY_NAME = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helperUtils.ACTIVITY_NAME = null;
    }
}