package eu.ortlepp.blogbuilder.gui;

import eu.ortlepp.blogbuilder.BlogBuilder;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * A simple GUI for BlogBuilder. Makes all actions available in one GUI window.
 *
 * @author Thorsten Ortlepp
 * @since 0.8
 */
public final class Window extends JFrame {

    /** Serial Version UID. Generated by Eclipse IDE. */
    private static final long serialVersionUID = 3204464669355771850L;

    /** The controller for the window. Manages all actions triggered in the GUI. */
    final Controller controller;

    /** Text area to show logging messages. */
    JLoggingTextArea console;

    /** Status bar to show status messages. */
    private JLabel status;

    /** Button to open a project. */
    private JButton btnOpenProject;

    /** Button to start the initialization action on the currently opened project. */
    private JButton btnActionInit;

    /** Button to start the build action on the currently opened project. */
    private JButton btnActionBuild;

    /** Indicator for the GUI status: true = GUI locked, false = GUI not locked. */
    boolean locked;


    /**
     * Constructor, initializes the window and its controller.
     */
    public Window() {
        super();
        controller = new Controller();
        locked = false;

        /* Try to set System Look and Feel */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println("Setting System Look and Feel failed");
        }

        initWindow();
        new Thread(new Unlocker()).start();
    }


    /**
     * Initialize the window and all GUI components it contains.
     */
    private void initWindow() {
        /* Window properties */
        setTitle("BlogBuilder " + BlogBuilder.VERSION);
        setSize(800, 300);
        setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new CloseListener());

        /* Center the window */
        final int xPos = (int) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - (int) getSize().width / 2;
        final int yPos = (int) Toolkit.getDefaultToolkit().getScreenSize().height / 2 - (int) getSize().height / 2;
        setLocation(xPos, yPos);

        /* Initialize status bar label */
        status = new JLabel();
        updateStatusProject();

        /* Container for project selection */
        final JPanel projectPanel = new JPanel();
        projectPanel.setBorder(BorderFactory.createTitledBorder("Project"));
        projectPanel.setLayout(new GridLayout(0, 1));
        btnOpenProject = new JButton("Select a project...");
        projectPanel.add(createButton(btnOpenProject, new SelectListener()));

        /* Container for actions (buttons) */
        final JPanel actionPanel = new JPanel();
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionPanel.setLayout(new GridLayout(0, 1));

        /* Add buttons to container */
        btnActionInit = new JButton("Initialize a new project");
        actionPanel.add(createButton(btnActionInit, new InitializeListener()));
        btnActionBuild = new JButton("Build an existing project");
        actionPanel.add(createButton(btnActionBuild, new BuildListener()));

        /* Put project selection and actions into a container */
        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(projectPanel, BorderLayout.PAGE_START);
        leftPanel.add(actionPanel, BorderLayout.CENTER);

        /* Create a container for the output / colsole */
        final JPanel outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputPanel.setLayout(new BorderLayout());
        outputPanel.add(createConsole(), BorderLayout.CENTER);

        /* Put the two main containers and the status bar into the window */
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.LINE_START);
        add(outputPanel, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.PAGE_END);
    }


    /**
     * Create a button. The button is wrapped in a panel.
     *
     * @param button The button to initialize
     * @param listener The ActionListener that is connected with the button
     * @return The initialized button (wrapped in a panel)
     */
    private JPanel createButton(final JButton button, final ActionListener listener) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        button.addActionListener(listener);
        panel.add(button);
        return panel;
    }


    /**
     * Initialize the logging console. To be scrollable, the text area is wrapped in a ScrollPane.
     *
     * @return The ScrollPane which contains the initialized logging console
     */
    private JScrollPane createConsole() {
        console = new JLoggingTextArea();
        console.setText("");
        final JScrollPane scrollConsole = new JScrollPane(console);
        scrollConsole.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollConsole.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollConsole;
    }


    /**
     * Initialize the status bar. The label of the status bar is wrapped in a panel.
     *
     * @return The initialized status bar
     */
    private JPanel createStatusBar() {
        status.setHorizontalAlignment(SwingConstants.LEFT);
        status.setBorder(new CompoundBorder(status.getBorder(), new EmptyBorder(0, 3, 0, 3)));
        final JPanel statusbar = new JPanel();
        statusbar.setLayout(new BorderLayout());
        statusbar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusbar.add(status, BorderLayout.CENTER);
        return statusbar;
    }


    /**
     * Show the currently opened project in the status bar. The project is taken from the controller.
     */
    void updateStatusProject() {
        status.setText(String.format("Current project: %s", controller.getProject()));
    }


    /**
     * Locks the GUI. No further action can be triggered when the GUI is locked.
     */
    void lockGui() {
        locked = true;
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        btnOpenProject.setEnabled(false);
        btnActionInit.setEnabled(false);
        btnActionBuild.setEnabled(false);
    }


    /**
     * Unlocks the GUI. All actions can be triggered when the GUI is unlocked.
     */
    void unlockGui() {
        btnOpenProject.setEnabled(true);
        btnActionInit.setEnabled(true);
        btnActionBuild.setEnabled(true);
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        locked = false;
    }


    /**
     * Show the window.
     */
    public void showWindow() {
        setVisible(true);
    }






    /**
     * The ActionListener for the "Select a project..." button. Opens either an existing project
     * or prepares the initialization of a new project.
     *
     * @author Thorsten Ortlepp
     * @since 0.8
     */
    private class SelectListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent event) {
            /* New or existing project? */
            final Object[] options = {"Create a new project", "Select an existing project"};
            final int selection =
                    JOptionPane.showOptionDialog(Window.this, "Create a new project or open an existing project?",
                            "Select a project...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            options, options[1]);

            if (selection == JOptionPane.YES_OPTION) {
                /* Create a new project */

                final String project =
                        JOptionPane.showInputDialog(Window.this, "Please enter the name of the new project:");
                if (project != null) {
                    final String path = selectDirectory("Select the project's loaction...");

                    if (!path.isEmpty()) {
                        controller.setProject(path + File.separator + project);
                        updateStatusProject();
                    }
                }

            } else if (selection == JOptionPane.NO_OPTION) {
                /* Open an existing project */

                final String directory = selectDirectory("Select existing project...");
                if (!directory.isEmpty()) {
                    controller.setProject(directory);
                    updateStatusProject();
                }
            }
        }


        /**
         * Select a directory; opens a file chooser which only can choose directories.
         *
         * @param chooserTitle The title of the directory chooser
         * @return The selected directory; empty if choosing is aborted
         */
        private String selectDirectory(final String chooserTitle) {
            final JFileChooser dirChooser = new JFileChooser();
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            dirChooser.setDialogTitle(chooserTitle);
            dirChooser.setMultiSelectionEnabled(false);
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setAcceptAllFileFilterUsed(false);

            /* Open or aborted? */
            if (dirChooser.showOpenDialog(Window.this) == JFileChooser.APPROVE_OPTION) {
                return dirChooser.getSelectedFile().toString();
            }
            return "";
        }

    }


    /**
     * The ActionListener for the "Initialize a new project" button. Runs the initialization action for
     * the currently selected directory.
     *
     * @author Thorsten Ortlepp
     * @since 0.8
     */
    private class InitializeListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent event) {
            lockGui();
            console.setText("");
            controller.runInitialization();
        }
    }


    /**
     * The ActionListener for the "Build an existing project" button. Runs the build action for the
     * currently opened project.
     *
     * @author Thorsten Ortlepp
     * @since 0.8
     */
    private class BuildListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent event) {
            lockGui();
            console.setText("");
            controller.runBuild();
        }
    }


    /**
     * Extended WindowAdapter to trigger the saving of the currently opened project.
     *
     * @author Thorsten Ortlepp
     * @since 0.8
     */
    private class CloseListener extends WindowAdapter {

        @Override
        public void windowClosing(final WindowEvent event) {
            super.windowClosing(event);
            controller.saveProject();
        }
    }


    /**
     * A thread that unlocks the GUI as soon as the action
     * (in the controller) is finished.
     *
     * @author Thorsten Ortlepp
     * @since 0.8
     */
    private class Unlocker implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    if (locked && !controller.isActive()) {
                        unlockGui();
                    }

                    TimeUnit.MILLISECONDS.sleep(300);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
