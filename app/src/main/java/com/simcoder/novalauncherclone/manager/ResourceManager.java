package com.simcoder.novalauncherclone.manager;

import com.simcoder.novalauncherclone.model.SongModel;
import com.simcoder.novalauncherclone.services.MusicService;

import java.util.ArrayList;

public class ResourceManager {
    public static ResourceManager resourceManager;

    public ArrayList<SongModel> songFiles = new ArrayList<>();

    public MusicService musicSvc;

    public static ResourceManager getInstance() {
        if (resourceManager == null)
            resourceManager = new ResourceManager();

        return resourceManager;
    }

    public ResourceManager() {
    }
}
