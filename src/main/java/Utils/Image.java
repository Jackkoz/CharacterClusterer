package Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image
{
    public static int WHITE = Color.WHITE.getRGB();
    public static int BLACK = Color.BLACK.getRGB();

    public static BufferedImage cropWhitespace(BufferedImage img) throws EmptyImageException
    {
        int maxX = 0, maxY = 0, minX = 0, minY = 0;
        int x,y;
        int width = img.getWidth() + img.getMinX();
        int height = img.getHeight() + img.getMinY();

        y = img.getMinY();
        while (y < height)
        {
            x = img.getMinX();
            while (x < width && img.getRGB(x, y) == WHITE)
            {
                x++;
            }

            if (x < width)
            {
                minY = y;
                break;
            }

            y++;
        }

        if (y == height)
            throw new EmptyImageException();

        y = height - 1;
        while (y >= minY)
        {
            x = img.getMinX();
            while (x < width && img.getRGB(x, y) == WHITE)
                x++;

            if (x < width)
            {
                maxY = y;
                break;
            }

            y--;
        }

        x = img.getMinX();

        while (x < width)
        {
            y = img.getMinY();
            while (y < height && img.getRGB(x, y) == WHITE)
                y++;

            if (y < height)
            {
                minX = x;
                break;
            }

            x++;
        }

        x = width - 1;
        while (x >= minX)
        {
            y = img.getMinY();
            while (y < height && img.getRGB(x, y) == WHITE)
                y++;

            if (y < height)
            {
                maxX = x;
                break;
            }

            x--;
        }

        return img.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    public static BufferedImage connectedImage(BufferedImage img)
    {
        BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++)
                img2.setRGB(x, y, blackNeighbours(x, y, img) >= 5 ? Image.BLACK : Image.WHITE);

        return img2;
    }

    public static int hasNeighbour(int x, int y, BufferedImage img1, BufferedImage img2)
    {
        if (img1.getRGB(x, y) == img2.getRGB(x, y))
            return 0;

        if (img1.getRGB(x, y) == Image.WHITE)
            return 1;

        if (x - 1 >= 0)
        {
            if (img2.getRGB(x - 1, y) == Image.BLACK)
                return 0;

            if (y - 1 >= 0 && img2.getRGB(x - 1, y - 1) == Image.BLACK)
                return 0;

            if (y + 1 < img2.getHeight() && img2.getRGB(x - 1, y + 1) == Image.BLACK)
                return 0;
        }

        if (y - 1 >= 0 && img2.getRGB(x, y - 1) == Image.BLACK)
            return 0;

        if (y + 1 < img2.getHeight() && img2.getRGB(x, y + 1) == Image.BLACK)
            return 0;

        if (x + 1 < img2.getWidth())
        {
            if (img2.getRGB(x + 1, y) == Image.BLACK)
                return 0;

            if (y - 1 >= 0 && img2.getRGB(x + 1, y - 1) == Image.BLACK)
                return 0;

            if (y + 1 < img2.getHeight() && img2.getRGB(x + 1, y + 1) == Image.BLACK)
                return 0;
        }

        return 1;
    }

    public static int blackNeighbours(int x, int y, BufferedImage img)
    {
        int neighbours = 0;

        if (x - 1 >= 0)
        {
            if (img.getRGB(x - 1, y) == Image.BLACK)
                neighbours++;

            if (y - 1 >= 0 && img.getRGB(x - 1, y - 1) == Image.BLACK)
                neighbours++;

            if (y + 1 < img.getHeight() && img.getRGB(x - 1, y + 1) == Image.BLACK)
                neighbours++;
        }

        if (y - 1 >= 0 && img.getRGB(x, y - 1) == Image.BLACK)
            neighbours++;

        if (y + 1 < img.getHeight() && img.getRGB(x, y + 1) == Image.BLACK)
            neighbours++;

        if (x + 1 < img.getWidth())
        {
            if (img.getRGB(x + 1, y) == Image.BLACK)
                neighbours++;

            if (y - 1 >= 0 && img.getRGB(x + 1, y - 1) == Image.BLACK)
                neighbours++;

            if (y + 1 < img.getHeight() && img.getRGB(x + 1, y + 1) == Image.BLACK)
                neighbours++;
        }

        return neighbours;
    }
}
