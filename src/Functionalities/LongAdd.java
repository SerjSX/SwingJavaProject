package Functionalities;

import javax.swing.*;
import java.awt.*;

public class LongAdd {

    //Specifically used if the components to add are a lot
    public static void addToPanel(JPanel panel, JComponent[] components) {
        for (JComponent comp: components) {
            panel.add(comp);
        }
    }

    public static void addToFrame(JInternalFrame frame, JComponent[] components) {
        for (JComponent comp: components) {
            frame.add(comp);
        }
    }
}
