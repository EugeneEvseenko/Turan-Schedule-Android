package com.turan.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    private final List<Day> days;

    ScheduleAdapter(Context context, List<Day> days) {
        this.days = days;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ViewHolder holder, int position) {
        Day day = days.get(position);
        int i = 0;
        for (Lesson lesson:day.lessons) {
            View v = inflater.inflate(R.layout.lesson_item, null);
            TextView title = (TextView) v.findViewById(R.id.lessonTitle);
            title.setText(lesson.getTitle());
            TextView subtitle = (TextView) v.findViewById(R.id.lessonSubtitle);
            subtitle.setText(lesson.getSubtitle(inflater.getContext()));
            if (lesson.active){
                LinearLayout background = (LinearLayout) v.findViewById(R.id.lesson);
                background.setBackgroundResource(R.color.purple_700);
                title.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.white));
                subtitle.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.white));
            }
            v.setId(i);
            v.setOnClickListener(view -> {
                Lesson item = days.get(position).lessons.get(view.getId());
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                mDialogBuilder.setTitle(item.name);
                mDialogBuilder.setMessage((item.call.pause != 0) ? String.format(inflater.getContext().getString(R.string.break_mask), item.call.pause) : inflater.getContext().getString(R.string.end_of_lessons));
                mDialogBuilder.setPositiveButton(R.string.call_dialog,
                        (dialog, id) -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.teacher.phone));
                            if (intent.resolveActivity(inflater.getContext().getPackageManager()) != null) {
                                inflater.getContext().startActivity(intent);
                            }
                        });
                mDialogBuilder.setNeutralButton(R.string.write_dialog,
                        (dialog, id) -> {
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ item.teacher.email});
                            email.setType("message/rfc822");
                            try {
                                inflater.getContext().startActivity(Intent.createChooser(email, inflater.getContext().getString(R.string.select_email)));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(inflater.getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                mDialogBuilder.setNegativeButton(R.string.dialog_close,
                        (dialog, id) -> {

                        });
                final AlertDialog mAlertDialog = mDialogBuilder.create();
                mAlertDialog.show();
            });
            holder.insertPoint.addView(v);
            i++;
        }
        holder.nameView.setText(day.name);
        if(day.active){
            holder.nameView.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.white));
            holder.header.setBackgroundResource(R.color.purple_500);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final LinearLayout insertPoint;
        final CardView header;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.dayofweek);
            header = (CardView) view.findViewById(R.id.headerDay);
            insertPoint = (LinearLayout) view.findViewById(R.id.insertPoint);
        }
    }
}
