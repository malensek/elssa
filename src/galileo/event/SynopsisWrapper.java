package galileo.event;

import java.io.IOException;

import galileo.net.GalileoMessage;
import galileo.serialization.SerializationException;
import galileo.serialization.Serializer;

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
