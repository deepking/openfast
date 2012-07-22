package org.openfast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleNode implements Node {
    private List<Node> nodes = Collections.emptyList();
    protected Map<QName, String> attributes = Collections.emptyMap();
    protected final QName name;

    public SimpleNode(QName nodeName) {
        this.name = nodeName;
    }

    @Override
    public void addNode(Node node) {
        if (nodes.isEmpty()) {
            nodes = new ArrayList<Node>(3);
        }
        nodes.add(node);
    }

    @Override
    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String getAttribute(QName name) {
        return attributes.get(name);
    }

    @Override
    public Map<QName, String> getAttributes() {
        return attributes;
    }

    @Override
    public List<Node> getChildren(QName name) {
        List<Node> children = Collections.emptyList();
        for (int i = 0; i < nodes.size() && name != null; i++) {
            Node child = nodes.get(i);
            if (name.equals(child.getNodeName())) {
                if (children.isEmpty()) {
                    children = new ArrayList<Node>();
                }
                children.add(nodes.get(i));
            }
        }
        return children;
    }

    @Override
    public QName getNodeName() {
        return name;
    }

    @Override
    public void setAttribute(QName name, String value) {
        if (attributes.isEmpty()) {
            attributes = new HashMap<QName, String>();
        }
        attributes.put(name, value);
    }

    @Override
    public boolean hasAttribute(QName name) {
        return attributes.containsKey(name);
    }

    @Override
    public boolean hasChild(QName name) {
        for (int i = 0; i < nodes.size() && name != null; i++) {
            Node child = nodes.get(i);
            if (name.equals(child.getNodeName())) {
                return true;
            }
        }
        return false;
    }
}
