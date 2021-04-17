package com.example.myfiretodo.Viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfiretodo.R;

public class TaskViewholder extends RecyclerView.ViewHolder {

    View mView;

    public TaskViewholder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setTitle(String title) {
        TextView textViewTitle = mView.findViewById(R.id.tvSTask);
        textViewTitle.setText(title);
    }

    public void setDiscription(String desc) {
        TextView textViewDesc = mView.findViewById(R.id.tvSDesc);
        textViewDesc.setText(desc);
    }

    public void setDate(String date) {
        TextView textViewDate = mView.findViewById(R.id.tvDate);
        textViewDate.setText(date);
    }

}
