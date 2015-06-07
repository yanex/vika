package org.yanex.vika.api;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import net.rim.device.api.util.LongHashtable;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.item.*;
import org.yanex.vika.api.item.collections.Messages;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.util.CaptchaInfo;
import org.yanex.vika.storage.UserStorage;
import org.yanex.vika.util.StringUtils;
import org.yanex.vika.util.fun.Function1;
import org.yanex.vika.util.fun.Pair;
import org.yanex.vika.util.fun.Predicate;
import org.yanex.vika.util.fun.Predicates;

import java.util.Vector;

public class MessagesAPI extends APIParser {

  private final Api api;

  MessagesAPI(Api api) {
    this.api = api;
  }

  public long addChatUser(CaptchaInfo captcha, long chatId, long uid) throws APIException {
    return parseLong(api.process(captcha, "messages.addChatUser", Arguments.make()
        .put("chat_id", chatId)
        .put("user_id", uid)
    ));
  }

  public String createChat(CaptchaInfo captcha, String uids, String title) throws APIException {
    return parseString(api.process(captcha, "messages.createChat", Arguments.make()
        .putIf(title != null, "title", title)
        .put("user_ids", uids)
    ));
  }

  public long delete(CaptchaInfo captcha, String mids) throws APIException {
    return parseBulk(api.process(captcha, "messages.delete", Arguments.make()
        .put("message_ids", mids)
    ));
  }

  public long deleteDialog(CaptchaInfo captcha, String uid, String chatId, int offset, int limit)
    throws APIException {
    return parseLong(api.process(captcha, "messages.deleteDialog", Arguments.make()
        .put("user_id", uid)
        .putIf(offset > 0, "offset", offset)
        .putIf(limit > 0, "count", limit)
    ));
  }

  public long editChat(CaptchaInfo captcha, long chatId, String title) throws APIException {
    return parseLong(api.process(captcha, "messages.editChat", Arguments.make()
        .put("chat_id", chatId)
        .put("title", title)
    ));
  }

  public Messages getByIdSingle(CaptchaInfo captcha, long mid, String mids, int previewLength)
    throws APIException {
    return parseMessages(api.process(captcha, "messages.getById", Arguments.make()
        .putIf(mid != 0, "message_ids", mid)
        .putIf(mids != null, "message_ids", mids)
        .putIf(previewLength > 0, "preview_length", previewLength)
    ));
  }

  public Users getChatUsers(CaptchaInfo captcha, long chatId) throws APIException {
    return parseUsers(api.process(captcha, "messages.getChatUsers", Arguments.make()
        .put("chat_id", chatId)
        .put("fields", VkApi.PROFILE_FIELDS)
    ));
  }

