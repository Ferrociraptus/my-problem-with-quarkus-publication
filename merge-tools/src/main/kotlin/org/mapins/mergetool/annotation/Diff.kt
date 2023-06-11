package org.mapins.mergetool.annotation

import kotlin.reflect.KClass


/**
 * This annotation for mark the diff values opponent to [Mergeable] classes.
 * It should mark public DTOs. This annotation scraps only public fields
 * and Kotlin properties without protected and private access.
 *
 * We recommend using data classes (records) with nullable fields
 * for filtering by [org.mapins.mergetool.filter.NotNullValuesFilter]
 *
 * For example:
 * ```
 * data class UserDiff (
 *  var name: String?,
 *  val surname: String?,
 *  ...
 * )
 * ```
 * */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Diff(
    val target: KClass<*>
)
