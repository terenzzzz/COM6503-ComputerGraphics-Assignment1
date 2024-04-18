import com.jogamp.common.util.IOUtil;
import gmaths.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

import javax.imageio.ImageIO;

import static com.jogamp.opengl.GL.*;
/**
 * This Class to build a SkyBox
 *
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class SkyBoxModel {

    private String name;
    private Mesh mesh;
    private Mat4 modelMatrix;
    private Shader shader;
    private Material material;
    private Camera camera;
    private TextureLibrary textures;
    private int cubeMapTextureID;
    private GL3 gl;


    /* Constructor */
    public SkyBoxModel(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Camera camera,
                       TextureLibrary textures, GL3 gl ) {
        this.name = name;
        this.mesh = mesh;
        this.modelMatrix = modelMatrix;
        this.shader = shader;
        this.material = material;
        this.camera = camera;
        this.textures = textures;
        this.gl = gl;

        /* List of image file paths for the skybox cube mapping */
        List<String> faces = new ArrayList<>();
        faces.add("textures/right.jpg");
        faces.add("textures/left.jpg");
        faces.add("textures/bottom.jpg");
        faces.add("textures/top.jpg");
        faces.add("textures/front.jpg");
        faces.add("textures/back.jpg");
        this.cubeMapTextureID = loadCubemap(faces, gl);
    }

    public void setModelMatrix(Mat4 m) {
        modelMatrix = m;
    }
    public void render(GL3 gl) {
        render(gl, modelMatrix);
    }
    public void render(GL3 gl, Mat4 modelMatrix) {
        if (mesh_null()) {
            System.out.println("Error: null in model render");
            return;
        }

        /* Remove any translation */
        Mat4 modifiedViewMatrix = new Mat4(camera.getViewMatrix());
        modifiedViewMatrix.removeTranslation();
        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(modifiedViewMatrix, modelMatrix));

        /* Send data to shader */
        shader.use(gl);
        shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
        shader.setVec3(gl, "viewPos", camera.getPosition());
        shader.setInt(gl, "cubeMapTexture", 0);
        /* render the mesh */
        mesh.render(gl);
    }

    /**
     * Loads a cubemap texture using the provided image files for the different faces of the cube.
     *
     * @param faces List of file paths for each face of the cubemap.
     * @param gl    The GL3 object for rendering.
     * @return The ID value of the loaded cubemap texture.
     */
    public static int loadCubemap(List<String> faces,GL3 gl) {
        /* Create cube map */
        int[] textureID = new int[1];
        gl.glGenTextures(1, textureID, 0);
        int textureIDValue = textureID[0];
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, textureIDValue);

        for (int i = 0; i < faces.size(); i++) {
            try {
                TextureData textureData = TextureIO.newTextureData(gl.getGLProfile(),
                        new File(faces.get(i)), false, null);

                /* Bind texture to the corresponding face of the cube map */
                gl.glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                        0, GL_RGB, textureData.getWidth(), textureData.getHeight(), 0,
                        GL_RGB, GL_UNSIGNED_BYTE, textureData.getBuffer());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Set cube map texture parameters */
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);

        return textureIDValue;
    }
    private boolean mesh_null() {
        return (mesh == null);
    }

}