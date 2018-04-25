package com.mygdx.game.framework;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;

public class EventDispatcher {
    private static EventDispatcher INSTANCE;

    private boolean disabled;
    private Deque<Event> eventList;
    private PriorityQueue<ScheduledEvent> scheduledEventList;
    private int pollEventListSize;

    public static void init() {
        INSTANCE = new EventDispatcher();
    }

    public static EventDispatcher getInstance() {
        return INSTANCE;
    }

    private EventDispatcher() {
        this.disabled = false;
        this.eventList = new ArrayDeque<Event>();
        this.scheduledEventList = new PriorityQueue<ScheduledEvent>();
        this.pollEventListSize = 0;
    }

    public void shutdown() {
        while (!eventList.isEmpty()) {
            poll();
        }

        while (!scheduledEventList.isEmpty()) {
            scheduledEventList.poll().cancel();
        }
        disabled = true;
    }

    public void poll() {
        int loops = 0;

        for (int count = 0, max = scheduledEventList.size(); count < max && !scheduledEventList.isEmpty(); count++) {
            ScheduledEvent scheduledEvent = scheduledEventList.peek();
            if (scheduledEvent.getRemainingTicks() > 0) {
                break;
            }
            scheduledEventList.poll();
            scheduledEvent.execute();

            if (scheduledEvent.nextCycle()) {
                scheduledEventList.add(scheduledEvent);
            }
        }

        this.pollEventListSize = eventList.size();
        loops = 0;
        while (pollEventListSize > 0) {
            if (loops > 50) {
                // log something wrong
                break;
            }

            for (int i = 0; i < pollEventListSize; i++) {
                Event event = eventList.getFirst();
                eventList.removeFirst();
                event.execute();
            }
            pollEventListSize = eventList.size();

            loops++;
        }
    }

    public ScheduledEvent scheduleEvent(final ScheduledEvent scheduledEvent) {
        if (disabled) {
            return new ScheduledEvent() {
                @Override
                public int getDelay() {
                    return scheduledEvent.getDelay();
                }

                @Override
                public void callback() {
                }
            }.setMaxCycles(1);
        }

        if (scheduledEvent.getDelay() < 0) {
            throw new RuntimeException();
        }
        scheduledEvent.setMaxCycles(1);

        scheduledEventList.add(scheduledEvent);
        return scheduledEvent;
    }

    public Event addEvent(Event event, boolean pushFront) {
        if (this.disabled) {
            return new Event() {
                @Override
                public void callback() {}
            };
        }

        if (pushFront) {
            this.eventList.addFirst(event);
            this.pollEventListSize++;
        } else {
            this.eventList.addLast(event);
        }

        return event;
    }

}
