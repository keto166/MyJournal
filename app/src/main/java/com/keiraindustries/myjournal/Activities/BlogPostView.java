package com.keiraindustries.myjournal.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keiraindustries.myjournal.Data.JournalData;
import com.keiraindustries.myjournal.Model.Blog;
import com.keiraindustries.myjournal.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class BlogPostView extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private int blogPos;
    private long modifyDate;
    private Blog blog;
    private EditText title;
    private EditText entry;
    private TextView date;
    private TextView hashTagView;
    private DatePickerDialog startTime;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private boolean isDateModified;
    private boolean isEntryModified;

    //TODO implement double tap edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_view);

        title = findViewById(R.id.et_title);
        entry = findViewById(R.id.et_entry);
        date = findViewById(R.id.tv_date);
        hashTagView = findViewById(R.id.tv_ht_list);


        blogPos = getIntent().getExtras().getInt(JournalData.BLOGIDNUM);
        if (blogPos == -1) {
            blog = new Blog();
            modifyDate = System.currentTimeMillis();
        } else {
            blog = JournalData.getInstance().getBlogList().get(blogPos);
        }
        title.setText(blog.getTitle());
        entry.setText(blog.getEntryText());
        date.setText(JournalData.getInstance().getDateFormat().format(new Date(blog.getEntryDate())));

        Calendar myCal = Calendar.getInstance();

        startTime = new DatePickerDialog(this, BlogPostView.this,
                myCal.get(Calendar.YEAR),
                myCal.get(Calendar.MONTH),
                myCal.get(Calendar.DAY_OF_MONTH));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteEntry();
                return false;
            }
        });

        hashTagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHashTags(v.getRootView());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO Save blog changes
        blog.setEntryText(entry.getText().toString());
        blog.setTitle(title.getText().toString());
        if (isDateModified) {

        } else {
            if (blog.getEntryDate() == 0) {
                blog.setEntryDate(modifyDate);
            } else {
                blog.setLastModDate(modifyDate);
            }
        }
        JournalData.getInstance().saveBlog(blog);
    }


    public void addHashTags(final View parentView) {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.hash_tags_popup, null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        RecyclerView rvHashTags = view.findViewById(R.id.rvHashTagList);
        rvHashTags.setHasFixedSize(true);
        rvHashTags.setLayoutManager(new LinearLayoutManager(parentView.getContext()));

        Button buttonAddHt = view.findViewById(R.id.b_add_ht);
        final EditText etNewHt = view.findViewById(R.id.et_ht_new);

        HTListRVAdapter htListRVAdapter = new HTListRVAdapter(blog.getHashtags());
        rvHashTags.setAdapter(htListRVAdapter);

        buttonAddHt.setOnClickListener(new View.OnClickListener() {
            public HTListRVAdapter adapter;
            @Override
            public void onClick(View v) {
                //Add contents to htlist
                if (!etNewHt.getText().toString().equals("")) {
                    if (!blog.getHashtags().contains(etNewHt.getText().toString())) {
                        blog.getHashtags().add(etNewHt.getText().toString());

                    }
                    etNewHt.setText("");
                    adapter.notifyDataSetChanged();
                }

            }

            public View.OnClickListener init(HTListRVAdapter adapter) {
                this.adapter = adapter;
                return this;
            }
        }.init(htListRVAdapter));

    }

    public void deleteEntry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Delete this entry?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                JournalData.getInstance().removeEntry(blog);
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void pickDate() {
        startTime.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar pickedDay = new GregorianCalendar(year,month, dayOfMonth,
                (int)Math.random()*24,(int)Math.random()*60,(int)Math.random()*60);
        blog.setEntryDate(pickedDay.getTimeInMillis());
        date.setText(JournalData.getInstance().getDateFormat().format(pickedDay.getTimeInMillis()));

    }

    public class HTListRVAdapter extends RecyclerView.Adapter<BlogPostView.HTListRVAdapter.MyViewHolder> {

        private List<String> itemList;

        public HTListRVAdapter(List itemList) {
            this.itemList = itemList;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hash_tag_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.tvHashTag.setText(itemList.get(position));
            //holder.tvHashTag.setText(position);
            holder.bDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

        }



        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView tvHashTag;
            public ImageButton bDelete;

            public MyViewHolder(View itemView) {
                super(itemView);

                tvHashTag = itemView.findViewById(R.id.tvHashTagValue);
                bDelete = itemView.findViewById(R.id.b_delete_hash_tag);

            }

        }


    }


}
