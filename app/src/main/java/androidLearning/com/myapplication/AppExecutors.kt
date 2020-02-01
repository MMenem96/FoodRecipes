package mitchcourses.com.myapplication

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object AppExecutors {


    val mNetworkIO: ScheduledExecutorService by lazy {
        return@lazy Executors.newScheduledThreadPool(3)
    }

}