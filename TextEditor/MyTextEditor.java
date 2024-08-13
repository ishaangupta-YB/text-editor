package TextEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.print.PrinterException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.io.*;
import java.awt.print.PrinterJob;

public class MyTextEditor extends JFrame {

      private JTextPane textPane;
      private JLabel statusLabel,statsLabel;
      private UndoManager undoManager;
      private JTextField findField,replaceField;
      private int findIndex = 0;
      private JComboBox<String> fontComboBox;
      private JSpinner fontSizeSpinner;
      private JCheckBox boldCheckBox,italicCheckBox,lineNumbersCheckBox;
      private Color textColor;
      private JTextArea lineNumbersArea;
      private File currentFile;
      public MyTextEditor() {
            initializeUI();
      }
      private void initializeUI() {
            setLayout(new BorderLayout());
            setTitle("Text Editor");
            setSize(900, 650);
            setMinimumSize(new Dimension(850, 650));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            textColor = Color.BLACK;

            JPanel  createControlPanel=createControlPanel();
            add(createControlPanel, BorderLayout.NORTH);
            textPane = new JTextPane();
            textPane.setPreferredSize(new Dimension(400, 300));
            textPane.addCaretListener(new CaretListener() {
                  @Override
                  public void caretUpdate(CaretEvent e) {
                        updateFontPanelFromSelection();
                  }
            });
            JScrollPane scrollPane = new JScrollPane(textPane);
            add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());

            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            statsLabel = new JLabel("Words: 0  |  Characters: 0  |  Lines: 0");
            statsPanel.add(statsLabel);
            bottomPanel.add(statsPanel,BorderLayout.CENTER);
            JPanel statusPanel = statusPanelUI();          bottomPanel.add(statusPanel,BorderLayout.WEST);
            JPanel gotoLinePanelUI = gotoLinePanelUI();
            bottomPanel.add(gotoLinePanelUI,BorderLayout.EAST);
            add(bottomPanel, BorderLayout.SOUTH);

            JPanel lineNumbersPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            lineNumbersArea = new JTextArea(10, 2);
            lineNumbersArea.setEditable(false);
            lineNumbersArea.setBackground(Color.LIGHT_GRAY);
            lineNumbersPanel.add(new JScrollPane(lineNumbersArea));
            add(lineNumbersPanel, BorderLayout.WEST);


            undoManager = new UndoManager();
            textPane.getDocument().addUndoableEditListener(new UndoableEditListener() {
                  @Override
                  public void undoableEditHappened(UndoableEditEvent e) {
                        undoManager.addEdit(e.getEdit());
                  }
            });

            textPane.getDocument().addDocumentListener(new DocumentListener() {
                  @Override
                  public void insertUpdate(DocumentEvent e) {
                        updateStats();
                        findText(findField.getText());
                        updateLineNumbers();
                  }

                  @Override
                  public void removeUpdate(DocumentEvent e) {
                        updateStats();
                        findText(findField.getText());
                        updateLineNumbers();
                  }

                  @Override
                  public void changedUpdate(DocumentEvent e) {
                  }
            });

            JMenuBar menuBar = new JMenuBar();
            setJMenuBar(menuBar);

            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu toolsMenu = new JMenu("Tools");

            menuBar.add(fileMenu);
            menuBar.add(editMenu);
            menuBar.add(toolsMenu);

            JMenuItem undoItem = new JMenuItem("Undo");
            JMenuItem redoItem = new JMenuItem("Redo");

            editMenu.add(undoItem);
            editMenu.add(redoItem);

            JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
            JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_O);
            JMenuItem saveItem = new JMenuItem("Save",KeyEvent.VK_S);
            JMenuItem saveAsMenuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
            JMenuItem exitItem = new JMenuItem("Exit");

            JMenuItem countOccurrencesMenuItem = new JMenuItem("Count Occurrences");
            toolsMenu.add(countOccurrencesMenuItem);

            JMenuItem searchOnlineMenuItem = new JMenuItem("Search Online");
            toolsMenu.add(searchOnlineMenuItem);

            JMenuItem toUppercaseMenuItem = new JMenuItem("Convert to Uppercase" );
            toolsMenu.add(toUppercaseMenuItem);
            JMenuItem toLowercaseMenuItem = new JMenuItem("Convert to Lowercase");
            toolsMenu.add(toLowercaseMenuItem);

            JMenu alignmentMenu = new JMenu("Change Text Alignment");
            JMenuItem leftAlignMenuItem = new JMenuItem("Left Align");
            JMenuItem centerAlignMenuItem = new JMenuItem("Center Align");
            JMenuItem rightAlignMenuItem = new JMenuItem("Right Align");

            toolsMenu.add(alignmentMenu);
            alignmentMenu.add(centerAlignMenuItem);
            alignmentMenu.add(leftAlignMenuItem);
            alignmentMenu.add(rightAlignMenuItem);

            JMenuItem printMenuItem = new JMenuItem("Print");
            toolsMenu.add(printMenuItem);

            fileMenu.add(newMenuItem);
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(saveAsMenuItem);
            fileMenu.addSeparator();
            fileMenu.add(exitItem);

            leftAlignMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        changeTextAlignment(StyleConstants.ALIGN_LEFT);
                  }
            });

            centerAlignMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        changeTextAlignment(StyleConstants.ALIGN_CENTER);
                  }
            });

            rightAlignMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        changeTextAlignment(StyleConstants.ALIGN_RIGHT);
                  }
            });


            printMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        printDocument();
                  }
            });

            toUppercaseMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        convertCase(true);
                  }
            });

            toLowercaseMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        convertCase(false);
                  }
            });


            searchOnlineMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        searchOnline();
                  }
            });

            countOccurrencesMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        countOccurrences();
                  }
            });

            newMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        newFile();
                  }
            });

            openItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        openFile();
                  }
            });

            saveItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        saveFile();
                  }
            });

            saveAsMenuItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        saveFileAs();
                  }
            });

            exitItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                  }
            });

            undoItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        undo();
                  }
            });

            redoItem.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        redo();
                  }
            });

            undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
            redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

            textPane.addMouseListener(new MouseAdapter() {
                  @Override
                  public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger())   createContextMenu().show(e.getComponent(), e.getX(), e.getY());
                  }
                  @Override
                  public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger())  createContextMenu().show(e.getComponent(), e.getX(), e.getY());
                  }
            });
      }
      private JPanel statusPanelUI(){
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusLabel = new JLabel("Status: Ready");
            panel.add(statusLabel);
            return panel;
      }

      private JPanel gotoLinePanelUI(){
            JPanel goToLinePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JLabel lineLabel = new JLabel("Go to Line:");
            JTextField lineField = new JTextField(5);
            JButton goToLineButton = new JButton("Go");

            goToLinePanel.add(lineLabel);
            goToLinePanel.add(lineField);
            goToLinePanel.add(goToLineButton);

            goToLineButton.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        goToLine(lineField.getText());
                  }
            });

            return goToLinePanel;
      }

      private JPanel findReplacePanelUI(){
            JPanel findReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JLabel findLabel = new JLabel("Find:");
            findField = new JTextField(15);
            JButton findButton = new JButton("Find");

            JLabel replaceLabel = new JLabel("Replace:");
            replaceField = new JTextField(15);
            JButton replaceButton = new JButton("Replace");

            findButton.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        findText(findField.getText());
                  }
            });

            replaceButton.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        replaceText(findField.getText(), replaceField.getText());
                  }
            });

            findReplacePanel.add(findLabel);
            findReplacePanel.add(findField);
            findReplacePanel.add(findButton);
            findReplacePanel.add(replaceLabel);
            findReplacePanel.add(replaceField);
            findReplacePanel.add(replaceButton);
            return findReplacePanel;
      }

      private JPanel createControlPanel() {
            JPanel controlPanel = new JPanel(new BorderLayout());

            JPanel mainPanel = new JPanel(new BorderLayout());

            JPanel fontPanel = createFontPanel();
            JPanel createColorPanel = createColorPanel();
            mainPanel.add(fontPanel,BorderLayout.EAST);
            mainPanel.add(createColorPanel,BorderLayout.WEST);

            controlPanel.add(mainPanel, BorderLayout.NORTH);

            JPanel mainPanel2 = new JPanel(new BorderLayout());

            lineNumbersCheckBox = new JCheckBox("Show Line Numbers");
            lineNumbersCheckBox.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        toggleLineNumbers(lineNumbersCheckBox.isSelected());
                  }
            });
            mainPanel2.add(lineNumbersCheckBox,BorderLayout.WEST);
            JPanel findReplacePanel = findReplacePanelUI();
            mainPanel2.add(findReplacePanel, BorderLayout.EAST);

            controlPanel.add(mainPanel2, BorderLayout.SOUTH);

            return controlPanel;
      }

      private void toggleLineNumbers(boolean showLineNumbers) {
            lineNumbersArea.setVisible(showLineNumbers);
            updateLineNumbers();
      }

      private void updateLineNumbers() {
            if (lineNumbersCheckBox.isSelected()) {
                  Element root = textPane.getDocument().getDefaultRootElement();
                  int totalLines =  root.getElementCount();
                  int leadingSpaces = Math.max(String.valueOf(totalLines).length(), 2);

                  StringBuilder lineNumbers = new StringBuilder();
                  for (int i = 1; i <= totalLines; i++)   lineNumbers.append(String.format("%" + leadingSpaces + "d\n", i));

                  lineNumbersArea.setText(lineNumbers.toString());
            }
      }
      private void updateStats() {
            String content = textPane.getText();
            int wordCount = countWords(content);
            int charCount = content.length();
            Element root = textPane.getDocument().getDefaultRootElement();
            int lineCount =  root.getElementCount();

            statsLabel.setText("Words: " + wordCount + "  |  Characters: " + charCount + "  |  Lines: " + lineCount);
      }

      private int countWords(String text) {
            StringTokenizer tokenizer = new StringTokenizer(text);
            return tokenizer.countTokens();
      }
      private JPanel createColorPanel(){
            JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel colorLabel = new JLabel("Text Color:");
            JButton chooseColorButton = new JButton("Choose Color");

            colorPanel.add(colorLabel);
            colorPanel.add(chooseColorButton);

            chooseColorButton.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        Color chosenColor = JColorChooser.showDialog(null, "Choose Color", textColor);
                        if (chosenColor != null) textColor = chosenColor;
                        chooseColor(textColor);
                  }
            });
            return colorPanel;
      }
      private void chooseColor(Color color) {
            StyledDocument doc = textPane.getStyledDocument();
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();

            if (start != end) {
                  SimpleAttributeSet attributes = new SimpleAttributeSet();
                  StyleConstants.setForeground(attributes, color);
                  doc.setCharacterAttributes(start, end - start, attributes, false);
            }
            applyColorToNewText(color);
            setStatus("Text color changed");
      }

      private void applyColorToNewText(Color color) {
            StyledDocument doc = textPane.getStyledDocument();
            int caretPosition = textPane.getCaretPosition();
            System.out.println("heeree1");

            if (textPane.getSelectionStart() != textPane.getSelectionEnd())   return;
            System.out.println("heeree2");
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(attributes, color);
            doc.setCharacterAttributes(caretPosition, 1, attributes, false);
      }

      private JPanel createFontPanel() {
            JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            fontComboBox = new JComboBox<>(fontNames);
            fontComboBox.addActionListener(new FontChangeListener());

            SpinnerModel spinnerModel = new SpinnerNumberModel(12, 6, 72, 1);
            fontSizeSpinner = new JSpinner(spinnerModel);
            fontSizeSpinner.addChangeListener(new FontChangeListener());

            boldCheckBox = new JCheckBox("Bold");
            boldCheckBox.addActionListener(new FontChangeListener());

            italicCheckBox = new JCheckBox("Italic");
            italicCheckBox.addActionListener(new FontChangeListener());

            fontPanel.add(new JLabel("Font:"));
            fontPanel.add(fontComboBox);
            fontPanel.add(new JLabel("Size:"));
            fontPanel.add(fontSizeSpinner);
            fontPanel.add(boldCheckBox);
            fontPanel.add(italicCheckBox);

            return fontPanel;
      }

      private class FontChangeListener implements ActionListener, javax.swing.event.ChangeListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                  updateFont();
            }

            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                  updateFont();
            }
      }

      private void updateFont() {
            Font selectedFont = createSelectedFont();
            setFontForSelection(selectedFont);
      }
