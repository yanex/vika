package org.yanex.vika;

import org.yanex.vika.api.APIException;
import org.yanex.vika.api.item.User;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.item.collections.UsersChats;
import org.yanex.vika.api.util.APIHelper;
import org.yanex.vika.gui.list.List;
import org.yanex.vika.gui.list.List.ListListener;
import org.yanex.vika.gui.list.converter.ListItems;
import org.yanex.vika.gui.list.item.AbstractListItem;
import org.yanex.vika.gui.list.item.UserItem;
import org.yanex.vika.gui.screen.VkMainScreen;
import org.yanex.vika.gui.widget.VkCompactTitleField;
import org.yanex.vika.local.VikaResource;
import org.yanex.vika.storage.UsersStorage;

import java.util.Vector;

class UserSelectScreen extends VkMainScreen implements ListListener {

    private final VkCompactTitleField title;
    private final List hintsList;
    private final List allFriendsList;

    private UserSelectListener listener;

    public UserSelectScreen() {
        title = new VkCompactTitleField(tr(VikaResource.User_select));
        setBanner(title);

        hintsList = new List();
        allFriendsList = new List();

        hintsList.setOwner(this);
        allFriendsList.setOwner(this);

        hintsList.setListener(this);
        allFriendsList.setListener(this);

        add(hintsList);
        add(allFriendsList);
    }

    public UserSelectListener getListener() {
        return listener;
    }

    public void itemClick(int id, AbstractListItem item) {
        close();
        if (item instanceof UserItem) {
            UserItem ui = (UserItem) item;
            if (listener != null) {
                listener.onUserSelect(ui.getUser());
            }
        }
    }

    public void setListener(UserSelectListener listener) {
        this.listener = listener;
    }

    public void loadNextPage(int already) {
        //empty
    }

    public void specialPaint(int id, AbstractListItem item) {
        //empty
    }

    protected boolean keyChar(char c, int status, int time) {
        if (c == 27) {
            if (listener != null) {
                listener.onUserSelect(null);
            }
            close();
            return true;
        }

        return super.keyChar(c, status, time);
    }

    protected void onUiEngineAttached(boolean attached) {
        super.onUiEngineAttached(attached);
        if (attached) {
            loadHints();
        }
    }

    private void loadAllFriends() {
        title.incUpdating();

        new APIHelper() {

            public void error(int error) {
                title.decUpdating();
            }

            public Object task() throws APIException {
                Users uAllUsers = UsersStorage.instance.get("all");
                if (uAllUsers != null) {
                    return ListItems.sortedUsers(uAllUsers);
                }

                Users ret = Vika.api().friends.get(captcha(), null, 0, 0, "name");

                Vector modified = new Vector();
                for (int i = 0; i < ret.size(); ++i) {
                    User user = new User(ret.get(i));
                    modified.addElement(new User(user, false, user.getLastSeen()));
                }
                UsersStorage.instance.put("all", new UsersChats(modified.size(), modified));
                return ListItems.sortedUsers(ret);
            }

            public void after(Object listItems) {
                title.decUpdating();
                allFriendsList.setItems((Vector) listItems);
            }
        }.start();
    }

    private void loadHints() {
        title.incUpdating();
        new APIHelper() {

            public void error(int error) {
                loadAllFriends();
                title.decUpdating();
            }

            public Object task() throws APIException {
                Users uHints = UsersStorage.instance.get("hints");
                if (uHints != null) {
                    return ListItems.users(uHints);
                }

                Users ret = Vika.api().friends.get(captcha(), null, 5, 0, "hints");

                Vector modified = new Vector();
                for (int i = 0; i < ret.size(); ++i) {
                    User user = new User(ret.get(i));
                    modified.addElement(new User(user, false, user.getLastSeen()));
                }
                UsersStorage.instance.put("hints",
                        new UsersChats(modified.size(), modified));
                return ListItems.users(ret);
            }

            public void after(Object listItems) {
                hintsList.setItems((Vector) listItems);
                loadAllFriends();
                title.decUpdating();
            }
        }.start();
    }

    static interface UserSelectListener {
        public void onUserSelect(User user);
    }

}
