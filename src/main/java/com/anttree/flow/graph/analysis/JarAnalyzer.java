package com.anttree.flow.graph.analysis;

import com.anttree.flow.graph.insepction.ReferenceInspector;
import com.anttree.flow.graph.model.Reference;
import com.anttree.flow.graph.utils.JarUtils;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JarAnalyzer extends AnalysisEngine {

    private final String inputFile;

    public JarAnalyzer(String inputFile) {
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
        HashMap<String, ClassNode> wholeClasses;

        try {
            wholeClasses = JarUtils.getEntries(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (target == null || target.isEmpty()) {
            classes.putAll(wholeClasses);
        } else {
            List<String> targets = Arrays.stream(target.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            wholeClasses.entrySet().stream().filter(entry ->
                targets.stream().anyMatch(t -> entry.getKey().startsWith(t))
            ).forEach(entry ->
                classes.put(entry.getKey(), entry.getValue())
            );
        }

        if (classes.isEmpty()) {
            throw new RuntimeException("Empty class");
        }

        return new ReferenceInspector().runThroughAllClassNodes(classes);
    }
}
