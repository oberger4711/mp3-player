package com.oberger.mp3player.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.oberger.mp3player.AlbumFileInfo;
import com.oberger.mp3player.ArtistFileInfo;
import com.oberger.mp3player.TrackFileInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumFileInfoProvider {

    private AlbumFileInfoProvider() {}

    public static List<AlbumFileInfo> queryAlbums(final Context context, final int artistIdSelection) {
        List<AlbumFileInfo> albums = new ArrayList<>();
        Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = new String[]{
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.ARTIST_ID + "=" + Integer.toString(artistIdSelection);
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor cursor = context.getContentResolver().query(externalUri, proj, selection, null, sortOrder);

        final Set<Integer> albumIds = new HashSet<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        // Artist ID
                        final int colArtistId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
                        final int artistId = cursor.getInt(colArtistId);
                        // Album ID
                        final int colAlbumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                        final int albumId = cursor.getInt(colAlbumId);
                        // Artist
                        final int colArtistName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                        final String artistName = cursor.getString(colArtistName);
                        // Album
                        final int colAlbum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                        final String albumName = cursor.getString(colAlbum);

                        if (!albumIds.contains(albumId)) {
                            // Get only distinct albums.
                            final List<TrackFileInfo> tracks = TrackFileInfoProvider.queryAlbumTracks(context, albumId);
                            albums.add(new AlbumFileInfo(albumId, albumName, artistName, tracks));
                            albumIds.add(albumId);
                            Log.d(ArtistFileInfoProvider.class.getSimpleName(), "Album " + albumName + " with id " + artistId);
                        }
                    } catch (IllegalArgumentException e) {
                        // Could not load a music file.
                        Log.i(TrackFileInfoProvider.class.getSimpleName(), "Skipping an album that could not be parsed.");
                    }
                } while (cursor.moveToNext());
            }
            assert cursor != null;
            cursor.close();
        }
        return albums;
    }
}
