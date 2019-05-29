package com.lzp.producerandconsumer;

/**
 * Created by li.zhipeng on 2019-05-20.
 */
public class Consumer {

    void consume() {
        new Thread() {
            @Override
            public void run() {
                while (true) {

                    if (Shop.canConsume()) {

                        synchronized (Shop.consumeLock) {
                            System.out.println("目前商品：" + Shop.count);
                            Shop.count--;
                            System.out.println("消耗一件商品，剩余：" + Shop.count);
                        }

                        // 睡眠1秒再消费
                        Consumer.this.sleep();
                    } else {
                        Shop.notifyProducer();

                        synchronized (Shop.consumeLock) {
                            try {
                                System.out.println("等到生产，目前商品数为：" + Shop.count);
                                Shop.consumeLock.wait();
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
