package com.Duong.Expense.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.Duong.Expense.Database.MyDatabaseHelper;
import com.Duong.Expense.ExpenseActivity.UpdateExpenseActivity;
import com.Duong.Expense.Object.Expense;
import com.Duong.Expense.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<Expense> expenses;
    TextView typeExpense, amountExpense, dateExpense, commentExpense;


    public ExpenseAdapter(Activity activity, Context context, List<Expense> expense) {
        this.activity = activity;
        this.context = context;
        this.expenses = expense;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_expense, parent, false);
        return new ExpenseAdapter.MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.MyViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        String types = expense.getTypeExpense();
        String amount = formatNumber(expense.getAmount());
        String date = expense.getDate();
        String des = expense.getDestinationExpense();

        holder.type.setText(types);
        holder.expenseDate.setText(date);
        holder.expenseAmount.setText(String.valueOf(amount));
        holder.des.setText(des);

        holder.deleteExpense.setOnClickListener(v -> deleteExpense(expense, expense.getId()));

        holder.updateExpense.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateExpenseActivity.class);
            intent.putExtra("selectedExpense", expense);
            activity.startActivityForResult(intent, 1);
        });
        holder.mainLayoutExpense.setOnClickListener(v -> showDetailExpense(expense));
    }

    @SuppressLint("SetTextI18n")
    private void showDetailExpense(Expense expense) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View contentPopUp = activity.getLayoutInflater().inflate(R.layout.popup,null);

        typeExpense = contentPopUp.findViewById(R.id.TypeExpensePopup);
        amountExpense = contentPopUp.findViewById(R.id.AmountPopup);
        dateExpense = contentPopUp.findViewById(R.id.DatePopup);
        commentExpense = contentPopUp.findViewById(R.id.commentsPopup);

        typeExpense.setText(expense.getTypeExpense());
        amountExpense.setText(formatNumber(expense.getAmount()));
        dateExpense.setText(expense.getDate());

        if(expense.getNote().isEmpty()){
            commentExpense.setText("No comment");
        }
        else{
            commentExpense.setText(expense.getNote());
        }
        dialogBuilder.setView(contentPopUp);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void deleteExpense(Expense expense, Integer id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + expense.getTypeExpense() + "?");
        builder.setMessage("Are you sure you want to delete ?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
            long result = myDB.deleteExpense(String.valueOf(id));
            if (result == -1) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                activity.finish();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                context.startActivity(activity.getIntent());
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
            // do nothing
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView updateExpense, deleteExpense;
        TextView type, expenseDate, expenseAmount, des;
        LinearLayout mainLayoutExpense;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.TypeExpense);
            expenseDate = itemView.findViewById(R.id.date);
            expenseAmount = itemView.findViewById(R.id.AmountAdapter);
            des = itemView.findViewById(R.id.DestinationAdapter);
            updateExpense = itemView.findViewById(R.id.imageViewEditExpense);
            deleteExpense = itemView.findViewById(R.id.imageViewDeleteExpense);
            mainLayoutExpense = itemView.findViewById(R.id.mainLayoutExpense);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayoutExpense.setAnimation(translate_anim);
        }
    }
        public static String formatNumber(Float amount) {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            formatter.setDecimalFormatSymbols(symbols);
            return formatter.format(amount);
        }
}