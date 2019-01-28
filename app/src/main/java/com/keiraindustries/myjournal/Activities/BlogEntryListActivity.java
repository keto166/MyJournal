package com.keiraindustries.myjournal.Activities;

import android.content.Intent;
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
import com.keiraindustries.myjournal.Model.Blog;
import com.keiraindustries.myjournal.Data.JournalData;
import com.keiraindustries.myjournal.R;

import java.util.ArrayList;
import java.util.List;

public class BlogEntryListActivity extends AppCompatActivity {

    private BlogEntryListRVAdapter blogEntryListRVAdapter;
    RecyclerView rvBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_entry_list);

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
        JournalData.getInstance().setActiveActivity(this);
        blogEntryListRVAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JournalData.getInstance().setActiveActivity(null);
    }


    public void update() {
        if (blogEntryListRVAdapter != null) {
            blogEntryListRVAdapter.notifyDataSetChanged();
        }
    }
}
