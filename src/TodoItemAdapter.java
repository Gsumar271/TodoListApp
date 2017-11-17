package com.todolistapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created on 10/20/17.
 */
public class TodoItemAdapter extends ArrayAdapter<TodoItem> {

    int resource;
    Context mContext;
    final int maxTodoLength = 10;

    public interface OnDeleteClickedListener {
        public void deleteItem(int position);
    }

    private OnDeleteClickedListener onDeleteClickedListener;

    public TodoItemAdapter(Context context,
                           int resource,
                           List<TodoItem> items)
    {
        super(context, resource, items);
        this.resource = resource;
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LinearLayout todoView;
        String origTaskString, taskString;
        TodoItem item = getItem(position);

        origTaskString = item.getTask();
        //Get the first 10 characters out
        if (origTaskString.length() > maxTodoLength)
            taskString = origTaskString.substring(0, maxTodoLength);
        else
            taskString = origTaskString;


        String noteString = item.getNote();
        long createdDate = item.getCreated();
        int priorityLevel = item.getPriority();
        Date dueDate = item.getDueDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String dateString = sdf.format(createdDate);
        String dueDateString = sdf.format(dueDate);

        if (convertView == null) {
            todoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, todoView, true);
        } else {
            todoView = (LinearLayout) convertView;
        }

        TextView dateView = (TextView)todoView.findViewById(R.id.duedate);
        TextView taskView = (TextView)todoView.findViewById(R.id.todoText);
        TextView noteListView = (TextView)todoView.findViewById(R.id.noteList);
        ImageView imageView = (ImageView)todoView.findViewById(R.id.list_image);
        Button buttonView = (Button)todoView.findViewById(R.id.subTaskButton);
        ImageButton deletebtn = (ImageButton)todoView.findViewById(R.id.deleteButton);

        onDeleteClickedListener = (TodoItemAdapter.OnDeleteClickedListener)mContext;

        deletebtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(mContext instanceof OnDeleteClickedListener)
                          onDeleteClickedListener.deleteItem(position);
                    }
                });


        noteListView.setText(taskString);
        buttonView.setText(dueDateString);
        switch (priorityLevel) {
            case 0:
                imageView.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                imageView.setBackgroundColor(Color.YELLOW);
                break;
            case 2:
                imageView.setBackgroundColor(Color.RED);
                break;
        }


        return todoView;
    }


}
