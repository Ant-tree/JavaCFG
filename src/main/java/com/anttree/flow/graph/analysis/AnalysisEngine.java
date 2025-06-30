package com.anttree.flow.graph.analysis;

import com.anttree.flow.graph.model.Reference;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Set;

public abstract class AnalysisEngine {

    public HashMap<String, ClassNode> classes = new HashMap<>();     // File name(b5/e.class) : ClassNode

    public abstract Set<Reference> run(String target);
}
