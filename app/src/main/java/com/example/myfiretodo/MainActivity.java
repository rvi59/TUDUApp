package com.example.myfiretodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userId;
    private FloatingActionButton mFloatingActionButton;
    private DatabaseReference mDatabaseReference;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private TextView mTextViewDate, mTextViewQuotes;


    private String key = "";
    private String title = "";
    private String description = "";


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
        mTextViewQuotes = findViewById(R.id.myQuotes);
        mTextViewQuotes.setSelected(true);

        List<String> myQuotes = new ArrayList<>();

        myQuotes.add("The worth of a book is to be measured by what you can carry away from it.");
        myQuotes.add("I thank you for nothing, because I understand nothing.");
        myQuotes.add("Anger is the ultimate destroyer of your own peace of mind.");
        myQuotes.add("Children really brighten up a household. They never turn the lights off.");
        myQuotes.add("Appreciate those early influences and what they've done for you.");
        myQuotes.add("Emotional empathy is what motivates us to help others.");
        myQuotes.add("Make the decision, make another. Remake one past, you cannot.");
        myQuotes.add("We must dare to think unthinkable thoughts.");
        myQuotes.add("If you want to see a rainbow you have to learn to see the rain.");
        myQuotes.add("Reading without reflecting is like eating without digesting.");

        Random random = new Random();

        int randomItem = random.nextInt(myQuotes.size());
        String randomElement = myQuotes.get(randomItem);

        mTextViewQuotes.setText(randomElement);


        String todayDate = DateFormat.getDateInstance().format(new Date());
        mTextViewDate.setText(todayDate);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

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


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        title = model.getTitle();
                        description = model.getDescription();
                        //Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        updateTask();
                    }
                });
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


    //Updating Task
    private void updateTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_task, null);
        builder.setView(view);

        //assigning builder to alertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);

        EditText uTask = view.findViewById(R.id.etUTask);
        EditText uDesc = view.findViewById(R.id.etUDesc);

        mProgressDialog.setMessage("Updating Your Task");
        mProgressDialog.setCanceledOnTouchOutside(true);
        //mProgressDialog.show();

        uTask.setText(title);
        uTask.setSelection(title.length());

        uDesc.setText(description);
        uDesc.setSelection(description.length());

        Button buttonDel = view.findViewById(R.id.btnDelete);
        Button buttonUpdate = view.findViewById(R.id.btnUpdate);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = uTask.getText().toString().trim();
                description = uDesc.getText().toString().trim();
                String date = DateFormat.getDateInstance().format(new Date());

                Todomodel todomodel = new Todomodel(title, description, date);
                mProgressDialog.show();
                mDatabaseReference.child(key).setValue(todomodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Task Updated", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            alertDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Updation Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        mProgressDialog.dismiss();
                    }
                });


            }
        });


        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Task Deleted sussfully", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            alertDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Deletion Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        alertDialog.dismiss();
                    }
                });
            }
        });


        alertDialog.show();

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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                finish();
        }

        return super.onOptionsItemSelected(item);
    }


}