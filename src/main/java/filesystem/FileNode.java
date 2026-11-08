package filesystem;

public class FileNode extends Node {
    private final int size;

    public FileNode(String name, int size) {
        super(name);
        if (size < 0) {
            throw new IllegalArgumentException("File size must be non-negative");
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
