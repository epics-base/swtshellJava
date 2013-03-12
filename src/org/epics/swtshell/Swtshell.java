package org.epics.swtshell;

public class Swtshell {

    /**
     * @param args
     */
    public static void main(String[] args) {
        org.epics.pvaccess.ClientFactory.start();
        org.epics.caV3.ClientFactory.start();
        SwtshellFactory.swtshell();
        return;
    }

}
