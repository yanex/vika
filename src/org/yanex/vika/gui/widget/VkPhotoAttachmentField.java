package org.yanex.vika.gui.widget;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.MapScreen;
import org.yanex.vika.Vika;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.*;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.dialog.ListBoxDialog;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.*;
import org.yanex.vika.gui.widget.base.AbstractBitmapField;
import org.yanex.vika.gui.widget.base.FocusableField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.bb.Blackberry;
import org.yanex.vika.util.fun.Pair;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.ImageLoaderCallback;
import org.yanex.vika.util.tdparty.GoogleMaps;

import java.util.Vector;

public class VkPhotoAttachmentField extends FocusableField implements ImageLoaderCallback, FieldChangeListener {

    private static final int MAX_PHOTO_LOAD_ATTEMPTS = 3;

    private static final Bitmap VIDEO_TOP = R.instance.getBitmap("Other/VideoPlay.png");

    private Attachment attachment;

    private AbstractBitmapField bitmap = null;
    private Bitmap bmp;
    private boolean loadingPhoto = false;
    private int photoLoadAttempts = 0;

    private int height;
    private int width;

    private String text;

    private static Theme theme;

    private int downscale = 1;

    static {
        VkPhotoAttachmentField.theme = new Theme();
        VkPhotoAttachmentField.theme.setPrimaryColor(0x000000);
        VkPhotoAttachmentField.theme.setSecondaryFontColor(0xFFFFFF);

        // Background defaultBackground = new
        // NinePatchBackground("Convs/Attaches/ItemBg.png");
        Background focusBackground = new NinePatchBackground("Convs/Attaches/ItemBgFocus.png");

        VkPhotoAttachmentField.theme.setBackground(null, focusBackground, focusBackground, null);

        int px05 = DP1 * 2 / 3;
        VkPhotoAttachmentField.theme.setPaddingEdges(px05, px05, px05, px05);
    }

    private VkPhotoAttachmentField() {
        super(Field.FIELD_HCENTER, VkPhotoAttachmentField.theme);
        setChangeListener(this);
        width = height = Display.getMinDimention() / 2 - DP6;
    }

    public VkPhotoAttachmentField(DocumentAttachment attachment) {
        this();
        downscale = 2;
        this.attachment = attachment;

        text = attachment.getTitle();

        loadPhoto();
    }

    public VkPhotoAttachmentField(GeoAttachment attachment) {
        this();
        this.attachment = attachment;
        loadPhoto();
    }

    public VkPhotoAttachmentField(PhotoAttachment attachment) {
        this();
        this.attachment = attachment;
        loadPhoto();
    }

    public VkPhotoAttachmentField(VideoAttachment attachment) {
        this();
        this.attachment = attachment;

        text = attachment.getTitle();

        loadPhoto();
    }

