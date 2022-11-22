package com.Duong.Expense.ExpenseActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Duong.Expense.R;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ExpenseShowJsonActivity extends AppCompatActivity {

    String FileName = "Download.txt";
    ArrayList<String> SaveList= new ArrayList<>();
    TextView showJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_show_json);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        showJson = findViewById(R.id.ShowJson);

        SaveList = readFile(FileName);

        showJson.setText(SaveList.get(0));
    }
    // Read file
    public ArrayList<String> readFile(String file) {
        ArrayList<String> text = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            text = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
        }
        return text;
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