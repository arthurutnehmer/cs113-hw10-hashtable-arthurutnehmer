package edu.miracosta.cs113;

import java.util.*;

public class HashTableChain<K, V> implements Map<K, V>
{
    /** The table */
    private LinkedList< Entry<K, V> > [] table;
    /** The number of keys */
    private int numKeys;
    /** The Capacity */
    private static final int CAPACITY = 101;
    /** The max load capacity*/
    private static final double LOAD_THRESHOLD = 3.0;

    /** The default constructor. */
    public HashTableChain()
    {
        table = new LinkedList[CAPACITY];
    }

    @Override
    public int hashCode()
    {
        Hashtable<K, V> test = new Hashtable<>();

        for(int i = 0; i < table.length; i++)
        {
            if(table[i] == null)
            {
                //skipp iteration.
            }
            else
            {
                for (Entry<K, V> nextItem : table[i])
                {
                    if (nextItem != null)
                    {
                        test.put(nextItem.key, nextItem.value);
                    }
                }
            }
        }

        return test.hashCode();

    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return new EntrySet();
    }

    /**
     * The number of slots the array has.
     * */
    @Override
    public int size()
    {
        return numKeys;
    }

    /**
     * Returns the keys in this table as a set.
     * */
    @Override
    public Set<K> keySet()
    {
        Set<K> keySet = new HashSet<K>(size());

        for (LinkedList<Entry<K,V>> list : table)
        {
            if(list != null)
            {
                for (Entry<K, V> entry : list) {
                    if (entry != null) {
                        keySet.add(entry.getKey());
                    }
                }
            }
        }
        return keySet;
    }

    /**
     * Method that will retrieve the item based on its hashCode.
     * */
    @Override
    public V get(Object key)
    {
        int index = key.hashCode() % table.length;
        if(index<0)
        {
            index += table.length;
        }
        if(table[index] == null)
        {
            return null;
        }

        //search the list at this index.
        for(Entry<K, V> nextItem: table[index])
        {
            if(nextItem.key.equals(key))
            {
                return nextItem.value;
            }
        }

        //key not in table
        return null;
    }

