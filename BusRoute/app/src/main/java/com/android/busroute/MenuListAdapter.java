package com.android.busroute;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


/**
 * RecyclerView adapter extended with project-specific required methods.
 */

public class MenuListAdapter extends
        RecyclerView.Adapter<MenuListAdapter.MenuHolder> {

    private static final String TAG = MenuListAdapter.class.getSimpleName();

    /* ViewHolder for each insect item */
    public class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView menuTime, menuSeat;

        public MenuHolder(View itemView) {
            super(itemView);


            menuTime = (TextView) itemView.findViewById(R.id.menu_time);
            menuSeat = (TextView) itemView.findViewById(R.id.menu_seat);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        @Override
        public void onClick(View view) {

        }
    }

    private ArrayList<ListModel> listmodel;
    private Context mContext;
    private final LayoutInflater mInflater;

    public MenuListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        //this.mCursor = cursor;
    }

    public void setData(ArrayList<ListModel> listmodel) {
        this.listmodel = listmodel;
        notifyDataSetChanged();
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.menu_item, parent, false);
        return new MenuHolder(view);
    }


    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {

        if (listmodel != null) {

                String menuAvailable = listmodel.get(position).getAvailable();
                String menuTime =  listmodel.get(position).getStarTime();
                String menuseats =  listmodel.get(position).getTotalSeats();

                holder.menuSeat.setText("Available Seat : "+menuAvailable+"/"+menuseats);
                holder.menuTime.setText("Start Time : "+menuTime);


                Log.e (TAG, "onBindViewHolder: is on point");
            } else {
                Log.e (TAG, "onBindViewHolder: Cursor is null.");
            }


    }



    @Override
    public int getItemCount() {
        if (listmodel != null) {
            return listmodel.size();
        } else {
            return -1;
        }
    }

}
