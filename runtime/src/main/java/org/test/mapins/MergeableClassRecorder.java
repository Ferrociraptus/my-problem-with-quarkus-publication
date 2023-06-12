package org.test.mapins;


import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;
import org.jboss.logging.Logger;
import org.mapins.mergetool.MergeUtil;


import java.util.List;
import java.util.Objects;

@Recorder
public class MergeableClassRecorder {

    Logger log = Logger.getLogger("org.mapins.cloud");


    void cacheMergeableClasses(BeanContainer mergeUtilContainer, List<String> classPaths){
        log.debugf("List of mergeable items found by Quarkus deployment: %n%s", classPaths);

//        MergeUtilProd mergeUtilProducer = mergeUtilContainer.beanInstance(MergeUtilProducer.class);
        MergeUtil mergeUtil = mergeUtilContainer.beanInstance(MergeUtil.class);
        classPaths.stream()
                .filter(Objects::nonNull)
                .map(
                    n -> {
                        try {return Class.forName(n);}
                        catch (Throwable ignore) { return null;}
                    })
                .filter(Objects::nonNull)
                .forEach(mergeUtil::cacheClass);

        log.infof("%n%s classes cached", classPaths);
    }
}
