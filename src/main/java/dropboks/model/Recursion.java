package dropboks.model;

import java.util.List;

/**
 * Created by miwas on 17.01.17.
 */
public interface Recursion<Type> {
    Type appendChildren(List<Type> list);
}
