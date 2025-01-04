import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class GUI extends JFrame implements ActionListener, KeyListener {

    private static JPanel[][] panels;
    private static JToggleButton[][] buttons;

    public GUI() {
        this.setTitle("Sudoku");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setLayout(new GridLayout(3, 3, 20, 20));
        addPanelAttributes();
        addButtonsToPanel();
        this.addKeyListener(this);
        this.setVisible(true);
    }

    //Generates panel for the frame
    private void addPanelAttributes() {
        panels = new JPanel[3][3];
        for (int column = 0; column < SudokuManager.getHeight() / 3; column++) {
            for (int row = 0; row < SudokuManager.getWidth() / 3; row++) {
                panels[column][row] = new JPanel();
                panels[column][row].setSize(60, 60);
                panels[column][row].setLayout(new GridLayout(3, 3, 5, 5));
                this.add(panels[column][row]);
            }
        }
    }

    //Generates buttons for the panel
    private void addButtonsToPanel() {
        buttons =
            new JToggleButton[SudokuManager.getHeight()][SudokuManager.getWidth()];
        int panelColumn = 0, panelRow = 0;
        for (int column = 0; column < SudokuManager.getHeight(); column++) {
            for (int row = 0; row < SudokuManager.getWidth(); row++) {
                int sudokuNumber = SudokuManager.getSudokuValue(column, row);
                buttons[column][row] = new JToggleButton();
                if (sudokuNumber == 0) {
                    buttons[column][row].setText(" ");
                    buttons[column][row].setBackground(Color.WHITE);
                } else {
                    buttons[column][row].setText(
                            Integer.toString(sudokuNumber)
                        );
                    buttons[column][row].setBackground(Color.GRAY);
                }

                buttons[column][row].setFocusable(false);
                buttons[column][row].addActionListener(this);
                buttons[column][row].setSelected(false);

                if (row == 3 || row == 6) panelRow++;
                if (row == 0) panelRow = 0;
                panels[panelColumn][panelRow].add(buttons[column][row]);
            }
            if (column == 2 || column == 5) panelColumn++;
        }
    }

    //Disables all buttons in the grid except the button that is passed in to the method
    private static void disableAllButtonsExcept(
        final JToggleButton selectedButton
    ) {
        for (int column = 0; column < SudokuManager.getHeight(); column++) {
            for (int row = 0; row < SudokuManager.getWidth(); row++) {
                if (
                    buttons[column][row] != selectedButton &&
                    buttons[column][row].isEnabled()
                ) buttons[column][row].setEnabled(false);
            }
        }
    }

    //Enables all buttons that does not have LIGHT_GRAY Color value
    private static void enableAllButtons() {
        for (int column = 0; column < SudokuManager.getHeight(); column++) {
            for (int row = 0; row < SudokuManager.getWidth(); row++) {
                if (
                    buttons[column][row].getBackground() != Color.LIGHT_GRAY
                ) buttons[column][row].setEnabled(true);
            }
        }
    }

    //Iterates through all the buttons and finds the button that is selected
    private static JToggleButton getSelectedButton(
        Position selectedButtonPosition
    ) {
        JToggleButton selectedButton = new JToggleButton();
        for (int column = 0; column < SudokuManager.getHeight(); column++) {
            for (int row = 0; row < SudokuManager.getWidth(); row++) {
                if (buttons[column][row].isSelected()) {
                    selectedButton = buttons[column][row];

                    //Sets the selectedButtonPosition coordinates to the selected button
                    selectedButtonPosition.setColumn(column);
                    selectedButtonPosition.setRow(row);
                }
            }
        }
        return selectedButton;
    }

    //Sets ButtonColor depending on the isValid condition
    private static void setButtonColor(boolean isValid, JToggleButton button) {
        if (isValid == false) {
            button.setForeground(Color.RED);
        } else {
            button.setForeground(Color.BLACK);
        }
    }

    private static void isRedButtonsValid() {
        for (int column = 0; column < SudokuManager.getHeight(); column++) {
            for (int row = 0; row < SudokuManager.getWidth(); row++) {
                if (buttons[column][row].getBackground() == Color.RED) {
                    int redButtonValue = Integer.valueOf(
                        buttons[column][row].getText()
                    );
                    //Check if RED button is still invalid
                    SudokuManager.setSudokuValue(0, column, row);
                    if (
                        SudokuManager.setSudokuValue(
                            redButtonValue,
                            column,
                            row
                        )
                    ) {
                        buttons[column][row].setBackground(Color.WHITE);
                    }
                }
            }
        }
    }

    //Overrided Methods

    //Handles the mouse press events
    @Override
    public void actionPerformed(ActionEvent e) {
        for (int column = 0; column < SudokuManager.getHeight(); column++) {
            for (int row = 0; row < SudokuManager.getWidth(); row++) {
                if (e.getSource() == buttons[column][row]) {
                    buttons[column][row].setSelected(true);
                    disableAllButtonsExcept(buttons[column][row]);
                }
            }
        }
    }

    //Handles the keyTyped events
    @Override
    public void keyTyped(KeyEvent e) {
        char[] validKeys = { '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        for (char validKey : validKeys) {
            if (e.getKeyChar() == validKey) {
                Position buttonPosition = new Position();
                JToggleButton selectedButton = getSelectedButton(
                    buttonPosition
                );
                if (selectedButton.getText() == "") return; //If button is not selected then exit this function

                selectedButton.setText(Character.toString(validKey));

                /*Sets a entered value to the SudokuManager object and
                returns true or false wheter the number is valid*/
                boolean isValid = SudokuManager.setSudokuValue(
                    Integer.valueOf(e.getKeyChar() - 48),
                    buttonPosition.getColumn(),
                    buttonPosition.getRow()
                );

                setButtonColor(isValid, selectedButton);

                isRedButtonsValid(); //Checks all the other red buttons if they still should be red

                selectedButton.setSelected(false);
                enableAllButtons();
            }
        }

        //If the Sudoku is solved, option dialog will pop up
        if (SudokuManager.isSudokuSolved() == true) {
            String[] response = { "Kyllä" };
            JOptionPane.showOptionDialog(
                null,
                "Olet voittaja",
                "Voittaja",
                JOptionPane.YES_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                response,
                0
            );
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Do nothing here. This just has to be overrided
    }

    //Handles the backspace keyPress event
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 8) {
            for (int column = 0; column < SudokuManager.getHeight(); column++) {
                for (int row = 0; row < SudokuManager.getWidth(); row++) {
                    if (buttons[column][row].isSelected()) {
                        buttons[column][row].setText(" ");
                        SudokuManager.setSudokuValue(0, column, row);
                        setButtonColor(true, buttons[column][row]);

                        isRedButtonsValid(); //Checks all the other red buttons if they still should be red
                        buttons[column][row].setSelected(false);
                        enableAllButtons();
                    }
                }
            }
        }
    }
}
