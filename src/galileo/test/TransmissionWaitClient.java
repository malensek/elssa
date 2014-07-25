package galileo.test.net;

import java.io.IOException;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.NetworkDestination;
import galileo.net.Transmission;

public class TransmissionWaitClient {

    private static final int MAX_MSG = 10000;

    private NetworkDestination server;
    private ClientMessageRouter messageRouter;

    public TransmissionWaitClient(NetworkDestination server)
    throws IOException {
        this.server = server;
        messageRouter = new ClientMessageRouter();
    }

    public void send(int numMessages)
    throws InterruptedException, IOException {
        for (int i = 0; i < numMessages; ++i) {
            byte[] payload = new byte[MAX_MSG];
            GalileoMessage msg = new GalileoMessage(payload);
            Transmission t = messageRouter.sendMessage(server, msg);
            t.finish();
        }
    }

    public void disconnect() {
        messageRouter.forceShutdown();
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: galileo.test.net.TransmissionWaitClient "
                    + "<server> <num-messages>");
            System.exit(1);
        }

        NetworkDestination server = new NetworkDestination(
                args[0], TransmissionWaitServer.PORT);

        TransmissionWaitClient twc = new TransmissionWaitClient(server);
        twc.send(Integer.parseInt(args[1]));
        twc.disconnect();
    }
}
