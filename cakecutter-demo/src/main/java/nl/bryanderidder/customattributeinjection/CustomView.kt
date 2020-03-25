package nl.bryanderidder.customattributeinjection

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import nl.bryanderidder.cakecutter.annotations.BindStyleable
import nl.bryanderidder.cakecutter.annotations.Styleable


/**
 * BindStyleable retrieves the value from the layout and applies it to the setter.
 *
 * @author Bryan de Ridder
 */
class CustomView(ctx: Context, internal val attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    val tvText: TextView = TextView(ctx).also(this::addView)
    @Styleable var viewText: String = ""
    @Styleable var viewTextSize: Float = 30f
    @Styleable var viewPadding: Int = 30
    @Styleable var viewPosition: PositionEnum = PositionEnum.LEFT
        set(value) {
            tvText.setText(value.toString())
            field = value
        }
    // this attribute allows you to use a different field name than the styleable.
    @BindStyleable(styleableId = R.styleable.CustomView_viewVisible) var visible: Boolean = true

    init {
        CakeCutter.bind(this)
    }
}
