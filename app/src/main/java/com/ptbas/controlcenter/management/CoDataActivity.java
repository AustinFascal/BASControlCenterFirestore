package com.ptbas.controlcenter.management;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ptbas.controlcenter.create.AddCoActivity;
import com.ptbas.controlcenter.databinding.ActivityDataCoBinding;
import com.ptbas.controlcenter.databinding.ActivityDataCustBinding;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.DragLinearLayoutUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.adapter.CoDataAdapter;
import com.ptbas.controlcenter.model.CoModel;
import com.ptbas.controlcenter.utility.LangUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class CoDataActivity extends AppCompatActivity {

    String[] searchTypeValue = {"giUID", "giRoUID", "giPoCustNumber", "vhlUID", "giMatName"};
    String dateStart = "", dateEnd = "", monthStrVal, dayStrVal;

    Context context;
    DatePickerDialog datePicker;


    Boolean expandStatus = true;
    HelperUtils helperUtils = new HelperUtils();
    ArrayList<CoModel> cashCoutModelArrayList = new ArrayList<>();

    CoDataAdapter coDataAdapter;

    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference refCO = db.collection("CashOutData");

    ProgressDialog pd;

    ActivityDataCoBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDataCoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        context = this;

        pd = new ProgressDialog(CoDataActivity.this);
        pd.setMessage("Memproses ...");
        pd.setCancelable(false);
        pd.show();

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Data Cash Out");

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

        // GO TO ADD GOOD ISSUE ACTIVITY
        binding.fabActionCreateCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoDataActivity.this, AddCoActivity.class);
                startActivity(intent);
            }
        });

        /*// GO TO RECAP GOOD ISSUE ACTIVITY
        fabActionRecapData.setOnClickListener(view -> {
            Intent intent = new Intent(CashOutManagementActivity.this, RecapGoodIssueDataActivity.class);
            startActivity(intent);
        });*/

        // SHOW DATA FROM DEFAULT QUERY
        showDataDefaultQuery();

        // HANDLE RECYCLERVIEW GI WHEN SCROLLING
        binding.recyclerView.setOnTouchListener((v, event) -> {
            switch ( event.getAction( ) ) {
                case MotionEvent.ACTION_SCROLL:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_DOWN:
                    binding.fabExpandMenu.animate().translationY(800).setDuration(100).start();
                    binding.fabExpandMenu.collapse();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    binding.fabExpandMenu.animate().translationY(0).setDuration(100).start();
                    break;
            }
            return false;
        });

        // HANDLE FILTER COMPONENTS WHEN ON CLICK
        binding.edtDateStartFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
                String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

                datePicker = new DatePickerDialog(CoDataActivity.this,
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

                            binding.edtDateStartFilter.setText(finalDate);
                            dateStart = finalDate;

                            //checkSelectedChipFilter();

                            binding.btnGiSearchByDateReset.setVisibility(View.VISIBLE);
                        }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
                datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePicker.show();
            }
        });

        binding.edtDateEndFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                dayStrVal = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                monthStrVal = String.valueOf(calendar.get(Calendar.MONTH));
                String yearStrVal = String.valueOf(calendar.get(Calendar.YEAR));

                datePicker = new DatePickerDialog(CoDataActivity.this,
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

                            binding.edtDateEndFilter.setText(finalDate);
                            dateEnd = finalDate;

                            //checkSelectedChipFilter();
                            binding.btnGiSearchByDateReset.setVisibility(View.VISIBLE);

                        }, Integer.parseInt(yearStrVal), Integer.parseInt(monthStrVal), Integer.parseInt(dayStrVal));
                datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                datePicker.show();
            }
        });

        coDataAdapter = new CoDataAdapter(this, cashCoutModelArrayList);

        binding.btnSelectAll.setVisibility(View.GONE);

        binding.btnVerifySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = coDataAdapter.getSelected().size();
                MaterialDialog md = new MaterialDialog.Builder((Activity) context)
                        .setAnimation(R.raw.lottie_approval)
                        .setTitle("Validasi Data Terpilih")
                        .setMessage("Apakah Anda yakin ingin mengesahkan "+size+" data Cash Out yang terpilih? Setelah disahkan, status tidak dapat dikembalikan.")
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            refCO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                                            String getDocumentID = documentSnapshot.getId();
                                            for (int i = 0; i < size; i++){
                                                if (getDocumentID.equals(coDataAdapter.getSelected().get(i).getCoDocumentID())){
                                                    db.collection("CashOutData").document(coDataAdapter.getSelected().get(i).getCoDocumentID()).update("coStatusApproval", true);
                                                    db.collection("CashOutData").document(coDataAdapter.getSelected().get(i).getCoDocumentID()).update("coApprovedBy", helperUtils.getUserId());
                                                    dialogInterface.dismiss();
                                                    //roManagementAdapter.clearSelection();
                                                }
                                            }

                                        }
                                    }
                                }
                            });
                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .setCancelable(true)
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            }
        });


        binding.btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = coDataAdapter.getSelected().size();
                MaterialDialog md = new MaterialDialog.Builder(CoDataActivity.this)
                        .setTitle("Hapus Data Terpilih")
                        .setAnimation(R.raw.lottie_delete)
                        .setMessage("Apakah Anda yakin ingin menghapus "+size+" data Cash Out yang terpilih? Setelah dihapus, data tidak dapat dikembalikan.")
                        .setCancelable(true)
                        .setPositiveButton("YA", R.drawable.ic_outline_check, (dialogInterface, which) -> {
                            refCO.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                                            String getDocumentID = documentSnapshot.getId();
                                            for (int i = 0; i < size; i++){
                                                db.collection("CashOutData").document(coDataAdapter.getSelected().get(i).getCoDocumentID()).delete();
                                                dialogInterface.dismiss();
                                           /* if (getDocumentID.equals(roManagementAdapter.getSelected().get(i).getRoDocumentID())){

                                                //roManagementAdapter.clearSelection();
                                            }*/
                                            }

                                        }
                                    }
                                }
                            });
                        /*dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        for (int i = 0; i < cashOutManagementAdapter.getSelected().size(); i++) {
                            //databaseReference.child("ReceivedOrderData").child(roManagementAdapter.getSelected().get(i).getGiUID()).removeValue();
                        }*/

                        })
                        .setNegativeButton("TIDAK", R.drawable.ic_outline_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();

                md.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                md.show();
            }
        });


        binding.btnExitSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                coDataAdapter.clearSelection();

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
        });

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                int itemSelectedSize = coDataAdapter.getSelected().size();
                if (coDataAdapter.getSelected().size()>0){

                    binding.fabExpandMenu.animate().translationY(800).setDuration(100).start();
                    binding.fabExpandMenu.collapse();

                    binding.tvTotalSelectedItem.setText(itemSelectedSize+" item terpilih");

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
                handler.postDelayed(this, 100);
            }
        };
        runnable.run();
    }

    private void showDataDefaultQuery() {

        db.collection("CashOutData").orderBy("coDateAndTimeCreated")
                .addSnapshotListener((value, error) -> {
                    pd.dismiss();
                    cashCoutModelArrayList.clear();
                    if (!value.isEmpty()){
                        for (DocumentSnapshot d : value.getDocuments()) {
                            CoModel coModel = d.toObject(CoModel.class);
                            cashCoutModelArrayList.add(coModel);
                        }
                        binding.llNoData.setVisibility(View.GONE);
                    } else{
                        binding.llNoData.setVisibility(View.VISIBLE);
                    }
                    Collections.reverse(cashCoutModelArrayList);
                    coDataAdapter = new CoDataAdapter(context, cashCoutModelArrayList);
                    binding.recyclerView.setAdapter(coDataAdapter);
                });
    }


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
        SearchView searchView = (SearchView) menu.findItem(R.id.search_data).getActionView();
        searchView.setQueryHint("Kata Kunci");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
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
            binding.cdvFilter.setVisibility(View.GONE);
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
                if (binding.spinnerSearchType.getText().toString().isEmpty() ||
                        Objects.requireNonNull(binding.edtDateStartFilter.getText()).toString().isEmpty() ||
                        Objects.requireNonNull(binding.edtDateEndFilter.getText()).toString().isEmpty()){
                    if (!searchView.getQuery().toString().isEmpty()){
                        dialogInterfaceUtils.fillSearchFilter(CoDataActivity.this, searchView);
                    }
                }

                return true;
            }
        });
        return true;
    }

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
       /* if (width<=1080){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            binding.recyclerView.setLayoutManager(mLayoutManager);
        }
        if (width>1080&&width<1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
            binding.recyclerView.setLayoutManager(mLayoutManager);
        }
        if (width>=1366){
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            binding.recyclerView.setLayoutManager(mLayoutManager);
        }*/

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.chipFilterAll.isChecked();
        showDataDefaultQuery();
    }

    @Override
    public void onBackPressed() {
        /*helper.refreshDashboard(this.getApplicationContext());
        finish();*/
        super.onBackPressed();
    }
}