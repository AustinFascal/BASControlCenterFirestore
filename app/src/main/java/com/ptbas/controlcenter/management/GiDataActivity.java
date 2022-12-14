package com.ptbas.controlcenter.management;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.create.AddGiActivity;
import com.ptbas.controlcenter.create.AddRcpActivity;
import com.ptbas.controlcenter.databinding.ActivityDataGiBinding;
import com.ptbas.controlcenter.model.CoModel;
import com.ptbas.controlcenter.model.CustModel;
import com.ptbas.controlcenter.model.GiModel;
import com.ptbas.controlcenter.model.RoModel;
import com.ptbas.controlcenter.update.UpdtGiActivity;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.DragLinearLayoutUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.utility.LangUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;

public class GiDataActivity extends AppCompatActivity {

    String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID", "giMatName"};
    String dateStart = "", dateEnd = "", searchTypeData="", monthStrVal, dayStrVal;

    Context context;

    DatePickerDialog datePicker;
    Boolean expandStatus = true;
    Boolean sortStatus = false;
    List<String> arrayListMaterialName, arrayListCompanyName;
    HelperUtils helperUtils = new HelperUtils();
    ArrayList<GiModel> giModelArrayList = new ArrayList<>();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> receiveOrderNumberList;

    SearchView searchView;

    ProgressDialog pd;

    public String roUID;

    GIAdapter giAdapter;

    ActivityDataGiBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDataGiBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        pd = new ProgressDialog(GiDataActivity.this);
        pd.setMessage("Memproses ...");
        pd.setCancelable(false);
        pd.show();

        context = this;

        helperUtils.ACTIVITY_NAME = "GIM";

        giAdapter = new GIAdapter(this, giModelArrayList, giAdapter, binding);


        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Good Issue");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        // DRAGLINEARLAYOUT FOR FILTERING
        DragLinearLayoutUtils dragLinearLayoutUtils = findViewById(R.id.drag_linear_layout);
        for(int i = 0; i < dragLinearLayoutUtils.getChildCount(); i++){
            View child = dragLinearLayoutUtils.getChildAt(i);
            // the child will act as its own drag handle
            dragLinearLayoutUtils.setViewDraggable(child, child);
        }

        // SET DEFAULT LANG CODE TO ENGLISH
        LangUtils.setLocale(this, "en");

        // INIT ARRAYS AND ADAPTER FOR FILTERING
        String[] searchType = {"ID Good Issue", "ID Received Order", "Nomor PO Customer", "NOPOL Kendaraan", "Nama Material"};
        ArrayList<String> arrayListSearchType = new ArrayList<>(Arrays.asList(searchType));
        ArrayAdapter<String> arrayAdapterSearchType = new ArrayAdapter<>(context, R.layout.style_spinner, arrayListSearchType);
        binding.spinnerSearchType.setAdapter(arrayAdapterSearchType);

        arrayListMaterialName = new ArrayList<>();
        arrayListCompanyName = new ArrayList<>();

        // GO TO ADD GOOD ISSUE ACTIVITY
        binding.fabActionCreateGi.setOnClickListener(view13 -> {
            Intent intent = new Intent(GiDataActivity.this, AddGiActivity.class);
            startActivity(intent);
        });

        // GO TO RECAP GOOD ISSUE ACTIVITY
        binding.fabActionCreateRecap.setOnClickListener(view12 -> {
            Intent intent = new Intent(GiDataActivity.this, AddRcpActivity.class);
            startActivity(intent);
        });


        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();

