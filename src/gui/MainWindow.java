package gui;


import ai.Clues;
import ai.GeneticSolver;
import common.Color;
import common.Debug;
import common.Row;
import game.ControlInterface;
import game.Database;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;


/*
 * Provides a GUI for the Mastermind game engine.
 *
 * The class contains all GUI elements and functions that a user
 * needs to interact with the Mastermind game engine (package game).
 * The Mastermind engine is self-contained, so that any UI
 * (GUI, console, etc. ) only has to use the ControlInterface of the
 * game package. This design makes the GUI and engine nearly independent
 * form each other. Logical changes in the Mastermind engine don't
 * affect the GUI. Of course graphical changes in the GUI don't affect the
 * Mastermind engine neither.
 *
 * see game.ControlInterface
 */

public class MainWindow extends javax.swing.JFrame {
    /*
     * The delay between two guesses (if the makeGuess()-Function of the AI was fast enough).
     * Object variables used by various functions.
     */
    private final int AI_GUESS_DELAY = 500;
    private static ControlInterface ci = new ControlInterface();
    private JDialog aboutDialog;
    private JButton chosenColButton;
    private JSlider colQuantSlider;
    private JCheckBox doubleColCheckBox;
    private JFileChooser fileChooser;
    private JMenuBar gameMenuBar;
    private JSlider gameWidthSlider;
    private JSlider gameTriesSlider;
    private JDialog settingsDialog;
    private JComboBox<String> gameModeComboBox;
    private JButton colButtons[];
    private JScrollPane colScrollPane;
    private JButton secretCodeButtons[];
    private JPanel secretCodePanel;
    private JButton gameButtons[][];
    private JPanel gamePanel;
    private JScrollPane gameScrollPane;
    private JLabel gamePlaceholder;
    private JLabel gameState;
    private Timer aiTimer;
    private Timer timer;
    private Login login;
    Database db = game.Database.getInstance();
    long time = 60000;


    /*
     * Creates new form MainWindow and initializes all components.
     */
    public MainWindow() {

        setLocation(460, 150);
        initKeyListener();
        initComponents();
        initNewGame();

        ArrayList<String> files = new ArrayList<String> ();
        files.add("audio/phrases/welcome_4digit_code_game.wav");
        files.add("audio/phrases/white_black_pegs_explained.wav");
        play(files);
    }


