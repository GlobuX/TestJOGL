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

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

public class Prog2_6_Animation implements GLEventListener {
    private GLWindow glWindow;
    private Animator animator;
    private long tick;
    private int renderingProgram;
    private int vao[] = new int[1];
    List<String[]> programs = new ArrayList<>();
    private float x = 0.0f;  // location of triangle
    private float inc = 0.01f;  // offset for moving the triangle

    public Prog2_6_Animation() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 2 - program 6");
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
        new Prog2_6_Animation()
                .loadProgram("shaders/vertShader2_6.glsl")
                .loadProgram("shaders/fragShader.glsl")
                .start();
    }

    public Prog2_6_Animation loadProgram(String file) {
        programs.add(readShaderSource(file));
        return this;
    }

    public void start() {
        glWindow.setVisible(true);
        animator.start();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL_COLOR_BUFFER_BIT);  // clear the background to black, each time
        gl.glUseProgram(renderingProgram);

        long currentTick = System.currentTimeMillis();
        long delta = currentTick - this.tick;
        this.tick = currentTick;
        x += inc * delta / 4;   // move the triangle along x axis
        if (x > 1.0f) inc = -0.01f;  // switch to moving the triangle to the left
        if (x < -1.0f) inc = 0.01f;  // switch to moving the triangle to the right
        int offsetLoc = gl.glGetUniformLocation(renderingProgram, "offset");  // retrieve pointer to "offset"
        gl.glProgramUniform1f(renderingProgram, offsetLoc, x);

        gl.glDrawArrays(GL_TRIANGLES,0,3);
    }

    public void init(GLAutoDrawable drawable) {
        this.tick = System.currentTimeMillis();
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