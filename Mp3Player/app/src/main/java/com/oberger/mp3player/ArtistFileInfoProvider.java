package com.oberger.mp3player;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by oberger on 4/14/18.
 */

public class ArtistFileInfoProvider {

    public static List<ArtistFileInfo> queryAllArtists(final Context context) {
        List<ArtistFileInfo> artists = new ArrayList<>();
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = new String[]{
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ARTIST
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.ARTIST + " ASC";
        Cursor cursor = context.getContentResolver().query(externalUri, proj, selection, selectionArgs, sortOrder);

        Set<Integer> artistIds = new HashSet<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        // ID
                        final int colId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
                        final int id = cursor.getInt(colId);
                        // Artist
                        final int colName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                        final String name = cursor.getString(colName);

                        if (!artistIds.contains(id)) {
                            // Get only distinct artists.
                            artists.add(new ArtistFileInfo(id, name));
                            artistIds.add(id);
                            Log.d(ArtistFileInfoProvider.class.getSimpleName(), "Artist " + name + " with id " + id);
                        }
                    } catch (IllegalArgumentException e) {
                        // Could not load a music file.
                        Log.i(TrackFileInfoProvider.class.getSimpleName(), "Skipping an artist that could not be parsed.");
                    }
                } while (cursor.moveToNext());
            }
            assert cursor != null;
            cursor.close();
        }
        return artists;
    }
}
