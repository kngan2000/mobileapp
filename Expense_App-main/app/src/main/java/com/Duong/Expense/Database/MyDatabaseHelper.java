package com.Duong.Expense.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.Duong.Expense.Object.Expense;
import com.Duong.Expense.Object.Trip;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "expense.db";
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "id";
    private static final String TABLE_NAME = "Trip";
    private static final String COLUMN_NAME = "TripName";
    private static final String COLUMN_DESTINATION = "TripDestination";
    private static final String COLUMN_DATE_FROM = "TripDateFrom";
    private static final String COLUMN_DATE_TO = "TripDateTo";
    private static final String COLUMN_RISK = "Risk";
    private static final String COLUMN_DESC = "TripDesc";
    // Expense
    //public static final String Expense_ID_COLUMN = "ExpenseId";
    private static final String TABLE_NAME_Expense = "Expense";
    private static final String COLUMN_TYPE = "expenseType";
    public static final String AMOUNT_COLUMN = "Amount";
    public static final String DATE_COLUMN = "Date";
    public static final String COMMENT_COLUMN = "Note";
    public static final String LOCATION_COLUMN = "ExpenseDestination";
    //    public static final String IMAGE_COLUMN = "image";
    public static final String TRIP_ID_COLUMN = "trip_Id";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        dropAndRecreate(db);
    }
    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        createTablesExpense(db);
    }

    private void dropAndRecreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_Expense);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESTINATION + " TEXT, " +
                COLUMN_DATE_FROM + " TEXT, " +
                COLUMN_DATE_TO + " TEXT, " +
                COLUMN_RISK + " INTEGER, " +
                COLUMN_DESC + " TEXT);";
        db.execSQL(query);
    }

    private void createTablesExpense(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_Expense +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AMOUNT_COLUMN + " FLOAT, " +
                COLUMN_TYPE + " TEXT, " +
                DATE_COLUMN + " TEXT, " +
                COMMENT_COLUMN + " TEXT, " +
                LOCATION_COLUMN + " TEXT, " +
                TRIP_ID_COLUMN + " INTEGER, " +
                " FOREIGN KEY (" + TRIP_ID_COLUMN + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "));";
        db.execSQL(query);
    }

    public long addTrip(Trip trip) {
        long insertId;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, trip.getName());
        values.put(COLUMN_DESTINATION, trip.getDes());
        values.put(COLUMN_DATE_FROM, trip.getDateFrom());
        values.put(COLUMN_DATE_TO, trip.getDateTo());
        values.put(COLUMN_RISK, trip.getRisk());
        values.put(COLUMN_DESC, trip.getDesc());
        // Inserting Row
        insertId = db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
        return insertId;
    }

    public long addExpense(Expense expense) {
        long insertId;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, expense.getTypeExpense());
        values.put(AMOUNT_COLUMN, expense.getAmount());
        values.put(DATE_COLUMN, expense.getDate());
        values.put(LOCATION_COLUMN, expense.getDestinationExpense());
        values.put(COMMENT_COLUMN, expense.getNote());
        values.put(TRIP_ID_COLUMN, expense.getTripID());
        // Inserting Row
        insertId = db.insert(TABLE_NAME_Expense, null, values);
        db.close(); // Closing database connection
        return insertId;
    }

    public List<Trip> getAll() {
        final String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        final List<Trip> list = new ArrayList<>();
        final Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Trip trip = new Trip();
                    trip.setId(cursor.getInt(0));
                    trip.setName(cursor.getString(1));
                    trip.setDes(cursor.getString(2));
                    trip.setDateFrom(cursor.getString(3));
                    trip.setDateTo(cursor.getString(4));
                    trip.setRisk(cursor.getString(5));
                    trip.setDesc(cursor.getString(6));
                    // Adding object to list
                    list.add(trip);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return list;
    }

    public List<Expense> getAllExpense(int id) {
        final String query = String.format(
                "SELECT b.%s, %s, %s, %s, %s, %s, %s FROM " +
                        "%s a, %s b WHERE a.%s = b.%s AND b.%s = %s ORDER BY b.%s DESC",
                COLUMN_ID, COLUMN_TYPE, AMOUNT_COLUMN, LOCATION_COLUMN, DATE_COLUMN, COMMENT_COLUMN, TRIP_ID_COLUMN, TABLE_NAME, TABLE_NAME_Expense, COLUMN_ID, TRIP_ID_COLUMN, TRIP_ID_COLUMN, id, COLUMN_ID
        );
        SQLiteDatabase db = this.getReadableDatabase();
        final List<Expense> list = new ArrayList<>();
        final Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Expense expense = new Expense();
                    expense.setId(cursor.getInt(0));
                    expense.setTypeExpense(cursor.getString(1));
                    expense.setAmount(cursor.getFloat(2));
                    expense.setDestinationExpense(cursor.getString(3));
                    expense.setDate(cursor.getString(4));
                    expense.setNote(cursor.getString(5));
                    // Adding object to list
                    list.add(expense);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return list;
    }

    public ArrayList<String> DownloadFile(int id) {
        final String query = String.format(
                "SELECT b.%s, %s, %s, %s, %s, %s, %s, %s FROM " +
                        "%s a, %s b WHERE a.%s = b.%s AND b.%s = %s ORDER BY b.%s DESC",
                COLUMN_ID, COLUMN_NAME,COLUMN_TYPE, AMOUNT_COLUMN, LOCATION_COLUMN, DATE_COLUMN, COMMENT_COLUMN, TRIP_ID_COLUMN, TABLE_NAME, TABLE_NAME_Expense, COLUMN_ID, TRIP_ID_COLUMN, TRIP_ID_COLUMN, id, COLUMN_ID
        );
        SQLiteDatabase db = this.getReadableDatabase();
        final ArrayList<String> list = new ArrayList<>();
        final Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            StringBuilder JsonFormat = new StringBuilder();
            if (cursor.moveToFirst()) {
                JsonFormat.append("\n{\n\t\"").append(cursor.getString(0)).append("\":[");
                do {
                    String type = cursor.getString(1);
                    String TripName = cursor.getString(2);
                    String amount = String.valueOf(cursor.getFloat(3));
                    String Location = cursor.getString(4);
                    String date = cursor.getString(5);
                    String comments = cursor.getString(6);
                    if (!cursor.isLast()) {
                        JsonFormat.append("\n\t\t{\n\t\t\t\"NameTrip\":" + "\"").append(type).append("\",\n");
                        JsonFormat.append("\t\t\t\"expense\":" + "\"").append(TripName).append("\",\n");
                        JsonFormat.append("\t\t\t\"amount\":" + "\"").append(amount).append("\",\n");
                        JsonFormat.append("\t\t\t\"date\":" + "\"").append(date).append("\",\n");
                        JsonFormat.append("\t\t\t\"Location\":" + "\"").append(Location).append("\",\n");
                        JsonFormat.append("\t\t\t\"comments\":" + "\"").append(comments).append("\"\n");
                        JsonFormat.append("\t\t},");
                    } else {
                        JsonFormat.append("\n\t\t{\n\t\t\t\"NameTrip\":" + "\"").append(type).append("\",\n");
                        JsonFormat.append("\t\t\t\"expense\":" + "\"").append(TripName).append("\",\n");
                        JsonFormat.append("\t\t\t\"amount\":" + "\"").append(amount).append("\",\n");
                        JsonFormat.append("\t\t\t\"date\":" + "\"").append(date).append("\",\n");
                        JsonFormat.append("\t\t\t\"Location\":" + "\"").append(Location).append("\",\n");
                        JsonFormat.append("\t\t\t\"comments\":" + "\"").append(comments).append("\"\n");
                        JsonFormat.append("\t\t}");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            JsonFormat.append("\n\t]\n}");
            list.add(JsonFormat.toString());
            db.close();
        }
        return list;
    }

    public Float getTotalExpense(String id) {
        Float total = 0f;
        String query = "SELECT " + AMOUNT_COLUMN + " FROM " + TABLE_NAME_Expense + " WHERE " + TRIP_ID_COLUMN + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        final Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    total += cursor.getFloat(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return total;
    }

    public long update(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, trip.getName());
        values.put(COLUMN_DESTINATION, trip.getDes());
        values.put(COLUMN_DATE_FROM, trip.getDateFrom());
        values.put(COLUMN_DATE_TO, trip.getDateTo());
        values.put(COLUMN_RISK, trip.getRisk());
        values.put(COLUMN_DESC, trip.getDesc());

        return db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(trip.getId())});
    }

    public long updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, expense.getTypeExpense());
        values.put(AMOUNT_COLUMN, expense.getAmount());
        values.put(DATE_COLUMN, expense.getDate());
        values.put(LOCATION_COLUMN, expense.getDestinationExpense());
        values.put(COMMENT_COLUMN, expense.getNote());

        return db.update(TABLE_NAME_Expense, values, "id=?", new String[]{String.valueOf(expense.getId())});
    }

    public long delete(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=?", new String[]{row_id});
    }

    public long deleteExpense(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_Expense, "id=?", new String[]{row_id});
    }


    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        dropAndRecreate(db);
    }

}
