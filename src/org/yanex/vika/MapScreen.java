//#preprocess
package org.yanex.vika;

import com.nutiteq.BasicMapComponent;
import com.nutiteq.cache.Cache;
import com.nutiteq.cache.CachingChain;
import com.nutiteq.cache.MemoryCache;
import com.nutiteq.components.WgsPoint;
import com.nutiteq.controls.ControlKeys;
import com.nutiteq.controls.OnScreenZoomControls;
import com.nutiteq.controls.UserDefinedKeysMapping;
import com.nutiteq.maps.GeoMap;
import com.nutiteq.maps.OpenStreetMap;
import com.nutiteq.net.DefaultDownloadStreamOpener;
import com.nutiteq.ui.DefaultCursor;
import com.nutiteq.ui.ThreadDrivenPanning;
import com.nutiteq.wrappers.Graphics;
import com.nutiteq.wrappers.Image;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.UiApplication;
import org.yanex.vika.api.http.LinkHelper;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.widget.base.TouchMapField;
import org.yanex.vika.util.tdparty.IPLocation;

public class MapScreen extends VkMainScreen {

  static interface MapListener {
    public void onMapCancel();

    public void onMapSelected(double latitude, double longitude);
  }

  private static final Image CURSOR = new Image(R.instance.getBitmap("Convs/Location.png"));


  private final MapListener listener;

  private TouchMapField map;
  private BasicMapComponent mapComponent;

  MapScreen(MapListener listener) {
    this(listener, Double.NaN, Double.NaN);
  }

  public MapScreen(MapListener listener, double latitude, double longitude) {
    super(Manager.NO_VERTICAL_SCROLL | Manager.NO_HORIZONTAL_SCROLL);
    this.listener = listener;

    WgsPoint startPoint = null;
    int startZoomLevel = 9;

    if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
      //bb6 library bug
      //#ifdef BB6
      double a = latitude;
      latitude = longitude;
      longitude = a;
      //#endif
      startPoint = new WgsPoint(latitude, longitude);
      startZoomLevel = 12;
    }

    if (startPoint != null) {
      init(startPoint, startZoomLevel);
    } else {
      loadCurrentLocation();
    }
  }

  private void init(WgsPoint point, int startZoomLevel) {
    mapComponent = new BasicMapComponent(
      Configuration.MAP_LICENCE_KEY, Configuration.MAP_LICENCE_AUTHOR, Configuration.MAP_LICENCE_APP_NAME, 320,
      240, point, startZoomLevel);

    GeoMap geomap = OpenStreetMap.MAPNIK;
    final Image missing = Image.createImage(geomap.getTileSize(), geomap.getTileSize());
    final Graphics graphics = missing.getGraphics();
    graphics.setColor(0xFFFFFFFF);
    graphics.fillRect(0, 0, geomap.getTileSize(), geomap.getTileSize());
    geomap.setMissingTileImage(missing);
    mapComponent.setMap(geomap);

    final MemoryCache memoryCache = new MemoryCache(5 * 1024 * 1024);
    mapComponent.setNetworkCache(new CachingChain(new Cache[]{memoryCache}));
    mapComponent.setPanningStrategy(new ThreadDrivenPanning());

    mapComponent
      .setDownloadStreamOpener(new DefaultDownloadStreamOpener(LinkHelper.getBBLink()));

    final UserDefinedKeysMapping keysMapping = new UserDefinedKeysMapping();
    keysMapping.defineKey(ControlKeys.ZOOM_OUT_KEY, 79);
    keysMapping.defineKey(ControlKeys.ZOOM_IN_KEY, 73);

    mapComponent.setControlKeysHandler(keysMapping);

    if (Touchscreen.isSupported()) {
      Bitmap b = R.instance.getBitmap("zoomcontrols.png");
      Image im = new Image(b);
      mapComponent.setOnScreenZoomControls(new OnScreenZoomControls(im));
    }

    mapComponent.setCursor(new CustomCursor());

    mapComponent.startMapping();

    map = new CustomMapFieldTouch(mapComponent);
    if (Touchscreen.isSupported() && listener != null) {
      map.setTouchOkShow(true);
    }
    add(map);
  }

  public MapListener getListener() {
    return listener;
  }

  protected boolean keyChar(char c, int status, int time) {
    if (c == 27) {
      dismiss();
      if (listener != null) {
        listener.onMapCancel();
      }
      return true;
    } else if (c == 13 || c == 10) {
      mapClicked();
    }

    return super.keyChar(c, status, time);
  }

  protected boolean navigationUnclick(int status, int time) {
    if (!Touchscreen.isSupported()) {
      mapClicked();
    }
    return true;
  }

  protected void sublayout(int width, int height) {
    super.sublayout(width, height);
    if (map != null) {
      mapComponent.resize(width, height);
    }
  }

  private void loadCurrentLocation() {
    new ThreadHelper() {

      public void after(Object o) {
        init((WgsPoint) o, 12);
      }

      public void error() {
        after(new WgsPoint(37.617633, 55.755786));
      }

      public Object task() {
        return IPLocation.instance.getPoint();
      }
    }.start();
  }

  private void mapClicked() {
    if (listener != null) {
      dismiss();
      WgsPoint w = map.getMap().getCenterPoint();
      listener.onMapSelected(w.getLat(), w.getLon());
    }
  }

  private void dismiss() {
    UiApplication.getUiApplication().popScreen(this);
  }

  private static class CustomCursor extends DefaultCursor {

    public CustomCursor() {
      super(0xFFFF0000);
    }

    public void paint(Graphics g, int screenX, int screenY, int displayWidth, int displayHeight) {
      g.drawImage(MapScreen.CURSOR, screenX - MapScreen.CURSOR.getWidth() / 2,
        screenY - MapScreen.CURSOR.getHeight() / 2, 0);
    }
  }

  private class CustomMapFieldTouch extends TouchMapField {

    public CustomMapFieldTouch(BasicMapComponent map) {
      super(map);
    }

    protected void okClicked() {
      MapScreen.this.mapClicked();
    }
  }
}
