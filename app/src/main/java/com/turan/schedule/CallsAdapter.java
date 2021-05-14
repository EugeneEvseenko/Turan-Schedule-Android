package com.turan.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<Call> calls;

    CallsAdapter(Context context, List<Call> calls) {
        this.calls = calls;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public CallsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.call_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CallsAdapter.ViewHolder holder, int position) {
        Call call = calls.get(position);
        holder.nameView.setText(call.getTitle(inflater.getContext()));
        holder.descriptionView.setText(call.getSubtitle(inflater.getContext()));
        if(call.active){
            holder.background.setBackgroundResource(R.color.purple_700);
            holder.nameView.setTextColor(ContextCompat.getColor(inflater.getContext(),R.color.white));
            holder.descriptionView.setTextColor(ContextCompat.getColor(inflater.getContext(),R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return calls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, descriptionView;
        final LinearLayout background;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.name);
            descriptionView = (TextView) view.findViewById(R.id.description);
            background = (LinearLayout) view.findViewById(R.id.background);
        }
    }
}
