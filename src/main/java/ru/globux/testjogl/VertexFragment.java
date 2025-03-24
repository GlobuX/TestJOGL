package ru.globux.testjogl;

import static com.jogamp.opengl.GL4.*;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.Animator;

public class VertexFragment implements GLEventListener {
    private GLWindow glWindow;
    private Animator animator;
    private float size;
    private boolean isGrow;
    private int renderingProgram;
    private int vao[] = new int[1];

    public VertexFragment() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        this.size = 49.0f;
        this.isGrow = true;
        glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 2 - program 2");
        glWindow.setSize(400, 200);
        glWindow.addGLEventListener(this);
        animator = new Animator(0 /* w/o AWT */);
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
    }

    public void start() {
        glWindow.setVisible(true);
        animator.start();
    }

    public static void main(String[] args) {
        new VertexFragment().start();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);
        if (isGrow) {
            if (size >= 50.0f) {
                isGrow = false;
                size -= 1.0f;
            }
            else {
                size += 1.0f;
            }
        }
        else {
            if (size <= 1.0f) {
                isGrow = true;
                size += 1.0f;
            }
            else {
                size -= 1.0f;
            }
        }
        gl.glPointSize(size);
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

        String vshaderSource[] = {
                "#version 430    \n",
                "void main(void) \n",
                "{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); } \n"
        };

//        String fshaderSource[] = {
//                "#version 430    \n",
//                "out vec4 color; \n",
//                "void main(void) \n",
//                "{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
//        };

        String fshaderSource[] = {
                "#version 430    \n",
                "out vec4 color; \n",
                "void main(void) \n",
                "{ if (gl_FragCoord.x < 295) color = vec4(1.0, 0.0, 0.0, 1.0); else color = vec4(0.0, 0.0, 1.0, 1.0); } \n"
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

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}