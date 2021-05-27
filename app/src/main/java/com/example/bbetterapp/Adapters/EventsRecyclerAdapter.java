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

public class EventsRecyclerAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Events> eventsList = new ArrayList<>();
    int listType;


    public EventsRecyclerAdapter(Context context, ArrayList<Events> events, int listType) {
        this.context = context;
        this.eventsList = events;
        this.listType = listType;
    }


    public void setData(ArrayList<Events> eventsList)
    {
        this.eventsList = eventsList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_layout, parent, false);

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textContent, textDate;
        private ImageView typeIcon, ivLeft, ivRight;
        private CardView cardView;

        public ListViewHolder(View itemView){

            super (itemView);
            textContent = (TextView)itemView.findViewById(R.id.textContentSingleEvent);
            textDate = (TextView)itemView.findViewById(R.id.textDateSingleEvent);
            typeIcon = (ImageView) itemView.findViewById(R.id.imageViewSingleEvent);
            cardView = (CardView)itemView.findViewById(R.id.cardViewToDo);

            itemView.setOnClickListener(this);

        }

        public void bindView(int position){

            if(eventsList.size()>=1){
                String content = eventsList.get(position).getEventDetails();
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


                textContent.setText(mainContent);
                textDate.setText(eventsList.get(position).getEventCreatedAt());

            }

        }
        public void onClick(View view){

        }

    }

}

