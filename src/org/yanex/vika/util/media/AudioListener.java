package org.yanex.vika.util.media;

public interface AudioListener {

    int GOODBYE = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int POSITION = 3;

    void onAudioEvent(int event, long duration, long position);

}
