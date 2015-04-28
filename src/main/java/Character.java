import Utils.Image;
import Utils.EmptyImageException;

import java.awt.image.BufferedImage;

public class Character
{
    public String fileName;
    public BufferedImage img;

    public Character(BufferedImage img, String s) throws EmptyImageException
    {
        fileName = s;
        this.img = Image.cropWhitespace(/*Image.connectedImage*/(img));
    }
}
