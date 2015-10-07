package io.elssa.test.through;

import io.elssa.event.EventMap;

class ThroughputEventMap extends EventMap {
    public ThroughputEventMap() {
        addMapping(ThroughputMessage.class);
    }
}
