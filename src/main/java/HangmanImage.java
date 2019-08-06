import org.pmw.tinylog.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HangmanImage
{
    private final List<ImageIcon> images = new ArrayList<>();
    private final String WRONG_GUESS0 = "wrongGuess0.png";
    private final String WRONG_GUESS1 = "wrongGuess1.png";
    private final String WRONG_GUESS2 = "wrongGuess2.png";
    private final String WRONG_GUESS3 = "wrongGuess3.png";
    private final String WRONG_GUESS4 = "wrongGuess4.png";
    private final String WRONG_GUESS5 = "wrongGuess5.png";
    private final String WRONG_GUESS6 = "wrongGuess6.png";

    public HangmanImage()
    {
        images.add(read(WRONG_GUESS0));
        images.add(read(WRONG_GUESS1));
        images.add(read(WRONG_GUESS2));
        images.add(read(WRONG_GUESS3));
        images.add(read(WRONG_GUESS4));
        images.add(read(WRONG_GUESS5));
        images.add(read(WRONG_GUESS6));
    }

    public List<ImageIcon> getImages()
    {
        return images;
    }

    private ImageIcon read(String name)
    {
        try
        {
            final String PATH = "img/" + name;
            BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(PATH));
            return new ImageIcon(image);
        }
        catch (IOException e)
        {
            Logger.error("Error reading icon " + name);
            e.printStackTrace();
            return null;
        }
    }
}