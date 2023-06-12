package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.mapins.mergetool.MergeFilter;
import org.mapins.mergetool.MergeUtil;
import org.mapins.mergetool.annotation.MergeBlocked;
import org.mapins.mergetool.annotation.Mergeable;

@ApplicationScoped
public class Startup {

    @Inject
    MergeUtil mergeUtil;

    void startup(@Observes StartupEvent startupEvent){
        @Mergeable(filter = MergeFilter.class)
        record Test(Integer a, @MergeBlocked Integer b, String c){};

        Test obj = new Test(1,2,"Hello");
        Test diff = new Test(5, 10, null);

        mergeUtil.applyDiff(obj, diff);

        // Expected 5, 2, "Hello"
        System.out.printf("Result obj is: %s\n", obj);
    }
}
