package dropboks;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by miwas on 10.01.17.
 */
public class PathResolver {

    public static String getParentPath(String path){
        return getParentPath(convertToList(path));
    }

    public static String getParentPath(ArrayList<String> path){
        return String.join("/", path.subList(0, path.size()-1));
    }

    public static String getUserName(String path){
        return getUserName(convertToList(path));
    }


    public static String getName(ArrayList<String> path){
        return path.get(path.size()-1);
    }

    public static String getName(String path){
        return getName(convertToList(path));
    }

    public static String getUserName(ArrayList<String> path){
        return path.get(0);
    }


    public static ArrayList<String> convertToList(String path){
        return new ArrayList<>(Arrays.asList(path.split("/")));
    }

    public static boolean isHomeDirectory(ArrayList<String> path){
        return (path.size() == 1);
    }

    public static boolean isHomeDirectory(String path){
        return isHomeDirectory(convertToList(path));
    }
}
