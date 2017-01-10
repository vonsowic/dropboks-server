package dropboks;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import static org.assertj.core.api.Assertions.*;

import static org.junit.Assert.*;

/**
 * Created by miwas on 10.01.17.
 */
public class PathResolverTest {
    @Test
    public void convertToList() throws Exception {
        ArrayList<String> list = PathResolver.convertToList("Ciri/Pliki/plik.txt");
        assertThat(list).hasSize(3)
                .contains("Ciri", "Pliki", "plik.txt");
    }

    @Test
    public void getParentPath() throws Exception {
        String parentPath = PathResolver.getParentPath("Ciri/Pliki/plik.txt");
        assertThat(PathResolver.convertToList(parentPath)).hasSize(2)
                .contains("Ciri", "Pliki")
                .doesNotContain("plik.txt");
    }

    @Test
    public void getUserName() throws Exception {
        Assert.assertEquals("Ciri", PathResolver.getUserName("Ciri/Pliki/Filmy/StarWars.avi"));
    }

    @Test
    public void isHomeDirectory() throws Exception {
        Assert.assertTrue( PathResolver.isHomeDirectory("Ciri") );
    }

    @Test
    public void isHomeDirectory1() throws Exception {
        Assert.assertFalse( PathResolver.isHomeDirectory("Ciri/plik.txt") );
    }

}