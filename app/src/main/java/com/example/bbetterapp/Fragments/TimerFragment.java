package com.example.bbetterapp.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.bbetterapp.Adapters.SessionsRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.CountDownActivity;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.R;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimerFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final long START_TIME_IN_MILLIS = 60000;
    private long TimeLeftInMillis = START_TIME_IN_MILLIS;

    private Button btnStartPause;
    private ImageView ivClock, ivArrow;
    private Spinner sessionLengthSpinner;

    private List<Sessions> sessionList;

    private String sessionLengthSelected;

    private SessionsRecyclerAdapter sessionsAdapter;
    private RecyclerView recycleViewTimer;

    private Utils utils;
    private Sessions sessionUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        utils = new Utils(getActivity());
        sessionUtils = new Sessions(getActivity());

        btnStartPause = v.findViewById(R.id.buttonStartPause);
        ivClock = v.findViewById(R.id.ivTimerCircle);
        ivArrow = v.findViewById(R.id.ivTimerArrow);
        recycleViewTimer = v.findViewById(R.id.recycleViewTimer);
        sessionLengthSpinner = v.findViewById(R.id.sessionLengthSpinner);

        InitEventTypeSpinner();

        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CountDownActivity.class);
                intent.putExtra("session_length", sessionLengthSelected);
                startActivity(intent);
            }
        });

        sessionList = sessionUtils.allSessionsList();

        sessionsAdapter = new SessionsRecyclerAdapter(getActivity(), sessionList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        recycleViewTimer.setLayoutManager(gridLayoutManager);
        recycleViewTimer.setAdapter(sessionsAdapter);

        sessionsAdapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        sessionList = sessionUtils.allSessionsList();
        sessionsAdapter.setData(sessionList);
    }

    private void InitEventTypeSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.sessionLength, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessionLengthSpinner.setAdapter(adapter);
        sessionLengthSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sessionLengthSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}