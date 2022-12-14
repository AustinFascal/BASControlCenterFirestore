package com.ptbas.controlcenter.create;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ptbas.controlcenter.utility.DialogInterfaceUtils;
import com.ptbas.controlcenter.utility.HelperUtils;
import com.ptbas.controlcenter.R;
import com.ptbas.controlcenter.model.ProdModel;

import java.util.Objects;

public class AddProdActivity extends AppCompatActivity {

    HelperUtils helperUtils = new HelperUtils();
    DialogInterfaceUtils dialogInterfaceUtils = new DialogInterfaceUtils();

    TextInputEditText edtProductName, edtPriceBuy, edtPriceSell;
    FloatingActionButton fabSaveMaterialData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prod);

        ActionBar actionBar = getSupportActionBar();

        // ACTION BAR FOR STANDARD ACTIVITY
        assert actionBar != null;
        helperUtils.handleActionBarConfigForStandardActivity(
                this, actionBar, "Tambah Material");

        // SYSTEM UI MODE FOR STANDARD ACTIVITY
        helperUtils.handleUIModeForStandardActivity(this, actionBar);

        edtProductName = findViewById(R.id.edtProductName);
        edtPriceBuy = findViewById(R.id.edtPriceBuy);
        edtPriceSell = findViewById(R.id.edtPriceSell);
        fabSaveMaterialData = findViewById(R.id.fabSaveMaterialData);

        edtProductName.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        fabSaveMaterialData.setOnClickListener(view -> {
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

            if (!(TextUtils.isEmpty(productName)
                    &&TextUtils.isEmpty(productBuyPrice)
                    &&TextUtils.isEmpty(productSellPrice))){
                insertData(productName, Double.parseDouble(productBuyPrice), Double.parseDouble(productSellPrice));
            }
        });
    }

    private void insertData(String productName, Double productBuyPrice, Double productSellPrice) {
        ProdModel prodModel = new ProdModel(productName, productBuyPrice, productSellPrice, true);

        String productUID = productName.replaceAll(" ", "").toLowerCase();
        DatabaseReference dbRefAddVehicle = FirebaseDatabase.getInstance().getReference("ProductData");
        dbRefAddVehicle.child(productUID).setValue(prodModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialogInterfaceUtils.savedInformationFromManagement(AddProdActivity.this);
            } else {
                try{
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(AddProdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        //helper.refreshDashboard(this.getApplicationContext());
        finish();
    }
}