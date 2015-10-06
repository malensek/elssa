/*
Copyright (c) 2015, Colorado State University
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

package io.elssa.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Represents a TCP network endpoint; a host/port pair.
 *
 * @author malensek
 */
public class NetworkEndpoint {

    protected String hostname;
    protected int port;

    public NetworkEndpoint(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public NetworkEndpoint(SocketAddress address) {
        if (address instanceof InetSocketAddress) {
            InetSocketAddress iAddress = (InetSocketAddress) address;
            this.hostname = iAddress.getHostName();
            this.port = iAddress.getPort();
        }
    }

    public String hostname() {
        return hostname;
    }

    public int port() {
        return port;
    }

    private String stringRepresentation() {
        return hostname + ":" + port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        NetworkEndpoint other = (NetworkEndpoint) obj;
        return this.stringRepresentation().equals(other.stringRepresentation());
    }

    @Override
    public String toString() {
        return stringRepresentation();
    }
}
