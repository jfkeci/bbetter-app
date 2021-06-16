package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bbetterapp.Adapters.AppsRecyclerAdapter;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Apps;

import java.util.ArrayList;
import java.util.List;

public class AppLockListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Apps> appsList = new ArrayList<>();

    EditText etSearchApps;
    Button buttonSave;

    AppsRecyclerAdapter adapter;

    ProgressDialog progressDialog;

    MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_list);

        dbHelper = new MyDbHelper(this);

        recyclerView = findViewById(R.id.recycleViewApps);
        etSearchApps = findViewById(R.id.etAppSearch);
        buttonSave = findViewById(R.id.saveBlockedButton);

        adapter = new AppsRecyclerAdapter(appsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AppsRecyclerAdapter.OnAppClickedListener() {
            @Override
            public void onAppClick(int position) {
                adapter.notifyDataSetChanged();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                getInstalledApps();
            }
        });

        etSearchApps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppLockListActivity.this, FragmentHolderActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle("fetching apps");
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }

    private void filter(String appString){
        List<Apps> filteredList = new ArrayList<>();

        for(Apps app : appsList){
            if(app.getAppname().toLowerCase().contains(appString.toLowerCase())){

                filteredList.add(app);
            }
        }

        adapter.filterList(filteredList);
    }

    public void getInstalledApps(){
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i<packageInfos.size(); i++){
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packname = packageInfos.get(i).packageName;

            Apps app = new Apps(name, icon, 0, packname);

            appsList.add(app);

        }
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}