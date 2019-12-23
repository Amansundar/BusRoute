package com.android.busroute.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.android.busroute.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private Resources mResources;
    private static final String DATABASE_NAME = "trip.db";
    private static final int DATABASE_VERSION = 1;
    Context context;
    SQLiteDatabase db;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mResources = context.getResources();

        db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ROUTE_TABLE = "CREATE TABLE " + DbContract.MenuEntry.ROUTE_NAME + " (" +
                DbContract.MenuEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.MenuEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL, " +
                DbContract.MenuEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_SRC + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_DURA + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_DESTI + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_ICON + " INTEGER NOT NULL " + " );";

        final String SQL_CREATE_ROUTETIMING_TABLE = "CREATE TABLE " + DbContract.MenuEntry.ROUTE_TIMING_NAME + " (" +
                DbContract.MenuEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.MenuEntry.COLUMN_ID + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_SEATS + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_AVAILABLE + " TEXT NOT NULL, " +
                DbContract.MenuEntry.COLUMN_START_TIME + " TEXT NOT NULL " + " );";

        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_ROUTETIMING_TABLE);
        Log.d(TAG, "Database Created Successfully" );


        try {
            readRouteToDb(db);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void readRouteToDb(SQLiteDatabase db) throws IOException, JSONException {

        final String ROUTE_ID = "id";
        final String ROUTE_NAME = "name";
        final String ROUTE_SRC = "source";
        final String ROUTE_DURA = "tripDuration";
        final String ROUTE_DESTI = "destination";
        final String ROUTE_ICON = "icon";

        try{
            String jsonDataString = readJsonRouteFromFile();
            JSONObject routeObject = new JSONObject(jsonDataString);
            JSONArray routeArray = routeObject.getJSONArray("routeInfo");

            for (int i = 0; i < routeArray.length(); ++i) {

                String id;
                String name;
                String src;
                String duration;
                String destination;
                String icon;


                JSONObject menuItemObject = routeArray.getJSONObject(i);

                id = menuItemObject.getString(ROUTE_ID);
                name = menuItemObject.getString(ROUTE_NAME);
                src = menuItemObject.getString(ROUTE_SRC);
                duration = menuItemObject.getString(ROUTE_DURA);
                destination = menuItemObject.getString(ROUTE_DESTI);
                icon = menuItemObject.getString(ROUTE_ICON);


                ContentValues menuValues = new ContentValues();

                menuValues.put(DbContract.MenuEntry.COLUMN_ID, id);
                menuValues.put(DbContract.MenuEntry.COLUMN_NAME, name);
                menuValues.put(DbContract.MenuEntry.COLUMN_SRC, src);
                menuValues.put(DbContract.MenuEntry.COLUMN_DURA, duration);
                menuValues.put(DbContract.MenuEntry.COLUMN_DESTI, destination);
                menuValues.put(DbContract.MenuEntry.COLUMN_ICON, icon);

                db.insert(DbContract.MenuEntry.ROUTE_NAME, null, menuValues);


                Log.d(TAG, "Inserted Successfully " + menuValues );
            }

            JSONObject timings = routeObject.getJSONObject("routeTimings");

            for (int i = 1; i <= 5; ++i) {

                String seats;
                String avaiable;
                String tripStartTime;

                routeArray = timings.getJSONArray("r00" + i);

                for (int j = 0; j < routeArray.length(); ++j) {

                    JSONObject menuItemObject = routeArray.getJSONObject(j);

                    seats = menuItemObject.getString(DbContract.MenuEntry.COLUMN_SEATS);
                    avaiable = menuItemObject.getString(DbContract.MenuEntry.COLUMN_AVAILABLE);
                    tripStartTime = menuItemObject.getString(DbContract.MenuEntry.COLUMN_START_TIME);

                    ContentValues menuValues = new ContentValues();

                    menuValues.put(DbContract.MenuEntry.COLUMN_ID, "r00" + i);
                    menuValues.put(DbContract.MenuEntry.COLUMN_SEATS, seats);
                    menuValues.put(DbContract.MenuEntry.COLUMN_AVAILABLE, avaiable);
                    menuValues.put(DbContract.MenuEntry.COLUMN_START_TIME, tripStartTime);

                    db.insert(DbContract.MenuEntry.ROUTE_TIMING_NAME, null, menuValues);

                }
            }

        }catch (Exception e){
            Log.d(TAG, "Inserted Successfully " + e.getMessage() );
        }
    }


    private String readJsonRouteFromFile() throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = mResources.openRawResource(
                    R.raw.route);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }
}
