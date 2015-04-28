import Utils.Image;
import Utils.EmptyImageException;
import org.apache.commons.math3.ml.clustering.Clusterable;

import java.awt.image.BufferedImage;

public class Character implements Clusterable
{
    public String fileName;
    public BufferedImage img;

    public Character(BufferedImage img, String s) throws EmptyImageException
    {
        fileName = s;
        this.img = Image.cropWhitespace(Image.connectedImage(img));
    }

    @Override
    public double[] getPoint()
    {
        throw new UnsupportedOperationException();
    }
}
