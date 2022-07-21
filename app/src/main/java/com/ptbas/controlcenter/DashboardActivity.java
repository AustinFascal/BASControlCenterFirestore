package com.ptbas.controlcenter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.adapter.MainFeaturesMenuAdapter;
import com.ptbas.controlcenter.adapter.StatisticsAdapter;
import com.ptbas.controlcenter.model.MainFeatureModel;
import com.ptbas.controlcenter.model.StatisticsModel;
import com.ptbas.controlcenter.model.UserModel;
import com.ptbas.controlcenter.userprofile.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout llAddGi, llAddVehicle, llAddPo, llAddInvoice, llTopView;
    private ImageView imageViewProfilePic;
    public FirebaseAuth authProfile;
    private String finalCountVehicle, finalCountUser, finalCountActiveGoodIssueData;

    private NestedScrollView nestedscrollview;
    private RelativeLayout linearLayout2;
    RecyclerView rvMainFeatures, rvStatistics;
    MainFeaturesMenuAdapter mainFeaturesMenuAdapter;
    StatisticsAdapter statisticsAdapter;

    private ConstraintLayout swipeContainer;
    private CardView crdviewWrapInternetError, crdviewWrapShortcuts;
    private LinearLayout llWrapShortcuts, llWrapNoInternet;

    private TextView title;
    private ImageButton imgbtnMenu;

    private Window window;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:


        nestedscrollview = findViewById(R.id.nestedscrollview);
        llTopView = findViewById(R.id.ll_top_view);

        title = findViewById(R.id.title);
        imgbtnMenu = findViewById(R.id.imgbtn_menu);

        title.setText(R.string.app_name);

        imageViewProfilePic = findViewById(R.id.imgbtn_profile);
        imageViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });

        //swipeContainer = findViewById(R.id.scroll_container_dashboard);
        crdviewWrapShortcuts = findViewById(R.id.crdview_wrap_shortcuts);
        llWrapShortcuts = findViewById(R.id.ll_wrap_shortcuts);
        llWrapNoInternet = findViewById(R.id.ll_wrap_no_internet);
        //crdviewWrapInternetError = findViewById(R.id.crdview_wrap_internet_error);

        crdviewWrapShortcuts.setVisibility(View.VISIBLE);
        //crdviewWrapInternetError.setVisibility(View.GONE);

        haveNetworkConnection();

        llAddGi =  findViewById(R.id.ll_add_gi);
        llAddVehicle = findViewById(R.id.ll_add_vehicle);
        llAddPo = findViewById(R.id.ll_add_po);
        llAddInvoice = findViewById(R.id.ll_add_invoice);

        llAddGi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AddGoodIssueActivity.class);
                startActivity(intent);
            }
        });

        llAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AddVehicleActivity.class);
                startActivity(intent);
            }
        });

        llAddPo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AddPurchaseOrderActivity.class);
                startActivity(intent);
            }
        });

        llAddInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                haveNetworkConnection();
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();

        rvMainFeatures = findViewById(R.id.rv_main_features);
        rvStatistics = findViewById(R.id.rv_statistics);

        mainFeaturesMenuAdapter = new MainFeaturesMenuAdapter(dataQueue(),getApplicationContext());
        rvMainFeatures.setAdapter(mainFeaturesMenuAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMainFeatures.setLayoutManager(gridLayoutManager);


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(this, "Terjadi kesalahan. Detail pengguna tidak tersedia saat ini.", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }

        databaseReference.child("VehicleData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getVehicleDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        databaseReference.child("GoodIssueData").orderByChild("giStatus").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countFinal = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                getActiveGoodIssueDataCount(String.valueOf(countFinal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getActiveGoodIssueDataCount(String countFinal) {
        finalCountActiveGoodIssueData = countFinal;
        showStatistics();
    }

    public void getVehicleDataCount(String countFinal){
        finalCountVehicle = countFinal;
        showStatistics();
    }

    public void getUserDataCount(String countFinal){
        finalCountUser = countFinal;
        showStatistics();
    }

    public void showStatistics(){
        statisticsAdapter = new StatisticsAdapter(dataQueueStatistic(),getApplicationContext());
        rvStatistics.setAdapter(statisticsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        rvStatistics.setLayoutManager(layoutManager);
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance("https://bas-delivery-report-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("RegisteredUser");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                //Toast.makeText(UserProfileActivity.this, userDetails.phone, Toast.LENGTH_SHORT).show();
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

        MainFeatureModel ob1 = new MainFeatureModel();
        ob1.setHeader("Manajemen Pengguna");
        ob1.setDesc("Atur detail data pengguna aplikasi");
        ob1.setImgName(R.drawable.ic_manage_user);
        holder.add(ob1);

        MainFeatureModel ob2 = new MainFeatureModel();
        ob2.setHeader("Manajemen Armada");
        ob2.setDesc("Atur detail data armada/kendaraan");
        ob2.setImgName(R.drawable.ic_manage_vehicle);
        holder.add(ob2);

        MainFeatureModel ob3 = new MainFeatureModel();
        ob3.setHeader("Manajemen Purchase Order");
        ob3.setDesc("Atur detail data purchase order");
        ob3.setImgName(R.drawable.ic_purchase_order);
        holder.add(ob3);

        MainFeatureModel ob4 = new MainFeatureModel();
        ob4.setHeader("Manajemen Good Issue");
        ob4.setDesc("Atur detail data good issue");
        ob4.setImgName(R.drawable.ic_good_issue);
        holder.add(ob4);

        MainFeatureModel ob5 = new MainFeatureModel();
        ob5.setHeader("Manajemen Invoice");
        ob5.setDesc("Atur data invoice transaksi");
        ob5.setImgName(R.drawable.ic_invoice);
        holder.add(ob5);

        return holder;
    }


    public ArrayList<StatisticsModel> dataQueueStatistic(){

        ArrayList<StatisticsModel> holder2 = new ArrayList<>();

        StatisticsModel ob1 = new StatisticsModel();
        ob1.setHeader("0");
        ob1.setDesc("Jumlah PO Aktif");
        holder2.add(ob1);

        /*StatisticsModel ob2 = new StatisticsModel();
        ob2.setHeader("0");
        ob2.setDesc("Jumlah PO Selesai");
        holder2.add(ob2);*/

        StatisticsModel ob3 = new StatisticsModel();
        ob3.setHeader(finalCountActiveGoodIssueData);
        ob3.setDesc("Jumlah GI Aktif");
        holder2.add(ob3);

        StatisticsModel ob4 = new StatisticsModel();
        ob4.setHeader(finalCountVehicle);
        ob4.setDesc("Jumlah Armada");
        holder2.add(ob4);

        StatisticsModel ob5 = new StatisticsModel();
        ob5.setHeader(finalCountUser);
        ob5.setDesc("Jumlah Pengguna");
        holder2.add(ob5);

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

    public void haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        int paddingDp = 80;
        float density = this.getResources().getDisplayMetrics().density;
        int paddingPixel = (int) (paddingDp * density);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            /*if (ni.getTypeName().equalsIgnoreCase("WIFI")){
                if (ni.isConnected()){
                    haveConnectedWifi = true;
                    llWrapShortcuts.setVisibility(View.VISIBLE);
                    llWrapNoInternet.setVisibility(View.GONE);
                } else {
                    llWrapShortcuts.setVisibility(View.GONE);
                    llWrapNoInternet.setVisibility(View.VISIBLE);
                }
            }


            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                    llWrapShortcuts.setVisibility(View.VISIBLE);
                    llWrapNoInternet.setVisibility(View.GONE);
                } else {
                    llWrapShortcuts.setVisibility(View.GONE);
                    llWrapNoInternet.setVisibility(View.VISIBLE);
                }
            }*/
            llWrapShortcuts.setVisibility(View.VISIBLE);
            llWrapNoInternet.setVisibility(View.GONE);

        }

        nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > 50) {
                    llTopView.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.white));
                    llTopView.setElevation(20);
                    title.setTextColor(ContextCompat.getColor(DashboardActivity.this, R.color.black));
                    imgbtnMenu.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(DashboardActivity.this, R.color.black)));

                    if (Build.VERSION.SDK_INT >= 21) {
                        //Window window = getWindow();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                        }
                        getWindow().setStatusBarColor(ContextCompat.getColor(DashboardActivity.this, R.color.white));// set status background white
                    }

                }
                if (scrollY < 50) {
                    llTopView.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.transparent));
                    llTopView.setElevation(0);
                    title.setTextColor(ContextCompat.getColor(DashboardActivity.this, R.color.white));
                    imgbtnMenu.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(DashboardActivity.this, R.color.white)));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);//  set status text dark
                    }
                    getWindow().setStatusBarColor(ContextCompat.getColor(DashboardActivity.this, android.R.color.transparent));// set status background white

                }

                if (scrollY == 0) {
                    Toast.makeText(DashboardActivity.this, "reached top", Toast.LENGTH_SHORT).show();
                }

                if (scrollY == ( v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight() )) {
                    Toast.makeText(DashboardActivity.this, "Bottom Scroll", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        haveNetworkConnection();
    }
}