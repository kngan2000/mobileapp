package com.Duong.Expense.TripActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.Duong.Expense.Database.MyDatabaseHelper;
import com.Duong.Expense.Object.Trip;
import com.Duong.Expense.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class UpdateActivity extends AppCompatActivity {

    Calendar calendar;
    EditText tripName, tripDestination, dateFrom, dateTo, description;
    Trip selectedTrip;
    RadioGroup radioGroup;
    RadioButton rdYes, rdNo, selectedRadioButton;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        tripName = findViewById(R.id.TripName);
        tripDestination = findViewById(R.id.TripDestination);
        description = findViewById(R.id.TripDescription);
        dateFrom = findViewById(R.id.dateFrom);
        dateTo = findViewById(R.id.dateEnd);
        rdYes = findViewById(R.id.radioYes);
        rdNo = findViewById(R.id.radioNo);
        btnSave = findViewById(R.id.save_button);

        getAndDisplayInfo();

        calendar = Calendar.getInstance();


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
                dateFrom.setText(sdf.format(calendar.getTime()));
            }
        };

        DatePickerDialog.OnDateSetListener datePickerTo = new DatePickerDialog.OnDateSetListener() {
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
                dateTo.setText(sdf.format(calendar.getTime()));
            }
        };
        dateFrom.setOnClickListener(view -> new DatePickerDialog(UpdateActivity.this, datePickerFrom, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        dateTo.setOnClickListener(view -> new DatePickerDialog(UpdateActivity.this, datePickerTo, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        //save button
        btnSave.setOnClickListener(view -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(this);

            radioGroup = findViewById(R.id.radioGroup);
            selectedRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            String risk = selectedRadioButton.getText().toString();

            selectedTrip.setName(tripName.getText().toString().trim());
            selectedTrip.setDes(tripDestination.getText().toString().trim());
            selectedTrip.setDateFrom(dateFrom.getText().toString().trim());
            selectedTrip.setDateTo(dateTo.getText().toString().trim());
            selectedTrip.setRisk(risk);
            selectedTrip.setDesc(description.getText().toString().trim());

            long result = myDB.update(selectedTrip);
            if (result == -1) {
                Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                finishActivity(1);
            }
        });
    }
    //get and display info
    private void getAndDisplayInfo() {
        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip");
        //display in textview
        tripName.setText(selectedTrip.getName());
        tripDestination.setText(selectedTrip.getDes());
        dateFrom.setText(selectedTrip.getDateFrom());
        dateTo.setText(selectedTrip.getDateTo());
        description.setText(selectedTrip.getDesc());

        if (selectedTrip.getRisk().equals("Yes")) {
            rdYes.setChecked(true);
        } else {
            rdNo.setChecked(true);
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(selectedTrip.getName());
        }
    }
}