package com.oberger.mp3player;

import java.util.Comparator;

/**
 * Created by oberger on 4/14/18.
 */

public class TrackFileInfo implements Comparator<TrackFileInfo> {

    private final String id;
    private final String filePath;
    private final String title;
    private int trackNumber;

    public TrackFileInfo(final String id, final String filePath, final String title) {
        this.id = id;
        this.filePath = filePath;
        this.title = title;
        this.trackNumber = -1;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    @Override
    public int compare(TrackFileInfo o1, TrackFileInfo o2) {
        if (o1.trackNumber < o2.trackNumber) {
            return -1;
        }
        else if (o2.trackNumber == o2.trackNumber) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  TrackFileInfo) {
            TrackFileInfo other = (TrackFileInfo) obj;
            return other.id.equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
