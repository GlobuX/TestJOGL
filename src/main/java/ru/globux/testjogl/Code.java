package ru.globux.testjogl;

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Code extends Frame implements GLEventListener {
    private GLCanvas myCanvas;

    public Code() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL4bc);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        setTitle("Chapter 2 - program 1");
        setSize(600,400);
        setLocation(200,200);
        myCanvas = new GLCanvas(glCapabilities);
//        myCanvas.setIgnoreRepaint(true);
        myCanvas.addGLEventListener(this);
//        this.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        this.add(myCanvas);
//        this.setVisible(true);
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void main(String[] args) {
        Frame frame = new Code();
        SwingUtilities.invokeLater(() -> {frame.setVisible(true);});
    }

    public void init(GLAutoDrawable drawable) {

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    public void dispose(GLAutoDrawable drawable) {

    }
}