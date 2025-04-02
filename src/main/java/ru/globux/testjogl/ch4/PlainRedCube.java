package ru.globux.testjogl.ch4;

import static com.jogamp.opengl.GL4.*;

import ru.globux.testjogl.util.Utils;
import java.nio.*;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import org.joml.Matrix4f;

public class PlainRedCube implements GLEventListener {
    private GLWindow glWindow;
    private Animator animator;
    private int renderingProgram;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private float cameraX, cameraY, cameraZ;
    private float cubeLocX, cubeLocY, cubeLocZ;

    // allocate variables for display() function
    private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);  // buffer for transfering matrix to uniform
    private Matrix4f pMat = new Matrix4f();  // perspective matrix
    private Matrix4f vMat = new Matrix4f();  // view matrix
    private Matrix4f mMat = new Matrix4f();  // model matrix
    private Matrix4f mvMat = new Matrix4f(); // model-view matrix
    private int mvLoc, pLoc;
    private float aspect;

    public PlainRedCube() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 4 - program 1a");
        glWindow.setSize(800, 600);
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

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
//        gl.glClear(GL_COLOR_BUFFER_BIT);

        gl.glUseProgram(renderingProgram);

        mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
        pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

        aspect = (float) glWindow.getWidth() / (float) glWindow.getHeight();
        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

        vMat.setTranslation(-cameraX, -cameraY, -cameraZ);

        mMat.setTranslation(cubeLocX, cubeLocY, cubeLocZ);

        mvMat.identity();
        mvMat.mul(vMat);
        mvMat.mul(mMat);

//        System.out.println("FloatBuffer capacity: " + vals.capacity());
//        System.out.println("FloatBuffer position: " + vals.position());


        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
//        System.out.println("FloatBuffer position after mvMat: " + vals.position());
//        vals.clear();

        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
//        System.out.println("FloatBuffer position after pMat: " + vals.position());
//        vals.clear();

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        renderingProgram = Utils.createShaderProgram("ch4/shaders/vertShader.glsl", "ch4/shaders/fragShader.glsl");
        setupVertices();
        cameraX  = 0.0f;  cameraY  = 0.0f;  cameraZ  = 8.0f;
        cubeLocX = -1.0f; cubeLocY = -2.5f; cubeLocZ = 0.0f;
    }

    private void setupVertices() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] vertexPositions = {
                        -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
                };

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertexPositions);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);
    }

    public static void main(String[] args) {
        new PlainRedCube().start();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}