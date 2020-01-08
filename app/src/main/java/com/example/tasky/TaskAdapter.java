package com.example.tasky;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.tasky.models.Task;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class TaskAdapter extends RealmBaseAdapter<Task> implements ListAdapter {

    private MainActivity activity;

    private static class ViewHolder {
        TextView taskName;
        CheckBox isTaskDone;
    }

    TaskAdapter(MainActivity activity, OrderedRealmCollection<Task> data) {
        super(data);
        this.activity = activity;
    }

    @SuppressLint("CutPasteId")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskName = convertView.findViewById(R.id.task_item_name);
            viewHolder.isTaskDone = convertView.findViewById(R.id.task_item_done);
            viewHolder.isTaskDone.setOnClickListener(listener);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Task task = adapterData.get(position);
            viewHolder.taskName.setText(task.getName());
            viewHolder.isTaskDone.setChecked(task.isDone());
            viewHolder.isTaskDone.setTag(position);
        }

        return convertView;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            if (adapterData != null) {
                Task task = adapterData.get(position);
                activity.changeTaskDone(task.getId());
            }
        }
    };
}