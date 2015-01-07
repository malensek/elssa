package galileo.test.through;

import galileo.event.EventMap;

class ThroughputEventMap extends EventMap {
    public ThroughputEventMap() {
        addMapping(ThroughputMessage.class);
    }
}
