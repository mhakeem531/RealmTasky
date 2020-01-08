package com.example.tasky;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.tasky.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.realm.Realm;
import io.realm.RealmResults;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        realm = Realm.getDefaultInstance();

        // RealmResults are "live" views, that are automatically kept up to date, even when changes happen
        // on a background thread. The RealmBaseAdapter will automatically keep track of changes and will
        // automatically refresh when a change is detected.
        RealmResults<Task> tasks = realm.where(Task.class).findAll();

        tasks = tasks.sort("timestamp");
        final TaskAdapter adapter = new TaskAdapter(this, tasks);

        ListView listView = findViewById(R.id.task_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Task task = (Task) adapterView.getAdapter().getItem(i);
                final EditText taskEditText = new EditText(MainActivity.this);
                taskEditText.setText(task.getName());
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edit Task")
                        .setView(taskEditText)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeTaskName(task.getId(), String.valueOf(taskEditText.getText()));
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTask(task.getId());
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText taskEditText = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Add Task")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String note = String.valueOf(taskEditText.getText());
                                if(!(note.trim().isEmpty())){
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
//                                            realm.createObject(Task.class, UUID.randomUUID().toString())
//                                                    .setName(note);

                                            Task task = realm.createObject(Task.class, UUID.randomUUID().toString());
                                            task.setName(note);
                                            task.setTimestamp(System.currentTimeMillis());
                                        }
                                    });
                                }else{
                                    Toast.makeText(getApplicationContext(), "enter a text to your note", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_delete) {
            deleteAllDone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void changeTaskDone(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setDone(!task.isDone());
            }
        });
    }



    private void changeTaskName(final String taskId, final String name) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task task = realm.where(Task.class).equalTo("id", taskId).findFirst();
                task.setName(name);
            }
        });
    }


    private void deleteTask(final String taskId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo("id", taskId)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    private void deleteAllDone() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo("done", true)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }


}
