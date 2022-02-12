import java.util.Random;

enum ScanResult
{
    ROW, COLUMN,
    BOX, ZERO, NOT_FOUND
}

public class SudokuManager
{
    private static int[][] grid;
    private static int height, width;
    private static int swapNumberStackFrameCounter = 0;

    //Constructors
    public SudokuManager()
    {
        setHeight(9);
        setWidth(9);
        grid = new int[getHeight()][getWidth()];
        while(!addNumbersToGrid() && !isGridValid())
        {
            resetGrid();
            swapNumberStackFrameCounter = 0;
        }
    }

    public SudokuManager(final int height, final int width)
    {
        setHeight(height);
        setWidth(width);
        grid = new int[getHeight()][getWidth()];
        while(!addNumbersToGrid() && !isGridValid())
        {
            resetGrid();
            swapNumberStackFrameCounter = 0;
        }
    }

    //Getters and setters
    public static int getHeight()
    {
        return SudokuManager.height;
    }
    public static int getWidth()
    {
        return SudokuManager.width;
    }

    public static void setHeight(final int height)
    {
        SudokuManager.height = height;
    }

    public static void setWidth(final int width)
    {
        SudokuManager.width = width;
    }

    public static int getSudokuValue(final int column, final int row)
    {
        return grid[column][row];
    }

    //Sets a value entered by the user and return true or false if value is valid
    public static boolean setSudokuValue(final int numberAttempt, final int column, final int row)
    {
        boolean isNumberValid = false;
        ScanResult scanResult = isNumberValid(numberAttempt, column, row);
        if(scanResult == ScanResult.NOT_FOUND || scanResult == ScanResult.ZERO)
            isNumberValid = true;
        else
            isNumberValid = false;
        
        grid[column][row] = numberAttempt;
        
        return isNumberValid;
    }

    private void resetGrid()
    {
        for(int column = 0; column < getHeight(); column++)
        {
            for(int row = 0; row < getWidth(); row++)
            {
                grid[column][row] = 0;
            }
        }
    }

    private boolean isGridValid()
    {
        for(int column = 0; column < getHeight(); column++)
        {
            for(int row = 0; row < getWidth(); row++)
            {
                if(isNumberValid(grid[column][row], column, row) != ScanResult.NOT_FOUND)
                    return false;
            }
        }
        return true;
    }

    public void hideRandomNumbersInGrid()
    {
        Random random = new Random();
        for(int column = 0; column < getHeight(); column++)
        {
            for(int row = 0; row < getWidth(); row++)
            {
                int hide = random.nextInt(2);
                if(hide == 1)
                    grid[column][row] = 0;
            }
        }
    }

    //This method generates the sudoku puzzle
    private boolean addNumbersToGrid()
    {
        for(int column = 0; column < getHeight(); column++)
        {
            //Generates numbers from 1 - 9 and shuffles them
            MyArray nineUniqueNumbers = new MyArray(getWidth());
            int uniqueNumbersIndex = 0;
            for(int row = 0; row < getWidth();)
            {
                //Loops through the row and tries different numbers from 1 to 9
                final int numberAttempt = nineUniqueNumbers.get(uniqueNumbersIndex);
                if(isNumberValid(numberAttempt, column, row) == ScanResult.NOT_FOUND)
                {
                    grid[column][row] = numberAttempt; //Place the number
                    uniqueNumbersIndex = 0; //Reset the uniqueNumbersIndex 
                    row++; //Move on to the next row
                }
                else
                {
                    uniqueNumbersIndex++;

                    //If all the numberAttempts have failed
                    if(uniqueNumbersIndex == nineUniqueNumbers.getSize())
                    {
                        //Starts recursively swap conflicting values in the grid
                        swapNumbersInGrid(numberAttempt, column, row);

                        //Reset the uniqueNumbersIndex
                        uniqueNumbersIndex = 0;
                        row++; //And move on in the current row
                    }
                    /*swapNumbersInGrid method might go in to an infinite loop
                    if that happens. Then exit function and try again. This should be fixed*/
                    if(swapNumberStackFrameCounter > 100)
                        return false;
                }
            }
        }
        return true;
    }

