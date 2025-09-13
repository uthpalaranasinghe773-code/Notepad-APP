import javax.swing.*;
import java.awt.*;
import java.io.*;

public class NotepadApp extends JFrame {

    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font MENU_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color MENU_FOREGROUND = new Color(25, 25, 112);

    private final JTextArea textArea;
    private final JFileChooser fileChooser;
    private final JLabel statusBar;

    // Themes applied in the light mode and dark mode
    private final Color LIGHT_BG = Color.WHITE;
    private final Color LIGHT_FG = Color.BLACK;
    private final Color DARK_BG = Color.BLACK;   
    private final Color DARK_FG = Color.WHITE;   


    public NotepadApp() {
        // Setting up the system default view
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Configure main frame
        setTitle("Notepad");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Initialize text area with scroll pane
        textArea = new JTextArea();
        textArea.setFont(TEXT_FONT);
        textArea.setLineWrap(false); 
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Initialize file chooser
        fileChooser = new JFileChooser();

        //Creatng the Status bar
        statusBar = new JLabel(" Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        // Building the  menu bar
        setJMenuBar(createMenuBar());

        // Layout
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Starting  with light mode
        applyLightMode();
    }

    // menu bar with File, Edit, View, and Help menus
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu()); 
        menuBar.add(createHelpMenu());
        return menuBar;
    }


     // File menu (Open, Save, Exit)
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        styleMenu(fileMenu);

        JMenuItem openItem = createMenuItem("Open", "ctrl O", e -> openFile());
        JMenuItem saveItem = createMenuItem("Save", "ctrl S", e -> saveFile());
        JMenuItem exitItem = createMenuItem("Exit", "ctrl Q", e -> dispose());

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        return fileMenu;
    }

    // Edit menu (Cut, Copy, Paste, Select All, Clear)
      private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        styleMenu(editMenu);

        JMenuItem cutItem = createMenuItem("Cut", "ctrl X", e -> textArea.cut());
        JMenuItem copyItem = createMenuItem("Copy", "ctrl C", e -> textArea.copy());
        JMenuItem pasteItem = createMenuItem("Paste", "ctrl V", e -> textArea.paste());
        JMenuItem selectAllItem = createMenuItem("Select All", "ctrl A", e -> textArea.selectAll());
        JMenuItem clearItem = createMenuItem("Clear", "ctrl D", e -> textArea.setText(""));

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        editMenu.add(clearItem);

        return editMenu;
    }

     //View menu (Word Wrap, Dark/Light Mode)
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        styleMenu(viewMenu);

        // Word Wrap toggle
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
        wordWrapItem.setFont(MENU_FONT);
        wordWrapItem.addActionListener(e -> {
            boolean enabled = wordWrapItem.isSelected();
            textArea.setLineWrap(enabled);
            updateStatus("Word Wrap " + (enabled ? "enabled" : "disabled"));
        });
        viewMenu.add(wordWrapItem);

        viewMenu.addSeparator();

        // Theme options with radio button
        JRadioButtonMenuItem lightMode = new JRadioButtonMenuItem("Light Mode");
        JRadioButtonMenuItem darkMode = new JRadioButtonMenuItem("Dark Mode");

        lightMode.setFont(MENU_FONT);
        darkMode.setFont(MENU_FONT);

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightMode);
        themeGroup.add(darkMode);

        lightMode.setSelected(true);

        lightMode.addActionListener(e -> applyLightMode());
        darkMode.addActionListener(e -> applyDarkMode());

        viewMenu.add(lightMode);
        viewMenu.add(darkMode);

        return viewMenu;
    }

    //Help menu (About).

        private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        styleMenu(helpMenu);

        JMenuItem aboutItem = createMenuItem("About", "F1",
                e -> JOptionPane.showMessageDialog(this,
                        "NotepadApp v1.1\nBuilt with Java Swing",
                        "About Notepad",
                        JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(aboutItem);
        return helpMenu;
    }

    //menu item with accelerator and action.
   
    private JMenuItem createMenuItem(String text, String shortcut, AbstractAction action) {
        JMenuItem item = new JMenuItem(action);
        item.setText(text);
        item.setFont(MENU_FONT);
        item.setToolTipText(text + " (" + shortcut + ")");
        item.setAccelerator(KeyStroke.getKeyStroke(shortcut));
        return item;
    }

    //menu item with lambda action listener.
    private JMenuItem createMenuItem(String text, String shortcut, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(MENU_FONT);
        item.setToolTipText(text + " (" + shortcut + ")");
        item.setAccelerator(KeyStroke.getKeyStroke(shortcut));
        item.addActionListener(listener);
        return item;
    }

     // styling  of the  menus
    private void styleMenu(JMenu menu) {
        menu.setFont(MENU_FONT);
        menu.setForeground(MENU_FOREGROUND);
    }

    //Opens a text file.
    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
                updateStatus("Opened: " + file.getName());
            } catch (IOException ex) {
                showError("Error reading file: " + ex.getMessage());
            }
        }
    }

    // Saves the content of the text area
    private void saveFile() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
                updateStatus("Saved: " + file.getName());
            } catch (IOException ex) {
                showError("Error saving file: " + ex.getMessage());
            }
        }
    }

    //Shows an error dialog and updates status bar
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        updateStatus("Error: " + message);
    }

    //Updates the status bar message at the bottom of the window.
    private void updateStatus(String message) {
        statusBar.setText(" " + message);
    }


    //light mode theme of the application
    private void applyLightMode() {
        textArea.setBackground(LIGHT_BG);
        textArea.setForeground(LIGHT_FG);
        textArea.setCaretColor(LIGHT_FG);
        updateStatus("Light Mode enabled");
    }

     //dark mode theme of the application
        private void applyDarkMode() {
        textArea.setBackground(DARK_BG);
        textArea.setForeground(DARK_FG);
        textArea.setCaretColor(DARK_FG);
        updateStatus("Dark Mode enabled");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NotepadApp().setVisible(true));
    }
}
