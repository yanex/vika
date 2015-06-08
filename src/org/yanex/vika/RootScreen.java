package org.yanex.vika;

import json.JSONArray;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.Audio;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Audios;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.item.collections.UsersChats;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.api.util.ThreadHelper;
import org.yanex.vika.gui.dialog.NewChatDialog;
import org.yanex.vika.gui.screen.RootScreenGui;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.list.List.ListListener;
import org.yanex.vika.gui.list.converter.ListItems;
import org.yanex.vika.gui.list.item.*;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.R;
import org.yanex.vika.gui.util.RoundedBackground;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.*;
import org.yanex.vika.gui.widget.VkToolbar.ToolbarListener;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.EditTextField;
import org.yanex.vika.gui.widget.base.EditTextField.EditListener;
import org.yanex.vika.gui.widget.base.PaneCaptionField;
import org.yanex.vika.gui.widget.manager.PaneManager;
import org.yanex.vika.local.Local;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.*;
import org.yanex.vika.util.fun.Pair;
import org.yanex.vika.util.media.AudioPlayer;

import java.util.Hashtable;
import java.util.Vector;

public class RootScreen extends VkMainScreen implements ToolbarListener {

  private final RootScreenGui gui;

  private String lastQuery = "";
  private String lastMessageQuery = "";

  private static RootScreen lastInstance;

  public static RootScreen getLastInstance() {
    return RootScreen.lastInstance;
  }

  private boolean needToReloadMessages = false;

  public RootScreen() {
    RootScreen.lastInstance = this;
    gui = new RootScreenGui(this);

    addMenuItem(new ExitMenuItem());
    addMenuItem(new SettingsMenuItem());
    addMenuItem(new RefreshMessagesMenuItem());

    setScrollingInertial(false);

    loadMessages();

    if (UsersStorage.instance.has("all")) {
      onLoadUpdate();

    } else {
      loadHints();
      tellOnline();
    }
  }

  public void close() {
    UiApplication.getUiApplication().requestBackground();
  }

  public void doSearch(final String q) {
    gui.title.incUpdating();
    new APIHelper() {

      public void error(int error) {
        lastQuery = null;
        gui.title.decUpdating();
      }

      public void after(Object o) {
        lastQuery = q;
        final Vector listItems = ListItems.users((Users) o);
        gui.searchList.setItems(listItems);
        gui.title.decUpdating();
      }

      public Object task() throws APIException {
        return Vika.api().users.search(captcha(), q, 10, 0);
      }

    }.start();
  }

  public Field getBanner() {
    return gui.banner;
  }

  public boolean isDirty() {
    return false;
  }

  private void loadAllFriends() {
    loadAllFriends(false);
  }

  private void loadAllFriends(final boolean forceNetwork) {
    if (gui.friendsList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.all", 1000 * 120)) {
      return;
    }

    gui.title.incUpdating();
    new APIHelper() {

      public Object task() throws APIException {
        if (!forceNetwork) {
          Users users = UsersStorage.instance.get("all");
          if (users.size() > 0) {
            FastStorage.instance.set("ll.all", System.currentTimeMillis());
            return ListItems.sortedUsers(users);
          }
        }

        Users users = Vika.api().friends.get(captcha(), null, 0, 0, "name");
        UsersStorage.instance.put("all", users);
        FastStorage.instance.set("ll.all", System.currentTimeMillis());
        FastStorage.instance.set("lu.all", System.currentTimeMillis());
        return ListItems.sortedUsers(users);
      }

      public void error(int error) {
        gui.title.decUpdating();
      }

      public void after(Object result) {
        gui.friendsList.setItems((Vector) result);
        loadOnline();
        gui.title.decUpdating();
      }
    }.start();
  }

  private void loadHints() {
    loadHints(false);
  }

