package com.anttree.flow.graph.model;

public class Component {
    private String owner;
    private String name;
    private int type;

    public Component(
            String owner,
            String name,
            int type
    ) {
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return getOwner() + "." + getName() + ":" + getType();
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
