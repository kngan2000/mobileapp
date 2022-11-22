package com.Duong.Expense.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.Duong.Expense.Database.MyDatabaseHelper;
import com.Duong.Expense.ExpenseActivity.ExpenseActivity;
import com.Duong.Expense.Object.Trip;
import com.Duong.Expense.R;
import com.Duong.Expense.TripActivity.TripActivity;
import com.Duong.Expense.TripActivity.UpdateActivity;
//import com.Duong.Expense.TripActivity.UpdateActivity;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> implements Filterable {

    private final Context context;
    private final Activity activity;
    private List<Trip> trips;
    private final List<Trip> OldTrips;

    public TripAdapter(Activity activity, Context context, List<Trip> trips) {
        this.activity = activity;
        this.context = context;
        this.trips = trips;
        this.OldTrips = trips;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_trip, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    // adapter get list trip
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Trip trip = trips.get(position);
        MyDatabaseHelper db = new MyDatabaseHelper(context);
//        int id = trip.getId();
        String name = trip.getName();
        String des = trip.getDes();
        String dateFrom = trip.getDateFrom();
        String dateTo = trip.getDateTo();
        String totalAmount = ExpenseAdapter.formatNumber(db.getTotalExpense(String.valueOf(trip.getId())));

        // set value to form
        holder.tripName.setText(name);
//        holder.TripId.setText(String.valueOf(id));
        holder.tripDestination.setText(des);
        holder.tripDate.setText(dateFrom.concat(" - " + dateTo));
        holder.total.setText(totalAmount);


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passing parameter values
                Intent intent = new Intent(context, ExpenseActivity.class);
                intent.putExtra("selectedTrip", trip);
                activity.startActivityForResult(intent, 1);
            }
        });
        holder.editTrip.setOnClickListener(view -> {
            //passing parameter values
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("selectedTrip", trip);
            activity.startActivityForResult(intent, 1);
        });
        holder.deleteTrip.setOnClickListener(view -> deleteTrip(trip.getId(), trip.getName()));
    }

    private void deleteTrip(int id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + name);
        builder.setMessage("Are you sure you want to delete ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                long result = myDB.delete(String.valueOf(id));
                if (result == -1) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, TripActivity.class));
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView editTrip, deleteTrip;
        TextView tripName, tripDestination, tripDate, total ;
        LinearLayout mainLayout;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            TripId = itemView.findViewById(R.id.TripID);
            tripName = itemView.findViewById(R.id.tripName);
            tripDestination = itemView.findViewById(R.id.TripDestination);
            tripDate = itemView.findViewById(R.id.date);
            total = itemView.findViewById(R.id.amount);

            deleteTrip = itemView.findViewById(R.id.imageViewDelete);
            editTrip = itemView.findViewById(R.id.imageViewEdit);

            mainLayout = itemView.findViewById(R.id.mainLayout);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    trips = OldTrips;
                } else {
                    List<Trip> list = new ArrayList<>();
                    for (Trip trip : OldTrips) {
                        if (trip.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(trip);
                        }
                        else if (trip.getDateFrom().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(trip);
                        }
                        else if (trip.getDes().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(trip);
                        }
                    }
                    trips = list;
                }
                FilterResults result = new FilterResults();
                result.values = trips;
                return result;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                trips = (List<Trip>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
