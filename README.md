# CakeCutter
A tiny annotation library for injecting styled attributes into custom views.

## Example
Traditional way of loading styled attributes:
```kotlin
class CustomView(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    var customText: String = ""
    var customNumber: Int = 0
    var customSize: Float = 0F

    init {
        val styledAttrs = ctx.obtainStyledAttributes(attrs, R.styleable.CustomView)
        try {
            text = styledAttrs.getString(R.styleable.CustomView_customText) ?: text
            number = styledAttrs.getInt(R.styleable.CustomView_customNumber, number)
            size = styledAttrs.getDimension(R.styleable.CustomView_customSize, size)
        } finally {
          styledAttrs.recycle()
        }
    }
}
```

With cakecutter:
```kotlin
class CustomView(ctx: Context, internal val attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    @Styleable var customText: String = ""
    @Styleable var customNumber: Float = 0F
    @Styleable var customSize: Int = 0

    init {
        CakeCutter.bind(this)
    }
}
```
The styleables are bound by property name, the default values are the intial values of the properties.

Some advantages:
* Default values are assigned once instead of twice.
* Layout/programmatic setters are combined.
* Less boilerplate.

Alternative annotation:
```kotlin
@BindStyleable(R.styleable.CustomView_customText) var text: String = ""
```
With this annotation the props can have different names than the styleables.

## Note
This project is more of an expirement/study on annotation libraries and [ButterKnife](https://github.com/JakeWharton/butterknife).

<br>
<br>


The cake is now ready to be served.

🍰

