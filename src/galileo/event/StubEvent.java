package galileo.event;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class StubEvent implements Event {

    public StubEvent() { }

    @Deserialize
    public StubEvent(SerializationInputStream in)
    throws IOException { }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException { }
}
