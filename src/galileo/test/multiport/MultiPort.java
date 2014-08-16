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

package galileo.test.multiport;

import java.io.IOException;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

public class MultiPort implements MessageListener {

    private ServerMessageRouter messageRouter;

    public MultiPort() {
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(this);
    }

    public void listen(int port)
    throws IOException {
        messageRouter.listen(port);
    }


    @Override
    public void onConnect(NetworkDestination endpoint) { }

    @Override
    public void onDisconnect(NetworkDestination endpoint) { }

    @Override
    public void onMessage(GalileoMessage message) {
        System.out.println("Received message on port: "
                + message.getContext().getServerPort());
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: galileo.test.net.MultiPort "
                    + "port1 port2 portN...");
            System.exit(1);
        }

        MultiPort mp = new MultiPort();

        for (String arg : args) {
            int port = Integer.parseInt(arg);
            mp.listen(port);
        }
    }
}
