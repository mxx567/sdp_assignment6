package filesystem;

abstract class Node implements Element {
    private final String name;
    private DirectoryNode parent;

    Node(String name) {
        this.name = java.util.Objects.requireNonNull(name);
    }

    @Override
    public final String getName() {
        return name;
    }

    final DirectoryNode getParent() {
        return parent;
    }

    @Override
    public final void addTo(DirectoryNode directory) {
        if (parent != null) {
            throw new IllegalArgumentException("The node already has a parent");
        }
        for (DirectoryNode ancestor = directory; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == this) {
                throw new IllegalArgumentException("Adding this node would create a cycle");
            }
        }
        directory.insertChild(this);
        parent = directory;
    }
}
