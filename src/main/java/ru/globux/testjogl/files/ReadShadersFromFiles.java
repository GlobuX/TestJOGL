package ru.globux.testjogl.files;

import static com.jogamp.opengl.GL4.*;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class ReadShadersFromFiles implements GLEventListener {
    private int renderingProgram;
    private int vao[] = new int[1];

    public ReadShadersFromFiles() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        final GLWindow glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 2 - program 4");
        glWindow.setSize(400, 200);
        glWindow.addGLEventListener(this);
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

        String vshaderSource[] = readShaderSource("/shaders/vertShader.glsl");
        String fshaderSource[] = readShaderSource("/shaders/fragShader.glsl");

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

    public static void main(String[] args) {
        new ReadShadersFromFiles();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    private String[] readShaderSource(String filename) {
        Path file = null;
        try {
            file = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());
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
        String[] program = null;
        program = new String[lines.size()];
        int i = 0;
        for(String line : lines) {
            program[i] = line + "\n";
        }
        return program;
    }
}
