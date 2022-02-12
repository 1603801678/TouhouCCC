import template.Characters;
import template.ConstantTool;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
/**
 * ????
 * @author ldj
 * @version 1.0.alpha
 */
public class Main {
    public static void main(String[] args) {
        pathInit();
        charactersInit();
    }
    /**??????*/
    private static void pathInit() {
        String path = Main.class.getClassLoader().getResource("Main.class").getPath();
        ConstantTool.rootpath = new File(path).getParentFile();
    }
    /**????*/
    private static void charactersInit() {
        File charactersRoot = new File(ConstantTool.rootpath,"\\characters");
        File[] characters = charactersRoot.listFiles();
        for(File f:characters) {
            String name = f.getName();
            try {
                Class<?> charclass = Main.class.forName("characters." + name + "." + name);
                if(!charclass.getSuperclass().equals(Characters.class))continue;
                Constructor<Characters> constructor = (Constructor<Characters>) charclass.getConstructor();
                Characters charac = constructor.newInstance();
                ConstantTool.charRegister.put(charac.getAbbreviation(),constructor);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                continue;
            } catch (InstantiationException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
