import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main
{
    private static String[] boardPieces = {" ", " ", " ", " ", " ", " ", " ", " ", " "};
    private static String[] players = {"X", "O"};
    private static int[][] winningCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                                                  {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                                                  {0, 4, 8}, {2, 4, 6}};

    private static int[] playerScores = {0, 0, 0};
    private static int playerTurn = 0;
    private static int lastPlayer = 0;
    private static boolean gameOver = false;
    private static boolean vsComputer = false;
    private static boolean running = true;
    private static boolean validChoice = false;
    private static boolean tiedGame = false;
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException, IOException
    {
        setConsoleWindow();
        System.out.println("Vs Computer? Enter yes or no.");
        String startChoice = in.nextLine();

        while(!validChoice && running)
        {
            if(startChoice.equalsIgnoreCase("exit"))
            {
                running = false;
                clearConsole();
            }
            else
            {
                startChoice = setValidChoice(startChoice);
            }
        }
        vsComputer = isVsComputer(startChoice);
        while(running)
        {
            makeGridWithScoreBoard();
            if(!gameOver)
            {
                lastPlayer = playerTurn;
                System.out.println("Player " + (lastPlayer + 1) + "'s turn.");

                if(playerTurn == 0 || (playerTurn == 1 && !vsComputer))
                {
                    String input = in.nextLine();

                    switch (input.toLowerCase())
                    {
                        case "top left":
                            setBoardPiece(0);
                            break;
                        case "top":
                            setBoardPiece(1);
                            break;
                        case "top right":
                            setBoardPiece(2);
                            break;
                        case "middle left":
                            setBoardPiece(3);
                            break;
                        case "middle":
                            setBoardPiece(4);
                            break;
                        case "middle right":
                            setBoardPiece(5);
                            break;
                        case "bottom left":
                            setBoardPiece(6);
                            break;
                        case "bottom":
                            setBoardPiece(7);
                            break;
                        case "bottom right":
                            setBoardPiece(8);
                            break;
                        case "exit":
                            running = false;
                            clearConsole();
                            break;
                        default:
                            System.out.print(input + " is not a valid placement.");
                            Thread.sleep(1500);
                            break;
                    }
                }
                else
                {
                    computerAI();
                    nextPlayerTurn();
                }
                checkBoard();
            }
            else
            {
                restartGame();
            }
        }
    }

    private static void setBoardPiece(int pos) throws InterruptedException
    {
        if(boardPieces[pos].equals(" "))
        {
            boardPieces[pos] = players[playerTurn];
            nextPlayerTurn();
        }
        else
        {
            System.out.println("This area already has another piece in it");
            System.out.println("Please pick again");
            Thread.sleep(1500);
        }

    }

    private static void computerAI() throws InterruptedException
    {
        Thread.sleep(1000);
        ArrayList<Integer> openPositions = new ArrayList<>();
        boolean firstMove = true;
        for(int i = 0; i < boardPieces.length; i++)
        {
            if(boardPieces[i].equals(" "))
            {
                openPositions.add(i);
            }
            else if(boardPieces[i].equals(players[1]))
            {
                firstMove = false;
            }
        }

        if(firstMove)
        {
            selectRandomPos(openPositions);
        }
        else
        {
            smartPlacement();
        }
    }

    private static void smartPlacement()
    {
        ArrayList<Integer> positionsHeld = new ArrayList<>();
        int[] viableCombination = {-1, -1, -1};
        int[] priorityCombination = {-1, -1, -1};
        int stopWinningMove = -1;

        for(int i = 0; i < boardPieces.length; i++)
        {
            if(boardPieces[i].equals(players[1]))
            {
                positionsHeld.add(i);
            }
        }

        int winProbability = 0;
        for(int i = 0; i < winningCombinations.length; i++)
        {
            if (boardPieces[winningCombinations[i][0]].equals(players[0]))
            {
                winProbability++;
            }
            if (boardPieces[winningCombinations[i][1]].equals(players[0]))
            {
                winProbability++;
            }
            if (boardPieces[winningCombinations[i][2]].equals(players[0]))
            {
                winProbability++;
            }
            if (boardPieces[winningCombinations[i][0]].equals(players[1]) || boardPieces[winningCombinations[i][2]].equals(players[1]) ||boardPieces[winningCombinations[i][2]].equals(players[1]))
            {
                winProbability--;
            }
            if(winProbability > 1 && (boardPieces[winningCombinations[i][0]].equals(" ") ||
                    boardPieces[winningCombinations[i][1]].equals(" ") || boardPieces[winningCombinations[i][2]].equals(" ")))
            {
                stopWinningMove = i;
                break;
            }
            winProbability = 0;
        }

        for(int i = 0; i < winningCombinations.length; i++)
        {
            int playerCount = 0;
            for(int comboPart : winningCombinations[i])
            {
                for(int j = 0; j < positionsHeld.size(); j++)
                {
                    int posValue = positionsHeld.get(j);
                    if(comboPart == posValue)
                    {
                        playerCount++;
                        break;
                    }
                }

                if(playerCount == 2 && (boardPieces[winningCombinations[i][0]].equals(" ")
                || boardPieces[winningCombinations[i][1]].equals(" ") || boardPieces[winningCombinations[i][2]].equals(" ")))
                {
                    priorityCombination = winningCombinations[i];
                    break;
                }
                else if(playerCount == 1 && viableCombination[0] == -1 && viableCombination[1] == -1 && viableCombination[2] == -1)
                {
                    viableCombination = winningCombinations[i];
                }
            }
        }

        if(priorityCombination[0] != -1 && priorityCombination[1] != -1 && priorityCombination[2] != -1)
        {
            if(boardPieces[priorityCombination[0]].equals(" "))
            {
                boardPieces[priorityCombination[0]] = players[1];
            }
            else if(boardPieces[priorityCombination[1]].equals(" "))
            {
                boardPieces[priorityCombination[1]] = players[1];
            }
            else
            {
                boardPieces[priorityCombination[2]] = players[1];
            }
        }
        else if(stopWinningMove > -1)
        {
            if(boardPieces[winningCombinations[stopWinningMove][0]].equals(" "))
            {
                boardPieces[winningCombinations[stopWinningMove][0]] = players[1];
            }
            else if(boardPieces[winningCombinations[stopWinningMove][1]].equals(" "))
            {
                boardPieces[winningCombinations[stopWinningMove][1]] = players[1];
            }
            else
            {
                boardPieces[winningCombinations[stopWinningMove][2]] = players[1];
            }
        }
        else
        {
            if(boardPieces[viableCombination[0]].equals(" "))
            {
                boardPieces[viableCombination[0]] = players[1];
            }
            else if(boardPieces[viableCombination[1]].equals(" "))
            {
                boardPieces[viableCombination[1]] = players[1];
            }
            else
            {
                boardPieces[viableCombination[2]] = players[1];
            }
        }
    }

    private static void selectRandomPos(ArrayList openPositions)
    {
        //Ripped and adapted from https://www.geeksforgeeks.org/java-math-random-method-examples/
        // define the range
        int max = openPositions.size() - 1;
        int min = 0;
        int range = max - min + 1;
        boolean foundPos = false;

        while(!foundPos)
        {
            int rand = (int) (Math.random() * range) + min;
            if(boardPieces[(int) openPositions.get(rand)].equals(" "))
            {
                boardPieces[(int) openPositions.get(rand)] = players[1];
                foundPos = true;
            }
        }
    }

    private static String setValidChoice(String startChoice)
    {
        if(startChoice.equalsIgnoreCase("yes") || startChoice.equalsIgnoreCase("no"))
        {
            validChoice = true;
            return startChoice;
        }
        else
        {
            System.out.println("Invalid option. Try again.");
            startChoice = in.nextLine();
            return startChoice;
        }
    }

    private static boolean isVsComputer(String startChoice)
    {
        if(startChoice.equalsIgnoreCase("yes"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static void nextPlayerTurn()
    {
        if(playerTurn == 0)
        {
            playerTurn = 1;
        }
        else
        {
            playerTurn = 0;
        }
    }

    private static void makeGridWithScoreBoard()
    {
        clearConsole();
        int counter = 0;
        String scoreTitle = "   Scoreboard";
        String underline = "===============";
        String player1 = " Player 1: " + playerScores[0];
        String player2 = " Player 2: " + playerScores[1];
        String ties = " Ties: " + playerScores[2];

        for(int i = 0; i <= 6; i++)
        {
            if(i % 2 == 0)
            {
                if(i == 0)
                {
                    System.out.printf("%28s%n", "|-----------|" + scoreTitle);
                }
                else if(i == 2)
                {
                    String gridPart = "|-----------|" + player1;
                    int spacer = gridPart.length() + 2;
                    System.out.printf("%" + spacer + "s%n", gridPart);
                }
                else if(i == 4)
                {
                    String gridPart = "|-----------|" + ties;
                    int spacer = gridPart.length() + 2;
                    System.out.printf("%" + spacer + "s%n", gridPart);
                }
                else
                {
                    System.out.printf("%15s%n", "|-----------|");
                }
            }
            else
            {
                if(i == 1)
                {
                    System.out.printf("%30s%n", "| "+boardPieces[counter]+" | "+boardPieces[counter + 1]+" | "+boardPieces[counter + 2]+" |" + underline);
                }
                else if(i == 3)
                {
                    String gridPart = "| "+boardPieces[counter]+" | "+boardPieces[counter + 1]+" | "+boardPieces[counter + 2]+" |" + player2;
                    int spacer = gridPart.length() + 2;
                    System.out.printf("%" + spacer + "s%n", gridPart);
                }
                else
                {
                    System.out.printf("%15s%n", "| "+boardPieces[counter]+" | "+boardPieces[counter + 1]+" | "+boardPieces[counter + 2]+" |");
                }
                counter += 3;
            }
        }
    }

    private static void checkBoard() throws InterruptedException, IOException
    {
        for(String player : players)
        {
            for(int[] win : winningCombinations)
            if(boardPieces[win[0]].equals(player) && boardPieces[win[1]].equals(player) && boardPieces[win[2]].equals(player))
            {
                gameOver = true;
            }
        }

        if(!gameOver)
        {
            int emptyCount = 0;
            for (String boardPiece : boardPieces)
            {
                if (boardPiece.equals(" "))
                {
                    emptyCount++;
                }
            }

            if(emptyCount == 0)
            {
                tiedGame = true;
                gameOver = true;
            }
        }


    }

    private static void scoreBoard()
    {
        System.out.printf("%27s%n","Scoreboard:  Player 1: " + playerScores[0]);
        System.out.printf("%27s%n", "Player 2: " + playerScores[1]);
    }

    private static void restartGame() throws InterruptedException
    {
        if(!tiedGame)
        {
            System.out.println("Player " + (lastPlayer+ 1) + " has won!");
            playerScores[lastPlayer] += 1;
        }
        else
        {
            System.out.println("The game has ended in a tie!");
            playerScores[2] += 1;
        }

        int counter = 3;
        while(counter > 0)
        {
            Thread.sleep(1000);
            System.out.print("Restarting game in " + counter + '\r');
            counter--;
        }

        clearBoardPieces();
        clearConsole();
        gameOver = false;
        tiedGame = false;
        playerTurn = 0;
    }

    private static void clearBoardPieces()
    {
        for(int i = 0; i < boardPieces.length; i++)
        {
            boardPieces[i] = " ";
        }
    }

    //From https://www.delftstack.com/howto/java/java-clear-console/ with some fixes
    public static void clearConsole(){
        try{
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private static void setConsoleWindow() throws IOException, InterruptedException
    {
        String operatingSystem = System.getProperty("os.name"); //Check the current operating system
        String commands[] = {"mode con: cols=30 lines=12", "color 5f", "title TicTacToe"};


        for(String command : commands)
        {
            if (operatingSystem.contains("Windows"))
            {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            }
        }
    }
}
