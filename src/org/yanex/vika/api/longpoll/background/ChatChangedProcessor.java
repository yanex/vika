package org.yanex.vika.api.longpoll.background;

import org.yanex.vika.api.longpoll.ChatChangedUpdate;
import org.yanex.vika.api.longpoll.LongPollProcessor;
import org.yanex.vika.api.longpoll.LongPollProcessorFactory;
import org.yanex.vika.api.longpoll.LongPollUpdate;

public class ChatChangedProcessor implements LongPollProcessor {

    public final ChatChangedUpdate update;

    public ChatChangedProcessor(ChatChangedUpdate update) {
        super();
        this.update = update;
    }

    public boolean process() {
        return false;
    }

    public static class Factory implements LongPollProcessorFactory {

        public LongPollProcessor create(LongPollUpdate update) {
            return new ChatChangedProcessor((ChatChangedUpdate) update);
        }

        public Class processorFor() {
            return ChatChangedUpdate.class;
        }

    }

}