  public void loadHints(final boolean forceNetwork) {
    if (!forceNetwork && gui.hintsList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.hints", 1000 * 120)) {
      return;
    }

    gui.title.incUpdating();
    new ThreadHelper() {

      public void error() {
        gui.title.incUpdating();
        new APIHelper() {

          public void error(int error) {
            gui.title.decUpdating();
          }

          public void after(Object o) {

            gui.hintsList.setItems((Vector) o);
            loadAllFriends(forceNetwork);
            gui.title.decUpdating();
          }

          public Object task() throws APIException {
            Users ret = Vika.api().friends.get(captcha(), null, 5, 0, "hints");

            Vector modified = new Vector();
            for (int i = 0; i < ret.size(); ++i) {
              User user = new User(ret.get(i), false, 0);
              modified.addElement(user);
            }
            UsersStorage.instance.put("hints",
              new Users(modified));

            FastStorage.instance.set("ll.hints", System.currentTimeMillis());
            FastStorage.instance.set("lu.hints", System.currentTimeMillis());
            return ListItems.users(ret);
          }
        }.start();
        gui.title.decUpdating();
      }

      public void after(Object listItems) {
        gui.hintsList.setItems((Vector) listItems);
        loadAllFriends(forceNetwork);
        gui.title.decUpdating();
      }

      public Object task() {
        if (forceNetwork) {
          return null;
        }

        Users hints = UsersStorage.instance.get("hints");
        if (hints.size() > 0) {
          FastStorage.instance.set("ll.hints", System.currentTimeMillis());
          return ListItems.users(hints);
        }
        return null;
      }

    }.start();
  }

  private void loadMessages() {
    loadMessages(false);
  }

  private void loadMessages(final boolean forceNetwork) {
    if (!forceNetwork && gui.messagesList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.messages", 1000 * 120)) {
      return;
    }

    final boolean haveToLoadNew = gui.messagesList.getItemsCount() == 0;

    final Hashtable previousItems = new Hashtable();
    for (int i = 0; i < gui.messagesList.getItemsCount(); ++i) {
      Field f = gui.messagesList.getField(i);
      if (f instanceof DialogItem) {
        DialogItem di = (DialogItem) f;
        previousItems.put(di.getMessage().getCode(), di);
      }
    }

    final APIHelper messageHelper = new APIHelper() {

      public Object task() throws APIException {
        Messages ret = Vika.api().messages.getDialogs(null, 0, 30, 0);
        MessagesStorage.instance.put("dialogs", ret);
        FastStorage.instance.set("ll.messages", System.currentTimeMillis());
        Vector listItems = ListItems.messages(ret);
        FastStorage.instance.set("lu.messages", System.currentTimeMillis());
        return listItems;
      }

      public void after(Object o) {
        gui.messagesList.setItems((Vector) o);
        gui.title.decUpdating();
      }

      public void error(int error) {
        gui.title.decUpdating();
      }
    };

    gui.title.incUpdating();
    if (forceNetwork) {
      messageHelper.start();
      return;
    }

    new ThreadHelper() {

      public Object task() {
        Messages dialogs = MessagesStorage.instance.get("dialogs");

        if (dialogs.size() > 0) {
          return ListItems.messages(dialogs, previousItems);
        } else {
          return null;
        }
      }

      public void after(Object o) {
        Vector listItems = (Vector) o;
        gui.messagesList.setItems(listItems);
        if (!forceNetwork) {
          if (haveToLoadNew && listItems.size() > 0
            || FastStorage.instance.spentFromThen("lu.messages", 1000 * 3600 * 24)) {
            FastStorage.instance.set("lu.messages", 0L);
            loadMessages(true);
          }
        }

        gui.title.decUpdating();
      }

      public void error() {
        messageHelper.start();
      }

    }.start();
  }

  private void loadOnline() {
    if (gui.onlineList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.online", 1000 * 60)) {
      return;
    }

    gui.title.incUpdating();
    new ThreadHelper() {

      public void after(Object o) {
        gui.title.decUpdating();
        gui.onlineList.setItems((Vector) o);
      }

      public void error() {
        gui.title.decUpdating();
      }

      public Object task() {
        Users users = UsersStorage.instance.getOnline();
        if (users.size() > 0) {
          FastStorage.instance.set("ll.online", System.currentTimeMillis());
          if (users.size() > 10) {
            return ListItems.sortedUsers(users);
          } else {
            return ListItems.users(users);
          }
        }
        return null; // -> error
      }
    }.start();
  }

  private void loadRequests() {
    loadRequests(false);
  }

