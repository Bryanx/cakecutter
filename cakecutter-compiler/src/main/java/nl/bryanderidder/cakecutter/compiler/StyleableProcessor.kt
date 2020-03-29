package nl.bryanderidder.cakecutter.compiler

import com.google.auto.service.AutoService
import nl.bryanderidder.cakecutter.annotations.BindStyleable
import nl.bryanderidder.cakecutter.annotations.Styleable
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind.*
import javax.lang.model.type.TypeMirror


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
        val packageName = injections.keys.first().toString().replaceAfterLast(".", "").dropLastWhile { it == '.' }
        val content = StringBuilder()
        // fill the object with a bind function
        injections.keys.forEach {
            content.appendln(generateBindFunction(it, injections[it]))
        }
        // Put the files in generated directory
        generateFile(packageName, content.toString())
        return false
    }

    private fun generateFile(pack: String, content: String) {
        val fileContent = String.format(OBJECT_FILE, pack, content).trimIndent()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val file = File(kaptKotlinGeneratedDir, "$OBJECT_NAME.kt")
        file.writeText(fileContent)
    }

    // Generate a function
    // Get the styled attributes
    // Set all initial values from the styled attrs.
    // Recycle the styled attrs.
    private fun generateBindFunction(
        classElement: TypeElement,
        injections: MutableSet<InjectionPoint>?
    ): String {
        val className = classElement.simpleName.toString()
        val func = StringBuilder()
        func.appendln("  fun $BIND(view: $classElement) {")
        func.appendln("    view.context.obtainStyledAttributes(view.attrs, R.styleable.$className)")
        func.appendln("    .apply {")
        func.appendln("      try {")
        injections?.forEach { point: InjectionPoint ->
            val id = if (point.styleId != null) "R.styleable.${className}_${point.styleId}"
            else "R.styleable.${className}_${point.variableName}"
            func.appendln("        ${fetchStyleable(point, id, point.type.toString())}")
        }
        func.appendln("      } finally {")
        func.appendln("        recycle()")
        func.appendln("      }")
        func.appendln("    }")
        func.append("  }")
        return func.toString()
    }

    // for each injection point (each annotation) fetch its attribute without style id.
    private fun fetchStyleable(
        point: InjectionPoint,
        id: String,
        type: String
    ): String {
        return when (point.type.kind) {
            DECLARED -> {
                if (type.contains("String")) "view.${point.variableName} = getString($id) ?: view.${point.variableName}"
                else "view.${point.variableName} = $type.values()[getInteger($id, view.${point.variableName}.ordinal)]"
            }
            FLOAT -> "view.${point.variableName} = getDimension($id, view.${point.variableName})"
            INT -> "view.${point.variableName} = getInt($id, view.${point.variableName})"
            BOOLEAN -> "view.${point.variableName} = getBoolean($id, view.${point.variableName})"
            ARRAY -> "view.${point.variableName} = getTextArray($id) ?: view.${point.variableName}"
            else -> ""
        }
    }

    // for each annotated field, get the variable name, type and styleId
    private fun processAnnotation(
        element: Element,
        classWithInjections: MutableMap<TypeElement, MutableSet<InjectionPoint>>
    ) {
        val variableName = element.simpleName.toString()
        val type = element.asType()
        val classElement = element.enclosingElement as TypeElement
        var styleId: String? = null
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
        val type: TypeMirror,
        val styleId: String?
    )

    companion object {
        const val OBJECT_NAME = "CakeCutter"
        const val BIND = "bind"
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val OBJECT_FILE =
"""
package %s

object $OBJECT_NAME {
%s}
"""
    }
}