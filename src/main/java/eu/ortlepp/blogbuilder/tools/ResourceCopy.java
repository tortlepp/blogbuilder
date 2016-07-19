package eu.ortlepp.blogbuilder.tools;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

/**
 * A tool class to copy the contents from the resources directory to the directory with the built blog.
 *
 * @author Thorsten Ortlepp
 */
public class ResourceCopy extends SimpleFileVisitor<Path> {

    /** A logger to write out messages to the user. */
    private static final Logger LOGGER = Logger.getLogger(ResourceCopy.class.getName());

    /** The target directory for the built blog. */
    private static Path target;

    /** A counter for all successfully copied files. */
    private static int counter;


    /**
     * Do the copying: Copy all files from the resources directory to the target directory for built blogs.
     * If necessary copy the directory structure as well. If a file already exists in the target directory
     * it is skipped.
     *
     * @param directory The project directory which contains the resources an the target directory
     */
    public static void copy(Path directory) {
        target = Paths.get(directory.toString(), Config.DIR_BLOG);
        counter = 0;
        try {
            Files.walkFileTree(Paths.get(directory.toString(), Config.DIR_RESOURCES), new ResourceCopy());
            LOGGER.info(String.format("%d resource files copied", counter));
        } catch (IOException ex) {
            LOGGER.severe(String.format("Error while copying: %s", ex.getMessage()));
            throw new RuntimeException(ex);
        }
    }


    /**
     * Visiting a file: Copy the file from the resources directory to the target directory for the built blog.
     *
     * @param file The visited file itself
     * @param attrs The attributes of the file
     * @return The result of the visit: Continue to visit other files and directories
     * @throws IOException Error while deleting the file
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        /* Create full target path */
        Path temp = target.relativize(file);
        temp = temp.subpath(2, temp.getNameCount());
        temp = Paths.get(target.toString(), temp.toString());

        /* Create folders and copy file */
        if (Files.exists(temp)) {
            LOGGER.warning(String.format("Resource file %s already exists, file not copied", temp.getFileName().toString()));
        } else {
            Files.createDirectories(temp.getParent());
            Files.copy(file, temp);
            counter++;
            LOGGER.info(String.format("Resource file %s copied", temp.getFileName().toString()));
        }

        return FileVisitResult.CONTINUE;
    }

}