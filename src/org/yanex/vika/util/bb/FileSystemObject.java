package org.yanex.vika.util.bb;

public class FileSystemObject {

    public final String name;
    public final String displayName;
    public final String where;
    public final boolean isFile;

    public FileSystemObject(String parent, String displayName, String name, boolean isFile) {
        this.name = name;
        this.displayName = displayName;
        this.where = parent;
        this.isFile = isFile;
    }

}
