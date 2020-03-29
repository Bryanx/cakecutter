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
 *
 * Bind your styleables to fields using generated code.
 * /@Styleable var viewText corresponds to [R.styleable.CustomView_viewText]
 *
 * Steps:
 * Set the access modifier of attributeset to internal
 * Add some annotated fields and name them the same as your styleables in [attrs.xml]
 * call CakeCutter.bind(this) in init()
 * Rebuild your project.
 *
 * @author Bryan de Ridder
 */
class CustomView(ctx: Context, internal val attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    val tvText: TextView = TextView(ctx).also(this::addView)
    @Styleable var customText: String = ""
    @Styleable var customTextSize: Float = 30f
    @Styleable var customPadding: Int = 30
    @Styleable var customPosition: PositionEnum = PositionEnum.LEFT
    @BindStyleable("customVisible") var visible: Boolean = true

    // Example with a setter. This setter is called initially by the layout.
    @Styleable var customColor: Int = ContextCompat.getColor(ctx, android.R.color.white)
        set(value) {
            field = value
            tvText.setTextColor(value)
            // invalidate()
            // requestLayout()
        }

    init {
        CakeCutter.bind(this) // call this and set [attrs] to internal
    }
}
