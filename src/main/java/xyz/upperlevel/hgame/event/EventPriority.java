package xyz.upperlevel.hgame.event;

public class EventPriority {
    public static final byte LOWEST = -64;
    public static final byte LOW = -32;
    public static final byte NORMAL = 0;
    public static final byte HIGH = 32;
    public static final byte HIGHEST = 64;

    /**
     * The MONITOR priority is the last priority to be called, in this priority no listener should change the event
     */
    public static final byte MONITOR = 127;
}
