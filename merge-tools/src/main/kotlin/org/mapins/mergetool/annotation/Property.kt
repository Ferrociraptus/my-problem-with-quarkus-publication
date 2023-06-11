package org.mapins.mergetool.annotation


/**
 * This property for set which property or field name should
 * be affected while merge operation is applying.
 *
 * Don't recommend to use it in class with [Mergeable] annotation
 *
 * Usage example:
 * @Mergeable
 * class User (
 *  var name: String
 * )
 *
 * @Diff
 * data class NameDiff (
 *  @Property(applyOn = "name")
 *  var fullName: String?
 * )
 * */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Property(
    /**
     * Property name in other class which object will be updated by diff classes
     * */
    val applyOn: String = ""
)