        // HANDLE FILTER COMPONENTS WHEN ON CLICK
        binding.edtGiDateFilterStart.setOnClickListener(view14 -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(GiDataActivity.this,
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

                        binding.edtGiDateFilterStart.setText(finalDate);
                        dateStart = finalDate;

                        checkSelectedChipFilter();

                        binding.btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        binding.edtGiDateFilterEnd.setOnClickListener(view15 -> {
            final Calendar calendar = Calendar.getInstance();
            dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
            String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

            datePicker = new DatePickerDialog(GiDataActivity.this,
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

                        binding.edtGiDateFilterEnd.setText(finalDate);
                        dateEnd = finalDate;

                        checkSelectedChipFilter();
                        binding.btnGiSearchByDateReset.setVisibility(View.VISIBLE);

                    }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        receiveOrderNumberList = new ArrayList<>();

        db.collection("ReceivedOrderData")
                .addSnapshotListener((value, error) -> {
                    receiveOrderNumberList.clear();
                    if (!Objects.requireNonNull(value).isEmpty()) {
                        for (DocumentSnapshot e : value.getDocuments()) {
                            String spinnerPurchaseOrders = Objects.requireNonNull(e.get("roUID")).toString();
                            receiveOrderNumberList.add(spinnerPurchaseOrders);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GiDataActivity.this, R.layout.style_spinner, receiveOrderNumberList);
                        arrayAdapter.setDropDownViewResource(R.layout.style_spinner);
                        binding.spinnerRoUID.setAdapter(arrayAdapter);
                    }
                });

        binding.spinnerRoUID.setOnItemClickListener((adapterView, view1, i, l) -> {
            binding.spinnerRoUID.setError(null);
            String selectedSpinnerPoPtBasNumber = (String) adapterView.getItemAtPosition(i);
            binding.btnResetSpinnerRoUID.setVisibility(View.VISIBLE);

            pd.setMessage("Memproses ...");
            pd.show();
            db.collection("ReceivedOrderData").whereEqualTo("roUID", selectedSpinnerPoPtBasNumber).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            RoModel roModel = documentSnapshot.toObject(RoModel.class);
                            roModel.setRoDocumentID(documentSnapshot.getId());

                            roUID = roModel.getRoDocumentID();

                            Toast.makeText(context, roUID, Toast.LENGTH_SHORT).show();

                            Query query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pd.dismiss();
                                    giModelArrayList.clear();
                                    if (snapshot.exists()){
                                        for (DataSnapshot item : snapshot.getChildren()){
                                            GiModel giModel = item.getValue(GiModel.class);
                                            if (giModel.getRoDocumentID().equals(roUID)){
                                                giModelArrayList.add(giModel);
                                            }

                                        }
                                        binding.llNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.llNoData.setVisibility(View.VISIBLE);
                                    }
                                    Collections.reverse(giModelArrayList);
                                    giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                                    binding.recyclerView.setAdapter(giAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    });
        });

        binding.btnResetSpinnerRoUID.setOnClickListener(view16 -> {
            binding.spinnerRoUID.setText(null);
            binding.btnResetSpinnerRoUID.setVisibility(View.GONE);
        });

        binding.spinnerSearchType.setOnItemClickListener((adapterView, view17, i, l) -> {
            binding.btnGiSearchByTypeReset.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                    searchTypeData = searchTypeValue[0];
                    binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    break;
                case 1:
                    searchTypeData = searchTypeValue[1];
                    binding.wrapSpinnerRoUID.setVisibility(View.VISIBLE);
                    searchView.setEnabled(false);
                    break;
                case 2:
                    searchTypeData = searchTypeValue[2];
                    binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    break;
                case 3:
                    searchTypeData = searchTypeValue[3];
                    binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    break;
                case 4:
                    searchTypeData = searchTypeValue[4];
                    binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                    searchView.setEnabled(true);
                    break;
                default:
                    break;
            }
        });

        binding.btnGiSearchByDateReset.setOnClickListener(view18 -> {
            resetSearchByDate();
            checkSelectedChipFilter();
        });

        binding.btnGiSearchByTypeReset.setOnClickListener(view19 -> resetSearchByType());

        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            dismissSelectedItem();

            if (checkedIds.contains(group.getCheckedChipId())){

                if (group.getCheckedChipId() == binding.chipFilterAll.getId()){
                    showDataDefaultQuery();
                }
                if (group.getCheckedChipId() == binding.chipFilterValid.getId()){
                    showDataSearchByApprovalStatus(true);
                }
                if (group.getCheckedChipId() == binding.chipFilterInvalid.getId()){
                    showDataSearchByApprovalStatus(false);
                }
                if (group.getCheckedChipId() == binding.chipFilterRcpTrue.getId()){
                    recappedStatusTrue();
                }
                if (group.getCheckedChipId() == binding.chipFilterRcpFalse.getId()){
                    recappedStatusFalse();
                }
                if (group.getCheckedChipId() == binding.chipFilterInvTrue.getId()){
                    invoicedTrue();
                }
                if (group.getCheckedChipId() == binding.chipFilterInvFalse.getId()){
                    invoicedFalse();
                }

                if (group.getCheckedChipId() == binding.chipFilterCOTrue.getId()){
                    cashOutStatusTrue();
                }
                if (group.getCheckedChipId() == binding.chipFilterCOFalse.getId()){
                    cashOutStatusFalse();
                }

                if (group.getCheckedChipId() == binding.chipFilterCurah.getId()){
                    showDataSearchByMaterialType("CURAH");
                }
                if (group.getCheckedChipId() == binding.chipFilterBorong.getId()){
                    showDataSearchByMaterialType("BORONG");
                }
            } else {

                showDataDefaultQuery();

            }
        });

        // Init chip item text
        String initWord1 = "SEMUA (";
        String initWord2 = "VALID (";
        String initWord3 = "BELUM VALID (";
        String initWord4 = "SUDAH DIAJUKAN (";
        String initWord5 = "BELUM DIAJUKAN (";
        String initWord6 = "SUDAH DIREKAP (";
        String initWord7 = "BELUM DIREKAP (";
        String initWord8 = "SUDAH DITAGIH (";
        String initWord9 = "BELUM DITAGIH (";
        String initWord10 = "CURAH (";
        String initWord11 = "BORONG (";
        String closingWord = ")";

        // Special ArrayList for filter
        ArrayList<GiModel> arrayGICOTrue = new ArrayList<>();
        ArrayList<GiModel> arrayGICOFalse = new ArrayList<>();
        ArrayList<GiModel> arrayGIRcpTrue = new ArrayList<>();
        ArrayList<GiModel> arrayGIRcpFalse = new ArrayList<>();
        ArrayList<GiModel> arrayGIInvTrue = new ArrayList<>();
        ArrayList<GiModel> arrayGIInvFalse = new ArrayList<>();

