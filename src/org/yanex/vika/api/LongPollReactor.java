package org.yanex.vika.api;

import org.yanex.vika.api.longpoll.LongPollListener;
import org.yanex.vika.api.longpoll.LongPollProcessor;
import org.yanex.vika.api.longpoll.LongPollProcessorFactory;
import org.yanex.vika.api.longpoll.LongPollUpdate;
import org.yanex.vika.api.longpoll.background.*;
import org.yanex.vika.storage.FastStorage;
import org.yanex.vika.util.bb.Notifications;
import org.yanex.vika.util.fun.*;

public class LongPollReactor implements LongPollListener {

  public static final LongPollReactor instance = new LongPollReactor();

  private final ImmutableList processors =
      new ImmutableListBuilder(LongPollProcessorFactory.class)
          .add(new ChatChangedProcessor.Factory())
          .add(new FlagsProcessor.Factory())
          .add(new FriendOfflineProcessor.Factory())
          .add(new FriendOnlineProcessor.Factory())
          .add(new MessageProcessor.Factory())
          .build();

  private void beep() {
    long l = FastStorage.instance.getLong("disable");

    if (l <= System.currentTimeMillis()) {
      Notifications.getInstance().trigger();
    }
  }

  public void longPollUpdate(RichVector updates) {
    // updates vector => (processor factory => processor) => filter null => fold
    Object beep = updates.transform(new Function1() {

      public Object apply(final Object update) {
        Object o = processors.findFirst(new Predicate() {

          public boolean pred(Object it) {
            return ((LongPollProcessorFactory) it).processorFor().isInstance(update);
          }
        });
        if (o != null) {
          LongPollProcessorFactory f = (LongPollProcessorFactory) o;
          return f.create((LongPollUpdate) update);
        } else {
          return null;
        }
      }
    }).filter(Predicates.notNull).foldr(Boolean.FALSE, new Function2() {

      public Object apply(Object o1, Object o2) {
        LongPollProcessor p = (LongPollProcessor) o2;
        return new Boolean(((Boolean) o1).booleanValue() || p.process());
      }
    });

    if (Boolean.TRUE.equals(beep)) {
      beep();
    }
  }

}
