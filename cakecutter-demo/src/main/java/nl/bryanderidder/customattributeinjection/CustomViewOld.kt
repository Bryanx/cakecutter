package nl.bryanderidder.customattributeinjection

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView


/**
 * Example how to create a layout custom attribute and a programmatic setter
 * At the moment these are separated.
 *
 * @author Bryan de Ridder
 */
class CustomViewOld(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    var defaultText: String = "Hello"
    var defaultTextSize: Float = 20f
    var defaultPadding: Int = 30
    var defaultVisibility: Boolean = true
    var text: String = defaultText
    var textSize: Float = defaultTextSize
    var textPadding: Int = defaultPadding
    var textVisibility: Boolean = true

    init {
        val styledAttrs = ctx.obtainStyledAttributes(attrs, R.styleable.CustomView)
        text = styledAttrs.getString(R.styleable.CustomView_viewText) ?: defaultText
        textSize = styledAttrs.getDimension(R.styleable.CustomView_viewTextSize, defaultTextSize)
        textPadding = styledAttrs.getInt(R.styleable.CustomView_viewPadding, defaultPadding)
        textVisibility = styledAttrs.getBoolean(R.styleable.CustomView_viewVisible, defaultVisibility)
        styledAttrs.recycle()
    }

}