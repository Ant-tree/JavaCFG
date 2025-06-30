package com.anttree.flow.graph.insepction;

import com.anttree.flow.graph.model.Reference;
import com.anttree.flow.graph.utils.SafeIterator;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceInspector extends InspectionImplements {

    Set<Reference> references = ConcurrentHashMap.newKeySet();

    public ReferenceInspector() {
        super();
    }

    public Set<Reference> runThroughClassNode(ClassNode classNode) {
        references.clear();

        new SafeIterator<MethodNode>(m ->
            instructionInspection(classNode, m, references)
        ).over(classNode.methods);

        return references;
    }

    public Set<Reference> runThroughAllClassNodes(
            HashMap<String, ClassNode> classNodes
    ) {
        references.clear();

        new SafeIterator<ClassNode>(c -> {
            new SafeIterator<MethodNode>(m ->
                methodInspection(c, m, references)
            ).over(c.methods);

            new SafeIterator<FieldNode>(f ->
                fieldInspection(c, f, references)
            ).over(c.fields);

            new SafeIterator<InnerClassNode>(i ->
                innerClassInspection(c, i, references)
            ).over(c.innerClasses);

            new SafeIterator<String>(s ->
                interfaceInspection(c, s, references)
            ).over(c.interfaces);

            if (c.superName != null) {
                superClassInspection(c, c.superName, references);
            }

        }).over(classNodes.values());

        return references;
    }
}
