package tech.sbdevelopment.v10lift.api.lists;

import lombok.Setter;
import tech.sbdevelopment.v10lift.api.objects.Floor;

import java.util.LinkedHashMap;
import java.util.Map;

public class LiftQueue {
    private final LinkedHashMap<String, Floor> queue = new LinkedHashMap<>();

    @Setter
    private LiftQueueListener listener;

    public interface LiftQueueListener {
        void onQueueChange();
    }

    public void requestFloor(String floorName, Floor floor) {
        if (!queue.containsKey(floorName)) {
            queue.put(floorName, floor);

            if (listener != null) {
                listener.onQueueChange();
            }
        }
    }

    public boolean hasRequests() {
        return !queue.isEmpty();
    }

    public String getNextFloor(String currentFloor, boolean movingUp) {
        if (!hasRequests()) return null;

        String nextFloor = currentFloor;
        for (Map.Entry<String, Floor> entry : queue.entrySet()) {
            if ((movingUp && entry.getValue().getY() > entry.getValue().getY()) || (!movingUp && entry.getValue().getY() < entry.getValue().getY())) {
                nextFloor = entry.getKey();
            }
        }

        return nextFloor;
    }
}
