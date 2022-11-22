package com.Duong.Expense.ExpenseActivity;

import static com.Duong.Expense.Adapter.ExpenseAdapter.formatNumber;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Duong.Expense.Adapter.ExpenseAdapter;
import com.Duong.Expense.Database.MyDatabaseHelper;
import com.Duong.Expense.Object.Expense;
import com.Duong.Expense.Object.Trip;
import com.Duong.Expense.R;
import com.Duong.Expense.TripActivity.TripActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpenseActivity extends AppCompatActivity {
    TextView tripName, destination, dateFrom, dateTo, tripRisk, empty, total, desc;
    Trip selectedTrip;

    FloatingActionButton btnAdd;
    ImageView emptyImage;

    List<Expense> expenses;
    ExpenseAdapter expenseAdapter;
    RecyclerView recyclerView;

    MyDatabaseHelper myDB;
    String FileName = "Download.txt";
    ArrayList<String> SaveList= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Expense List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        selectedTrip = (Trip) intent.getSerializableExtra("selectedTrip");


        tripName = findViewById(R.id.tripName);
        destination = findViewById(R.id.destination);
        dateFrom = findViewById(R.id.dateFrom);
        dateTo = findViewById(R.id.dateTo);
        tripRisk = findViewById(R.id.Risk);
        desc = findViewById(R.id.Description);

        total = findViewById(R.id.Total_expense);
        empty = findViewById(R.id.no_data_Expense);
        emptyImage = findViewById(R.id.empty_imageview_Expense);
        recyclerView = findViewById(R.id.recyclerViewExpense);

        myDB = new MyDatabaseHelper(ExpenseActivity.this);
        expenses = new ArrayList<>();

        displayOrNot();

        expenseAdapter = new ExpenseAdapter(ExpenseActivity.this, this, expenses);
        recyclerView.setAdapter(expenseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExpenseActivity.this));
        //add expense
        btnAdd = findViewById(R.id.Floating_Add_button_Expense);
        btnAdd.setOnClickListener(view -> {
            Intent intents = new Intent(ExpenseActivity.this, Add_Expense_Activity.class);
            intents.putExtra("selectedTrip", selectedTrip);
            startActivity(intents);
        });
        getDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, TripActivity.class));
            return true;
        }if(item.getItemId()== R.id.delete_all){
            confirmDialog();
        }if (item.getItemId()== R.id.download_File){
            try {
                JsonDownload(selectedTrip.getId());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //download file
    private void JsonDownload (int id) throws IOException {
        SaveList = myDB.DownloadFile(id);
        if (saveFile(FileName, SaveList)){
            Toast.makeText(this, "Export Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ExpenseShowJsonActivity.class));
        } else{
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }
    //save file
    public boolean saveFile(String file, ArrayList<String> text) {
        try {
            FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(text);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    //get details
    private void getDetails() {
        tripName.setText(selectedTrip.getName());
        destination.setText(selectedTrip.getDes());
        dateFrom.setText(selectedTrip.getDateFrom());
        dateTo.setText(selectedTrip.getDateTo());
        tripRisk.setText(selectedTrip.getRisk());
        desc.setText(selectedTrip.getDesc());

        Float totalExpenses = myDB.getTotalExpense(String.valueOf(selectedTrip.getId()));
        total.setText(formatNumber(totalExpenses) + " " + "USD");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void displayOrNot() {
        expenses = myDB.getAllExpense(selectedTrip.getId());
        if (expenses.size() == 0) {
            emptyImage.setVisibility(View.VISIBLE);
            empty.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
            empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TripActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_download, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(ExpenseActivity.this);
            myDB.deleteAll();
            //Refresh Activity
            Intent intent = new Intent(ExpenseActivity.this, TripActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }
}
