import Utils.EmptyImageException;

import javax.imageio.ImageIO;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        long start = System.currentTimeMillis();

        ArrayList<Character> characters = new ArrayList<>();
        File[] files = new File("C:\\Users\\Jacek\\Documents\\Studia\\III\\SUS\\myapp\\data").listFiles();
        for (File f : files)
        {
            try
            {
                characters.add(new Character(ImageIO.read(f), f.getName()));
            }
            catch (RasterFormatException | EmptyImageException e)
            {
                System.out.println(f.getName());
                System.out.println(e.getMessage());
            }
        }

        DBScan clusterer = new DBScan(2, 2);
        int clustered = 0;
        int i = 0;

        List<List<Character>> clusters = clusterer.cluster(characters);

        long end = System.currentTimeMillis();
        long elapsed = end - start;

        File root = new File("C:\\Users\\Jacek\\Desktop\\sus\\");

        if (root.exists())
        {
            Files.walk(root.toPath()).
                    sorted((a, b) -> b.compareTo(a)). // reverse; files before dirs
                    forEach(p -> {
                try
                {
                    if (! p.toFile().isDirectory())
                    {
                        Files.delete(p);
                    }
                } catch (IOException e)
                { /* ... */ }
            });
        }

        root.mkdir();

        for (List<Character> c : clusters)
        {
            i++;
            File f = new File(root.getPath() + "\\" + i);
            if (!f.exists())
            {
                f.mkdir();
            }
//            System.out.println("*******");
//            System.out.println("Cluster size: " + c.size());
            clustered += c.size();
            for (Character chr : c)
            {
//                System.out.println(chr.fileName);
                File outputfile = new File(f.getPath() + "\\" +  chr.fileName);
                ImageIO.write(chr.img, "png", outputfile);
            }
        }

        System.out.println();
        System.out.println("Clusters: " + clusters.size());
        System.out.println("Total in clusters: " + clustered);
        System.out.print("Cluster sizes: ");
        for (List c : clusters)
            System.out.print(c.size() + " ");
        System.out.println();

        long minutes = Math.floorDiv(elapsed, 1000 * 60);
        long seconds = elapsed/1000 - 60 * minutes;
        System.out.println("\nTime: " + minutes + ":" + seconds);
    }
}
