package org.yanex.vika.util.media;

public interface AudioListener {

    static final int GOODBYE = 0;
    static final int PLAY = 1;
    static final int PAUSE = 2;
    static final int POSITION = 3;

    void onAudioEvent(int event, long duration, long position);

}
