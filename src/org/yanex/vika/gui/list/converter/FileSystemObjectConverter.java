package org.yanex.vika.gui.list.converter;

import org.yanex.vika.gui.list.item.FileItem;
import org.yanex.vika.util.bb.FileSystemObject;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.RichVector;

import java.util.Vector;

final class FileSystemObjectConverter {

    static Vector fileSystemObjects(RichVector items) {
        return items.transform(new Function1() {

            public Object apply(Object it) {
                return new FileItem((FileSystemObject) it);
            }
        });
    }

}
