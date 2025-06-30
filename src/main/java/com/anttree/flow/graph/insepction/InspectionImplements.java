package com.anttree.flow.graph.insepction;

import com.anttree.flow.graph.model.Component;
import com.anttree.flow.graph.model.ComponentType;
import com.anttree.flow.graph.model.Reference;
import com.anttree.flow.graph.utils.NodeUtils;
import com.anttree.flow.graph.utils.SafeIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InspectionImplements {

    public InspectionImplements() {
        // Default constructor
    }

    public Component getComponentIfAvailable(ClassNode classNode, AbstractInsnNode node) {
        if (node.getOpcode() == Opcodes.CHECKCAST ||
            node.getOpcode() == Opcodes.INSTANCEOF ||
            node.getOpcode() == Opcodes.NEW ||
            node.getOpcode() == Opcodes.ANEWARRAY
        ) {
            TypeInsnNode castedNode = (TypeInsnNode) node;
            return new Component(
                    NodeUtils.normalizeClassName(classNode.name),
                    NodeUtils.normalizeClassName(castedNode.desc),
                    ComponentType.CLASS
            );
        }
        if (node.getOpcode() == Opcodes.MULTIANEWARRAY) {
            MultiANewArrayInsnNode castedNode = (MultiANewArrayInsnNode) node;
            return new Component(
                    NodeUtils.normalizeClassName(classNode.name),
                    NodeUtils.normalizeClassName(castedNode.desc),
                    ComponentType.CLASS
            );
        }
        if (node instanceof LdcInsnNode) {
            LdcInsnNode castedNode = (LdcInsnNode) node;
            if (!(castedNode.cst instanceof Type)) {
                return null;
            }
            Type type = (Type) castedNode.cst;
            if (type.getSort() != Type.OBJECT && type.getSort() != Type.ARRAY) {
                return null;
            }
            return new Component(
                    NodeUtils.normalizeClassName(classNode.name),
                    NodeUtils.normalizeClassName(type.getClassName()),
                    ComponentType.CLASS
            );
        }
        if (node instanceof MethodInsnNode) {
            MethodInsnNode castedNode = (MethodInsnNode) node;
            return new Component(
                    NodeUtils.normalizeClassName(castedNode.owner),
                    castedNode.name + castedNode.desc,
                    ComponentType.METHOD
            );
        }
        if (node instanceof FieldInsnNode) {
            FieldInsnNode castedNode = (FieldInsnNode) node;
            return new Component(
                    NodeUtils.normalizeClassName(castedNode.owner),
                    NodeUtils.normalizeClassName(castedNode.desc) + ":" + castedNode.name,
                    ComponentType.FIELD
            );
        }
        return null;
    }

    protected void methodInspection(
            ClassNode classNode,
            MethodNode methodNode,
            Set<Reference> references
    ) {
        if (methodNode.instructions != null) {
            instructionInspection(classNode, methodNode, references);
        }
        new HashSet<String>() {{
            addAll(NodeUtils.getClassNamesFromMethodDescriptor(methodNode.desc));
            addAll(NodeUtils.getGenericTypesInSignature(methodNode.signature));
        }}.forEach(name -> references.add(new Reference(
                new Component( // Source component
                        NodeUtils.normalizeClassName(classNode.name),
                        methodNode.name + methodNode.desc,
                        ComponentType.METHOD
                ),
                new Component( // Destination component
                        NodeUtils.normalizeClassName(classNode.name),
                        NodeUtils.normalizeClassName(name),
                        ComponentType.CLASS
                )
        )));
    }

    protected void instructionInspection(
            ClassNode classNode,
            MethodNode methodNode,
            Set<Reference> references
    ) {
        Component source = new Component(
                NodeUtils.normalizeClassName(classNode.name),
                methodNode.name + methodNode.desc,
                ComponentType.METHOD
        );

        new SafeIterator<AbstractInsnNode>(n -> {
            Component destination = getComponentIfAvailable(classNode, n);
            if (destination == null) {
                return;
            }
            references.add(new Reference(source, destination));
        }).over(List.of(methodNode.instructions.toArray()));
    }

    protected void fieldInspection(
            ClassNode classNode,
            FieldNode fieldNode,
            Set<Reference> references
    ) {
        Component source = new Component(
                NodeUtils.normalizeClassName(classNode.name),
                NodeUtils.normalizeClassName(fieldNode.desc) + ":" + fieldNode.name,
                ComponentType.FIELD
        );

        Component destination = new Component(
                NodeUtils.normalizeClassName(fieldNode.desc),
                NodeUtils.normalizeClassName(fieldNode.desc),
                ComponentType.FIELD
        );

        references.add(new Reference(source, destination));
    }

    protected void innerClassInspection(
            ClassNode classNode,
            InnerClassNode innerClassNode,
            Set<Reference> references
    ) {
        Component source = new Component(
                NodeUtils.normalizeClassName(classNode.name),
                NodeUtils.normalizeClassName(classNode.name),
                ComponentType.CLASS
        );

        Component destination = new Component(
                NodeUtils.normalizeClassName(innerClassNode.name),
                NodeUtils.normalizeClassName(innerClassNode.name),
                ComponentType.CLASS
        );

        references.add(new Reference(source, destination));
    }

    protected void interfaceInspection(
            ClassNode classNode,
            String interfaceName,
            Set<Reference> references
    ) {
        Component source = new Component(
                NodeUtils.normalizeClassName(classNode.name),
                NodeUtils.normalizeClassName(classNode.name),
                ComponentType.CLASS
        );

        Component destination = new Component(
                NodeUtils.normalizeClassName(interfaceName),
                NodeUtils.normalizeClassName(interfaceName),
                ComponentType.CLASS
        );

        references.add(new Reference(source, destination));
    }

    protected void superClassInspection(
            ClassNode classNode,
            String superClassName,
            Set<Reference> references
    ) {
        Component source = new Component(
                NodeUtils.normalizeClassName(classNode.name),
                NodeUtils.normalizeClassName(classNode.name),
                ComponentType.CLASS
        );

        Component destination = new Component(
                NodeUtils.normalizeClassName(superClassName),
                NodeUtils.normalizeClassName(superClassName),
                ComponentType.CLASS
        );

        references.add(new Reference(source, destination));
    }
}
