package org.yanex.vika.util.bb;

import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.fun.RichVector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

public class DeviceMemory {

    public static boolean useSDCard() {
        String root = null;
        Enumeration e = FileSystemRegistry.listRoots();
        while (e.hasMoreElements()) {
            root = (String) e.nextElement();
            if (root.equalsIgnoreCase("sdcard/")) {
                return true;
            }
        }

        return false;
    }

    public static RichVector listRoots() {
        RichVector ret = new RichVector();

        try {
            String root, droot;
            Enumeration e = FileSystemRegistry.listRoots();
            while (e.hasMoreElements()) {
                root = (String) e.nextElement();
                droot = StringUtils.replace(root, "/", "");
                ret.addElement(new FileSystemObject("file:///", droot, root, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static RichVector listDirectory(FileSystemObject dir, RichVector ext) {
        RichVector ret = new RichVector();

        String path = dir.where + dir.name;

        try {
            FileConnection fileConnection = (FileConnection) Connector.open(path);
            if (fileConnection.isDirectory()) {
                Enumeration enum = fileConnection.list();
                while (enum.hasMoreElements()) {
                    String n = ((String) enum.nextElement()).trim();
                    String dn = StringUtils.replace(n, "/", "");

                    boolean add = true;

                    if (dir.where.equals("file:///") && dir.name.equals("store/")) {
                        if (!n.equals("home/")) {
                            add = false;
                        }
                    }
                    if (dir.where.equals("file:///store/") && dir.name.equals("home/")) {
                        if (!n.equals("user/")) {
                            add = false;
                        }
                    }

                    if (!n.equals(".") && !n.equals("..")) {
                        if (!n.endsWith("/")) {
                            if (DeviceMemory.matches(n, ext) && add) {
                                ret.addElement(new FileSystemObject(path, dn, n, true));
                            }
                        } else {
                            if (add) {
                                ret.addElement(new FileSystemObject(path, dn, n, false));
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static final boolean forceInternalMemory = false;

    public static long getDirectorySize(String path) {
        try {
            FileConnection fc = (FileConnection) Connector.open(path, Connector.READ);
            long size = fc.directorySize(true);
            fc.close();

            return size;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getCacheDir() {
        String path = "file://";
        if (DeviceMemory.useSDCard() && !DeviceMemory.forceInternalMemory) {
            path += "/SDCard/vika/";
        } else {
            path += "/store/home/user/vika/";
        }
        return path;
    }

    public static FileSystemObject getCacheFSO() {
        String where = "file://" +
                ((DeviceMemory.useSDCard() && !DeviceMemory.forceInternalMemory) ?
                        "/SDCard/" : "/store/home/user/");

        return new FileSystemObject(where, "vika", "vika/", false);
    }

    public static byte[] read(String path) {
        try {
            FileConnection fc = (FileConnection) Connector.open(path, Connector.READ);

            InputStream in = fc.openInputStream();
            byte[] imageBytes = new byte[(int) fc.fileSize()];
            in.read(imageBytes);
            in.close();
            fc.close();

            return imageBytes;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] readRelative(String filename) {
        String path = "file://";
        if (DeviceMemory.useSDCard() && !DeviceMemory.forceInternalMemory) {
            path += "/SDCard/vika";
        } else {
            path += "/store/home/user/vika";
        }

        path += "/" + filename;

        return DeviceMemory.read(path);
    }

    public static String getMime(String filename) {
        filename = filename.toLowerCase();
        if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".bmp")) {
            return "image/bmp";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/x-binary";
        }

    }

    public static boolean delete(String path) {
        try {
            FileConnection fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);

            fc.delete();

            fc.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getRelativeFilename(String filename) {
        if (filename.indexOf("/") < 0) {
            return filename;
        }

        int i = filename.lastIndexOf('/');
        return filename.substring(i + 1);
    }

    public static boolean save(byte[] bytes, String filename) {
        String path = "file://";
        if (DeviceMemory.useSDCard() && !DeviceMemory.forceInternalMemory) {
            path += "/SDCard/vika/";
        } else {
            path += "/store/home/user/vika/";
        }

        boolean result = DeviceMemory.makeDirectory(path);

    /*
     * if (!result && useSDCard() && !forceInternalMemory) { forceInternalMemory = true; return
     * save(bytes, filename); }
     */

        if (!result) {
            return false;
        }

        path += filename;

        try {
            FileConnection fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);
            if (!fc.exists()) {
                fc.create();
            }

            OutputStream out = fc.openOutputStream();
            out.write(bytes);
            out.close();
            fc.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean makeDirectory(String path) {
        try {
            FileConnection fc = (FileConnection) Connector.open(path, Connector.READ_WRITE);

            if (!fc.exists()) {
                fc.mkdir();
            }

            fc.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static boolean matches(String filename, Vector ext) {
        if (ext == null) {
            return true;
        }

        filename = filename.toLowerCase();

        for (int i = 0; i < ext.size(); ++i) {
            String s = (String) ext.elementAt(i);
            if (filename.endsWith(s.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
