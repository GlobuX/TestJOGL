package ru.globux.testjogl.ch4;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import ru.globux.testjogl.util.Utils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.*;

public class TexturedMatrixStack extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private double startTime = 0.0;
    private double elapsedTime;
    private int renderingProgram;
    private int vao[] = new int[1];
    private int vbo[] = new int[6];
    private float cameraX, cameraY, cameraZ;
    private int texSun;
    private int texPlanet;
    private int texMoon;

    // allocate variables for display() function
    private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
    private Matrix4fStack mvStack = new Matrix4fStack(5);
    private Matrix4f pMat = new Matrix4f();
    private int mvLoc, pLoc;
    private float aspect;
    private double tf;

    public TexturedMatrixStack() {
        setTitle("Chapter 4 - program 6");
        setSize(1024, 600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        this.add(myCanvas);
        this.setVisible(true);
        Animator animator = new Animator(myCanvas);
        animator.start();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                animator.stop();
                dispose();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new TexturedMatrixStack();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClearColor(0.0f, 0.0f, 0.2f, 1.0f);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        elapsedTime = System.currentTimeMillis() - startTime;

        gl.glUseProgram(renderingProgram);

        mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
        pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        // push view matrix onto the stack
        mvStack.pushMatrix();

        mvStack.translate(-cameraX, -cameraY, -cameraZ);
//        mvStack.setLookAt(0.0f, 0.0f, 12.0f,0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f);

        tf = elapsedTime / 1000.0;  // time factor

        // ----------------------  Diamond == sun
        mvStack.pushMatrix();
        mvStack.translate(0.0f, 0.0f, 0.0f);
        mvStack.pushMatrix();
        mvStack.rotate((float) tf, 1.0f, 0.0f, 0.0f);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texSun);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, 24);
        mvStack.popMatrix();

        //-----------------------  cube == planet
        mvStack.pushMatrix();
        mvStack.translate((float) Math.sin(tf) * 6.0f, 0.0f, (float) Math.cos(tf) * 6.0f);
        mvStack.pushMatrix();
        mvStack.rotate((float) tf * 5, 0.0f, 1.0f, 0.0f);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texPlanet);

        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();

        //-----------------------  smaller cube == moon1
        mvStack.pushMatrix();
        mvStack.translate((float) Math.sin(tf * 8) * 3.0f, (float) Math.sin(tf * 8) * 3.0f, (float) Math.cos(tf * 8) * 3.0f);
//        mvStack.translate(0.0f, (float) Math.sin(tf) * 2.0f, (float) Math.cos(tf) * 2.0f);
        mvStack.rotate((float) tf, 0.0f, 0.0f, 1.0f);
        mvStack.scale(0.25f, 0.25f, 0.25f);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texPlanet);

        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();

        //-----------------------  smaller pyramid == moon2
        mvStack.pushMatrix();
        mvStack.translate((float) Math.sin(tf * 4) * 5.0f, (float) Math.sin(tf * 4) * 5.0f, (float) Math.cos(tf * 4) * 5.0f);
//        mvStack.translate(0.0f, (float) Math.sin(tf) * 2.0f, (float) Math.cos(tf) * 2.0f);
        mvStack.rotate((float) tf, 0.0f, 0.0f, 1.0f);
        mvStack.scale(0.25f, 0.25f, 0.25f);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, texMoon);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, 18);

        mvStack.clear();
//        mvStack.popMatrix();
//        mvStack.popMatrix();
//        mvStack.popMatrix();
//        mvStack.popMatrix();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

        startTime = System.currentTimeMillis();
        renderingProgram = Utils.createShaderProgram(
                "ch4/shaders/vertShader_textured.glsl",
                "ch4/shaders/fragShader_textured.glsl"
        );
        setupVertices();
        cameraX = 0.0f; cameraY = 0.0f; cameraZ = 14.0f;

        texSun = Utils.loadTexture("ch4/textures/sun.jpg");
        texPlanet = Utils.loadTexture("ch4/textures/green.jpg");
        texMoon = Utils.loadTexture("ch4/textures/darkgreen.jpg");
    }

    private void setupVertices() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] cubePositions = {
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
        float[] cubeCoordinates = {
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f
        };

        float[] pyramidPositions = {
                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,    //front
                1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,    //right
                1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,  //back
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,  //left
                -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
                1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f  //RR
        };
        float[] pyrTextureCoordinates = {
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //front
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //right
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //back
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //left
                0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,  //LF
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f   //RR
        };

        float[] diamondPositions = {
                -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 0.0f, 2.0f, 0.0f,
                1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 2.0f, 0.0f,
                1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 2.0f, 0.0f,
                -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 0.0f, 2.0f, 0.0f,
                -1.0f, 0.0f, -1.0f, 0.0f, -2.0f, 0.0f, 1.0f, 0.0f, -1.0f,
                1.0f, 0.0f, -1.0f, 0.0f, -2.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 0.0f, -2.0f, 0.0f, -1.0f, 0.0f, 1.0f,
                -1.0f, 0.0f, 1.0f, 0.0f, -2.0f, 0.0f, -1.0f, 0.0f, -1.0f
        };
        float[] diamondCoordinates = {
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //front
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //right
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //back
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,  //left
                1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f
        };

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer diamBuf = Buffers.newDirectFloatBuffer(diamondPositions);
        gl.glBufferData(GL_ARRAY_BUFFER, diamBuf.limit() * 4, diamBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer texDiamBuf = Buffers.newDirectFloatBuffer(pyrTextureCoordinates);
        gl.glBufferData(GL_ARRAY_BUFFER, texDiamBuf.limit() * 4, texDiamBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cubePositions);
        gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit() * 4, cubeBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        FloatBuffer texCubeBuf = Buffers.newDirectFloatBuffer(pyrTextureCoordinates);
        gl.glBufferData(GL_ARRAY_BUFFER, texCubeBuf.limit() * 4, texCubeBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pyramidPositions);
        gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit() * 4, pyrBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer texPyrBuf = Buffers.newDirectFloatBuffer(pyrTextureCoordinates);
        gl.glBufferData(GL_ARRAY_BUFFER, texPyrBuf.limit() * 4, texPyrBuf, GL_STATIC_DRAW);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}
