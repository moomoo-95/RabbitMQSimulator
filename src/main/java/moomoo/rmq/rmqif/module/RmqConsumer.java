package moomoo.rmq.rmqif.module;

import java.util.concurrent.BlockingQueue;

public class RmqConsumer implements Runnable {

    private final BlockingQueue<String> messageQueue;

    public RmqConsumer(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        messageProcessing();
    }

    private void messageProcessing() {

    }
}
