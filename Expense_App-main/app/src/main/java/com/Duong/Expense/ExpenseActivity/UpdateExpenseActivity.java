package com.Duong.Expense.ExpenseActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Duong.Expense.Database.MyDatabaseHelper;
import com.Duong.Expense.Object.Expense;
import com.Duong.Expense.Object.Trip;
import com.Duong.Expense.R;
import com.Duong.Expense.TripActivity.TripActivity;
import com.Duong.Expense.TripActivity.UpdateActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateExpenseActivity extends AppCompatActivity {

    Trip selectedTrip;

    AutoCompleteTextView typeExpense;
    EditText dateInput, amount, comment, Destination;
    Button btnSave;
    ImageView imageViewLocationUpdate;

    String[] typeExpenseList;
    ArrayAdapter<String> adapter;
    Calendar calendar;

    Expense selectedExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expense);

        Intent intent = getIntent();
        selectedExpense = (Expense) intent.getSerializableExtra("selectedExpense");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update \"" + selectedExpense.getTypeExpense() + "\"");

        dateInput = findViewById(R.id.expenseDate);
        typeExpense = findViewById(R.id.itemListTypeExpenses);
        amount = findViewById(R.id.amountExpense);
        comment = findViewById(R.id.comments);
        Destination = findViewById(R.id.ExpenseDestination);
        btnSave = findViewById(R.id.btnSave);
        calendar = Calendar.getInstance();

        getAndDisplayInfo();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                dateInput.setText(sdf.format(calendar.getTime()));

            }
        };

        dateInput.setOnClickListener(view -> new DatePickerDialog(UpdateExpenseActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        //Dropdown type expense
        typeExpenseList = getResources().getStringArray(R.array.typeExpense);
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, typeExpenseList
        );
        typeExpense.setAdapter(adapter);
        btnSave.setOnClickListener(view -> checkCredentials());
        imageViewLocationUpdate = findViewById(R.id.UpdateLocation);
        imageViewLocationUpdate.setOnClickListener(v -> whenClickLocation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                    } else {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        Location Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try {
                            String city = hereLocation(Location.getLatitude(), Location.getLongitude());
                            Destination.setText(city);
                        } catch (Exception e) {
                            Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //Get location
    private String hereLocation(double latitude, double longitude) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        cityName = adr.getLocality();
                        cityName = cityName + ", " + adr.getAdminArea() + ", " + adr.getCountryName();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }
    //When click location icon
    private void whenClickLocation() {
        imageViewLocationUpdate.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            } else {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Location Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try {
                    String city = hereLocation(Location.getLatitude(), Location.getLongitude());
                    Destination.setText(city);
                } catch (Exception e) {
                    Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkCredentials() {
        String type = typeExpense.getText().toString().trim();
        String money = amount.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        if (type.isEmpty()) {
            typeExpense.setError("This is a required field");
            typeExpense.requestFocus();
        } else if (money.isEmpty()) {
            showError(amount, "This is a required field");
        } else if (date.isEmpty()) {
            showError(dateInput, "This is a required field");
        } else {
            updateExpense();
        }
    }

    private void updateExpense() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(this);

        selectedExpense.setTypeExpense(typeExpense.getText().toString().trim());
        selectedExpense.setAmount(Float.parseFloat(amount.getText().toString().trim()));
        selectedExpense.setDate(dateInput.getText().toString().trim());
        selectedExpense.setDestinationExpense(Destination.getText().toString().trim());
        selectedExpense.setNote(comment.getText().toString().trim());

        long result = myDB.updateExpense(selectedExpense);

        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finishActivity(1);
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void getAndDisplayInfo() {
        typeExpense.setText(selectedExpense.getTypeExpense());
        amount.setText(String.valueOf(selectedExpense.getAmount()));
        dateInput.setText(selectedExpense.getDate());
        Destination.setText(selectedExpense.getDestinationExpense());
        comment.setText(selectedExpense.getNote());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}