package com.keiraindustries.myjournal.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by keira on 2/3/19.
 */

public class JournalManifest implements Serializable {
    public HashMap<Long, Long> entryMap;
    public ArrayList<Long> toBeDeleted;

    public JournalManifest() {
        entryMap = new HashMap<>();
        toBeDeleted = new ArrayList<>();
    }

    public void copyManifest(JournalManifest manifest) {
        this.entryMap = manifest.entryMap;
        this.toBeDeleted = manifest.toBeDeleted;
    }
}
