package com.oberger.mp3player;

import java.util.List;

public interface QueueListener {
    void changeQueue(final List<TrackFileInfo> queue);
}
