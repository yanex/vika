package org.yanex.vika.gui.widget.base;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import org.yanex.vika.api.util.Emoticons;
import org.yanex.vika.gui.util.TextDrawHelper;
import org.yanex.vika.util.StringUtils;

import java.util.Vector;

public class TextField extends Manager {

    private class BitmapElement implements Element {

        private final Bitmap bitmap;

        public BitmapElement(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void draw(Graphics g, int x, int y) {
            if (bitmap != null) {
                g.drawBitmap(x, y, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
            }
        }

        public int getHeight() {
            if (bitmap != null) {
                return bitmap.getHeight();
            } else {
                return 0;
            }
        }

        public int getWidth() {
            if (bitmap != null) {
                return bitmap.getWidth();
            } else {
                return 0;
            }
        }

        public boolean isBlank() {
            return bitmap == null;
        }

        public String toString() {
            return "Bitmap";
        }

    }

    private interface Element {
        void draw(Graphics g, int x, int y);

        int getHeight();

        int getWidth();

        boolean isBlank();
    }

    private class Line extends Vector implements Element {

        public void draw(Graphics g, int x, int y) {
            int ah = getHeight();

            for (int i = 0; i < size(); ++i) {
                if (elementAt(i) instanceof Element) {
                    Element em = (Element) elementAt(i);
                    em.draw(g, x, y + (ah - em.getHeight()) / 2);
                    x += em.getWidth();
                }
            }
        }

        public int draw(Graphics g, int x, int y, int maxWidth) {
            TextElement ELLIPSIZE = new TextElement("...");

            int ah = getHeight();

            boolean ellipsize = lines.size() > 1;
            if (ellipsize) {
                maxWidth -= ELLIPSIZE.getWidth();
            }

            int nx = x;
            for (int i = 0; i < size(); ++i) {
                if (elementAt(i) instanceof Element) {
                    Element em = (Element) elementAt(i);
                    int w = em.getWidth();
                    if (x + w > maxWidth) {
                        break;
                    }
                    em.draw(g, x, y + (ah - em.getHeight()) / 2);
                    x += w;
                    if (!em.isBlank()) {
                        nx = x;
                    }
                }
            }

            if (ellipsize) {
                ELLIPSIZE.draw(g, nx, y + (ah - ELLIPSIZE.getHeight()) / 2);
                nx += ELLIPSIZE.getWidth();
            }

            return nx;
        }

        public int getHeight() {
            int h = 0;
            for (int i = 0; i < size(); ++i) {
                if (elementAt(i) instanceof Element) {
                    h = Math.max(h, ((Element) elementAt(i)).getHeight());
                }
            }
            return h;
        }

        public int getWidth() {
            int w = 0;
            for (int i = 0; i < size(); ++i) {
                if (elementAt(i) instanceof Element) {
                    w += ((Element) elementAt(i)).getWidth();
                }
            }
            return w;
        }

        public boolean isBlank() {
            return size() == 0;
        }
    }

    public class Lines extends Vector implements Element {

        public void draw(Graphics g, int x, int y) {
            int oldColor = g.getColor();
            try {
                g.setColor(color);
                for (int i = 0; i < size(); ++i) {
                    if (elementAt(i) instanceof Element) {
                        Element em = (Element) elementAt(i);
                        em.draw(g, x, y);
                        y += em.getHeight();
                    }
                }
            } finally {
                g.setColor(oldColor);
            }
        }

        public int drawFirstLine(Graphics g, int x, int y, int maxWidth) {
            for (int i = 0; i < size() && i < 1; ++i) {
                if (elementAt(i) instanceof Line) {
                    Line em = (Line) elementAt(i);
                    return em.draw(g, x, y, maxWidth);
                }
            }
            return 0;
        }

        public int getHeight() {
            int h = 0;
            for (int i = 0; i < size(); ++i) {
                if (elementAt(i) instanceof Element) {
                    h += ((Element) elementAt(i)).getHeight();
                }
            }
            return h;
        }

        public int getWidth() {
            int w = 0;
            for (int i = 0; i < size(); ++i) {
                if (elementAt(i) instanceof Element) {
                    w = Math.max(w, ((Element) elementAt(i)).getWidth());
                }
            }
            return w;
        }

        public boolean isBlank() {
            return size() == 0;
        }
    }

    private class NewLineElement extends TextElement {

        private final int width;

        public NewLineElement(int width) {
            super("");
            this.width = width;
        }

        public void draw(Graphics g, int x, int y) {

        }

        public int getWidth() {
            return width;
        }

    }

    private class TextElement implements Element {

        private String text;
        private Font font = null;

        public TextElement(String text) {
            this.text = text;
            font = TextField.this.getFont();
        }

        public void draw(Graphics g, int x, int y) {
            g.drawText(text, x, y);
        }

        public Font getFont() {
            return font;
        }

        public int getHeight() {
            return font.getHeight();
        }

        public String getText() {
            return text;
        }

        public int getWidth() {
            return font.getAdvance(text);
        }

        public boolean isBlank() {
            return text.trim().length() == 0;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }

    private String text;
    private Lines lines;

    private int mw = -1, mh = -1, lw = -1, lh = -1;

    private int color;

    public TextField() {
        super(0);
        setText("");
    }

    public TextField(int style) {
        super(style);
        setText("");
    }

    public TextField(String text, long style) {
        super(style);
        setText(text);
    }

    public int getColor() {
        return color;
    }

    public XYRect getFirstLineRect() {
        XYRect rect = new XYRect();
        if (lines != null) {
            if (lines.size() > 0) {
                Element e = (Element) lines.elementAt(0);
                rect = new XYRect(0, 0, e.getWidth(), e.getHeight());
            }
        }
        return rect;
    }

    public void paint(Graphics g) {
        // super.paint(graphics);

        if (lines != null) {
            lines.draw(g, 0, 0);
        }
    }

    public int paintFirstLine(Graphics g, int maxWidth) {
        if (lines != null) {
            return lines.drawFirstLine(g, 0, 0, maxWidth);
        }
        return 0;
    }

    private String pr(String text) {
        String s = "";

        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            s += (int) c + " ";
        }

        return s;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setText(String text) {
        boolean updateLayout = false;

        if (text == null) {
            text = "";
        }
        if (this.text != null && !this.text.equals(text)) {
            updateLayout = true;
        }
        this.text = text;

        // text = pr(text);

        if (mw < 0 || mh < 0) {
            return;
        }

        Vector tokens = StringUtils.splitWords(text);

        Lines lines = new Lines();
        Line line = new Line();
        int freeSpace = mw, i;

        for (i = 0; i < tokens.size(); ++i) {
            String token = (String) tokens.elementAt(i);
            Bitmap em = Emoticons.instance.getEmoticon(token);

            Element element = null;
            if (em != null) {
                element = new BitmapElement(em);
            } else {
                if (token.equals("\n")) {
                    element = new NewLineElement(freeSpace);
                } else {
                    String s = TextDrawHelper.calcTrim(token, getFont(), mw);
                    if (s.length() < token.length()) {
                        String cont = token.substring(s.length());
                        tokens.setElementAt(cont, i--);
                    }
                    element = new TextElement(s);
                }
            }

            if (element.getWidth() > freeSpace) {
                if (line.size() > 0) {
                    lines.addElement(line);
                }
                line = new Line();
                freeSpace = mw;
            }
            line.addElement(element);
            freeSpace -= element.getWidth();
        }

        if (line.size() > 0) {
            lines.addElement(line);
        }

        for (i = 0; i < lines.size(); ++i) {
            Line l = (Line) lines.elementAt(i);
            for (int j = l.size() - 1; j >= 0; --j) {
                Object o = l.elementAt(j);
                if (o instanceof NewLineElement) {
                    l.removeElement(o);
                    break;
                }
            }
            if (l.size() > 0) {
                Object o = l.elementAt(0);
                if (o instanceof TextElement) {
                    TextElement te = (TextElement) o;
                    te.setText(te.getText().trim());
                }
            }
        }

        this.lines = lines;

        // Dialog.alert(tokens+" = "+lines.toString());

        if (updateLayout) {
            updateLayout();
        }
    }

    public void sublayout(int width, int height) {
        if (lw != width || lh != height) {
            mw = width - getPaddingLeft() - getPaddingRight();
            mh = height - getPaddingTop() - getPaddingBottom();

            setText(text);

            mw = lines.getWidth() + getPaddingLeft() + getPaddingRight();
            mh = lines.getHeight() + getPaddingTop() + getPaddingBottom();

            lw = width;
            lh = height;
        }

        setExtent(mw, mh);
    }

}
