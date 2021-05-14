package com.turan.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class BasicAdapter extends RecyclerView.Adapter<BasicAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final ArrayList<String> basics;

    BasicAdapter(Context context, ArrayList<String> basics) {
        this.basics = basics;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public BasicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.basic_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BasicAdapter.ViewHolder holder, int position) {
        String item = basics.get(position);
        holder.titleView.setText(item);
    }

    @Override
    public int getItemCount() {
        return basics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;
        ViewHolder(View view){
            super(view);
            titleView = (TextView) view.findViewById(R.id.basicTitle);
        }
    }
}
