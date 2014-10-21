package org.yanex.vika.api;

import json.JSONException;
import json.JSONObject;
import org.yanex.vika.api.http.Arguments;
import org.yanex.vika.api.item.collections.Users;
import org.yanex.vika.api.util.CaptchaInfo;

public class FriendsAPI extends APIParser {

  private final Api api;

  FriendsAPI(Api api) {
    this.api = api;
  }

  public long add(CaptchaInfo captcha, long userId, String _text) throws APIException {
    return parseLong(api.process(captcha, "friends.add", Arguments.make()
        .put("user_id", userId)
        .put("text", _text)
    ));
  }

  public int areFriendsSingle(CaptchaInfo captcha, long userId) throws APIException {
    String response = api.process(captcha, "friends.areFriends", Arguments.make()
            .put("user_ids", userId)
    );

    try {
      JSONObject jso = new JSONObject(response);
      if (jso.has("response")) {
        JSONObject o = jso.getJSONArray("response").getJSONObject(0);
        return o.getInt("friend_status");
      } else {
        throw new APIException(jso);
      }
    } catch (JSONException e1) {
      throw new APIException(ErrorCodes.JSON_ERROR);
    }
  }

  public long delete(CaptchaInfo captcha, long userId) throws APIException {
    return parseLong(api.process(captcha, "friends.delete", Arguments.make()
        .put("user_id", userId)
    ));
  }

  public Users get(CaptchaInfo captcha, String _userId, int count, int offset, String _order)
      throws APIException {
    return parseUsers(api.process(captcha, "friends.get", Arguments.make()
        .putIf(_userId != null, "user_id", _userId)
        .putIf(_userId == null, "user_id", api.getToken().getUserId())
        .put("order", _order)
        .putIf(count > 0, "count", count)
        .putIf(offset > 0, "offset", offset)
        .put("fields", VkApi.PROFILE_FIELDS)
    ));
  }

  public Users getByPhones(CaptchaInfo captcha, String phones) throws APIException {
    return parseUsers(api.process(captcha, "friends.getByPhones", Arguments.make()
        .put("phones", phones)
        .put("fields", VkApi.PROFILE_FIELDS)
    ));
  }

  public Users getOnline(CaptchaInfo captcha) throws APIException {
    String code = "return API.users.get({\"user_ids\":API.friends.getOnline(),\"fields\":\""
        + VkApi.PROFILE_FIELDS + "\"});";

    return parseUsers(api.execute(captcha, code));
  }

  public Users getRequests(CaptchaInfo captcha, int count, int offset) throws APIException {
    String code = "return API.users.get({\"user_ids\":API.friends.getRequests({\"offset\":"
        + offset + ",\"count\":" + count + "}),\"fields\":\"" + VkApi.PROFILE_FIELDS + "\"});";

    return parseUsers(api.execute(captcha, code));
  }

  public Users getSuggestions(CaptchaInfo captcha, String _filter, int count, int offset)
      throws APIException {
    return parseUsers(api.process(captcha, "friends.getSuggestions", Arguments.make()
        .put("filter", _filter)
        .putIf(count > 0, "count", count)
        .putIf(offset > 0, "offset", offset)
        .put("fields", VkApi.PROFILE_FIELDS)
    ));
  }

}
