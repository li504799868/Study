package com.lzp.producerandconsumer;

/**
 * Created by li.zhipeng on 2019-05-20.
 */
public class Producer {

    void produce() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (Shop.needProduce()) {
                        synchronized (Shop.consumeLock) {
                            Shop.count++;
                            System.out.println("生产1一个商品，目前的商品数量为：" + Shop.count);
                            // 已经有了商品，通知消费者消费
                        }

                        Shop.notifyConsumer();
                        // 睡眠1秒再生产
                        Producer.this.sleep();
                    } else {
                        synchronized (Shop.produceLock) {
                            try {
                                System.out.println("商品数已经为最大，停止生产：" + Shop.count);
                                Shop.produceLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }

    private void sleep(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
