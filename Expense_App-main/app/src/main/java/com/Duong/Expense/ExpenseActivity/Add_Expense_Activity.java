package com.Duong.Expense.ExpenseActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Duong.Expense.Database.MyDatabaseHelper;
import com.Duong.Expense.Object.Expense;
import com.Duong.Expense.Object.Trip;
import com.Duong.Expense.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Add_Expense_Activity extends AppCompatActivity {
    EditText dateExpense, amount, note, des;

    Trip selectedTrip;

    Button add_button;
    ImageView imageViewLocation;
    Calendar calendar;
    String[] typeExpenseList;
    ArrayAdapter<String> adapter;
    AutoCompleteTextView typeExpense;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip");

        dateExpense = findViewById(R.id.dateFromExpense);
        note = findViewById(R.id.Note);
        amount = findViewById(R.id.Amount);
        des = findViewById(R.id.ExpenseDestination);
        add_button = findViewById(R.id.add_button_expense);
        typeExpense = findViewById(R.id.itemListTypeExpense);

        // Type Expense
        typeExpenseList = getResources().getStringArray(R.array.typeExpense);
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, typeExpenseList
        );
        typeExpense.setAdapter(adapter);

        calendar = Calendar.getInstance();
        //Date Picker for EditText Date
        DatePickerDialog.OnDateSetListener datePickerFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateCalendar();
            }

            private void updateCalendar() {
                String format = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                dateExpense.setText(sdf.format(calendar.getTime()));
            }
        };
        //setOnClickListener to show date picker
        dateExpense.setOnClickListener(view -> new DatePickerDialog(Add_Expense_Activity.this, datePickerFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        add_button = findViewById(R.id.add_button_expense);
        add_button.setOnClickListener(v -> checkCredentials());
        imageViewLocation = findViewById(R.id.imageViewLocation);
        imageViewLocation.setOnClickListener(v -> whenClickLocation());
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
                            des.setText(city);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    private void whenClickLocation() {
        imageViewLocation.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            } else {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Location Location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try {
                    String city = hereLocation(Location.getLatitude(), Location.getLongitude());
                    des.setText(city);
                } catch (Exception e) {
                    Toast.makeText(this, "Please turn on your location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void checkCredentials() {
        String type = typeExpense.getText().toString().trim();
        String amountInput = amount.getText().toString().trim();
        String DateExpense = dateExpense.getText().toString().trim();

        if (type.isEmpty()) {
            typeExpense.setError("This is a required field");
            typeExpense.requestFocus();
        } else if (amountInput.isEmpty()) {
            showError(amount);
        } else if (DateExpense.isEmpty()) {
            showError(dateExpense);
        } else {
            addExpense();
        }
    }


    private void addExpense() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(Add_Expense_Activity.this);
        Expense expense = new Expense();
        expense.setAmount(Float.valueOf(amount.getText().toString().trim()));
        expense.setDate(dateExpense.getText().toString().trim());
        expense.setNote(note.getText().toString().trim());
        expense.setDestinationExpense(des.getText().toString().trim());
        expense.setTypeExpense(typeExpense.getText().toString().trim());
        expense.setTripID(selectedTrip.getId());

        long result = myDB.addExpense(expense);
        if (result == -1) {
            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Add_Expense_Activity.this, ExpenseActivity.class);
            Toast.makeText(getBaseContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();
            intent.putExtra("selectedTrip", selectedTrip);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field");
        input.requestFocus();
    }
}

