package filesystem;

import java.util.ArrayList;

public class DirectoryNode extends Node {
    private final ArrayList<Element> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public DirectoryNode addElement(Element child) {
        child.addTo(this);
        return this;
    }

    public void insertChild(Node child) {
        for (Element existing : children) {
            if (existing.getName().equals(child.getName())) {
                throw new IllegalArgumentException("Duplicate child name: " + child.getName());
            }
        }
        children.add(child);
    }

    public ArrayList<Element> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
