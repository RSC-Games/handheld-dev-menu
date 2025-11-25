package system;
import java.util.ArrayList;

import net.java.games.input.Component;
import net.java.games.input.Controller;

abstract class ControllerLayout {
    static final float DPAD_BUTTON_RANGE = 0.125f;
    static final float STICK_DEADZONE = 0.25f;

    ArrayList<Component> newButtons = new ArrayList<>();
    ArrayList<Component> activeButtons = new ArrayList<>();

    Component[] leftStick;
    Component[] rightStick;
    Component leftStickBtn;
    Component rightStickBtn;
    Component dpad;
    Component aBtn;
    Component bBtn;
    Component xBtn;
    Component yBtn;
    Component minusBtn;
    Component plusBtn;
    Component homeBtn;
    Component leftTrigBtn;
    Component rightTrigBtn;
    //Component leftBumperBtn;
    //Component rightBumperBtn;

    ArrayList<Component> buttonComponents;

    public static ControllerLayout getMapping(Controller controller) {
        System.out.println("mapping " + controller.getName());
        System.out.println("controller info " + controller.getType());

        switch (controller.getName()) {
            case "Controller (XBOX 360 For Windows)": // JC200 reports this in Xbox mode.
                return new XBOX_360_DefaultLayout(controller);
            case "ZhiXu Gamepad":
            case "Gamepad": // JC200 reports this normally
                return new JC200_DefaultLayout(controller);
            default:
                System.err.println("error: unknown controller type " + controller.getName());
        }
        return null;
    }

    protected Component searchComponentByName(Controller controller, String name) {
        Component[] components = controller.getComponents();

        for (Component component : components) {
            if (component.getName().equals(name))
                return component;
        }

        System.err.printf("Warning: no component \"%s\" found on gamepad \"%s\"\n", name, controller.getName());
        return null;
    }

    /**
     * Remove unpressed buttons to facilitate the one-shot system.
     */
    public void poll() {
        activeButtons.addAll(newButtons);
        newButtons.clear();

        ArrayList<Component> dupButtons = new ArrayList<>();
        dupButtons.addAll(activeButtons);

        // Get rid of buttons that are no longer being pressed.
        for (Component component : dupButtons)
            if (!(component.getPollData() > component.getDeadZone()))
                activeButtons.remove(component);

        // Find new buttons
        for (Component component : buttonComponents) {
            if (component.getPollData() > component.getDeadZone() && !activeButtons.contains(component))
                newButtons.add(component);
        }
    }

    boolean buttonJustPressed(Component button) {
        return newButtons.contains(button);
    }

    /**
     * Returns both axes of the left stick (x, y).
     * X: Left values are negative, right values are positive.
     * Y: Up values are negative, down values are positive.
     * @return
     */
    public float[] getLeftStick() { 
        float xRaw = leftStick[0].getPollData();
        xRaw = Math.abs(xRaw) > STICK_DEADZONE ? xRaw : 0;
        float yRaw = leftStick[1].getPollData();
        yRaw = Math.abs(yRaw) > STICK_DEADZONE ? yRaw : 0;

        return new float[] {xRaw, yRaw};
    }

    public boolean getLeftStickButton() {
        return leftStickBtn.getPollData() > leftStickBtn.getDeadZone();
    }

    /**
     * Returns both axes of the right stick (x, y).
     * X: Left values are negative, right values are positive.
     * Y: Up values are negative, down values are positive.
     * @return
     */
    public float[] getRightStick() {
        return new float[] {rightStick[0].getPollData(), rightStick[1].getPollData()};
    }

    public boolean getRightStickButton() {
        if (!buttonJustPressed(rightStickBtn))
            return false;

        return rightStickBtn.getPollData() > rightStickBtn.getDeadZone();
    }

    public float getDPAD() {
        return dpad.getPollData();
    }

    /**
     * DPAD left is indicated by a 1 reading, but is still technically pressed if
     * the reading is 0.125.
     * @return
     */
    public boolean getDPADLeft() {      
        float dpadReading = getDPAD();
        return Math.abs(dpadReading - 1f) <= DPAD_BUTTON_RANGE || dpadReading == 0.125;
    }


    /**
     * DPAD up is within the range of 0.125 to 0.375, so an offset of 0.25 makes
     * the math really easy.
     * @return
     */
    public boolean getDPADUp() {
        return Math.abs(getDPAD() - 0.25f) <= DPAD_BUTTON_RANGE;
    }

