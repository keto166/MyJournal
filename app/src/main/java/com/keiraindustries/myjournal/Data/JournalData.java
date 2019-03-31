package com.keiraindustries.myjournal.Data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.keiraindustries.myjournal.Activities.BlogEntryListActivity;
import com.keiraindustries.myjournal.Model.Blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
    public JournalManifest deviceManifest;
    public JournalManifest storageManifest;
    public String appPath;                          //path to the working directory of the app
    private ArrayList<Long> toBeDLed;
    private ArrayList<Long> toBeULed;
    public ArrayList<Blog> toBeDeleted;
    private Gson gson;

//    private FirebaseDatabase database;
//    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
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
        gson = new Gson();
        initialized = true;

//        database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReference().child("MyJournal");
//        databaseReference.keepSynced(true);
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                Blog blog = dataSnapshot.getValue(Blog.class);
//                blog.setID(dataSnapshot.getKey());
//
//                blogList.add(blog);
//
////                Collections.reverse(blogList);
////
////                blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this,blogList);
////                recyclerView.setAdapter(blogRecyclerAdapter);
////                blogRecyclerAdapter.notifyDataSetChanged();
//                if (activeActivity != null) {
//                    //TODO fix this: activeActivity.update();
//                }
//
//
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        dateFormat = SimpleDateFormat.getDateInstance();
        dateFormat.setTimeZone(TimeZone.getDefault());



        //initialize blogList
        if (blogList == null) {blogList = new ArrayList<>();}
        if (storageManifest == null) {storageManifest = new JournalManifest();}
        if (deviceManifest == null) {deviceManifest = new JournalManifest();}
        File devManifestFile = new File(getDevManifestPath());
        if (devManifestFile.exists()) {
            loadManifest(deviceManifest, getDevManifestPath());
        }
        for (Long l : deviceManifest.entryMap.keySet()) {
            //Load blog
            blogList.add(loadBlog(getNotesDirectory() + "/" + l.toString()));
        }

        toBeDeleted = new ArrayList<>();

    }

    public List<Blog> getBlogList() {
        return blogList;
    }

    public Blog loadBlog(String path) {
        Blog tempBlog = null;
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            tempBlog = gson.fromJson((String)in.readObject(),Blog.class);
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempBlog;
    }

    public void saveBlog(Blog blog) {
        String path = getNotesDirectory() + "/" + blog.getEntryDate();
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gson.toJson(blog));
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        deviceManifest.entryMap.put(blog.getEntryDate(),blog.getLastModDate());
        if (!blogList.contains(blog)) { blogList.add(blog);}
        saveManifest(deviceManifest,getDevManifestPath());


