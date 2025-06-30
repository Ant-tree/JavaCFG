package com.anttree.flow.graph.model;

public class Reference {

    private Component source;
    private Component destination;

    public Reference(
            Component source,
            Component destination
    ) {
        this.source = source;
        this.destination = destination;
    }

    public Component getSrc() {
        return source;
    }

    public Component getDest() {
        return destination;
    }

    @Override
    public String toString() {
        return getSrc().toString() + " -> " + getDest().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Component)) {
            return super.equals(obj);
        } else {
            return this.toString().equals(obj.toString());
        }
    }
}
