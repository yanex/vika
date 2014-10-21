package org.yanex.vika.gui.widget.manager;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

import java.util.Vector;

public class BalloonFieldManager extends VerticalFieldManager {

  public void addAll(Vector fields) {
    Field[] a = new Field[fields.size()];
    fields.copyInto(a);
    addAll(a);
  }

}
