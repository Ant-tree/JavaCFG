package com.anttree.flow.graph.analysis;

import com.anttree.flow.graph.insepction.ReferenceInspector;
import com.anttree.flow.graph.model.Ext;
import com.anttree.flow.graph.model.Reference;
import com.anttree.flow.graph.utils.JarUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Set;

public class ClassAnalyzer extends AnalysisEngine {

    private final String inputFile;

    public ClassAnalyzer(String inputFile) {
        this.inputFile = inputFile;
    }

    @Override
    public Set<Reference> run(String target) {
        try {
            return executor(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Reference> executor(String target) {
        try {
            classes = JarUtils.getEntries(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (classes.isEmpty()) {
            throw new RuntimeException("Empty class");
        }

        ClassNode targetClassNode = classes.get(target + Ext.CLASS);

        if (targetClassNode == null) {
            throw new RuntimeException("Target class " + target + " not found");
        }

        return new ReferenceInspector().runThroughClassNode(targetClassNode);
    }
}
