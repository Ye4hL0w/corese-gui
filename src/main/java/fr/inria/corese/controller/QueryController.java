package fr.inria.corese.controller;

import fr.inria.corese.core.Graph;
import fr.inria.corese.core.query.QueryProcess;
import fr.inria.corese.kgram.api.core.Node;
import fr.inria.corese.kgram.core.Mapping;
import fr.inria.corese.kgram.core.Mappings;
import fr.inria.corese.sparql.compiler.java.Datatype;

public class QueryController {

    private Graph graph;

    public QueryController() {
        this.graph = new Graph();
        initialize();
    }

    private void initialize() {

    }

    public void handleQueryExecution(String query) {
        QueryProcess exec = QueryProcess.create(graph);
        try {
            Mappings map = exec.query(query);

            if (query.toLowerCase().contains("select")) {
                handleSelectQuery(map);
            } else if (query.toLowerCase().contains("ask")) {
                handleAskQuery(map);
            } else if (query.toLowerCase().contains("construct")) {
                handleConstructQuery(map);
            } else if (query.toLowerCase().contains("insert") || query.toLowerCase().contains("delete")) {
                handleUpdateQuery(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing query: " + e.getMessage());
        }
    }

    private void handleSelectQuery(Mappings map) {
        for (Mapping m : map) {
            StringBuilder row = new StringBuilder();
            for (Node var : map.getSelect()) {
                Node node = m.getValue(var);
                if (node != null) {
                    row.append(var).append(": ").append(node.toString()).append("\t");
                } else {
                    row.append(var).append(": ").append("null").append("\t");
                }
            }
            System.out.println(row.toString());
        }
    }

    private void handleAskQuery(Mappings map) {
        boolean result = !map.isEmpty();
        System.out.println("ASK query result: " + result);
    }

    private void handleConstructQuery(Mappings map) {
        Graph resultGraph = (Graph) map.getGraph();
        String graphString = resultGraph.display();
        System.out.println("CONSTRUCT query result:\n" + graphString);
    }

    private void handleUpdateQuery(Mappings map) {
        System.out.println("Update query executed successfully.");
    }
}
