package org.test.mapins;


import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.Feature;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.mapins.mergetool.MergeUtil;
import org.mapins.mergetool.annotation.Diff;
import org.mapins.mergetool.annotation.Mergeable;
import io.quarkus.deployment.annotations.Record;

import java.util.List;

public final class MergeToolResourceProcessor {


    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem(Feature.CACHE);
    }


    @BuildStep
    void collectMergeableClasses(CombinedIndexBuildItem index, BuildProducer<MergeableClassCacheItem> producer) {
        index.getIndex()
                .getKnownClasses()
                .stream()
                .filter(c -> (c.hasAnnotation(Mergeable.class) || c.hasAnnotation(Diff.class)))
                .map(MergeableClassCacheItem::new)
                .forEach(producer::produce);
    }

    @BuildStep
    void createMergeUtilClass(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        additionalBeanProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .addBeanClass(MergeUtil.class)
                        .setUnremovable()
                        .setDefaultScope(DotNames.SINGLETON)
                        .build()
        );
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void CacheAllMergeableClasses(List<MergeableClassCacheItem> classes,
                                  MergeableClassRecorder recorder,
                                  BeanContainerBuildItem containerItem){

        final List<String> listOfCachedClasses = classes.stream().map(e -> e.get().name().toString()).toList();

        recorder.cacheMergeableClasses(containerItem.getValue(), listOfCachedClasses);
    }
}
