package com.anttree.flow.graph.utils;

import java.util.Collection;

public class SafeIterator<T> {

    public interface Delegation<T> {
        void exec(T item);
    }

    Delegation<T> delegation;
    private Collection<T> items;
    private final boolean definedGeneric;

    public SafeIterator(
            Collection<T> items,
            Delegation<T> delegation
    ) {
        this.definedGeneric = true;
        this.items = items;
        this.delegation = delegation;
    }

    public SafeIterator(Delegation<T> delegation) {
        this.definedGeneric = false;
        this.delegation = delegation;
    }

    public void run() {
        if (!definedGeneric) {
            throw new RuntimeException("Generic type is not defined. \n" +
                    "Please use the constructor with Collection<T> items parameter, " +
                    "to use current run() method. \n" +
                    "Otherwise, consider using over(Collection<T> items) method."
            );
        }
        if (items == null || items.isEmpty()) {
            return;
        }

        for (T item : items) {
            delegation.exec(item);
        }
    }

    public void over(Collection<T> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        for (T item : items) {
            delegation.exec(item);
        }
    }
}
