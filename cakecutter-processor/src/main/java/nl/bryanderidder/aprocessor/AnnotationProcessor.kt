package nl.bryanderidder.aprocessor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import nl.bryanderidder.annotations.BindStyleable
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
class AnnotationProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String>
            = mutableSetOf(BindStyleable::class.java.name)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        // All annotations enter here from all classes.
        // Create a map of <Class,Set<Annotations>>
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
        val file = FileSpec.builder(packageName, OBJECT_NAME)
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
    private fun generateBindFunction(classElement: TypeElement, injections: MutableSet<InjectionPoint>?): FunSpec {
        val className = classElement.simpleName.toString()
        val bind = FunSpec.builder(BIND)
            .addParameter("view", classElement.asType().asTypeName())
            .addStatement("val styledAttrs = view.context.obtainStyledAttributes(view.attrs, R.styleable.$className)")
        injections?.forEach { point: InjectionPoint ->
            bind.addStatement(createStatementForAttr(point))
        }
        bind.addStatement("styledAttrs.recycle()")
        return bind.build()
    }

    // for each injection point (each annotation) fetch its attribute
    private fun createStatementForAttr(point: InjectionPoint): String =
        when (point.type) {
            DECLARED -> "view.${point.variableName} = styledAttrs.getString(${point.styleId}) ?: view.${point.variableName}"
            FLOAT -> "view.${point.variableName} = styledAttrs.getDimension(${point.styleId}, view.${point.variableName})"
            INT -> "view.${point.variableName} = styledAttrs.getInt(${point.styleId}, view.${point.variableName})"
            BOOLEAN -> "view.${point.variableName} = styledAttrs.getBoolean(${point.styleId}, view.${point.variableName})"
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
        val styleId = element.getAnnotation(BindStyleable::class.java).value
        classWithInjections.putIfAbsent(classElement, mutableSetOf())
        classWithInjections[classElement]?.add(InjectionPoint(variableName, type, styleId))
    }

    private class InjectionPoint internal constructor(
        val variableName: String,
        val type: TypeKind,
        val styleId: Int
    )

    companion object {
        const val OBJECT_NAME = "CakeCutter"
        const val BIND = "bind"
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}