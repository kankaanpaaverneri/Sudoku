import java.util.Random;

public class MyArray
{
    private int[] array;
    private int size;

    //Array class which creates numbers from 1 - array size and shuffles the values

    //Constructors
    public MyArray()
    {
        setSize(9);
        array = new int[getSize()];
        this.setValuesFromOneToNine();
        this.shuffleValues();
    }

    public MyArray(final int size)
    {
        setSize(size);
        array = new int[getSize()];
        this.setValuesFromOneToNine();
        this.shuffleValues();
    }

    //Getters and setters
    public int get(final int index)
    {
        return this.array[index];
    }

    public void set(final int index, final int value)
    {
        this.array[index] = value;
    }
    public int getSize()
    {
        return this.size;
    }

    public void setSize(final int size)
    {
        this.size = size;
    }

    //Other member methods
    public void setValuesFromOneToNine()
    {
        for(int i = 0; i < getSize(); i++)
        {
            array[i] = i+1;
        }
    }

    public void shuffleValues()
    {
        Random random = new Random();
        for(int i = 0; i < getSize(); i++)
        {
            int j = random.nextInt(9);

            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public void displayArray()
    {
        for(int i = 0; i < this.getSize(); i++)
        {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
}