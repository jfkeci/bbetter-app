package com.example.bbetterapp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Apps;
import com.example.bbetterapp.R;

import java.util.ArrayList;
import java.util.List;

public class AppsRecyclerAdapter extends RecyclerView.Adapter<AppsRecyclerAdapter.AppExampleViewHolder> {

    List<Apps> appsList = new ArrayList<>();
    List<Apps> blockedAppsList = new ArrayList<>();

    Context context;

    private OnAppClickedListener mListener;

    MyDbHelper dbHelper;

    public interface OnAppClickedListener{
        void onAppClick(int position);
    }

    public void setOnItemClickListener(OnAppClickedListener listener){
        mListener = listener;
    }

    public AppsRecyclerAdapter(List<Apps> appsLis, Context context) {
        this.appsList = appsList;
        this.context = context;
        dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public AppExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.app_row_layout, parent, false);

        return new AppExampleViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppExampleViewHolder holder, int position) {
        Apps app = appsList.get(position);

        holder.appName.setText(app.getAppname());
        holder.appIcon.setImageDrawable(app.getAppicon());

        app = checkTheApp(app);

        if(app.getStatus() == 0){
            holder.lockIcon.setImageResource(R.drawable.ic_lock_open_white);
        }else{
            holder.lockIcon.setImageResource(R.drawable.ic_lock_white);
        }

    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public class AppExampleViewHolder extends RecyclerView.ViewHolder{

        TextView appName;
        ImageView appIcon, lockIcon;

        public AppExampleViewHolder(@NonNull View itemView, final OnAppClickedListener listener) {
            super(itemView);

            appName = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
            lockIcon = itemView.findViewById(R.id.lockIcon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAppClick(position);
                            setAppStatus(position);
                        }
                    }
                }
            });
        }
    }
    public void filterList(List<Apps> filteredList){
        appsList = filteredList;
        notifyDataSetChanged();
    }

    public void setAppStatus(int position){

        Apps app = appsList.get(position);

        int status = app.getStatus();

        if(status == 0){
            appsList.get(position).setStatus(1);
            dbHelper.addNewApp(appsList.get(position));
        }if(status == 1){
            appsList.get(position).setStatus(0);
            dbHelper.deleteApp(appsList.get(position).getAppname());
        }
    }

    public Apps checkTheApp(Apps app){
        blockedAppsList.clear();
        blockedAppsList = allBlockedAppsList();

        for (Apps blocked_app : blockedAppsList) {
            if(blocked_app.getAppname().equals(app.getAppname())){
                app.setStatus(blocked_app.getStatus());
            }
        }

        return app;
    }

    public List<Apps> allBlockedAppsList(){
        List<Apps> myApps = new ArrayList<>();

        myApps.clear();

        Cursor res = dbHelper.getAllApps();

        Drawable icon = null;

        while(res.moveToNext()){
            Apps app = new Apps(res.getString(0), icon,
                    Integer.parseInt(res.getString(1)), res.getString(2) );
            myApps.add(app);
        }

        return myApps;
    }
}