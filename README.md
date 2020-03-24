# CakeCutter
A tiny annotation library for injecting styled attributes into custom views.

## Example
Traditional way of loading styled attributes:
```kotlin
class CustomView(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    var text: String = ""
    var number: Int = 0
    var size: Float = 0F

    init {
        val styledAttrs = ctx.obtainStyledAttributes(attrs, R.styleable.CustomView)
        text = styledAttrs.getString(R.styleable.CustomView_text) ?: text
        number = styledAttrs.getInt(R.styleable.CustomView_number, number)
        size = styledAttrs.getDimension(R.styleable.CustomView_size, size)
        styledAttrs.recycle()
    }
}
```

With cakecutter:
```kotlin
class CustomView(ctx: Context, internal val attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    @BindStyleable(R.styleable.CustomView_text) var text: String = ""
    @BindStyleable(R.styleable.CustomView_number) var number: Float = 0F
    @BindStyleable(R.styleable.CustomView_size) var size: Int = 0

    init {
        CakeCutter.bind(this)
    }
}
```
