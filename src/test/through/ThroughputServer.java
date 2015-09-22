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

import galileo.event.ConcurrentEventReactor;
import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.net.ServerMessageRouter;

public class ThroughputServer {

    protected static final int PORT = 5050;

    protected static final int QUERY_SIZE = 64;
    protected static final int REPLY_SIZE = 4096;

    private ThroughputEventMap eventMap = new ThroughputEventMap();
    private ConcurrentEventReactor reactor
        = new ConcurrentEventReactor(this, eventMap, 8);
    private ServerMessageRouter messageRouter;

    private long counter;

    public void start()
    throws IOException {
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(reactor);
        reactor.start();
        messageRouter.listen(PORT);
        System.out.println("Listening on port " + PORT);
    }

    @EventHandler
    public void handleMessage(ThroughputMessage msg, EventContext context) {
        byte[] payload = msg.getPayload();
        counter += payload.length / 100000;
        if (counter % 1000 == 0) {
            System.out.print('.');
        }
    }

    public static void main(String[] args) throws Exception {
        ThroughputServer serv = new ThroughputServer();
        serv.start();
    }
}
