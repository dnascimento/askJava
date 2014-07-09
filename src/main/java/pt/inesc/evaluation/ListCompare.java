package pt.inesc.evaluation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListCompare<T> {
    List<T> list1;
    List<T> list2;

    String label;


    public ListCompare(List<T> list1, List<T> list2, String label) {
        super();
        this.list1 = list1;
        this.list2 = list2;
        this.label = label;
    }



    public List<T> compare() {
        // lists are equal
        if (list1.equals(list2)) {
            return list1;
        }

        // Quero saber: quais uma delas nao tem
        List<T> shared = new ArrayList<T>();

        Iterator<T> it1 = list1.iterator();
        while (it1.hasNext()) {
            T s = it1.next();
            if (list2.remove(s)) {
                // ambas têm
                it1.remove();
                shared.add(s);
            } else {
                System.out.println(label + " server 6666 has: " + s);
            }
        }

        for (T s : list2) {
            System.out.println(label + " server 7666 has: " + s);
        }
        return shared;
    }
}
