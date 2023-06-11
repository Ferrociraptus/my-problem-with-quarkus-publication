package org.test.mapins;

import io.quarkus.builder.item.SimpleBuildItem;
import org.jboss.jandex.ClassInfo;

public final class MergeUtilBuildItem extends SimpleBuildItem {
    private final ClassInfo mergeUtil;

    public MergeUtilBuildItem(ClassInfo mergeUtil) {
        this.mergeUtil = mergeUtil;
    }

    public ClassInfo get(){
        return mergeUtil;
    }
}
