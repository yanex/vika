package org.yanex.vika.gui.list.item;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.decor.Background;
import org.yanex.vika.api.item.User;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.GradientBackground;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.AbstractBitmapField;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.network.ImageLoader;
import org.yanex.vika.util.network.ImageLoaderCallback;
import org.yanex.vika.util.network.State;

public class UserItem extends ListItem implements ImageLoaderCallback {

    private final AbstractBitmapField PHOTO_DEFAULT = new AbstractBitmapField(
            R.instance.getBitmap("camera.png"), new XYDimension(DP14, DP14), true, true);

    private static final Background BACKGROUND_FOCUS =
            new GradientBackground(0x59a0e8, 0x1c65be);

    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0xFFFFFF)
            .setPaddingEdges(DP2, DP2, DP2, DP2)
            .setBackground(null, BACKGROUND_FOCUS, BACKGROUND_FOCUS, null);

    private final User user;

    private Bitmap photo = null;
    private State photoState = State.None;

    public UserItem(User user) {
        super(UserItem.THEME);
        this.user = user;
    }

    public boolean filter(String filter) {
        return filter == null || filter.length() == 0
                || StringUtils.nonRegistryContains(user.getFullName(), filter);
    }

    public int getPreferredHeight() {
        return DP14;
    }

    public int getPreferredWidth() {
        return Integer.MAX_VALUE;
    }

    public User getUser() {
        return user;
    }

    // uiThread
    private void loadPhoto() {
        if (photoState != State.None) {
            return;
        }
        photoState = State.Loading;
        ImageLoader.instance.load(user.getPhotoURL(), "user",
                new XYDimension(getPreferredHeight(), getPreferredHeight()), this);
    }

    public void onError(String url, String tag) {
        photoState = State.Error;
    }

    public void onLoad(String url, String tag, Bitmap bmp) {
        this.photo = bmp;
        photoState = State.Complete;
        invalidate();
    }

    protected void paint(Graphics g, XYRect rect) {
        int height = rect.height, width = rect.width;

        g.setBackgroundColor(0);

        int oldColor = g.getColor();
        int oldAlpha = g.getGlobalAlpha();

        int color = getTheme().getPrimaryColor();
        int secondColor = 0x9e9e9e;
        if (isActive() || isFocused()) {
            color = getTheme().getSecondaryFontColor();
            secondColor = 0xffffff;
        }

        try {
            g.setColor(0xCCCCCC);
            if (photo != null) {
                g.drawBitmap(DP1 + rect.x, rect.y, getContentHeight(),
                        getContentHeight(), photo, 0, 0);
            } else {
                PHOTO_DEFAULT.draw(g,
                        DP1 + rect.x, rect.y,
                        getContentHeight(), getContentHeight());
                if (photoState == State.None) {
                    loadPhoto();
                }
            }

            int dx = getContentHeight() + UserItem.DP2 + DP1 * 2;
            int w, dy;

            String name = user.getFullName();

            Font f = Fonts.bold(DP7, Ui.UNITS_px);
            w = f.getBounds(name);
            dy = (height - f.getHeight()) / 2;

            g.setGlobalAlpha(255);
            g.setFont(f);
            g.setColor(color);
            g.drawText(name, rect.x + dx, rect.y + dy);

            dx += w + UserItem.DP2 * 3 / 4;

            if (user.isOnline()) {
                f = Fonts.narrow(DP7, Ui.UNITS_px);
                dy = (height - f.getHeight()) / 2;

                g.setGlobalAlpha(255);
                g.setFont(f);
                g.setColor(secondColor);
                g.drawText("online", rect.x + dx, rect.y + dy);
            }

        } finally {
            g.setColor(oldColor);
            g.setGlobalAlpha(oldAlpha);
        }
    }

}
