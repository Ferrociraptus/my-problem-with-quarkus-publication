package org.test.mapins;

import jakarta.enterprise.inject.Produces;
import org.mapins.mergetool.MergeUtil;


public class MergeUtilProducer {

    @Produces
    public MergeUtil produceMergeUtil() {
        return new MergeUtil();
    }
}
