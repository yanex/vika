package org.yanex.vika.local;

import org.yanex.vika.gui.screen.VkMainScreen;

public class CountHelper {

  public static String attachmentsString(int count) {
    return CountHelper.count(count, "Вложение", "вложение", "вложения", "вложений",
        "Attachment", "attachments");
  }

  private static String count(long count, String rus1, String rusSingle, String rus24,
                              String rus5, String enSingle, String enPlural) {
    boolean russian = VkMainScreen.tr(VikaResource.LANGUAGE).equals("русский");

    if (russian) {
      if (count == 1) {
        return rus1;
      }
      if (count % 10 == 1 && count % 100 - count % 10 != 10) {
        return count + " " + rusSingle;
      } else if (count % 10 > 1 && count % 10 < 5 && count % 100 - count % 10 != 10) {
        return count + " " + rus24;
      } else {
        return count + " " + rus5;
      }
    } else {
      if (count == 1) {
        return enSingle;
      } else {
        return count + " " + enPlural;
      }
    }
  }

  public static String documentsString(int count) {
    return CountHelper.count(count, "Документ", "документ", "документа", "документов",
        "Document", "documents");
  }

  public static String forwardedMessagesString(int count) {
    return CountHelper.count(count, "1 пересланное сообщение", "пересланное сообщение",
        "пересланных сообщения", "пересланных сообщений", "1 forwarded message",
        "forwarded messages");
  }

  public static String hoursString(long count) {
    return CountHelper.count(count, "час назад", "час назад", "часа назад", "часов назад",
        "an hour ago", "hours ago");
  }

  public static String minutesString(long count) {
    return CountHelper.count(count, "минуту назад", "минуту назад", "минуты назад",
        "минут назад", "a minute ago", "minutes ago");
  }

  public static String photosString(int count) {
    return CountHelper.count(count, "Фотография", "фотография", "фотографии", "фотографий",
        "Photo", "photos");
  }

  public static String secondsString(long count) {
    return CountHelper.count(count, "секунду назад", "секунду назад", "секунды назад",
        "секунд назад", "a minute ago", "minutes ago");
  }

  public static String talkPeopleString(int count) {
    return CountHelper.count(count, "1 Участник беседы", "участник беседы", "участника беседы",
        "участников беседы", "Member", "members");
  }

}
