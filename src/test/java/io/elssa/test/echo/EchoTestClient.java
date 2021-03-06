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

package io.elssa.test.echo;

import java.io.IOException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.elssa.net.ClientMessageRouter;
import io.elssa.net.ElssaMessage;
import io.elssa.net.MessageListener;
import io.elssa.net.NetworkEndpoint;

public class EchoTestClient implements MessageListener {

    private ClientMessageRouter messageRouter;
    private NetworkEndpoint netDest;

    private Set<String> sentMessages
        = Collections.synchronizedSet(new HashSet<String>());

    public EchoTestClient(NetworkEndpoint netDest) throws Exception {
        this.netDest = netDest;
        messageRouter = new ClientMessageRouter();
        messageRouter.addListener(this);
    }

    @Override
    public void onConnect(NetworkEndpoint endpoint) { }

    @Override
    public void onDisconnect(NetworkEndpoint endpoint) {
        System.out.println("Disconnected from server. "
                + "Goodbye, and have a nice day!");
        System.exit(0);
    }

    @Override
    public void onMessage(ElssaMessage message) {
        String messageStr = new String(message.payload());
        if (sentMessages.contains(messageStr) == false) {
            System.out.println("Corrupted message: " + messageStr);
        } else {
            sentMessages.remove(messageStr);
            System.out.println("-> " + messageStr);
        }
    }

    public void sendMessage()
    throws IOException {
        byte[] payload = UUID.randomUUID().toString().getBytes();
        ElssaMessage message = new ElssaMessage(payload);
        sentMessages.add(new String(payload));
        messageRouter.sendMessage(netDest, message);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("ScaleTestClient <host> <num-messages>");
            return;
        }

        String hostname = args[0];
        int messages = Integer.parseInt(args[1]);

        NetworkEndpoint server = new NetworkEndpoint(
                hostname, EchoTestServer.PORT);
        EchoTestClient etc = new EchoTestClient(server);

        for (int i = 0; i < messages; ++i) {
            etc.sendMessage();
        }
    }
}
