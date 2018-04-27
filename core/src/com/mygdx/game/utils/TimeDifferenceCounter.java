package com.mygdx.game.utils;

import java.util.HashMap;
import java.util.Map;

public class TimeDifferenceCounter {
    private static TimeDifferenceCounter INSTANCE;

    private Map<Object, Long> timersMap;

    private TimeDifferenceCounter() {
        this.timersMap = new HashMap<Object, Long>();
    }

    public static void init() {
        INSTANCE = new TimeDifferenceCounter();
    }

    public static TimeDifferenceCounter getInstance() {
        return INSTANCE;
    }

    public void startCounter(Object key) {
        long currentMilis = System.currentTimeMillis();
        this.timersMap.put(key, currentMilis);
    }

    public long getTimeDifference(Object key) {
        if (!timersMap.containsKey(key)) {
            return -1;
        }

        long currentMilis = System.currentTimeMillis();
        long difference = currentMilis - this.timersMap.get(key);
        return difference;
    }


}
