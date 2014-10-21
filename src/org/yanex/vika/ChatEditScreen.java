package org.yanex.vika;

import com.mobiata.bb.ui.decor.NinePatchBackground;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.screen.ChatEditScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.dialog.WaitingDialog;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.list.List.ListListener;
import org.yanex.vika.gui.list.converter.ListItems;
import org.yanex.vika.gui.list.item.AbstractListItem;
import org.yanex.vika.gui.list.item.UserItem;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.base.*;
import org.yanex.vika.gui.widget.manager.RightFieldManager;
import org.yanex.vika.local.CountHelper;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.util.RandomUtils;
import org.yanex.vika.util.fun.RichVector;

import java.util.Vector;

public class ChatEditScreen extends VkMainScreen implements ListListener, FieldChangeListener {

  private final ChatEditScreenGui gui;

  private Chat chat = null;
  private boolean newMode = false;

  private RichVector users = new RichVector();

  public ChatEditScreen() { // new mode
    gui = new ChatEditScreenGui(this, true);
    newMode = true;
  }

  public ChatEditScreen(Chat chat) { // edit mode
    this.chat = chat;
    gui = new ChatEditScreenGui(this, false);
    newMode = false;
    loadUsers();
  }

  public void _changeSubject() {
    String newTitle = gui.chatTitle.getText().trim();
    if (newTitle.length() == 0) {
      Dialog.alert(tr(VikaResource.You_need_to_enter_subject));
      return;
    }

    changeSubject();
  }

  public void _addUsers() {
    UserSelectScreen scr = new UserSelectScreen();
    scr.setListener(new UserSelectScreen.UserSelectListener() {

      public void onUserSelect(User user) {
        if (user != null) {
          if (hasUser(user)) {
            return;
          }

          if (newMode) {
            users.addElement(user);
            updateList();
          } else {
            addChatUser(user);
          }
        }
      }
    });
    scr.show();
  }

  public void _createChat() {
    if (users.size() < 2) {
      Dialog.alert(tr(VikaResource.You_need_to_add_at_least_two));
      return;
    }

    final String text = gui.chatMessage.getText().trim();
    if (text.length() == 0) {
      Dialog.alert(tr(VikaResource.You_need_to_enter_a_text));
      return;
    }

    createChat();
  }

  public void itemClick(int id, AbstractListItem item) {
    UserItem ui = (UserItem) item;

    if (ui.getUser().getId() == (Vika.api().getToken().getUserId())) {
      return;
    }

    int newId = id;
    if (users.size() == id + 1) {
      newId--;
    }

    if (newMode) {
      users.removeElement(ui.getUser());
      updateList();
      try {
        gui.list.getField(gui.list.getRealItemId(newId)).setFocus();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      if (Dialog.ask(Dialog.D_YES_NO, tr(VikaResource.Remove_member_question)) != Dialog.YES) {
        return;
      }

      removeUser(id, ui.getUser());
    }
  }

  public void loadNextPage(int already) {
    //nothing
  }

  public void specialPaint(int id, AbstractListItem item) {
    //nothing
  }

  private void addChatUser(final User user) {
    final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Adding_member));
    dialog.show();

