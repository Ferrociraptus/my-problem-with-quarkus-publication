package org.test.mapins;

import io.quarkus.builder.item.MultiBuildItem;
import org.jboss.jandex.ClassInfo;



public final class MergeableClassCacheItem extends MultiBuildItem {

    private final ClassInfo mergeableClasses;

    public MergeableClassCacheItem(ClassInfo mergeableClasses) {
        this.mergeableClasses = mergeableClasses;
    }

    public ClassInfo get() {
        return mergeableClasses;
    }

}
