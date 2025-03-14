package ru.globux.testjogl;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jogamp.opengl.GL.GL_POINTS;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

public class Prog2_5_Triangle implements GLEventListener {
    private int renderingProgram;
    private int vao[] = new int[1];
    private GLWindow glWindow;
    private Animator animator;
    List<String[]> programs = new ArrayList<>();

    public Prog2_5_Triangle() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 2 - program 4");
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

    public static void main(String[] args) {
        new Prog2_5_Triangle()
                .loadPrograms("shaders/vertShader2_5.glsl")
                .loadPrograms("shaders/fragShader.glsl")
                .start();
    }

    public Prog2_5_Triangle loadPrograms(String file) {
        programs.add(readShaderSource(file));
        return this;
    }

    public void start() {
        glWindow.setVisible(true);
        animator.start();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(renderingProgram);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

//        String vshaderSource[] = readShaderSource("shaders/vertShader.glsl");
//        String fshaderSource[] = readShaderSource("shaders/fragShader.glsl");
        String[] vshaderSource = programs.get(0);
        String[] fshaderSource = programs.get(1);;

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glCompileShader(vShader);

        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
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

    private String[] readShaderSource(String filename) {
        Path file = null;
        try {
            URI uri = this.getClass().getClassLoader().getResource(filename).toURI();
            file = Paths.get(Objects.requireNonNull(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Charset charset = StandardCharsets.US_ASCII;
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file, charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] program = new String[lines.size()];
        int i = 0;
        for(String line : lines) {
            program[i] = line + "\n";
            ++i;
        }
        return program;
    }
}