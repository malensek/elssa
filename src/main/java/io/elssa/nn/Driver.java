package io.elssa.nn;

public class Driver {

    public static void main(String[] args) throws Exception {

        ServerMessageRouter smr = new ServerMessageRouter();
        smr.listen(5555);
    }
}
