package graphic_interface;

import files_catalogs_management.FileException;
import process_management.PcbException;
import text_interface.TextInterface;
import web_management.WebException;

import java.util.concurrent.atomic.AtomicBoolean;

public class ShellThread implements Runnable {
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);


    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    public void run() {

        running.set(true);
        while (running.get()) {
            try {
                TextInterface.run();
            } catch (Exception | PcbException | WebException | FileException e) {
                e.printStackTrace();
            }
        }
    }
}
