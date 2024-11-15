package org.sequence.executor;

public class SimpleNode implements Node {

    private final String id;
    private Node next;

    public SimpleNode(String id, Node next) {

        this.id = id;
        this.next = next;
    }

    public Node next() {

        return next;
    }

    public NodeResponse execute() {

        System.out.println("Executing node: " + id);
        return null;
    }
}
