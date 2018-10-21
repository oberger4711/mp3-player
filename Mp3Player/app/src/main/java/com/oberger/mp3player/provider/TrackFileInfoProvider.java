package com.oberger.mp3player.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
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

    public static List<TrackFileInfo> queryAllTracks(final Context context) {
        return queryAlbumTracks(context, -1);
    }

    public static List<TrackFileInfo> queryAlbumTracks(final Context context, final int albumIdSelection) {
        List<TrackFileInfo> tracks = new ArrayList<>();
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.ARTIST};
        String selection;
        String sortOrder;
        if (albumIdSelection >= 0) {
            // Specific album.
            selection = MediaStore.Audio.Media.ALBUM_ID + "=" + Integer.toString(albumIdSelection);
            sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
        }
        else {
            // No specific album.
            selection = "";
            sortOrder = MediaStore.Audio.Media.TITLE;
        }
        String[] selectionArgs = null;
        Cursor audioCursor = context.getContentResolver().query(externalUri, proj, selection, selectionArgs, sortOrder);

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                final String musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
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

                        // Artist
                        final int colArtist = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                        final String artistName = audioCursor.getString(colArtist);

                        // Check if in music directory.
                        if (fileName.startsWith(musicDir)) {
                            fileInfo = new TrackFileInfo(id, fileName, title, artistName);
                        }
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