  public Messages getDialogs(CaptchaInfo captcha, int offset,
                             int count, int previewLength) throws APIException {

    String param = Arguments.make()
      .putIf(offset > 0, "offset", offset)
      .putIf(count > 0, "count", count)
      .putIf(previewLength > 0, "preview_length", previewLength).toJsonString();

    String code = "var dialogs = API.messages.getDialogs(" + param + ").items@.message; "
      + "return [dialogs, API.users.get({\"fields\":\"" + VkApi.PROFILE_FIELDS
      + "\",\"user_ids\":[" + api.getToken().getUserId()
      + "]+dialogs@.user_id+dialogs@.chat_active})];";

    String response = api.execute(captcha, code);

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONArray responseJson = jso.getJSONArray("response");
        JSONArray messagesJson = responseJson.getJSONArray(0);
        JSONArray usersJson = responseJson.getJSONArray(1);

        final LongHashtable users = UserUtils.jsonToHashtable(usersJson);
        final User myUser = (User) users.get(api.getToken().getUserId());
        UserStorage.instance.updateUsers(myUser);

        return new Messages(parseMessages(messagesJson).transform(new Function1() {

          public Object apply(Object it) {
            Message.Builder message = ((Message) it).edit();
            User user = (User) users.get(message.getUserId());

            if (myUser == null || user == null) {
              return null;
            }

            message.setUser(user);
            message.setMyUser(myUser);

            if (message.getChatActive() != null) {
              Vector chatActiveUsers = new Vector();

              int addedUsers = 4;
              for (int i = 0; addedUsers > 0 && i < message.getChatActive().length; ++i) {
                User chatUser = (User) users.get(message.getChatActive()[i]);
                if (chatUser != null) {
                  chatActiveUsers.addElement(chatUser);
                  addedUsers--;
                }
              }
              message.setChatActiveUsers(new Users(chatActiveUsers));
            }

            return message.build();
          }
        }).filter(Predicates.notNull));
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public Messages getHistory(CaptchaInfo captcha, long uid, final long chatId, int offset,
                             int count) throws APIException {
    long selfId = api.getToken().getUserId();

    String param = Arguments.make()
      .putIf(uid != 0, "user_id", uid)
      .putIf(chatId != 0, "chat_id", chatId)
      .putIf(offset > 0, "offset", offset)
      .putIf(count > 0, "count", count)
      .toJsonString();

    String code = "var history = API.messages.getHistory(" + param + ").items; "
      + "return [history, API.users.get({\"fields\":\"" + VkApi.PROFILE_FIELDS
      + "\",\"user_ids\":[" + selfId + "]+history@.user_id})];";

    String response = api.execute(captcha, code);

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONArray a = jso.getJSONArray("response");
        JSONArray messagesJson = a.getJSONArray(0);
        JSONArray usersJson = a.getJSONArray(1);

        final LongHashtable users = UserUtils.jsonToHashtable(usersJson);
        final User myUser = (User) users.get(selfId);

        final Messages parsedMessages = parseMessages(messagesJson);
        return new Messages(parsedMessages.transform(new Function1() {

          public Object apply(Object it) {
            Message.Builder message = ((Message) it).edit();
            User user = (User) users.get(message.getUserId());

            if (user == null || message.isOut() && myUser == null) {
              return null;
            }

            message.setUser(user);
            message.setFromChat(chatId != 0);
            if (message.isOut()) {
              message.setMyUser(myUser);
            }

            if (message.getChatActive() != null) {
              Vector chatActiveUsers = new Vector();
              for (int i = 0; i < message.getChatActive().length; ++i) {
                User chatUser = (User) users.get(message.getChatActive()[i]);
                if (chatUser != null) {
                  chatActiveUsers.addElement(chatUser);
                }
              }
              message.setChatActiveUsers(new Users(chatActiveUsers));
            }

            return message.build();
          }
        }).filter(Predicates.notNull));
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public ActivityStatus getLastActivity(CaptchaInfo captcha, String uid) throws APIException {
    String response = api.process(
      captcha, "messages.getLastActivity", Arguments.with("user_id", uid));

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONObject o = jso.getJSONObject("response");
        int online = o.getInt("online");
        long time = o.getLong("time");

        return new ActivityStatus(online == 1, time);
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  private String jsonEscape(String s) {
    return StringUtils.replace(s, "\"", "\\\"");
  }

  public long markAsNew(CaptchaInfo captcha, String mids) throws APIException {
    return parseLong(api.process(
      captcha, "messages.markAsNew", Arguments.with("message_ids", mids)));
  }

  public long markAsRead(CaptchaInfo captcha, String mids) throws APIException {
    return parseLong(api.process(
      captcha, "messages.markAsRead", Arguments.with("message_ids", mids)));
  }

  public String onLoad(CaptchaInfo captcha) throws APIException {

    String code = "API.account.setOnline(); "
      + "var online = API.friends.getOnline(); "
      + "var fdialog = API.messages.getDialogs({\"offset\":0,\"count\":1,\"preview_length\":30}); "
      + "return [online, fdialog];";

    String response = api.execute(captcha, code);

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONArray a = jso.getJSONArray("response");

        JSONArray online = a.getJSONArray(0);
        JSONArray fdialog = a.getJSONArray(1);

        UserStorage.instance.updateOnline(online);

        JSONObject message = fdialog.getJSONObject(1);
        return message.getString("mid");
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public long removeChatUser(CaptchaInfo captcha, long chatId, long userId) throws APIException {
    return parseLong(api.process(captcha, "messages.removeChatUser", Arguments.make()
        .put("chat_id", chatId)
        .put("user_id", userId)
    ));
  }

  public long restore(CaptchaInfo captcha, String mid) throws APIException {
    return parseLong(api.process(captcha, "messages.restore", Arguments.with("message_id", mid)));
  }

  public Messages search(CaptchaInfo captcha, String q, int offset, int count)
    throws APIException {
    return parseMessages(api.process(captcha, "messages.search", Arguments.make()
        .put("q", q)
        .putIf(offset > 0, "offset", offset)
        .putIf(count > 0, "count", count)
    ));
  }

  public Pair searchAll(CaptchaInfo captcha, String q) throws APIException {
    String code = "var tres = API.messages.search({\"q\":\"" + jsonEscape(q)
      + "\",\"offset\":0,\"count\":20}); "
      + "var ures = API.messages.searchDialogs({\"fields\":\"" + VkApi.PROFILE_FIELDS
      + "\",\"q\":\"" + q + "\"}); " + "var users = API.users.get({\"fields\":\""
      + VkApi.PROFILE_FIELDS
      + "\",\"user_ids\":ures@.profile+tres.items@.id+tres.items@.chat_active}); "
      + "return [tres, ures, users];";

    String response = api.execute(captcha, code);

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONArray ma = jso.getJSONArray("response");

        JSONArray _tres = parseArray(ma.get(0));
        JSONArray _ures = parseArray(ma.get(1));
        JSONArray _users = parseArray(ma.get(2));

        LongHashtable utable = new LongHashtable();
        Vector uvector = new Vector();
        int i;

        for (i = 0; i < _users.length(); ++i) {
          JSONObject o = _users.getJSONObject(i);
          User u = new User(o);
          utable.put(u.getId(), u);
          uvector.addElement(u);
        }
        UserStorage.instance.updateUsers(new Users(uvector));

        Vector users = new Vector();
        Messages messages = parseMessages(_tres);

        for (i = 0; i < _ures.length(); ++i) {
          JSONObject o = _ures.getJSONObject(i);
          String type = o.getString("type");
          if (type.equals("profile")) {
            User u = new User(o);
            users.addElement(u);
          } else if (type.equals("chat")) {
            long chatId = o.getLong("chat_id");
            String title = o.getString("title");
            Vector vcu = new Vector();
            JSONArray cu = o.getJSONArray("users");
            for (int k = 0; k < cu.length(); ++k) {
              long userId = cu.getLong(k);
              User u = (User) utable.get(userId);
              if (u != null) {
                vcu.addElement(u);
              }
            }
            if (vcu.size() > 0) {
              Chat c = new Chat(chatId, title, new Users(vcu), vcu.size());
              users.addElement(c);
            }
          }
        }

        return new Pair(new Users(users), messages);
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public String send(CaptchaInfo captcha, long uid, long chatId, String message,
                     String attachments, String forwardMessages, String title, int type, String guid,
                     double latitude, double longitude) throws APIException {
    return parseString(api.process(captcha, "messages.send", Arguments.make()
        .putIf(uid != 0, "user_id", uid)
        .putIf(chatId != 0, "chat_id", chatId)
        .putIf(message != null, "message", message)
        .putIf(attachments != null, "attachment", attachments)
        .putIf(forwardMessages != null, "forward_messages", forwardMessages)
        .putIf(title != null, "title", title)
        .putIf(type > 0, "type", type)
        .putIf(guid != null, "guid", guid)
        .putIf(latitude != Double.MIN_VALUE, "latitude", latitude)
        .putIf(longitude != Double.MIN_VALUE, "longitude", longitude)
    ));
  }

  public long setActivity(CaptchaInfo captcha, long uid, long chatId, String type)
    throws APIException {
    long id = (uid > 0) ? uid : APIUtils.getChatId(chatId);
    return parseBulk(api.process(captcha, "messages.setActivity", Arguments.make()
        .putIf(uid != 0, "user_id", id)
        .putIf(type != null, "type", type).putIf(type == null, "type", "typing")
    ));
  }

}
