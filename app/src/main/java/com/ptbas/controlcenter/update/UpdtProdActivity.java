package com.ptbas.controlcenter.update;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.ProdModel;

import java.util.Objects;

public class UpdtProdActivity extends AppCompatActivity {

    HelperUtils helperUtils = new HelperUtils();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    TextInputEditText edtProductName, edtPriceBuy, edtPriceSell;
    FloatingActionButton fabSaveMaterialData;
    RadioGroup radioGroupStatus;
    RadioButton radioStatusSelected;
    LinearLayout wrapStatusToggle;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductData");

    String productUID ="";

    Boolean productStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updt_prod);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Perbarui Data Material");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        wrapStatusToggle = findViewById(R.id.wrapStatusToggle);
        radioGroupStatus = findViewById(R.id.radio_group_status);
        edtProductName = findViewById(R.id.edtProductName);
        edtPriceBuy = findViewById(R.id.edtPriceBuy);
        edtPriceSell = findViewById(R.id.edtPriceSell);
        fabSaveMaterialData = findViewById(R.id.fabSaveMaterialData);
        int selectedStatusId = radioGroupStatus.getCheckedRadioButtonId();
        radioStatusSelected = findViewById(selectedStatusId);
        //radioStatusSelected = findViewById(R.id.radio_group_status);

        edtProductName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productUID = extras.getString("key");
            databaseReference.child(productUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProdModel prodModel = snapshot.getValue(ProdModel.class);

                    if (prodModel == null){
                        finish();
                    } else {
                        productStatus = prodModel.getProductStatus();
                        edtProductName.setText(prodModel.getProductName());
                        edtPriceBuy.setText(String.valueOf(prodModel.getPriceBuy()));
                        edtPriceSell.setText(String.valueOf(prodModel.getPriceSell()));

                        if (Objects.requireNonNull(edtProductName.getText()).toString().equals("JASA ANGKUT")){
                            wrapStatusToggle.setVisibility(View.GONE);
                        } else {
                            wrapStatusToggle.setVisibility(View.VISIBLE);
                        }

                        if (productStatus.equals(true)){
                            radioGroupStatus.check(R.id.radio_active);
                        }else {
                            radioGroupStatus.check(R.id.radio_inactive);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        fabSaveMaterialData.setOnClickListener(view -> {

            //boolean productStatusVal;
            String productName = Objects.requireNonNull(edtProductName.getText()).toString();
            String productBuyPrice = Objects.requireNonNull(edtPriceBuy.getText()).toString();
            String productSellPrice = Objects.requireNonNull(edtPriceSell.getText()).toString();

            if (TextUtils.isEmpty(productName)){
                edtProductName.setError("Mohon masukkan nama material dengan benar");
                edtProductName.requestFocus();
            }
            if (TextUtils.isEmpty(productBuyPrice)){
                edtPriceBuy.setError("Mohon masukkan harga beli dengan benar");
                edtPriceBuy.requestFocus();
            }
            if (TextUtils.isEmpty(productSellPrice)){
                edtPriceSell.setError("Mohon masukkan harga jual dengan benar");
                edtPriceSell.requestFocus();
            }

            if (radioGroupStatus.getCheckedRadioButtonId() == R.id.radio_active){
                productStatus = true;
            } else if (radioGroupStatus.getCheckedRadioButtonId() == R.id.radio_inactive){
                productStatus = false;
            }

            if (!(TextUtils.isEmpty(productName)
                    &&TextUtils.isEmpty(productBuyPrice)
                    &&TextUtils.isEmpty(productSellPrice))){
                insertData(productName, Double.parseDouble(productBuyPrice), Double.parseDouble(productSellPrice));
            }
        });
    }

    private void insertData(String productName, Double productBuyPrice, Double productSellPrice) {
        ProdModel prodModel = new ProdModel(productName, productBuyPrice, productSellPrice, productStatus);

        //String productUID = productName.replaceAll(" ", "").toLowerCase();
        DatabaseReference dbRefAddVehicle = FirebaseDatabase.getInstance().getReference("ProductData");
        dbRefAddVehicle.child(productUID).setValue(prodModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialogInterfaceUtils.updatedInformation(UpdtProdActivity.this);
            } else {
                try{
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(UpdtProdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}