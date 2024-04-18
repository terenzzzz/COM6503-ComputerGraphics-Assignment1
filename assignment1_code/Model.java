import gmaths.*;

import java.nio.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;
/**
 * Model Class
 * Modified from the Lab Material
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class Model {

    private String name;
    private Mesh mesh;
    private Mat4 modelMatrix;
    private Shader shader;
    private Material material;
    private Camera camera;
    private Light light1, light2;
    private SpotLight sl;
    private Texture diffuse;
    private Texture specular;

    /**
     * Constructors with different params
     */
    public Model() {
        name = null;
        mesh = null;
        modelMatrix = null;
        material = null;
        camera = null;
        light1 = null;
        light2 = null;
        shader = null;
    }

    /* Two Light Two Texture */
    public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light light1, Light light2, Camera camera, Texture diffuse, Texture specular) {
        this.name = name;
        this.mesh = mesh;
        this.modelMatrix = modelMatrix;
        this.shader = shader;
        this.material = material;
        this.light1 = light1;
        this.light2 = light2;
        this.camera = camera;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    /* Three Lights Two Texture */
    public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light light1, Light light2, SpotLight sl, Camera camera, Texture diffuse, Texture specular) {
        this.name = name;
        this.mesh = mesh;
        this.modelMatrix = modelMatrix;
        this.shader = shader;
        this.material = material;
        this.light1 = light1;
        this.light2 = light2;
        this.sl = sl;
        this.camera = camera;
        this.diffuse = diffuse;
        this.specular = specular;
    }


    /* One Light Zero Texture */
    public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light light, Camera camera) {
        this(name, mesh, modelMatrix, shader, material, light, null, camera, null, null);
    }

    public void setModelMatrix(Mat4 m) {
        modelMatrix = m;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    /* Backdrop render */
    public void render(GL3 gl, float offsetY1, float offsetY2) {
        shader.use(gl);
        shader.setFloat(gl, "offset1", 0f, offsetY1);
        shader.setFloat(gl, "offset2", 0f, offsetY2);

        lightConfig(gl);

        if (diffuse != null) {
            shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
            gl.glActiveTexture(GL.GL_TEXTURE0);
            diffuse.bind(gl);
        }
        if (specular != null) {
            shader.setInt(gl, "second_texture", 1);
            gl.glActiveTexture(GL.GL_TEXTURE1);
            specular.bind(gl);
        }
        mesh.render(gl);
        render(gl, modelMatrix);
    }

    public void render(GL3 gl) {
        render(gl, modelMatrix);
    }

    /* Stander model rendering */
    public void render(GL3 gl, Mat4 modelMatrix) {
        if (mesh_null()) {
            System.out.println("Error: null in model render");
            return;
        }

        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));

        shader.use(gl);
        shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
        shader.setVec3(gl, "viewPos", camera.getPosition());

        lightConfig(gl);

        shader.setVec3(gl, "material.ambient", material.getAmbient());
        shader.setVec3(gl, "material.diffuse", material.getDiffuse());
        shader.setVec3(gl, "material.specular", material.getSpecular());
        shader.setFloat(gl, "material.shininess", material.getShininess());

        if (diffuse != null) {
            shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
            gl.glActiveTexture(GL.GL_TEXTURE0);
            diffuse.bind(gl);
        }
        if (specular != null) {
            shader.setInt(gl, "second_texture", 1);
            gl.glActiveTexture(GL.GL_TEXTURE1);
            specular.bind(gl);
        }
        // then render the mesh
        mesh.render(gl);
    }

    /**
     * Configures lighting parameters for the graphics rendering using OpenGL (GL3).
     * This method sets up properties for light sources (if available) in the shader.
     *
     * @param gl The GL3 object for rendering.
     */
    private void lightConfig(GL3 gl) {
        if (light1 != null) {
            shader.setVec3(gl, "light1.position", light1.getPosition());
            shader.setVec3(gl, "light1.ambient", light1.getMaterial().getAmbient());
            shader.setVec3(gl, "light1.diffuse", light1.getMaterial().getDiffuse());
            shader.setVec3(gl, "light1.specular", light1.getMaterial().getSpecular());
            shader.setFloat(gl, "light1.ambient_strength", light1.getAmbient_strength());
            shader.setFloat(gl, "light1.specular_strength", light1.getSpecular_strength());
            shader.setFloat(gl, "light1.diffuse_strength", light1.getDiffuse_strength());
        }

        if (light2 != null) {
            shader.setVec3(gl, "light2.position", light2.getPosition());
            shader.setVec3(gl, "light2.ambient", light2.getMaterial().getAmbient());
            shader.setVec3(gl, "light2.diffuse", light2.getMaterial().getDiffuse());
            shader.setVec3(gl, "light2.specular", light2.getMaterial().getSpecular());
            shader.setFloat(gl, "light2.ambient_strength", light2.getAmbient_strength());
            shader.setFloat(gl, "light2.specular_strength", light2.getSpecular_strength());
            shader.setFloat(gl, "light2.diffuse_strength", light2.getDiffuse_strength());
        }

        if (sl != null) {
            Vec3 direction = sl.getLightDirection();
            float cutOff = (float) Math.cos(Math.toRadians(12.5f));
            float outerCutOff = (float) Math.cos(Math.toRadians(17.5f));

            shader.setVec3(gl, "sl.position", sl.getLampWorldPosition());
            shader.setVec3(gl, "sl.direction", direction);
            shader.setFloat(gl, "sl.cutOff", cutOff);
            shader.setFloat(gl, "sl.outerCutOff", outerCutOff);

            shader.setVec3(gl, "sl.ambient", sl.getLampMaterial().getAmbient());
            shader.setVec3(gl, "sl.diffuse", sl.getLampMaterial().getDiffuse());
            shader.setVec3(gl, "sl.specular", sl.getLampMaterial().getSpecular());
            shader.setFloat(gl, "sl.ambient_strength", sl.getAmbient_strength());
            shader.setFloat(gl, "sl.specular_strength", sl.getSpecular_strength());
            shader.setFloat(gl, "sl.diffuse_strength", sl.getDiffuse_strength());

        }
    }


    private boolean mesh_null() {
        return (mesh == null);
    }
    public void dispose(GL3 gl) {
        mesh.dispose(gl);  // only need to dispose of mesh
    }

}