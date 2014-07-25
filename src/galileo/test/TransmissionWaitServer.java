package galileo.test.net;

import java.io.IOException;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

public class TransmissionWaitServer implements MessageListener {

    protected static final int PORT = 5050;

    private int counter = 0;
    private ServerMessageRouter messageRouter;

    public void listen()
    throws IOException {
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(this);
        messageRouter.listen(PORT);
        System.out.println("Listening...");
    }

    @Override
    public void onConnect(NetworkDestination endpoint) { }

    @Override
    public void onDisconnect(NetworkDestination endpoint) { }

    @Override
    public void onMessage(GalileoMessage message) {
        counter++;
//        if (counter % 1000 == 0) {
//            System.out.println("Messages received: " + counter);
//        }

        System.out.println(counter);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    throws Exception {
        TransmissionWaitServer tws = new TransmissionWaitServer();
        tws.listen();
    }
}
