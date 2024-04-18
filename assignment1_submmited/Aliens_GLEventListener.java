
import com.jogamp.opengl.util.texture.Texture;
import gmaths.*;

import com.jogamp.opengl.*;

/**
 * Aliens_GLEventListener Class implements GLEventListener Class
 * Modified from the Lab Material
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class Aliens_GLEventListener implements GLEventListener {
    private Camera camera;
    private GL3 gl;

    /** Constructor */
    public Aliens_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(0f, 15f, 30f));
        this.camera.setTarget(new Vec3(0f, 7f, 0f));
    }

    /** Initialisation */
    public void init(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL3();
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
        gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
        gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
        initialise(gl);
        startTime = getSeconds();
    }

    /** Called to indicate the drawing surface has been moved and/or resized  */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        float aspect = (float) width / (float) height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    /** Draw */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    /** Clean up memory, if necessary */
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        backDrop.dispose(gl);
        ground.dispose(gl);
        light1.dispose(gl);
        light2.dispose(gl);
    }

    /** THE SCENE*/
    private TextureLibrary textures;
    private final Vec3 baseColor = new Vec3(0.5f, 0.5f, 0.5f);
    private Model backDrop, ground;
    private SkyBoxModel skyBox;
    private Light light1, light2;
    private SpotLight sl;
    private ET et1, et2;
    private SGNode etRoot1, etRoot2, spotLight;
    private Shader backdropShader, groundShader;
    private Mesh backdropMesh, groundMesh;
    private Material backdropMaterial, groundMaterial;
    private Float light1Color = 1.0f, light2Color = 1.0f, spotLightColor = 1.0f;
    private int rockAngle = 0, rollAngle = 0;
    private Boolean isCheering = false, isSlSpin = true;
    private double elapsedTime, slowElapsedTime;

    /** Initialise the scene */
    public void initialise(GL3 gl) {
        System.out.println("Initialising ... ");
        System.out.println("Please Wait Around 5-10 sec ... ");
        textures = new TextureLibrary();
        /* Adding sky box textures */
        textures.add(gl, "left", "textures/left.jpg");
        textures.add(gl, "front", "textures/front.jpg");
        textures.add(gl, "right", "textures/right.jpg");
        textures.add(gl, "back", "textures/back.jpg");
        textures.add(gl, "top", "textures/top.jpg");
        textures.add(gl, "bottom", "textures/bottom.jpg");

        /* Adding background textures */
        textures.add(gl, "backdrop", "textures/backdrop.jpg");
        textures.add(gl, "ground", "textures/ground.png");
        textures.add(gl, "snowflake", "textures/snowflake1.png");

        /* Adding ET textures */
        textures.add(gl, "Jade", "textures/Jade.jpg");
        textures.add(gl, "Jade_specular", "textures/Jade_specular.jpg");
        textures.add(gl, "GroundForest", "textures/GroundForest.jpg");
        textures.add(gl, "GroundForest_specular", "textures/GroundForest_specular.jpg");
        textures.add(gl, "Marble", "textures/Marble.jpg");
        textures.add(gl, "Marble_specular", "textures/Marble_specular.jpg");
        textures.add(gl, "WallMedieval", "textures/WallMedieval.jpg");
        textures.add(gl, "WallMedieval_specular", "textures/WallMedieval_specular.jpg");
        textures.add(gl, "Plaster", "textures/Plaster.jpg");
        textures.add(gl, "Plaster_specular", "textures/Plaster_specular.jpg");
        textures.add(gl, "IceBlock", "textures/IceBlock.jpg");

        /* Initialise Globle Light1 Model */
        light1 = new Light(gl);
        light1.setPosition(new Vec3(-10, 12, 6));  // changing light position each frame
        light1.setCamera(camera);
        light1.setLight(gl, light1Color);

        /* Initialise Globle Light2 Model */
        light2 = new Light(gl);
        light2.setPosition(new Vec3(10, 12, 6));  // changing light position each frame
        light2.setCamera(camera);
        light2.setLight(gl, light2Color);

        /* Initialise SpotLight Model*/
        sl = new SpotLight(gl, textures, light1, light2, sl, camera);
        spotLight = sl.makeSpotLight(-8f, 0f, 3f);

        /* Initialise ground Model */
        groundMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        groundShader = new Shader(gl, "vertexShaders/vs_backdrop.txt", "fragmentShaders/fs_backdrop_2t.txt");
        groundMaterial = new Material(baseColor, baseColor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
        ground = new Model("Ground", groundMesh, new Mat4(1), groundShader, groundMaterial, light1, light2, sl, camera, textures.get("ground"), textures.get("ground"));

        /* Initialise backdrop Model */
        backdropMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        backdropShader = new Shader(gl, "vertexShaders/vs_backdrop.txt", "fragmentShaders/fs_backdrop_2t.txt");
        backdropMaterial = new Material(baseColor, baseColor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
        backDrop = new Model("backDrop", backdropMesh, new Mat4(1), backdropShader, backdropMaterial, light1, light2, sl, camera, textures.get("backdrop"), textures.get("snowflake"));

        /* Initialise ET1 Model */
        Texture[] et1HeadTextures = {textures.get("GroundForest"), textures.get("GroundForest_specular")};
        Texture[] et1BodyTextures = {textures.get("Plaster"), textures.get("Plaster_specular")};
        Texture[] et1OtherTextures = {textures.get("WallMedieval"), textures.get("WallMedieval_specular")};
        et1 = new ET(gl, textures, light1, light2, sl, camera, et1HeadTextures, et1BodyTextures, et1OtherTextures);
        etRoot1 = et1.makeET(-3f, 0f, 2f);

        /* Initialise ET2 Model */
        Texture[] et2HeadTextures = {textures.get("IceBlock"), textures.get("IceBlock")};
        Texture[] et2BodyTextures = {textures.get("Marble"), textures.get("Marble_specular")};
        Texture[] et2OtherTextures = {textures.get("Jade"), textures.get("Jade_specular")};
        et2 = new ET(gl, textures, light1, light2, sl, camera, et2HeadTextures, et2BodyTextures, et2OtherTextures);
        etRoot2 = et2.makeET(3f, 0f, 2f);

        /* Initialise skybox Model */
        Mesh skyBoxMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Shader skyBoxShader = new Shader(gl, "vertexShaders/vs_skybox.txt", "fragmentShaders/fs_skybox.txt");
        Material skyBoxMaterial = new Material(baseColor, baseColor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
        skyBox = new SkyBoxModel("skyBox", skyBoxMesh, new Mat4(1), skyBoxShader, skyBoxMaterial, camera, textures, gl);
        System.out.println("Initialise Finished. Rendering Frame...");
    }

    /**
     * Render the scene
     */
    public void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL.GL_CULL_FACE);

        elapsedTime = (getSeconds() - startTime);
        slowElapsedTime = elapsedTime * 0.1;

        skyBox.setModelMatrix(getMforSkyBox());
        skyBox.render(gl);

        light1.setLight(gl, light1Color);
        light1.render(gl);

        light2.setLight(gl, light2Color);
        light2.render(gl);

        float offsetY1 = (float) (slowElapsedTime - Math.floor(slowElapsedTime));
        float offsetY2 = (float) (slowElapsedTime - Math.floor(slowElapsedTime) - 1.0f);
        backDrop.setModelMatrix(getMforBackdrop());
        backDrop.render(gl, offsetY1, offsetY2);

        ground.setModelMatrix(getMforGround());
        ground.render(gl);

        et1.rockET(elapsedTime,rockAngle);
        et2.rockET(elapsedTime,rockAngle);
        et1.rollET(elapsedTime,rollAngle);
        et2.rollET(elapsedTime,rollAngle);

        if (isCheering){
            et1.spinET();
            et2.spinET();
            et1.shakeHands(elapsedTime);
            et2.shakeHands(elapsedTime);
            et1.rockET(elapsedTime,rockAngle);
            et2.rockET(elapsedTime,rockAngle);
        }

        etRoot1.draw(gl);
        etRoot2.draw(gl);

        sl.goSpin(isSlSpin);
        sl.setSpotLightColor(gl, spotLightColor);
        spotLight.draw(gl);
    }

    /**
    * Method for changing AmbientStrength
    */
    public void changeAmbientStrength(float v) {
        System.out.println("Set Ambient Strength to " + v);
        light1.setAmbient_strength(v);
        light2.setAmbient_strength(v);
        sl.setAmbient_strength(v);
    }

    /**
     * Method for changing SpecularStrength
     */
    public void changeSpecularStrength(float v) {
        System.out.println("Set Specular Strength to " + v);
        light1.setSpecular_strength(v);
        light2.setSpecular_strength(v);
        sl.setSpecular_strength(v);
    }

    /**
     * Method for changing DiffuseStrength
     */
    public void changeDiffuseStrength(float v) {
        System.out.println("Set Diffuse Strength to " + v);
        light1.setDiffuse_strength(v);
        light2.setDiffuse_strength(v);
        sl.setDiffuse_strength(v);
    }

    /**
     * Method for init modelMatrix for skybox
     */
    private Mat4 getMforSkyBox() {
        float size = 100f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size, size, size), modelMatrix);
        return modelMatrix;
    }

    /**
     * Method for init modelMatrix for backdrop
     */
    private Mat4 getMforBackdrop() {
        float size = 16f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size, 1f, size), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, size * 0.5f, -size * 0.5f), modelMatrix);
        return modelMatrix;
    }

    /**
     * Method for init modelMatrix for ground
     */
    private Mat4 getMforGround() {
        float size = 16f;
        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(size, 1f, size), modelMatrix);
        return modelMatrix;
    }

    /* ======================================== */
    /** User Interface Handlers */
    public void rockTheET() {
        if (rockAngle == 0) {
            rockAngle = 15;
        } else {
            rockAngle = 0;
        }
    }

    public void rollTheET() {
        if (rollAngle == 0) {
            rollAngle = 30;
        } else {
            rollAngle = 0;
        }
    }

    public void standStill() {
        rockAngle = 0;
        rollAngle = 0;
        isCheering = false;
        et1.setSpinAllAngle(0f);
        et2.setSpinAllAngle(0f);
        et1.setFacingAngle(0f);
        et2.setFacingAngle(0f);
        et1.setArmsAngle(60f);
        et2.setArmsAngle(60f);
    }

    public void cheering() {
        et1.setFacingAngle(90f);
        et2.setFacingAngle(270f);
        isCheering = !isCheering;
        rollTheET();
        rockTheET();
    }


    public void toggleLight1() {
        if (light1Color == 1.0f) {
            light1Color = 0.0f;
            Material material = new Material();
            material.setAmbient(0.0f, 0.0f, 0.0f);
            material.setDiffuse(0.0f, 0.0f, 0.0f);
            material.setSpecular(0.0f, 0.0f, 0.0f);
            light1.setMaterial(material);
        } else {
            light1Color = 1.0f;
            light1.setMaterial(light1.defaultMaterial());
        }
    }

    public void toggleLight2() {
        if (light2Color == 1.0f) {
            light2Color = 0.0f;
            Material material = new Material();
            material.setAmbient(0.0f, 0.0f, 0.0f);
            material.setDiffuse(0.0f, 0.0f, 0.0f);
            material.setSpecular(0.0f, 0.0f, 0.0f);
            light2.setMaterial(material);
        } else {
            light2Color = 1.0f;
            light2.setMaterial(light2.defaultMaterial());
        }
    }

    public void toggleSportLight() {
        if (spotLightColor == 1.0f) {
            Material material = new Material();
            material.setAmbient(0.0f, 0.0f, 0.0f);
            material.setDiffuse(0.0f, 0.0f, 0.0f);
            material.setSpecular(0.0f, 0.0f, 0.0f);
            spotLightColor = 0.0f;
            sl.setLampMaterial(material);
        } else {
            spotLightColor = 1.0f;
            sl.setLampMaterial(sl.defaultMaterial());
        }
    }

    public void toggleSportLightSpin() {
        isSlSpin = (isSlSpin) ? false : true;
    }

    /** TIME */
    private double startTime;
    private double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

}
