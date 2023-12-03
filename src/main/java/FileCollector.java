import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileCollector {

    public static void main(String[] args) {
        // Parse the command-line arguments and create a configuration object.
        Config config = parseArguments(args);

        // Check if the mandatory source and destination paths are provided.
        if (config.source == null || config.destination == null) {
            System.out.println("Source and destination paths are required.");
            return;
        }

        // Start timing the operation.
        long startTime = System.currentTimeMillis();

        // List to hold the paths of files to be copied.
        List<Path> paths;
        try {
            // Find files with specified extensions within the given depth.
            paths = findByFilesExtension(config.source, config.depth, config.fileExtensions);
        } catch (IOException e) {
            // Handle any IO exceptions that occur during file filtering.
            System.out.println("Error while filtering files: " + e.getMessage());
            return;
        }

        // Initialize counters for files and total bytes copied.
        AtomicLong filesCounter = new AtomicLong(1);
        AtomicLong totalBytes = new AtomicLong(0);

        // Iterate over the filtered files and copy each one.
        paths.forEach(file -> copyFile(file, config.destination, filesCounter, totalBytes));

        // Print a summary of the operation once all files are copied.
        printSummary(startTime, totalBytes.get());
    }

    private static void copyFile(Path file, Path destination, AtomicLong filesCounter, AtomicLong totalBytes) {
        // Convert Path to File for easier handling.
        File sourceFile = file.toFile();
        // Resolve the destination path for the current file.
        Path destFile = destination.resolve(sourceFile.getName());

        // Print the file copy status.
        System.out.println("#" + filesCounter.getAndIncrement() + " File " + sourceFile.getName() + " was copied");
        // Update the total bytes counter.
        totalBytes.addAndGet(sourceFile.length());

        try {
            // Perform the actual file copy.
            Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Handle any IO exceptions during file copy.
            System.err.println("Error copying file " + sourceFile.getName() + ": " + e.getMessage());
        }
    }

    private static void printSummary(long startTime, long totalBytes) {
        // Calculate the total time taken for the operation.
        double performance = (System.currentTimeMillis() - startTime) / 1000.0;
        // Print out the performance and total bytes copied.
        System.out.println("\nPerformance: " + performance + " sec");
        System.out.println("Total copied: " + totalBytes / 1000000 + " MB");
    }

    private static Config parseArguments(String[] args) {
        // Initialize a new configuration object.
        Config config = new Config();

        // Loop over the arguments to parse them.
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                // Handle file extension argument.
                case "--extension":
                case "-e":
                    config.fileExtensions.add(args[++i].toLowerCase());
                    break;
                // Handle source path argument.
                case "--source":
                    config.source = Paths.get(args[++i]);
                    break;
                // Handle destination path argument.
                case "--dest":
                    config.destination = Paths.get(args[++i]);
                    break;
                // Handle depth argument for file search.
                case "--depth":
                    config.depth = Integer.parseInt(args[++i]);
                    break;
            }
        }
        // Return the filled configuration object.
        return config;
    }

    public static List<Path> findByFilesExtension(Path path, int depth, Set<String> fileExtensions)
            throws IOException {
        // Check if the provided path is a directory.
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        // Use Files.walk to find all files within the given depth.
        try (Stream<Path> walk = Files.walk(path, depth)) {
            // Filter the files by the specified extensions and collect them into a list.
            return walk
                    .filter(Files::isRegularFile)
                    .filter(file -> fileExtensions.stream().anyMatch(file.toString()::contains))
                    .collect(Collectors.toList());
        }
    }

    // Inner class to hold configuration options.
    static class Config {
        Set<String> fileExtensions = new HashSet<>();
        Path source;
        Path destination;
        int depth = Integer.MAX_VALUE;
    }
}

