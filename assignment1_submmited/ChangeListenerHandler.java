import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ChangeListener to handling Slider of User Interface
 *
 * @author Zhicong Jiang zjiang34@sheffield.ac.uk>
 */
public class ChangeListenerHandler implements ChangeListener {
    Aliens_GLEventListener glEventListener;
    String label;

    public ChangeListenerHandler(Aliens_GLEventListener glEventListener, String l) {
        this.glEventListener = glEventListener;
        this.label = l;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int value = source.getValue();
            if (label.equals("Ambient Strength:")){
                glEventListener.changeAmbientStrength((float) value / 100);
            } else if (label.equals("Specular Strength:")) {
                glEventListener.changeSpecularStrength((float) value / 10);
            } else if (label.equals("Diffuse Strength:")) {
                glEventListener.changeDiffuseStrength((float) value / 100);
            }
            System.out.println(label + " value: " + value);
        }
    }
}
