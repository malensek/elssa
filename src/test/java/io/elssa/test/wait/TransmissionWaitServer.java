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

package io.elssa.test.wait;

import java.io.IOException;

import io.elssa.net.ElssaMessage;
import io.elssa.net.MessageListener;
import io.elssa.net.NetworkEndpoint;
import io.elssa.net.ServerMessageRouter;

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
    public void onConnect(NetworkEndpoint endpoint) { }

    @Override
    public void onDisconnect(NetworkEndpoint endpoint) { }

    @Override
    public void onMessage(ElssaMessage message) {
        counter++;
        if (counter % 1000 == 0) {
            System.out.println("Messages received: " + counter);
        }

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
