package mitchcourses.com.myapplication.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import androidLearning.com.myapplication.R


class HorizontalDottedProgress : View {
    private val TAG = "HorizontalDottedProgres"


    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    private var dotRadius: Int = 5
    private var bounceDotRadius: Int = 8
    private var dotPosition: Int = 1
    private var dotsAmount: Int = 10

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.setColor(resources.getColor(R.color.colorAccent))
        createDot(canvas, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //Animation called when attached to the window
        startAnimation()
    }

    private fun createDot(canvas: Canvas?, paint: Paint) {
        /*Here i have set the progress bar with 10 dots, so repeat
        and when i=mDotPosition then increase the radius of the dot
        */
        for (dot in 1..10) {
            if (dot == dotPosition) {
                canvas?.drawCircle(
                    (10 + (dot * 20)).toFloat(),
                    bounceDotRadius.toFloat(),
                    bounceDotRadius.toFloat(),
                    paint
                )
            } else {
                canvas?.drawCircle(
                    (10 + (dot * 20)).toFloat(),
                    bounceDotRadius.toFloat(),
                    dotRadius.toFloat(),
                    paint
                )
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = 0
        var height = 0
        val calculateWidth = (20 * 9)
        width = calculateWidth
        height = bounceDotRadius * 2
        setMeasuredDimension(width, height)
    }


    private fun startAnimation() {
        val bounceAnimation: BounceAnimation =
            BounceAnimation()
        bounceAnimation.duration = 100
        bounceAnimation.repeatCount = Animation.INFINITE
        bounceAnimation.interpolator = LinearInterpolator()
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                dotPosition++
                if (dotPosition == dotsAmount)
                    dotPosition = 1

                Log.d(TAG, "----On Animation Repeat----");

            }

            override fun onAnimationEnd(animation: Animation?) {

            }


            override fun onAnimationStart(animation: Animation?) {

            }

        })
        startAnimation(bounceAnimation)
    }


}

class BounceAnimation : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
    }

}
