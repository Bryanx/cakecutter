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

    val tvText: TextView = TextView(ctx)

    var defaultText: String = "Hello"

    var defaultTextSize: Float = 20f

    var defaultPadding: Int = 30

    var defaultVisibility: Boolean = true

    var text: String = defaultText
        set(value) {
            field = value
            tvText.text = value
        }

    var textSize: Float = defaultTextSize
        set(value) {
            field = value
            tvText.textSize = value
        }

    var textPadding: Int = defaultPadding
        set(value) {
            field = value
            tvText.setPadding(value, value, value, value)
        }

    var textVisibility: Boolean = true
        set(value) {
            field = value
            tvText.visibility = if (value) VISIBLE else GONE
        }

    init {
        addView(tvText)
        val styledAttrs = ctx.obtainStyledAttributes(attrs, R.styleable.CustomView)
        text = styledAttrs.getString(R.styleable.CustomView_view_text) ?: defaultText
        textSize = styledAttrs.getDimension(R.styleable.CustomView_view_textSize, defaultTextSize)
        textPadding = styledAttrs.getInt(R.styleable.CustomView_view_padding, defaultPadding)
        textVisibility = styledAttrs.getBoolean(R.styleable.CustomView_view_padding, defaultVisibility)
        styledAttrs.recycle()
    }

}