package ru.globux.testjogl.ch4;

import static com.jogamp.opengl.GL4.*;

import ru.globux.testjogl.util.Utils;
import java.nio.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
//import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.*;
import org.joml.Matrix4f;

public class Instancing implements GLEventListener {
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
    private int vLoc;
    private int pLoc;
    private int tfLoc;
    private float aspect;

    // tumbling variable
    private double elapsedTime;
    private double startTime;
    private double timeFactor;

    public Instancing() {
        final GLProfile profile = GLProfile.get(GLProfile.GL4bc);
        final GLCapabilities caps = new GLCapabilities(profile);
        glWindow = GLWindow.create(caps);
        glWindow.setTitle("Chapter 4 - program 2");
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

    public static void main(String[] args) {
        new Instancing().start();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL_COLOR_BUFFER_BIT);

        gl.glUseProgram(renderingProgram);

        vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
        pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

        aspect = (float) glWindow.getWidth() / (float) glWindow.getHeight();
        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

        vMat.setTranslation(-cameraX, -cameraY, -cameraZ);

        // use system time to generate slowly-increasing sequence of floating-point values
        // elapsedTime, startTime, and if would all be declared of type double
        // time factor = 1000 for 24 instances, 10000 for 100000 instances
        elapsedTime = System.currentTimeMillis() - startTime;
        timeFactor = elapsedTime / 10000.0;

        gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        // computations that build (and transform) mMat have been moved to the vertex shader
        // there is no longer any need to build an MV matrix in the Java/JOGL application
        gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals)); // the shaders does need the V matrix
        tfLoc = gl.glGetUniformLocation(renderingProgram, "tf");    // uniform for the time factor
        gl.glUniform1f(tfLoc, (float) timeFactor);                            // (the shader needs that too)


        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        // 0, 36, 24  when 24 instances
        // 0, 36, 100000 when 100000 instances
        gl.glDrawArraysInstanced(GL_TRIANGLES, 0, 36, 100000);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        startTime = System.currentTimeMillis();
        renderingProgram = Utils.createShaderProgram(
                        "ch4/shaders/vertShader_instancing.glsl",
                        "ch4/shaders/fragShader_interpolated.glsl"
        );
        setupVertices();
        // cameraZ = 25.0f for 24 instances
        // cameraZ = 420.0f for 100000 instances
        cameraX  = 0.0f;  cameraY  = 0.0f;  cameraZ  = 420.0f;
        cubeLocX = 0.0f; cubeLocY = -2.5f; cubeLocZ = 0.0f;
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

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}

