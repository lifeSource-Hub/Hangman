import org.pmw.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hangman // implements ActionListener
{
    private final String URL = "jdbc:sqlite::resource:hangmanWords.db";
    private final String TABLE = "longwords";

    private Action guessHandler;
    private Action newGameHandler;
    private List<Character> guessedLetters;
    private char[] victoryWord;
    private char[] displayWordArr;
    private int correctGuessCount;
    private int incorrectGuessCount;

    private JFrame frame;
    private List<ImageIcon> images;
    private JLabel hangmanImg;
    private JLabel displayWord;
    private JLabel guessErrorMsg;
    private JLabel newGameLbl;
    private JButton newGameBtn;
    private JTextField guessInput;

    public static void main(String[] args)
    {
        Hangman hangman = new Hangman();
    }

    private Hangman()
    {
        this("Hangman");
    }

    private Hangman(String title)
    {
        setGuessHandler();
        setNewGameHandler();

        images = new HangmanImage().getImages();
        hangmanImg = new JLabel(images.get(incorrectGuessCount));
        
        newGameLbl = new JLabel();
        newGameBtn = new JButton("New Game");
        newGameBtn.setMargin(new Insets(0, 3, 0, 3));
        newGameBtn.addActionListener(newGameHandler);
        
        JLabel displayWordLbl = new JLabel("Your Word: ");
        displayWord = new JLabel();
        
        JLabel guessLbl = new JLabel("Guess a Letter: ");
        guessInput = new JTextField(4);
        guessInput.addActionListener(guessHandler);
        JButton enterBtn = new JButton("Enter");
        enterBtn.setMargin(new Insets(0, 3, 0, 3));
        enterBtn.addActionListener(guessHandler);
        
        guessErrorMsg = new JLabel();
        frame = new JFrame(title);
        
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.PAGE_AXIS));
        lowerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel newGameRow = new JPanel();
        JPanel wordRow = new JPanel();
        JPanel guessRow = new JPanel();
        JPanel msgRow = new JPanel();

        setupNewGame();

        newGameRow.add(newGameLbl);
        newGameRow.add(newGameBtn);
        wordRow.add(displayWordLbl);
        wordRow.add(displayWord);
        guessRow.add(guessLbl);
        guessRow.add(guessInput);
        guessRow.add(enterBtn);
        msgRow.add(guessErrorMsg);

        lowerPanel.add(newGameRow);
        lowerPanel.add(wordRow);
        lowerPanel.add(guessRow);
        lowerPanel.add(msgRow);

        frame.add(hangmanImg, BorderLayout.CENTER);
        frame.add(lowerPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void setGuessHandler()
    {
        guessHandler = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (validateGuess())
                {
                    updateDisplay();
                }

                guessInput.setText(null);
                guessInput.grabFocus();
            }
        };
    }

    private void setNewGameHandler()
    {
        newGameHandler = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setupNewGame();
            }
        };
    }

    private void setupNewGame()
    {
        correctGuessCount = 0;
        incorrectGuessCount = 0;

        victoryWord = getRandomWord().toCharArray();
        displayWordArr = new char[victoryWord.length];
        guessedLetters = new ArrayList<>();
        hangmanImg.setIcon(images.get(incorrectGuessCount));

        newGameLbl.setText(null);
        newGameBtn.setVisible(false);

        displayWord.setText(null);

        for (int i = 0; i < victoryWord.length; i++)
        {
            displayWordArr[i] = '_';
        }

        updateDisplay();
        Logger.debug("Victory word: " + Arrays.toString(victoryWord) + "\n");
    }

    private void updateDisplay()
    {
        StringBuilder str = new StringBuilder();

        for (char c : displayWordArr)
        {
            str.append(c);
            str.append("  ");
        }

        displayWord.setText(str.toString());
        guessErrorMsg.setText(null);

        frame.pack();
        frame.repaint();
        frame.revalidate();
    }

    private boolean validateGuess()
    {
        char playerGuess = ' ';
        String text = guessInput.getText();

        Pattern pattern = Pattern.compile("^[A-Za-z]");
        Matcher matcher = pattern.matcher(text);

        if (!text.isBlank() && matcher.find())
        {
            playerGuess = text.toLowerCase().charAt(0);

            if (guessedLetters.contains(playerGuess))
            {
                guessErrorMsg.setText("You've already guessed '" + playerGuess + "'");
            }
            else
            {
                guessedLetters.add(playerGuess);
                searchVictoryWord(playerGuess);
                return true;
            }
        }

        return false;
    }

    private void searchVictoryWord(char playerGuess)
    {
        boolean charNotFound = true;

        for (int i = 0; i < victoryWord.length; i++)
        {
            if (playerGuess == victoryWord[i])
            {
                displayWordArr[i] = playerGuess;
                correctGuessCount++;
                charNotFound = false;
            }

            if (correctGuessCount == victoryWord.length)
            {
                endGame("You won! Play again?");
                break;
            }
        }

        if (charNotFound)
        {
            incorrectGuessCount++;
            displayNextImg();

            if (incorrectGuessCount == images.size() - 1)
            {
                endGame("You lose! Play again?");
            }
        }
    }

    private void displayNextImg()
    {
        hangmanImg.setIcon(images.get(incorrectGuessCount));
    }

    private void endGame(String msg)
    {
        newGameLbl.setText(msg);
        newGameBtn.setVisible(true);

        frame.pack();
        frame.repaint();
        frame.revalidate();
    }

    private String getRandomWord()
    {
        String word = "";
        Random generator = new Random(System.currentTimeMillis());

        try (Connection conn = DriverManager.getConnection(URL))
        {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM longwords");
            int rowCount = resultSet.getInt("COUNT(*)");

            int randomInt = generator.nextInt(rowCount);
            resultSet = statement.executeQuery("SELECT word FROM longwords WHERE id IN(" + randomInt + ");");

            word = resultSet.getString("word").toLowerCase();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return word;
    }
}