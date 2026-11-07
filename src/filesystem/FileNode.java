package filesystem;

public class FileNode implements Element{
    private int size;
    private String name;

    public FileNode(String name, int size){
        this.name = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
