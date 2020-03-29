package nl.bryanderidder.customattributeinjection

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.Integer.getInteger


/**
 * 
 * The traditional way of loading styled attributes
 *
 * @author Bryan de Ridder
 */
class CustomViewOld(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    val tvText: TextView = TextView(ctx).also(this::addView)

    var defaultText: String = "Hello"
    var defaultTextSize: Float = 20f
    var defaultPadding: Int = 30
    var defaultVisibility: Boolean = true
    var defaultPosition: PositionEnum = PositionEnum.LEFT
    var defaultColor: Int = ContextCompat.getColor(ctx, android.R.color.white)

    var customText: String = defaultText
    var customTextSize: Float = defaultTextSize
    var customPadding: Int = defaultPadding
    var customPosition: PositionEnum = defaultPosition
    var customVisible: Boolean = defaultVisibility

    var customColor: Int = defaultColor
        set(value) {
            field = value
            tvText.setTextColor(value)
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CustomView)
            .apply {
                try {
                    customVisible = getBoolean(R.styleable.CustomView_customVisible, customVisible)
                    customText = getString(R.styleable.CustomView_customText) ?: defaultText
                    customTextSize = getDimension(R.styleable.CustomView_customTextSize, defaultTextSize)
                    customPadding = getInt(R.styleable.CustomView_customPadding, defaultPadding)
                    customPosition = PositionEnum.values()[getInteger(
                        R.styleable.CustomView_customPosition,
                        defaultPosition.ordinal
                    )]
                    customColor = getInt(R.styleable.CustomView_customColor, defaultColor)
                } finally {
                    recycle()
                }
            }
    }
}