import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

import java.awt.*;
/**
 * This Class to build a Spotlight
 *
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class SpotLight {

    private SGNode SpotLightRoot;
    private GL3 gl;
    private TextureLibrary textures;
    private Light light1, light2;
    private SpotLight sl;
    private Camera camera;
    private Shader shader;
    private Model lampModel;
    private Material lampMaterial;
    private TransformNode rotateAll, translateToPoleTop, translateX;
    private SGNode lamp,lampshade,pole;
    private float ambient_strength, specular_strength, diffuse_strength;

    /** spotLight constructor */
    public SpotLight(GL3 gl, TextureLibrary textures, Light l1,Light l2,SpotLight sl, Camera c) {
        this.gl = gl;
        this.textures = textures;
        this.light1 = l1;
        this.light2 = l2;
        this.sl = sl;
        this.camera = c;
        this.lampModel = makeLamp(gl);
        this.ambient_strength = 0.3f;
        this.specular_strength = 5.0f;
        this.diffuse_strength = 0.5f;
    }

    private float rotateAllAngleStart = 25, rotateAllAngle = rotateAllAngleStart;

    /** Modelling SpotLight */
    public SGNode makeSpotLight(Float tlx, Float tly, Float tlz) {
        SpotLightRoot = new NameNode("SpotLight structure");
        Model sphere = makeSphere(gl, textures);

        Float poleHeight = 13f;
        pole = makePiece("pole", sphere, 0.5f, poleHeight, -2f);
        Float lampshadeWidth = 1.5f;
        Float lampshadeHeight = 0.8f;
        lampshade = makePiece("lampshade", sphere, lampshadeWidth, lampshadeHeight, 0f);
        lamp = makePiece("lamp", lampModel, 0.5f, 0.5f, 0.5f);

        translateToPoleTop = new TransformNode("Translate To Pole Top(0," + poleHeight + ",0)",
                Mat4Transform.translate(0, poleHeight - 0.1f, 0));
        TransformNode rotateLampShade = new TransformNode("Rotate Lamp Shade(-75)", Mat4Transform.rotateAroundZ(-75));

        TransformNode translateToRightLaneShade = new TransformNode("translate(" + lampshadeWidth / 2 + "," + lampshadeHeight / 2 + ",0)",
                Mat4Transform.translate(lampshadeWidth / 2, lampshadeHeight / 4, 0));

        rotateAll = new TransformNode("Spin ALL(" + rotateAllAngle + ")", Mat4Transform.rotateAroundZ(rotateAllAngle));

        translateX = new TransformNode("Translate Root(" + tlx + "," + tly + "," + tlz + ")", Mat4Transform.translate(tlx, tly, tlz));

        /* Spotlight scene graph hierarchy*/
        SpotLightRoot.addChild(translateX);
        translateX.addChild(pole);
        pole.addChild(translateToPoleTop);
        translateToPoleTop.addChild(rotateAll);
        rotateAll.addChild(rotateLampShade);
        rotateLampShade.addChild(lampshade);
        lampshade.addChild(translateToRightLaneShade);
        translateToRightLaneShade.addChild(lamp);

        SpotLightRoot.update();
        return SpotLightRoot;
    }

    /**
    * Method to make a basic Sphere model
     */
    private Model makeSphere(GL3 gl, TextureLibrary textures) {
        String name = "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "vertexShaders/vs_standard.txt", "fragmentShaders/fs_standard_2t.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4, 4, 4), Mat4Transform.translate(0, 0.5f, 0));
        Model sphere = new Model(name, mesh, modelMatrix, shader, material, light1, light2, sl, camera, textures.get("Jade"),textures.get("Jade_specular"));
        return sphere;
    }

    /**
     * Method to make a lamp model
     */
    private Model makeLamp(GL3 gl) {
        String name = "lamp";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        shader = new Shader(gl, "vertexShaders/vs_spotlight.txt", "fragmentShaders/fs_spotlight.txt");
        lampMaterial = defaultMaterial();
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(1f, 1f, 1f), Mat4Transform.translate(0, 0.5f, 0));
        lampModel = new Model(name, mesh, modelMatrix, shader, lampMaterial, light1, camera);
        return lampModel;
    }

    /**
     * Method to make pieces model for spotlight
     */
    private SGNode makePiece(String n, Model sphere, float sx, float sy, float sz) {
        NameNode nodeName = new NameNode(n);
        Mat4 m = Mat4Transform.scale(sx, sy, sx);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode transformName = new TransformNode("scale(" + sx + "," + sy + "," + sz + "); translate(0,0.5,0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
        nodeName.addChild(transformName);
        transformName.addChild(sphereNode);
        return nodeName;
    }

    public void setSpotLightColor(GL3 gl, Float color){
        shader.use(gl);
        shader.setFloat(gl,"lampColor",color);
        lampModel.setShader(shader);
    }
    public Material defaultMaterial(){
        lampMaterial = new Material();
        lampMaterial.setAmbient(0.3f, 0.3f, 0.3f);
        lampMaterial.setDiffuse(0.5f, 0.5f, 0.5f);
        lampMaterial.setSpecular(0.7f, 0.7f, 0.7f);
        return lampMaterial;
    }

    public void setLampMaterial(Material lampMaterial) {this.lampMaterial = lampMaterial;}
    public Material getLampMaterial() {
        return lampMaterial;
    }

    /**
     * Initiates or stops a spinning animation based on the provided status.
     *
     * @param status If true, initiates the spinning animation by updating the transformation.
     *               If false, stops the spinning animation.
     */
    public void goSpin(boolean status) {
        if (status){
            rotateAllAngle = rotateAllAngle - 0.5f;
            rotateAll.setTransform(Mat4Transform.rotateAroundY(rotateAllAngle));
        }
        SpotLightRoot.update();
    }

    /**
     * Retrieves the world position of the lamp in 3D space.
     *
     * @return The world position of the lamp as a Vec3 object.
     */
    public Vec3 getLampWorldPosition() {
        float[] matrixArray = lamp.getLocalTransform().toFloatArrayForGLSL();
        float x = matrixArray[12];
        float y = matrixArray[13];
        float z = matrixArray[14];
        return new Vec3(x, y, z);
    }

    /**
     * Retrieves the world position of the lampshade tip in 3D space.
     *
     * @return The world position of the lampshade tip as a Vec3 object.
     */
    public Vec3 getTipWorldPosition() {
        float[] lampshadeMatrix = lampshade.getLocalTransform().toFloatArrayForGLSL();
        float x = lampshadeMatrix[12];
        float y = lampshadeMatrix[13];
        float z = lampshadeMatrix[14];
        return new Vec3(x, y, z);
    }

    /**
     * Retrieves the direction of the light from the lamp to the lampshade tip in 3D space.
     *
     * @return The normalized direction of the light as a Vec3 object.
     */
    public Vec3 getLightDirection() {
        Vec3 lamp = getLampWorldPosition();
        Vec3 tip = getTipWorldPosition();
        lamp.subtract(tip);
        lamp.normalize();
        return lamp;
    }

    public void setAmbient_strength(float as) {ambient_strength = as;}
    public void setDiffuse_strength(float ds) {diffuse_strength = ds;}
    public void setSpecular_strength(float ss) {specular_strength = ss;}
    public float getAmbient_strength() {return ambient_strength;}
    public float getDiffuse_strength() {return diffuse_strength;}
    public float getSpecular_strength() {return specular_strength;}
}

