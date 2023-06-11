package org.mapins.mergetool

import org.mapins.mergetool.annotation.Diff
import org.mapins.mergetool.annotation.MergeBlocked
import org.mapins.mergetool.annotation.Mergeable
import org.mapins.mergetool.annotation.Property
import java.util.logging.Logger
import java.util.stream.Collectors
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmName


/**
 * This utility class for provide handling the [org.mapins.mergetool.annotation] annotations
 * and provide merging class values operation
 * */
class MergeUtil() {
    // TODO: make available for java too. Now only Kotlin properties (getters)

    private class ClassCache (
        val fields: Map<String, KProperty1<*, *>> = HashMap(),
        val filter: MergeFilter? = null,
        val annotation: Annotation? = null
    )

    private val _classCache: MutableMap<KClass<out Any>, ClassCache> = HashMap()

    fun applyDiff(to: Any, from: Any){

        val applyClass = to::class
        val diffClass = from::class

        if (!_classCache.containsKey(applyClass)){
            cacheClass(applyClass)
        }
        if (!_classCache.containsKey(diffClass)){
            cacheClass(diffClass)
        }

        val classCache: ClassCache = _classCache[applyClass]!!
        val diffClassCache: ClassCache = _classCache[diffClass]!!

        if (classCache.annotation !is Mergeable)
            throw MergeDiffException("You try to merge data into unmergeable class." +
                    " Add @Mergeable annotation if you sure to update")

        if (applyClass != diffClass && diffClassCache.annotation !is Diff
            || (diffClassCache.annotation is Diff && diffClassCache.annotation.target != applyClass))
            throw MergeDiffException("You try to merge uncheked data into Mergeable class." +
                    " Add @Diff or @Mergeable annotation to your diff class if you sure to merge diff")

        for (property in classCache.fields.values){
            if (!diffClassCache.fields.containsKey(property.name))
                continue

            val diffProperty = diffClassCache.fields[property.name]!!

            property.isAccessible = true

            if (classCache.filter != null && !classCache.filter.filter(property.toString(),
                    diffProperty.getter.call(from)))
                continue

            if (property is KMutableProperty1<*, *>)
                property.setter.call(to, diffProperty.getter.call(from))
            else if (classCache.annotation.mergeUnmutable && property.javaField != null){
                val field = property.javaField
                if (field!!.trySetAccessible()){
                    field.set(to, diffProperty.getter.call(from))
                }
            }
        }
    }

    /**
     * This is a caching operation if you want to save time at runtime by caching and startup.
     * It is not a necessary operation, and it will be run only once if you use only applyDiff operation.
     *
     * Do not recommend manual usage.
     * */
    fun cacheClass(kcls: KClass<out Any>){
        val annotation = kcls.findAnnotation<Mergeable>() ?: kcls.findAnnotation<Diff>()
        ?: throw MergeDiffException("You try to cache unchecked classes. Please add annotation @Mergeable or @Diff")


        var filter: MergeFilter? = null

        if (annotation is Mergeable) {
            filter = if (annotation.filter == MergeFilter::class)
                null
            // if filter exists as kotlin object
            else if (annotation.filter.objectInstance != null)
                annotation.filter.objectInstance
            else
                annotation.filter.createInstance()
        }

        // get all Java fields in Kotlin properties
        // (public Kotlin property = private java field + public get and set method, look like usual Java property)
        val fields = kcls.declaredMemberProperties
            .stream()
            // additional clear properties because it is not a field but look the same
            .filter{if(annotation is Mergeable && annotation.mergePrivate) true
            else it.visibility == KVisibility.PUBLIC}
            .filter { !it.hasAnnotation<MergeBlocked>() }
            .filter { it != null }
            .collect(Collectors.toList())

        _classCache[kcls] = ClassCache(
            fields.associateBy {
            if (it.hasAnnotation<Property>() && it.findAnnotation<Property>()!!.applyOn.isNotEmpty())
                it.findAnnotation<Property>()!!.applyOn
            else it.name },
            filter,
            annotation)
    }

    fun cacheClass(cls: Class<out Any>){
        cacheClass(cls.kotlin)
    }
}

