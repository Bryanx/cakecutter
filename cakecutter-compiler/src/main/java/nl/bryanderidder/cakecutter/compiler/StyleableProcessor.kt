package nl.bryanderidder.cakecutter.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import nl.bryanderidder.cakecutter.annotations.BindStyleable
import nl.bryanderidder.cakecutter.annotations.Styleable
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeKind.*

@AutoService(Processor::class)
class StyleableProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String>
            = mutableSetOf(Styleable::class.java.name, BindStyleable::class.java.name)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        // All annotations enter here from all classes.
        // Create a map of <Class,Set<Props>>
        val injections: MutableMap<TypeElement, MutableSet<InjectionPoint>> = mutableMapOf()
        // Fill the map with classes and their annotations
        annotations?.flatMap { roundEnv.getElementsAnnotatedWith(it) }
            ?.forEach { processAnnotation(it, injections) }

        if (injections.isEmpty()) return false
        val packageName = injections.keys.first().toString().replaceAfterLast(".", "")
        // generate a single object
        val objectBuilder = TypeSpec.objectBuilder(OBJECT_NAME)
        // fill the object with bind functions for each view
        injections.keys.forEach {
            objectBuilder.addFunction(generateBindFunction(it, injections[it]))
        }
        // Generate a single file
        val file = FileSpec.builder(packageName,
                OBJECT_NAME
            )
            .addType(objectBuilder.build())
            .build()
        // Put files in generated directory
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))

        return false
    }

    // Generate a function
    // Get the styled attributes
    // Set all initial values from the styled attrs.
    // Recycle the styled attrs.
    private fun generateBindFunction(
        classElement: TypeElement,
        injections: MutableSet<InjectionPoint>?
    ): FunSpec {
        val className = classElement.simpleName.toString()
        return FunSpec.builder(BIND).apply {
            addParameter("view", classElement.asType().asTypeName())
            addStatement("view.context.obtainStyledAttributes(view.attrs, R.styleable.$className)")
            addStatement("  .apply {")
            addStatement("    try {")
            injections?.forEach { point: InjectionPoint ->
                if (point.styleId == null) addStatement("      ${fetchStyleableWithoutId(point, className)}")
                else addStatement("      ${fetchStyleableWithId(point)}")
            }
            addStatement("    } finally {")
            addStatement("      recycle()")
            addStatement("    }")
            addStatement("}")
            }.build()
    }

    // for each injection point (each annotation) fetch its attribute
    private fun fetchStyleableWithoutId(
        point: InjectionPoint,
        className: String
    ): String =
        when (point.type) {
            DECLARED -> "view.${point.variableName} = getString(R.styleable.${className}_${point.variableName}) ?: view.${point.variableName}"
            FLOAT -> "view.${point.variableName} = getDimension(R.styleable.${className}_${point.variableName}, view.${point.variableName})"
            INT -> "view.${point.variableName} = getInt(R.styleable.${className}_${point.variableName}, view.${point.variableName})"
            BOOLEAN -> "view.${point.variableName} = getBoolean(R.styleable.${className}_${point.variableName}, view.${point.variableName})"
            else -> ""
        }

    // for each injection point (each annotation) fetch its attribute
    private fun fetchStyleableWithId(point: InjectionPoint): String =
        when (point.type) {
            DECLARED -> "view.${point.variableName} = getString(${point.styleId}) ?: view.${point.variableName}"
            FLOAT -> "view.${point.variableName} = getDimension(${point.styleId}, view.${point.variableName})"
            INT -> "view.${point.variableName} = getInt(${point.styleId}, view.${point.variableName})"
            BOOLEAN -> "view.${point.variableName} = getBoolean(${point.styleId}, view.${point.variableName})"
            else -> ""
        }

    // for each annotated field, get the variable name, type and styleId
    private fun processAnnotation(
        element: Element,
        classWithInjections: MutableMap<TypeElement, MutableSet<InjectionPoint>>
    ) {
        val variableName = element.simpleName.toString()
        val type = element.asType().kind
        val classElement = element.enclosingElement as TypeElement
        var styleId: Int? = null
        try {
            styleId = element.getAnnotation(BindStyleable::class.java).styleableId
        } catch (e: Exception) { /* annotation not found. */}
        classWithInjections.putIfAbsent(classElement, mutableSetOf())
        classWithInjections[classElement]?.add(
            InjectionPoint(
                variableName,
                type,
                styleId
            )
        )
    }

    private class InjectionPoint internal constructor(
        val variableName: String,
        val type: TypeKind,
        val styleId: Int?
    )

    companion object {
        const val OBJECT_NAME = "CakeCutter"
        const val BIND = "bind"
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}