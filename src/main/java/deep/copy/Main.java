package deep.copy;


import deep.copy.domain.Man;
import deep.copy.utils.CopyUtils;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {


        List<String> favoriteBooks = new ArrayList<>();
        favoriteBooks.add("Think and grow rich!");
        favoriteBooks.add("Effective Java.");
        favoriteBooks.add("Kafka!");
        Man man = new Man("Joe", 22, favoriteBooks);

//        Man man1 = Copy.deepCopy(man);


    }
}