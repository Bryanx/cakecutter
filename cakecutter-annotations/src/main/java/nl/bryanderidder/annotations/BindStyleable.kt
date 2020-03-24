package nl.bryanderidder.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class BindStyleable(val value: Int)