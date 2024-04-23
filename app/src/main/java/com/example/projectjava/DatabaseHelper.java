package com.example.projectjava;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "FitTracker.db";
    private static final int DATABASE_VERSION = 53;

    private static final String TABLE_WORKOUT = "workout";
    private static final String WORKOUT_ID = "id";
    private static final String WORKOUT_START_DATE = "startDate";
    private static final String WORKOUT_END_DATE = "endDate";
    private static final String WORKOUT_TYPE = "type";
    private static final String WORKOUT_NOTES = "notes";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_WORKOUT + " (" + WORKOUT_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORKOUT_START_DATE + " TEXT, " + WORKOUT_END_DATE + " TEXT, " + WORKOUT_TYPE + " TEXT, " + WORKOUT_NOTES + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        onCreate(db);
    }

    public long addWorkout(Workout w){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        cv.put(WORKOUT_START_DATE, "teste");
        cv.put(WORKOUT_START_DATE, "teste");
        cv.put(WORKOUT_TYPE, w.getType());
        cv.put(WORKOUT_NOTES, w.getNotes());

        long id = db.insert(TABLE_WORKOUT, null, cv);

        if (id == -1){
            Toast.makeText(context, "Failed to insert the Workout.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully inserted the Workout!", Toast.LENGTH_SHORT).show();
        }
        db.close();
        return 1;
    }
}