    /**
     * Ditto except 0.375 to 0.625.
     * @return
     */
    public boolean getDPADRight() {
        return Math.abs(getDPAD() - 0.5f) <= DPAD_BUTTON_RANGE;
    }

    /**
     * Same except 0.625 to 0.875.
     * @return
     */
    public boolean getDPADDown() {
        return Math.abs(getDPAD() - 0.75f) <= DPAD_BUTTON_RANGE;
    }

    public boolean getButtonA() {
        if (!buttonJustPressed(aBtn))
            return false;

        return aBtn.getPollData() > aBtn.getDeadZone();
    }

    public boolean getButtonB() {
        if (!buttonJustPressed(bBtn))
            return false;
        
        return bBtn.getPollData() > bBtn.getDeadZone();
    }
    
    public boolean getButtonX() {
        if (!buttonJustPressed(xBtn))
            return false;

        return xBtn.getPollData() > xBtn.getDeadZone();
    }

    public boolean getButtonY() {
        if (!buttonJustPressed(yBtn))
            return false;

        return yBtn.getPollData() > yBtn.getDeadZone();
    }

    public boolean getButtonMinus() {
        if (!buttonJustPressed(minusBtn))
            return false;

        return minusBtn.getPollData() > minusBtn.getDeadZone();
    }

    public boolean getButtonPlus() {
        if (!buttonJustPressed(plusBtn))
            return false;

        return plusBtn.getPollData() > plusBtn.getDeadZone();
    }

    public boolean getButtonHome() {
        if (!buttonJustPressed(homeBtn))
            return false;

        return homeBtn.getPollData() > homeBtn.getDeadZone();
    }

    public boolean getTriggerL() {
        if (!buttonJustPressed(leftTrigBtn))
            return false;

        return leftTrigBtn.getPollData() > leftTrigBtn.getDeadZone();
    }

    public boolean getTriggerR() {
        if (!buttonJustPressed(rightTrigBtn))
            return false;

        return rightTrigBtn.getPollData() > rightTrigBtn.getDeadZone();
    }

    public abstract boolean getTriggerZL();
    public abstract boolean getTriggerZR();
    
    /**
     * Retained for testing purposes and for testing gamepads. Not used for most
     * cases, but still too important to rewrite.
     */
    //@SuppressWarnings("unused")
    static void printMappingInformation(Controller controller) {
        System.out.println("************************************************************");

        for (Component component : controller.getComponents())
            System.out.println(component.getName() + ": " + component.getPollData() + " (dead " + component.getDeadZone() + ")");
    }
}

class XBOX_360_DefaultLayout extends ControllerLayout {
    Component triggerZ;

    // In XBOX 360 mode:
    // Button 0: A
    // Button 1: B
    // Button 2: X
    // Button 3: Y
    // Button 4: L
    // Button 5: R
    // Button 6: -
    // Button 7: +
    // Button 8: LB
    // Button 9: RB
    // Hat Switch: DPAD (0.25 up to 1.0 left clockwise)
    // X Axis: LX
    // Y Axis: LY (inverted)
    // X Rotation: LX
    // Y Rotation: LY (inverted)
    // Z Axis: (+1 = ZL, -1 = ZR)
    XBOX_360_DefaultLayout(Controller controller) {
        leftStick = new Component[] {
            searchComponentByName(controller, "X Axis"), 
            searchComponentByName(controller, "Y Axis")
        };
        rightStick = new Component[] {
            searchComponentByName(controller, "X Rotation"), 
            searchComponentByName(controller, "Y Rotation")
        };
        leftStickBtn = searchComponentByName(controller, "Button 8");
        rightStickBtn = searchComponentByName(controller, "Button 9");
        dpad = searchComponentByName(controller, "Hat Switch");
        aBtn = searchComponentByName(controller, "Button 0");
        bBtn = searchComponentByName(controller, "Button 1");
        xBtn = searchComponentByName(controller, "Button 2");
        yBtn = searchComponentByName(controller, "Button 3");
        minusBtn = searchComponentByName(controller, "Button 6");
        plusBtn = searchComponentByName(controller, "Button 7");
        homeBtn = searchComponentByName(controller, "Button 6");
        leftTrigBtn = searchComponentByName(controller, "Button 4");
        rightTrigBtn = searchComponentByName(controller, "Button 5");
        triggerZ = searchComponentByName(controller, "Z Axis");

        buttonComponents = new ArrayList<Component>();
        buttonComponents.add(leftStickBtn);
        buttonComponents.add(rightStickBtn);
        buttonComponents.add(aBtn);
        buttonComponents.add(bBtn);
        buttonComponents.add(xBtn);
        buttonComponents.add(yBtn);
        buttonComponents.add(minusBtn);
        buttonComponents.add(plusBtn);
        buttonComponents.add(homeBtn);
        buttonComponents.add(leftTrigBtn);
        buttonComponents.add(rightTrigBtn);
    }

