public class Position
{
    private int column, row;

    //Helper class to store the column and a row values in to one object

    public Position()
    {
        setColumn(0);
        setRow(0);
    }

    public Position(final int column, final int row)
    {
        setColumn(column);
        setRow(row);
    }

    //Getters and setters
    public int getColumn()
    {
        return this.column;
    }

    public int getRow()
    {
        return this.row;
    }

    public void setColumn(final int column)
    {
        this.column = column;
    }

    public void setRow(final int row)
    {
        this.row = row;
    }
}