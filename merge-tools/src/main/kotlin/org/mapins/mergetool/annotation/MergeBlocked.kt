package org.mapins.mergetool.annotation


/**
 * Annotation for ignore field and property (and property with field)
 * while applying merge operation from [org.mapins.mergetool.MergeUtil]
 *
 * Usage example:
 * @Mergeable
 * class User (
 *  @MergeBlocked
 *  val login: String,  // this can't be changed
 *  var name: String    // this will be updated
 * )
 * */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class MergeBlocked()
