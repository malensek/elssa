/*
Copyright (c) 2013, Colorado State University
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

package galileo.test.net;

import java.io.IOException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import galileo.event.EventType;
import galileo.event.GalileoEvent;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;
import galileo.util.Checksum;

/**
 * An event for hash tests that includes a variable sized payload with its SHA1
 * checksum to ensure the event was not corrupted while being transferred
 * across the network.
 *
 * @author malensek
 */
public class HashTestEvent implements GalileoEvent {

    private byte[] data;
    private byte[] hash;

    private Random random = new Random();
    private Checksum check = new Checksum();

    public HashTestEvent(int size) {
        data = new byte[size];
        random.nextBytes(data);
        hash = check.hash(data);
    }

    public boolean verify() {
        if (Arrays.equals(hash, check.hash(data))) {
            return true;
        } else {
            BigInteger b1 = new BigInteger(1, hash);
            BigInteger b2 = new BigInteger(1, check.hash(data));
            System.out.println(b1.toString(16) + " =/= " + b2.toString(16));
            return false;
        }
    }

    /**
     * Corrupt a HashTest event on purpose.  Sneaky sneaky...
     */
    public void corrupt() {
        int r = random.nextInt(data.length);
        byte b = data[r];
        data[r] = (byte) (b + 1);
    }

    @Override
    public EventType getType() {
        return EventType.DEBUG;
    }

    @Deserialize
    public HashTestEvent(SerializationInputStream in)
    throws IOException, SerializationException {
        hash = in.readField();
        data = in.readField();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeField(hash);
        out.writeField(data);
    }
}
