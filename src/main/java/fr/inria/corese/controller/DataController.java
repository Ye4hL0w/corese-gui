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
        return 41226;
    }

    public int getNumberOfGraphs() {
        return 1;
    }

    public int getNumberOfSemanticElements() {
        return 12;
    }

    public int getNumberOfRules() {
        return 6;
    }

}
