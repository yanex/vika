package org.yanex.vika;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.list.List.ListListener;
import org.yanex.vika.gui.list.converter.ListItems;
import org.yanex.vika.gui.list.item.AbstractListItem;
import org.yanex.vika.gui.list.item.FileItem;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.widget.VkCompactTitleField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.bb.DeviceMemory;
import org.yanex.vika.util.bb.FileSystemObject;
import org.yanex.vika.util.fun.RichVector;

import java.util.Stack;
import java.util.Vector;

class FileSelectWindow extends VkMainScreen implements ListListener {

    private final List list;
    private final VkCompactTitleField title;

    private final RichVector ext;

    private String filename = null;
    private Stack stack = new Stack();

    FileSelectWindow(Vector ext) {
        this.ext = new RichVector(ext);

        setFont(Fonts.defaultFont);

        list = new List();
        list.setLastPage(true);
        list.setListener(this);
        title = new VkCompactTitleField("Blackberry");

        setBanner(title);
        add(list);

        Vector listItems = ListItems.fileSystemObjects(DeviceMemory.listRoots());
        list.setItems(listItems);
    }

    public String getFilename() {
        return filename;
    }

    public void itemClick(int id, AbstractListItem item) {
        if (id == 0 && stack.size() > 0) {
            back();
            return;
        }

        FileItem fi = (FileItem) item;
        FileSystemObject fso = fi.getFileSystemObject();

        if (fso.isFile) {
            filename = fso.where + fso.name;
            UiApplication.getUiApplication().popScreen(this);
        } else {
            stack.push(fso);
            reload();
        }
    }

    void showModal() {
        UiApplication.getUiApplication().pushModalScreen(this);
    }

    protected boolean keyChar(char c, int status, int time) {
        if (c == 27 && stack.size() > 0) {
            back();
            return true;
        }
        return super.keyChar(c, status, time);
    }

    public void loadNextPage(int already) {
        //empty
    }

    public void specialPaint(int id, AbstractListItem item) {
        //empty
    }

    protected void makeMenu(Menu menu, int instance) {
        super.makeMenu(menu, instance);

        menu.add(new CancelItem());
    }

    private void reload() {
        Vector listItems;
        if (stack.size() == 0) {
            title.setText("Blackberry");
            listItems = ListItems.fileSystemObjects(DeviceMemory.listRoots());
        } else {
            FileSystemObject fso = (FileSystemObject) stack.peek();
            title.setText(fso.displayName);
            listItems = ListItems.fileSystemObjects(DeviceMemory.listDirectory(fso, ext));
            listItems.insertElementAt(new FileItem(null), 0);
        }
        list.setItems(listItems);
        if (stack.size() > 0) {
            try {
                list.getField(2).setFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void back() {
        if (stack.size() > 0) {
            stack.pop();
            reload();
        }
    }

    private class CancelItem extends MenuItem {
        public CancelItem() {
            super(tr(VikaResource.Cancel), 10, 20);
        }

        public void run() {
            UiApplication.getUiApplication().popScreen(FileSelectWindow.this);
        }
    }

}
