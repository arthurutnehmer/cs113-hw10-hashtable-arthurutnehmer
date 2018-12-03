package edu.miracosta.cs113;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class TestMain
{
    public static void main(String[] args)
    {
        Map<String, Integer> hashTable = new Hashtable<String, Integer>();
        Map<String, Integer> hashTable2 = new HashTableChain<String, Integer>();

        for (int i = 0; i < 13; i ++)
        {
            hashTable.put(Integer.toString(i), i);
        }

        for (int i = 0; i < 13; i ++)
        {
            hashTable2.put(Integer.toString(i), i);
        }



        System.out.println(hashTable2.hashCode());
        System.out.println(hashTable.hashCode());

    }

}
