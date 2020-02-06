package mitchcourses.com.myapplication

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object AppExecutors {


    val mNetworkIO: ScheduledExecutorService by lazy {
        return@lazy Executors.newScheduledThreadPool(3)
    }

    //Executor to make db operations on db CRUD OP
    val diskIO: Executor = Executors.newSingleThreadExecutor()

    //Executor to post data into UI (Send data from background to mainThread
    val mainThreadExecutor: Executor = MainThreadExecutor()


    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }
}