    @Override
    public boolean getTriggerZL() {
        return triggerZ.getPollData() > STICK_DEADZONE;
    }

    @Override
    public boolean getTriggerZR() {
        return triggerZ.getPollData() < -STICK_DEADZONE;
    }
}

class JC200_DefaultLayout extends ControllerLayout {
    Component triggerZL;
    Component triggerZR;

    // Buttons translation list for this specific controller (Windows: name (Linux)):
    // Button 0: A (A)
    // Button 1: B (B)
    // Button 2: 
    // Button 3: X (X)
    // Button 4: Y (Y)
    // Button 5: 
    // Button 6: L (Left Thumb)
    // Button 7: R (Right Thumb)
    // Button 8:
    // Button 9:
    // Button 10: - (Select)
    // Button 11: + (Start)
    // Button 12:
    // Button 13: LT (Left Thumb 3)
    // Button 14: RT (Right Thumb 3)
    // Button 15: Home (Unknown)
    // Brake: ZL (slider)
    // Accelerator: ZR (slider)
    // Hat Switch: DPAD (0.25 up to 1.0 left clockwise)
    // X Axis: LX (x)
    // Y Axis: LY (y)
    // Z Axis: RX (z)
    // Z Rotation: RY (rz)
    JC200_DefaultLayout(Controller controller) {
        boolean linux = System.getProperty("os.name").equals("Linux");

        leftStick = new Component[] {
            searchComponentByName(controller, linux ? "x" : "X Axis"), 
            searchComponentByName(controller, linux ? "y" : "Y Axis")
        };
        rightStick = new Component[] {
            searchComponentByName(controller, linux ? "z" : "Z Axis"), 
            searchComponentByName(controller, linux ? "rz" : "Z Rotation")
        };
        leftStickBtn = searchComponentByName(controller, linux ? "Left Thumb 3" : "Button 13");
        rightStickBtn = searchComponentByName(controller, linux ? "Right Thumb 3" : "Button 14");
        dpad = searchComponentByName(controller, linux ? "pov" : "Hat Switch");
        aBtn = searchComponentByName(controller, linux ? "A" : "Button 0");
        bBtn = searchComponentByName(controller, linux ? "B" : "Button 1");
        xBtn = searchComponentByName(controller, linux ? "X" : "Button 3");
        yBtn = searchComponentByName(controller, linux ? "Y" : "Button 4");
        minusBtn = searchComponentByName(controller, linux ? "Select" : "Button 10");
        plusBtn = searchComponentByName(controller, linux ? "Start" : "Button 11");
        homeBtn = searchComponentByName(controller, linux ? "Unknown" : "Button 15");
        leftTrigBtn = searchComponentByName(controller, linux ? "Left Thumb" : "Button 6");
        rightTrigBtn = searchComponentByName(controller, linux ? "Right Thumb" : "Button 7");
        triggerZL = searchComponentByName(controller, linux ? "slider" : "Brake");
        triggerZR = searchComponentByName(controller, linux ? "slider" : "Accelerator");

        buttonComponents = new ArrayList<Component>();
        buttonComponents.add(leftStickBtn);
        buttonComponents.add(rightStickBtn);
        buttonComponents.add(aBtn);
        buttonComponents.add(bBtn);
        buttonComponents.add(xBtn);
        buttonComponents.add(yBtn);
        buttonComponents.add(minusBtn);
        buttonComponents.add(plusBtn);
        buttonComponents.add(homeBtn);
        buttonComponents.add(leftTrigBtn);
        buttonComponents.add(rightTrigBtn);
    }

    @Override
    public boolean getTriggerZL() {
        if (!buttonJustPressed(triggerZL))
            return false;

        return triggerZL.getPollData() > triggerZL.getDeadZone();
    }

    @Override
    public boolean getTriggerZR() {
        if (!buttonJustPressed(triggerZR))
            return false;

        return triggerZR.getPollData() > triggerZR.getDeadZone();
    }
    
}