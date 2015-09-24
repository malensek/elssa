/*
Copyright (c) 2014, Colorado State University
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

package io.elssa.event;

import io.elssa.serialization.SerializationException;
import io.elssa.serialization.Serializer;

import java.io.IOException;

import galileo.net.GalileoMessage;

/**
 * Wrapper for simple {@link EventWithSynopsis} events.  This wrapper does not
 * support any event type other than EventWithSynopsis.
 *
 * @author malensek
 */
public class SynopsisWrapper implements EventWrapper {

    @Override
    public GalileoMessage wrap(Event e)
    throws IOException {
        if (e instanceof EventWithSynopsis == false) {
            throw new IOException("This wrapper can only handle "
                    + "EventWithSynopsis instances");
        }

        byte[] rawMessage = Serializer.serialize(e);
        return new GalileoMessage(rawMessage);
    }

    @Override
    public Event unwrap(GalileoMessage msg)
    throws IOException, SerializationException {

        EventWithSynopsis event = Serializer.deserialize(
                EventWithSynopsis.class, msg.getPayload());
        return event;
    }
}
