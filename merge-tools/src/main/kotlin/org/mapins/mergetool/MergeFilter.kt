package org.mapins.mergetool


/**
 * Provides the interface for [org.mapins.mergetool.annotation.Mergeable] annotation
 * for setting custom filter strategy.
 *
 * For example one common filter is [org.mapins.mergetool.filter.NotNullValuesFilter]
 * */
interface MergeFilter {
    fun filter(fieldName: String, value: Any?): Boolean
}