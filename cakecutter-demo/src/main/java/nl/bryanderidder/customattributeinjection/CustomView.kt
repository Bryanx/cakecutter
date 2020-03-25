package nl.bryanderidder.customattributeinjection

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import nl.bryanderidder.cakecutter.annotations.BindStyleable
import nl.bryanderidder.cakecutter.annotations.Styleable


/**
 * BindStyleable retrieves the value from the layout and applies it to the setter.
 *
 * @author Bryan de Ridder
 */
class CustomView(ctx: Context, internal val attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    @Styleable var viewText: String = ""
    @Styleable var viewTextSize: Float = 30f
    @Styleable var viewPadding: Int = 30
    // this attribute allows you to use a different field name than the styleable.
    @BindStyleable(styleableId = R.styleable.CustomView_viewVisible) var visible: Boolean = true

    init {
        CakeCutter.bind(this)
    }
}