//
      private Font createSelectedFont() {
            String selectedFontFamily = (String) fontComboBox.getSelectedItem();
            int selectedFontSize = (int) fontSizeSpinner.getValue();
            int fontStyle = Font.PLAIN;

            if (boldCheckBox.isSelected()) {
                  fontStyle |= Font.BOLD;
            }

            if (italicCheckBox.isSelected()) {
                  fontStyle |= Font.ITALIC;
            }

            return new Font(selectedFontFamily, fontStyle, selectedFontSize);
      }
      private void setFontForSelection(Font font) {
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attributes, font.getFamily());
            StyleConstants.setFontSize(attributes, font.getSize());
            StyleConstants.setBold(attributes, (font.getStyle() & Font.BOLD) != 0);
            StyleConstants.setItalic(attributes, (font.getStyle() & Font.ITALIC) != 0);

            textPane.setCharacterAttributes(attributes, false);
      }

      private void updateFontPanelFromSelection() {
            int selectionStart = textPane.getSelectionStart();
            int selectionEnd = textPane.getSelectionEnd();

            if (selectionStart != selectionEnd) {
                  Font selectedFont = createSelectedFont();
                  setFontForSelection(selectedFont);
            }
      }

      private void findText(String searchText) {
            if (searchText.isEmpty()) {
                  setStatus("Enter text to find");
                  return;
            }
            String content = textPane.getText();
            int index = content.indexOf(searchText, findIndex);

            if (index != -1) {
                  textPane.select(index, index + searchText.length());
                  findIndex = index + 1;
                  setStatus("Text found");
            } else {
                  findIndex = 0;
                  setStatus("Text not found");
            }
      }

      private void replaceText(String searchText, String replacement) {
            if (searchText.isEmpty()) {
                  setStatus("Enter text to find");
                  return;
            }

            StyledDocument doc = textPane.getStyledDocument();
            String content;
            try {
                  content = doc.getText(0, doc.getLength());
            } catch (BadLocationException e) {
                  e.printStackTrace();
                  setStatus("Error reading text");
                  return;
            }

            int index = content.indexOf(searchText);

            if (index != -1) {
                  try {
                        doc.remove(index, searchText.length());
                        doc.insertString(index, replacement, null);
                        setStatus("Text replaced");
                  } catch (BadLocationException e) {
                        e.printStackTrace();
                        setStatus("Error replacing text");
                  }
            } else {
                  setStatus("Text not found for replacement");
            }
      }

      private void goToLine(String lineNumber) {
            if (lineNumber.isEmpty()) {
                  setStatus("Enter line number");
                  return;
            }

            try {
                  int line = Integer.parseInt(lineNumber);
                  StyledDocument doc = textPane.getStyledDocument();

                  if (line >= 1 && line <= doc.getDefaultRootElement().getElementCount()) {
                        Element lineElement = doc.getDefaultRootElement().getElement(line - 1);

                        int startOffset = lineElement.getStartOffset();
                        int endOffset = lineElement.getEndOffset();

                        textPane.setCaretPosition(startOffset);
                        textPane.setSelectionStart(startOffset);
                        textPane.setSelectionEnd(endOffset);

                        setStatus("Moved to line " + lineNumber);
                  } else {
                        setStatus("Invalid line number");
                  }
            } catch (NumberFormatException e) {
                  setStatus("Invalid line number");
            }
      }

      private JPopupMenu createContextMenu() {
            JPopupMenu contextMenu = new JPopupMenu();
            JMenuItem cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
            JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
            JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
            contextMenu.add(cutItem);
            contextMenu.add(copyItem);
            contextMenu.add(pasteItem);
            return contextMenu;
      }

      private void newFile() {
            textPane.setText("");
            currentFile = null;
            setStatus("New file created.");
      }

      private void openFile() {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                  File file = fileChooser.getSelectedFile();
                  try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                              content.append(line).append("\n");
                        }
                        textPane.setText(content.toString());
                        currentFile = file;
                        setStatus("File opened: " + file.getAbsolutePath());
                  } catch (IOException e) {
                        e.printStackTrace();
                        setStatus("Error opening the file");
                  }
            }
      }

      private void saveFile() {
            if (currentFile != null) {
                  saveToFile(currentFile);
            } else {
                  saveFileAs();
            }
      }

      private void saveFileAs() {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                  File selectedFile = fileChooser.getSelectedFile();
                  saveToFile(selectedFile);
            }
      }

      private void saveToFile(File file) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                  writer.write(textPane.getText());
                  setStatus("File saved: " + file.getAbsolutePath());
                  currentFile = file;
            } catch (IOException e) {
                  setStatus("Error saving file.");
                  e.printStackTrace();
            }
      }

      private void searchOnline() {
            String selectedText = textPane.getSelectedText();

            if (selectedText != null && !selectedText.isEmpty()) {
                  String searchURL = "https://www.google.com/search?q=" + selectedText;
                  openWebPage(searchURL);
                  setStatus("Searching online for: " + selectedText);
            } else {
                  setStatus("No text selected for online search.");
            }
      }

      private void openWebPage(String url) {
            try {
                  Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                  setStatus("Error opening web page.");
                  e.printStackTrace();
            }
      }

      private void countOccurrences() {
            String targetWord = JOptionPane.showInputDialog(this, "Enter word to count occurrences:");
            if (targetWord != null && !targetWord.isEmpty()) {
                  String documentText = textPane.getText();
                  int occurrences = countWordOccurrences(documentText, targetWord);
                  setStatus("Occurrences of '" + targetWord + "': " + occurrences);
            }
      }

      private int countWordOccurrences(String text, String targetWord) {
            String[] words = text.split("\\s+");
            int count = 0;
            for (String word : words) {
                  if (word.equals(targetWord)) {
                        count++;
                  }
            }
            return count;
      }

      private void convertCase(boolean toUppercase) {
            int selectionStart = textPane.getSelectionStart();
            int selectionEnd = textPane.getSelectionEnd();

            if (selectionStart != selectionEnd) {
                  String selectedText = textPane.getSelectedText();
                  String newText = toUppercase ? selectedText.toUpperCase() : selectedText.toLowerCase();
                  textPane.replaceSelection(newText);
                  setStatus("Text converted to " + (toUppercase ? "uppercase." : "lowercase."));
            }
      }

      private void changeTextAlignment(int alignment) {
            int selectionStart = textPane.getSelectionStart();
            int selectionEnd = textPane.getSelectionEnd();

            if (selectionStart != selectionEnd) {
                  StyledDocument doc = textPane.getStyledDocument();
                  SimpleAttributeSet attributes = new SimpleAttributeSet();
                  StyleConstants.setAlignment(attributes, alignment);
                  doc.setParagraphAttributes(selectionStart, selectionEnd - selectionStart, attributes, false);
                  setStatus("Text alignment changed.");
            }
      }
      private void printDocument() {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new TextPanePrintable(textPane));

            if (job.printDialog()) {
                  try {
                        job.print();
                  } catch (PrinterException e) {
                        e.printStackTrace();
                  }
            }
      }
      private void undo() {
            if (undoManager.canUndo()) {
                  undoManager.undo();
                  setStatus("Undo performed");
            } else {
                  setStatus("Nothing to undo");
            }
      }

      private void redo() {
            if (undoManager.canRedo()) {
                  undoManager.redo();
                  setStatus("Redo performed");
            } else {
                  setStatus("Nothing to redo");
            }
      }

      private void setStatus(String message) {
            System.out.println("Status: " + message);
            statusLabel.setText("Status: " + message);
      }

      public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                        new MyTextEditor().setVisible(true);
                  }
            });
      }

      private static class TextPanePrintable implements Printable {
            private JTextPane textPane;

            public TextPanePrintable(JTextPane textPane) {
                  this.textPane = textPane;
            }

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                  if (pageIndex > 0) {
                        return Printable.NO_SUCH_PAGE;
                  }
                  Graphics2D g2d = (Graphics2D) graphics;
                  g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                  double scaleX = pageFormat.getImageableWidth() / textPane.getWidth();
                  double scaleY = pageFormat.getImageableHeight() / textPane.getHeight();
                  double scale = Math.min(scaleX, scaleY);

                  g2d.scale(scale, scale);
                  textPane.setDoubleBuffered(false);
                  textPane.printAll(graphics);
                  textPane.setDoubleBuffered(true);
                  return Printable.PAGE_EXISTS;
            }
      }
}