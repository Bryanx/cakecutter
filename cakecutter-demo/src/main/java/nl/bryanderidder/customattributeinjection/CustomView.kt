package nl.bryanderidder.customattributeinjection

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import nl.bryanderidder.annotations.BindStyleable


/**
 * BindStyleable retrieves the value from the layout and applies it to the setter.
 *
 * @author Bryan de Ridder
 */
class CustomView(ctx: Context, internal val attrs: AttributeSet) : FrameLayout(ctx, attrs) {

    val tvText: TextView = TextView(ctx)

    @BindStyleable(R.styleable.CustomView_view_text)
    var text: String = ""
        set(value) {
            field = value
            tvText.text = value
        }

    @BindStyleable(R.styleable.CustomView_view_textSize)
    var textSize: Float = 30f
        set(value) {
            field = value
            tvText.textSize = value
        }

    @BindStyleable(R.styleable.CustomView_view_padding)
    var textPadding: Int = 30
        set(value) {
            field = value
            tvText.setPadding(value, value, value, value)
        }

    @BindStyleable(R.styleable.CustomView_view_visible)
    var textVisibility: Boolean = true
        set(value) {
            field = value
            tvText.visibility = if (value) VISIBLE else GONE
        }

    init {
        addView(tvText)
        CakeCutter.bind(this)
    }
}