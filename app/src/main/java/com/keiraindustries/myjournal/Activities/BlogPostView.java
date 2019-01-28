package com.keiraindustries.myjournal.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.keiraindustries.myjournal.Data.JournalData;
import com.keiraindustries.myjournal.Model.Blog;
import com.keiraindustries.myjournal.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class BlogPostView extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {


    private int blogPos;
    private Blog blog;
    private EditText title;
    private EditText entry;
    private TextView date;
    private TextView hashTagView;
    private DatePickerDialog startTime;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

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
        } else {
            blog = JournalData.getInstance().getBlogList().get(blogPos);
        }
        title.setText(blog.getTitle());
        entry.setText(blog.getEntryText());
        date.setText(JournalData.getInstance().getDateFormat().format(new Date(blog.getEntryDate())));
        hashTagView.setText(blog.getHashtags());

        startTime = new DatePickerDialog(this, BlogPostView.this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//        startTime = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        Calendar newDate = Calendar.getInstance();
//                        newDate.set(year, monthOfYear, dayOfMonth);
//            }
//
//        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
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
                addHashTags();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO Save blog changes
        blog.setEntryText(entry.getText().toString());
        blog.setTitle(title.getText().toString());
        try {
            Date d = JournalData.getInstance().getDateFormat().parse("15134510");
            blog.setEntryDate(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JournalData.getInstance().saveBlog(blog);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void addHashTags() {
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.hash_tags_popup, null);



        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        final EditText newHashTag = (EditText) view.findViewById(R.id.et_ht_new);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                blog.setHashtags(newHashTag.getText().toString());
                hashTagView.setText(blog.getHashtags());
            }
        });


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
        Date d = new Date(year-1900,month, dayOfMonth);
        d.getTime();
        blog.setEntryDate(d.getTime());
        date.setText(JournalData.getInstance().getDateFormat().format(new Date(blog.getEntryDate())));

    }

}
