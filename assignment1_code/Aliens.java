import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Main Class of the program
 *
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class Aliens extends JFrame {

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
    private GLCanvas canvas;
    private Aliens_GLEventListener glEventListener;
    private final FPSAnimator animator;
    private Camera camera;

    /** Main Method as the entrance of the program */
    public static void main(String[] args) {
        Aliens aliens = new Aliens("Aliens");
        aliens.getContentPane().setPreferredSize(dimension);
        aliens.pack();
        aliens.setVisible(true);
        aliens.setMinimumSize(dimension);
        aliens.canvas.requestFocusInWindow();
    }

    /** Scene Constructor */
    public Aliens(String textForTitleBar) {
        super(textForTitleBar);
        GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        canvas = new GLCanvas(glcapabilities);
        camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
        glEventListener = new Aliens_GLEventListener(camera);
        canvas.addGLEventListener(glEventListener);
        canvas.addMouseMotionListener(new MyMouseInput(camera));
        canvas.addKeyListener(new MyKeyboardInput(camera));
        getContentPane().add(canvas, BorderLayout.CENTER);

        /* Init User Interface */
        ActionListenerHandler actionListenerHandler = new ActionListenerHandler(glEventListener);
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(actionListenerHandler);
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);

        /* Bottom Panel */
        JPanel functionP = new JPanel();
        JPanel lightButtonP = new JPanel();
        JPanel animateButtonP = new JPanel();
        JPanel sliderP = new JPanel();

        functionP.setPreferredSize(new Dimension(WIDTH, HEIGHT / 5));
        functionP.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        this.add(functionP, BorderLayout.SOUTH);
        functionP.add(animateButtonP);
        functionP.add(lightButtonP);
        functionP.add(sliderP);

        createButtons(lightButtonP,animateButtonP, actionListenerHandler);
        createSlider(sliderP);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                animator.stop();
                remove(canvas);
                dispose();
                System.exit(0);
            }
        });
        animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    /**
     * Creates and adds toggle buttons to the specified panels for controlling various functionalities.
     *
     * @param lightP              The JPanel where buttons related to lights will be added.
     * @param animateP            The JPanel where buttons related to animations will be added.
     * @param actionListenerHandler The ActionListenerHandler for button actions.
     * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
     */
    private void createButtons(JPanel lightP,JPanel animateP, ActionListenerHandler actionListenerHandler) {
        String[] buttonLabels = {
                "Globle Light1 On/Off",
                "Globle Light2 On/Off",
                "SpotLight Spin On/Off",
                "SpotLight On/Off",
                "Rock On/Off",
                "Roll On/Off",
                "Cheering On/Off",
                "Stand Still"
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JToggleButton button = new JToggleButton(buttonLabels[i]);
            button.addActionListener(actionListenerHandler);
            if (i<4){
                lightP.add(button);
            }else{
                animateP.add(button);
            }

        }
    }

    /**
     *  Loop to create multiple Sliders
     */
    private void createSlider(JPanel panel) {
        String[] sliderLabels = {
                "Ambient Strength:",
                "Specular Strength:",
                "Diffuse Strength:"
        };

        for (String label : sliderLabels) {
            JLabel lb = new JLabel(label);
            JSlider slider;
            if (label.equals("Ambient Strength:")) {
                slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
            } else if (label.equals("Specular Strength:")) {
                slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
            } else {
                slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
            }
            slider.setMajorTickSpacing(50);
            slider.setMinorTickSpacing(50);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.addChangeListener(new ChangeListenerHandler(glEventListener, label));
            panel.add(lb);
            panel.add(slider);
        }
    }

    /*
     *  Keyboard and Mouse Handler
     */
    class MyKeyboardInput extends KeyAdapter {
        private Camera camera;

        public MyKeyboardInput(Camera camera) {
            this.camera = camera;
        }

        public void keyPressed(KeyEvent e) {
            Camera.Movement m = Camera.Movement.NO_MOVEMENT;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    m = Camera.Movement.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    m = Camera.Movement.RIGHT;
                    break;
                case KeyEvent.VK_UP:
                    m = Camera.Movement.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    m = Camera.Movement.DOWN;
                    break;
                case KeyEvent.VK_A:
                    m = Camera.Movement.FORWARD;
                    break;
                case KeyEvent.VK_Z:
                    m = Camera.Movement.BACK;
                    break;
            }
            camera.keyboardInput(m);
        }
    }

    class MyMouseInput extends MouseMotionAdapter {
        private Point lastpoint;
        private Camera camera;

        public MyMouseInput(Camera camera) {
            this.camera = camera;
        }

        /**
         * mouse is used to control camera position
         *
         * @param e instance of MouseEvent
         */
        public void mouseDragged(MouseEvent e) {
            Point ms = e.getPoint();
            float sensitivity = 0.001f;
            float dx = (float) (ms.x - lastpoint.x) * sensitivity;
            float dy = (float) (ms.y - lastpoint.y) * sensitivity;
            //System.out.println("dy,dy: "+dx+","+dy);
            if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK)
                camera.updateYawPitch(dx, -dy);
            lastpoint = ms;
        }

        /**
         * mouse is used to control camera position
         *
         * @param e instance of MouseEvent
         */
        public void mouseMoved(MouseEvent e) {
            lastpoint = e.getPoint();
        }
    }
}