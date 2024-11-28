package edu.northeastern.group5_final.messaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import edu.northeastern.group5_final.R;

public class Inbox_recyclerViewAdapter extends RecyclerView.Adapter<Inbox_recyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<Messages> messages;

    public Inbox_recyclerViewAdapter(Context context, ArrayList<Messages> messages){
        this.context = context;
        this.messages = messages;

    }

    @NonNull
    @Override
    public Inbox_recyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inbox_recycler_view_row, parent, false);
        return new Inbox_recyclerViewAdapter.MyViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Inbox_recyclerViewAdapter.MyViewHolder holder, int position) {
        holder.txtSender.setText(messages.get(position).getReceiver());
        holder.txtSubject.setText(messages.get(position).getTopic());
        holder.txtDate.setText(messages.get(position).getDate());
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        Button btnView;
        TextView txtSender;
        TextView txtSubject;
        TextView txtDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnView = itemView.findViewById(R.id.btn_view);
            txtSender = itemView.findViewById(R.id.txt_sender);
            txtSubject = itemView.findViewById(R.id.txt_subject);
            txtDate = itemView.findViewById(R.id.txt_date);

        }
    }
}