    /**
     * Will decide if the Hash table contains this value.
     * */
    @Override
    public boolean containsValue(Object value)
    {

        for(int i = 0; i < table.length; i++)
        {
            if(table[i] == null)
            {
                //skipp iteration.
            }
            else
            {
                for (Entry<K, V> nextItem : table[i])
                {
                    if (nextItem.getValue().equals(value))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Erases all object in array.
     * */
    @Override
    public void clear()
    {
        for(int i = 0; i< table.length; i++)
        {
            table[i] = null;
        }
        numKeys = 0;
    }

    /**
     * Unsupported.
     * */
    @Override
    public Collection<V> values()
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Unsupported.
     * */
    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * will insert into array and put into a node if the array is occupied at this location.
     * */
    @Override
    public V put(K key, V value)
    {
        int index = key.hashCode() % table.length;
        if(index < 0)
        {
            index += table.length;
        }
        if(table[index] == null)
        {
            //create a linked list at table.
            table[index] = new LinkedList< Entry<K, V> >();
        }
        //search the list at table[index] to find the key
        for (Entry<K, V> nextItem: table[index])
        {
            if(nextItem.key.equals(key))
            {
                //replace the value for this key.
                V oldVal = nextItem.value;
                nextItem.setValue(value);
                return oldVal;
            }
        }

        //key is not in the table, add new item.
        table[index].addFirst(new Entry<K,V>(key, value));
        numKeys++;
        if(numKeys> (LOAD_THRESHOLD*table.length))
        {
            rehash();
        }

        return null;
    }

    /**
     * Will decide if the Hash table contains this object.
     * */
    @Override
    public boolean containsKey(Object key)
    {
        int index = key.hashCode() % table.length;
        if (index < 0)
        {
            index += table.length;
        }
        //If nothing is at this key index, returm null.
        if (table[index] == null)
        {
            return false;
        }

        for (Entry<K, V> entry : table[index])
        {
            if(entry.getKey().equals(key))
            {
                    return true;
            }
        }
        return false;
    }

    /**
     * This method will rehash the table.
     * It does this by clearing the table, making a new one, rehashing and reinserting.
     * */
    private void rehash()
    {
        //old table is saved.
        LinkedList<Entry<K,V>>[] oldTable = table;
        //new table is created at a length of 2x.
        table = new LinkedList[oldTable.length * 2 + 1];
        numKeys = 0;
        //rehash and reinsert at new locations.
        for (LinkedList<Entry<K, V>> list : oldTable)
        {
            if (list != null)
            {
                for (Entry<K,V> entry : list)
                {
                    put(entry.getKey(), entry.getValue());
                }
            }
        }
    }



    /*
     * Removes the object is present in the array of list.
     */
    @Override
    public V remove(Object key)
    {
        int index = key.hashCode() % table.length;
        if (index < 0)
        {
            index += table.length;
        }
        //If nothing is at this key index, returm null.
        if (table[index] == null)
        {
            return null;
        }
        //else, go to the key and the node that is equall to the object.
        for (Entry<K, V> entry : table[index])
        {
            if(entry.getKey().equals(key))
            {
                V value = entry.getValue();
                table[index].remove(entry);
                numKeys--;
                //if the index is empty,set to null.
                if (table[index].isEmpty())
                {
                    table[index] = null;
                }
                return value;
            }
        }
        return null;
    }


    private class EntrySet extends AbstractSet<Map.Entry<K,V>>
    {

        @Override
        public int size()
        {
            return numKeys;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator()
        {
            return new SetIterator();
        }
    }


    /*
     * Will return true of numKeys >1 or false if numKeys < 1.
     */
    @Override
    public boolean isEmpty()
    {
        return numKeys == 0;
    }

    @Override
    public String toString()
    {
        String toReturn = "";
        for(int i = 0; i < table.length; i++)
        {
            if(table[i] == null)
            {
                //skipp iteration.
            }
            else
            {
                for (Entry<K, V> nextItem : table[i])
                {
                    toReturn += nextItem.getValue() + "\n";
                }
            }
        }
        return toReturn;

    }


    private class SetIterator implements Iterator<Map.Entry<K,V>>
    {
        int index = 0;
        Entry<K, V> lastItemReturned = null;
        Iterator<Entry<K, V>> iter = null;

        @Override
        public boolean hasNext()
        {

            if (iter != null && iter.hasNext())
            {
                return true;
            }
            do {
                index++;
                if (index >= table.length)
                {
                    return false;
                }
            }
            while (table[index] == null);
            iter = table[index].iterator();
            return iter.hasNext();
        }

        /*
         * Returns the next element in the iteration.
         */
        @Override
        public Map.Entry<K, V> next()
        {
            if (iter.hasNext())
            {
                lastItemReturned = iter.next();
                return lastItemReturned;
            }
            else
            {
                throw new NoSuchElementException();
            }
        }


        /*
         * Removes the last item returned by a call to next.
         * If a call to remove is not preceded by a call to next,
         * it throws an IllegalStateException.
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() throws IllegalStateException
        {
            if (lastItemReturned == null)
            {
                throw new IllegalStateException();
            } else
                {
                iter.remove();
                lastItemReturned = null;
            }
        }
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Hashtable)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Hashtable c = (Hashtable) obj;

        return c.equals(this);
    }

    /** Inner class Entry<K, V> */
    private static class Entry<K, V> implements Map.Entry<K, V>
    {
        private K key;
        private  V value;


        public Entry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }

        @Override
        public V setValue(V val)
        {
            V oldVal = value;
            value = val;
            return oldVal;
        }

        @Override
        public boolean equals(Object other)
        {
            return other instanceof Entry && key.equals(((Entry<K, V>) other).key);
        }
        @Override
        public String toString()
        {
            return key + "=" + value;
        }
    }

}
