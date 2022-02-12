import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JOptionPane;

public class GUI extends JFrame implements ActionListener, KeyListener
{
    private static JPanel panel;
    private static JToggleButton[][] buttons;

    public GUI()
    {
        this.setTitle("Sudoku");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        addPanelAttributes();
        addButtonsToPanel();
        this.add(panel);
        this.addKeyListener(this);
        this.setVisible(true);
    }

    //Generates panel for the frame
    private static void addPanelAttributes()
    {
        panel = new JPanel();
        panel.setSize(550, 550);
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setLayout(new GridLayout(9, 9, 10, 10));
    }

    //Generates buttons for the panel
    private void addButtonsToPanel()
    {
        buttons = new JToggleButton[SudokuManager.getHeight()][SudokuManager.getWidth()];
        for(int column = 0; column < SudokuManager.getHeight(); column++)
        {
            for(int row = 0; row < SudokuManager.getWidth(); row++)
            {
                int sudokuNumber = SudokuManager.getSudokuValue(column, row);
                buttons[column][row] = new JToggleButton();
                if(sudokuNumber == 0)
                {
                    buttons[column][row].setText(" ");
                    buttons[column][row].setBackground(Color.WHITE);
                }
                else
                {
                    buttons[column][row].setText(Integer.toString(sudokuNumber));
                    buttons[column][row].setBackground(Color.GRAY);
                }
                
                buttons[column][row].setFocusable(false);
                buttons[column][row].addActionListener(this);
                buttons[column][row].setSelected(false);
                panel.add(buttons[column][row]);
            }
        }
    }

    //Disables all buttons in the grid except the button that is passed in to the method
    private static void disableAllButtonsExcept(final JToggleButton selectedButton)
    {
        for(int column = 0; column < SudokuManager.getHeight(); column++)
        {
            for(int row = 0; row < SudokuManager.getWidth(); row++)
            {
                if(buttons[column][row] != selectedButton && buttons[column][row].isEnabled())
                    buttons[column][row].setEnabled(false);
            }
        }
    }

    //Enables all buttons that have the LIGHT_GRAY Color value
    private static void enableAllButtons()
    {
        for(int column = 0; column < SudokuManager.getHeight(); column++)
        {
            for(int row = 0; row < SudokuManager.getWidth(); row++)
            {
                if(buttons[column][row].getBackground() != Color.LIGHT_GRAY)
                    buttons[column][row].setEnabled(true);
            }
        }
    }

    //Iterates through all the buttons an finds the button that is selected
    private static JToggleButton getSelectedButton(Position selectedButtonPosition)
    {
        JToggleButton selectedButton = new JToggleButton();
        for(int column = 0; column < SudokuManager.getHeight(); column++)
        {
            for(int row = 0; row < SudokuManager.getWidth(); row++)
            {
                if(buttons[column][row].isSelected())
                {
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
    private static void setButtonColor(boolean isValid, JToggleButton button)
    {
        if(isValid == false)
            button.setBackground(Color.RED);
        else
            button.setBackground(Color.WHITE);
    }

    //Overrided Methods

    //Handles the mouse press events
    @Override
    public void actionPerformed(ActionEvent e)
    {
        for(int column = 0; column < SudokuManager.getHeight(); column++)
        {
            for(int row = 0; row < SudokuManager.getWidth(); row++)
            {
                if(e.getSource() == buttons[column][row])
                {
                    buttons[column][row].setSelected(true);
                    disableAllButtonsExcept(buttons[column][row]);
                }
            }
        }
    }

    //Handles the keyTyped events
    @Override
    public void keyTyped(KeyEvent e)
    {
        char[] validKeys = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for(char validKey : validKeys)
        {
            if(e.getKeyChar() == validKey)
            {
                Position buttonPosition = new Position();
                JToggleButton selectedButton = getSelectedButton(buttonPosition);
                selectedButton.setText(Character.toString(validKey));

                /*Sets a entered value to the SudokuManager object and
                returns true or false wheter the number is valid*/
                boolean isValid = SudokuManager.setSudokuValue(Integer.valueOf(e.getKeyChar()-48),
                    buttonPosition.getColumn(), buttonPosition.getRow());
                
                setButtonColor(isValid, selectedButton);
                
                selectedButton.setSelected(false);
                enableAllButtons();
            }
        }
        //If the Sudoku is solved, option dialog will pop up
        if(SudokuManager.isSudokuSolved() == true)
        {
            String[] response = {"Kyllä"};
            JOptionPane.showOptionDialog(
                                    null,
                                    "Olet viineri",
                                    "Viineri",
                                    JOptionPane.YES_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE,
                                    null,
                                    response,
                                    0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        //Do nothing here. This just has to be overrided
    }

    //Handles the backspace keyPress event
    @Override
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == 8)
        {
            for(int column = 0; column < SudokuManager.getHeight(); column++)
            {
                for(int row = 0; row < SudokuManager.getWidth(); row++)
                {
                    if(buttons[column][row].isSelected())
                    {
                        buttons[column][row].setText(" ");
                        SudokuManager.setSudokuValue(0, column, row);
                        setButtonColor(true, buttons[column][row]);

                        buttons[column][row].setSelected(false);
                        enableAllButtons();
                    }
                    
                }
            }
        }
    }
}