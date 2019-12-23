package com.android.busroute.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class DbContract {

    public static final String CONTENT_AUTHORITY = "com.android.busroute";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_TIMING_ENTRIES = "entries";

    private static final String PATH_ROUTE_ENTRIES = "routes";


    public static class MenuEntry implements BaseColumns {

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.busroute.entries";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.busroute.entry";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMING_ENTRIES).build();

        public static final Uri CONTENT_ROUTE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTE_ENTRIES).build();

        public static final String ROUTE_NAME = "Route";
        public static final String ROUTE_TIMING_NAME = "Route_Timing";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SRC = "source";
        public static final String COLUMN_DURA = "tripDuration";
        public static final String COLUMN_DESTI = "destination";
        public static final String COLUMN_ICON = "icon";

        public static final String COLUMN_SEATS = "totalSeats";
        public static final String COLUMN_AVAILABLE = "avaiable";
        public static final String COLUMN_START_TIME = "tripStartTime";

    }
}
