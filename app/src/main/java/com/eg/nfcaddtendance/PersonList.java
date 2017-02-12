package com.eg.nfcaddtendance;

import java.util.ArrayList;

/**
 * Created by Eugene Galkine on 2/1/2017.
 */

public class PersonList
{
    private ArrayList<Person> list = new ArrayList<Person>();

    public PersonList()
    {
        list.add(new Person("0x0490d30a0e4980", "Michael", "Hafeli", "2"));
        list.add(new Person("0x040036aae74381", "Amir", "Patel", "3"));
        list.add(new Person("0x04ae60c2e64380", "Peter", "Ogan", "4"));
    }

    public Person getFromID(String id)
    {
        for (Person p : list)
            if (p.id.equals(id))
                return p;

        return null;
    }

    class Person
    {
        public String id;
        public String fname, lname, pid;

        public Person(String id, String fname, String lname, String pid)
        {
            this.id = id;
            this.fname = fname;
            this.lname = lname;
            this.pid = pid;
        }
    }
}
