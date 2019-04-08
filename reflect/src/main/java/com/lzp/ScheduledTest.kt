package com.lzp

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 * Created by li.zhipeng on 2019/3/28.
 */
class ScheduledTest {

    private val mScheduledExecutorService = Executors.newScheduledThreadPool(4, object : ThreadFactory {
        override fun newThread(r: Runnable): Thread {
            return Thread(r)
        }
    })

    fun scheduleAtFixedRate() {
        // 循环任务，按照上一次任务的发起时间计算下一次任务的开始时间
        mScheduledExecutorService.scheduleAtFixedRate({
            println("first:" + System.currentTimeMillis() / 1000)
            Thread.sleep(3000)
        }, 1, 1, TimeUnit.SECONDS)
    }

}