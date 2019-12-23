package com.android.busroute;

import android.animation.ArgbEvaluator;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.android.busroute.data.DbContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    Adapter caroselAdapter;
    String currentRoute;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    RecyclerView recyclerView;
    MenuListAdapter adapter;

    private Timer autoUpdate;

    private static final String[] TIMING_PROJECTION = new String[]{
            DbContract.MenuEntry._ID,
            DbContract.MenuEntry.COLUMN_ID,
            DbContract.MenuEntry.COLUMN_SEATS,
            DbContract.MenuEntry.COLUMN_AVAILABLE,
            DbContract.MenuEntry.COLUMN_START_TIME
    };

    private static final String[] ROUTE_PROJECTION = new String[]{
            DbContract.MenuEntry._ID,
            DbContract.MenuEntry.COLUMN_ID,
            DbContract.MenuEntry.COLUMN_SRC,
            DbContract.MenuEntry.COLUMN_DURA,
            DbContract.MenuEntry.COLUMN_DESTI
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new MenuListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };

        colors = colors_temp;
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateScreen();
                    }
                });
            }
        }, 0, 60000); // updates each 60 secs
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            autoUpdate.cancel();
        }catch (Exception e){

        }
    }

    public boolean getTime (String getTime) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(c.getTime()).compareTo(getTime)<=0;

    }

    public void updateScreen(){
        String queryUri = DbContract.MenuEntry.CONTENT_URI.toString();
        onLoadFinished(getContentResolver().query(Uri.parse(queryUri), TIMING_PROJECTION, null, null, DbContract.MenuEntry.COLUMN_ID));
    }


    public void onLoadFinished(Cursor data) {
        final TreeMap<String,ArrayList<ListModel>> routeMap = new TreeMap<>();

        if (data != null) {
            try {
                while (data.moveToNext()) {
                    int columnId = data.getColumnIndex(DbContract.MenuEntry.COLUMN_ID);
                    String id = data.getString(columnId);
                    int totalseats = data.getColumnIndex(DbContract.MenuEntry.COLUMN_SEATS);
                    int available = data.getColumnIndex(DbContract.MenuEntry.COLUMN_AVAILABLE);
                    int time = data.getColumnIndex(DbContract.MenuEntry.COLUMN_START_TIME);

                    String menuAvailable = data.getString(available);
                    String menuTime = data.getString(time);
                    String menuseats = data.getString(totalseats);
                    if (getTime(menuTime)) {
                        ListModel mListModel = new ListModel(menuTime, menuAvailable, menuseats);
                        if(!routeMap.containsKey(id)){
                            ArrayList mArrayListv= new ArrayList<ListModel>();
                            mArrayListv.add(mListModel);
                            routeMap.put(id,mArrayListv);
                        }
                        else{
                            ArrayList mArrayListv= routeMap.get(id);
                            mArrayListv.add(mListModel);
                            routeMap.put(id,mArrayListv);
                        }
                    }
                }
            } catch (Exception e){

            }
            finally {
                data.close();
            }
        }
        int currentPosition=0;
        final ArrayList<Model> models = new ArrayList<>();
        String queryUri = DbContract.MenuEntry.CONTENT_ROUTE_URI.toString();
        Cursor cursor=  getContentResolver().query(Uri.parse(queryUri), ROUTE_PROJECTION, null, null, DbContract.MenuEntry.COLUMN_ID);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int columnId = cursor.getColumnIndex(DbContract.MenuEntry.COLUMN_ID);

                    int src = cursor.getColumnIndex(DbContract.MenuEntry.COLUMN_SRC);
                    int dura = cursor.getColumnIndex(DbContract.MenuEntry.COLUMN_DURA);
                    int dest = cursor.getColumnIndex(DbContract.MenuEntry.COLUMN_DESTI);
                    String id = cursor.getString(columnId);

                    if(routeMap.containsKey(id)){
                        String duration = cursor.getString(dura);
                        String source = cursor.getString(src);
                        String destination = cursor.getString(dest);
                        models.add(new Model( id, source+" - "+destination, duration));
                        if(currentRoute!=null && currentRoute.equals(id))
                            currentPosition=models.size()-1;
                    }
                }

                caroselAdapter = new Adapter(models, this);
                viewPager.setAdapter(caroselAdapter);
                viewPager.setCurrentItem(currentPosition);
                viewPager.setPadding(130, 0, 130, 0);

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        if (position < (caroselAdapter.getCount() -1) && position < (colors.length - 1)) {
                            viewPager.setBackgroundColor(

                                    (Integer) argbEvaluator.evaluate(
                                            positionOffset,
                                            colors[position],
                                            colors[position + 1]
                                    )
                            );
                        }

                        else {
                            viewPager.setBackgroundColor(colors[colors.length - 1]);
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if(!routeMap.isEmpty()) {
                            currentRoute = models.get(position).getTitle();
                            adapter.setData(routeMap.get(currentRoute));
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });


            } finally {
                cursor.close();
            }
        }
        if(!routeMap.isEmpty()) {
            findViewById(R.id.error).setVisibility(View.GONE);
            if(currentRoute!=null && routeMap.containsKey(currentRoute)){
                adapter.setData(routeMap.get(currentRoute));
            }
            else{
                currentRoute = routeMap.firstEntry().getKey();
                adapter.setData(routeMap.firstEntry().getValue());
            }

        }else{
            adapter.setData(null);
            findViewById(R.id.error).setVisibility(View.VISIBLE);
        }
    }
}

