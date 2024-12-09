package com.bjornp.aoc.util.viz;

import javax.swing.*;
import java.awt.*;

public class Graphics extends JFrame {
    public Graphics(String title, int width, int height) throws HeadlessException {
        this.setSize(width, height);
        this.setPreferredSize(new Dimension(width, height));
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
