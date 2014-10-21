package org.yanex.vika.gui.screen;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import org.yanex.vika.*;
import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.Audio;
import org.yanex.vika.api.item.Chat;
import org.yanex.vika.api.item.Message;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.list.converter.ListItems;
import org.yanex.vika.gui.list.item.*;
import org.yanex.vika.gui.util.Fonts;
import org.yanex.vika.gui.util.Theme;
import org.yanex.vika.gui.widget.*;
import org.yanex.vika.gui.widget.base.CustomLabelField;
import org.yanex.vika.gui.widget.base.EditTextField;
import org.yanex.vika.gui.widget.base.PaneCaptionField;
import org.yanex.vika.gui.widget.manager.PaneManager;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.MessagesStorage;
import org.yanex.vika.util.media.AudioPlayer;

import java.util.Vector;

public class RootScreenGui extends ScreenGui {

  public final RootScreen screen;

  public CustomLabelField searchLabel;

  public VerticalFieldManager banner;
  public VkCompactTitleField title;
  public VkToolbar toolbar;

  public EditTextField friendsSearch;
  public EditTextField messagesSearch;
  public EditTextField searchSearch;

  public VerticalFieldManager messagesSearchBar;
  public VerticalFieldManager searchSearchVFM;
  public VerticalFieldManager friendsSearchBar;

  public VerticalFieldManager root;

  public VerticalFieldManager bannerAddings;

  public VerticalFieldManager tabMessages;
  public VerticalFieldManager tabFriends;
  public VerticalFieldManager tabSearch;
  public VerticalFieldManager tabMusic;

  public PaneManager friendsPaneManager;
  public PaneCaptionField friendsPaneCaption;

  public List requestsList;
  public List suggestionsList;
  public List searchList;
  public List messagesSearchList;
  public List messagesPeopleSearchList;
  public List messagesList;
  public List musicList;
  public List hintsList;
  public List friendsList;
  public List onlineList;

  private Field currentTab;

  private boolean isCurrentlySearch = false;

  public RootScreenGui(RootScreen screen) {
    screen.setTitle((Field) null);
    screen.setFont(Fonts.defaultFont);

    this.screen = screen;

    root = new VerticalFieldManager();

    banner = new VerticalFieldManager();
    title = new VkCompactTitleField(tr(VikaResource.Messages));
    toolbar = new VkToolbar();
    toolbar.setListener(screen);
    bannerAddings = new VerticalFieldManager();

    banner.add(title);
    banner.add(toolbar);
    banner.add(bannerAddings);

    addMessages();
    addFriends();
    addSearch();
    addMusic();

    screen.setBanner(banner);
    root.add(tabMessages);
    screen.add(root);

    setCallbacks();
    currentTab = tabMessages;
  }

  private void addFriends() {
    tabFriends = new VerticalFieldManager();

    friendsSearch = new VkSearchEditField(TextField.NO_NEWLINE);
    friendsSearch.setHint(tr(VikaResource.Search));

    friendsSearchBar = new VkSearchBar(friendsSearch);

    hintsList = new List();
    friendsList = new List();
    onlineList = new List();

    VerticalFieldManager allFriends = new VerticalFieldManager();
    allFriends.add(hintsList);
    allFriends.add(friendsList);

    friendsPaneCaption = new VkPaneCaptionField();
    friendsPaneManager = new PaneManager(friendsPaneCaption, screen);
    friendsPaneManager.addPane(tr(VikaResource.FRIENDS), allFriends);
    friendsPaneManager.addPane(tr(VikaResource.Online), onlineList);

    tabFriends.add(friendsSearchBar);
    tabFriends.add(friendsPaneManager);

  }

  private void addMessages() {
    tabMessages = new VerticalFieldManager();

    messagesSearch = new VkSearchEditField(TextField.NO_NEWLINE);
    messagesSearch.setHint(tr(VikaResource.Search));

    messagesSearchBar = new VkSearchBar(messagesSearch);

    messagesList = new List();
    messagesSearchList = new List();
    messagesPeopleSearchList = new List();

    tabMessages.add(messagesSearchBar);
    tabMessages.add(messagesList);
  }

  private void addMusic() {
    tabMusic = new VerticalFieldManager();

    musicList = new List();
    musicList.setOwner(screen);
    VkBigPlayer bigPlayer = new VkBigPlayer();

    VerticalFieldManager vfm = new VerticalFieldManager();
    int px = DP3;
    vfm.setPadding(px, px, px, px);
    vfm.add(bigPlayer);

    tabMusic.add(vfm);
    tabMusic.add(musicList);
  }

