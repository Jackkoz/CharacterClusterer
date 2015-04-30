import Utils.EmptyImageException;

import javax.imageio.ImageIO;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        ArrayList<Character> characters = new ArrayList<>();
        for (File f : new File(args[1]).listFiles())
        {
            try
            {
                characters.add(new Character(ImageIO.read(f), f.getName()));
            }
            catch (RasterFormatException | EmptyImageException ignored) {}
        }

        DBScan clusterer = new DBScan(10, 1);

        List<List<Character>> clusters = clusterer.cluster(characters);

        PrintWriter output = new PrintWriter(args[2], "UTF-8");

        for (List<Character> c : clusters)
        {
            for (Character chr : c)
            {
                output.print(chr.fileName + " ");
            }
            output.println();
        }

        for (Character chr : clusterer.noiseSet)
        {
            output.print(chr.fileName + " ");
            output.println();
        }

        output.close();
    }
}
