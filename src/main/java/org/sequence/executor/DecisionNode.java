package org.sequence.executor;

import java.util.List;
import java.util.Random;

public class DecisionNode implements Node {

    private final List<Node> nodes;
    private final String id;
    private Node next;

    public DecisionNode(String id, List<Node> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public Node next() {

        return next;
    }

    public NodeResponse execute() {

        System.out.println("Executing decision node: " + id);

        Random random = new Random();
        int next = random.nextInt(nodes.size());
        this.next = nodes.get(next);
        return null;
    }

}
