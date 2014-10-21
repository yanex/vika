package org.yanex.vika.api.longpoll;

public interface LongPollProcessorFactory {

  Class processorFor();
  LongPollProcessor create(LongPollUpdate update);

}
