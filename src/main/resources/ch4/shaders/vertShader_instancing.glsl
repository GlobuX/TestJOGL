#version 430
layout (location = 0) in vec3 position;

uniform mat4 v_matrix; // only the P and V matrices are sent from the application
uniform mat4 p_matrix;
uniform float tf; // time factor  for animation and placement of cubes

out vec4 varyingColor;

mat4 buildRotateX(float rad); // declaration of matrix transformation utility functions
mat4 buildRotateY(float rad); // (GLSL requires functions to be declared prior to invocation
mat4 buildRotateZ(float rad);
mat4 buildTranslate(float x, float y, float z);

void main(void)
{
    float x = gl_InstanceID + tf; // value based on time factor, but different for each cube instance
    float a = sin(203.0 * x / 8000.0) * 403.0; // these are the x, y, and z components for the translation, below
    float b = sin(301.0 * x / 4001.0) * 401.0;
    float c = sin(400.0 * x / 6003.0) * 405.0;

    //	float a = sin(.35 * x) * 8.0;			// when 24 instances
    //	float b = cos(.52 * x) * 8.0;
    //	float c = sin(.70 * x) * 8.0;

    // build the rotation and translation matrices to be applied to this cube's model matrix
    mat4 localRotX = buildRotateX(1.75 * x);
    mat4 localRotY = buildRotateY(1.75 * x);
    mat4 localRotZ = buildRotateZ(1.75 * x);
    mat4 localTrans = buildTranslate(a, b, c);

    // build the model matrix and then the model-view matrix
    mat4 newM_matrix = localTrans * localRotX * localRotY * localRotZ;
    mat4 mv_matrix = v_matrix * newM_matrix;

    gl_Position = p_matrix * mv_matrix * vec4(position, 1.0);
    varyingColor = vec4(position, 1.0) * 0.5 + vec4(0.5, 0.5, 0.5, 0.5);
}

    // utility function to build a translation matrix (from Chapter3)
mat4 buildTranslate(float x, float y, float z) {
    mat4 trans = mat4(
                 1.0, 0.0, 0.0, 0.0,
                 0.0, 1.0, 0.0, 0.0,
                 0.0, 0.0, 1.0, 0.0,
                 x,   y,   z,   1.0
                 );
    return trans;
}

// similar functions included for rotation around the X, Y, and Z axes (also from Chapter3)

    // build and return a matrix that performs a rotation around the X axis
mat4 buildRotateX(float rad) {
    mat4 xrot = mat4(
                1.0, 0.0,      0.0,       0.0,
                0.0, cos(rad), -sin(rad), 0.0,
                0.0, sin(rad), cos(rad),  0.0,
                0.0, 0.0,      0.0,       1.0
                );
    return xrot;
}

    // build and return a matrix that performs a rotation around the Y axis
mat4 buildRotateY(float rad) {
    mat4 yrot = mat4(
                cos(rad),  0.0, sin(rad), 0.0,
                0.0,       1.0, 0.0,      0.0,
                -sin(rad), 0.0, cos(rad), 0.0,
                0.0,       0.0, 0.0,      1.0
                );
    return yrot;
}

    // build and rturn a matrix that performs a rotation around the Z axis
mat4 buildRotateZ(float rad) {
    mat4 zrot = mat4(
                cos(rad),  sin(rad), 0.0, 0.0,
                -sin(rad), cos(rad), 0.0, 0.0,
                0.0,       0.0,      1.0, 0.0,
                0.0,       0.0,      0.0, 1.0
                );
    return zrot;
}