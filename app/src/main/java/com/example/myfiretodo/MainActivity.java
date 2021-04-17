package com.example.myfiretodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfiretodo.Models.Todomodel;
import com.example.myfiretodo.Viewholder.TaskViewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userId;
    private FloatingActionButton mFloatingActionButton;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private TextView mTextViewDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Custom Appbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        mFloatingActionButton = findViewById(R.id.myFab);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("task").child(userId);
        mProgressDialog = new ProgressDialog(this);
        mRecyclerView = findViewById(R.id.notesRecycler);
        mTextViewDate = findViewById(R.id.todayDate);

        String todayDate = DateFormat.getDateInstance().format(new Date());
        mTextViewDate.setText(todayDate);


        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
                //Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Adding Task
    private void addTask() {

        //Creating AlertBuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.newtask_dialog, null);
        builder.setView(view);

        //assigning builder to alertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        final EditText editTextTitle = view.findViewById(R.id.etTask);
        final EditText editTextDescription = view.findViewById(R.id.etDesc);
        Button buttonSave = view.findViewById(R.id.btnSave);
        Button buttonCancel = view.findViewById(R.id.btnCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();

                String taskId = mDatabaseReference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (title.isEmpty()) {
                    editTextTitle.setError("task  is Required");
                } else if (description.isEmpty()) {
                    editTextDescription.setError("Description is Required");
                } else {
                    mProgressDialog.setMessage("Adding Your Task");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    Todomodel todomodel = new Todomodel(title, description, taskId, date);
                    assert taskId != null;
                    mDatabaseReference.child(taskId).setValue(todomodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                alertDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();


    }

    //Firebase RecyclerView For showing Data
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Todomodel> options = new FirebaseRecyclerOptions.Builder<Todomodel>()
                .setQuery(mDatabaseReference, Todomodel.class)
                .build();

        FirebaseRecyclerAdapter<Todomodel, TaskViewholder> adapter = new FirebaseRecyclerAdapter<Todomodel, TaskViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewholder holder, int position, @NonNull Todomodel model) {

                holder.setDate(model.getDate());
                holder.setTitle(model.getTitle());
                holder.setDiscription(model.getDescription());

            }

            @NonNull
            @Override
            public TaskViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_task, parent, false);

                return new TaskViewholder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    //For Menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}