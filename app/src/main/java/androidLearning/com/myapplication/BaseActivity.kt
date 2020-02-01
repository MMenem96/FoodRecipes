package mitchcourses.com.myapplication

import android.widget.FrameLayout
import android.widget.ProgressBar
import androidLearning.com.myapplication.R
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

abstract class BaseActivity : AppCompatActivity() {


    lateinit var progressBar: ProgressBar

    override fun setContentView(layoutResID: Int) {
        val constraintLayout: ConstraintLayout =
            layoutInflater.inflate(R.layout.activity_base, null) as ConstraintLayout
        progressBar = constraintLayout.findViewById(R.id.progress_bar)
        val frameLayout: FrameLayout = constraintLayout.findViewById(R.id.activity_content)
        layoutInflater.inflate(layoutResID, frameLayout, true)
        super.setContentView(constraintLayout)
    }

    fun showProgressBar(visibilty: Int) {
        progressBar.visibility = visibilty
    }

}