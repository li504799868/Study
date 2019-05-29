package com.lzp.producerandconsumer;

/**
 * Created by li.zhipeng on 2019-05-20.
 */
public class Shop {

    private static final int MAXCOUNT = 10;

    public static int count = 0;

    public static final Object produceLock = new Object();

    public static final Object consumeLock = new Object();

    public static void addProducer(Producer producer) {
        producer.produce();
    }

    public static void addConsumer(Consumer consumer) {
        consumer.consume();
    }

    public static boolean needProduce() {
        synchronized (consumeLock) {
            return count < MAXCOUNT;
        }
    }

    public static boolean canConsume() {
        synchronized (consumeLock) {
            return count > 0;
        }
    }

    public static void notifyConsumer() {
        // 通知消费者
        synchronized (consumeLock) {
            consumeLock.notifyAll();
        }
    }

    public static void notifyProducer() {
        synchronized (produceLock) {
            produceLock.notifyAll();
        }
    }

}
