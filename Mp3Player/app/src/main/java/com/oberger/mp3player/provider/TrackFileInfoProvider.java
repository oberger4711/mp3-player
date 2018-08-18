package com.oberger.mp3player.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.oberger.mp3player.TrackFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oberger on 4/14/18.
 */

public class TrackFileInfoProvider {

    private TrackFileInfoProvider() {}

    public static List<TrackFileInfo> queryAlbumTracks(final Context context, final int albumIdSelection) {
        List<TrackFileInfo> tracks = new ArrayList<>();
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK};
        String selection = MediaStore.Audio.Media.ALBUM_ID + "=" + Integer.toString(albumIdSelection);
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
        Cursor audioCursor = context.getContentResolver().query(externalUri, proj, selection, selectionArgs, sortOrder);

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    // Parse necessary meta data.
                    TrackFileInfo fileInfo = null;
                    try {
                        // Title
                        final int colFileTitle = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                        final String title = audioCursor.getString(colFileTitle);
                        // ID
                        final int colId = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                        final String id = audioCursor.getString(colId);
                        // Name
                        final int colFileName = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                        final String fileName = audioCursor.getString(colFileName);

                        fileInfo = new TrackFileInfo(id, fileName, title);
                    } catch (IllegalArgumentException e) {
                        // Could not load a music file.
                        Log.i(TrackFileInfoProvider.class.getSimpleName(), "Skipping a file that could not be parsed.");
                    }
                    if (fileInfo != null) {
                        // Parse optional meta data.
                        try {
                            final int colTrackNumber = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);
                            final int trackNumber = audioCursor.getInt(colTrackNumber);
                            fileInfo.setTrackNumber(trackNumber);
                        } catch (IllegalArgumentException e) {
                            Log.i(TrackFileInfoProvider.class.getSimpleName(), "Skipping optional album name for '" + fileInfo.getTitle() + "'.");
                        }
                        tracks.add(fileInfo);
                        Log.d(TrackFileInfoProvider.class.getSimpleName(), fileInfo.getTitle());
                    }
                }
                while (audioCursor.moveToNext());
            }
            assert audioCursor != null;
            audioCursor.close();
        }
        return tracks;
    }
}
