package com.keiraindustries.myjournal.Data;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


/**
 * Created by keira on 3/30/19.
 */

public class FileDownloader extends IntentService {


    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_FILE_TYPE = "file_type";
    public static final String VALUE_MANIFEST = "manifest";
    public static final String VALUE_NOTE = "note";
    protected static final String MANIFEST_DL_PATH = "keiraindustries.com/MyJournal";
    protected static final String MANIFEST_DL_NAME = "manifest.ser";
    protected static final String BLOG_DL_PATH = "keiraindustries.com/MyJournal/Notes";
    protected static final String LOGIN_HOST = "keiraindustries.com";
    protected static final String LOGIN_ID = "keira166";
    protected static final String LOGIN_PW = "GTFrde112!!@";

    protected String destinationFilePath = null;
    protected String sourceFilePath = null;
    protected String fileName = null;
    protected String fileType = null;

    public FileDownloader() {
        super("myIntentService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO DONE Switch to Jsch methods


        try {
            JSch jsch = new JSch();
            Session session;

            fileType = intent.getExtras().getString(KEY_FILE_TYPE);
            fileName = intent.getExtras().getString(KEY_FILE_NAME);

            buildPaths();

            session = jsch.getSession(LOGIN_ID, LOGIN_HOST, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(LOGIN_PW);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(sourceFilePath, destinationFilePath);
            sftpChannel.exit();
            session.disconnect();
            //TODO Respond to JournalData
            if (fileType.equals(VALUE_MANIFEST)) {
                JournalData.getInstance().loadStorageManifest(getApplicationContext());
            } else if (fileType.equals(VALUE_NOTE)) {
                JournalData.getInstance().addToBlogList(Long.parseLong(fileName));
            } else {
                //TODO Throw something at a chair
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "FileDLer");
            Log.e("Error", fileName);
            Log.e("Error", fileType);
        }





    }

    protected void buildPaths() {
        if (fileType.equals(VALUE_MANIFEST)) {
            destinationFilePath = JournalData.getInstance().getStorManifestPath();
            sourceFilePath = MANIFEST_DL_PATH + "/" + MANIFEST_DL_NAME;
        } else if (fileType.equals(VALUE_NOTE)) {
            destinationFilePath = JournalData.getInstance().getNotesDirectory() + "/" + fileName;
            sourceFilePath = BLOG_DL_PATH + "/" + fileName;
        } else {

            //TODO Throw an error or a chair or something!!
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
