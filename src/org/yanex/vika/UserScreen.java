package org.yanex.vika;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.dialog.WaitingDialog;
import org.yanex.vika.gui.screen.UserScreenGui;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.bb.Blackberry;

public class UserScreen extends VkMainScreen implements FieldChangeListener {

  public static final int
    RELATIONS_NONE = 0,
    RELATIONS_OUTCOMING_REQUEST = 1,
    RELATIONS_INCOMING_REQUEST = 2,
    RELATIONS_FRIENDSHIP = 3;

  private final User user;
  private final UserScreenGui gui;

  public UserScreen(final User user) {
    super(Manager.NO_VERTICAL_SCROLL);
    this.user = user;
    setFont(Fonts.defaultFont);
    setTitle((Field) null);

    gui = new UserScreenGui(this, user);
  }

  public VkMainScreen show() {
    super.show();

    final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Please_wait));
    dialog.setCancellable(true);

    final APIHelper helper = new APIHelper() {

      public void after(Object obj) {
        dialog.dismiss();
        gui.display(((Integer) obj).intValue());
        gui.image.setURL(user.getPhotoURL());
      }

      public void error(int error) {
        dialog.dismiss();
        Dialog.alert(tr(VikaResource.Network_is_not_available));
        close();
      }

      public Object task() throws APIException {
        return new Integer(Vika.api().friends.areFriendsSingle(captcha(), user.getId()));
      }
    };

    dialog.setListener(new WaitingDialog.WaitingDialogListener() {

      public void onCancel() {
        helper.interrupt();
        close();
      }
    });

    dialog.show();
    helper.start();

    return this;
  }

  public void fieldChanged(Field f, int context) {
    if (f == gui.addToFriends) {
      addToFriends();
    } else if (f == gui.rejectRequest) {
      rejectRequest();
    } else if (f == gui.cancelRequest) {
      removeRequest();
    } else if (f == gui.sendMessage) {
      sendMessage();
    } else if (f == gui.deleteFriend) {
      deleteFromFriends();
    } else if (f == gui.image) {
      Blackberry.launch(user.getPhotoURL());
    }
  }

  private void addToFriends() {
    new APIHelper() {

      public void after(Object _result) {
        long raw = ((Long) _result).longValue();
        if (raw == 1) {
          gui.display(RELATIONS_OUTCOMING_REQUEST);
        } else if (raw == 2) {
          gui.display(RELATIONS_FRIENDSHIP);
        } else {
          gui.display(RELATIONS_NONE);
        }
      }

      public void error(int error) {
        Dialog.alert(tr(VikaResource.Network_is_not_available));
      }

      public Object task() throws APIException {
        return new Long(Vika.api().friends.add(captcha(), user.getId(), null));
      }
    }.start();
  }

  private void deleteFromFriends() {
    if (Dialog.ask(Dialog.D_YES_NO, tr(VikaResource.Are_you_sure)) != Dialog.YES) {
      return;
    }

    new Unfriend() {
      public Object task() throws APIException {
        return new Long(Vika.api().friends.delete(captcha(), user.getId()));
      }
    }.start();
  }

  private void rejectRequest() {
    new Unfriend() {
      public Object task() throws APIException {
        return new Long(Vika.api().friends.delete(captcha(), user.getId()));
      }
    }.start();
  }

  private void removeRequest() {
    new Unfriend() {
      public Object task() throws APIException {
        return new Long(Vika.api().friends.delete(captcha(), user.getId()));
      }
    }.start();
  }

  private void sendMessage() {
    new ConversationScreen(user).show();
  }

  private abstract class Unfriend extends APIHelper {
    public void after(Object obj) {
      gui.display(RELATIONS_NONE);
    }

    public void error(int error) {
      Dialog.alert(tr(VikaResource.Network_is_not_available));
    }
  }

}
