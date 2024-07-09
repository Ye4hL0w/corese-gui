package fr.inria.corese.controller;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.load.Load;
import java.io.File;

public class DataController {

    private Graph graph;

    public DataController(){
        this.graph = Graph.create();
    }

    public void loadGraphFromFile(File file){
        try {
            Load ld = Load.create(graph);
            ld.parse(file.getAbsolutePath());
            System.out.println("Graph loaded from file: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearGraph() {
        this.graph = Graph.create();
    }

    public Graph getGraph() {
        return graph;
    }

    /* Stats */

    public int getNumberOfTriplets() {
        // TODO : Implements the logic to recover the number of triplets.
        return 0;
    }

    public int getNumberOfGraphs() {
        // TODO : Implements the logic to recover the number of graph.
        return 0;
    }

    public int getNumberOfSemanticElements() {
        // TODO : Implements the logic to recover the number of the semantic elements.
        return 0;
    }

    public int getNumberOfRules() {
        // TODO : Implements the logic to recover the number of rules.
        return 0;
    }

}
