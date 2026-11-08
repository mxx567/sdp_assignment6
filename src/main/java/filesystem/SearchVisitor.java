package filesystem;

import java.util.ArrayList;

public class SearchVisitor implements Visitor {
    private final String nameMatches;
    private final long minSize;
    private String prefix;
    private final ArrayList<String> matches = new ArrayList<>();


    public SearchVisitor(String nameMatches, long minSize) {
        this.nameMatches = nameMatches;
        this.minSize = minSize;
    }

    @Override
    public void visit(FileNode file) {
        String name = file.getName();
        if (name.endsWith(nameMatches) && file.getSize() >= minSize) {
            matches.add(prefix == null ? name : prefix + name);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        String previous = prefix;
        prefix = (previous == null) ? "" : previous + directory.getName() + "/";

        for (Element child : directory.getChildren()) {
            child.accept(this);
        }

        prefix = previous;
    }

    public ArrayList<String> getMatches() {
        ArrayList<String> sorted = new ArrayList<>(matches);
        sorted.sort(String::compareTo);
        return sorted;
    }
}