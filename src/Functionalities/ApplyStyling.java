package Functionalities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ApplyStyling {
    private static final Font labelTitle = new Font("Serif", Font.BOLD, 24);
    private static final Font labelText = new Font("Serif", Font.PLAIN, 18);
    private static final Font labelBoldText = new Font("Serif", Font.BOLD, 18);

    private static final Font inputField = new Font("Serif", Font.PLAIN, 14);
    
    private static final Font basicButtonText = new Font("Serif", Font.PLAIN, 14);
    private static final Font biggerButtonText = new Font("Serif", Font.BOLD, 16);

    public static void applyTitle(JLabel label, boolean withPadding) {
        label.setFont(labelTitle);

        if (withPadding) {
            label.setBorder(new EmptyBorder(0,10,30,10));
        }
    }

    public static void applyText(JLabel label) {
        label.setFont(labelText);
        label.setBorder(new EmptyBorder(10,10,10,10));
    }
    
    public static void applyText(JTextField field) {
        field.setFont(inputField);
    }

    public static void applyBoldText(JLabel label) {
        label.setFont(labelBoldText);
        label.setBorder(new EmptyBorder(10,10,10,10));
    }
    
    public static void applyBasicButton(JButton button) {
        button.setFont(basicButtonText);
        button.setBorder(new EmptyBorder(10,10,10,10));
    }
    
    public static void applyBiggerButton(JButton button) {
        button.setFont(biggerButtonText);
        button.setBorder(new EmptyBorder(10,10,10,10));
    }


    public static void loopAndApplyText(JLabel[] labels) {
        for (JLabel label: labels) {
            applyText(label);
        }
    }

    public static void loopAndApplyText(JTextField[] fields) {
        for (JTextField field: fields) {
            applyText(field);
        }
    }

    public static void loopAndApplyBoldText(JLabel[] labels) {
        for (JLabel label: labels) {
            applyBoldText(label);
        }
    }

    public static void loopAndApplyBasicButton(JButton[] buttons) {
        for (JButton button: buttons) {
            applyBasicButton(button);
        }
    }

    public static void loopAndApplyBiggerButton(JButton[] buttons) {
        for (JButton button: buttons) {
            applyBiggerButton(button);
        }
    }

}
