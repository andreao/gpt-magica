package no.volve;

import java.util.Map;

public abstract class NodeTransformer {
    public abstract Node transform(Node node, Map<String, Node> bindings);
}