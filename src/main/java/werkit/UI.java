package werkit;

import data.workouts.Workout;
import storage.FileManager;
import storage.UnknownFileException;
import textcolors.TextColor;

import java.util.Scanner;

//import static textcolors.TextColor.COLOR_RESET;
//import static textcolors.TextColor.COLOR_YELLOW;

/**
 * This class contains all the user interface-related texts and methods for the WerkIt! Application.
 */
public class UI {
    // WerkIt ASCII Banner Logo Art
    public static final String WERKIT_BANNER_LOGO = " __        __        _    ___ _   _ \n"
            + " \\ \\      / /__ _ __| | _|_ _| |_| |\n"
            + "  \\ \\ /\\ / / _ \\ '__| |/ /| || __| |\n"
            + "   \\ V  V /  __/ |  |   < | || |_|_|\n"
            + "    \\_/\\_/ \\___|_|  |_|\\_\\___|\\__(_)\n"
            + "                                    ";

    public static final String WELCOME_MESSAGE = "Welcome to WerkIt!, your personal exercise planner.";
    public static final String GOODBYE_MESSAGE = "Thank you for using WerkIt! See you again soon...";
    // The default parameters for printing the formatting lines
    public static final int DEFAULT_LINE_LENGTH = 70;
    public static final String DEFAULT_LINE_CHAR = "-";
    // Prompt symbol
    public static final String PROMPT_SYMBOL = ">";
    // File loading-related messages
    public static final String FILE_LOAD_OK = "OK!";
    public static final String FILE_LOAD_NOT_OK = "Not OK...";
    public static final String LOADING_FILE_DATA_MSG = "Loading saved file data...";
    public static final String EXERCISES_FILE_LOADED_MSG =  "Exercises file\t%s\n";
    public static final String WORKOUTS_FILE_LOADED_MSG = "Workouts file\t%s\n";
    // Workout-related messages
    public static final String NEW_WORKOUT_CREATED_MESSAGE = "Alright, the following workout has been created:";

    // Scanner object for reading in user input from standard input
    Scanner inputReader = new Scanner(System.in);

    /**
     * Prints a line on the console based on the default parameters defined in this Java class.
     * <p>
     * Source: Team Member Alan Low's iP codebase
     * Link: https://github.com/alanlowzies/ip/blob/8556dd6a5106d190f5ac0458c6d2c34f98737a91/src/main/java/sora/SoraUI.java
     */
    public void printLine() {
        // Prints a line based on the default parameters
        printLine(DEFAULT_LINE_LENGTH, DEFAULT_LINE_CHAR);
    }

    /**
     * Prints a line on the console based on the specified length and the character/symbol to use
     * <p>
     * Source: Team Member Alan Low's iP codebase
     * Link: https://github.com/alanlowzies/ip/blob/8556dd6a5106d190f5ac0458c6d2c34f98737a91/src/main/java/sora/SoraUI.java
     *
     * @param lineLen The length of the line (measured in 'number of characters').
     * @param character The character that should be used to print the line.
     */
    public void printLine(int lineLen, String character) {
        // Generate a line of whitespaces
        String lineOfWhitespaces = String.format("%" + lineLen + "s", " ");

        // Replace each whitespace with the specified character
        String lineOfChars = lineOfWhitespaces.replaceAll(" ", character);

        // Finally, print out the line of characters
        System.out.println(lineOfChars);
    }

    public void printGreetings() {
        printLine(DEFAULT_LINE_LENGTH, "=");
        System.out.println(WERKIT_BANNER_LOGO);
        System.out.println(WELCOME_MESSAGE);
        printLine();
    }

    public void printGoodbye() {
        System.out.println(GOODBYE_MESSAGE);
        printLine(DEFAULT_LINE_LENGTH, "=");
    }

    /**
     * Prints a message that prompts the user to enter a command
     * <p>
     * Method adapted from Team Member Alan Low's iP codebase.
     * Link: https://github.com/alanlowzies/ip/blob/8556dd6a5106d190f5ac0458c6d2c34f98737a91/src/main/java/sora/SoraUI.java
     */
    public void printUserInputPrompt(boolean isFirstPrompt) {
        printLine();
        if (isFirstPrompt) {
            System.out.println("Now then, what can I do for you today?");
            System.out.println("(Need help? Type 'help' for a guide!)");
        } else {
            System.out.println("What's next?");
        }

        printLine();
        System.out.print(PROMPT_SYMBOL + " ");
    }

