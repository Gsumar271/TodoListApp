package com.eugenesumaryev.floatingbuttons;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eugenesumaryev on 10/20/17.
 */
public class TodoItem {

    String task;
    String note;
    long created;
    int priority;
    Date dueDate;



    public TodoItem(String _task)
    {
        this(_task, new Date(System.currentTimeMillis()));
    }


    public TodoItem(String _task, Date _duedate) {
        task = _task;
        created = System.currentTimeMillis();
        priority = 0;
        dueDate =  _duedate;
        note = "";


    }

    public String getTask() {
        return task;
    }

    public long getCreated() {
        return created;
    }

    public String getNote() {
        return note;
    }

    public int getPriority() {
        return priority;
    }

    public void setNote(String note) {

        this.note = note;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateString = sdf.format(created);
        return "(" + dateString + ") " + task;
    }
}