        // Handle item count based on categories
        /*new Handler(Looper.getMainLooper()).postDelayed(() -> {

                    // Load Chip All Data
                    velAll = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int val = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            binding.chipFilterAll.setText(initWord1.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Valid Data
                    velValid = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int val = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            binding.chipFilterValid.setText(initWord2.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Invalid Data
                    velInvalid = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int val = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            binding.chipFilterInvalid.setText(initWord3.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Cashed Out True Data
                    velCOTrue = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                if (!Objects.requireNonNull(goodIssueModel).getGiCashedOutTo().equals("")) {
                                    arrayGICOTrue.add(goodIssueModel);
                                }
                            }
                            int val = arrayGICOTrue.size();
                            binding.chipFilterCOTrue.setText(initWord4.concat(String.valueOf(val)).concat(closingWord));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Cashed Out False Data
                    velCOFalse = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                if (Objects.requireNonNull(goodIssueModel).getGiCashedOutTo().equals("")) {
                                    arrayGICOFalse.add(goodIssueModel);
                                }
                            }
                            int val = arrayGICOFalse.size();
                            binding.chipFilterCOFalse.setText(initWord5.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Recapped True Data
                    velRcpTrue = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                if (!Objects.requireNonNull(goodIssueModel).getGiRecappedTo().equals("")) {
                                    arrayGIRcpTrue.add(goodIssueModel);
                                }
                            }
                            int val = arrayGIRcpTrue.size();
                            binding.chipFilterRcpTrue.setText(initWord6.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Recapped False Data
                    velRcpFalse = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                if (Objects.requireNonNull(goodIssueModel).getGiRecappedTo().equals("")) {
                                    arrayGIRcpFalse.add(goodIssueModel);
                                }
                            }
                            int val = arrayGIRcpFalse.size();
                            binding.chipFilterRcpFalse.setText(initWord7.concat(String.valueOf(val)).concat(closingWord));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Invoiced True Data
                    velInvTrue = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                if (!Objects.requireNonNull(goodIssueModel).getGiInvoicedTo().equals("")) {
                                    arrayGIInvTrue.add(goodIssueModel);
                                }
                            }
                            int val = arrayGIInvTrue.size();
                            binding.chipFilterInvTrue.setText(initWord8.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Invoiced False Data
                    velInvFalse = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot item : dataSnapshot.getChildren()) {
                                GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                                if (Objects.requireNonNull(goodIssueModel).getGiInvoicedTo().equals("")) {
                                    arrayGIInvFalse.add(goodIssueModel);
                                }
                            }
                            int val = arrayGIInvFalse.size();
                            binding.chipFilterInvFalse.setText(initWord9.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Curah Data
                    velCurah = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int val = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            binding.chipFilterCurah.setText(initWord10.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // Load Chip Borong Data
                    velBorong = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int val = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            binding.chipFilterBorong.setText(initWord11.concat(String.valueOf(val).concat(closingWord)));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };

                    // DB REFERENCE ADD VEL
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velAll);
                    databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(true).addListenerForSingleValueEvent();(velValid);
                    databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(false).addListenerForSingleValueEvent();(velInvalid);
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velCOTrue);
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velCOFalse);
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velRcpTrue);
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velRcpFalse);
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velInvTrue);
                    databaseReference.child("GoodIssueData").addListenerForSingleValueEvent();(velInvFalse);
                    databaseReference.child("GoodIssueData").orderByChild("giMatType").equalTo("CURAH").addListenerForSingleValueEvent();(velCurah);
                    databaseReference.child("GoodIssueData").orderByChild("giMatType").equalTo("BORONG").addListenerForSingleValueEvent();(velBorong);

                    pd.dismiss();

                }, // Delay load every 3 seconds
                3000);*/
    }

    public static class GIAdapter extends RecyclerView.Adapter<GIAdapter.GIHolder>{

        Context c;
        ArrayList<GiModel> giModelArrayList;
        ArrayList<GiModel> checkedGoodIssue = new ArrayList<>();

        GIAdapter giAdapter;
        String custDocumentID;

        public boolean isSelectedAll = false;
        ActivityDataGiBinding binding;
        ProgressDialog pd;

        String coAccBy;

        public GIAdapter(Context c, ArrayList<GiModel> giModelArrayList, GIAdapter giAdapter, ActivityDataGiBinding binding){
            this.c = c;
            this.giModelArrayList = giModelArrayList;
            this.giAdapter = giAdapter;
            this.binding = binding;
        }

        @NonNull
        @Override
        public GIHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_good_issue_desktop, null);
            GIHolder giHolder = new GIHolder(v);
            return giHolder;
        }

        public Activity getActivity(Context context) {
            if (context == null) {
                return null;
            } else if (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                else {
                    return getActivity(((ContextWrapper) context).getBaseContext());
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull GIHolder holder, int pos) {
            final GiModel giModel = giModelArrayList.get(pos);
            pd = new ProgressDialog(c);
            pd.setCancelable(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            HelperUtils helperUtils = new HelperUtils();

            DatabaseReference databaseReferenceGI = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference2GI = databaseReferenceGI.child("GoodIssueData");

            HashMap<String, Object> stringHashMap = new HashMap<>();
            stringHashMap.put("giStatus", true);
            stringHashMap.put("giVerifiedBy", helperUtils.getUserId());

            DecimalFormat df = new DecimalFormat("0.00");
            Double cubication = giModel.getGiVhlCubication();
            String dateNTime = giModel.getGiDateCreated()+" "+ giModel.getGiTimeCreted();

            String[] partGiUID = giModel.getGiUID().split("-");
            String giUID = partGiUID[0];

            String roDocumentID = giModel.getRoDocumentID();

            String matDetail = giModel.getGiMatType()+" - "+ giModel.getGiMatName();
            String vhlDetail = "(P) "+ giModel.getVhlLength().toString()+" (L) "+ giModel.getVhlWidth().toString()+" (T) "+ giModel.getVhlHeight().toString()+" | "+"(K) "+ giModel.getVhlHeightCorrection().toString()+" (TK) "+ giModel.getVhlHeightAfterCorrection().toString();
            String vhlUID = giModel.getVhlUID();
            boolean giStatus = giModel.getGiStatus();
            String giRecappedTo = giModel.getGiRecappedTo();
            String giInvoicedTo = giModel.getGiInvoicedTo();
            String giCashedOutTo = giModel.getGiCashedOutTo();

            holder.tvCubication.setText(Html.fromHtml(df.format(cubication) +" m\u00B3"));
            holder.tvGiDateTime.setText(dateNTime);
            holder.tvGiUid.setText(giUID);
            holder.tvGiMatDetail.setText(matDetail);
            holder.tvGiVhlDetail.setText(vhlDetail);
            holder.tvVhlUid.setText(vhlUID);

            FirebaseFirestore db1 = FirebaseFirestore.getInstance();
            db1.collection("ReceivedOrderData").whereEqualTo("roDocumentID", roDocumentID)
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            if (!value.isEmpty()) {
                                for (DocumentSnapshot d : value.getDocuments()) {
                                    RoModel roModel = d.toObject(RoModel.class);

                                    assert roModel != null;
                                    String poNumber = roModel.getRoPoCustNumber();
                                    custDocumentID = roModel.getCustDocumentID();

                                    holder.tvPoCustNumber.setText("PO: " + poNumber);

                                    db1.collection("CustomerData").whereEqualTo("custDocumentID", custDocumentID)
                                            .addSnapshotListener((value2, error2) -> {
                                                if (value2 != null) {
                                                    if (!value2.isEmpty()) {
                                                        for (DocumentSnapshot e : value2.getDocuments()) {
                                                            CustModel custModel = e.toObject(CustModel.class);
                                                            String customerName = custModel.getCustName();
                                                            String customerAlias = custModel.getCustUID();

                                                            holder.tvCustomerName.setText(customerAlias+" - "+customerName);
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });

            if (giStatus){
                holder.llStatusApproved.setVisibility(View.VISIBLE);
                holder.btnApproveGi.setVisibility(View.GONE);
            } else {
                holder.llStatusApproved.setVisibility(View.GONE);
                holder.btnApproveGi.setVisibility(View.VISIBLE);
            }

            if (!giRecappedTo.isEmpty()){
                holder.llStatusRecapped.setVisibility(View.VISIBLE);
            } else {
                holder.llStatusRecapped.setVisibility(View.GONE);
            }

            if (!giInvoicedTo.isEmpty()){
                holder.llStatusInvoiced.setVisibility(View.VISIBLE);
            } else {
                holder.llStatusInvoiced.setVisibility(View.GONE);
            }

            if (!giCashedOutTo.isEmpty()){
                holder.llCashedOutStatus.setVisibility(View.VISIBLE);
            } else {
                holder.llCashedOutStatus.setVisibility(View.GONE);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("CashOutData").whereEqualTo("coDocumentID", giCashedOutTo).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    CoModel coModel = documentSnapshot.toObject(CoModel.class);
                                    coModel.setCoDocumentID(documentSnapshot.getId());
                                    coAccBy = coModel.getCoAccBy();
                                    if (!coAccBy.isEmpty()){
                                        holder.llCashedOutStatus.setBackground(ContextCompat.getDrawable(c, R.drawable.pill_green));
                                    } else {
                                        holder.llCashedOutStatus.setBackground(ContextCompat.getDrawable(c, R.drawable.pill_red));
                                    }
                                }
                            }
                    );

            holder.btn3.setOnClickListener(view -> {
                String giUID1 = giModel.getGiUID();
                Intent i = new Intent(c, UpdtGiActivity.class);
                i.putExtra("key", giUID1);
                c.startActivity(i);
            });

            holder.btn2.setOnClickListener(view -> {
                if (holder.tvPoCustNumber.getText().toString().equals("PO: -")){
                    Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }
                    MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                            .setAnimation(R.raw.lottie_attention)
                            .setTitle("Perhatian!", TextAlignment.START)
                            .setMessage("Data Good Issue ini masih belum memiliki nomor PO. Mohon perbarui data tersebut agar dapat melakukan validasi dan dapat muncul saat direkapitulasi.", TextAlignment.START)
                            .setPositiveButton("OKE", (dialogInterface, which) -> dialogInterface.dismiss())
                            .setCancelable(true)
                            .build();
                    md.show();
                } else {
                    Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100,
                                VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }

                    MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                            .setAnimation(R.raw.lottie_approval)
                            .setTitle("Validasi Data")
                            .setMessage("Apakah Anda yakin ingin mengesahkan data Good Issue yang Anda pilih? Setelah disahkan, status tidak dapat dikembalikan.")
                            .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("GoodIssueData").child(giModel.getGiUID()).child("giStatus").setValue(true);
                                databaseReference.child("GoodIssueData").child(giModel.getGiUID()).child("giVerifiedBy").setValue(helperUtils.getUserId());
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                            .setCancelable(true)
                            .build();

                    md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    md.show();
                }
            });

            holder.btn1.setOnClickListener(view -> {
                Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100,
                            VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }

                MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                        .setTitle("Hapus Data")
                        .setAnimation(R.raw.lottie_delete)
                        .setMessage("Apakah Anda yakin ingin menghapus data Good Issue yang Anda pilih? Setelah dihapus, data tidak dapat dikembalikan.")
                        .setCancelable(true)
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("GoodIssueData").child(giModel.getGiUID()).removeValue();
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            });

            holder.setItemClickListener(new GIHolder.ItemClickListener(){
                @Override
                public void onItemClick(View v, int pos){
                    CheckBox cbSelectItem = (CheckBox) v;
                    final GiModel currentGoodIssue = giModelArrayList.get(pos);

                    if (cbSelectItem.isChecked()){
                        giModelArrayList.get(pos).setChecked(true);
                        checkedGoodIssue.add(currentGoodIssue);
                    }else if (!cbSelectItem.isChecked()){
                        giModelArrayList.get(pos).setChecked(false);
                        checkedGoodIssue.remove(currentGoodIssue);
                    }

                    int itemSelectedSize = giAdapter.getSelected().size();
                    float itemSelectedVolume = giAdapter.getSelectedItemVolume();

                    String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");

                    if (giAdapter.getSelected().size() > 0) {
                        binding.fabExpandMenu.animate().translationY(800).setDuration(100).start();
                        binding.fabExpandMenu.collapse();

                        binding.tvTotalSelectedItem.setText(itemSelectedSize + " item terpilih");
                        binding.tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);

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
                        binding.fabExpandMenu.animate().translationY(0).setDuration(100).start();

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


                }
            });

            holder.cbSelectItem.setChecked(giModelArrayList.get(pos).isChecked());

            if (giModelArrayList.size()<0){
                exitSelection(binding.fabExpandMenu, binding.llBottomSelectionOptions, binding.btnSelectAll);
            }

            binding.btnSelectAll.setOnClickListener(v -> {
                if(!isSelectedAll){
                    giAdapter.selectAll();
                    binding.btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_deselect));
                    isSelectedAll = true;
                }else{
                    isSelectedAll = false;
                    giAdapter.clearSelection();
                    binding.btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_select_all));
                }

                int itemSelectedSize = giAdapter.getSelected().size();
                float itemSelectedVolume = giAdapter.getSelectedItemVolume();

                String itemSelectedVolumeAndBuyPriceVal = df.format(itemSelectedVolume).concat(" m3");

                binding.tvTotalSelectedItem.setText(itemSelectedSize + " item terpilih");
                binding.tvTotalSelectedItem2.setText(itemSelectedVolumeAndBuyPriceVal);

                if (itemSelectedSize == 0){
                    exitSelection(binding.fabExpandMenu, binding.llBottomSelectionOptions, binding.btnSelectAll);
                }

                notifyDataSetChanged();
            });
            binding.btnDeleteSelected.setOnClickListener(view -> {
                int size = giAdapter.getSelected().size();
                MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                        .setTitle("Hapus Data Terpilih")
                        .setAnimation(R.raw.lottie_delete)
                        .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Good Issue yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                        .setCancelable(true)
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            for (int i = 0; i < giAdapter.getSelected().size(); i++) {
                                databaseReference.child("GoodIssueData").child(giAdapter.getSelected().get(i).getGiUID()).removeValue();
                            }

                            exitSelection(binding.fabExpandMenu, binding.llBottomSelectionOptions, binding.btnSelectAll);

                            binding.fabExpandMenu.animate().translationY(0).setDuration(100).start();
                            binding.btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_select_all));
                            binding.llBottomSelectionOptions.animate()
                                    .translationY(binding.llBottomSelectionOptions.getHeight()).alpha(0.0f)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            binding.llBottomSelectionOptions.setVisibility(View.GONE);
                                        }
                                    });

                            notifyDataSetChanged();

                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();

                notifyDataSetChanged();
            });
            binding.btnVerifySelected.setOnClickListener(view -> {
                int size = giAdapter.getSelected().size();
                MaterialDialog md = new MaterialDialog.Builder(getActivity(c))
                        .setAnimation(R.raw.lottie_approval)
                        .setTitle("Validasi Data Terpilih")
                        .setMessage("Apakah Anda yakin ingin mengesahkan "+size+" data Good Issue yang terpilih? Setelah disahkan, status tidak dapat dikembalikan.")
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            dialogInterface.dismiss();

                            for (int i = 0; i < giAdapter.getSelected().size(); i++) {
                                databaseReference2GI.child(giAdapter.getSelected().get(i).getGiUID()).updateChildren(stringHashMap);
                            }

                            exitSelection(binding.fabExpandMenu, binding.llBottomSelectionOptions, binding.btnSelectAll);

                            binding.fabExpandMenu.animate().translationY(0).setDuration(100).start();
                            binding.btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_select_all));
                            binding.llBottomSelectionOptions.animate()
                                    .translationY(binding.llBottomSelectionOptions.getHeight()).alpha(0.0f)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            binding.llBottomSelectionOptions.setVisibility(View.GONE);
                                        }
                                    });

                            notifyDataSetChanged();
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .setCancelable(true)
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            });
            binding.btnExitSelection.setOnClickListener(view -> exitSelection(binding.fabExpandMenu, binding.llBottomSelectionOptions, binding.btnSelectAll));

        }

        @Override
        public int getItemCount() {
            return giModelArrayList.size();
        }

        static class GIHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            LinearLayout llStatusApproved, llStatusRecapped, llStatusInvoiced, llCashedOutStatus, llRoNeedsUpdate, llHiddenView, llWrapGiStatus;
            TextView tvCubication, tvGiDateTime, tvGiUid, tvRoUid, tvGiMatDetail, tvGiVhlDetail,
                    tvVhlUid, tvPoCustNumber, tvCustomerName;
            RelativeLayout btnDeleteGi, btnApproveGi, btnCloneGi;
            Button btn1, btn2, btn3, btn4;
            ConstraintLayout cardView;
            CheckBox cbSelectItem;

            ItemClickListener itemClickListener;

            public GIHolder(@NonNull View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.cdvItem);
                llHiddenView = itemView.findViewById(R.id.llHiddenView);
                llWrapGiStatus = itemView.findViewById(R.id.llWrapItemStatus);
                llStatusApproved = itemView.findViewById(R.id.llStatusApproved);
                llCashedOutStatus = itemView.findViewById(R.id.llCashedOutStatus);
                llStatusRecapped = itemView.findViewById(R.id.llStatusRecapped);
                llStatusInvoiced = itemView.findViewById(R.id.ll_status_invoiced);
                llRoNeedsUpdate = itemView.findViewById(R.id.ll_ro_needs_update);
                tvCubication = itemView.findViewById(R.id.tv_cubication);
                tvGiDateTime = itemView.findViewById(R.id.tvDateCreated);
                tvGiUid = itemView.findViewById(R.id.tv_gi_uid);
                tvRoUid = itemView.findViewById(R.id.tvCoTotal);
                tvPoCustNumber = itemView.findViewById(R.id.tv_po_cust_number);
                tvGiMatDetail = itemView.findViewById(R.id.tv_gi_mat_detail);
                tvGiVhlDetail = itemView.findViewById(R.id.tv_gi_vhl_detail);
                tvVhlUid = itemView.findViewById(R.id.tv_vhl_uid);
                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                btnDeleteGi = itemView.findViewById(R.id.btn_delete_gi);
                btnApproveGi = itemView.findViewById(R.id.btn_approve_gi);
                btnCloneGi = itemView.findViewById(R.id.btn_clone_gi);
                btn1 = itemView.findViewById(R.id.btnDeleteItem);
                btn2 = itemView.findViewById(R.id.btnApproveItem);
                btn3 = itemView.findViewById(R.id.btnOpenItemDetail);
                btn4 = itemView.findViewById(R.id.btnClone);
                cbSelectItem = itemView.findViewById(R.id.cbSelectItem);

                cbSelectItem.setOnClickListener(this);
            }

            public void setItemClickListener(ItemClickListener ic){
                this.itemClickListener = ic;
            }

            @Override
            public void onClick(View v) {
                this.itemClickListener.onItemClick(v,getLayoutPosition());
            }

            interface ItemClickListener{
                void onItemClick(View v, int pos);
            }
        }

        public void exitSelection(FloatingActionsMenu fabExpandMenu, LinearLayout llBottomSelectionOptions, ImageButton btnSelectAll) {
            giAdapter.clearSelection();
            isSelectedAll = false;
            fabExpandMenu.animate().translationY(0).setDuration(100).start();
            btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(c, R.drawable.ic_outline_select_all));

            llBottomSelectionOptions.animate()
                    .translationY(llBottomSelectionOptions.getHeight()).alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            llBottomSelectionOptions.setVisibility(View.GONE);
                        }
                    });

            notifyDataSetChanged();
        }

        public void selectAll(){
            isSelectedAll=true;
            for (int i = 0; i < giModelArrayList.size(); i++) {
                giModelArrayList.get(i).setChecked(true);
            }
            notifyDataSetChanged();
        }

        public void clearSelection() {
            isSelectedAll=false;
            for (int i = 0; i < giModelArrayList.size(); i++) {
                giModelArrayList.get(i).setChecked(false);
            }
            notifyDataSetChanged();
        }

        public ArrayList<GiModel> getSelected() {
            ArrayList<GiModel> selected = new ArrayList<>();
            for (int i = 0; i < giModelArrayList.size(); i++) {
                if (giModelArrayList.get(i).isChecked()) {
                    selected.add(giModelArrayList.get(i));
                }
            }
            return selected;
        }

        public float getSelectedItemVolume() {
            float selected = 0;
            for (int i = 0; i < giModelArrayList.size(); i++) {
                if (giModelArrayList.get(i).isChecked()) {
                    selected += giModelArrayList.get(i).getGiVhlCubication();
                    if (!giModelArrayList.get(i).isChecked()){
                        selected -= giModelArrayList.get(i).getGiVhlCubication();
                    }
                }
            }
            return selected;
        }
    }

    // Load data if chip selected
    private void checkSelectedChipFilter() {
        if (binding.chipFilterAll.isChecked()){
            showDataDefaultQuery();
        }
        if (binding.chipFilterValid.isChecked()){
            showDataSearchByApprovalStatus(true);
        }
        if (binding.chipFilterInvalid.isChecked()){
            showDataSearchByApprovalStatus(false);
        }
        if (binding.chipFilterInvTrue.isChecked()){
            invoicedTrue();
        }
        if (binding.chipFilterInvFalse.isChecked()){
            invoicedFalse();
        }
        if (binding.chipFilterCurah.isChecked()){
            showDataSearchByMaterialType("CURAH");
        }
        if (binding.chipFilterBorong.isChecked()){
            showDataSearchByMaterialType("BORONG");
        }
    }

    private void resetSearchByType() {
        binding.spinnerSearchType.setText("");
        binding.spinnerSearchType.clearFocus();
        binding.btnGiSearchByTypeReset.setVisibility(View.GONE);
    }

    private void resetSearchByDate() {
        binding.btnGiSearchByDateReset.setVisibility(View.GONE);
        binding.edtGiDateFilterStart.setText(null);
        binding.edtGiDateFilterEnd.setText(null);
        dateStart = "";
        dateEnd = "";
    }

    private void showDataSearchByType(String newText, String searchTypeData) {
        pd.setMessage("Memproses ...");
        pd.show();

        if (searchTypeData.equals(searchTypeValue[1])){
            dialogInterfaceUtils.cannotFillHere(GiDataActivity.this, searchView);
            searchView.setEnabled(false);
        }

        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        if (!searchTypeData.equals(searchTypeValue[1])){
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pd.dismiss();
                    giModelArrayList.clear();
                    if (snapshot.exists()){
                        //nestedScrollView.setVisibility(View.VISIBLE);
                        binding.llNoData.setVisibility(View.GONE);
                        for (DataSnapshot item : snapshot.getChildren()) {
                            GiModel giModel = item.getValue(GiModel.class);

                            if (searchTypeData.equals(searchTypeValue[0])){
                                if(Objects.requireNonNull(giModel).getGiUID().contains(newText)) {
                                    giModelArrayList.add(giModel);
                                }
                                binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                            }

                            if (searchTypeData.equals(searchTypeValue[3])){
                                if(Objects.requireNonNull(giModel).getVhlUID().contains(newText)) {
                                    giModelArrayList.add(giModel);
                                }
                                binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                            }

                            if (searchTypeData.equals(searchTypeValue[4])){
                                if(Objects.requireNonNull(giModel).getGiMatName().contains(newText)) {
                                    giModelArrayList.add(giModel);
                                }
                                binding.wrapSpinnerRoUID.setVisibility(View.GONE);
                            }
                        }
                        Collections.reverse(giModelArrayList);
                    } else  {
                        binding.llNoData.setVisibility(View.VISIBLE);
                    }
                    giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                    binding.recyclerView.setAdapter(giAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void showDataSearchByApprovalStatus(boolean b) {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                approvalStatusOnDataChange(snapshot, b);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void approvalStatusOnDataChange(DataSnapshot snapshot, boolean b) {
        giModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GiModel giModel = item.getValue(GiModel.class);
                if (Objects.equals(item.child("giStatus").getValue(), b)) {
                    giModelArrayList.add(giModel);
                    binding.llNoData.setVisibility(View.GONE);
                }
            }
        } else  {
            binding.llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(giModelArrayList);
        giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
        binding.recyclerView.setAdapter(giAdapter);

        if (giModelArrayList.size()<1){
            binding.llNoData.setVisibility(View.VISIBLE);
        }

    }

    private void recappedStatusTrue() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GiModel giModel = item.getValue(GiModel.class);
                        if (!Objects.equals(item.child("giRecappedTo").getValue(), "")) {
                            giModelArrayList.add(giModel);
                            binding.llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                binding.recyclerView.setAdapter(giAdapter);

                if (giModelArrayList.size()<1){
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recappedStatusFalse() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GiModel giModel = item.getValue(GiModel.class);
                        if (Objects.equals(item.child("giRecappedTo").getValue(), "")) {
                            giModelArrayList.add(giModel);
                            binding.llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                binding.recyclerView.setAdapter(giAdapter);

                if (giModelArrayList.size()<1){
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cashOutStatusTrue() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GiModel giModel = item.getValue(GiModel.class);
                        if (!Objects.equals(item.child("giCashedOutTo").getValue(), "")) {
                            giModelArrayList.add(giModel);
                            binding.llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                binding.recyclerView.setAdapter(giAdapter);

                if (giModelArrayList.size()<1){
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cashOutStatusFalse() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GiModel giModel = item.getValue(GiModel.class);
                        if (Objects.equals(item.child("giCashedOutTo").getValue(), "")) {
                            giModelArrayList.add(giModel);
                            binding.llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                binding.recyclerView.setAdapter(giAdapter);

                if (giModelArrayList.size()<1){
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void invoicedTrue() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GiModel giModel = item.getValue(GiModel.class);
                        if (!Objects.equals(item.child("giInvoicedTo").getValue(), "")) {
                            giModelArrayList.add(giModel);
                            binding.llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                binding.recyclerView.setAdapter(giAdapter);

                if (giModelArrayList.size()<1){
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void invoicedFalse() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot item : snapshot.getChildren()) {
                        GiModel giModel = item.getValue(GiModel.class);
                        if (Objects.equals(item.child("giInvoicedTo").getValue(), "")) {
                            giModelArrayList.add(giModel);
                            binding.llNoData.setVisibility(View.GONE);
                        }
                    }
                } else  {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
                Collections.reverse(giModelArrayList);
                giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
                binding.recyclerView.setAdapter(giAdapter);

                if (giModelArrayList.size()<1){
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDataSearchByMaterialType(String data) {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                materialTypeOnDataChange(snapshot, data);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void materialTypeOnDataChange(DataSnapshot snapshot, String data) {
        giModelArrayList.clear();
        if (snapshot.exists()){
            for (DataSnapshot item : snapshot.getChildren()) {
                GiModel giModel = item.getValue(GiModel.class);
                if (Objects.equals(item.child("giInvoiced").getValue(), false)) {
                    if (Objects.equals(item.child("giMatType").getValue(), data)) {
                        giModelArrayList.add(giModel);
                        binding.llNoData.setVisibility(View.GONE);
                    }
                }
            }
        } else  {
            binding.llNoData.setVisibility(View.VISIBLE);
        }
        Collections.reverse(giModelArrayList);
        giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
        binding.recyclerView.setAdapter(giAdapter);

        if (giModelArrayList.size()<1){
            binding.llNoData.setVisibility(View.VISIBLE);
        }

    }

    // Dismiss selected item
    public void dismissSelectedItem(){
        binding.fabExpandMenu.animate().translationY(0).setDuration(100).start();
        binding.btnSelectAll.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_outline_select_all));
        binding.llBottomSelectionOptions.animate()
                .translationY( binding.llBottomSelectionOptions.getHeight()).alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.llBottomSelectionOptions.setVisibility(View.GONE);
                    }
                });
    }

    // On create default query
    public void showDataDefaultQuery() {
        pd.setMessage("Memproses ...");
        pd.show();
        Query query = null;
        if (dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated");
        }
        if (!dateStart.isEmpty()&&dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart);
        }
        if (dateStart.isEmpty()&&!dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").endAt(dateEnd);
        }
        if (!dateStart.isEmpty() && !dateEnd.isEmpty()){
            query = databaseReference.child("GoodIssueData").orderByChild("giDateCreated").startAt(dateStart).endAt(dateEnd);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                giModelArrayList.clear();
                if (snapshot.exists()){
                    pd.dismiss();
                    for (DataSnapshot item : snapshot.getChildren()){
                        GiModel giModel = item.getValue(GiModel.class);
                        giModelArrayList.add(giModel);
                    }
                    binding.llNoData.setVisibility(View.GONE);
                    Collections.reverse(giModelArrayList);

                    giAdapter = new GiDataActivity.GIAdapter(context, giModelArrayList, giAdapter, binding);
                    binding.recyclerView.setAdapter(giAdapter);
                } else {
                    binding.llNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (giModelArrayList.size()<1){
            binding.llNoData.setVisibility(View.VISIBLE);
        }

    }

    // Set margin for the last item in RecyclerView
    public static class MemberItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            // only for the last one
            if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = 120/* set your margin here */;
            }

        }
    }

    // Create OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.query_search_menu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        };

        menu.findItem(R.id.search_data).setOnActionExpandListener(onActionExpandListener);
        searchView = (SearchView) menu.findItem(R.id.search_data).getActionView();
        searchView.setQueryHint("Kata Kunci");

        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(view -> {
            binding.llWrapFilterChipGroup.setVisibility(View.GONE);
            binding.wrapSearchBySpinner.setVisibility(View.VISIBLE);
            binding.cdvFilter.setVisibility(View.VISIBLE);
            TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());
            if (expandStatus){
                expandStatus=false;
                menu.findItem(R.id.filter_data).setIcon(R.drawable.ic_outline_filter_alt);
            }
        });

        searchView.setOnCloseListener(() -> {
            binding.llWrapFilterChipGroup.setVisibility(View.VISIBLE);
            binding.wrapSearchBySpinner.setVisibility(View.GONE);
            binding.btnResetSpinnerRoUID.setVisibility(View.GONE);
            binding.spinnerRoUID.setText(null);
            binding.wrapSpinnerRoUID.setVisibility(View.GONE);

            showDataDefaultQuery();

            TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //View viewLayout = ManageGoodIssueActivity.this.getCurrentFocus();
                if (binding.spinnerSearchType.getText().toString().isEmpty()){
                    dialogInterfaceUtils.fillSearchFilter(GiDataActivity.this, searchView);
                    /*if (!searchView.getQuery().toString().isEmpty()){
                        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //imm.hideSoftInputFromWindow(viewLayout.getWindowToken(), 0);
                    }*/
                } else {
                    showDataSearchByType(newText, searchTypeData);
                }
                return true;
            }
        });
        return true;
    }

    // OptionsMenu Item Selected
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_data) {
            if (expandStatus){
                expandStatus=false;
                binding.cdvFilter.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_outline_filter_alt);
            } else {
                expandStatus=true;
                binding.cdvFilter.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_outline_filter_alt_off);
            }
            TransitionManager.beginDelayedTransition(binding.cdvFilter, new AutoTransition());
            return true;
        } else if (item.getItemId() == R.id.menu_refresh){
            checkSelectedChipFilter();
            return true;
        }else if (item.getItemId() == R.id.menu_sort){
            if (sortStatus){
                sortStatus=false;
                item.setIcon(R.drawable.ic_sort_calendar_descending);
            } else {
                sortStatus=true;
                item.setIcon(R.drawable.ic_sort_calendar_ascending);
            }
            Collections.reverse(giModelArrayList);
            giAdapter = new GIAdapter(context, giModelArrayList, giAdapter, binding);
            binding.recyclerView.setAdapter(giAdapter);
            return true;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // HANDLE RESPONSIVE CONTENT
        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;*/

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.addItemDecoration(new MemberItemDecoration());
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(giAdapter);
    }

    @Override
    public void onBackPressed() {
        binding.recyclerView.setAdapter(null);
        giModelArrayList.clear();
        this.finish();
        super.onBackPressed();
    }

}