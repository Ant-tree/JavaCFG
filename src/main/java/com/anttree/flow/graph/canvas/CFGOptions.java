package com.anttree.flow.graph.canvas;

public class CFGOptions {

    private boolean omitAnonymousClasses = true;

    public CFGOptions(
            boolean omitAnonymousClasses
    ) {
        this.omitAnonymousClasses = omitAnonymousClasses;
    }

    public boolean isOmitAnonymousClasses() {
        return omitAnonymousClasses;
    }

    public void setOmitAnonymousClasses(boolean omitAnonymousClasses) {
        this.omitAnonymousClasses = omitAnonymousClasses;
    }
}
