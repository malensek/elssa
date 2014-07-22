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

import java.io.IOException;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;
import galileo.serialization.Serializer;

/**
 * General container that wraps various communication events in Galileo.
 * An EventContainer includes the type of event ({@link EventType}), an
 * identification number for the event, and a byte array representing the event
 * contents.
 *
 * Event identification numbers are used for non-blocking transactional
 * communications between a client and server; the ID number can be used to
 * track a specific transaction.
 *
 * Given a {@link GalileoEvent}, the EventContainer will serialize the event
 * contents to use as the payload for the container.
 */
public class EventContainer implements ByteSerializable {

    private static int idCounter;

    private EventType type;
    private int id;
    private byte[] payload;

    /**
     * Create a new EventContainer for the given event, and generate an ID
     * number for the event.
     *
     * <em>WARNING:</em> using a combination of user-specified IDs and
     * program-generated IDs will likely result in erratic behavior.
     */
    public EventContainer(GalileoEvent event)
    throws IOException {
        this(event, incrementIDCounter());
    }

    /**
     * Create a new EventContainer for the given event, with a user-specified
     * identification number for the event.
     *
     * <em>WARNING:</em> using a combination of user-specified IDs and
     * program-generated IDs will likely result in erratic behavior.
     */
    public EventContainer(GalileoEvent event, int eventId)
    throws IOException {
        this.type = event.getType();
        this.id = eventId;
        this.payload = Serializer.serialize(event);
    }

    /**
     * Increment the automatic ID generator and return the result.
     */
    private static synchronized int incrementIDCounter() {
        if (idCounter < 0) {
            idCounter = 0;
        }
        idCounter++;
        return idCounter;
    }

    public EventType getEventType() {
        return type;
    }

    public int getEventId() {
        return id;
    }

    public byte[] getEventPayload() {
        return payload;
    }

    public EventContainer(SerializationInputStream in)
    throws IOException {
        type = EventType.fromInt(in.readInt());
        id = in.readInt();
        payload = in.readField();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(type.toInt());
        out.writeInt(id);
        out.writeField(payload);
    }
}
