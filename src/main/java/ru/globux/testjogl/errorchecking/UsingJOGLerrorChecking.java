package ru.globux.testjogl.errorchecking;

import static com.jogamp.opengl.GL4.*;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;

public class UsingJOGLerrorChecking implements GLEventListener {
    private int renderingProgram;
    private int vao[] = new int[1];

    public UsingJOGLerrorChecking() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        final GLWindow glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 2 - program 3b");
        glWindow.setSize(400, 200);
        glWindow.addGLEventListener(this);
        //final FPSAnimator animator = new FPSAnimator(60);
        final Animator animator = new Animator(0 /* w/o AWT */);
        //animator.setUpdateFPSFrames(60, System.err);
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

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(renderingProgram);
        gl.glDrawArrays(GL_POINTS, 0, 1);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        int[] linked = new int[1];

        String vshaderSource[] = {
                "#version 430    \n",
                "void main(void) \n",
                "{ gl_Position = vec4(0.0, 0.0, 0.5, 1.0); } \n",
                };
        String fshaderSource[] = {
                "#version 430    \n",
                "out vec4 color; \n",
                "void main(void) \n",
                "{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
                };

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 3, vshaderSource, null, 0);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fshaderSource, null, 0);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);

        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    public static void main(String[] args) {
        new UsingJOGLerrorChecking();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}
