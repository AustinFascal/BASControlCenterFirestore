package com.ptbas.controlcenter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ptbas.controlcenter.create.AddInvoiceActivity;
import com.ptbas.controlcenter.create.AddProductData;
import com.ptbas.controlcenter.create.AddReceivedOrder;
import com.ptbas.controlcenter.create.AddVehicleActivity;
import com.ptbas.controlcenter.model.MainFeatureModel;
import com.ptbas.controlcenter.model.StatisticsModel;
import com.ptbas.controlcenter.model.UserModel;
import com.ptbas.controlcenter.userprofile.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeContainer;

    private static final int LAUNCH_SECOND_ACTIVITY = 0;


    CoordinatorLayout coordinatorLayout;
    ConstraintLayout bottomSheet;
    LinearLayout linearLayout, llAddGi, llShowOthers, llAddRo, llAddInvoice, llTopView, llWrapShortcuts, llWrapProfilePic;
    TextView title, tvShowAllShortcuts;
    RecyclerView rvMainFeatures, rvStatistics;
    NestedScrollView nestedscrollview;
    CardView crdviewWrapInternetError, crdviewWrapShortcuts;
    ImageView imageViewProfilePic;
    //ImageButton imgbtnMenu;

    FirebaseAuth authProfile;
    String finalCountMaterial, finalCountUser, finalCountActiveReceivedOrderData, finalCountActiveGoodIssueDataToInvoiced, finalCountActiveGoodIssueData, finalCountCustomer;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    MainFeaturesMenuAdapter mainFeaturesMenuAdapter;
    StatisticsAdapter statisticsAdapter;

    Helper helper = new Helper();

    BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        llWrapProfilePic = findViewById(R.id.wrap_profile_pic);
        bottomSheet = findViewById(R.id.bottomSheetPODetails);
        linearLayout = findViewById(R.id.linearLayoutWashboard);
        nestedscrollview = findViewById(R.id.nestedscrollview);
        llTopView = findViewById(R.id.ll_top_view);
        title = findViewById(R.id.title);
        //imgbtnMenu = findViewById(R.id.imgbtn_menu);
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
        llAddInvoice = findViewById(R.id.ll_add_invoice);

        // HANDLING SDK VERSION
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

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

        LinearLayout llAddVehicleFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_vehicle);
        LinearLayout llAddCustomerFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_customer);
        LinearLayout llAddMaterialFromBottomSheet = bottomSheet.findViewById(R.id.ll_add_material);
        ImageView ivExpandCollapseFromBottomSheet = bottomSheet.findViewById(R.id.iv_expand_collapse);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        ivExpandCollapseFromBottomSheet.setImageResource(R.drawable.ic_outline_keyboard_arrow_down);
                        ivExpandCollapseFromBottomSheet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        });
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_DRAGGING:
                        ivExpandCollapseFromBottomSheet.setImageResource(R.drawable.ic_outline_keyboard_arrow_up);
                        ivExpandCollapseFromBottomSheet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        });
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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

        title.setText(R.string.app_name);

        imageViewProfilePic.setOnClickListener(view -> {
            /*Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);*/

            // on image click we are opening new activity
            // and adding animation between this two activities.
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            // below method is used to make scene transition
            // and adding fade animation in it.
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    DashboardActivity.this, llWrapProfilePic, ViewCompat.getTransitionName(llWrapProfilePic));
            // starting our activity with below method.
            startActivity(intent, options.toBundle());
        });

        crdviewWrapShortcuts.setVisibility(View.VISIBLE);

        haveNetworkConnection();

        // FADE TRANSITION FOR DASHBOARD ACTIVITY
        Fade fade = new Fade();
        //View decor = getWindow().getDecorView();
        //fade.excludeTarget(decor.findViewById(androidx.appcompat.R.id.action_bar_container), true);
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
        llAddInvoice.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, AddInvoiceActivity.class);
            startActivity(intent);
        });
        llShowOthers.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        // HANDLER CHECK INTERNET CONNECTIVITY
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                haveNetworkConnection();
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();

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
        mainFeaturesMenuAdapter = new MainFeaturesMenuAdapter(dataQueue(),getApplicationContext());
        rvMainFeatures.setAdapter(mainFeaturesMenuAdapter);
        /*GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMainFeatures.setLayoutManager(gridLayoutManager);*/

        // REFRESH DASHBOARD'S CONTENTS
        swipeContainer.setOnRefreshListener(() -> helper.refreshDashboard(DashboardActivity.this));

        // SUM VEHICLE
        databaseReference.child("ProductData").addListenerForSingleValueEvent(new ValueEventListener() {
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
        databaseReference.child("RegisteredUser").addListenerForSingleValueEvent(new ValueEventListener() {
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
        databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
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
        databaseReference.child("GoodIssueData").orderByChild("giInvoiced").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getActivefinalCountActiveGoodIssueDataToInvoicedCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // SUM RECEIVED ORDER - NEED APPROVAL



        db.collection("ReceivedOrderData").whereEqualTo("roStatus", false)
                .addSnapshotListener((value, error) -> {
                    int countFinal = Integer.parseInt(String.valueOf(value.getDocuments().size()));
                    getActiveReceivedOrderDataCount(String.valueOf(countFinal));
                });
        /*databaseReference.child("ReceivedOrders").orderByChild("roStatus").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getActiveReceivedOrderDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        // SUM CUSTOMER
        databaseReference.child("CustomerData").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getCustomerDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // HAMBURGER MENU CLICK LISTENER
        /*imgbtnMenu.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(DashboardActivity.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_dashboard_option);
            dialog.show();
        });*/
    }


    private void getActiveReceivedOrderDataCount(String countFinal) {
        finalCountActiveReceivedOrderData = countFinal;
        showStatistics();
    }

    private void getActivefinalCountActiveGoodIssueDataToInvoicedCount(String countFinal) {
        finalCountActiveGoodIssueDataToInvoiced = countFinal;
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
/*
                    fullName = firebaseUser.getDisplayName();
                    doB = userModel.doB;
                    email = firebaseUser.getEmail();
                    gender = userModel.gender;
                    phone = userModel.phone;
                    accessCode = userModel.accessCode;

                    textViewWelcome.setText("Halo, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewGender.setText(gender);
                    textViewDoB.setText(doB);
                    textViewPhone.setText(phone);

                    switch (accessCode) {
                        case "000111":
                            textViewAccessCode.setText("Admin BAS");
                            llAdminBAS.setVisibility(View.VISIBLE);
                            llAdminWings.setVisibility(View.GONE);
                            llAdminSuper.setVisibility(View.GONE);
                            break;
                        case "111000":
                            textViewAccessCode.setText("Admin Wings");
                            llAdminBAS.setVisibility(View.GONE);
                            llAdminWings.setVisibility(View.VISIBLE);
                            llAdminSuper.setVisibility(View.GONE);
                            break;
                        case "111111":
                            textViewAccessCode.setText("Super Admin");
                            llAdminBAS.setVisibility(View.GONE);
                            llAdminWings.setVisibility(View.GONE);
                            llAdminSuper.setVisibility(View.VISIBLE);
                            break;
                        default:
                            textViewAccessCode.setText("Unknown");
                            break;
                    }
*/

                    Uri uri = firebaseUser.getPhotoUrl();
                    swipeContainer.setRefreshing(false);
                    Picasso.with(DashboardActivity.this).load(uri).into(imageViewProfilePic);

                } else {
                    Toast.makeText(DashboardActivity.this, "NULL", Toast.LENGTH_SHORT).show();
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
        mRO.setHeader("Manajemen Received Order");
        mRO.setImgName(R.drawable.ic_purchase_order);
        holder.add(mRO);

        MainFeatureModel mGI = new MainFeatureModel();
        mGI.setHeader("Manajemen Good Issue");
        mGI.setImgName(R.drawable.ic_good_issue);
        holder.add(mGI);

        MainFeatureModel mInv = new MainFeatureModel();
        mInv.setHeader("Manajemen Invoice");
        mInv.setImgName(R.drawable.ic_invoice);
        holder.add(mInv);

        MainFeatureModel mMat = new MainFeatureModel();
        mMat.setHeader("Manajemen Material");
        mMat.setImgName(R.drawable.ic_add_material);
        holder.add(mMat);

        MainFeatureModel mUsr = new MainFeatureModel();
        mUsr.setHeader("Manajemen Pengguna");
        mUsr.setImgName(R.drawable.ic_manage_user);
        holder.add(mUsr);

        MainFeatureModel mVhl = new MainFeatureModel();
        mVhl.setHeader("Manajemen Armada");
        mVhl.setImgName(R.drawable.ic_manage_vehicle);
        holder.add(mVhl);



        MainFeatureModel ob5 = new MainFeatureModel();
        ob5.setHeader("Manajemen Customer");
        //ob5.setDesc("Atur rincian data customer");
        ob5.setImgName(R.drawable.ic_manage_customers);
        holder.add(ob5);



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
        ob3.setHeader(finalCountActiveGoodIssueDataToInvoiced);
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

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        /*int paddingDp = 80;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingPixel = (int) (paddingDp * density);*/


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")){
                if (ni.isConnected()){
                    haveConnectedWifi = true;
                    //swipeContainer.setRefreshing(true);
                    //swipeContainer.setRefreshing(false);
                } else {

                }
            }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                    //swipeContainer.setRefreshing(true);
                    //swipeContainer.setRefreshing(false);
                } else {

                }
            }
            llWrapShortcuts.setVisibility(View.VISIBLE);

        }

        nestedscrollview.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (scrollY > 50) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                int nightModeFlags =
                        DashboardActivity.this.getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        llTopView.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.black));
                        llTopView.setElevation(20);
                        title.setTextColor(ContextCompat.getColor(DashboardActivity.this, R.color.white));
                        //imgbtnMenu.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(DashboardActivity.this, R.color.white)));

                        if (Build.VERSION.SDK_INT >= 21) {
                            //Window window = getWindow();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                            }
                            getWindow().setStatusBarColor(ContextCompat.getColor(DashboardActivity.this, R.color.black));// set status background white
                        }
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        llTopView.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.white));
                        llTopView.setElevation(20);
                        title.setTextColor(ContextCompat.getColor(DashboardActivity.this, R.color.black));
                        //imgbtnMenu.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(DashboardActivity.this, R.color.black)));

                        if (Build.VERSION.SDK_INT >= 21) {
                            //Window window = getWindow();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                            }
                            getWindow().setStatusBarColor(ContextCompat.getColor(DashboardActivity.this, R.color.white));// set status background white
                        }
                        break;
                }


            }
            if (scrollY < 300) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            if (scrollY < 50) {
                title.setTextColor(ContextCompat.getColor(DashboardActivity.this, R.color.white));
                //imgbtnMenu.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(DashboardActivity.this, R.color.white)));
                llTopView.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.transparent));
                llTopView.setElevation(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);//  set status text dark
                }
                getWindow().setStatusBarColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.transparent));// set status background white

            }

            /*if (scrollY == 0) {
                Toast.makeText(DashboardActivity.this, "reached top", Toast.LENGTH_SHORT).show();
            }

            if (scrollY == ( v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight() )) {
                Toast.makeText(DashboardActivity.this, "Bottom Scroll", Toast.LENGTH_SHORT).show();
            }*/
        });
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
                            .setTextColor(getResources().getColor(R.color.dark_green))
                            .setBackgroundTint(getResources().getColor(R.color.pure_green))
                            .setAction("TAMBAH LAGI", view1 -> {
                                Intent i = new Intent(this, AddReceivedOrder.class);
                                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);

                            })
                            .setActionTextColor(getResources().getColor(R.color.black))
                            .show();
                }
                //String result=data.getStringExtra("result");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        haveNetworkConnection();
    }
}