    private static void swapNumbersInGrid(int numberAttempt, int column, int row)
    {
        swapNumberStackFrameCounter++; // Increments a static variable every time we call this function
        if(swapNumberStackFrameCounter > 100)
            return;
        
        //Exit condition for the recursive function
        if(isNumberValid(numberAttempt, column, row) == ScanResult.NOT_FOUND)
        {
            grid[column][row] = numberAttempt;
            return;
        }
        
        //This represents the conflicting numbers location in the grid
        Position newPosition = new Position(column, row);

        //This represents the newNumber that will be put on to the conflicting numbers position
        int newNumberOption = 0;

        if(scanRow(numberAttempt, column) == ScanResult.ROW)
        {
            //newPosition gets the conflicting numbers column and row
            newPosition = moveCurrentPosition(numberAttempt, column, row, ScanResult.ROW);
            grid[column][row] = numberAttempt; //Put the numberAttempt in the current location
            grid[newPosition.getColumn()][newPosition.getRow()] = 0; //Remove the conflicting number from it´s location

            //Get newNumberOptions for the conflicting numbers location
            newNumberOption = getNumberOptions(newPosition.getColumn(), newPosition.getRow(), ScanResult.ROW);

            //Call this function again with the newNumber option and with it´s location
            swapNumbersInGrid(newNumberOption, newPosition.getColumn(), newPosition.getRow());
            grid[column][row] = 0; //Then disable this number for the other scans
        }

        if(scanColumn(numberAttempt, row) == ScanResult.COLUMN)
        {
            newPosition = moveCurrentPosition(numberAttempt, column, row, ScanResult.COLUMN);
            grid[column][row] = numberAttempt;
            grid[newPosition.getColumn()][newPosition.getRow()] = 0;
            newNumberOption = getNumberOptions(newPosition.getColumn(), newPosition.getRow(), ScanResult.COLUMN);
            swapNumbersInGrid(newNumberOption, newPosition.getColumn(), newPosition.getRow());
            grid[column][row] = 0;
        }

        if(scanBox(numberAttempt, column, row) == ScanResult.BOX)
        {
            newPosition = moveCurrentPosition(numberAttempt, column, row, ScanResult.BOX);
            grid[column][row] = numberAttempt;
            grid[newPosition.getColumn()][newPosition.getRow()] = 0;
            newNumberOption = getNumberOptions(newPosition.getColumn(), newPosition.getRow(), ScanResult.BOX);
            swapNumbersInGrid(newNumberOption, newPosition.getColumn(), newPosition.getRow());
            grid[column][row] = 0;
        }
        grid[column][row] = numberAttempt; //Put back the number and exit the function
    }

    private static Position moveCurrentPosition(final int numberAttempt, final int column, final int row, ScanResult scanResult)
    {
        /*This function scans ROW COLUMN or BOX to see where the conflicting numbers
        location is in the grid and returns it*/
        if(scanResult == ScanResult.ROW)
        {
            for(int i = 0; i < getWidth(); i++)
            {
                if(grid[column][i] == numberAttempt)
                    return new Position(column, i);
            }
        }
        if(scanResult == ScanResult.COLUMN)
        {
            for(int i = 0; i < getHeight(); i++)
            {
                if(grid[i][row] == numberAttempt)
                    return new Position(i, row);
            }
        }
        if(scanResult == ScanResult.BOX)
        {
            int columnStart = column - column % (getHeight() / 3);
            int rowStart = row - row % (getWidth() / 3);
            for(int i = columnStart; i < columnStart + (getHeight() / 3); i++)
            {
                for(int j = rowStart; j < rowStart + (getHeight() / 3); j++)
                {
                    if(grid[i][j] == numberAttempt)
                        return new Position(i, j);
                }
            }
        }
        return new Position(column, row);
    }