    public static void play(ArrayList<String> files){
        byte[] buffer = new byte[4096];
        for (String filePath : files) {
            File file = new File(filePath);
            try {
                AudioInputStream is = AudioSystem.getAudioInputStream(file);
                AudioFormat format = is.getFormat();
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();
                while (is.available() > 0) {
                    int len = is.read(buffer);
                    line.write(buffer, 0, len);
                }
                line.drain();
                line.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Initialize all GUI components">

    /*
     * Init all GUI components (Constructor sub-function, called only once).
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        aboutDialog = new JDialog();
        fileChooser = new JFileChooser();
        settingsDialog = new JDialog();
        colQuantSlider = new JSlider();
        gameWidthSlider = new JSlider();
        gameTriesSlider = new JSlider();
        doubleColCheckBox = new JCheckBox();
        gameScrollPane = new JScrollPane();
        secretCodePanel = new JPanel();
        chosenColButton = new JButton();
        colScrollPane = new JScrollPane();
        gameMenuBar = new JMenuBar();
        gamePanel = new JPanel();
        gamePlaceholder = new JLabel();
        gameState = new JLabel();
        gameModeComboBox = new JComboBox<String>();
        login = new Login();
        db.createGameEntry(login.getName());


        // About Dialog
        aboutDialog.setTitle("About");
        // position the dialog relative to an existing frame
        aboutDialog.setLocation(585, 360);
        aboutDialog.setResizable(false);
        JButton closeButton = new JButton("Close");
        JLabel aboutLabel = new JLabel(
                "<html><b>Developer:</b> Mariia Romaniuk<br />"
                        + "<b>Project:</b> REACH Coding Challenge<br />"
                        + "<b>Version:</b> V1 05.18.2020<br />"
                        + "<b>Review:</b> Caleb Cameron, Joe Kuang");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        // About Dialog Layout
        javax.swing.GroupLayout aboutDialogLayout = new javax.swing.GroupLayout(
                aboutDialog.getContentPane());
        aboutDialog.getContentPane().setLayout(aboutDialogLayout);
        aboutDialogLayout.setHorizontalGroup(
                aboutDialogLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(aboutDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(aboutLabel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(85, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                aboutDialogLayout.createSequentialGroup()
                                        .addContainerGap(178, Short.MAX_VALUE)
                                        .addComponent(closeButton)
                                        .addContainerGap())
        );
        aboutDialogLayout.setVerticalGroup(
                aboutDialogLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(aboutDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(aboutLabel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(closeButton)
                                .addContainerGap())
        );

        // File Chooser
        fileChooser.setCurrentDirectory(
                new File(System.getProperty("user.dir")));
        // Init fileChooser filter
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Mastermind-Savegames (*." + ci.FILE_EXTENSION +
                                ")", ci.FILE_EXTENSION));
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Settings Dialog
        settingsDialog.setTitle("Settings");
        settingsDialog.setLocation(596, 216);
        settingsDialog.setResizable(false);

        JLabel colorsLabel = new JLabel("Number of digits in the range:");

        colQuantSlider.setMaximum(10);
        colQuantSlider.setMinimum(1);
        colQuantSlider.setMajorTickSpacing(1);
        colQuantSlider.setPaintLabels(true);
        colQuantSlider.setPaintTicks(true);
        colQuantSlider.setSnapToTicks(true);

        JLabel gameWidthLabel = new JLabel("Number of digits in secret code:");

        gameWidthSlider.setMaximum(8);
        gameWidthSlider.setMinimum(1);
        gameWidthSlider.setMajorTickSpacing(1);
        gameWidthSlider.setPaintLabels(true);
        gameWidthSlider.setPaintTicks(true);
        gameWidthSlider.setSnapToTicks(true);

        JLabel maxTriesLabel = new JLabel("Number of attempts:");

        gameTriesSlider.setMaximum(10);
        gameTriesSlider.setMinimum(1);
        gameTriesSlider.setMajorTickSpacing(1);
        gameTriesSlider.setPaintLabels(true);
        gameTriesSlider.setPaintTicks(true);
        gameTriesSlider.setSnapToTicks(true);

        doubleColCheckBox.setText("Use Duplicate Numbers");

        JLabel gameModeLabel = new JLabel(
                "<html>Game Mode:<br />(<b>H</b>uman - " +
                        "<b>A</b>rtificial <b>I</b>ntelligence)");

        DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>();
        dcbm.addElement("H: Codebreaker - AI: Codemaker");
        dcbm.addElement("H: Codemaker - AI: Codebreaker");
        dcbm.addElement("H: Codemaker - H: Codebreaker");
        gameModeComboBox.setModel(dcbm);

        JButton saveButton = new JButton("Apply (New Game)");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        // Settings Dialog Layout
        javax.swing.GroupLayout settingsDialogLayout =
                new javax.swing.GroupLayout(
                        settingsDialog.getContentPane());
        settingsDialog.getContentPane().setLayout(settingsDialogLayout);
        settingsDialogLayout.setHorizontalGroup(
                settingsDialogLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(settingsDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(settingsDialogLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(colorsLabel)
                                        .addComponent(colQuantSlider,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(gameWidthLabel)
                                        .addComponent(cancelButton)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                settingsDialogLayout.createSequentialGroup()
                                                        .addComponent(saveButton))
                                        .addComponent(maxTriesLabel)
                                        .addComponent(gameTriesSlider,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(gameWidthSlider,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(doubleColCheckBox)
                                        .addComponent(gameModeLabel)
                                        .addComponent(gameModeComboBox,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                )
                                .addContainerGap())
        );
        settingsDialogLayout.setVerticalGroup(
                settingsDialogLayout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(settingsDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(colorsLabel)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(colQuantSlider,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gameWidthLabel)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gameWidthSlider,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxTriesLabel)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gameTriesSlider,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(doubleColCheckBox)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gameModeLabel)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gameModeComboBox)
                                .addGap(30)
                                .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(settingsDialogLayout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(saveButton)
                                        .addComponent(cancelButton))
                                .addContainerGap())
        );

        // Main Window
        setTitle("Mastermind Game");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        JLabel guessLabel = new JLabel("Your Guess");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        getContentPane().add(guessLabel, gridBagConstraints);

        JLabel resultLabel = new JLabel("Result");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(resultLabel, gridBagConstraints);

        gameScrollPane.setPreferredSize(new Dimension(400, 400));
        gamePanel.addComponentListener(
                new ComponentListener() {
                    public void componentResized(ComponentEvent e) {
                        // Scroll down if gamePanel was resized
                        gameScrollPane.getVerticalScrollBar().setValue(
                                gameScrollPane.getVerticalScrollBar().getMaximum());
                    }

                    public void componentMoved(ComponentEvent e) {}
                    public void componentShown(ComponentEvent e) {}
                    public void componentHidden(ComponentEvent e) {}
                });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(gameScrollPane, gridBagConstraints);

        // Secret Code
        JLabel secretCodeLabel = new JLabel("Secret Code");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        getContentPane().add(secretCodeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(secretCodePanel, gridBagConstraints);

        // Choose Number
        JLabel colorLabel = new JLabel("Chosen Number");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        getContentPane().add(colorLabel, gridBagConstraints);

        chosenColButton.setHorizontalAlignment(
                javax.swing.SwingConstants.CENTER);
        chosenColButton.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(chosenColButton, gridBagConstraints);


        // Init the timer button and start the countdown
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("mm : ss");
        JButton timerButton = new JButton(sdf.format(new Date(time)));

        // Reset game timer
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                timerButton.setText(sdf.format(new Date(time)));
                time -= 1000;
                if (time == 0-1000){
                    playSound("resources/Lost.wav");
                    playSound("phrases/goodbye.wav");
                    gameState.setText("Time is up! You lost!");
                    JOptionPane.showMessageDialog(
                            null, "Time is up! You lost!", "Info:",
                            JOptionPane.INFORMATION_MESSAGE);
                    timer.stop();
                }
            }
        };
        timer = new Timer(1000, al);
        timerButton.setVisible(true);
        timer.start();

        timerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Use the equivalent action
                pauseTimerActionPerformed(evt, timer);
            }
        });

        /*
         * Remove the key binding to space key which allows the Spacebar
         * to act as a mouse click on the focused button. That is,
         * by default, hitting Space will 'click' on the focused button.
         * I want to change this behavior.
         */
        timerButton.getInputMap().put(
                KeyStroke.getKeyStroke("SPACE"), "none" );
        timerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Use the equivalent menu action.
                pauseTimerActionPerformed(evt, timer);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(timerButton, gridBagConstraints);

        // Color scroll pane
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(colScrollPane, gridBagConstraints);

        JButton showHintButton = new JButton("Show Hint");
        // Remove binding to space key
        showHintButton.getInputMap().put(
                KeyStroke.getKeyStroke("SPACE"), "none" );
        showHintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Use the equivalent menu action.
                showHintMenuItemActionPerformed(evt);
            }
        });
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(showHintButton, gridBagConstraints);

