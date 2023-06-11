package org.mapins.mergetool.annotation

import org.mapins.mergetool.MergeFilter
import kotlin.reflect.KClass


/**
 * Annotate the class where should apply changes from other [Diff]
 * or [Mergeable] (not recommend except using same class)
 * objects to current object.
 *
 * It uses any accessible fields (in primary Kotlin properties usage).
 * You can configure the scrapping fields strategy by implementing your own [MergeFilter],
 * set the parameters of this annotation or mark fields by [MergeBlocked] and [Property] annotations.
 *
 * Nesting classes unsupported!
 * */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Mergeable(

    /**
     * Filter for fields with custom criteria on this class while merging
     * */
    val filter: KClass<out MergeFilter> = MergeFilter::class,

    /**
     * Is apply changes from [Diff] objects to private members (protected and private) with the same name
     * */
    val mergePrivate: Boolean = false,

    /**
     * Is apply changes from [Diff] objects to unmutable Kotlin fields (with val)
     * */
    val mergeUnmutable: Boolean = false
)
