package com.keiraindustries.myjournal.Data;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Created by keira on 4/3/19.
 */

public class FileUploader extends FileDownloader {

    public FileUploader() {
        super();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

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
            sftpChannel.put(sourceFilePath, destinationFilePath);

            sftpChannel.exit();
            session.disconnect();
            //TODO respond to JournalData maybe?
        } catch (Exception e) {
            Log.e("Error", e.getMessage() + " (FileULer)");
            Log.e("Error", fileName);
            Log.e("Error", fileType);
        }


    }

    @Override
    protected void buildPaths() {
        if (fileType.equals(VALUE_MANIFEST)) {
            destinationFilePath = MANIFEST_DL_PATH + "/" + MANIFEST_DL_NAME;
            sourceFilePath = JournalData.getInstance().getStorManifestPath();
        } else if (fileType.equals(VALUE_NOTE)) {
            destinationFilePath = BLOG_DL_PATH + "/" + fileName;
            sourceFilePath = JournalData.getInstance().getNotesDirectory() + "/" + fileName;
        } else {

            //TODO Throw an error or a chair or something!!
        }
    }
}
