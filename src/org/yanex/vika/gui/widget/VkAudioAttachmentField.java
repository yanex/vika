package org.yanex.vika.gui.widget;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.api.item.AudioAttachment;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.ImageButtonField;
import org.yanex.vika.gui.widget.base.SliderField;
import org.yanex.vika.gui.widget.base.SliderField.SliderListener;
import org.yanex.vika.util.media.AudioListener;
import org.yanex.vika.util.media.AudioPlayer;

public class VkAudioAttachmentField extends HorizontalFieldManager implements FieldChangeListener,
        SliderListener, AudioListener {

    private class CustomImageButton extends ImageButtonField {

        private boolean localIsPlaying = false;
        private Bitmap defaultBitmap, focusedBitmap;

        public CustomImageButton() {
            super(VkAudioAttachmentField.PLAY, VkAudioAttachmentField.PLAY.getWidth(),
                    VkAudioAttachmentField.PLAY.getHeight(), 0, VkAudioAttachmentField.BUTTON_THEME,
                    false);
            defaultBitmap = VkAudioAttachmentField.PLAY;
            focusedBitmap = VkAudioAttachmentField.PLAY_HOVER;
        }

        protected void onFocus(int direction) {
            super.onFocus(direction);
            setBitmap(focusedBitmap);
        }

        protected void onUnfocus() {
            super.onUnfocus();
            setBitmap(defaultBitmap);
        }

        public void updateState() {
            if (localIsPlaying != isPlaying) {
                if (isPlaying) {
                    defaultBitmap = VkAudioAttachmentField.PAUSE;
                    focusedBitmap = VkAudioAttachmentField.PAUSE_HOVER;
                } else {
                    defaultBitmap = VkAudioAttachmentField.PLAY;
                    focusedBitmap = VkAudioAttachmentField.PLAY_HOVER;
                }
            }

            if (isFocused()) {
                setBitmap(focusedBitmap);
            } else {
                setBitmap(defaultBitmap);
            }

            localIsPlaying = isPlaying;
        }

    }

    private static final Bitmap PLAY, PLAY_HOVER, PAUSE, PAUSE_HOVER;

    private static final Theme BUTTON_THEME;

    static {
        PLAY = R.instance.getBitmap("Audio/Play.png");
        PLAY_HOVER = R.instance.getBitmap("Audio/PlayHover.png");
        PAUSE = R.instance.getBitmap("Audio/Pause.png");
        PAUSE_HOVER = R.instance.getBitmap("Audio/PauseHover.png");

        BUTTON_THEME = new Theme();
    }

    private AudioAttachment attachment;
    private boolean isPlaying = false;

    private boolean launched = false;
    private CustomImageButton button;
    private CustomLabelField performer;
    private CustomLabelField title;

    private SliderField slider;

    private static int lastWidth = -1;

    public VkAudioAttachmentField(AudioAttachment attachment, Theme blackTheme) {
        // super(0, new Theme());
        super(Field.FOCUSABLE | Field.FIELD_HCENTER);
        this.attachment = attachment;

        Theme labelTheme = new Theme();
        labelTheme.setPrimaryColor(0);

        button = new CustomImageButton();
        button.setMargin(0, R.px(2), 0, 0);
        button.setChangeListener(this);

        performer = new CustomLabelField(attachment.getPerformer(), 0, blackTheme);
        performer.setFont(Fonts.defaultBold);
        title = new CustomLabelField(attachment.getTitle(), 0, blackTheme);

        slider = new SliderField(0, 0);
        slider.setListener(this);

        add(button);

        VerticalFieldManager vfm = new VerticalFieldManager();
        vfm.add(performer);
        vfm.add(title);
        vfm.add(slider);

        add(vfm);
    }

    public void fieldChanged(Field field, int context) {
        if (field == button) {
            if (isPlaying) {
                isPlaying = false;
                button.updateState();
                slider.setShowSlider(false);
                if (launched) {
                    AudioPlayer.instance.pause();
                }
            } else {
                isPlaying = true;
                button.updateState();
                if (!launched) {
                    launched = true;
                    AudioPlayer.instance.setListener(this);
                    AudioPlayer.instance.play(attachment.getUrl(), attachment.getTitle(),
                            attachment.getPerformer());
                } else {
                    AudioPlayer.instance.resume();
                }
                slider.setShowSlider(AudioPlayer.instance.isSeekSupported());
            }
        }
    }

    public void newPosition(float position) {
        if (AudioPlayer.instance.isSeekSupported()) {
            float d = AudioPlayer.instance.getDuration();

            if (d > 0) {
                long p = (long) (d * position);
                AudioPlayer.instance.seek(p);
            }
        }
    }

    public void onAudioEvent(int event, long duration, long position) {
        switch (event) {
            case AudioListener.GOODBYE:
                launched = false;
                isPlaying = false;
                slider.setPosition(0);
                button.updateState();
                break;
            case AudioListener.POSITION:
                float d = duration,
                        p = position;
                slider.setPosition(p / d);
                break;
            case AudioListener.PAUSE:
                break;
            case AudioListener.PLAY:
                break;
        }
    }

    protected void sublayout(int maxWidth, int maxHeight) {
        super.sublayout(maxWidth, maxHeight);

        int width = getWidth();

        if (VkAudioAttachmentField.lastWidth != width) {
            VkAudioAttachmentField.lastWidth = width;
            slider.setWidth(width);
            // updateLayout();
        }
    }

}
