package Utils;

import Utils.Image;

import javax.imageio.ImageIO;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class mainUtils
{
    public static void main(String[] args) throws IOException
    {
        ArrayList<Character> characters = new ArrayList<>();
        File[] files = new File("C:\\Users\\Jacek\\Documents\\Studia\\III\\SUS\\myapp\\data").listFiles();
        for (File f : files)
        {
            try
            {
                File outputfile = new File("C:\\Users\\Jacek\\Documents\\Studia\\III\\SUS\\myapp\\datacomponents\\" + f.getName());
                ImageIO.write(Image.connectedImage(ImageIO.read(f)), "png", outputfile);
            } catch (RasterFormatException e)
            {
                System.out.println(f.getName());
                System.out.println(e.getMessage());
            }
        }
    }
}