//OLD        DatabaseReference savePost;
//        if (blog.getID() != null) {
//            savePost = databaseReference.child(blog.getID());
//        } else {
//            savePost = databaseReference.push();
//        }
//        Map<String, String> dataToSave = new HashMap<>();
//        dataToSave.put("title", blog.getTitle());
//        dataToSave.put("desc", blog.getEntryText());
//        dataToSave.put("timestamp", String.valueOf(blog.getEntryDate()));
//        dataToSave.put("hashtags", blog.getHashtags());
//
//        savePost.setValue(dataToSave);
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void removeEntry(Blog blog) {
        if (toBeDeleted == null) { toBeDeleted = new ArrayList<>();}
        toBeDeleted.add(blog);
    }

    public void setActiveActivity(BlogEntryListActivity activeActivity) {
        this.activeActivity = activeActivity;
        if (activeActivity != null) {
            appPath = activeActivity.getFilesDir() + "/manifest";
            File file = new File(appPath);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    //TODO report ERROR
                }
            }
        }
    }

    public void saveManifest(JournalManifest manifest, String path) {
        try {
            FileOutputStream fileOut =new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(manifest);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void loadManifest(JournalManifest manifest, String path) {
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            JournalManifest tempManifest = (JournalManifest)in.readObject();
            manifest.copyManifest(tempManifest);
            in.close();
            fileIn.close();
            Toast.makeText(activeActivity, "Manifest loaded", Toast.LENGTH_SHORT).show();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public void syncWithStorage() {

        //todo check storage manifest exists on firebase

        //getting storage manifest
        try {
            mStorageRef = FirebaseStorage.getInstance().getReference().child("manifest");
            Uri path = Uri.fromFile(new File(getStorManifestPath()));
            mStorageRef.getFile(path)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            loadManifest(storageManifest,getStorManifestPath());
                            syncContinue();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            storageManifest = new JournalManifest();
                            syncContinue();
                        }
                    });
        } catch (Exception e) {

        }



    }

    private void syncContinue() {

        //loading or creating device manifest
        if (deviceManifest == null) {
            File devManifestFile = new File(getDevManifestPath());
            if (devManifestFile.exists()) {
                loadManifest(deviceManifest, getDevManifestPath());
            } else {
                deviceManifest = new JournalManifest();
            }
        }

        //comparing manifests to see what needs to be downloaded from storage
        if (toBeDLed == null) {toBeDLed = new ArrayList<>();} else {toBeDLed.clear();}
        if (toBeULed == null) {toBeULed = new ArrayList<>();} else {toBeULed.clear();}
        if (toBeDeleted == null) {toBeDeleted = new ArrayList<>();}

        for (Long l : deviceManifest.entryMap.keySet()) {
            if (storageManifest.entryMap.containsKey(l)) {
                if (deviceManifest.entryMap.get(l) < storageManifest.entryMap.get(l)) {
                    toBeDLed.add(l);
                } else if (deviceManifest.entryMap.get(l) > storageManifest.entryMap.get(l)) {
                    toBeULed.add(l);
                }
            } else {
                toBeULed.add(l);
            }
        }

        for (Long l : storageManifest.entryMap.keySet()) {
            if (!deviceManifest.entryMap.containsKey(l)) {
                toBeDLed.add(l);
            }
        }

        //Deleting entries

        for (Blog blog : toBeDeleted) {
            final Blog fblog = blog;
            File file = new File(getNotesDirectory()+"/"+Long.valueOf(blog.getEntryDate()).toString());
            file.delete();

            if (deviceManifest.entryMap.containsKey(blog.getEntryDate()))
                    {deviceManifest.entryMap.remove(blog.getEntryDate());}
            if (storageManifest.entryMap.containsKey(blog.getEntryDate()))
                {storageManifest.entryMap.remove(blog.getEntryDate());
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("Entries")
                            .child(Long.valueOf(blog.getEntryDate()).toString());
                    mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activeActivity, "delete failed", Toast.LENGTH_SHORT).show();
                        }
                    });}
            if (toBeDLed.contains(blog.getEntryDate())) {toBeDLed.remove(blog.getEntryDate());}
            if (toBeULed.contains(blog.getEntryDate())) {toBeULed.remove(blog.getEntryDate());}
            removeFromBlogList(fblog);



        }
        toBeDeleted.clear();

        //Downloading

        for (Long l : toBeDLed) {
            try {
                final Long fl = l;
                mStorageRef = FirebaseStorage.getInstance().getReference().child("Entries").child(l.toString());
                Uri path = Uri.fromFile(new File(getNotesDirectory()+"/"+l.toString()));
                mStorageRef.getFile(path)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                addToBlogList(fl);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });

            } catch (Exception e) {

            }
        }
        toBeDLed.clear();
        saveManifest(deviceManifest,getDevManifestPath());

        //Uploading
        for (Long l : toBeULed) {
            try {
                mStorageRef = FirebaseStorage.getInstance().getReference().child("Entries").child(l.toString());
                Uri path = Uri.fromFile(new File(getNotesDirectory()+"/" +l.toString()));
                mStorageRef.putFile(path)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Toast.makeText(activeActivity, "done", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(activeActivity, "UL failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                storageManifest.entryMap.put(l,deviceManifest.entryMap.get(l));
            } catch (Exception e) {

            }

        }
        toBeULed.clear();
        //Uploading the storage manifest
        try {
            saveManifest(storageManifest,getStorManifestPath());

            mStorageRef = FirebaseStorage.getInstance().getReference().child("manifest");
            Uri path = Uri.fromFile(new File(getStorManifestPath()));
            mStorageRef.putFile(path);
        } catch (Exception e) {

        }


        //Update caller
        //TODO activeActivity.update();

    }

    private synchronized void addToBlogList(Long l) {
        deviceManifest.entryMap.put(l,storageManifest.entryMap.get(l));
        blogList.add(loadBlog(getNotesDirectory()+"/"+l.toString()));
        if (activeActivity != null) {
            activeActivity.update();
        }
    }

    private synchronized void removeFromBlogList(Blog blog) {

        if (blogList.contains(blog)) {blogList.remove(blog);}
        if (activeActivity != null) {
            activeActivity.update();
        }
    }

    public String getNotesDirectory() {
        File file = new File(appPath + "/Notes");
        if (!file.exists()) {
            if (!file.mkdir()) {
                //TODO report ERROR
            }
        }
        return file.getAbsolutePath();
    }

    public String getManifestDirectory() {
        File file = new File(appPath + "/Manifests");
        if (!file.exists()) {
            if (!file.mkdir()) {
                //TODO report ERROR
            }
        }
        return file.getAbsolutePath();
    }

    public String getDevManifestPath() {
        String temp = getManifestDirectory()+"/device_manifest.ser";
        return temp;
    }

    public String getStorManifestPath() {
        String temp = getManifestDirectory()+"/storage_manifest.ser";
        return temp;
    }
}
