package com.example.cscan.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cscan.R;
import com.example.cscan.activity.GroupDocumentActivity;
import com.example.cscan.activity.MainActivity;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.Documents;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.Images;
import com.example.cscan.service.getListImageCallBack;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    public Activity activity;

    public List<Documents> List;


    public DocumentAdapter(Activity activity2, List<Documents> List2) {
        this.activity = activity2;
        this.List = List2;
    }

    public void filterList(List<Documents> List2) {
        this.List = List2;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.group_item_list, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,  final int i) {
        viewHolder.iv_group_folder_img.setImageResource(R.drawable.ic_folder_fill);
        viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
        viewHolder.iv_group_first_img.setVisibility(View.GONE);
        viewHolder.tv_group_name.setText(List.get(i).getDocumentName());
        viewHolder.tv_group_date.setText(List.get(i).getDate());
        viewHolder.rl_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).clickOnListItem(((Documents) List.get(i)));
            }
        });
        viewHolder.iv_group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder((activity));
                builder.setTitle("Enter PIN");

                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredPIN = input.getText().toString();
                        // Compare enteredPIN with the correct PIN
                        String correctPIN = Constant.user_current.getPin(); // Replace with your actual correct PIN

                        if (enteredPIN.equals(correctPIN)) {

                            ((MainActivity) activity).clickOnListMore(List.get(i));
                        } else {
                            // Show an error message
                            Toast.makeText(activity, "Sai mã pin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_group_first_img;
        ImageView iv_group_more;
        ImageView iv_group_folder_img;
        RelativeLayout rl_group;
        TextView tv_group_date;
        TextView tv_group_name;

        public ViewHolder(View view) {
            super(view);
            rl_group = (RelativeLayout) view.findViewById(R.id.rl_group);
            iv_group_first_img = (ImageView) view.findViewById(R.id.iv_group_first_img);
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            tv_group_date = (TextView) view.findViewById(R.id.tv_group_date);
            iv_group_more = (ImageView) view.findViewById(R.id.iv_group_more);
            iv_group_folder_img = (ImageView) view.findViewById(R.id.iv_group_folder_img);
        }
    }
}
