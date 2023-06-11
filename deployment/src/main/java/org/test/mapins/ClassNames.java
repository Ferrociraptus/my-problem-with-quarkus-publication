package org.test.mapins;

import org.jboss.jandex.DotName;

import java.util.HashSet;
import java.util.Set;

public class ClassNames {
    static final Set<DotName> CREATED_CONSTANTS = new HashSet<>();

    private ClassNames() {
    }

    private static DotName createConstant(String fqcn) {
        DotName result = DotName.createSimple(fqcn);
        CREATED_CONSTANTS.add(result);
        return result;
    }

    public static final DotName MERGE_UTIL = createConstant("org.mapins.mergetool.MergeUtil");

}
