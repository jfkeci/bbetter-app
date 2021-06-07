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

import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.R;

import java.util.ArrayList;
import java.util.List;

public class EventsRecyclerAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Events> eventsList;
    int listType;

    public EventsRecyclerAdapter(Context context, ArrayList<Events> eventsList, int listType) {
        this.context = context;
        this.eventsList = eventsList;
        this.listType = listType;
    }

    public EventsRecyclerAdapter() {}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_layout,parent, false);

        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((listViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class listViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView twContent, twDate;
        private ImageView ivType, ivLeft, ivRight;
        private CardView cvToDo;

        public listViewHolder(View itemView){

            super (itemView);
            twContent = (TextView)itemView.findViewById(R.id.twContentSingleEvent);
            twDate = (TextView)itemView.findViewById(R.id.twDateSingleEvent);
            ivType = (ImageView)itemView.findViewById(R.id.ivSingleEvent);
            ivLeft = (ImageView)itemView.findViewById(R.id.swLeft);
            ivRight = (ImageView)itemView.findViewById(R.id.swRight);
            cvToDo = (CardView)itemView.findViewById(R.id.cvToDo);

            itemView.setOnClickListener(this);

        }

        public void bindView(int position){

            if(eventsList.size()>=1){
                String content = eventsList.get(position).getEventTitle();
                String mainContent = "";

                if(content.length() > 35){
                    int n=35;
                    for(int i=0;i<content.length();i++){
                        mainContent = mainContent + content.charAt(i);
                        if(i==n){
                            mainContent = mainContent + " \n ";
                            n+=35;
                        }
                    }
                }if(content.length() < 35){
                    mainContent = content;
                }

                twContent.setText(mainContent);
                twDate.setText(eventsList.get(position).getEventDate());

                if(eventsList.get(position).getEventType() == 1){
                    ivType.setImageResource(R.drawable.ic_calendar_white);
                    cvToDo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myTealGreen));
                }
                if(eventsList.get(position).getEventType() == 2){
                    ivType.setImageResource(R.drawable.ic_notify_white);
                    cvToDo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myMattePink));
                }
                if(eventsList.get(position).getEventType() == 3){
                    ivType.setImageResource(R.drawable.ic_check_white);
                    cvToDo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myLightBlue));
                }

                if(eventsList.get(position).isEventChecked()){
                    cvToDo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.myDarkGreen));
                }
            }

            if(listType == 1){
                ivLeft.setVisibility(View.INVISIBLE);
                ivRight.setVisibility(View.INVISIBLE);
            }
        }
        public void onClick(View view){

        }

    }

    public void setData(ArrayList<Events> eventsList)
    {
        this.eventsList = eventsList;
        this.notifyDataSetChanged();
    }
}

