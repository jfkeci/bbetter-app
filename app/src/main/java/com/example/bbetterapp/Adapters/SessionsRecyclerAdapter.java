package com.example.bbetterapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.R;

import java.util.ArrayList;
import java.util.List;

public class SessionsRecyclerAdapter extends RecyclerView.Adapter {
    Context context;
    List<Sessions> sessionsList;

    public SessionsRecyclerAdapter(Context context, List<Sessions> sessions) {
        this.context = context;
        this.sessionsList = sessions;
    }

    public void setData(List<Sessions> sessionsList) {
        this.sessionsList = sessionsList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_row_layout, parent, false);

        return new SessionsRecyclerAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SessionsRecyclerAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return sessionsList.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView twContent1, twDate1, twTime1;
        private ImageView ivIcon1;
        private CardView cardView1;

        public ListViewHolder(View itemView) {

            super(itemView);
            twContent1 = itemView.findViewById(R.id.singleSessionlength);
            twDate1 = itemView.findViewById(R.id.singleSessionDate);
            twTime1 = itemView.findViewById(R.id.singleSessionTime);
            ivIcon1 = itemView.findViewById(R.id.sessionTimeIcon);
            cardView1 = itemView.findViewById(R.id.cardViewSession);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {

            if (sessionsList.get(position).isSessionFinished()) {
                cardView1.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myTealGreen));
            } else {
                cardView1.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myMattePink));
            }
            if (sessionsList.get(position).getSessionLength() == 10) {
                ivIcon1.setImageResource(R.drawable.ic_one_white);
            }
            if (sessionsList.get(position).getSessionLength() == 15) {
                ivIcon1.setImageResource(R.drawable.ic_one_white);
            }
            if (sessionsList.get(position).getSessionLength() == 20) {
                ivIcon1.setImageResource(R.drawable.ic_two_white);
            }
            if (sessionsList.get(position).getSessionLength() == 25) {
                ivIcon1.setImageResource(R.drawable.ic_two_white);
            }
            if (sessionsList.get(position).getSessionLength() == 30) {
                ivIcon1.setImageResource(R.drawable.ic_three_white);
            }
            if (sessionsList.get(position).getSessionLength() == 35) {
                ivIcon1.setImageResource(R.drawable.ic_three_white);
            }
            if (sessionsList.get(position).getSessionLength() == 40) {
                ivIcon1.setImageResource(R.drawable.ic_four_white);
            }
            String length = sessionsList.get(position).getSessionLength() + "min";
            String sDate = sessionsList.get(position).getSessionCreatedAt();
            twContent1.setText(length);
            twDate1.setText(sDate);

        }

        public void onClick(View view) {
        }
    }
}