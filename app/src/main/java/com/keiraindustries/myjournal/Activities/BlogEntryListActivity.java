package com.keiraindustries.myjournal.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.keiraindustries.myjournal.Activities.Adapters.BlogEntryListRVAdapter;
import com.keiraindustries.myjournal.Data.JournalData;
import com.keiraindustries.myjournal.R;


public class BlogEntryListActivity extends AppCompatActivity {

    private BlogEntryListRVAdapter blogEntryListRVAdapter;
    RecyclerView rvBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_entry_list);

        JournalData.getInstance().setBLA(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvBlog = findViewById(R.id.rvBlogEntryList);
        rvBlog.setHasFixedSize(true);
        rvBlog.setLayoutManager(new LinearLayoutManager(this));

        blogEntryListRVAdapter = new BlogEntryListRVAdapter(this);
        rvBlog.setAdapter(blogEntryListRVAdapter);

        Toast.makeText(this, "List Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_add_entry:
                Intent intent = new Intent(this, BlogPostView.class);
                intent.putExtra(JournalData.BLOGIDNUM, -1);
                startActivity(intent);
                return true;
            case R.id.action_sync:
                JournalData.getInstance().syncWithStorage(getApplicationContext());
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO Fix this
        JournalData.getInstance().setBLA(this);
        blogEntryListRVAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JournalData.getInstance().setBLA(null);
    }

    public void update() {
        if (blogEntryListRVAdapter != null) {
            blogEntryListRVAdapter.notifyDataSetChanged();
        }
    }

    public void deletePopup(final BlogEntryListRVAdapter.MyViewHolder holder, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        if (JournalData.getInstance().toBeDeleted.contains(holder.blog)) {
            builder.setMessage("Mark to return from delete: " + holder.blog.getTitle()+ "?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    JournalData.getInstance().toBeDeleted.remove(holder.blog);
                    v.setBackgroundColor(Color.argb(0, 0, 0, 0));
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
        } else {
            builder.setMessage("Mark to delete: " + holder.blog.getTitle()+ "?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    JournalData.getInstance().removeEntry(holder.blog);
                    v.setBackgroundColor(Color.argb(125, 125, 60, 60));
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
        }


        AlertDialog alert = builder.create();
        alert.show();
    }

    public void shout(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
