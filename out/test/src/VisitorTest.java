package filsystem;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {
    @Test
    void acceptSelectsTheCorrectVisitOverload() {
        List<String> calls = new ArrayList<>();
        Visitor recorder = new Visitor() {
            @Override
            public void visit(FileNode file) {
                calls.add("file " + file.getName());
            }

            @Override
            public void visit(DirectoryNode directory) {
                calls.add("directory " + directory.getName());
            }
        };

        Element file = new FileNode("readme.md", 12);
        Element directory = new DirectoryNode("src");
        file.accept(recorder);
        directory.accept(recorder);
        assertEquals(List.of("file readme.md", "directory src"), calls);
    }

    @Test
    void totalSizeIncludesEveryFileAcrossThreeLevels() {
        DirectoryNode root = new DirectoryNode("src");
        DirectoryNode logs = new DirectoryNode("logs");
        DirectoryNode old = new DirectoryNode("old");
        root.addElement(logs);
        logs.addElement(old);
        root.addElement(new FileNode("readme.md", 200));
        logs.addElement(new FileNode("today.log", 512));
        old.addElement(new FileNode("yesterday.log", 40));

        TotalSizeVisitor size = new TotalSizeVisitor();
        root.accept(size);
        assertEquals(752L, size.getTotalSize());
    }

    @Test
    void searchRequiresBothSuffixAndInclusiveMinimumSize() {
        DirectoryNode root = new DirectoryNode("src");
        root.addElement(new FileNode("short.log", 9));
        root.addElement(new FileNode("notes.txt", 30));
        root.addElement(new FileNode("exact.log", 10));

        SearchVisitor search = new SearchVisitor(".log", 10);
        root.accept(search);
        assertEquals(List.of("exact.log"), search.getMatches());

        SearchVisitor textSearch = new SearchVisitor(".txt", 30);
        root.accept(textSearch);
        assertEquals(List.of("notes.txt"), textSearch.getMatches());
    }

    @Test
    void searchSortsPathsRelativeToChosenSubtree() {
        DirectoryNode root = new DirectoryNode("src");
        DirectoryNode chosen = new DirectoryNode("reports");
        DirectoryNode dir = new DirectoryNode("dir");
        root.addElement(chosen);
        root.addElement(new FileNode("outside.log", 50));
        chosen.addElement(new FileNode("z.log", 20));
        chosen.addElement(dir);
        dir.addElement(new FileNode("pp.log", 512));
        chosen.addElement(new FileNode("a.log", 10));

        SearchVisitor search = new SearchVisitor(".log", 10);
        chosen.accept(search);
        assertEquals(List.of("a.log", "dir/pp.log", "z.log"), search.getMatches());
    }

    @Test
    void statisticsCountRootAndRestoreDepthBetweenSiblings() {
        DirectoryNode root = new DirectoryNode("src");
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        root.addElement(first);
        first.addElement(new DirectoryNode("empty"));
        root.addElement(second);
        second.addElement(new FileNode("pp.log", 512));

        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);
        assertEquals(1, stats.getFileCount());
        assertEquals(4, stats.getDirectoryCount());
        assertEquals(2, stats.getMaxDepth());
    }

    @Test
    void emptyDirectoryCountsAsOneDirectoryAtDepthZero() {
        DirectoryNode root = new DirectoryNode("empty");
        StatisticsVisitor stats = new StatisticsVisitor();
        TotalSizeVisitor size = new TotalSizeVisitor();
        root.accept(stats);
        root.accept(size);
        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaxDepth());
        assertEquals(0L, size.getTotalSize());
    }

    @Test
    void searchWithNoMatchingFilesReturnsAnEmptyList() {
        DirectoryNode root = new DirectoryNode("src");
        root.addElement(new FileNode("readme.md", 200));
        SearchVisitor search = new SearchVisitor(".log", 0);
        root.accept(search);
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void freshVisitorsProduceIndependentResultsOnRepeatedRuns() {
        DirectoryNode root = new DirectoryNode("src");
        root.addElement(new FileNode("pp.log", 512));
        for (int run = 0; run < 2; run++) {
            TotalSizeVisitor size = new TotalSizeVisitor();
            SearchVisitor search = new SearchVisitor(".log", 10);
            StatisticsVisitor stats = new StatisticsVisitor();
            root.accept(size);
            root.accept(search);
            root.accept(stats);
            assertEquals(512L, size.getTotalSize());
            assertEquals(List.of("pp.log"), search.getMatches());
            assertEquals(1, stats.getFileCount());
            assertEquals(1, stats.getDirectoryCount());
            assertEquals(1, stats.getMaxDepth());
        }
    }

    @Test
    void rejectsNegativeSizeAndAllowsZeroBytes() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.log", -1));
        assertEquals(0L, new FileNode("empty.log", 0).getSize());
    }

    @Test
    void rejectsDuplicateSiblingNamesAcrossElementTypes() {
        DirectoryNode root = new DirectoryNode("src");
        FileNode original = new FileNode("data", 10);
        root.addElement(original);
        assertThrows(IllegalArgumentException.class,
                () -> root.addElement(new DirectoryNode("data")));
        assertEquals(List.of(original), List.copyOf(root.getChildren()));
    }

    @Test
    void rejectsASecondParentAndPreservesOriginalParent() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode file = new FileNode("pp.log", 512);
        first.addElement(file);
        assertThrows(IllegalArgumentException.class, () -> second.addElement(file));
        assertEquals(List.of(file), List.copyOf(first.getChildren()));
        assertTrue(second.getChildren().isEmpty());
    }

    @Test
    void rejectsSelfAndAncestorCycles() {
        DirectoryNode root = new DirectoryNode("src");
        DirectoryNode child = new DirectoryNode("dir");
        root.addElement(child);
        assertThrows(IllegalArgumentException.class, () -> child.addElement(child));
        assertThrows(IllegalArgumentException.class, () -> child.addElement(root));
        assertTrue(child.getChildren().isEmpty());
        assertEquals(List.of(child), List.copyOf(root.getChildren()));
    }
}