    private static int getNumberOptions(final int column, final int row, ScanResult scanResult)
    {
        /*This function scans ROW, COLUMN or BOX to see all the unusedNumbers*/
        MyArray unusedNumbers = new MyArray();
        int numberOption = 0;
        if(scanResult == ScanResult.ROW)
        {
            for(int i = 0; i < getWidth(); i++)
            {
                for(int u = 0; u < unusedNumbers.getSize(); u++)
                {
                    if(unusedNumbers.get(u) == grid[column][i] && unusedNumbers.get(u) != 0)
                        unusedNumbers.set(u, 0);
                }
            }
        }
        if(scanResult == ScanResult.COLUMN)
        {
            for(int i = 0; i < getHeight(); i++)
            {
                for(int u = 0; u < unusedNumbers.getSize(); u++)
                {
                    if(unusedNumbers.get(u) == grid[i][row] && unusedNumbers.get(u) != 0)
                        unusedNumbers.set(u, 0);
                }
            }
        }
        if(scanResult == ScanResult.BOX)
        {
            int columnStart = column - column % (getHeight() / 3);
            int rowStart = row - row % (getHeight() / 3);
            for(int i = columnStart; i < columnStart + (getHeight() / 3); i++)
            {
                for(int j = rowStart; j < rowStart + (getWidth() / 3); j++)
                {
                    for(int u = 0; u < unusedNumbers.getSize(); u++)
                    {
                        if(unusedNumbers.get(u) == grid[i][j] && unusedNumbers.get(u) != 0)
                            unusedNumbers.set(u, 0);
                    }
                }
            }
        }
        //Then it returns one of the unusedNumbers
        for(int u = 0; u < getWidth() ; u++)
        {
            if(unusedNumbers.get(u) != 0)
            {
                numberOption = unusedNumbers.get(u);
                break;
            }
        }
        return numberOption;
    }

    private static ScanResult isNumberValid(final int numberAttempt, final int column, final int row)
    {
        //This one return an enums ROW, COLUMN or BOX if there is a conflicting number
        if(numberAttempt == 0)
            return ScanResult.ZERO;
        
        if(scanRow(numberAttempt, column) == ScanResult.ROW)
            return ScanResult.ROW;
        if(scanColumn(numberAttempt, row) == ScanResult.COLUMN)
            return ScanResult.COLUMN;
        if(scanBox(numberAttempt, column, row) == ScanResult.BOX)
            return ScanResult.BOX;
        
        return ScanResult.NOT_FOUND;
    }

    private static ScanResult scanColumn(final int numberAttempt, final int row)
    {
        for(int i = 0; i < getHeight(); i++)
        {
            if(numberAttempt == grid[i][row])
            {
                return ScanResult.COLUMN;
            }
        }
        return ScanResult.NOT_FOUND;
    }

    private static ScanResult scanRow(final int numberAttempt, final int column)
    {
        for(int i = 0; i < getWidth(); i++)
        {
            if(numberAttempt == grid[column][i])
            {
                return ScanResult.ROW;
            }
        }
        return ScanResult.NOT_FOUND;
    }

    private static ScanResult scanBox(final int numberAttempt, final int column, final int row)
    {
        int columnStart = column - column % 3;
        int rowStart = row - row % 3;
        for(int i = columnStart; i < columnStart + 3; i++)
        {
            for(int j = rowStart; j < rowStart + 3; j++)
            {
                if(numberAttempt == grid[i][j])
                    return ScanResult.BOX;
            }
        }
        return ScanResult.NOT_FOUND;
    }

    //Return true if sudoku is solved
    public static boolean isSudokuSolved()
    {
        for(int column = 0; column < getHeight(); column++)
        {
            for(int row = 0; row < getWidth(); row++)
            {
                if(grid[column][row] == 0)
                    return false;
            }
        }
        return true;
    }

    //Displays Grid in to the console
    public static void displayGrid()
    {
        for(int i = 0; i < getHeight(); i++)
        {
            for(int j = 0; j < getWidth(); j++)
            {
                System.out.print(grid[i][j] + " ");
                if(j == 2 || j == 5)
                    System.out.print(" ");
            }
            System.out.println();
            if(i == 2 || i == 5)
                System.out.println();
        }
    }
}