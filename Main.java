

public class Main
{
    public static void main(String[] args)
    {
        SudokuManager sudokuManager = new SudokuManager();
        sudokuManager.hideRandomNumbersInGrid();

        new GUI();
    }
}