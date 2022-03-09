package werkit;

import commands.Command;
import commands.ExitCommand;
import commands.InvalidCommandException;
import data.exercises.ExerciseList;
import data.workouts.WorkoutList;
import storage.FileManager;
import storage.UnknownFileException;

import java.io.IOException;

/**
 * This class initiates the various classes/components of WerkIt! and contains the logic code for
 * prompting the user for an input continuously until the user enters the exit command.
 */
public class WerkIt {
    private UI ui;
    private Parser parser;
    private ExerciseList exerciseList;
    private WorkoutList workoutList;
    private FileManager fileManager;

    /**
     * Initialises the components of the WerkIt! application, greets the user, and loads the
     * various files stored in the system's local disk into this instance of WerkIt! (if applicable).
     */
    public WerkIt() {
        // Initialise Components
        this.ui = new UI();
        this.exerciseList = new ExerciseList();
        this.workoutList = new WorkoutList(getExerciseList());
        this.parser = new Parser(getUI(), getExerciseList(), getWorkoutList());
        this.fileManager = new FileManager();

        getUI().printGreetings();

        // Do file imports
        getUI().printLoadingFileDataMessage();
        try {
            loadExerciseFile();
            loadWorkoutFile();
        } catch (UnknownFileException e) {
            // TODO: handle exception
        }
    }

    public UI getUI() {
        return this.ui;
    }

    public Parser getParser() {
        return this.parser;
    }

    public ExerciseList getExerciseList() {
        return this.exerciseList;
    }

    public WorkoutList getWorkoutList() {
        return this.workoutList;
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    /**
     * Continuously prompts the user for an input (and thereafter executing the necessary
     * actions) until the exit command is entered.
     * <p>
     * Method adapted from Team Member Alan Low's iP codebase.
     * Link: https://github.com/alanlowzies/ip/blob/8556dd6a5106d190f5ac0458c6d2c34f98737a91/src/main/java/sora/Sora.java
     */
    public void startContinuousUserPrompt() {
        boolean userWantsToExit = false;
        boolean isFirstPrompt = true;

        do {
            try {
                getUI().printUserInputPrompt(isFirstPrompt);
                isFirstPrompt = false;
                String userInput = getUI().getUserInput();
                Command newCommand = getParser().parseUserInput(userInput);

                if (newCommand instanceof ExitCommand) {
                    userWantsToExit = true;
                    continue;
                }

                newCommand.execute();

            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
                System.out.println("Please try again.");
            }
        } while (!userWantsToExit);

        // User is exiting the program
        getUI().printGoodbye();
    }

    private void loadExerciseFile() throws UnknownFileException {
        boolean isExerciseFileLoadSuccessful = false;
        try {
            fileManager.loadExercisesFromFile(getExerciseList());
            isExerciseFileLoadSuccessful = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            getUI().printFileLoadStatusMessage(FileManager.EXERCISE_FILENAME,
                    isExerciseFileLoadSuccessful);
        }
    }

    private void loadWorkoutFile() throws UnknownFileException {
        boolean isWorkoutFileLoadSuccessful = false;
        try {
            isWorkoutFileLoadSuccessful = fileManager.loadWorkoutsFromFile(getWorkoutList());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            getUI().printFileLoadStatusMessage(FileManager.WORKOUT_FILENAME,
                    isWorkoutFileLoadSuccessful);
        }
    }
}