  private void addSearch() {
    tabSearch = new VerticalFieldManager();

    searchSearch = new VkSearchEditField(TextField.NO_NEWLINE);
    searchSearch.setHint(tr(VikaResource.Search));

    searchSearchVFM = new VkSearchBar(searchSearch);
    Theme labelTheme = new Theme();
    labelTheme.setPrimaryColor(0x666666);
    searchLabel = new CustomLabelField(tr(VikaResource.You_can_search), Field.USE_ALL_WIDTH
      | DrawStyle.HCENTER, labelTheme);
    searchLabel.setMargin(DP2, DP5, DP2, DP5);
    searchLabel.setFont(Fonts.narrow(7));
    searchSearchVFM.add(searchLabel);

    suggestionsList = new List();
    requestsList = new List();
    searchList = new List();

    tabSearch.add(searchSearchVFM);
    tabSearch.add(requestsList);
    tabSearch.add(suggestionsList);
  }

  private void setCallbacks() {
    suggestionsList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        UserItem ui = (UserItem) item;
        new UserScreen(ui.getUser()).show();
      }

      public void loadNextPage(final int already) {
        title.incUpdating();
        new APIHelper() {

          public void error(int error) {
            suggestionsList.nextPageLoaded();
            title.decUpdating();
          }

          public void after(Object obj) {
            suggestionsList.appendItems((Vector) obj);
            suggestionsList.nextPageLoaded();
            title.decUpdating();
          }

          public Object task() throws APIException {
            Users ret = Vika.api().friends.getSuggestions(captcha(), null, 10,
              already - 1);
            return ListItems.users(ret);
          }

        }.start();
      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    requestsList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        UserItem ui = (UserItem) item;
        new UserScreen(ui.getUser()).show();
      }

      public void loadNextPage(final int already) {

      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    musicList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        SongItem si = (SongItem) item;
        Audio audio = si.audio;
        AudioPlayer.instance.play(audio.getUrl(), audio.getTitle(), audio.getArtist());
      }

      public void loadNextPage(final int already) {

      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    searchList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        UserItem ui = (UserItem) item;
        new UserScreen(ui.getUser()).show();
      }

      public void loadNextPage(final int already) {
        if (screen.getLastQuery() != null) {
          title.incUpdating();
          new APIHelper() {

            public void error(int error) {
              searchList.nextPageLoaded();
              title.decUpdating();
            }

            public void after(Object obj) {
              searchList.appendItems((Vector) obj);
              searchList.nextPageLoaded();
              title.decUpdating();
            }

            public Object task() throws APIException {
              Users ret = Vika.api().users.search(captcha(), screen.getLastQuery(), 10,
                already);
              return ListItems.users(ret);
            }

          }.start();
        }
      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    onlineList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        UserItem ui = (UserItem) item;
        new UserScreen(ui.getUser()).show();
      }

      public void loadNextPage(int already) {

      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    friendsList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        UserItem ui = (UserItem) item;
        new UserScreen(ui.getUser()).show();
      }

