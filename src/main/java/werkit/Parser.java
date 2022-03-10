package werkit;

import commands.Command;
import commands.ExitCommand;
import commands.InvalidCommandException;
import commands.WorkoutCommand;
import commands.HelpCommand;
import commands.ExerciseCommand;
import data.exercises.ExerciseList;
import data.workouts.WorkoutList;
import storage.FileManager;

import java.io.IOException;

import static commands.WorkoutCommand.CREATE_ACTION_KEYWORD;
import static commands.WorkoutCommand.LIST_ACTION_KEYWORD;
import static commands.WorkoutCommand.DELETE_ACTION_KEYWORD;

/**
 * This class will parse the input that the user enters into the WerkIt! application into data
 * that can be further processed by other classes in this application.
 * Design of the commands is inspired by the AddressBook-Level2 project
 * Link: https://se-education.org/addressbook-level2/
 */
public class Parser {
    private UI ui;
    private ExerciseList exerciseList;
    private WorkoutList workoutList;
    private FileManager fileManager;

    public Parser(UI ui, ExerciseList exerciseList, WorkoutList workoutList, FileManager fileManager) {
        this.ui = ui;
        this.exerciseList = exerciseList;
        this.workoutList = workoutList;
        this.fileManager = fileManager;
    }

    public UI getUi() {
        return ui;
    }

    public ExerciseList getExerciseList() {
        return exerciseList;
    }

    public WorkoutList getWorkoutList() {
        return workoutList;
    }

    public Command parseUserInput(String userInput) throws ArrayIndexOutOfBoundsException,
            InvalidCommandException, IOException {
        // Check for illegal characters
        boolean hasIllegalCharacters = checkInputForIllegalCharacters(userInput);
        String className = this.getClass().getSimpleName();
        if (hasIllegalCharacters) {
            throw new InvalidCommandException(className, InvalidCommandException.ILLEGAL_CHARACTER_USED_ERROR_MSG);
        }

        // Determine the type of Command subclass to instantiate
        String commandKeyword = userInput.split(" ", 2)[0];

        switch (commandKeyword) {
        case WorkoutCommand.BASE_KEYWORD:
            return createWorkoutCommand(userInput);
        case ExitCommand.BASE_KEYWORD:
            return createExitCommand(userInput);
        case HelpCommand.BASE_KEYWORD:
            return createHelpCommand(userInput);
        case ExerciseCommand.BASE_KEYWORD:
            return createExerciseCommand(userInput);
        default:
            throw new InvalidCommandException(className, InvalidCommandException.INVALID_COMMAND_ERROR_MSG);
        }
    }

    private boolean checkInputForIllegalCharacters(String userInput) {
        for (String illegalCharacter : FileManager.ILLEGAL_CHARACTERS) {
            if (userInput.contains(illegalCharacter)) {
                return true;
            }
        }
        return false;
    }

    public WorkoutCommand createWorkoutCommand(String userInput) throws ArrayIndexOutOfBoundsException,
            InvalidCommandException, IOException {
        // Determine the action the user has entered
        String actionKeyword = userInput.split(" ", 3)[1];
        String arguments = null;
        switch (actionKeyword) {
        case CREATE_ACTION_KEYWORD:
        case DELETE_ACTION_KEYWORD:
            arguments = userInput.split(" ", 3)[2];
            break;
        case LIST_ACTION_KEYWORD:
            break;
        default:
            String className = this.getClass().getSimpleName();
            throw new InvalidCommandException(className, InvalidCommandException.INVALID_ACTION_ERROR_MSG);
        }
        return new WorkoutCommand(userInput, fileManager, workoutList, actionKeyword, arguments);
    }

    public ExitCommand createExitCommand(String userInput) {
        ExitCommand newCommand = new ExitCommand(userInput);
        return newCommand;
    }

    public ExerciseCommand createExerciseCommand(String userInput) throws InvalidCommandException {
        String actionKeyword = userInput.split(" ", 3)[1];
        String arguments = null;
        switch (actionKeyword) {
        case LIST_ACTION_KEYWORD:
            break;
        default:
            String className = this.getClass().getSimpleName();
            throw new InvalidCommandException(className, InvalidCommandException.INVALID_ACTION_ERROR_MSG);
        }
        return new ExerciseCommand(userInput, ui, exerciseList, actionKeyword, arguments);
    }

    public HelpCommand createHelpCommand(String userInput) {
        return new HelpCommand(userInput);
    }
}