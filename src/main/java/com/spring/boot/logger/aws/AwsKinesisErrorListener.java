package com.spring.boot.logger.aws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AwsKinesisErrorListener {
    interface Listener {
        void onElementAdded(Map element);
    }

    private final List<Listener> listeners = new ArrayList<>();

    public AwsKinesisErrorListener registerListener(Listener listener) {
        listeners.add(listener);
        return this;
    }

    public void offer(Map e) {
        listeners.forEach(listener -> listener.onElementAdded(e));
    }
}
