package filesystem;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DirectoryNode directoryNode = new DirectoryNode("src");
        directoryNode.addElement(new FileNode("main.java", 600));
        directoryNode.addElement(new FileNode("readme.md", 200));
        DirectoryNode dir = new DirectoryNode("dir");
        directoryNode.addElement(dir);
        dir.addElement(new FileNode("out.log", 512));
        SearchVisitor sv = new SearchVisitor(".log", 10);
        TotalSizeVisitor ts = new TotalSizeVisitor();
        directoryNode.accept(sv);
        ArrayList<String> matches = sv.getMatches();
        for( String m : matches ){
            System.out.println(m);
        }
        directoryNode.accept(ts);
        System.out.println(ts.getTotalSize());
        StatisticsVisitor stv = new StatisticsVisitor();
        directoryNode.accept(stv);
        System.out.println(stv.getMaxDepth());
    }
}
