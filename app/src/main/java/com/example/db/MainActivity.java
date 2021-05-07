package com.example.db;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout user_name;
    private TextInputLayout user_phone;
    private RecyclerView listView;
    private DatabaseHelper helper;
    private Cursor cursor;
    public static ArrayList<UserData> dataList;
    public static DataAdapter adapter;
    UserData data;

    //    StringBuilder stringBuilder = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_name = (TextInputLayout) findViewById(R.id.name);
        user_phone = (TextInputLayout) findViewById(R.id.phone_number);
        listView = (RecyclerView) findViewById(R.id.list_view);
        dataList = new ArrayList<>();
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DataAdapter(this, dataList);
        listView.setAdapter(adapter);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.purple_500));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        MenuItem item = menu.findItem(R.id.search_view);
        android.widget.SearchView searchView = (android.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                filter(s);
                return false;
            }
        });

        return true;
    }

    private void filter(String newText) {

        ArrayList<UserData> filterList = new ArrayList<>();

        for(UserData item : dataList) {

            if(item.getName().toLowerCase().contains(newText.toLowerCase()))
                filterList.add(item);
        }

        if(filterList.isEmpty()) {

            listView.setVisibility(View.GONE);
            Toast.makeText(this, "No records found", Toast.LENGTH_SHORT).show();
        } else {
            listView.setVisibility(View.VISIBLE);
            adapter.filter(filterList);
        }
    }

    public void addData(View view) {

        String name = user_name.getEditText().getText().toString().trim();
        String phone = user_phone.getEditText().getText().toString().trim();

        if (name.equals("")) {
            user_name.setError("Empty username");
        } else {
            user_name.setError("");
        }

        if (phone.equals("")) {
            user_phone.setError("Empty phone number");
        } else {
            user_phone.setError("");
        }


        if (!name.equals("") && !phone.equals("")) {

            helper = new DatabaseHelper(this);
            if (helper.insertData(name, phone)) {
                Toast.makeText(this, "Data inserted Successfully", Toast.LENGTH_SHORT).show();
                user_name.getEditText().setText("");
                user_phone.getEditText().setText("");
            } else {
                user_name.getEditText().setText("");
                user_phone.getEditText().setText("");
            }

        }
    }

    public void getData(View view) {

        helper = new DatabaseHelper(this);
        cursor = helper.getData();
        dataList.clear();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No record found", Toast.LENGTH_SHORT).show();
        } else {

            Log.d("MainActivity", "cursor have the data");
            while (cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String phone = cursor.getString(2);

                data = new UserData();
                data.setId(id);
                data.setName(name);
                data.setPhone(phone);

                dataList.add(data);

            }
            adapter = new DataAdapter(this, dataList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit!")
                .setIcon(R.drawable.exit)
                .setMessage("Do you really want to Exit")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}