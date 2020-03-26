package nl.bryanderidder.cakecutter.annotations

/**
 *
 * Bind a field to a styleable.
 * @param styleableId the id of the styleable. For example: R.styleable.CustomView_myProp
 *
 * @author Bryan de Ridder
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class BindStyleable(val styleableId: String)