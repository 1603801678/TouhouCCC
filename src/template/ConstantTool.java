package template;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

/**
 * 一些工具，在其他类中会用到
 * @author ldj
 * @version 1.0.alpha
 */

public final class ConstantTool {
    public static File rootpath;
    public static TreeMap<String,Constructor<Characters>> charRegister = new TreeMap<>();

    public static Characters getCharacter(String abv){
        try {
            return charRegister.get(abv).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void change(T[] t, int a, int b) {
        T temp = t[a];
        t[a] = t[b];
        t[b] = temp;
    }

    public static String arrayFormat(String in) {
        int l = in.length() - 1;
        return in.substring(1,l).replaceAll(", ", "\n");
    }

    public static String multiLine(String in) {
        return String.format("<html>%s</html>", in.toString().replaceAll("\n", "<br>"));
    }
}
