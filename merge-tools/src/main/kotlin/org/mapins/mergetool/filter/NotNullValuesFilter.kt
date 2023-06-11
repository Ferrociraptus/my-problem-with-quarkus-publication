package org.mapins.mergetool.filter

import org.mapins.mergetool.MergeFilter


/**
 * This filter for use with [org.mapins.mergetool.annotation.Mergeable] annotation.
 *
 * Filter all not null values.
 * */
object NotNullValuesFilter : MergeFilter {
    override fun filter(fieldName: String, value: Any?): Boolean {
        return value != null
    }
}