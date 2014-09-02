package org.geoint.bugs.mailResourceClose;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class EmailResourceLeakTest {

    //mail configuration
    public static final int SMTP_PORT = 9999;
    public static final String SMTP_HOST = "127.0.0.1";
    public static final String SMTP_PROTOCOL = "smtp";

    //test configuration
    private static final int NUM_EMAILS = 20000;
    private static final int NUM_PROCESSORS = 20;

    public static void main(String... args) throws Exception {
        ServerSetup gmConfig = new ServerSetup(SMTP_PORT, SMTP_HOST, SMTP_PROTOCOL);
        GreenMail mailServer = new GreenMail(gmConfig);
        mailServer.start();

        long startTime = System.currentTimeMillis();
        ExecutorService exec = Executors.newFixedThreadPool(NUM_PROCESSORS);

        //keep the counter external to the excutor to simplify the shutdown
        Thread counterThread = new Thread(new FileDescriptorCounterTask());
        counterThread.start();
        for (int i = 0; i < NUM_PROCESSORS; i++) {
            exec.submit(new SendEmailsWithURLAttachmentTask(Math.round(NUM_EMAILS / NUM_PROCESSORS)));
        }

        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS);

        counterThread.interrupt();
        System.out.println("Runtime (seconds): "
                + TimeUnit.SECONDS.convert(System.currentTimeMillis()
                        - startTime, TimeUnit.MILLISECONDS));
        mailServer.stop();

    }
}
