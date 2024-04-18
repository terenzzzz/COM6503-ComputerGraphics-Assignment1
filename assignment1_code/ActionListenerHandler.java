import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
* ActionListener to handling Action of User Interface
*
* @author Zhicong Jiang zjiang34@sheffield.ac.uk>
*/
public class ActionListenerHandler implements ActionListener {
    Aliens_GLEventListener glEventListener;


    /** Constructor to initialize Aliens_GLEventListener */
    public ActionListenerHandler(Aliens_GLEventListener glEventListener) {
        this.glEventListener = glEventListener;
    }

    /** Method triggered when an action is performed */
    @Override
    public void actionPerformed(ActionEvent e) {
        /* Check the action command and perform corresponding actions */

        /* Toggle Global Light 1 on/off */
        if (e.getActionCommand().equalsIgnoreCase("Globle Light1 On/Off")) {
            System.out.println("Globle Light1 On/Off");
            glEventListener.toggleLight1();
        }

        /* Toggle Global Light 2 on/off */
        if (e.getActionCommand().equalsIgnoreCase("Globle Light2 On/Off")) {
            System.out.println("Globle Light2 On/Off");
            glEventListener.toggleLight2();
        }

        /* Toggle SpotLight on/off */
        if (e.getActionCommand().equalsIgnoreCase("SpotLight On/Off")) {
            System.out.println("SpotLight On/Off");
            glEventListener.toggleSportLight();
        }

        /* Toggle SpotLight Spin on/off */
        if (e.getActionCommand().equalsIgnoreCase("SpotLight Spin On/Off")) {
            System.out.println("SpotLight Spin On/Off");
            glEventListener.toggleSportLightSpin();
        }

        /* Toggle Rock movement on/off */
        if (e.getActionCommand().equalsIgnoreCase("Rock On/Off")) {
            System.out.println("Rock On/Off");
            glEventListener.rockTheET();
        }

        /* Toggle Roll movement on/off */
        if (e.getActionCommand().equalsIgnoreCase("Roll On/Off")) {
            System.out.println("Roll On/Off");
            glEventListener.rollTheET();
        }

        /* Make the character stand still */
        if (e.getActionCommand().equalsIgnoreCase("Stand Still")) {
            System.out.println("Stand Still");
            glEventListener.standStill();
        }

        /* Toggle Cheering animation on/off */
        if (e.getActionCommand().equalsIgnoreCase("Cheering On/Off")) {
            System.out.println("Cheering On/Off");
            glEventListener.cheering();
        }

        /* Quit the application if requested */
        if (e.getActionCommand().equalsIgnoreCase("Quit")) {
            System.exit(0);
        }
    }
}
