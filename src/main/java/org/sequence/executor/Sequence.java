package org.sequence.executor;

public class Sequence {

    private final Node startNode;

    public Sequence(Node startNode) {

        this.startNode = startNode;
    }

    public void execute() {

        Node current = startNode;
        while (current != null) {
            NodeResponse response = current.execute();

            if (response.getStatus() == NodeResponse.Status.DECISION_REQUIRED) {

            } else if (response.getStatus() == NodeResponse.Status.INPUT_REQUIRED) {

            } else if (response.getStatus() == NodeResponse.Status.COMPLETE) {
                current = current.next();
            }
        }
    }
}
