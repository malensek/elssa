/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package galileo.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates the supported event types.
 *
 * @author malensek
 */
public enum EventType implements EventTypeMap {
    UNKNOWN (0),
    GENERAL (1),
    QUERY (2),
    QUERY_REQUEST (8),
    QUERY_RESPONSE (3),
    QUERY_PREAMBLE (10),
    STORAGE (4),
    STORAGE_REQUEST (7),
    SYSTEM (5),
    DEBUG (6),
    DISCONNECT(9);

    private final int type;

    private EventType(int type) {
        this.type = type;
    }

    @Override
    public int toInt() {
        return type;
    }

    static Map<Integer, EventType> typeMap = new HashMap<>();

    static {
        for (EventType t : EventType.values()) {
            typeMap.put(t.toInt(), t);
        }
    }

    public static EventType fromInt(int i) {
        EventType t = typeMap.get(i);
        if (t == null) {
            return EventType.UNKNOWN;
        }

        return t;
    }
}
