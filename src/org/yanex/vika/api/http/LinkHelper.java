package org.yanex.vika.api.http;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import org.yanex.vika.gui.dialog.ListBoxDialog;
import org.yanex.vika.local.Local;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.OptionsStorage;
import rimx.network.TransportDetective;
import rimx.network.URLFactory;

public class LinkHelper {

    public static final int METHOD_AUTO = 0;
    public static final int METHOD_ALTERNATIVE = 1;
    public static final int METHOD_WIFI = 2;
    public static final int METHOD_WAP2 = 3;
    public static final int METHOD_BIS = 4;
    public static final int METHOD_MDS = 5;
    public static final int METHOD_TCP = 6;

    private static final String[] items = {"Auto", "Alternative", "Wi-fi", "WAP", "BIS", "MDS", "TCP"};

    public static String getAuto(final URLFactory factory, final TransportDetective detect) {
        if (DeviceInfo.isSimulator()) {
            return factory.getHttpDefaultUrl() + ";deviceside=true";
        }

        if (detect.isCoverageAvailable(TransportDetective.TRANSPORT_TCP_WIFI)) {
            return factory.getHttpTcpWiFiUrl();
        }

        ServiceRecord record;

        if (detect.isCoverageAvailable(TransportDetective.TRANSPORT_BIS_B)) {
            record = detect.getBisServiceRecord();
            if (record != null) {
                return factory.getHttpBisUrl(record);
            }
        }

        if (detect.isCoverageAvailable(TransportDetective.TRANSPORT_MDS)) {
            return factory.getHttpMdsUrl(true);
        }

        if (detect.isCoverageAvailable(TransportDetective.DEFAULT_TCP_CELLULAR)) {
            record = detect.getDefaultTcpCellularServiceRecord();
            if (record != null) {
                return factory.getHttpDefaultTcpCellularUrl(record);
            }
        }

        if (detect.isCoverageAvailable(TransportDetective.TRANSPORT_WAP2)) {
            record = detect.getWap2ServiceRecord();
            if (record != null) {
                return factory.getHttpWap2Url(record);
            }
        }

        return factory.getHttpDefaultUrl();
    }

    public static String getBBLink() {
        return LinkHelper.getBBLink("http://google.com/");
    }

    public static String getBBLink(String url) {
        String m = OptionsStorage.instance.getString("connection_mode", "0");
        int method = Integer.parseInt(m);
        String ret = url;

        URLFactory factory = new URLFactory(url);
        TransportDetective detect = new TransportDetective();
        ServiceRecord record;

        switch (method) {
            case METHOD_AUTO:
                ret = LinkHelper.getAuto(factory, detect);
                break;
            case METHOD_WIFI:
                ret = factory.getHttpTcpWiFiUrl();
                break;
            case METHOD_WAP2:
                record = LinkHelper.getWAP2ServiceRecord();
                if (record != null) {
                    ret = factory.getHttpWap2Url(record);
                }
                break;
            case METHOD_BIS:
                record = detect.getBisServiceRecord();
                if (record != null) {
                    ret = factory.getHttpBisUrl(record);
                }
                break;
            case METHOD_MDS:
                ret = factory.getHttpMdsUrl(true);
                break;
            case METHOD_TCP:
                record = detect.getDefaultTcpCellularServiceRecord();
                if (record != null) {
                    ret = factory.getHttpDefaultTcpCellularUrl(record);
                }
                break;
            case METHOD_ALTERNATIVE:
                ConnectionFactory connFact = new ConnectionFactory();
                ConnectionDescriptor connDesc;
                connDesc = connFact.getConnection(url);
                if (connDesc != null) {
                    ret = connDesc.getUrl();
                }
                break;
            default:
                break;
        }

        if (method != LinkHelper.METHOD_ALTERNATIVE) {
            url = factory.getHttpDefaultUrl();
        }

        return ret == null || ret.length() <= url.length() ? "" : ret.substring(url.length());
    }

    private static ServiceRecord getWAP2ServiceRecord() {
        ServiceBook sb = ServiceBook.getSB();
        ServiceRecord[] records = sb.getRecords();

        for (int i = 0; i < records.length; i++) {
            String cid = records[i].getCid().toLowerCase();
            String uid = records[i].getUid().toLowerCase();
            if (cid.indexOf("wptcp") != -1 && uid.indexOf("wifi") == -1 && uid.indexOf("mms") == -1) {
                return records[i];
            }
        }

        return null;
    }

    public static void selectConnectionType() {
        ListBoxDialog dialog = new ListBoxDialog(LinkHelper.items);
        dialog.show();
        if (dialog.getSelection() >= 0) {
            OptionsStorage.instance.set("connection_mode", dialog.getSelection() + "");
            Dialog.alert(Local.tr(VikaResource.Current_connection_type) + " "
                    + LinkHelper.items[dialog.getSelection()] + ".");
        }
    }

    public static class SelectConnectionTypeItem extends MenuItem {
        public SelectConnectionTypeItem() {
            super(Local.tr(VikaResource.Connection_type), 10, 10);
        }

        public void run() {
            LinkHelper.selectConnectionType();
        }
    }

}
