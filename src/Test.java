
import template.ConstantTool;
import visual.UserInterface;

import javax.swing.*;

/**
 * ???
 * @author ldj
 * @version 1.0.alpha
 */

public class Test {
    public static void main(String[] args) {
        Main.main(null);
        //System.out.println(ConstantTool.charRegister.keySet());
        SwingUtilities.invokeLater(()->new UserInterface());
    }
}