        JButton validateGuessButton = new JButton("Validate Guess");
        // Remove binding to space key
        validateGuessButton.getInputMap().put(
                KeyStroke.getKeyStroke("SPACE"), "none" );
        validateGuessButton.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        // Use the equivalent menu action.
                        validateGuessMenuItemActionPerformed(evt);
                    }
                });
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(validateGuessButton, gridBagConstraints);
        // More code now in initColorTable

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(gameState, gridBagConstraints);


        // Main Window Menus
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameMenuItem = new JMenuItem("New Game");
        newGameMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_N,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        newGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(newGameMenuItem);
        gameMenu.add(new JPopupMenu.Separator());

        JMenuItem showHintMenuItem = new JMenuItem("Show Hint");
        showHintMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_H,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        showHintMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHintMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(showHintMenuItem);

        JMenuItem validateGuessMenuItem = new JMenuItem("Validate Guess");
        validateGuessMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_V,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        validateGuessMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        validateGuessMenuItemActionPerformed(evt);
                    }
                });
        gameMenu.add(validateGuessMenuItem);

        JMenuItem setLastGuessMenuItem = new JMenuItem("Set Last Guess");
        setLastGuessMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_L,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        setLastGuessMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        setLastGuessMenuItemMenuItemActionPerformed(evt);
                    }
                });
        gameMenu.add(setLastGuessMenuItem);

        gameMenu.add(new JPopupMenu.Separator());

        JMenuItem loadMenuItem = new JMenuItem("Load Game");
        loadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(loadMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save Game");
        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_S,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(saveAsMenuItem);

        gameMenu.add(new JPopupMenu.Separator());

        JMenuItem quitMenuItem = new JMenuItem("Quit Game");
        quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        gameMenu.add(quitMenuItem);

        gameMenuBar.add(gameMenu);

        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem editSettingsMenuItem = new JMenuItem("Edit Settings");
        editSettingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_E,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        editSettingsMenuItem.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        editSettingsMenuItemActionPerformed(evt);
                    }
                });
        settingsMenu.add(editSettingsMenuItem);

        gameMenuBar.add(settingsMenu);

        JMenu helpMenu = new JMenu("Help");

        JMenuItem howToMenuItem = new JMenuItem("How To Play");
        howToMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                howToMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(howToMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem("About Mastermind");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);
        gameMenuBar.add(helpMenu);

        setJMenuBar(gameMenuBar);

        setMinimumSize(new Dimension(300, 250));
        pack();
    }

    public void restartTimer() {
        if (ci.getSettingWidth() == 4){
            time = 60000;
            timer.start();
        }
        else if (ci.getSettingWidth() == 5){
            time = 120000;
            timer.start();
        }
        else if (ci.getSettingWidth() == 6){
            time = 180000;
            timer.start();
        }
        else if (ci.getSettingWidth() == 7){
            time = 240000;
            timer.start();
        }
        else if (ci.getSettingWidth() == 8){
            time = 300000;
            timer.start();
        }
    }

    public void playSound(String soundName) {
        try {
            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
            Clip clip = AudioSystem.getClip( );
            clip.open(audioInputStream);
            clip.start( );
        }
        catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace( );
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialize/update GUI components that may be affected by changed settings">

    /*
     * Initialize all GUI components for a new game.
     */
    private void initNewGame() {
        this.requestFocus();

        // Stop AI guess timer
        if (aiTimer != null) {
            aiTimer.stop();
        }

        // Set state
        if (ci.getGameEnded() == false &&
                ci.getSettingAiMode() == false) {
            gameState.setText("Choose a number (Click or key 1, 2, 3,..) " +
                    "and place it (Click or key a, b, c,...).");
        }
        else if (ci.getGameEnded() == true){
            gameState.setText("Game is finished.");
        } else {
            gameState.setText("Set the secret code and watch the AI.");
        }

        // Init components that may be affected by changed settings.
        initGameTable();
        initSecretCode();
        initColorTable();
        restartTimer();
    }

    /*
     * Initialize the color table with the available colors.
     * The available colors depends on the color quantity setting.
     */
    private void initColorTable() {
        Color[] all = Color.values();
        colButtons = new JButton[ci.getSettingColQuant()];
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        for (int i = 0; i < ci.getSettingColQuant(); i++) {
            // Initialize buttons with numbers
            byte[] b = new byte[1];
            b[0] = (byte)(48 + i);
            colButtons[i] = new JButton(new String(b));
            // Remove binding to space key.
            colButtons[i].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            // Add listener.
            colButtons[i].addActionListener(
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            numButtonsActionPerformed(evt);
                        }
                    });
            // Set color
            java.awt.Color color = new java.awt.Color(all[i].getRGB());
            colButtons[i].setBackground(color);
            // Set size
            colButtons[i].setMinimumSize(new Dimension(95, 28));
            colButtons[i].setMaximumSize(new Dimension(95, 28));
            colButtons[i].setPreferredSize(new Dimension(95, 28));
            // Add to panel
            colButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons.add(colButtons[i]);
        }
        colScrollPane.setViewportView(buttons);
    }

    /*
     * Initialize the game table (game field).
     *
     * Creates a panel in table layout with result and guess pins.
     * If a game was loaded the table will be filled with the
     * loaded game. Otherwise a new game with one empty Row will be initialized.
     *
     * see #showLoadedGameTable()
     */
    private void initGameTable() {
        int width = ci.getSettingWidth();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gamePanel.removeAll();
        gameButtons = new JButton[0][0];
        gamePanel.setLayout(layout);

        // Add labels on the panel for key usage
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < width; i++) {
            JLabel label = new JLabel("" + (char)(97 + i), SwingConstants.CENTER);
            gbc.gridx = width + i + 1;
            gamePanel.add(label, gbc);
        }

        // Add empty row
        addRow();
        if (ci.getLoaded() == true) {
            // Show the loaded game
            showLoadedGameTable();
        }
        gameScrollPane.setViewportView(gamePanel);
        gameScrollPane.revalidate();
    }

    // Draw the game table by using loaded game data
    private void showLoadedGameTable() {
        int i = 0;
        int arn = ci.getActiveRowNumber();
        while (i < arn) {
            // Add result
            showResultRow(i);
            // Add guess
            showGameRow(i);
            if (ci.getGameEnded() == false || i < arn - 1) {
                // Add new row at the end if game not ended
                addRow();
            }
            i++;
        }
    }

    /*
     * Initialize the secret code row.
     * This function only initializes the buttons and reveals the code
     * if the game already ended.
     * A secret code is already generated by the ControlInterface.
     *
     * see game.ControlInterface
     * see game.SecretCode
     */
    private void initSecretCode() {
        int numDigits = ci.getSettingWidth();
        secretCodePanel.removeAll();
        secretCodeButtons = new JButton[numDigits];
        secretCodePanel.setLayout(new GridLayout(1, numDigits));

        for (int i = 0; i < numDigits; i++) {
            // Init
            secretCodeButtons[i] = new JButton();
            // Remove binding to space key, that prevents the JComponent from responding to SPACE presses
            secretCodeButtons[i].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            // Add listener
            secretCodeButtons[i].addActionListener(
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            secretCodeButtonsActionPerformed(evt);
                        }
                    });
            // Set size
            secretCodeButtons[i].setPreferredSize(new Dimension(20, 28));
            secretCodeButtons[i].setMinimumSize(new Dimension(20, 28));
            // Add to panel
            secretCodePanel.add(secretCodeButtons[i]);
        }
        // Reveal code only if game ended (loaded game)
        if (ci.getGameEnded() == true) {
            revealSecretCode();
        }
        secretCodePanel.revalidate();
    }

    /*
     * Initialize a global Key Listener.
     * The listener provides the game handling by keyboard.
     * In practice this means you can choose a color via key 1, 2, 3, etc.
     * and place it to the placeholders via a, b, c, etc.
     * The guess can be checked by pressing the space bar.
     *
     * see #keyTyped(java.awt.event.KeyEvent)
     */
    private void initKeyListener(){
        AWTEventListener ael = new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                // Only react to key typed events
                if (event.getID() == KeyEvent.KEY_TYPED) {
                    keyTyped((KeyEvent) event);
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(
                ael, AWTEvent.KEY_EVENT_MASK);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="About Dialog action performed">

    /*
     * Show the about dialog.
     * evt - the triggered event. Not used.
     */
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Show about dialog:
        aboutDialog.pack();
        aboutDialog.setVisible(true);
    }

    /*
     * Hide the about dialog.
     * evt - the triggered event. Not used.
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Hide about dialog:
        aboutDialog.setVisible(false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Settings Dialog action performed">

    /*
     * Show the settings dialog.
     * Before the dialog is set to visible, all elements will be
     * initialized with the current settings.
     * evt - the triggered event. Not used.
     */
    private void editSettingsMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        // Init dialog with settings.
        doubleColCheckBox.setSelected(
                ci.getSettingDoubleCols());
        gameWidthSlider.setValue(ci.getSettingWidth());
        colQuantSlider.setValue(ci.getSettingColQuant());
        gameTriesSlider.setValue(ci.getSettingMaxTries());
        gameModeComboBox.setSelectedIndex(
                ci.getSettingAiMode() == true ? 1 : 0);
        gameModeComboBox.setSelectedIndex(
                ci.getSettingDoublePlayerMode() == true ? 1 : 0);
        // Show settings dialog:
        settingsDialog.pack();
        settingsDialog.setVisible(true);
    }

    /*
     * Hide the settings dialog without saving.
     * evt - the triggered event. Not used.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Hide settings dialog:
        settingsDialog.setVisible(false);
    }

    /*
     * Save, init new game (with new settings) and hide dialog.
     * If the settings are not valid an error message will be displayed.
     * evt - the triggered event. Not used.
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        boolean dc = doubleColCheckBox.isSelected();
        int range = colQuantSlider.getValue();
        int width = gameWidthSlider.getValue();
        // Check settings.
        if(width <= range || dc) {
            // Set settings.
            ci.setSettingDoubleCols(dc);
            ci.setSettingColQuant(range);
            ci.setSettingWidth(width);
            ci.setSettingMaxTries(gameTriesSlider.getValue());
            ci.setSettingAiMode(
                    gameModeComboBox.getSelectedIndex() == 1 ? true : false);
            ci.setSettingDoublePlayerMode(
                    gameModeComboBox.getSelectedIndex() == 1 ? true : false);
            // Init new game.
            ci.newGame();
            initNewGame();
        } else {
            // Too small range of digits (without duplicates)
            // for the number of digits in the combination.
            JOptionPane.showMessageDialog(null,
                    "Too few numbers to choose from or too much numbers in the combination.\n" +
                            "Use duplicates or change number of digits in the combination" +
                            " / game width settings.",
                    "Error:", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Hide dialog
        settingsDialog.setVisible(false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Load/Save Dialog action performed">

    /*
     * Show file browser dialog and load chosen game.
     * evt - the triggered event. Not used.
     */
    private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        fileChooser.setDialogTitle("Load Game");
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION ||
                fileChooser.getSelectedFile() == null ||
                fileChooser.getSelectedFile().getAbsolutePath() == null) {
            return;
        }
        try {
            Debug.dbgPrint(fileChooser.getSelectedFile().getAbsolutePath());
            // Load game
            ci.load(
                    fileChooser.getSelectedFile().getAbsolutePath());
        }
        catch (Exception e) {
            Debug.errorPrint(e.toString());
            return;
        }
        // Init loaded game
        initNewGame();
    }

    /*
     * Show file browser dialog and save game to the given path and name.
     * evt - the triggered event. Not used.
     */
    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        fileChooser.setDialogTitle("Save Game");
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION ||
                fileChooser.getSelectedFile() == null ||
                fileChooser.getSelectedFile().getAbsolutePath() == null) {
            return;
        }
        try {
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            // Add file extension if not given by the user.
            if (file.endsWith("." + ci.FILE_EXTENSION) == false) {
                file += "." + ci.FILE_EXTENSION;
            }
            Debug.dbgPrint("Save to " + file);
            // Save game.
            ci.save(file);
        }
        catch (Exception e) {
            Debug.errorPrint(e.toString());
            return;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Sub Functions">

    /*
     * Checks if the complete active Row in the game table is set with a color.
     * return: true if all fields in the active Row are set, false otherwise.
     */
    private boolean rowIsSet(){
        int width = ci.getSettingWidth();
        for (int i = width; i < width * 2; i++){
            JButton button = (JButton)
                    gameButtons[ci.getActiveRowNumber()][i];
            // If empty (gray) cell was found return false
            if (button.getBackground().getRGB() == Color.Null.getRGB()) {
                return false;
            }
        }
        return true;
    }

    /*
     * Cycles through the secret code (GUI) and sets it in the game engine.
     * see #translateColor(java.awt.Color)
     */
    private void writeSecretCode() {
        int width = ci.getSettingWidth();
        Color[] numbers = new Color[width];
        for (int i = 0; i < width; i++) {
            numbers[i] = translateColor(secretCodeButtons[i].getBackground());
        }
        ci.setSecretCode(numbers);
    }

    /*
     * Cycles through the active game Row (GUI) and sets it in the game engine.
     * see #translateColor(java.awt.Color)
     */
    private void writeToGameField() {
        int width = ci.getSettingWidth();
        Color[] numbers = new Color[width];
        for (int i = width; i < width * 2; i++) {
            numbers[-width+i] = translateColor(
                    gameButtons[ci.getActiveRowNumber()]
                            [i].getBackground());
        }
        ci.writeToGameField(numbers);
    }

    /*
     * Translate the java.awt.Color to a common.Color.
     *
     * color - a color in the java.awt.Color format.
     * return: a color in the common.Color format.
     * see common.Color
     * see java.awt.Color
     */
    private Color translateColor (java.awt.Color color) {
        Color ret = null;
        switch (color.getRGB()) {
            case -137672:
                ret = Color.Yellow;
                break;
            case -8716289:
                ret = Color.LightBlue;
                break;
            case -8716422:
                ret = Color.LightGreen;
                break;
            case -34182:
                ret = Color.LightRed;
                break;
            case -20614:
                ret = Color.LightOrange;
                break;
            case -5276929:
                ret = Color.LightPurple;
                break;
            case -32768:
                ret = Color.Orange;
                break;
            case -8351640:
                ret = Color.Olive;
                break;
            case -871999:
                ret = Color.LightPink;
                break;
            case -837510:
                ret = Color.Pink;
                break;
            case -1:
                ret = Color.White;
                break;
            case -16777216:
                ret = Color.Black;
                break;
        }
        return ret;
    }

    /*
     * Reveals the secret code by showing it in the GUI.
     * Gets the secret code from the game engine.
     */
    private void revealSecretCode() {
        //playSound("audio/phrases/the_code_guess_was.wav");
        Color[] secret = ci.getSecretCode().getColors();
        for (int i = 0; i <  ci.getSettingWidth(); i++){
            secretCodeButtons[i].setBackground(
                    new java.awt.Color(secret[i].getRGB()));
            secretCodeButtons[i].setText(secret[i].getNum());
            //playSound("audio/phrases/" + (secret[i].getNum()) + ".wav");
        }
    }

    /*
     * Show the guess result of a Row.
     * Gets the guess result from the game engine.
     * row - the row in which the result is displayed.
     */
    private void showResultRow(int row) {
        // playSound("resources/ClickOn.wav");
        Color[] result = ci.getResultRow(row).getColors();
        for (int i = 0; i < ci.getSettingWidth(); i++) {
            JButton button = (JButton) gameButtons[row][i];
            java.awt.Color c = new java.awt.Color(result[i].getRGB());
            if (c.getRGB() == Color.Null.getRGB()) {
                // Use color "null" as the default color
                c = null;
            }
            button.setBackground(c);
        }
    }

    /*
     * Show the guess row.
     * Function is used to display a loaded or AI game.
     * Gets the guess row from the game engine.
     * row - the row in which the guess is displayed.
     */
    private void showGameRow(int row) {
        int width = ci.getSettingWidth();
        Color[] colors = ci.getGameFieldRow(row).getColors();
        for (int i = width; i < width * 2; i++) {
            gameButtons[row][i].setBackground(
                    new java.awt.Color(colors[i-width].getRGB()));
            gameButtons[row][i].setText(colors[i-width].getNum());
        }
    }

    /*
     * Parse the return state of the ControlInterface.turn() function.
     * for state == 1 show "you won" message;
     * for state == -1 show "you lose" message. Otherwise add a new Row.
     *
     * state - a state integer like the ControlInterface.turn() function returns it.
     * see game.ControlInterface
     * see addRow()
     */
    private void parseGameState(int state) {
        if (state == 1) {
            timer.stop();
            revealSecretCode();
            //playSound("resources/Won.wav");
            playSound("audio/phrases/won.wav");
            this.setEnabled(false);
            if (ci.getSettingAiMode() == true) {
                gameState.setText("AI: I got the code!");
                JOptionPane.showMessageDialog(
                        null, "AI: I got the code!", "Info:",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                gameState.setText("You won!");
                ImageIcon in = new ImageIcon("resources/In.png");
                JOptionPane.showMessageDialog(null, null, "You won!",
                        JOptionPane.INFORMATION_MESSAGE, in);
                db.incrementGuessScore(login.getName());
                //System.out.println(db.getGuessScore(login.getName()));
            }
            this.setEnabled(true);
        } else if (state == -1) {
            timer.stop();
            revealSecretCode();
            playSound("resources/Lost.wav");
            this.setEnabled(false);
            if (ci.getSettingAiMode() == true) {
                gameState.setText("AI: I couldn't crack the code!");
                JOptionPane.showMessageDialog(
                        null, "AI: I couldn't crack the code!",
                        "Info:", JOptionPane.INFORMATION_MESSAGE);
            } else {
                gameState.setText("You lost!");
                ImageIcon out = new ImageIcon("resources/Out.png");
                JOptionPane.showMessageDialog(null, null, "You lost!",
                        JOptionPane.INFORMATION_MESSAGE, out);
            }
            this.setEnabled(true);
        } else {
            // Only add a new row if the game continues
            addRow();
        }
    }

    /*
     * Add a new Row to the game table.
     */
    private void addRow() {
        int width = ci.getSettingWidth();
        int i;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel label;

        // Add a new row
        JButton[][] temp = new JButton[gameButtons.length + 1][width * 2];
        // Get reference from old game table
        System.arraycopy(gameButtons, 0, temp, 0, gameButtons.length);
        i = gameButtons.length;
        gameButtons = temp;

        label = new JLabel("" + (i + 1), SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = i + 1;
        gbc.weightx = 0.05;
        gamePanel.add(label, gbc);

        // Results
        gbc.weightx = 0.1;
        for (int j = 0; j < width; j++) {
            gameButtons[i][j] = new JButton();
            gameButtons[i][j].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            gameButtons[i][j].setPreferredSize(new Dimension(20, 28));
            gameButtons[i][j].addActionListener(
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            gameButtonResultActionPerformed(evt);
                        }
                    });
            gbc.gridx = j + 1; // +1 because of guess nr. label
            gbc.gridy = i + 1; // +1 because of key usage label
            gamePanel.add(gameButtons[i][j], gbc);
        }
        // Guesses
        gbc.weightx = 0.9;
        for (int j = width; j < width * 2; j++) {
            gameButtons[i][j] = new JButton();
            /*
             * Add ToolTipText.
             * This tag is used to find the buttons position in the array.
             * By this effective way the button knows its own position,
             * without crawling through the button array.
             */
            gameButtons[i][j].setToolTipText(
                    "Button in row: " + (i + 1) +
                            ", column: " + ((j - width) + 1));
            gameButtons[i][j].getInputMap().put(
                    KeyStroke.getKeyStroke("SPACE"), "none" );
            gameButtons[i][j].setPreferredSize(new Dimension(35, 28));
            gameButtons[i][j].addActionListener(
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            gameButtonPinActionPerformed(evt);
                        }
                    });
            gbc.gridx = j + 1;
            gbc.gridy = i + 1;
            gamePanel.add(gameButtons[i][j], gbc);
        }

        // Placeholder dummy
        gbc.gridy = i + 2;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gamePanel.remove(gamePlaceholder);
        gamePanel.add(gamePlaceholder, gbc);
        gamePanel.revalidate();
    }

    /*
     * Parse the typed key.
     * Choose color for keys: 1, 2, 3, etc.
     * Place color for keys: a, b, c, etc.
     * Check guess result for key: space bar.
     *
     * k - the key event that should be parsed.
     */
    private void keyTyped(KeyEvent k) {

        if (ci.getGameEnded() == false && this.isFocused()) {
            int key = (int)k.getKeyChar();
            // If it is a "choose number" key (1, 2, 3, ...)
            if (key-48 >= 0 && key-48 < ci.getSettingColQuant()) {
                colButtons[key-48].doClick();
                playSound("audio/numbers/" + (colButtons[key-48].getText()) + ".wav");
            }
            // If it is a "place number" key (a, b, c, ...)
            else if (key-97 >= 0 && key-97 < ci.getSettingWidth()) {
                if (ci.getSettingAiMode() == false) {
                    gameButtons[ci.getActiveRowNumber()]
                            [(key - 97) + ci.getSettingWidth()].doClick();
                } else {
                    secretCodeButtons[key - 48].doClick();
                    playSound("audio/numbers/" + (colButtons[key-48].getText()) + ".wav");
                }
            }
            // If it is the check result key (SPACE)
            else if (key == 32) {
                if (ci.getSettingAiMode() == false) {
                    gameButtons[ci.getActiveRowNumber()]
                            [0].doClick();
                }
            }
        }
    }

    /*
     * Run an AI game until the game is solved.
     * While the AI is guessing the GUI is locked.
     * Every "AI_GUESS_DELAY"ms the timer makes a guess. This provides the GUI
     * enough time between two guesses to repaint.
     * In most cases the AI can guess very fast. Only if the game width is
     * large and the color quantity is high it will take longer in later guesses.
     * If a guess creation takes more than "AI_GUESS_DELAY"ms the timer
     * waits until the guess is computed.
     *
     * see #AI_GUESS_DELAY
     */
    private void doAIGame() {
        final GeneticSolver gs = new GeneticSolver(ci);
        gs.initResults();
        gameState.setText("AI is guessing. Please wait...");
        setEnabled(false);

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Do a guess every AI_GUESS_DELAY ms.
                if (ci.getGameEnded() == false) {
                    int state = gs.makeGuess();
                    showGameRow(ci.getActiveRowNumber()-1);
                    showResultRow(ci.getActiveRowNumber()-1);
                    parseGameState(state);
                } else {
                    aiTimer.stop();
                    setEnabled(true);
                }
            }
        };

        aiTimer = new Timer(AI_GUESS_DELAY, taskPerformer);
        aiTimer.start();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Menu actions (by click)">

    /*
     * Quit the whole game.
     * evt - the triggered event. Not used.
     */
    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    /*
     * Initialize a new game.
     * evt - the triggered event. Not used.
     */
    private void newGameMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        ci.newGame();
        initNewGame();
    }


    private void pauseTimerActionPerformed(
            java.awt.event.ActionEvent evt, Timer timer) {
        playSound("resources/ClickOn.wav");
        timer.stop();
    }


    /*
     * Fill out the current game table Row with a guess.
     * The logic for a valid guess is managed in the AI.
     *
     * evt - the triggered event. Not used.
     * see ai.GeneticSolver
     */
    private void showHintMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        playSound("resources/ClickOn.wav");
        if (ci.getGameEnded() == false && ci.getSettingAiMode() == false) {
            int i = ci.getSettingWidth();
            GeneticSolver hint = new GeneticSolver(ci);
            hint.initResults();
            Row r = hint.generateGuess();
            for (Color color : r.getColors()) {
                gameButtons[ci.getActiveRowNumber()]
                        [i].setBackground(new java.awt.Color(color.getRGB()));
                gameButtons[ci.getActiveRowNumber()]
                        [i].setText(color.getNum());
                i++;
            }
        }
    }

    /*
     * Checks if the guess is valid or makes no sense in context of previous guesses and results.
     *
     * evt - the triggered event. Not used.
     * see ai.Clues
     */
    private void validateGuessMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        playSound("resources/ClickOn.wav");
        if (ci.getGameEnded() == false && ci.getSettingAiMode() == false) {
            if (rowIsSet() == true) {
                Color[] c = new Color[ci.getSettingWidth()];
                for (int i = 0; i < c.length; i++) {
                    c[i] = translateColor(gameButtons[ci.getActiveRowNumber()]
                                    [ci.getSettingWidth() + i].getBackground());
                }
                Row r = new Row(c);
                if (Clues.isFeasible(ci, r) == true) {
                    JOptionPane.showMessageDialog(null, "Good guess!", "Info:",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(
                            null, "Not a good idea...", "Info:",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Row is not set", "Info:",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /*
     * Set the last guess in the active row.
     * This feature becomes handy if you want to change only a few colors in your next guess.
     *
     * evt - the triggered event. Not used.
     */
    private void setLastGuessMenuItemMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        playSound("resources/ClickOn.wav");
        int i = ci.getSettingWidth();
        if (ci.getGameEnded() == false && ci.getSettingAiMode() == false
                && ci.getActiveRowNumber() > 0) {
            for (Color c : ci.getGameFieldRow(
                    ci.getActiveRowNumber()-1).getColors()) {
                gameButtons[ci.getActiveRowNumber()][i].setBackground(new java.awt.Color(c.getRGB()));
                gameButtons[ci.getActiveRowNumber()][i].setText(c.getNum());
                i++;
            }
        }
    }

    /*
     * Show the how-to.pdf in the system std. viewer.
     * The how-to.pdf will be exported to the systems std. temp. directory.
     * Then the file will be opened (if supported) with the system std. PDF viewer.
     *
     * evt - the triggered event. Not used.
     * see java.awt.Desktop
     */
    private void howToMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {
        String tempPath = System.getProperty("java.io.tmpdir") + "/how-to.pdf";
        try {
            // Export the PDF from the jar to std. temp folder.
            InputStream in = MainWindow.class.getResourceAsStream(
                    "/gui/how-to.pdf");
            File f = new File(tempPath);
            OutputStream out = new FileOutputStream(f);
            byte buf[] = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf,0,len);
            }
            out.close();
            in.close();

            // Open PDF if export went well.
            if (f.exists() == true) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(f);
                } else {
                    Debug.errorPrint("Awt Desktop is not supported!");
                    JOptionPane.showMessageDialog(null,
                            "Awt Desktop is not supported!", "Error:",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Debug.errorPrint(tempPath + " doesn't exist!");
                JOptionPane.showMessageDialog(null,
                        tempPath + " doesn't exist!", "Error:",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            Debug.errorPrint(ex.toString());
            JOptionPane.showMessageDialog(null,
                    "Couldn't open " + tempPath, "Error:",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Button actions (by click)">

    /*
     * Set the chosen color.
     * evt - the triggered event.
     * The source object color will be used to set the chosen color.
     */
    private void numButtonsActionPerformed(java.awt.event.ActionEvent evt) {
        playSound("resources/ClickPick.wav");
        JButton button = (JButton) evt.getSource();
        chosenColButton.setBackground(button.getBackground());
        chosenColButton.setText(button.getText());
    }

    /*
     * Set the secret code and colors
     * evt - the triggered event.
     * The source object color will be used to set the secret code color.
     */
    private void secretCodeButtonsActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getSettingAiMode() && ci.getGameEnded() == false) {
            if (ci.getSettingDoubleCols() == false) {
                // Check for double colors
                for (int i = 0; i < secretCodeButtons.length; i++) {
                    java.awt.Color c = secretCodeButtons[i].getBackground();
                    if (c.getRGB() != Color.Null.getRGB() &&
                            c.getRGB() == chosenColButton.getBackground().getRGB()) {
                        gameState.setText("Double numbers are not allowed.");
                        return;
                    }
                }
            }
            // Set numbers and colors
            JButton button = (JButton) evt.getSource();
            button.setBackground(chosenColButton.getBackground());
            button.setText(chosenColButton.getText());
            gameState.setText("Set the secret code and watch the AI.");
            // Check if secret code is set
            for (int i = 0; i < secretCodeButtons.length; i++) {
                if (secretCodeButtons[i].getBackground().getRGB()
                        == Color.Null.getRGB()) {
                    return;
                }
            }
            // Secret code is set. Start AI game
            writeSecretCode();
            doAIGame();
        } else {
            // Set numbers and colors
            JButton button = (JButton) evt.getSource();
            button.setBackground(chosenColButton.getBackground());
            button.setText(chosenColButton.getText());
            gameState.setText("Set the secret code.");
            // Check if secret code is set
            for (int i = 0; i < secretCodeButtons.length; i++) {
                if (secretCodeButtons[i].getBackground().getRGB()
                        == Color.Null.getRGB()) {
                    return;
                }
            }
            // Secret code is set. Start the game
            writeSecretCode();
            initGameTable();
            initColorTable();
            restartTimer();
        }
    }

    /*
     * Check the active game Row and display the result.
     * evt - the triggered event. Not used.
     * @see #writeToGameField()
     * @see #parseGameState(int)
     * @see #showResultRow(int)
     */
    private void gameButtonResultActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getSettingAiMode() == false &&
                ci.getGameEnded() == false &&
                rowIsSet() == true) {
            // Check for guess result
            // Write the numbers(form the GUI) to the real game
            writeToGameField();
            // Check numbers
            parseGameState(ci.turn());
            // Show the guess result in the GUI
            showResultRow(ci.getActiveRowNumber() - 1);
            if (ci.getSettingAiMode() == false) {
                gameState.setText("You have " + (ci.getSettingMaxTries() -
                        ci.getActiveRowNumber()) + " attempts left! Choose a number and place it.");
            }
        }
    }

    /*
     * Place the chosen color to the game table pin.
     * evt - the triggered event.
     * The source object color will be set to the chosen color.
     */
    private void gameButtonPinActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (ci.getSettingAiMode() == false) {
            JButton button = (JButton) evt.getSource();
            // Get buttons array position by parsing the ToolTipText
            int row = Integer.parseInt(button.getToolTipText().substring(
                    button.getToolTipText().indexOf(": ") + 2,
                    button.getToolTipText().indexOf(","))) - 1;
            int col = Integer.parseInt(button.getToolTipText().substring(
                    button.getToolTipText().lastIndexOf(": ")+2)) - 1;
            int width = ci.getSettingWidth();

            if (row == ci.getActiveRowNumber()) {
                if (ci.getSettingDoubleCols() == false) {
                    // Check for double colors
                    for (int i = width; i < width * 2; i++) {
                        java.awt.Color c = gameButtons[row][i].getBackground();
                        if (c.getRGB() != Color.Null.getRGB() && i != col + width &&
                                c.getRGB() ==
                                        chosenColButton.getBackground().getRGB()) {
                            gameState.setText(
                                    "Double numbers are not allowed.");
                            return;
                        }
                    }
                }
                playSound("resources/ClickPin.wav");
                button.setBackground(chosenColButton.getBackground());
                button.setText(chosenColButton.getText());
                if (ci.getGameEnded() == false &&
                        rowIsSet() == true) {
                    gameState.setText(
                            "Click on one result button (or press SPACE) " +
                                    "to check your guess.");
                } else {
                    gameState.setText(
                            "Choose a color (Click or key 1, 2, 3,..) " +
                                    "and place it (Click or key a, b, c,...).");
                }
            }
        }
    }

    // </editor-fold>


    /*
     * Change to "Nimbus" Look & Feel(if installed)
     * and initialize the main window with a new standard game
     */
    public static void main(String args[]) {

        try {
            // Set the look of the main window
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            Debug.errorPrint("Nimbus Look & Feel not found. Fallback.");
        }

        // Create and display the login form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new Login().setVisible(true);

                /*
                System.out.println("Generating & deleting audio files...");

                AudioFileBuilder audioFileBuilder = new AudioFileBuilder();
                audioFileBuilder.createPhraseAudioFiles();
                audioFileBuilder.deleteOldPhraseAudioFiles();

                System.out.println("Completed audio file generation & deletion!");
                 */
            }
        });
    }
}