    public void fieldChanged(Field f, int arg1) {
        if (attachment instanceof PhotoAttachment) {
            Blackberry.launch(((PhotoAttachment) attachment).getSrcBig());
        } else if (attachment instanceof DocumentAttachment) {
            Blackberry.launch(((DocumentAttachment) attachment).getUrl());
        } else if (attachment instanceof GeoAttachment) {
            GeoAttachment ga = (GeoAttachment) attachment;
            new MapScreen(null, ga.getGeo().getLatitude(), ga.getGeo().getLongitude()).show();
        } else if (attachment instanceof VideoAttachment) {
            VideoAttachment va = (VideoAttachment) attachment;
            showVideo(va);
        }
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public int getPreferredHeight() {
        return height + VkPhotoAttachmentField.theme.getPaddingEdges().top
                + VkPhotoAttachmentField.theme.getPaddingEdges().bottom;
    }

    public int getPreferredWidth() {
        return width + VkPhotoAttachmentField.theme.getPaddingEdges().left
                + VkPhotoAttachmentField.theme.getPaddingEdges().right;
    }

    private void launch(String url) {
        BrowserSession browserSession = Browser.getDefaultSession();
        browserSession.displayPage(url);
        browserSession.showBrowser();
    }

    public void layout(int width, int height) {
        super.layout(width, height);
        this.width = this.height = Display.getWidth() / 2 - DP6;
        if (bmp != null) {
            if (bitmap == null || bitmap.getWidth() != this.width || bitmap.getHeight() != this.height) {
                bitmap = new AbstractBitmapField(bmp, new XYDimension(this.width, this.height), true);
            }
        }
    }

    public void layout(int width, int height, boolean fullPrerender) {
        this.width = this.height = Display.getMinDimention() / 2 - DP6;

        super.layout(width, height, fullPrerender);
        if (bmp != null) {
            if (bitmap == null || bitmap.getWidth() != this.width || bitmap.getHeight() != this.height) {
                bitmap = new AbstractBitmapField(bmp, new XYDimension(this.width, this.height), true);
            }
        }
    }

    private void loadPhoto() {
        if (attachment == null || photoLoadAttempts > VkPhotoAttachmentField.MAX_PHOTO_LOAD_ATTEMPTS) {
            return;
        }

        String src = null;
        if (attachment instanceof PhotoAttachment) {
            src = ((PhotoAttachment) attachment).getSrc();
        } else if (attachment instanceof DocumentAttachment) {
            src = ((DocumentAttachment) attachment).getUrl();
        } else if (attachment instanceof VideoAttachment) {
            src = ((VideoAttachment) attachment).getPhoto320();
        } else if (attachment instanceof GeoAttachment) {
            GeoAttachment ga = (GeoAttachment) attachment;
            src = GoogleMaps.getSmall(ga.getGeo().getLatitude(), ga.getGeo().getLongitude());
        }

        if (src == null) {
            return;
        }

        loadingPhoto = true;
        photoLoadAttempts++;
        ImageLoader.instance.load(src, "photo",
        /*-1, -1, //do not resize now*/
        /* width*2, height*2, */
                -1, -1, false, true, false, // cache in memory
                Bitmap.SCALE_TO_FILL, this, downscale, // downscale
                3);
    }

    public void onError(String url, String tag) {
        loadingPhoto = false;
    }

    public void onLoad(String url, String tag, Bitmap bmp) {
        loadingPhoto = false;
        this.bmp = bmp;
        RoundAngles.roundAngles(bmp, 3);
    /*
     * if (bmp.getWidth()>bmp.getHeight()) setSize(this.width,
     * this.width*bmp.getHeight()/bmp.getWidth()); else
     * setSize(this.height*bmp.getWidth()/bmp.getHeight(), this.height);
     */

        bitmap = new AbstractBitmapField(bmp, new XYDimension(this.width, this.height), true);

        invalidate();
    }

    protected void paint(Graphics g) {
        int oldAlpha = g.getGlobalAlpha();

        if (isActive() || isFocused()) {
            g.setGlobalAlpha(200);
        }

        if (bitmap == null && !loadingPhoto
                && photoLoadAttempts < VkPhotoAttachmentField.MAX_PHOTO_LOAD_ATTEMPTS) {
            loadPhoto();
        } else if (bitmap != null) {
            bitmap.draw(g, 0, 0, getContentWidth(), getContentHeight());
        }

        if (text != null) {
            int h = R.px(10);

            int oldAlpha2 = g.getGlobalAlpha();
            int oldColor = g.getColor();

            g.setGlobalAlpha(150);
            g.fillRect(0, getContentHeight() - h, getContentWidth(), h);
            g.setGlobalAlpha(200);
            g.setColor(0xEEEEEE);

            Font f = g.getFont();
            int x = (getContentWidth() - f.getAdvance(text)) / 2;
            int y = getContentHeight() - h + (h - f.getHeight()) / 2;

            if (x > 0) {
                g.drawText(text, x, y);
            } else {
                TextDrawHelper.drawEllipsizedString(text, g, DP1 + getPaddingLeft(), y,
                        getContentWidth() - DP2);
            }

            g.setGlobalAlpha(oldAlpha2);
            g.setColor(oldColor);
        }

        if (attachment instanceof VideoAttachment) {
            int y = (getContentHeight() - VkPhotoAttachmentField.VIDEO_TOP.getHeight()) / 2;
            int x = (getContentWidth() - VkPhotoAttachmentField.VIDEO_TOP.getWidth()) / 2;

            g.drawBitmap(x, y, VkPhotoAttachmentField.VIDEO_TOP.getWidth(),
                    VkPhotoAttachmentField.VIDEO_TOP.getHeight(), VkPhotoAttachmentField.VIDEO_TOP, 0,
                    0);
        }

        g.setGlobalAlpha(oldAlpha);
    }

    private void showVideo(final VideoAttachment va) {
        new APIHelper() {

            public void after(Object obj) {
                Vector links = ((Video) obj).getLinks();

                if (links.size() > 1) {
                    String[] items = new String[links.size()];
                    for (int i = 0; i < items.length; ++i) {
                        items[i] = (String) ((Pair) links.elementAt(i)).first;
                    }
                    ListBoxDialog dialog = new ListBoxDialog(items);
                    dialog.show();
                    if (dialog.getSelection() < 0) {
                        return;
                    }

                    launch((String) ((Pair) links.elementAt(dialog.getSelection())).second);
                } else {
                    launch(((Video) obj).get240pUrl());
                }
            }

            public void error(int error) {
                Dialog.alert(VkMainScreen.tr(VikaResource.UNABLE_TO_LAUNCH_VIDEO));
            }

            public Object task() throws APIException {
                return Vika.api().video.getVideo(captcha(), va.getOwnerId() + "_" + va.getId());
            }
        }.start();
    }
}
