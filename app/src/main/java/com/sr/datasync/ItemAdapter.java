package com.sr.datasync;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ArrayList<EntryClass> entryClasses;
    private ArrayList<String> dataIDs;

    public ItemAdapter(ArrayList<EntryClass> entryClasses, ArrayList<String> dataIDs){
        this.entryClasses = entryClasses;
        this.dataIDs = dataIDs;
        notifyDataSetChanged();
    }

    public void addItem(EntryClass entryClass, String dataID){
        dataIDs.add(dataID);
        entryClasses.add(entryClass);
        notifyItemInserted(dataIDs.size()-1);
    }

    public void updateView(ArrayList<EntryClass> entryClasses, ArrayList<String> dataIDs){
        this.entryClasses = entryClasses;
        this.dataIDs = dataIDs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        EntryClass entry = entryClasses.get(position);
        String dataID = dataIDs.get(position);

        holder.setDataID(dataID);
        holder.setPosition(position);
        holder.titleTV.setText(entry.getTitle());
        holder.dataTV.setText(entry.getData());
    }

    @Override
    public int getItemCount() {
        return dataIDs.size();
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV, dataTV;
        private String dataID;
        private int position;

        public void setDataID(String dataID) {
            this.dataID = dataID;
        }

        public void setPosition(int position){
            this.position = position;
        }

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.card_title);
            dataTV = itemView.findViewById(R.id.card_data);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), EditMainActivity.class);
                    intent.putExtra("DATA_ID", dataID);
                    intent.putExtra("POSITION", position);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }


}
