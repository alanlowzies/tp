package data.exercises;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.LogHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExerciseListTest {
    ExerciseList exerciseList;

    @BeforeEach
    void setUp() {
        LogHandler.startLogHandler();
        exerciseList = new ExerciseList();
        String firstSampleExercise = "push up";
        String secondSampleExercise = "pull up";
        exerciseList.addExerciseToList(firstSampleExercise);
        exerciseList.addExerciseToList(secondSampleExercise);
    }

    @Test
    public void isCorrectQuantity() {
        assertEquals(2, exerciseList.getNumberOfExercises());
    }

    @Test
    public void isCorrectIndex() {
        assertEquals("pull up", exerciseList.getExerciseAt(2));
    }

    @Test
    public void isExist() {
        assertTrue(exerciseList.checkIfExerciseExists("pull up"));
    }
}

