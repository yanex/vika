package org.yanex.vika.gui.list.item;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import org.yanex.vika.gui.util.GuiItem;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.ImageField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.bb.FileSystemObject;
import org.yanex.vika.util.fun.Pair;

public class FileItem extends ComplexListItem implements GuiItem {

    private static final Bitmap FOLDER = R.instance.getBitmap("FS/Folder.png");
    private static final Bitmap FILE = R.instance.getBitmap("FS/File.png");
    private static final Bitmap PICTURE = R.instance.getBitmap("FS/Picture.png");
    private static final Bitmap BACK = R.instance.getBitmap("FS/Back.png");

    private static final Bitmap FOLDER_HOVER = R.instance.getBitmap("FS/Folder.png");
    private static final Bitmap FILE_HOVER = R.instance.getBitmap("FS/File.png");
    private static final Bitmap PICTURE_HOVER = R.instance.getBitmap("FS/Picture.png");
    private static final Bitmap BACK_HOVER = R.instance.getBitmap("FS/Back.png");

    private static final Theme THEME = new Theme()
            .setPrimaryColor(0x000000)
            .setSecondaryFontColor(0xFFFFFF)
            .setPaddingEdges(DP2, DP2, DP2, DP2);

    private final FileSystemObject fso;

    private final HorizontalFieldManager hfm;
    private final CustomLabelField label;
    private final ImageField icon;

    private final Bitmap bitmapNormal;
    private final Bitmap bitmapHover;

    public FileItem(FileSystemObject fso) {
        this.fso = fso;

        Pair bitmaps = fetchBitmaps(fso);
        bitmapNormal = (Bitmap) bitmaps.first;
        bitmapHover = (Bitmap) bitmaps.second;

        label = new CustomLabelField(
                (fso == null) ? tr(VikaResource.Back) : fso.displayName,
                DrawStyle.ELLIPSIS | Field.FIELD_VCENTER, THEME);
        icon = new ImageField(bitmapNormal, DP12, DP12, Field.FIELD_VCENTER, false);

        hfm = new HorizontalFieldManager();
        hfm.add(icon);
        hfm.add(label);
        add(hfm);

        addingCompleted();
    }

    private Pair fetchBitmaps(FileSystemObject fso) {
        if (fso == null) {
            return new Pair(BACK, BACK_HOVER);
        } else if (!fso.isFile) {
            return new Pair(FOLDER, FOLDER_HOVER);
        } else if (isImageFile(fso.name)) {
            return new Pair(PICTURE, PICTURE_HOVER);
        } else return new Pair(FILE, FILE_HOVER);
    }

    private boolean isImageFile(String name) {
        if (name.length() >= 4) {
            String ext = fso.name.substring(fso.name.length() - 4).toLowerCase();
            if (!ext.equals(".png") && !ext.equals(".jpg")) {
                return true;
            }
        }
        return false;
    }

    public FileSystemObject getFileSystemObject() {
        return fso;
    }

    protected void onLayoutFocus() {
        label.getTheme().setPrimaryColor(0xFFFFFF);
        icon.setBitmap(bitmapHover);
    }

    protected void onLayoutUnfocus() {
        label.getTheme().setPrimaryColor(0x000000);
        icon.setBitmap(bitmapNormal);
    }

}
