package com.ptbas.controlcenter.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.DashboardActivity;
import com.ptbas.controlcenter.Helper;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.GIManagementAdapter;
import com.ptbas.controlcenter.create.AddGoodIssueActivity;
import com.ptbas.controlcenter.model.GoodIssueModel;
import com.ptbas.controlcenter.utils.LangUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

public class GoodIssueManagementActivity extends AppCompatActivity {

    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private ConstraintLayout bottomSheet;
    private NestedScrollView nestedScrollView;
    private RelativeLayout wrapExpandCollapse;
    private TextInputEditText edtGiDateFilterStart, edtGiDateFilterEnd;
    private ImageButton btnGiSearchByDate;
    DatePickerDialog datePicker;

    ArrayList<GoodIssueModel> goodIssueModelArrayList = new ArrayList<>();
    GIManagementAdapter giManagementAdapter;
    RecyclerView rvGoodIssueList;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Helper helper = new Helper();
    Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_issue_management);

        LangUtils.setLocale(this, "en");

        context = this;
        bottomSheet = findViewById(R.id.bottomSheetGIMenu);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setFitToContents(true);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        rvGoodIssueList = findViewById(R.id.rv_good_issue_list);
        wrapExpandCollapse = findViewById(R.id.wrap_expand_collapse);
        edtGiDateFilterStart = findViewById(R.id.edt_gi_date_filter_start);
        edtGiDateFilterEnd = findViewById(R.id.edt_gi_date_filter_end);
        btnGiSearchByDate = findViewById(R.id.btn_gi_search_by_date);
        showData();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Manajemen Good Issue");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.white)));

        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.black)));
                break;

            case Configuration.UI_MODE_NIGHT_NO:

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.white)));
                break;
        }

        rvGoodIssueList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch( View v, MotionEvent event ) {
                switch ( event.getAction( ) ) {
                    case MotionEvent.ACTION_SCROLL:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                }
                return false;
            }
        });



        edtGiDateFilterStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(GoodIssueManagementActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                edtGiDateFilterStart.setText(String.valueOf(dayOfMonth + "/" +(month + 1) + "/" + year));
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        edtGiDateFilterEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(GoodIssueManagementActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                edtGiDateFilterEnd.setText(String.valueOf(dayOfMonth + "/" +(month + 1) + "/" + year));
                            }
                        }, year, month, day);
                datePicker.show();
            }
        });

        btnGiSearchByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO MAKE FUNCTION
            }
        });

        LinearLayout llAddGi = bottomSheet.findViewById(R.id.ll_add_gi);

        llAddGi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodIssueManagementActivity.this, AddGoodIssueActivity.class);
                startActivity(intent);
            }
        });

        wrapExpandCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void showData() {
        databaseReference.child("GoodIssueData").orderByChild("giDateCreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodIssueModelArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    GoodIssueModel goodIssueModel = item.getValue(GoodIssueModel.class);
                    goodIssueModelArrayList.add(goodIssueModel);
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        helper.refreshDashboard(this.getApplicationContext());
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showData();
    }

    @Override
    public void onBackPressed() {
        helper.refreshDashboard(this.getApplicationContext());
        super.onBackPressed();
    }
}