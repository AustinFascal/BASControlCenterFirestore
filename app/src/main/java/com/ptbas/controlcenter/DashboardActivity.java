package com.ptbas.controlcenter;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.droidnet.DroidListener;
import com.droidnet.DroidNet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.ptbas.controlcenter.adapter.MainFeaturesMenuAdapter;
import com.ptbas.controlcenter.adapter.StatisticsAdapter;
import com.ptbas.controlcenter.create.AddCustomerActivity;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.create.AddCashOutActivity;
import com.ptbas.controlcenter.create.AddInvoiceActivity;
import com.ptbas.controlcenter.create.AddProductData;
import com.ptbas.controlcenter.create.AddRecapGoodIssueDataActivity;
import com.ptbas.controlcenter.create.AddReceivedOrder;
import com.ptbas.controlcenter.create.AddSupplierActivity;
import com.ptbas.controlcenter.create.AddVehicleActivity;
import com.ptbas.controlcenter.utility.Helper;
import com.ptbas.controlcenter.model.MainFeatureModel;
import com.ptbas.controlcenter.model.StatisticsModel;
import com.ptbas.controlcenter.model.UserModel;
import com.ptbas.controlcenter.userprofile.UserProfileActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class DashboardActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeContainer;

    private static final int LAUNCH_SECOND_ACTIVITY = 0;

    boolean doubleBackToExitPressedOnce = false;

    Context context;

    CoordinatorLayout coordinatorLayout;
    ConstraintLayout bottomSheet;
    LinearLayout linearLayout, llAddGi, llShowOthers, llAddRo, llAddBKK, llAddRecap, llAddInv, llTopView, llWrapShortcuts, llWrapProfilePic;
    TextView title, tvShowAllMenu;
    RecyclerView rvMainFeatures, rvStatistics;
    NestedScrollView nestedscrollview;
    CardView crdviewWrapShortcuts;
    ImageView imageViewProfilePic;

    FirebaseAuth authProfile;
    String finalCountMaterial, finalCountUser, finalCountActiveReceivedOrderData, finalCountActiveGoodIssueDataToBKK, finalCountActiveGoodIssueData, finalCountCustomer;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    MainFeaturesMenuAdapter mainFeaturesMenuAdapter;
    StatisticsAdapter statisticsAdapter;

    Helper helper = new Helper();

    BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

       /* DroidNet mDroidNet = DroidNet.getInstance();
        mDroidNet.addInternetConnectivityListener(this);*/

        context = this;

        llWrapProfilePic = findViewById(R.id.wrap_profile_pic);
        bottomSheet = findViewById(R.id.bottomSheetPODetails);
        linearLayout = findViewById(R.id.linearLayoutWashboard);
        nestedscrollview = findViewById(R.id.nestedscrollview);
        llTopView = findViewById(R.id.ll_top_view);
        title = findViewById(R.id.title);
        tvShowAllMenu = findViewById(R.id.tvShowAllMenu);
        imageViewProfilePic = findViewById(R.id.imgbtn_profile);
        swipeContainer = findViewById(R.id.swipeContainerDashboard);
        crdviewWrapShortcuts = findViewById(R.id.crdview_wrap_shortcuts);
        llWrapShortcuts = findViewById(R.id.ll_wrap_shortcuts);
        rvMainFeatures = findViewById(R.id.rv_main_features);
        rvStatistics = findViewById(R.id.rv_statistics);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        llAddGi =  findViewById(R.id.ll_add_gi);
        llShowOthers = findViewById(R.id.ll_show_other);
        llAddRo = findViewById(R.id.ll_add_po);
        llAddBKK = findViewById(R.id.ll_add_bkk);
        llAddRecap = findViewById(R.id.ll_add_recap);
        llAddInv = findViewById(R.id.ll_add_invoice);

        // HANDLING SDK VERSION
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        // HANDLING SYSTEM UI MODE
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                linearLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.dashboard_main_content_dark, null));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                linearLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.dashboard_main_content, null));
                break;
        }

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(DashboardActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // Toast.makeText(DashboardActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Jika Anda menolak perizinan yang dibutuhkan, Anda tidak dapat menggunakan layanan ini.\n\nSilakan aktifkan perizinan di [Pengaturan] > [Perizinan]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE, Manifest.permission.ACCESS_NETWORK_STATE)
                .check();

        // HANDLING USER ACCOUNT
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Terjadi kesalahan. Detail pengguna tidak tersedia saat ini.", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }

        // BOTTOM SHEET SECTION
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setFitToContents(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        int colorFrom = ContextCompat.getColor(this, R.color.transparent);
        int colorTo = ContextCompat.getColor(this, R.color.black_semi_transparent);
        ValueAnimator colorAnimation1 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
        colorAnimation1.setDuration(50); // milliseconds
        colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bottomSheet.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        colorAnimation2.setDuration(50); // milliseconds
        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bottomSheet.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });



        LinearLayout llAddVehicleFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_vehicle);
        LinearLayout llAddCustomerFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_customer);
        LinearLayout llAddMaterialFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_material);
        LinearLayout llAddSupplierFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_supplier);
        ImageView ivExpandCollapseFromBottomSheet = bottomSheet.findViewById(R.id.iv_expand_collapse);
        ConstraintLayout bottomSheetPODetails = bottomSheet.findViewById(R.id.bottomSheetPODetails);

        bottomSheetPODetails.setOnClickListener(view1 -> {
            ivExpandCollapseFromBottomSheet.setImageResource(R.drawable.ic_outline_keyboard_arrow_down);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            colorAnimation2.start();
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        colorAnimation1.start();
                        ivExpandCollapseFromBottomSheet.setImageResource(R.drawable.ic_outline_keyboard_arrow_down);
                        ivExpandCollapseFromBottomSheet.setOnClickListener(view -> {
                            colorAnimation2.start();
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        });
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        colorAnimation2.start();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        ivExpandCollapseFromBottomSheet.setImageResource(R.drawable.ic_outline_keyboard_arrow_up);
                        ivExpandCollapseFromBottomSheet.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tvShowAllMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AllManagementMenus.class);
                startActivity(intent);
            }
        });

        llAddVehicleFromBottomSheet.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddVehicleActivity.class);
            startActivity(intent);
        });

        llAddCustomerFromBottomSheet.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddCustomerActivity.class);
            startActivity(intent);
        });

        llAddMaterialFromBottomSheet.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddProductData.class);
            startActivity(intent);
        });

        llAddSupplierFromBottomSheet.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddSupplierActivity.class);
            startActivity(intent);
        });

        title.setText(R.string.app_name);

        imageViewProfilePic.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    DashboardActivity.this, llWrapProfilePic, Objects.requireNonNull(ViewCompat.getTransitionName(llWrapProfilePic)));
            startActivity(intent, options.toBundle());
        });

        crdviewWrapShortcuts.setVisibility(View.VISIBLE);

        //haveNetworkConnection();

        // FADE TRANSITION FOR DASHBOARD ACTIVITY
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        // DASHBOARD'S SHORTCUTS CLICK LISTENER
        llAddRo.setOnClickListener(view -> {
            Intent i = new Intent(this, AddReceivedOrder.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
        });
        llAddGi.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddGoodIssueActivity.class);
            startActivity(intent);
        });

        llAddRecap.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddRecapGoodIssueDataActivity.class);
            startActivity(intent);
        });
        llAddBKK.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddCashOutActivity.class);
            startActivity(intent);
        });
        llAddInv.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddInvoiceActivity.class);
            startActivity(intent);
        });
        llShowOthers.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));




        // ADAPTER FOR MAIN FEATURES
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            rvMainFeatures.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
            rvMainFeatures.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 6);
            rvMainFeatures.setLayoutManager(mLayoutManager);
        }
        mainFeaturesMenuAdapter = new MainFeaturesMenuAdapter(dataQueue(),getApplicationContext(), 1);
        rvMainFeatures.setAdapter(mainFeaturesMenuAdapter);

        // REFRESH DASHBOARD'S CONTENTS
        swipeContainer.setOnRefreshListener(() -> helper.refreshDashboard(DashboardActivity.this));

        // SUM VEHICLE
        databaseReference.child("ProductData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getMaterialDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // SUM REGISTERED USER
        databaseReference.child("RegisteredUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getUserDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // SUM GOOD ISSUE - NEED APPROVAL
        databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getActiveGoodIssueDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // SUM GOOD ISSUE - NEED INVOICED
        databaseReference.child("GoodIssueData").orderByChild("giInvoiced").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getActivefinalCountActiveGoodIssueDataToBKKCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // SUM RECEIVED ORDER - NEED APPROVAL
        db.collection("ReceivedOrderData").whereEqualTo("roStatus", false)
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    int countFinal = Integer.parseInt(String.valueOf(value.getDocuments().size()));
                    getActiveReceivedOrderDataCount(String.valueOf(countFinal));
                });
        // SUM CUSTOMER
        databaseReference.child("CustomerData").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getCustomerDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getActiveReceivedOrderDataCount(String countFinal) {
        finalCountActiveReceivedOrderData = countFinal;
        showStatistics();
    }

    private void getActivefinalCountActiveGoodIssueDataToBKKCount(String countFinal) {
        finalCountActiveGoodIssueDataToBKK = countFinal;
        showStatistics();
    }
    private void getActiveGoodIssueDataCount(String countFinal) {
        finalCountActiveGoodIssueData = countFinal;
        showStatistics();
    }

    public void getMaterialDataCount(String countFinal){
        finalCountMaterial = countFinal;
        showStatistics();
    }

    public void getUserDataCount(String countFinal){
        finalCountUser = countFinal;
        showStatistics();
    }
    public void getCustomerDataCount(String countFinal){
        finalCountCustomer = countFinal;
        showStatistics();
    }

    public void showStatistics(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        rvStatistics.setLayoutManager(layoutManager);
        statisticsAdapter = new StatisticsAdapter(dataQueueStatistic(), (Context) this);
        rvStatistics.setAdapter(statisticsAdapter);
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel != null){
                    if (firebaseUser.getPhotoUrl() != null){
                        Uri  uri = firebaseUser.getPhotoUrl();
                        swipeContainer.setRefreshing(false);
                        Picasso.with(DashboardActivity.this).load(uri).into(imageViewProfilePic);
                    } else{
                        imageViewProfilePic.setImageResource(R.drawable.default_user_profile);
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Gagal memuat data. Pastikan Anda terhubung dengan internet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<MainFeatureModel> dataQueue(){

        ArrayList<MainFeatureModel> holder = new ArrayList<>();

        MainFeatureModel mRO = new MainFeatureModel();
        mRO.setHeader("Data\nReceived Order");
        mRO.setImgName(R.drawable.ic_purchase_order);
        holder.add(mRO);

        MainFeatureModel mGI = new MainFeatureModel();
        mGI.setHeader("Data\nGood Issue");
        mGI.setImgName(R.drawable.ic_good_issue);
        holder.add(mGI);

        MainFeatureModel mRKPGI = new MainFeatureModel();
        mRKPGI.setHeader("Data Rekap\nGood Issue");
        mRKPGI.setImgName(R.drawable.ic_recap_good_issue);
        holder.add(mRKPGI);

        MainFeatureModel mCO = new MainFeatureModel();
        mCO.setHeader("Data\nCash Out");
        mCO.setImgName(R.drawable.ic_bkk);
        holder.add(mCO);

        MainFeatureModel mInv = new MainFeatureModel();
        mInv.setHeader("Data\nInvoice");
        mInv.setImgName(R.drawable.ic_invoice);
        holder.add(mInv);

        MainFeatureModel mAioRprt = new MainFeatureModel();
        mAioRprt.setHeader("Data Laporan\nAll-in-One");
        mAioRprt.setImgName(R.drawable.ic_aioreport);
        holder.add(mAioRprt);

        return holder;
    }


    public ArrayList<StatisticsModel> dataQueueStatistic(){

        ArrayList<StatisticsModel> holder2 = new ArrayList<>();

        StatisticsModel ob1 = new StatisticsModel();
        ob1.setHeader(finalCountActiveReceivedOrderData);
        ob1.setDesc("RO Perlu Divalidasi");
        holder2.add(ob1);

        StatisticsModel ob2 = new StatisticsModel();
        ob2.setHeader(finalCountActiveGoodIssueData);
        ob2.setDesc("GI Perlu Divalidasi");
        holder2.add(ob2);

        StatisticsModel ob3 = new StatisticsModel();
        ob3.setHeader(finalCountActiveGoodIssueDataToBKK);
        ob3.setDesc("GI Perlu Ditagihkan");
        holder2.add(ob3);

        StatisticsModel ob4 = new StatisticsModel();
        ob4.setHeader(finalCountMaterial);
        ob4.setDesc("Jumlah Material");
        holder2.add(ob4);

        StatisticsModel ob5 = new StatisticsModel();
        ob5.setHeader(finalCountUser);
        ob5.setDesc("Jumlah Pengguna");
        holder2.add(ob5);

        StatisticsModel ob6 = new StatisticsModel();
        ob6.setHeader(finalCountCustomer);
        ob6.setDesc("Jumlah Customer");
        holder2.add(ob6);

        StatisticsModel ob7 = new StatisticsModel();
        ob7.setHeader("0");
        ob7.setDesc("Jumlah Supplier");
        holder2.add(ob7);

        return holder2;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String returnString = data.getStringExtra("addedStatus");
                String activityType = data.getStringExtra("activityType");
                if (returnString.equals("true")&&activityType.equals("RO")){
                    Snackbar.make(coordinatorLayout, "Berhasil! Mau menambah data lagi?", Snackbar.LENGTH_LONG)
                            .setTextColor(ContextCompat.getColor(this, R.color.dark_green))
                            .setBackgroundTint(ContextCompat.getColor(this, R.color.pure_green))
                            .setAction("TAMBAH LAGI", view1 -> {
                                Intent i = new Intent(this, AddReceivedOrder.class);
                                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
                            })
                            .setActionTextColor(ContextCompat.getColor(this, R.color.black))
                            .show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan kembali sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}