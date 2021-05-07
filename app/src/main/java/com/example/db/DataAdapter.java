package com.example.db;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    Context mContext;
    ArrayList<UserData> dataList;
    DatabaseHelper helper;
    UserData data;

    DataAdapter(Context context, ArrayList<UserData> dataList) {

        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.list_view, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {

        holder.id.setText(String.valueOf(dataList.get(position).getId()));
        holder.name.setText(dataList.get(position).getName());
        holder.phone.setText(dataList.get(position).getPhone());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String items[] = {"Update", "Delete", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Choose Action")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                switch (i) {
                                    case 0:
                                        buildDialog(items[0], position);
                                        dialogInterface.cancel();
                                        break;
                                    case 1:
                                        buildDialog(items[1], position);
                                        dialogInterface.cancel();
                                        break;
                                    case 2 :
                                        dialogInterface.cancel();
                                        break;
                                    default:
                                        Toast.makeText(mContext, "Invalid dialog item", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

    }

    private void buildDialog(String item, int position) {


        if (item.equals("Delete")) {
            helper = new DatabaseHelper(mContext);
            
            if (helper.deleteData(dataList.get(position).getId())) {
                Toast.makeText(mContext, "Data deleted Successfully", Toast.LENGTH_SHORT).show();

                Cursor cursor = helper.getData();
                dataList.clear();
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
               notifyDataSetChanged();
            }
        }
        if (item.equals("Update")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(R.layout.alert_dialog_layout)
                    .setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.show();

            TextInputLayout alert_name = dialog.findViewById(R.id.alert_name);
            TextInputLayout alert_phone = dialog.findViewById(R.id.alert_phone);
            MaterialButton button_modify = dialog.findViewById(R.id.alert_update_button);
            MaterialButton button_cancel = dialog.findViewById(R.id.alert_cancel_button);

            button_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });


            button_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String update_name = alert_name.getEditText().getText().toString();
                    String update_phone = alert_phone.getEditText().getText().toString();
                    if (update_name.equals("")) {
                        alert_name.setError("Empty Name");

                    }
                    if (update_phone.equals("")) {
                        alert_phone.setError("Empty Phone");
                    }
                    if (!update_name.equals("") && !update_phone.equals("")) {

                        helper = new DatabaseHelper(mContext);
                        if (helper.updateData(update_name, update_phone, dataList.get(position).getId())) {
                            Toast.makeText(mContext, "Data updated Successfully", Toast.LENGTH_SHORT).show();

                            Cursor cursor = helper.getData();
                            MainActivity.dataList.clear();
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
                            notifyDataSetChanged();
                            dialog.cancel();
                        } else {
                            Toast.makeText(mContext, "Error updating data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void filter(ArrayList<UserData> filterList) {

        dataList = filterList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, phone;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.text_id);
            name = itemView.findViewById(R.id.text_name);
            phone = itemView.findViewById(R.id.text_phone);
            imageView = itemView.findViewById(R.id.image_edit);
        }
    }
}
