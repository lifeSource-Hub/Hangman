import java.sql.*;
import java.util.*;

public class Main
{
    private final String URL = "jdbc:sqlite::resource:hangmanWords.db";
    private final String[] BODY_PARTS = {" o", "\n\\", "|", "/", "\n |", "\n/ ", "\\"};
    private int incorrectGuessCount = 0;

    public static void main(String[] args)
    {
        Main hangman = new Main();
    }

    private Main()
    {
        char playerGuess;
        int correctGuessCount = 0;
        boolean gameOver = false;
        List<Character> guessedLetters = new ArrayList<>();

        char[] victoryWord = getRandomWord().toCharArray();
        char[] displayWord = new char[victoryWord.length];

        for (int i = 0; i < victoryWord.length; i++)
        {
            displayWord[i] = '_';
        }

        System.out.println("Winning word: " + Arrays.toString(victoryWord) + "\n");

        while (!gameOver)
        {
            boolean incorrectGuess = true;

            System.out.print("\nYour word: ");

            for (char c : displayWord)
            {
                System.out.print(c);
                System.out.print(' ');
            }

            System.out.print("\n\nGuess a letter: ");
            playerGuess = new Scanner(System.in).next().charAt(0);
            System.out.println("\n***************************************************");
            // TODO validate input

            if (guessedLetters.contains(playerGuess))
            {
                System.out.println("You've already guessed " + playerGuess);
            }
            else
            {
                guessedLetters.add(playerGuess);

                for (int i = 0; i < victoryWord.length; i++)
                {
                    if (playerGuess == victoryWord[i])
                    {
                        displayWord[i] = playerGuess;
                        correctGuessCount++;
                        incorrectGuess = false;
                    }

                    if (correctGuessCount == victoryWord.length)
                    {
                        winGame();
                        gameOver = true;
                        break;
                    }
                }

                if (incorrectGuess)
                {
                    incorrectGuessCount++;
                    printBodyPart();

                    if (incorrectGuessCount == BODY_PARTS.length)
                    {
                        loseGame();
                        gameOver = true;
                    }
                }
            }
        }
    }

    // TODO replace hard-coded SQL
    private String getRandomWord()
    {
        String word = "";
        Random generator = new Random(System.currentTimeMillis());
        String rowCountQuery = "SELECT COUNT(*) FROM longwords";
        StringBuilder wordQuery = new StringBuilder("SELECT word FROM longwords WHERE id IN(");

        try (Connection conn = DriverManager.getConnection(URL))
        {
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(rowCountQuery);
            int rowCount = resultSet.getInt("COUNT(*)");

            wordQuery.append(generator.nextInt(rowCount));
            wordQuery.append(");");
            resultSet = statement.executeQuery(wordQuery.toString());

            word = resultSet.getString("word");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return word;
    }

    private void printBodyPart()
    {
        System.out.println();

        for (int i = 0; i < incorrectGuessCount; i++)
        {
            System.out.print(BODY_PARTS[i]);
        }

        System.out.println();
    }

    private void winGame()
    {
        System.out.println("\nYou won!");
        // TODO more here
    }

    private void loseGame()
    {
        System.out.println("\nYou killed him!");
    }
}