package com.anttree.flow.graph.canvas;

import com.anttree.flow.graph.model.Component;
import com.anttree.flow.graph.model.ComponentType;
import com.anttree.flow.graph.model.Pair;
import com.anttree.flow.graph.model.Reference;
import com.anttree.flow.graph.utils.JarUtils;
import com.anttree.flow.graph.utils.NodeUtils;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.*;

@SuppressWarnings("CallToPrintStackTrace")
public class Graph {

    private static Color getColor(int type) {
        if (type == ComponentType.FIELD) {
            return Color.ROYALBLUE;
        }
        if (type == ComponentType.CLASS) {
            return Color.SPRINGGREEN;
        }
        if (type == ComponentType.UNDEFINED) {
            return Color.RED;
        }
        return Color.BLACK;
    }

    private static String getName(Component component) {
        return component.getOwner() + "." + component.getName();
    }

    public static void draw(
            String name,
            Set<Reference> references,
            String outputPath
    ) {
        //Graphviz.useDefaultEngines();

        try (GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine()) {
            List<GraphvizEngine> engines = new ArrayList<>();
            engine.timeout(30, TimeUnit.SECONDS);
            engines.add(engine);
            engines.add(new GraphvizV8Engine());

            Graphviz.useEngine(engines);

            MutableGraph graph = mutGraph(name)
                    .setDirected(true)
                    .graphAttrs()
                    .add(Rank.dir(TOP_TO_BOTTOM))
                    .graphAttrs().add(Rank.sep(2.0));

            references.stream().filter(ref ->
                    // flow from class to class are not included
                    ref.getSrc().getType() != ComponentType.CLASS &&
                    ref.getDest().getType() != ComponentType.CLASS
            ).map(ref -> mutNode(getName(ref.getSrc()))
                    .add(getColor(ref.getSrc().getType()))
                    .addLink(mutNode(
                            getName(ref.getDest())
                    ).add(getColor(
                            ref.getDest().getType())
                    ))
            ).forEach(node -> {
                //node.add(getColor(ComponentType.CLASS));
                graph.add(node);
            });

            graph.nodes().size();
            graph.edges().size();

            Graphviz.fromGraph(graph)
                    .render(Format.DOT)
                    .toFile(new File(outputPath + ".dot"));

            Graphviz.fromGraph(graph)
                    .render(Format.PNG)
                    .toFile(new File(outputPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawClassOnly(
            String name,
            Set<Reference> references,
            CFGOptions CFGOptions,
            String outputPath
    ) {
        //Graphviz.useDefaultEngines();
        try (GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine()) {
            List<GraphvizEngine> engines = new ArrayList<>();
            engine.timeout(5, TimeUnit.MINUTES);
            engines.add(engine);
            engines.add(new GraphvizV8Engine());

            Graphviz.useEngine(engines);

            MutableGraph graph = mutGraph(name)
                    .setDirected(true)
                    .graphAttrs()
                    .add(Rank.dir(TOP_TO_BOTTOM))
                    .graphAttrs().add(Rank.sep(2.0));

            references.stream().map(ref -> new Pair<>(
                    ref.getSrc().getOwner(),
                    ref.getDest().getOwner()
            )).filter(p ->
                    !p.first.equals(p.second)
            ).filter(p ->
                    !JarUtils.isDefaultClassName(p.first) &&
                    !JarUtils.isDefaultClassName(p.second)
            ).map(p -> {
                if (!CFGOptions.isOmitAnonymousClasses() ||
                    !(p.first.contains("$") || p.second.contains("$"))
                ) {
                    return p;
                }
                return new Pair<>(
                        NodeUtils.omitAnonymousClass(p.first),
                        NodeUtils.omitAnonymousClass(p.second)
                );
            }).map(ref -> mutNode(ref.first)
                    .addLink(mutNode(ref.second))
            ).forEach(node -> {
                node.add(getColor(ComponentType.CLASS));
                graph.add(node);
            });

            Graphviz.fromGraph(graph)
                    .render(Format.DOT)
                    .toFile(new File(outputPath + ".dot"));

            Graphviz.fromGraph(graph)
                    .render(Format.PNG)
                    .toFile(new File(outputPath));

            Graphviz.releaseEngine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
