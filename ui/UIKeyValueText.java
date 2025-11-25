package ui;

import java.awt.Color;
import java.awt.Point;

public class UIKeyValueText extends UIBase {
    //private static Graphics __g = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY).getGraphics();

    protected UIText key;
    protected UIText value;

    public UIKeyValueText(UIElement parent, Point location, Color defaultColor) {
        super(parent, location);
        this.key = new UIText(this, new Point(), "key:", defaultColor, 12);
        this.value = new UIText(this, new Point(32, 0), "value", defaultColor, 12);
    }
    
    public void setKeyText(String keyText) {
        keyText += ":  ";
        this.key.setText(keyText);

        //FontMetrics metrics = __g.getFontMetrics(key.getFont());
        //int textWidth = metrics.stringWidth(keyText);
        this.value.setPosition(new Point(keyText.length() * 7, 0));
    }

    public void setValueText(String valueText) {
        this.value.setText(valueText);
    }

    public void setValueColor(Color color) {
        this.value.setColor(color);
    }
}
