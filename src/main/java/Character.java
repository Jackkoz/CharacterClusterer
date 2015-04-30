import Utils.Image;
import Utils.EmptyImageException;

import java.awt.image.BufferedImage;

public class Character
{
    public String fileName;
    public BufferedImage img;
    public DBScan.PointStatus status;
    public boolean visited;

    public Character(BufferedImage img, String s) throws EmptyImageException
    {
        fileName = s;
        this.img = Image.cropWhitespace(img);
        status = null;
        visited = false;
    }
}
