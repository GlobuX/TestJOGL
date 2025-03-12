package ru.globux.testjogl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;

public class TestNEWT implements GLEventListener {
    final GLWindow glWindow;
    final Animator animator;

    public static void main(String[] args) {
        new TestNEWT();
    }

    public TestNEWT() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        glWindow = GLWindow.create(caps);
        glWindow.setTitle("Test NEWT");
        glWindow.setSize(600, 400);
        glWindow.addGLEventListener(this);
        //FPSAnimator animator = new FPSAnimator(60);
        animator = new Animator(0 /* w/o AWT */);
//        animator.setUpdateFPSFrames(60, System.err);
        animator.setUpdateFPSFrames(60, null);
        animator.add(glWindow);
        glWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(final WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });
        glWindow.setVisible(true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }
}
