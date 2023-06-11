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

    public MergeToolResourceProcessor(){}

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
    void findMergeUtilClass(CombinedIndexBuildItem index, BuildProducer<MergeUtilBuildItem> producer) {
        producer.produce(new MergeUtilBuildItem(index.getIndex().getClassByName(ClassNames.MERGE_UTIL)));
    }

//    @BuildStep
//    AnnotationsTransformerBuildItem annotateClassCache(MergeUtilBuildItem item) {
//        AnnotationsTransformer transformer = new AnnotationsTransformer() {
//            @Override
//            public boolean appliesTo(AnnotationTarget.Kind kind) {
//                // at some point, we might want to support METHOD_PARAMETER too, but for now getting annotations for them
//                // is cumbersome so let's wait for Jandex improvements
//                return kind == AnnotationTarget.Kind.CLASS;
//            }
//
//            @Override
//            public void transform(TransformationContext transformationContext) {
//                transformationContext
//                        .transform()
//                        .add(DotNames.SINGLETON)
//                        .done();
//            }
//        };
//        return new AnnotationsTransformerBuildItem(transformer);
//    }

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

        MergeUtil mergeUtilObj = containerItem.getValue().beanInstance(MergeUtil.class);

        recorder.cacheMergeableClasses(mergeUtilObj, listOfCachedClasses);
    }
}