    /**
     * Reads in a line of the user from the standard input, trims the input, and returns it as a String.
     * <p>
     * Method adapted from Team Member Alan Low's iP codebase.
     * Link: https://github.com/alanlowzies/ip/blob/8556dd6a5106d190f5ac0458c6d2c34f98737a91/src/main/java/sora/SoraParser.java
     */
    protected String getUserInput() {
        String userInput = inputReader.nextLine();
        String userInputTrimmed = userInput.trim();
        printLine();

        return userInputTrimmed;
    }

    /**
     * Prints a message when a new workout has been created. The newly created workout will also be displayed.
     *
     * @param newWorkout The Workout object that is newly created.
     */
    public void printNewWorkoutCreatedMessage(Workout newWorkout) {
        System.out.println(NEW_WORKOUT_CREATED_MESSAGE);
        System.out.println();
        System.out.println("\t" + newWorkout.toString());
        System.out.println();
    }

    /**
     * Prints the text in colors pre-defined in TextColor class.
     * @param color ANSI color codes defined in TextColor class ONLY.
     * @param text The string text that needs to be colored.
     */
    public void printColorText(String color, String text) {
        System.out.println(color + text + TextColor.COLOR_RESET);
    }

    /**
     * Formats the string to contain the ANSI color code specified.
     *
     * @param color The color to format the string into.
     * @param text The text to be formatted with a color.
     * @return The string formatted with the ANSI color code.
     */
    public String getColorText(String color, String text) {
        String textWithColor = color + text + TextColor.COLOR_RESET;
        return textWithColor;
    }

    /**
     * Prints the help messages. To be updated.
     */
    public void printHelpMessage() {
        printListHelp();
        printLine();
        printWorkoutAddHelp();
        printLine();
        printWorkoutDeleteHelp();
        printLine();
        printWorkoutUpdateHelp();
        printLine();
        printExitHelp();
    }

    /**
     * Prints help message for 'list' command.
     */
    public void printListHelp() {
        System.out.println("\t To view all workouts, please enter:");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /list");
        System.out.println("\t This will print all the existing workouts.");
    }

    /**
     * Prints help message for 'workout /new' command.
     */
    public void printWorkoutAddHelp() {
        System.out.println("\t To add a workout, please enter: ");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /new <exercise name> /reps <no. of repetitions>");
        System.out.println("\t Example: ");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /new push up /reps 10");
        System.out.println("\t This will add a workout with 10 reps of push up.");
    }

    /**
     * Prints help message for 'workout /delete' command.
     */
    public void printWorkoutDeleteHelp() {
        System.out.println("\t To delete a workout, please enter: ");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /delete <index>");
        System.out.println("\t Example: ");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /delete 1");
        System.out.println("\t This will delete the workout with index 1 if exists.");
    }

    /**
     * Prints help message for 'workout /update' command.
     */
    public void printWorkoutUpdateHelp() {
        System.out.println("\t To update a workout, please enter: ");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /update <index> <quantity>");
        System.out.println("\t Example: ");
        printColorText(TextColor.COLOR_YELLOW, "\t workout /update 1 15");
        System.out.println("\t This will update the workout with index 1 to 15 reps if exists.");
    }

    /**
     * Prints help message for 'exit' command.
     */
    public void printExitHelp() {
        System.out.println("\t To exit werkIt, please enter: ");
        printColorText(TextColor.COLOR_YELLOW, "\t exit");
        System.out.println("\t This will exit werkIt.");
    }

    public void printLoadingFileDataMessage() {
        System.out.println(LOADING_FILE_DATA_MSG);
    }

    public void printFileLoadStatusMessage(String filename, boolean isLoadSuccessful) throws UnknownFileException {
        String statusMessage;
        if (isLoadSuccessful) {
            statusMessage = getColorText(TextColor.COLOR_GREEN, FILE_LOAD_OK);
        } else {
            statusMessage = getColorText(TextColor.COLOR_RED, FILE_LOAD_NOT_OK);
        }

        String messageToPrint;
        switch (filename) {
        case FileManager.EXERCISE_FILENAME:
            messageToPrint = EXERCISES_FILE_LOADED_MSG;
            break;
        case FileManager.WORKOUT_FILENAME:
            messageToPrint = WORKOUTS_FILE_LOADED_MSG;
            break;
        default:
            throw new UnknownFileException(filename, UnknownFileException.UNKNOWN_FILE_MSG);
        }

        System.out.printf(messageToPrint, statusMessage);
    }
}
