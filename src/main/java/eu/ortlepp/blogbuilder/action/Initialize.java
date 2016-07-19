package eu.ortlepp.blogbuilder.action;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import eu.ortlepp.blogbuilder.tools.Config;

/**
 * Action: Initialize a new project.
 *
 * @author Thorsten Ortlepp
 */
public class Initialize {

    /** A logger to write out messages to the user. */
    private static final Logger LOGGER = Logger.getLogger(Initialize.class.getName());

    /** The directory of the project to initialize. */
    private final Path directory;


    /**
     * Run the initialization process for the project.
     *
     * @param directory Directory of the project to initialize
     */
    public static void initialize(Path directory) {
        new Initialize(directory).process();
    }


    /**
     * Constructor, prepare the initialization.
     *
     * @param directory
     */
    private Initialize(Path directory) {
        this.directory = directory;
    }


    /**
     * Run the initialization process step by step.
     */
    private void process() {
        try {
            createDirectories();
            copyFiles();
            LOGGER.info("Initialization successful");
        } catch (IOException ex) {
            LOGGER.severe(String.format("Initialization failed: %s", ex.getMessage()));
        }
    }


    /**
     * Create all directories and subdirectories.
     *
     * @throws IOException Error while creating the directories
     */
    private void createDirectories() throws IOException {
        String dirStr = directory.toString();

        Files.createDirectories(directory);
        LOGGER.info(String.format("Created project directory %s", directory.toString()));

        Files.createDirectory(Paths.get(dirStr, Config.DIR_BLOG));
        Files.createDirectory(Paths.get(dirStr, Config.DIR_CONTENT));
        Files.createDirectory(Paths.get(dirStr, Config.DIR_RESOURCES));
        Files.createDirectory(Paths.get(dirStr, Config.DIR_TEMPLATES));
        LOGGER.info("Created directories");

        Files.createDirectory(Paths.get(dirStr, Config.DIR_CONTENT, "2015"));
        Files.createDirectory(Paths.get(dirStr, Config.DIR_CONTENT, "2016"));
        Files.createDirectory(Paths.get(dirStr, Config.DIR_RESOURCES, "images"));
        LOGGER.info("Created subdirectories");
    }


    /**
     * Copy sample files from the JAR archive into the created directories.
     *
     * @throws IOException Error while copying the files
     */
    private void copyFiles() throws IOException {
        String dirStr = directory.toString();

        Files.copy(getResourceStream("config/blog.properties"), Paths.get(dirStr, "blog.properties"));
        LOGGER.info("Created configuration file");

        Files.copy(getResourceStream("content/minions_ipsum_bananaaa_version.md"), Paths.get(dirStr, Config.DIR_CONTENT, "minions_ipsum_bananaaa_version.md"));
        Files.copy(getResourceStream("content/minions_ipsum_latin_version.md"), Paths.get(dirStr, Config.DIR_CONTENT, "minions_ipsum_latin_version.md"));
        Files.copy(getResourceStream("content/zombie_ipsum_part_1.md"), Paths.get(dirStr, Config.DIR_CONTENT, "2015", "zombie_ipsum_part_1.md"));
        Files.copy(getResourceStream("content/zombie_ipsum_part_2.md"), Paths.get(dirStr, Config.DIR_CONTENT, "2015", "zombie_ipsum_part_2.md"));
        Files.copy(getResourceStream("content/cupcake_ipsum_part_1.md"), Paths.get(dirStr, Config.DIR_CONTENT, "2016", "cupcake_ipsum_part_1.md"));
        Files.copy(getResourceStream("content/cupcake_ipsum_part_2.md"), Paths.get(dirStr, Config.DIR_CONTENT, "2016", "cupcake_ipsum_part_2.md"));
        Files.copy(getResourceStream("content/cupcake_ipsum_part_3.md"), Paths.get(dirStr, Config.DIR_CONTENT, "2016", "cupcake_ipsum_part_3.md"));
        Files.copy(getResourceStream("content/veggie_ipsum.md"), Paths.get(dirStr, Config.DIR_CONTENT, "2016", "veggie_ipsum.md"));
        LOGGER.info("Created sample Markdown files");

        Files.copy(getResourceStream("resources/image.jpg"), Paths.get(dirStr, Config.DIR_RESOURCES, "images", "image.jpg"));
        Files.copy(getResourceStream("resources/style.css"), Paths.get(dirStr, Config.DIR_RESOURCES, "style.css"));
        LOGGER.info("Created sample resource files");

        Files.copy(getResourceStream("templates/include_footer.ftl"), Paths.get(dirStr, Config.DIR_TEMPLATES, "include_footer.ftl"));
        Files.copy(getResourceStream("templates/include_header.ftl"), Paths.get(dirStr, Config.DIR_TEMPLATES, "include_header.ftl"));
        Files.copy(getResourceStream("templates/page_blogpost.ftl"), Paths.get(dirStr, Config.DIR_TEMPLATES, "page_blogpost.ftl"));
        Files.copy(getResourceStream("templates/page_category.ftl"), Paths.get(dirStr, Config.DIR_TEMPLATES, "page_category.ftl"));
        Files.copy(getResourceStream("templates/page_index.ftl"), Paths.get(dirStr, Config.DIR_TEMPLATES, "page_index.ftl"));
        Files.copy(getResourceStream("templates/page_page.ftl"), Paths.get(dirStr, Config.DIR_TEMPLATES, "page_page.ftl"));
        LOGGER.info("Created Freemarker templates");
    }


    /**
     * Get a resource file from the JAR archive.
     *
     * @param resource The resource file to get
     * @return The resource file as stream
     */
    private InputStream getResourceStream(String resource) {
        return this.getClass().getClassLoader().getResourceAsStream("eu/ortlepp/blogbuilder/samples/" + resource);
    }

}