  private void loadRequests(final boolean forceNetwork) {
    if (!forceNetwork && gui.requestsList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.requests", 1000 * 120)) {
      return;
    }

    gui.title.incUpdating();
    new ThreadHelper() {

      public void error() {
        gui.title.incUpdating();
        new APIHelper() {

          public void error(int error) {
            gui.title.decUpdating();
          }

          public void after(Object o) {
            gui.requestsList.setItems((Vector) o);
            gui.title.decUpdating();
          }

          public Object task() throws APIException {
            Users ret = Vika.api().friends.getRequests(captcha(), 20, 0);

            Vector modified = new Vector();
            for (int i = 0; i < ret.size(); ++i) {
              User user = new User(ret.get(i), false, 0);
              modified.addElement(user);
            }
            UsersStorage.instance.put("requests", new UsersChats(modified.size(),
              modified));

            FastStorage.instance.set("ll.requests", System.currentTimeMillis());
            FastStorage.instance.set("lu.requests", System.currentTimeMillis());
            Vector listItems = ListItems.users(ret);
            if (listItems.size() > 0) {
              listItems
                .insertElementAt(
                  new SeparatorItem(VkMainScreen
                    .tr(VikaResource.Friend_requests)), 0);
            }
            return listItems;
          }
        }.start();
        gui.title.decUpdating();
      }

      public void after(Object o) {
        gui.requestsList.setItems((Vector) o);
        gui.title.decUpdating();
      }

      public Object task() {
        if (forceNetwork) {
          return null;
        }

        Users uRequests = UsersStorage.instance.get("requests");

        boolean f = uRequests != null
          && !FastStorage.instance.spentFromThen("lu.requests", 1000 * 3600 * 6);
        if (f) {
          FastStorage.instance.set("ll.requests", System.currentTimeMillis());
          Vector listItems = ListItems.users(uRequests);
          if (listItems.size() > 0) {
            listItems.insertElementAt(
              new SeparatorItem(tr(VikaResource.Friend_requests)), 0);
          }
          return listItems;
        }
        return null;
      }
    }.start();
  }

  private void loadSuggestions() {
    loadSuggestions(false);
  }

  private void loadSuggestions(final boolean forceNetwork) {
    if (!forceNetwork && gui.suggestionsList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.suggestions", 1000 * 120)) {
      return;
    }

    gui.title.incUpdating();
    new ThreadHelper() {

      public void error() {
        gui.title.incUpdating();
        new APIHelper() {

          public void error(int error) {
            gui.title.decUpdating();
          }

          public void after(Object o) {
            gui.suggestionsList.setItems((Vector) o);
            gui.title.decUpdating();
          }

          public Object task() throws APIException {
            Users ret = Vika.api().friends.getSuggestions(captcha(), null, 10, 0);

            Vector modified = new Vector();
            for (int i = 0; i < ret.size(); ++i) {
              User user = new User(ret.get(i), false, 0);
              modified.addElement(user);
            }
            UsersStorage.instance.put("suggestions", new UsersChats(modified.size(),
              modified));

            FastStorage.instance.set("lu.suggestions", System.currentTimeMillis());
            FastStorage.instance.set("ll.suggestions", System.currentTimeMillis());
            Vector listItems = ListItems.users(ret);
            listItems
              .insertElementAt(
                new SeparatorItem(VkMainScreen
                  .tr(VikaResource.People_you_may_know)), 0);
            return listItems;
          }
        }.start();

        gui.title.decUpdating();
      }

      public void after(Object o) {
        gui.title.decUpdating();
        gui.suggestionsList.setItems((Vector) o);
      }

      public Object task() {
        if (forceNetwork) {
          return null;
        }

        Users uSuggestions = UsersStorage.instance.get("suggestions");
        boolean f = uSuggestions != null
          && !FastStorage.instance.spentFromThen("lu.suggestions", 1000 * 3600 * 24);
        if (f) {
          FastStorage.instance.set("ll.suggestions", System.currentTimeMillis());
          Vector listItems = ListItems.users(uSuggestions);
          listItems.insertElementAt(
            new SeparatorItem(tr(VikaResource.People_you_may_know)), 0);
          return listItems;
        }
        return null;
      }

    }.start();
  }

  private void loadUserMusic(final boolean forceNetwork) {
    if (!forceNetwork && gui.musicList.getItemsCount() > 0
      && !FastStorage.instance.spentFromThen("ll.own_music", 1000 * 120)) {
      return;
    }

    gui.title.incUpdating();
    new APIHelper() {

      public Object task() throws APIException {
        if (!forceNetwork) {
          Audios audios = MusicStorage.instance.get("own");

          boolean f = audios != null
            && !FastStorage.instance.spentFromThen("lu.own_music", 1000 * 3600 * 6);
          if (f) {
            FastStorage.instance.set("ll.own_music", System.currentTimeMillis());
            return ListItems.itemsFromAudios(audios);
          }
        }

        Audios ret = Vika.api().audio.get(captcha(), null);
        MusicStorage.instance.put("own", ret);

        FastStorage.instance.set("ll.own_music", System.currentTimeMillis());
        return ListItems.itemsFromAudios(ret);
      }

      public void after(Object result) {
        gui.musicList.setItems((Vector) result);
        gui.title.decUpdating();
      }

      public void error(int error) {
        gui.title.decUpdating();
      }

    }.start();
  }

