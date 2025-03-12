package ru.globux.testjogl;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.jogamp.opengl.awt.GLCanvas;

public class GLFrame extends Frame {

    private GLCanvas canvas;
    private Animator anim;

    public GLFrame() {
        GLProfile glProfile = GLProfile.get(GLProfile.GL4bc);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        GLCanvas canvas = new GLCanvas(glCapabilities);
        canvas.setIgnoreRepaint(true);
        canvas.setSize(640, 480);

        setTitle("Sample OpenGL Java application");
        setResizable(false);
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        setSize(getPreferredSize());

        canvas.addGLEventListener(new Renderer());

        anim = new Animator(canvas);
        anim.setRunAsFastAsPossible(true);

        anim.start();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                anim.stop();
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        GLFrame frame = new GLFrame();
        frame.setVisible(true);
    }
}