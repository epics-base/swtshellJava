package org.epics.swtshell;

public class Swtshell {

    /**
     * swtshell main.
     * @param args
     */
    public static void main(String[] args) {
        org.epics.pvaccess.ClientFactory.start();
        org.epics.ca.ClientFactory.start();
        SwtshellFactory.swtshell();
        return;
    }

}
