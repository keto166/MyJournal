package com.keiraindustries.myjournal.Data;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by keira on 3/30/19.
 */

public class FileChecker extends FileDownloader {

    public boolean fileFound;

    //TODO This

    public FileChecker() {
        super();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        fileFound = false;
        try {
            JSch jsch = new JSch();
            Session session;


            fileName = intent.getExtras().getString(KEY_FILE_NAME);
            fileType = intent.getExtras().getString(KEY_FILE_TYPE);

            buildPaths();

            session = jsch.getSession(LOGIN_ID, LOGIN_HOST, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(LOGIN_PW);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            Vector vectorContents = sftpChannel.ls(sourceFilePath);
            if (vectorContents != null) {
                ArrayList<ChannelSftp.LsEntry> contents = new ArrayList<ChannelSftp.LsEntry>(vectorContents);
                for (ChannelSftp.LsEntry entry : contents) {
                    if (entry.getFilename().equals(MANIFEST_DL_NAME)) {
                        //Match found
                        fileFound = true;
                    }
                }
            }

            sftpChannel.exit();
            session.disconnect();
            //TODO Respond to JournalData
            if (fileType.equals(VALUE_MANIFEST)) {
                if (fileFound) {
                    JournalData.getInstance().downloadStorageManifest(getApplicationContext());
                    //TODO JournalData download storage Manifest
                } else {
                    JournalData.getInstance().newStorageManifest(getApplicationContext());
                    //TODO JournalData make new storage Manifest
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage() + "FileChecker");
            Log.e("Error", fileName);
            Log.e("Error", fileType);
        }



    }

    @Override
    protected void buildPaths() {
        if (fileType.equals(VALUE_MANIFEST)) {
            destinationFilePath = JournalData.getInstance().getStorManifestPath();
            sourceFilePath = MANIFEST_DL_PATH;
        } else if (fileType.equals(VALUE_NOTE)) {
            destinationFilePath = JournalData.getInstance().getNotesDirectory() + "/" + fileName;
            sourceFilePath = BLOG_DL_PATH + "/" + fileName;
        } else {

            //TODO Throw an error or a chair or something!!
        }
    }
}
