package ru.globux.testjogl.errorchecking;

import static com.jogamp.opengl.GL4.*;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.Animator;

public class CodedVersion implements GLEventListener {
    private int renderingProgram;
    private int vao[] = new int[1];

    public CodedVersion() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        final GLWindow glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 2 - program 3a");
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
        GL4 gl = (GL4) drawable.getGL();
        gl.glUseProgram(renderingProgram);
        gl.glDrawArrays(GL_POINTS, 0, 1);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
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

        checkOpenGLError();  // can use returned boolean if desired
        gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
        if (vertCompiled[0] != 1) {
            System.out.println("vertex shader compilation failed");
            printShaderLog(vShader);
        }

        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fshaderSource, null, 0);
        gl.glCompileShader(fShader);

        checkOpenGLError();  // can use returned boolean if desired
        gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
        if (fragCompiled[0] != 1) {
            System.out.println("fragment shader compilation failed");
            printShaderLog(fShader);
        }

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);

        gl.glLinkProgram(vfprogram);
        checkOpenGLError();
        gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
        if (linked[0] != 1) {
            System.out.println("linking failed");
            printProgramLog(vfprogram);
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    public static void main(String[] args) {
        new CodedVersion();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    private void printShaderLog(int shader) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        // determine the length of the shader compilation log
        gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
            System.out.println("Shader Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    void printProgramLog(int prog) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        int[] len = new int[1];
        int[] chWrittn = new int[1];
        byte[] log = null;

        // determine length of the program compilation log
        gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
        if (len[0] > 0) {
            log = new byte[len[0]];
            gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
            System.out.println("Program Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    boolean checkOpenGLError() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        boolean foundError = false;
        GLU glu = new GLU();
        int glErr = gl.glGetError();
        while (glErr != GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            foundError = true;
            glErr = gl.glGetError();
        }
        return foundError;
    }
}