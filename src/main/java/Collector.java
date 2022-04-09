import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Collector {
    public static void main(String[] args) {
        /*Service variables initialization*/
        Set<String> fileExtensions = new HashSet<>();
        int depth = Integer.MAX_VALUE;
        Path source = null;
        Path dest = null;
        AtomicReference<Double> totalBytes = new AtomicReference<>((double) 0);
        AtomicLong filesCounter = new AtomicLong(1);
        Long startTime = new Date().getTime();
        List<Path> paths = null;
        /*Argument parsing*/
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--extension":
                case "-e":
                    fileExtensions.add(args[i + 1].toLowerCase());
                    fileExtensions.add(args[i + 1].toUpperCase());
                    break;
                case "--source":
                    source = Paths.get(args[i + 1]);
                    break;
                case "--dest":
                    dest = Paths.get(args[i + 1]);
                    break;
                case "--depth":
                    depth = Integer.parseInt(args[i + 1]);
                    break;
            }
        }
        /*Filter all files*/
        try {
            paths = findByFilesExtension(source, depth, fileExtensions);
        } catch (IOException e) {
            System.out.println("Invalid path value");
        }
        /*Copying files*/
        Path finalDest = dest;
        assert paths != null;

        paths.forEach(file -> {
            File sourceFile = new File(String.valueOf(file));
            File destFile = new File(finalDest + "\\" + sourceFile.getName());
            System.out.println("#" + filesCounter + " File " + sourceFile.getName() + " was copied");
            filesCounter.getAndIncrement();
            totalBytes.updateAndGet(v -> v + sourceFile.length());
            try {
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        /*Printing service data*/
        Long endTime = new Date().getTime();
        double performance = (endTime - startTime) / 1000.0;
        System.out.println();
        System.out.println("Performance " + performance + " sec");
        System.out.println("Total copied " + totalBytes.get() / 1000000 + " MB");
    }

    public static List<Path> findByFilesExtension(Path path, int depth, Set<String> fileExtensions)
            throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        AtomicReference<List<Path>> result = new AtomicReference<>();

        Path walker = Files.walkFileTree(
                path,
                new HashSet<>(Collections.singletonList(FileVisitOption.FOLLOW_LINKS)),
                Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException e) {
                        System.err.printf("Visiting failed for %s\n", file);
                        return FileVisitResult.SKIP_SUBTREE; // skip doesn't work when get on RECYCLE.BIN
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir,
                                                             BasicFileAttributes attrs) {
                        if (dir.toString().contains("RECYCLE")) {
                            return FileVisitResult.SKIP_SIBLINGS;
                        } // skip doesn't work when get on RECYCLE.BIN
                        System.out.printf("About to visit directory %s\n", dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
        /*Getting all files paths and then filter them by Set of file extensions*/
        try (Stream<Path> walk = Files.walk(walker, depth)) {
            result.set(walk
                    .filter(Files::isRegularFile)
                    .filter(file -> fileExtensions.stream().anyMatch(e -> file.toString().contains(e)))
                    .collect(Collectors.toList()));
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
        return result.get();
    }
}
