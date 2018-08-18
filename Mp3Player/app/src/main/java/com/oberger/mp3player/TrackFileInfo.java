package com.oberger.mp3player;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by oberger on 4/14/18.
 */

public class TrackFileInfo implements Comparator<TrackFileInfo>, Parcelable {

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

    private TrackFileInfo(final Parcel source) {
        String[] strings = new String[3];
        source.readStringArray(strings);
        this.id = strings[0];
        this.filePath = strings[1];
        this.title = strings[2];
        this.trackNumber = source.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                id,
                filePath,
                title
        });
        dest.writeInt(trackNumber);
    }

    public static final Parcelable.Creator<TrackFileInfo> CREATOR = new Parcelable.Creator<TrackFileInfo>() {
        @Override
        public TrackFileInfo createFromParcel(final Parcel source) {
            return new TrackFileInfo(source);
        }

        @Override
        public TrackFileInfo[] newArray(final int size) {
            return new TrackFileInfo[size];
        }
    };
}
