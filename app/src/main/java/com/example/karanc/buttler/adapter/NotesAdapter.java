package com.example.karanc.buttler.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.karanc.buttler.R;
import com.example.karanc.buttler.appUtils.Utils;
import com.example.karanc.buttler.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    public List<Note> list;
    public List<Note> selectednotes_list;
    private Context context;
    private String TAG = NotesAdapter.class.getSimpleName();


    public NotesAdapter(List<Note> list, Context context, List<Note> selectednotes_list) {
        this.list = list;
        this.context = context;
        this.selectednotes_list = selectednotes_list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.noteview, null, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mNoteTime.setText(list.get(position).getTime());
        holder.mcontainer.setBackgroundColor(Utils.getRandomMaterialColor(context, "50to300"));
        holder.mNoteDesc.setText(list.get(position).getDesc());
        // Change background color on select
        if (selectednotes_list.size() > 0) {
            if (selectednotes_list.contains(list.get(position)))
                holder.mcontainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Note> filter) {
        this.list = filter;
        this.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mNoteDesc;
        public TextView mNoteTime;
        public RelativeLayout mcontainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            mNoteTime = itemView.findViewById(R.id.note_time);
            mNoteDesc = itemView.findViewById(R.id.note_desc);
            mcontainer = itemView.findViewById(R.id.note_container);

        }


    }


}