      public void loadNextPage(int already) {

      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    hintsList.setListener(new List.ListListener() {

      public void itemClick(int id, AbstractListItem item) {
        UserItem ui = (UserItem) item;
        new UserScreen(ui.getUser()).show();
      }

      public void loadNextPage(int already) {

      }

      public void specialPaint(int id, AbstractListItem item) {

      }
    });

    searchSearch.setListener(new EditTextField.EditListener() {

      private void closeSearch() {
        if (isCurrentlySearch) {
          isCurrentlySearch = false;
          if (searchLabel.getManager() == null) {
            searchSearchVFM.add(searchLabel);
          }
          tabSearch.delete(searchList);
          tabSearch.add(requestsList);
          tabSearch.add(suggestionsList);
        }
      }

      private void initSearch() {
        if (!isCurrentlySearch) {
          isCurrentlySearch = true;
          if (searchLabel.getManager() != null) {
            searchSearchVFM.delete(searchLabel);
          }
          tabSearch.delete(requestsList);
          tabSearch.delete(suggestionsList);
          searchList.setItems(new Vector());
          tabSearch.add(searchList);
        }
        screen.doSearch(searchSearch.getText());
      }

      public boolean onButtonPressed(int key) {
        if (key == 10 && searchSearch.getText().length() > 0) {
          initSearch();
          return true;
        } else if (searchSearch.getText().length() == 0) {
          closeSearch();
          return false;
        }
        return false;
      }

      public boolean onNavigationUnclick() {
        if (searchSearch.getText().length() > 0) {
          initSearch();
          return true;
        } else if (searchSearch.getText().length() == 0) {
          closeSearch();
          return true;
        }
        return false;
      }

      public void pastButtonPressed(int key) {

      }
    });

    friendsSearch.setListener(new EditTextField.EditListener() {

      public boolean onButtonPressed(int key) {
        return false;
      }

      public void pastButtonPressed(int key) {
        onlineList.filter(friendsSearch.getText());
        friendsList.filter(friendsSearch.getText());
      }
    });

    messagesSearch.setListener(new EditTextField.EditListener() {

      public boolean onButtonPressed(int key) {
        return false;
      }

      public void pastButtonPressed(int key) {
        String text = messagesSearch.getText().trim();
        if (text.length() == 0) {
          if (messagesList.getManager() == null) {
            tabMessages.delete(messagesSearchList);
            tabMessages.delete(messagesPeopleSearchList);
            tabMessages.add(messagesList);
          }
        } else {
          if (messagesList.getManager() != null) {
            tabMessages.delete(messagesList);
            messagesPeopleSearchList.setItems(new Vector());
            messagesSearchList.setItems(new Vector());
            tabMessages.add(messagesPeopleSearchList);
            tabMessages.add(messagesSearchList);
          }
          screen.searchMessages(text, key == 10 || key == 13);
        }
      }

      public void postNavigationUnclick() {
        pastButtonPressed(10);
      }
    });

    messagesList.setListener(new List.ListListener() {

      public void itemClick(int _id, AbstractListItem item) {
        if (item instanceof DialogItem) {
          DialogItem di = (DialogItem) item;
          Message m = di.getMessage();

          boolean isChat = m.isFromChat();

          if (!isChat) {
            new ConversationScreen(m.getUser()).show();
          } else {
            new ConversationScreen(new Chat(m)).show();
          }
        }
      }

      public void loadNextPage(final int already) {
        title.incUpdating();
        new APIHelper() {

          public void error(int error) {
            messagesList.nextPageLoaded();
            title.decUpdating();
          }

          public void after(Object obj) {
            messagesList.appendItems((Vector) obj);
            messagesList.nextPageLoaded();
            title.decUpdating();
          }

          public Object task() throws APIException {
            Messages ret = Vika.api().messages.getDialogs(captcha(), already, 20, 0);

            Vector last = MessagesStorage.instance.get("dialogs").copy();
            if (last != null) {
              for (int i = 0; i < ret.size(); ++i) {
                last.addElement(ret.get(i));
              }
              MessagesStorage.instance.put("dialogs", new Messages(last));
            }

            return ListItems.messages(ret);
          }

        }.start();
      }

      public void specialPaint(int id, AbstractListItem item) {

      }

    });

    messagesSearchList.setListener(new List.ListListener() {

      public void itemClick(int _id, AbstractListItem item) {
        if (item instanceof DialogItem) {
          DialogItem di = (DialogItem) item;
          Message m = di.getMessage();

          new MessageViewScreen(m).show();
        }
      }

      public void loadNextPage(final int already) {
        if (screen.getLastMessageQuery().length() > 0) {
          title.incUpdating();

          new APIHelper() {

            public void error(int error) {
              messagesSearchList.nextPageLoaded();
              title.decUpdating();
            }

            public void after(Object obj) {
              messagesSearchList.appendItems((Vector) obj);
              messagesSearchList.nextPageLoaded();
              title.decUpdating();
            }

            public Object task() throws APIException {
              Messages ret = Vika.api().messages.search(captcha(),
                screen.getLastMessageQuery(), already, 20);
              return ListItems.messages(ret);
            }

          }.start();
        }
      }

      public void specialPaint(int id, AbstractListItem item) {

      }

    });

    messagesPeopleSearchList.setListener(new List.ListListener() {

      public void itemClick(int _id, AbstractListItem item) {
        if (item instanceof ChatItem) {
          new ConversationScreen(((ChatItem) item).getChat()).show();
        } else if (item instanceof UserItem) {
          new ConversationScreen(((UserItem) item).getUser()).show();
        }
      }

      public void loadNextPage(final int already) {

      }

      public void specialPaint(int id, AbstractListItem item) {

      }

    });
  }

  public boolean keyChar(char ch, int status, int time) {
    if (ch == 'F' || ch == 'f') {
      if (currentTab == tabFriends) {
        if (!friendsSearch.isFocus()) {
          friendsSearch.setFocus();
          return true;
        }
      } else if (currentTab == tabMessages) {
        if (!messagesSearch.isFocus()) {
          messagesSearch.setFocus();
          return true;
        }
      } else if (currentTab == tabSearch) {
        if (!searchSearch.isFocus()) {
          searchSearch.setFocus();
          return true;
        }
      }
    }
    return false;
  }

  public boolean touchEvent(TouchEvent message) {
    if (getCurrentTab() != tabFriends) {
      return false;
    }

    if (message.getEvent() == TouchEvent.CLICK && message.getY(1) > banner.getHeight()) {
      int h = banner.getHeight();
      Manager mainManager = screen.getMainManager();
      if (message.getY(1) + mainManager.getVerticalScroll() > h
        + friendsSearchBar.getHeight()) {
        return friendsPaneManager.touchEvent(message, h
          - mainManager.getVerticalScroll());
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public Field getCurrentTab() {
    return currentTab;
  }

  public void setCurrentTab(Field currentTab) {
    this.currentTab = currentTab;
  }
}
