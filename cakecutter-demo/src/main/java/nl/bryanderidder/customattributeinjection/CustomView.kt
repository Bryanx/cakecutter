package nl.bryanderidder.customattributeinjection

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    @Styleable var viewColor: Int = ContextCompat.getColor(ctx, android.R.color.white)
        set(value) {
            tvText.setTextColor(value)
            field = value
        }
    // this attribute allows you to use a different field name than the styleable.
    @BindStyleable("viewVisible") var visible: Boolean = true

    init {
        CakeCutter.bind(this)
    }
}
