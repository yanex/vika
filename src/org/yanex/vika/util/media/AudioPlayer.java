package org.yanex.vika.util.media;

import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.api.http.LinkHelper;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;

public class AudioPlayer {

    public static final AudioPlayer instance = new AudioPlayer();

    private AudioListener listener;
    private MainAudioListener mainListener;
    private Player player;
    private boolean seekSupported;

    private int delay;

    public void decVolume() {
        VolumeControl volumeControl = null;
        try {
            volumeControl = (VolumeControl) player.getControl("VolumeControl");
            int volume = volumeControl.getLevel();
            if (volume >= 0) {
                volumeControl.setLevel(Math.max(0, volume - 10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getDuration() {
        if (player == null || player.getState() != Player.PREFETCHED
                && player.getState() != Player.STARTED) {
            return Player.TIME_UNKNOWN;
        }
        try {
            return player.getDuration();
        } catch (Exception e) {
            return Player.TIME_UNKNOWN;
        }
    }

    public long getPosition() {
        if (player == null || player.getState() != Player.PREFETCHED
                && player.getState() != Player.STARTED) {
            return Player.TIME_UNKNOWN;
        }
        try {
            return player.getMediaTime();
        } catch (Exception e) {
            return Player.TIME_UNKNOWN;
        }
    }

    public void incVolume() {
        VolumeControl volumeControl = null;
        try {
            volumeControl = (VolumeControl) player.getControl("VolumeControl");
            int volume = volumeControl.getLevel();
            if (volume >= 0) {
                volumeControl.setLevel(Math.min(100, volume + 10));
            }
        } catch (Exception ignored) {
        }
    }

    public boolean isSeekSupported() {
        return seekSupported;
    }

    public void pause() {
        try {
            player.stop();
            if (listener != null) {
                listener.onAudioEvent(AudioListener.PAUSE, getDuration(), getPosition());
            }
            if (mainListener != null) {
                mainListener.onAudioEvent(AudioListener.PAUSE, getDuration(), getPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(String url, String title, String performer) {
        if (player != null) {
            if (player.getState() == Player.STARTED) {
                try {
                    player.stop();
                } catch (MediaException e) {
                    e.printStackTrace();
                }
            }
            player.close();
        }

        try {
            if (url.startsWith("http")) {
                url = url + LinkHelper.getBBLink(url);
            }

            final Player _player = Manager.createPlayer(url);
            player = _player;

            _player.realize();
            _player.prefetch();
            _player.start();

            if (mainListener != null) {
                mainListener.onTrackChange(title, performer);
            }

            long len = _player.getDuration() / 1000;
            if (len > 0 && listener != null) {
                delay = (int) (len / 200);
                Thread positionThread = createPositionThread(_player);
                positionThread.start();
            }

            try {
                _player.setMediaTime(1);
                seekSupported = true;
            } catch (Exception e) {
                seekSupported = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            player.start();
            if (listener != null) {
                listener.onAudioEvent(AudioListener.PLAY, getDuration(), getPosition());
            }
            if (mainListener != null) {
                mainListener.onAudioEvent(AudioListener.PLAY, getDuration(), getPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seek(long time) {
        if (!isSeekSupported()) {
            return;
        }

        try {
            if (player.getState() != Player.STARTED) {
                player.start();
            }
            long mt = player.setMediaTime(time);
            if (listener != null) {
                listener.onAudioEvent(AudioListener.POSITION, getDuration(), mt);
            }
        } catch (MediaException e) {
            seekSupported = false;
        }
    }

    public void setListener(AudioListener l) {
        if (listener != null) {
            listener.onAudioEvent(AudioListener.GOODBYE, -1, -1);
        }
        listener = l;
    }

    public void setMainListener(MainAudioListener l) {
        if (mainListener != null) {
            mainListener.onAudioEvent(AudioListener.GOODBYE, -1, -1);
        }
        mainListener = l;
    }

    private Thread createPositionThread(final Player player) {
        return new Thread() {

            public void run() {
                try {
                    while (player == AudioPlayer.this.player) {
                        Thread.sleep(delay);
                        final long duration = getDuration();
                        final long position = getPosition();

                        UiApplication.getUiApplication().invokeLater(new Runnable() {
                            public void run() {
                                if (listener != null) {
                                    listener.onAudioEvent(AudioListener.POSITION, duration,
                                            position);
                                }
                                if (mainListener != null) {
                                    mainListener.onAudioEvent(AudioListener.POSITION,
                                            duration, position);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    interrupt();
                }
            }
        };
    }

}