  public void needToReloadMessages() {
    needToReloadMessages = true;
  }

  protected void onExposed() {
    if (needToReloadMessages) {
      needToReloadMessages = false;
      updateMessages();
    }
    super.onExposed();
  }

  protected void onFocus(int arg0) {
    super.onFocus(arg0);
  }

  private void onLoadUpdate() {
    gui.title.incUpdating();
    new APIHelper() {

      public void error(int error) {
        UserStorage.instance.updateOnline(new JSONArray());
        gui.title.decUpdating();
      }

      public void after(Object o) {
        long lastLocalMid = MessagesStorage.instance.getLastMid();
        long mid = Long.parseLong((String) o);

        if (mid > 0 && lastLocalMid > 0 && lastLocalMid < mid) {
          MessagesStorage.instance.delete("dialogs");
          FastStorage.instance.delete("ll.messages");
          loadMessages();
        }
        gui.title.decUpdating();
      }

      public Object task() throws APIException {
        return Vika.api().messages.onLoad(captcha());
      }
    }.start();
  }

  protected void onUndisplay() {
    FastStorage.instance.update();
    super.onUndisplay();
  }

  public void searchMessages(String query, boolean networkSearch) {
    if (query == null) {
      return;
    }

    final String q = query.toLowerCase().trim();

    gui.title.incUpdating();
    new APIHelper() {

      Vector userListItems,
        textListItems;

      public void error(int error) {
        gui.title.decUpdating();
      }

      public void after(Object o) {
        lastMessageQuery = q;

        gui.title.decUpdating();

        gui.messagesPeopleSearchList.setItems(userListItems);
        gui.messagesSearchList.setItems(textListItems);
      }

      public Object task() throws APIException {
        Pair p = Vika.api().messages.searchAll(captcha(), q);

        Users users = (Users) p.first;
        Messages messages = (Messages) p.second;

        userListItems = ListItems.users(users);
        textListItems = ListItems.messages(messages);

        if (textListItems.size() > 0 && userListItems.size() > 0) {
          userListItems.addElement(new NullItem());
        }
        return null;
      }
    }.start();
  }

  public void switchToFriends() {
    if (gui.getCurrentTab() == gui.tabFriends) {
      return;
    }

    getMainManager().setBackground(null);

    gui.setCurrentTab(gui.tabFriends);
    gui.root.deleteAll();
    gui.bannerAddings.deleteAll();
    gui.bannerAddings.add(gui.friendsPaneCaption);
    gui.root.add(gui.tabFriends);
    gui.title.setText(tr(VikaResource.FRIENDS));

    removeAllMenuItems();
    addMenuItem(new ExitMenuItem());
    addMenuItem(new SettingsMenuItem());
    addMenuItem(new RefreshFriendsMenuItem());

    loadHints();
  }

  public void switchToMessages() {
    if (gui.getCurrentTab() == gui.tabMessages) {
      return;
    }

    getMainManager().setBackground(null);

    gui.setCurrentTab(gui.tabMessages);
    gui.root.deleteAll();
    gui.bannerAddings.deleteAll();
    gui.root.add(gui.tabMessages);
    gui.title.setText(tr(VikaResource.Messages));

    removeAllMenuItems();
    addMenuItem(new ExitMenuItem());
    addMenuItem(new SettingsMenuItem());
    addMenuItem(new RefreshMessagesMenuItem());

    loadMessages();
  }

  public void switchToMusic() {
    if (gui.getCurrentTab() == gui.tabMusic) {
      return;
    }

    getMainManager().setBackground(null);

    gui.setCurrentTab(gui.tabMusic);
    gui.root.deleteAll();
    gui.bannerAddings.deleteAll();
    gui.root.add(gui.tabMusic);
    gui.title.setText(tr(VikaResource.Music));

    removeAllMenuItems();
    addMenuItem(new ExitMenuItem());
    addMenuItem(new SettingsMenuItem());

    loadUserMusic(false);
  }

