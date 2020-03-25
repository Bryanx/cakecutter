package nl.bryanderidder.cakecutter.annotations

/**
 *
 * Bind a field to a styleable.
 * The field name needs to be the same as the styleable.
 *
 * @author Bryan de Ridder
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Styleable