    new APIHelper() {

      public void error(int error) {
        if (error == 15) { // can't remove, not admin
          dialog.dismiss();
          Dialog.alert(tr(VikaResource.You_cant_add_member));
          return;
        }

        dialog.dismiss();
      }

      public void after(Object obj) {
        dialog.dismiss();

        users.addElement(user);
        updateList();
      }

      public Object task() throws APIException {
        Vika.api().messages.addChatUser(captcha(), chat.getChatId(), user.getId());
        return null;
      }
    }.start();
  }

  private void changeSubject() {
    final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Changing_subject));
    dialog.show();

    final String newTitle = gui.chatTitle.getText().trim();

    new APIHelper() {

      public void error(int error) {
        if (error == 15) { // can't remove, not admin
          dialog.dismiss();
          Dialog.alert(tr(VikaResource.You_cant_change_topic));
          return;
        }

        dialog.dismiss();
      }

      public void after(Object obj) {
        dialog.dismiss();
      }

      public Object task() throws APIException {
        Vika.api().messages.editChat(captcha(), chat.getChatId(), newTitle);
        return null;
      }
    }.start();
  }

  private void createChat() {
    final String uids = getUids();
    final String title = gui.chatTitle.getText().length() > 0 ? gui.chatTitle.getText() : null;
    final String message = gui.chatMessage.getText();

    final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Creating_chat));
    dialog.show();

    new APIHelper() {

      public void error(int error) {
        dialog.dismiss();
        Dialog.alert(tr(VikaResource.Unable_to_create_chat));
      }

      public void after(Object _chatId) {
        final long chatId = ((Long) _chatId).longValue();
        final String guid = RandomUtils.instance.nextString();

        new APIHelper() {

          public void error(int error) {
            dialog.dismiss();
            Dialog.alert(tr(VikaResource.Unable_to_create_chat));
          }

          public void after(Object obj) {
            dialog.dismiss();

            Chat chat = new Chat(chatId, title, new Users(users), users.size());
            new ConversationScreen(chat).show();
            close();
          }

          public Object task() throws APIException {
            Vika.api().messages.send(captcha(), 0, chatId, message, null, null,
              null, 1, guid, Double.MIN_VALUE, Double.MIN_VALUE);
            return null;
          }
        }.start();
      }

      public Object task() throws APIException {
        return Vika.api().messages.createChat(captcha(), uids, title);
      }
    }.start();
  }

  private String getUids() {
    StringBuffer ret = new StringBuffer();
    for (int i = 0; i < users.size(); ++i) {
      User u = (User) users.elementAt(i);
      long uid = u.getId();

      if (ret.length() == 0) {
        ret.append(uid);
      } else {
        ret.append(",").append(uid);
      }
    }
    return ret.toString();
  }

  private boolean hasUser(User user) {
    for (int i = 0; i < users.size(); ++i) {
      User u = (User) users.elementAt(i);
      if (u.getId() == (user.getId())) {
        return true;
      }
    }
    return false;
  }

  private void loadUsers() {
    if (chat.getUsersCount() <= chat.getActiveUsers().size()) {
      users = chat.getActiveUsers().copy();
      updateList();
    } else { // have to load others
      new APIHelper() {
        public void after(Object o) {
          users = ((Users) o).copy();
          updateList();
        }

        public void error(int error) {
          Dialog.alert(tr(VikaResource.Unable_to_load_members));
        }

        public Object task() throws APIException {
          return Vika.api().messages.getChatUsers(captcha(), chat.getChatId());
        }
      }.start();
    }
  }

  private void removeUser(final int listId, final User user) {
    final WaitingDialog dialog = new WaitingDialog(tr(VikaResource.Deleting_member));
    dialog.show();

    new APIHelper() {

      public void error(int error) {
        if (error == 15) { // can't remove, not admin
          dialog.dismiss();
          Dialog.alert(tr(VikaResource.You_cant_remove_member));
          return;
        }

        dialog.dismiss();
      }

      public void after(Object obj) {
        dialog.dismiss();

        int newId = listId;
        if (users.size() == listId + 1) {
          newId--;
        }

        users.removeElement(user);
        updateList();
        try {
          gui.list.getField(gui.list.getRealItemId(newId)).setFocus();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      public Object task() throws APIException {
        Vika.api().messages.removeChatUser(captcha(), chat.getChatId(), user.getId());
        return null;
      }
    }.start();
  }

  private void updateList() {
    Vector listItems = ListItems.users(new Users(users));
    gui.list.setItems(listItems);
  }

  public Chat getChat() {
    return chat;
  }

  public boolean isDirty() {
    return false;
  }
}
