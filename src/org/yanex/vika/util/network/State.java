package org.yanex.vika.util.network;

import org.yanex.vika.util.fun.Enum;

public class State extends Enum {
    public static final State
            None = new State("None", 0),
            Loading = new State("Loading", 1),
            Complete = new State("Complete", 2),
            Error = new State("Error", 3);

    private State(String name, int num) {
        super(name, num);
    }
}