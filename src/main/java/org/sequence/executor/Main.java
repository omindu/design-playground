package org.sequence.executor;

import java.util.List;

public class Main {

    private static final Node END_NODE = null;

    public static void main(String[] args) {

        // Sequence = node1 -> node2 -> decisionNode1 -> [node3 -> node6 | node4 | node5]

        Node node6 = new SimpleNode("node 6", END_NODE);
        Node node5 = new SimpleNode("node 5", END_NODE);
        Node node4 = new SimpleNode("node 4", END_NODE);

        Node node3 = new SimpleNode("node 3", node6);
        DecisionNode decisionNode1 = new DecisionNode("decision node 1", List.of(node3, node4, node5));

        Node node2 = new SimpleNode("node .12", decisionNode1);
        Node node1 = new SimpleNode("node 1", node2);

        Sequence sequence = new Sequence(node1);
        sequence.execute();
    }
}