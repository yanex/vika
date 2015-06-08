package org.yanex.vika.gui.list.item;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.MessageViewScreen;
import org.yanex.vika.api.item.*;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.RoundedBackground;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.VkAudioAttachmentField;
import org.yanex.vika.gui.widget.VkDocumentAttachmentField;
import org.yanex.vika.gui.widget.VkPhotoAttachmentField;
import org.yanex.vika.gui.widget.VkPhotoAttachmentField.PhotoAttachmentFieldListener;
import org.yanex.vika.gui.widget.base.AutoLoadingBitmapField;
import org.yanex.vika.gui.widget.base.ButtonField;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.TextField;
import org.yanex.vika.local.CountHelper;
import org.yanex.vika.util.HappyDate;

import java.util.Vector;

public class ForwardedMessageItem extends HorizontalFieldManager implements
        PhotoAttachmentFieldListener, FieldChangeListener, GuiItem {

    private static Background INNER_FOCUS = new RoundedBackground(0xcbd5e3);

    private final Theme BLACK_THEME, GRAY_THEME, BLUE_THEME, BLUE_INNER_THEME;

    private int lineColor = 0x9ba4af;

    private Message message;
    private HorizontalFieldManager root;

    private VerticalFieldManager vfmMain;
    private AutoLoadingBitmapField photo;
    private CustomLabelField name;
    private CustomLabelField date;
    private TextField text;

    private ActiveRichTextField activeText;

    public ForwardedMessageItem(Message message) {
        this.message = message;

        BLACK_THEME = new Theme();
        BLACK_THEME.setPrimaryColor(0);

        GRAY_THEME = new Theme();
        GRAY_THEME.setPrimaryColor(0x91a4b6);

        BLUE_THEME = new Theme();
        BLUE_THEME.setPrimaryColor(0x4f7ca3);

        BLUE_INNER_THEME = new Theme();
        BLUE_INNER_THEME.setPrimaryColor(0);
        BLUE_INNER_THEME.setBackground(null, ForwardedMessageItem.INNER_FOCUS,
                ForwardedMessageItem.INNER_FOCUS, null);

        init();

        if (message.getUser() != null) {
            photo.setURL(message.getUser().getPhotoURL());
            name.setText(message.getUser().getFirstName());
        }

        date.setText(HappyDate.getStringDate(message.getDate() * 1000));

        root.add(photo);
        vfmMain.add(name);
        vfmMain.add(date);

        if (message.getBody() != null && message.getBody().length() > 0) {
            if (testActive(message.getBody())) {
                activeText.setText(message.getBody());
                vfmMain.add(activeText);
            } else {
                text.setText(message.getBody());
                vfmMain.add(text);
            }
        }

        int i;

        Vector documentItems = new Vector();
        Vector photoItems = new Vector();
        Vector audioItems = new Vector();
        Vector videoItems = new Vector();
        CustomLabelField utilLabel;

        if (message.getAttachments() != null) {
            for (i = 0; i < message.getAttachments().size(); ++i) {
                Attachment a = (Attachment) message.getAttachments().getObject(i);
                if (a instanceof PhotoAttachment) {
                    PhotoAttachment pa = (PhotoAttachment) a;
                    if (pa.getSrc() != null && pa.getSrc().length() > 0) {
                        VkPhotoAttachmentField item = new VkPhotoAttachmentField(pa);
                        item.setAttachmentListener(this);
                        photoItems.addElement(item);
                    }
                } else if (a instanceof DocumentAttachment) {
                    DocumentAttachment da = (DocumentAttachment) a;
                    if (da.getSize() < 1000 * 1000
                            && (da.getExt().toLowerCase().equals("png")
                            || da.getExt().toLowerCase().equals("jpg") || da.getExt()
                            .toLowerCase().equals("gif"))) {
                        VkPhotoAttachmentField item = new VkPhotoAttachmentField(da);
                        item.setAttachmentListener(this);
                        documentItems.addElement(item);
                    } else {
                        VkDocumentAttachmentField item = new VkDocumentAttachmentField(da);
                        documentItems.addElement(item);
                    }
                } else if (a instanceof VideoAttachment) {
                    VideoAttachment va = (VideoAttachment) a;
                    VkPhotoAttachmentField item = new VkPhotoAttachmentField(va);
                    item.setAttachmentListener(this);
                    videoItems.addElement(item);
                } else if (a instanceof AudioAttachment) {
                    AudioAttachment aa = (AudioAttachment) a;
                    VkAudioAttachmentField item = new VkAudioAttachmentField(aa, BLACK_THEME);
                    audioItems.addElement(item);
                }
            }
        }

        if (photoItems.size() > 0) {
            utilLabel = new CustomLabelField(CountHelper.photosString(photoItems.size()),
                    Field.FIELD_HCENTER
                            | DrawStyle.HCENTER, BLUE_THEME);
            utilLabel.setMargin(0, 0, DP1, 0);
            photoItems.addElement(utilLabel);
        }
        if (documentItems.size() > 0) {
            utilLabel = new CustomLabelField(CountHelper.documentsString(documentItems.size()),
                    Field.FIELD_HCENTER | DrawStyle.HCENTER, BLUE_THEME);
            utilLabel.setMargin(0, 0, DP1, 0);
            documentItems.addElement(utilLabel);
        }
        if (videoItems.size() > 0) {
            int count = videoItems.size();
            for (i = 0; i < count; ++i) {
                VkPhotoAttachmentField va = (VkPhotoAttachmentField) videoItems.elementAt(i * 2);
                VideoAttachment vva = (VideoAttachment) va.getAttachment();
                if (vva.getTitle() != null && vva.getTitle().length() > 0) {
                    utilLabel = new CustomLabelField(vva.getTitle(), Field.FIELD_HCENTER | DrawStyle.HCENTER,
                            BLUE_THEME);
                    utilLabel.setMargin(0, 0, DP1, 0);
                    videoItems.insertElementAt(utilLabel, i + 1);
                }
            }
        }

        addAll(vfmMain, photoItems);
        addAll(vfmMain, documentItems);
        addAll(vfmMain, videoItems);
        addAll(vfmMain, audioItems);

        if (message.getGeo() != null) {
            VkPhotoAttachmentField item = new VkPhotoAttachmentField(new GeoAttachment(message));
            item.setAttachmentListener(this);
            vfmMain.add(item);
        }

        if (message.getForwardedMessages().size() > 0) {
            ButtonField fl = new ButtonField(CountHelper.forwardedMessagesString(message
                    .getForwardedMessages().size()), 0, BLUE_INNER_THEME);
            fl.setPadding(DP1, DP1, DP1, DP1);
            fl.setMargin(DP1, 0, 0, 0);

            fl.setChangeListener(this);
            vfmMain.add(fl);
        }

        root.add(vfmMain);

        add(root);
    }

    public void addAll(Manager manager, Vector fields) {
        Field[] a = new Field[fields.size()];
        fields.copyInto(a);
        manager.addAll(a);
    }

    public void drawFocus() {
        lineColor = 0xffffff;
        BLUE_THEME.setPrimaryColor(0xffffff);
        BLUE_INNER_THEME.setPrimaryColor(0xffffff);
        BLACK_THEME.setPrimaryColor(0xffffff);
        GRAY_THEME.setPrimaryColor(0xffffff);

        if (text.getManager() != null) {
            text.setColor(0xFFFFFF);
        }
    }

    public void drawUnfocus() {
        BLUE_THEME.setPrimaryColor(0x4f7ca3);
        BLUE_INNER_THEME.setPrimaryColor(0x4f7ca3);
        BLACK_THEME.setPrimaryColor(0);
        GRAY_THEME.setPrimaryColor(0x91a4b6);
        lineColor = 0x9ba4af;

        if (text.getManager() != null) {
            text.setColor(0x000000);
        }
    }

    public void fieldChanged(Field f, int arg1) {
        if (f instanceof ButtonField) {
            new MessageViewScreen(message).show();
        }
    }

    private void init() {
        setMargin(DP1, 0, DP1, 0);

        root = new HorizontalFieldManager();
        vfmMain = new VerticalFieldManager();

        root.setPadding(0, 0, 0, DP3);

        photo = new AutoLoadingBitmapField(new XYDimension(DP10, DP10), 0,
                true);
        photo.setPadding(0, DP2, 0, 0);

        name = new CustomLabelField("", 0, BLACK_THEME);
        date = new CustomLabelField("", 0, GRAY_THEME);

        name.setFont(Fonts.bold(6));
        date.setFont(Fonts.narrow(5));

        text = new TextField("", 0);
        activeText = new ActiveRichTextField("");
    }

    public void onSizeChange(int newWidth, int newHeight) {
        updateLayout();
    }

    protected void paint(Graphics g) {
        super.paint(g);

        int oldColor = g.getColor();
        g.setColor(lineColor);

        g.fillRect(0, 0, DP1, getContentHeight());

        g.setColor(oldColor);
    }

    private boolean testActive(String text) {
        return false;
    }
}
