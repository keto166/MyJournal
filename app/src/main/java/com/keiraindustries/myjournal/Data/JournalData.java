package com.keiraindustries.myjournal.Data;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keiraindustries.myjournal.Activities.BlogEntryListActivity;
import com.keiraindustries.myjournal.Activities.BlogPostView;
import com.keiraindustries.myjournal.MainActivity;
import com.keiraindustries.myjournal.Model.Blog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Keira on 1/8/19.
 */



public class JournalData {
    private static final JournalData ourInstance = new JournalData();
    public static final String BLOGIDNUM = "Blog ID Number";
    private List<Blog> blogList;
    public DateFormat dateFormat;
    private BlogEntryListActivity activeActivity;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean initialized;

    public static JournalData getInstance() {
        return ourInstance;
    }

    private JournalData() {
        initialized = false;
        blogList = new ArrayList<>();

    }

    public void initialize() {
        if (initialized) {return;}
        initialized = true;

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("MyJournal");
        databaseReference.keepSynced(true);


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Blog blog = dataSnapshot.getValue(Blog.class);
                blog.setID(dataSnapshot.getKey());

                blogList.add(blog);

//                Collections.reverse(blogList);
//
//                blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this,blogList);
//                recyclerView.setAdapter(blogRecyclerAdapter);
//                blogRecyclerAdapter.notifyDataSetChanged();
                if (activeActivity != null) {
                    activeActivity.update();
                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        dateFormat = SimpleDateFormat.getDateInstance();
        dateFormat.setTimeZone(TimeZone.getDefault());
    }

    public List<Blog> getBlogList() {
        return blogList;
    }

    public void saveBlog(Blog blog) {

        DatabaseReference savePost;
        if (blog.getID() != null) {
            savePost = databaseReference.child(blog.getID());
        } else {
            savePost = databaseReference.push();
        }
        Map<String, String> dataToSave = new HashMap<>();
        dataToSave.put("title", blog.getTitle());
        dataToSave.put("desc", blog.getEntryText());
        dataToSave.put("timestamp", String.valueOf(blog.getEntryDate()));
        dataToSave.put("hashtags", blog.getHashtags());

        savePost.setValue(dataToSave);
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void removeEntry(Blog blog) {
        database.getReference().child("MyJournal").child(blog.getID()).removeValue();
        //TODO remove from bloglist
        blogList.remove(blog);
    }

    public void setActiveActivity(BlogEntryListActivity activeActivity) {
        this.activeActivity = activeActivity;
    }
}
