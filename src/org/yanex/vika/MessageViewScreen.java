package org.yanex.vika;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import org.yanex.vika.api.item.*;
import org.yanex.vika.gui.list.item.ForwardedMessageItem;
import org.yanex.vika.gui.screen.MessageViewScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.widget.VkAudioAttachmentField;
import org.yanex.vika.gui.widget.VkDocumentAttachmentField;
import org.yanex.vika.gui.widget.VkPhotoAttachmentField;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.local.CountHelper;
import org.yanex.vika.util.HappyDate;
import org.yanex.vika.util.fun.Action1;
import org.yanex.vika.util.fun.Function1;

import java.util.Vector;

public class MessageViewScreen extends VkMainScreen implements FieldChangeListener {
    private final Message message;
    private final MessageViewScreenGui gui;

    public MessageViewScreen(Message message) {
        this.message = message;
        gui = new MessageViewScreenGui(this);

        gui.name.setText(message.getUser().getFullName());
        gui.time.setText(HappyDate.getStringDate(message.getDate() * 1000));

        if (message.getUser() != null) {
            gui.photo.setURL(message.getUser().getPhotoURL());
            gui.name.setText(message.getUser().getFullName());
        }

        if (message.getBody().length() > 0) {
            gui.addBody(message.getBody());
        }

        Vector photos = message.getAttachments()
                .filter(PhotoAttachment.class).transform(new PhotoAttachmentTransformer());

        Vector documents = message.getAttachments()
                .filter(DocumentAttachment.class).transform(new DocumentAttachmentTransformer());

        Vector audios = message.getAttachments()
                .filter(AudioAttachment.class).transform(new AudioAttachmentTransformer());

        Vector videos = message.getAttachments()
                .filter(VideoAttachment.class).transform(new VideoAttachmentTransformer());

        if (videos.size() > 0) {
            int count = videos.size();
            for (int i = 0; i < count; ++i) {
                VkPhotoAttachmentField va = (VkPhotoAttachmentField) videos.elementAt(i * 2); // sic!
                VideoAttachment vva = (VideoAttachment) va.getAttachment();
                if (vva.getTitle() != null && vva.getTitle().length() > 0) {
                    CustomLabelField utilLabel = new CustomLabelField(vva.getTitle(),
                            Field.FIELD_HCENTER | DrawStyle.HCENTER, MessageViewScreenGui.BLUE_THEME);
                    utilLabel.setMargin(0, 0, DP1, 0);
                    videos.insertElementAt(utilLabel, i + 1); // i*2 because of this. Beware.
                }
            }
        }

        gui.addAttachments(photos, CountHelper.photosString(photos.size()));
        gui.addAttachments(documents, CountHelper.documentsString(documents.size()));
        gui.addAttachments(videos, null);
        gui.addAttachments(audios, null);

        if (message.getGeo() != null) {
            VkPhotoAttachmentField item = new VkPhotoAttachmentField(new GeoAttachment(message));
            gui.mainLayout.add(item);
        }

        message.getForwardedMessages().each(new Action1() {

            public void run(Object it) {
                gui.mainLayout.add(new ForwardedMessageItem((Message) it));
            }
        });

        add(gui.outerLayout);
    }

    public void fieldChanged(Field f, int arg1) {
        if (f instanceof ButtonField) {
            new MessageViewScreen(message).show();
        }
    }

    public boolean isDirty() {
        return false;
    }

    public VkMainScreen show() {
        super.show();
        gui.photo.setURL(message.getUser().getPhotoURL());
        return this;
    }

    private final class DocumentAttachmentTransformer implements Function1 {
        public Object apply(Object it) {
            DocumentAttachment da = (DocumentAttachment) it;
            String ext = da.getExt().toLowerCase();
            if (da.getSize() < 1000 * 1000 &&
                    (ext.equals("png") || ext.equals("jpg") || ext.equals("gif"))) {
                return new VkPhotoAttachmentField(da);
            } else {
                return new VkDocumentAttachmentField(da);
            }
        }
    }

    private final class AudioAttachmentTransformer implements Function1 {
        public Object apply(Object it) {
            return new VkAudioAttachmentField((AudioAttachment) it, MessageViewScreenGui.BLACK_THEME);
        }
    }

    private final class VideoAttachmentTransformer implements Function1 {
        public Object apply(Object it) {
            return new VkPhotoAttachmentField((VideoAttachment) it);
        }
    }

    private final class PhotoAttachmentTransformer implements Function1 {
        public Object apply(Object it) {
            return new VkPhotoAttachmentField((PhotoAttachment) it);
        }
    }

}