  public void switchToSearch() {
    if (gui.getCurrentTab() == gui.tabSearch) {
      return;
    }

    getMainManager().setBackground(null);

    gui.setCurrentTab(gui.tabSearch);
    gui.root.deleteAll();
    gui.bannerAddings.deleteAll();
    gui.root.add(gui.tabSearch);
    gui.title.setText(tr(VikaResource.Search));

    removeAllMenuItems();
    addMenuItem(new ExitMenuItem());
    addMenuItem(new SettingsMenuItem());
    addMenuItem(new RefreshRequestsMenuItem());
    addMenuItem(new RefreshSuggestionsMenuItem());

    loadRequests();
    loadSuggestions();
  }

  private void tellOnline() {
    new APIHelper() {

      public void after(Object o) {
      }

      public Object task() throws APIException {
        Vika.api().account.online(captcha());
        return null;
      }
    }.start();
  }

  public boolean toolbarClicked(int current) {
    switch (current) {
      case 0:
        switchToMessages();
        break;
      case 1:
        switchToFriends();
        break;
      case 2:
        switchToSearch();
        break;
      case 3:
        switchToMusic();
        return false;
      case 4:
        NewChatDialog dialog = new NewChatDialog();
        dialog.show();
        int ret = dialog.getSelection();
        if (ret == 0) {
          UserSelectScreen scr = new UserSelectScreen();
          scr.setListener(new UserSelectScreen.UserSelectListener() {

            public void onUserSelect(User user) {
              if (user != null) {
                new ConversationScreen(user).show();
              }
            }
          });
          scr.show();
        } else if (ret == 1) {
          new ChatEditScreen().show();
        }
        return false;
    }

    return true;
  }

  public boolean keyChar(char ch, int status, int time) {
    return gui.keyChar(ch, status, time) || super.keyChar(ch, status, time);
  }

  protected boolean touchEvent(TouchEvent message) {
    return gui.touchEvent(message) || super.touchEvent(message);
  }

  public void updateMessages() {
    if (gui.messagesList.getItemsCount() > 0) {
      FastStorage.instance.delete("ll.messages");
      loadMessages();
    }
  }

  public String getLastQuery() {
    return lastQuery;
  }

  public void setLastQuery(String lastQuery) {
    this.lastQuery = lastQuery;
  }

  public String getLastMessageQuery() {
    return lastMessageQuery;
  }

  public void setLastMessageQuery(String lastMessageQuery) {
    this.lastMessageQuery = lastMessageQuery;
  }

  private void logout() {
    Vika.api().longpoll.stop();

    MessagesStorage.instance.clear();
    UserStorage.instance.clear();
    UsersStorage.instance.clear();

    SafeStorage.instance.delete("ui_longpoll_lastts");
    SafeStorage.instance.delete("longpoll.ts");
    SafeStorage.instance.delete("longpoll.maxmid");

    OptionsStorage.instance.delete("account.access_token");
    OptionsStorage.instance.delete("account.user_id");
    OptionsStorage.instance.delete("account.secret");

    while (UiApplication.getUiApplication().getActiveScreen() != null) {
      UiApplication.getUiApplication().popScreen();
    }

    UiApplication.getUiApplication().pushScreen(Vika.createLoginScreen());
  }

  private class ExitMenuItem extends MenuItem {
    public ExitMenuItem() {
      super(Local.tr(VikaResource.Exit), 100, 9);
    }

    public void run() {
      loadRequests(true);
    }
  }

  private class RefreshFriendsMenuItem extends MenuItem {
    public RefreshFriendsMenuItem() {
      super(Local.tr(VikaResource.Refresh_friends), 20, 20);
    }

    public void run() {
      loadHints(true);
    }
  }

  private class RefreshMessagesMenuItem extends MenuItem {
    public RefreshMessagesMenuItem() {
      super(Local.tr(VikaResource.Refresh_messages), 20, 20);
    }

    public void run() {
      loadMessages(true);
    }
  }

  private class RefreshRequestsMenuItem extends MenuItem {
    public RefreshRequestsMenuItem() {
      super(Local.tr(VikaResource.Refresh_requests), 10, 10);
    }

    public void run() {
      loadRequests(true);
    }
  }

  private class RefreshSuggestionsMenuItem extends MenuItem {
    public RefreshSuggestionsMenuItem() {
      super(Local.tr(VikaResource.Refresh_suggestions), 20, 20);
    }

    public void run() {
      loadSuggestions(true);
    }
  }

  private class SettingsMenuItem extends MenuItem {
    public SettingsMenuItem() {
      super(Local.tr(VikaResource.Settings), 10005, 100);
    }

    public void run() {
      new OptionsScreen().show();
    }
  }
}
