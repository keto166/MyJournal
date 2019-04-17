package com.keiraindustries.myjournal.Data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
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


    private static JournalData ourInstance;
    public static final String BLOGIDNUM = "Blog ID Number";
    private List<Blog> blogList;
    public DateFormat dateFormat;
    private BlogEntryListActivity bla;
    public JournalManifest deviceManifest;
    public JournalManifest storageManifest;
    public String appPath;                          //path to the working directory of the app
    private ArrayList<Long> toBeDLed;
    private ArrayList<Long> toBeULed;
    public ArrayList<Blog> toBeDeleted;
    private Gson gson;
    private boolean initialized;

    public static JournalData getInstance() {
        if (ourInstance == null) {
            ourInstance = new JournalData();
            ourInstance.initialize();
        }
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

        dateFormat = SimpleDateFormat.getDateInstance();
        dateFormat.setTimeZone(TimeZone.getDefault());



        //initialize blogList
        if (blogList == null) {blogList = new ArrayList<>();}
        if (storageManifest == null) {storageManifest = new JournalManifest();}
        if (deviceManifest == null) {deviceManifest = new JournalManifest();}
        String temp = getDevManifestPath();
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

    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void removeEntry(Blog blog) {
        if (toBeDeleted == null) { toBeDeleted = new ArrayList<>();}
        toBeDeleted.add(blog);
    }

    public void setFilesDir(String filesDir) {
        appPath = filesDir + "/manifest";
        File file = new File(appPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                //TODO report ERROR
            }

        }
    }

    public void saveManifest(JournalManifest manifest, String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(manifest);
            out.close();
            fileOut.close();
            bla.shout("Manifest saved");
        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "Saving Manifest");
            Log.e("Error", path);
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
            bla.shout("Manifest loaded");
        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "Loading Manifest");
            Log.e("Error", path);
        }
    }

    public void syncWithStorage(Context context) {

        //getting storage manifest
        try {
            Intent i = new Intent(context, FileChecker.class);
            i.putExtra(FileDownloader.KEY_FILE_NAME, "manifest");
            i.putExtra(FileDownloader.KEY_FILE_TYPE, FileDownloader.VALUE_MANIFEST);
            context.startService(i);
        } catch (Exception e) {

        }



    }

    public void downloadStorageManifest(Context context) {
        Intent i = new Intent(context, FileDownloader.class);
        i.putExtra(FileDownloader.KEY_FILE_NAME, "manifest");
        i.putExtra(FileDownloader.KEY_FILE_TYPE, FileDownloader.VALUE_MANIFEST);
        context.startService(i);
    }

    public void loadStorageManifest(Context context) {
        loadManifest(storageManifest,getStorManifestPath());
        syncContinue(context);
    }

    public void newStorageManifest(Context context) {
        storageManifest = new JournalManifest();
        syncContinue(context);
    }

    private void syncContinue(Context context) {

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
        //TODO update this section

        for (Blog blog : toBeDeleted) {
//            final Blog fblog = blog;
//            File file = new File(getNotesDirectory()+"/"+Long.valueOf(blog.getEntryDate()).toString());
//            file.delete();
//
//            if (deviceManifest.entryMap.containsKey(blog.getEntryDate()))
//                    {deviceManifest.entryMap.remove(blog.getEntryDate());}
//            if (storageManifest.entryMap.containsKey(blog.getEntryDate()))
//                {storageManifest.entryMap.remove(blog.getEntryDate());
//                    mStorageRef = FirebaseStorage.getInstance().getReference().child("Entries")
//                            .child(Long.valueOf(blog.getEntryDate()).toString());
//                    mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(bla, "delete failed", Toast.LENGTH_SHORT).show();
//                        }
//                    });}
//            if (toBeDLed.contains(blog.getEntryDate())) {toBeDLed.remove(blog.getEntryDate());}
//            if (toBeULed.contains(blog.getEntryDate())) {toBeULed.remove(blog.getEntryDate());}
//            removeFromBlogList(fblog);



        }
        toBeDeleted.clear();

        //Downloading blog entries

        for (Long l : toBeDLed) {
            try {
                Intent i = new Intent(context, FileDownloader.class);
                i.putExtra(FileDownloader.KEY_FILE_NAME, l.toString());
                i.putExtra(FileDownloader.KEY_FILE_TYPE, FileDownloader.VALUE_NOTE);
                context.startService(i);

            } catch (Exception e) {

            }
        }
        toBeDLed.clear();
        saveManifest(deviceManifest,getDevManifestPath());

        //Uploading
        for (Long l : toBeULed) {
            try {
                Intent i = new Intent(context, FileUploader.class);
                i.putExtra(FileDownloader.KEY_FILE_NAME, l.toString());
                i.putExtra(FileDownloader.KEY_FILE_TYPE, FileDownloader.VALUE_NOTE);
                context.startService(i);
                storageManifest.entryMap.put(l,deviceManifest.entryMap.get(l));
            } catch (Exception e) {

            }

        }
        toBeULed.clear();
        //Uploading the storage manifest
        try {
            saveManifest(storageManifest,getStorManifestPath());

            Intent i = new Intent(context, FileUploader.class);
            i.putExtra(FileDownloader.KEY_FILE_NAME, "manifest");
            i.putExtra(FileDownloader.KEY_FILE_TYPE, FileDownloader.VALUE_MANIFEST);
            context.startService(i);
        } catch (Exception e) {

        }
    }

    protected synchronized void addToBlogList(Long l) {
        deviceManifest.entryMap.put(l,storageManifest.entryMap.get(l));
        blogList.add(loadBlog(getNotesDirectory()+"/"+l.toString()));
        if (bla != null) {
            bla.update();
        }
    }

    private synchronized void removeFromBlogList(Blog blog) {

        if (blogList.contains(blog)) {blogList.remove(blog);}
        if (bla != null) {
            bla.update();
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


    public void setBLA(BlogEntryListActivity bla) {
        this.bla = bla;
    }

}
