import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * ET Class to model a ET
 *
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class ET extends Model {

    private SGNode ETRoot;
    private GL3 gl;
    private TextureLibrary textures;
    private Light light1, light2;
    private SpotLight spotLight;
    private Camera camera;
    private Texture[] headTextures, bodyTextures, otherTextures;

    private float rockAngle = 0, rollAngle = 0, spinAllAngle = 0, facingAngle = 0, armsAngle = 60;
    TransformNode rockETNode, rollETNode, spinAllNode, facingNode, shakeHandsNode, rotateRightArm, rotateLeftArm;

    /* Constructor to initialize ET with necessary parameters */
    public ET(GL3 gl, TextureLibrary textures, Light l1, Light l2, SpotLight sl, Camera c,
              Texture[] ht, Texture[] bt, Texture[] ot) {
        this.gl = gl;
        this.textures = textures;
        this.light1 = l1;
        this.light2 = l2;
        this.spotLight = sl;
        this.camera = c;
        this.headTextures = ht;
        this.bodyTextures = bt;
        this.otherTextures = ot;
    }

    /* Method to create the ET model base a hierarchy scene graph */
    public SGNode makeET(Float tlx, Float tly, Float tlz) {
        ETRoot = new NameNode("ET structure");

        /* Creating sphere models for different body parts with specific textures */
        Model headSphere = makeSphere(gl, headTextures[0], headTextures[1]);
        Model bodySphere = makeSphere(gl, bodyTextures[0], bodyTextures[1]);
        Model otherSphere = makeSphere(gl, otherTextures[0], otherTextures[1]);

        /* Calculating dimensions for body parts */
        Float ETBodyDiameter = 3f;
        Float ETHeadDiameter = 2.5f;
        Float radioMastHeight = 0.5f;

        /* Creating nodes for different body parts */
        SGNode body = makePiece("body", bodySphere, ETBodyDiameter, ETBodyDiameter, ETBodyDiameter);
        SGNode head = makePiece("head", headSphere, ETHeadDiameter, ETHeadDiameter, ETHeadDiameter);
        SGNode leftArm = makePiece("leftArm", otherSphere, 0.6f, 1.4f, 0.6f);
        SGNode rightArm = makePiece("rightArm", otherSphere, 0.6f, 1.4f, 0.6f);
        SGNode leftEar = makePiece("leftEar", otherSphere, 0.3f, 1.4f, 0.6f);
        SGNode rightEar = makePiece("rightEar", otherSphere, 0.3f, 1.4f, 0.6f);
        SGNode leftEye = makePiece("leftEye", otherSphere, 0.5f, 0.5f, 0.5f);
        SGNode rightEye = makePiece("rightEye", otherSphere, 0.5f, 0.5f, 0.5f);
        SGNode radioMast = makePiece("radioMast", otherSphere, 0.1f, radioMastHeight, 0.2f);
        SGNode radioAntenna = makePiece("radioAntenna", otherSphere, 0.3f, 0.3f, 0.3f);

        TransformNode translateToBodyTop = new TransformNode("translate(0," + ETBodyDiameter + ",0)",
                Mat4Transform.translate(0, ETBodyDiameter, 0));

        TransformNode translateToLeftArm = new TransformNode("translate(" + ETBodyDiameter / 2 + "," +
                ETBodyDiameter / 2 + ",0)", Mat4Transform.translate(-ETBodyDiameter / 2 + 0.1f,
                ETBodyDiameter / 2, 0));
        rotateLeftArm = new TransformNode("rotateAroundZ(-60)", Mat4Transform.rotateAroundZ(armsAngle));

        TransformNode translateToRightArm = new TransformNode("translate(" + ETBodyDiameter / 2 + "," + ETBodyDiameter / 2 + ",0)",
                Mat4Transform.translate(ETBodyDiameter / 2 - 0.1f, ETBodyDiameter / 2, 0));
        rotateRightArm = new TransformNode("rotateAroundZ(-60)", Mat4Transform.rotateAroundZ(-armsAngle));

        TransformNode translateToLeftEar = new TransformNode("translate(" + ETHeadDiameter / 2 + "," + ETHeadDiameter / 2 + ",0)",
                Mat4Transform.translate(-ETHeadDiameter / 2 + 0.1f, ETHeadDiameter / 2, 0));

        TransformNode translateToRightEar = new TransformNode("translate(" + ETHeadDiameter / 2 + "," + ETHeadDiameter / 2 + ",0)",
                Mat4Transform.translate(ETHeadDiameter / 2 - 0.1f, ETHeadDiameter / 2, 0));

        TransformNode translateToLeftEye = new TransformNode("translate(" + ETHeadDiameter / 4 + "," + ETHeadDiameter / 4 + ",0)",
                Mat4Transform.translate(-ETHeadDiameter / 4, 3 * ETHeadDiameter / 5, 1f));

        TransformNode translateToRightEye = new TransformNode("translate(" + ETHeadDiameter / 4 + "," + ETHeadDiameter / 4 + ",0)",
                Mat4Transform.translate(ETHeadDiameter / 4, 3 * ETHeadDiameter / 5, 1f));

        TransformNode translateToRadioMast = new TransformNode("translate(0," + ETHeadDiameter + ",0)",
                Mat4Transform.translate(0, ETHeadDiameter, 0f));

        TransformNode translateToRadioAntenna = new TransformNode("translate(0," + radioMastHeight + ",0)",
                Mat4Transform.translate(0, radioMastHeight, 0f));

        TransformNode translateX = new TransformNode("translate(" + tlx + "," + tly + "," + tlz + ")", Mat4Transform.translate(tlx, tly, tlz));

        rockETNode = new TransformNode("rotateAroundZ(" + rockAngle + ")", Mat4Transform.rotateAroundZ(rockAngle));
        rollETNode = new TransformNode("rotateAroundZ(" + rollAngle + ")", Mat4Transform.rotateAroundZ(rollAngle));
        spinAllNode = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(spinAllAngle));
        facingNode = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(facingAngle));
        shakeHandsNode = new TransformNode("rotateAroundZ", Mat4Transform.rotateAroundZ(armsAngle));

        /* ET hierarchy */
        ETRoot.addChild(spinAllNode);
        spinAllNode.addChild(translateX);
        translateX.addChild(facingNode);
        facingNode.addChild(rockETNode);
        rockETNode.addChild(body);
        body.addChild(translateToLeftArm);
        translateToLeftArm.addChild(rotateLeftArm);
        rotateLeftArm.addChild(leftArm);

        body.addChild(translateToRightArm);
        translateToRightArm.addChild(rotateRightArm);
        rotateRightArm.addChild(rightArm);

        body.addChild(translateToBodyTop);
        translateToBodyTop.addChild(rollETNode);
        rollETNode.addChild(head);

        head.addChild(translateToLeftEar);
        translateToLeftEar.addChild(leftEar);

        head.addChild(translateToRightEar);
        translateToRightEar.addChild(rightEar);

        head.addChild(translateToLeftEye);
        translateToLeftEye.addChild(leftEye);

        head.addChild(translateToRightEye);
        translateToRightEye.addChild(rightEye);

        head.addChild(translateToRadioMast);
        translateToRadioMast.addChild(radioMast);
        radioMast.addChild(translateToRadioAntenna);
        translateToRadioAntenna.addChild(radioAntenna);

        ETRoot.update();
        return ETRoot;
    }

    /**
     * Creates a sphere Model object with specified parameters.
     *
     * @param gl       The GL3 object for rendering.
     * @param diffuse  The diffuse texture for the sphere.
     * @param specular The specular texture for the sphere.
     * @return The created sphere Model object.
     */
    private Model makeSphere(GL3 gl, Texture diffuse, Texture specular) {
        String name = "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "vertexShaders/vs_standard.txt", "fragmentShaders/fs_standard_2t.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 99.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4, 4, 4), Mat4Transform.translate(0, 0.5f, 0));
        Model sphere = new Model(name, mesh, modelMatrix, shader, material, light1, light2, spotLight, camera, diffuse, specular);
        return sphere;
    }

    /**
     * Creates a Scene Graph Node for a piece with specified parameters.
     *
     * @param n      The name of the piece.
     * @param sphere The sphere Model to be used as the piece.
     * @param sx     The scaling factor along the x-axis.
     * @param sy     The scaling factor along the y-axis.
     * @param sz     The scaling factor along the z-axis.
     * @return The created Scene Graph Node for the piece.
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

    /**
     * Sets the facing angle of an object.
     *
     * @param angle The angle to set for the object's facing direction.
     */
    public void setFacingAngle(float angle) {
        facingNode.setTransform(Mat4Transform.rotateAroundY(angle));
        ETRoot.update();
    }

    /**
     * Performs a shaking hands animation based on elapsed time.
     *
     * @param elapsedTime The time elapsed for the animation.
     */
    public void shakeHands(double elapsedTime){
        float angleRange = 30.0f;
        float minAngle = 30.0f;
        float normalizedTime = (float) Math.sin(elapsedTime*5);
        armsAngle = minAngle + (normalizedTime + 1.0f) * 0.5f * angleRange;

        rotateLeftArm.setTransform(Mat4Transform.rotateAroundX(armsAngle));
        rotateRightArm.setTransform(Mat4Transform.rotateAroundX(armsAngle));
        ETRoot.update();
    }

    /**
     * Performs a spinning animation for the entire scene graph.
     */
    public void spinET() {
        spinAllAngle += 0.5f;
        spinAllNode.setTransform(Mat4Transform.rotateAroundY(spinAllAngle));
        ETRoot.update();
    }

    /**
     * Sets the angle for spinning the entire scene graph.
     *
     * @param spinAllAngle The angle for spinning the entire scene graph.
     */
    public void setSpinAllAngle(float spinAllAngle) {
        this.spinAllAngle = spinAllAngle;
        spinAllNode.setTransform(Mat4Transform.rotateAroundY(spinAllAngle));
        ETRoot.update();
    }

    /**
     * Sets the angle for the arms.
     *
     * @param armsAngle The angle for the arms.
     */
    public void setArmsAngle(float armsAngle) {
        this.armsAngle = armsAngle;
        rotateLeftArm.setTransform(Mat4Transform.rotateAroundZ(armsAngle));
        rotateRightArm.setTransform(Mat4Transform.rotateAroundZ(-armsAngle));
    }

    /**
     * Performs a rocking animation for ET
     *
     * @param elapsedTime The time elapsed for the animation.
     * @param angle       The angle for rocking the object.
     */
    public void rockET(double elapsedTime, int angle) {
        rockAngle = angle * (float) Math.sin(elapsedTime);
        rockETNode.setTransform(Mat4Transform.rotateAroundZ(rockAngle));
        ETRoot.update();
    }

    /**
     * Performs a rolling animation for ET
     *
     * @param elapsedTime The time elapsed for the animation.
     * @param angle       The angle for rolling the object.
     */
    public void rollET(double elapsedTime, int angle) {
        rollAngle = angle * (float) Math.sin(elapsedTime);
        rollETNode.setTransform(Mat4Transform.rotateAroundZ(rollAngle));
        ETRoot.update();
    }
}
