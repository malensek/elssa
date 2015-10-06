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

package galileo.test.through;

import java.io.IOException;

import galileo.event.BasicEventWrapper;
import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.test.util.PerformanceTimer;

public class ThroughputClient {

    private ClientMessageRouter messageRouter;
    private NetworkDestination netDest;
    private ThroughputEventMap eventMap = new ThroughputEventMap();
    private BasicEventWrapper wrapper = new BasicEventWrapper(eventMap);

    public ThroughputClient(NetworkDestination netDest) throws Exception {
        this.netDest = netDest;
        messageRouter = new ClientMessageRouter();
    }

    public void send()
    throws IOException {
        ThroughputMessage msg = new ThroughputMessage(1000 * 1000);
        messageRouter.sendMessage(netDest, wrapper.wrap(msg));
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("galileo.test.net.ScaleTestClient "
                    + "host num-threads");
            System.out.println("Add a 3rd parameter to turn on verbose mode.");
            return;
        }

        String hostname = args[0];
        ThroughputClient client = new ThroughputClient(
                new NetworkDestination(args[0], ThroughputServer.PORT));
        for (int i = 0; i < 10000; ++i) {
            client.send();
        }
        System.out.println("All done.");
    }
}
