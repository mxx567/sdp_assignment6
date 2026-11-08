package filesystem;

public interface Element {
    String getName();
    void addTo(DirectoryNode directory);
    void accept(Visitor visitor);
}
