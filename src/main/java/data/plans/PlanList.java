package data.plans;

import commands.PlanCommand;
import data.workouts.InvalidWorkoutException;
import data.workouts.Workout;
import data.workouts.WorkoutList;
import storage.LogHandler;
import textcolors.TextColor;
import werkit.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents an instance of a list of plans entered by the user.
 * It contains functionality to validate the user's inputs as well as allow the user to
 * create, list and delete plans.
 */
public class PlanList {
    public static final int MAX_NUMBER_OF_WORKOUTS_IN_A_PLAN = 10;
    public static final String RESERVED_PLAN_NAME = "rest day";
    private WorkoutList workoutList;
    private HashMap<String, Plan> plansHashMapList = new HashMap<>();
    private ArrayList<String> plansDisplayList = new ArrayList<>();
    private static Logger logger = Logger.getLogger(PlanList.class.getName());

    /**
     * Constructs an instance of the PlanList class.
     *
     * @param workoutList An instance of the WorkoutList class.
     */
    public PlanList(WorkoutList workoutList) {
        this.workoutList = workoutList;
        LogHandler.linkToFileLogger(logger);
    }

    /**
     * Gets the ArrayList of keys of Plan objects.
     * The keys of Plan objects are their unique plan names.
     *
     * @return An ArrayList of keys of Plan objects.
     */
    public ArrayList<String> getPlansDisplayList() {
        return this.plansDisplayList;
    }

    /**
     * Gets the HashMap of Plan objects.
     *
     * @return A HashMap of Plan objects.
     */
    public HashMap<String, Plan> getPlansHashMapList() {
        return this.plansHashMapList;
    }

    /**
     * Gets the Plan object based on its key as stored in the plansHashMapList.
     *
     * @param planKey The key that maps to the desired Plan object.
     * @return The Plan object that is mapped to planKey.
     */
    public Plan getPlanFromKey(String planKey) {
        return getPlansHashMapList().get(planKey);
    }

    /**
     * Retrieves the Plan object from the HashMap plansHashMapList based on the index number
     * of the object stored in plansDisplayList. This index number is the number shown in
     * 'plan /list'.
     *
     * @param indexNum The index number of the plan as shown in 'plan /list'.
     * @return The Plan object that corresponds to the index number.
     */
    public Plan getPlanFromIndexNum(int indexNum) {
        int elementNum = indexNum - 1;
        assert (elementNum >= 0);
        String keyValue = getPlansDisplayList().get(elementNum);
        Plan planObject = plansHashMapList.get(keyValue);
        return planObject;
    }

    /**
     * Retrieves the index number of a plan based on its position in the plansDisplayList
     * ArrayList.
     *
     * @param planName The name of the plan whose index number this method has to find.
     * @return An integer representing the index number the plan is listed in the plansDisplayList
     *         ArrayList.
     * @throws InvalidPlanException If the given plan name was not found in plansDisplayList.
     */
    public int getIndexNumFromPlanName(String planName) throws InvalidPlanException {
        for (int i = 0; i < getPlansDisplayList().size(); i += 1) {
            if (getPlansDisplayList().get(i).equals(planName)) {
                return (i + 1);
            }
        }

        String className = this.getClass().getSimpleName();
        throw new InvalidPlanException(className, InvalidPlanException.UNKNOWN_PLAN_NAME_ERROR_MSG);
    }

    /**
     * Parses the given user argument to identify the details of the new plan to be added.
     * Thereafter, the details will be checked for their validity. If all checks pass, a new
     * Plan object is instantiated before adding it to the ArrayList of plans.
     *
     * @param userArgument The user's details for the new plan, including plan name
     *                     and workout(s) to be added to the plan, via their workout number(s).
     * @return A Plan object that represents the new plan.
     * @throws ArrayIndexOutOfBoundsException If userArgument contains insufficient arguments and parsing fails.
     * @throws NumberFormatException If the workout number(s) specified in userArgument is an
     *                               invalid number.
     * @throws InvalidPlanException If the details specified in userArgument is invalid.
     */
    public Plan createAndAddPlan(String userArgument) throws ArrayIndexOutOfBoundsException,
            NumberFormatException, InvalidPlanException {
        String userPlanNameInput = userArgument.split(PlanCommand.CREATE_ACTION_WORKOUTS_KEYWORD)[0].trim();

        String className = this.getClass().getSimpleName();
        boolean hasValidPlanName = checkPlanNameValidity(userPlanNameInput);
        if (!hasValidPlanName) {
            logger.log(Level.WARNING, "Plan name is invalid.");
            throw new InvalidPlanException(className, InvalidPlanException.INVALID_PLAN_NAME_ERROR_MSG);
        }

        String userWorkoutNumbersString = userArgument.split(PlanCommand.CREATE_ACTION_WORKOUTS_KEYWORD)[1].trim();

        int numberOfWorkoutsInAPlan = userWorkoutNumbersString.split(",").length;
        boolean isAppropriateNumberOfWorkouts = checkMinMaxNumberOfWorkouts(numberOfWorkoutsInAPlan);
        if (!isAppropriateNumberOfWorkouts) {
            logger.log(Level.WARNING, "Number of workouts to add in a plan is invalid.");
            throw new InvalidPlanException(className, InvalidPlanException.MIN_MAX_WORKOUTS_IN_A_PLAN);
        }
        assert (numberOfWorkoutsInAPlan > 0 && numberOfWorkoutsInAPlan <= MAX_NUMBER_OF_WORKOUTS_IN_A_PLAN);

        ArrayList<Workout> workoutsToAddInAPlanList = new ArrayList<Workout>();
        for (int i = 0; i < numberOfWorkoutsInAPlan; i += 1) {
            int workoutNumberInteger = Integer.parseInt(userWorkoutNumbersString.split(",")[i].trim());

            boolean isWithinWorkoutListRange = checkWorkoutNumberWithinRange(workoutNumberInteger);
            if (!isWithinWorkoutListRange) {
                logger.log(Level.WARNING, "Workout number to add in the plan is invalid.");
                throw new InvalidPlanException(className, InvalidPlanException.WORKOUT_NUMBER_OUT_OF_RANGE);
            }
            assert (workoutNumberInteger > 0) && (workoutNumberInteger <= workoutList.getWorkoutsDisplayList().size());

            String workoutToAddKey = workoutList.getWorkoutsDisplayList().get(workoutNumberInteger - 1);
            Workout workoutToAddObject = workoutList.getWorkoutFromKey(workoutToAddKey);
            workoutsToAddInAPlanList.add(workoutToAddObject);
        }

        Plan newPlan = new Plan(userPlanNameInput, workoutsToAddInAPlanList);
        logger.log(Level.INFO, "New plan created.");

        String newPlanKey = newPlan.toString();
        plansHashMapList.put(newPlanKey, newPlan);
        plansDisplayList.add(newPlanKey);
        return newPlan;
    }

    /**
     * Checks if the provided plan details already exists in the ArrayList of plans. A plan
     * is considered to already exist in the list if the plan name matches an existing plan
     * name in the ArrayList. Additionally, checks if the plan name is called "rest day".
     * If it is, do not allow a plan called "rest day" as it is used in the schedule feature.
     *
     * @param userPlanNameInput The plan name entered by user to check.
     * @return True if an existing plan with the same plan name exists in the plans list.
     *         Otherwise, returns false.
     */
    public boolean checkPlanNameValidity(String userPlanNameInput) {
        String userPlanNameInputLowerCase = userPlanNameInput.toLowerCase();
        if (userPlanNameInputLowerCase.equals(RESERVED_PLAN_NAME)) {
            return false;
        }

        for (int i = 0; i < plansDisplayList.size(); i += 1) {
            String getPlanName = plansDisplayList.get(i).toLowerCase();

            if (userPlanNameInputLowerCase.equals(getPlanName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method checks whether the number of workouts to be added in a
     * plan is within the minimum and maximum number of workouts a plan can hold.
     * The range is 1 - 10 workouts.
     *
     * @param numberOfWorkouts The number of workouts to be added in a plan.
     * @return True if the number of workouts to be added is within the min
     *         and max range, else false.
     */
    public boolean checkMinMaxNumberOfWorkouts(int numberOfWorkouts) {
        return numberOfWorkouts > 0 && numberOfWorkouts <= MAX_NUMBER_OF_WORKOUTS_IN_A_PLAN;
    }

    /**
     * This method checks whether the workout number supplied
     * is within the range of the current workout list (in workout /list).
     *
     * @param workoutNumber The workout number to check.
     * @return True if workout number is within the range of the workout list,
     *         else false if out of range.
     */
    public boolean checkWorkoutNumberWithinRange(int workoutNumber) {
        return workoutNumber > 0 && workoutNumber <= workoutList.getWorkoutsDisplayList().size();
    }

    public void insertPlanIntoList(String planKey, Plan plan) throws InvalidWorkoutException {
        boolean isExistingWorkout = true;
        String className = this.getClass().getSimpleName();
        var workoutData = plan.getWorkoutsInPlanList();
        for (Workout workoutInPlanFile : workoutData) {
            if (!workoutList.getWorkoutsHashMapList().containsKey(workoutInPlanFile.toString())) {
                isExistingWorkout = false;
                break;
            }
        }
        if (!isExistingWorkout) {
            throw new InvalidWorkoutException(className, InvalidWorkoutException.INVALID_WORKOUT_ERROR_MSG);
        }
        plansHashMapList.put(planKey, plan);
        plansDisplayList.add(planKey);
    }

    /**
     * Prints all the plan names that are stored in the plans list.
     */
    public void listAllPlan() {
        if (getPlansDisplayList().size() <= 0) {
            System.out.println("Oops! You have not created any plans yet!"
                    + "\nTo create a new plan, enter 'plan /new <plan name> /workouts "
                    + "\n<workout number(s) to add, separated by comma>'."
                    + "\nAlternatively, enter 'help' for more information.");
            return;
        }

        assert (getPlansDisplayList().size() > 0);
        System.out.println("Here are all your plan(s).");
        System.out.println("To view each plan in detail, enter\n'plan /details <plan number in list>'.\n");
        for (int i = 0; i < getPlansDisplayList().size(); i += 1) {
            System.out.println((i + 1) + ". " + getPlansDisplayList().get(i));
        }
    }

    /**
     * Prints the plan details, workouts in the plan.
     *
     * @param userArgument The argument entered by user, that is, the index of plan to view details.
     * @param ui An instance of the UI class.
     */
    public void listPlanDetails(String userArgument, UI ui) throws NumberFormatException {
        int indexOfPlan = Integer.parseInt(userArgument.trim());
        Plan planToViewDetails = getPlanFromIndexNum(indexOfPlan);
        String planName = getPlansDisplayList().get(indexOfPlan - 1);

        ArrayList<Workout> workoutsInPlanList = planToViewDetails.getWorkoutsInPlanList();
        int numberOfWorkoutsInPlan = workoutsInPlanList.size();
        assert ((numberOfWorkoutsInPlan <= MAX_NUMBER_OF_WORKOUTS_IN_A_PLAN) && (numberOfWorkoutsInPlan > 0)) :
                "Total number of workouts is not valid.";

        System.out.println("Here are the " + numberOfWorkoutsInPlan + " workouts in ["
                + ui.getColorText(TextColor.COLOR_YELLOW, planName) + "].");

        for (int i = 0; i < numberOfWorkoutsInPlan; i++) {
            System.out.println((i + 1) + ". " + workoutsInPlanList.get(i).toString());
        }
    }

    /**
     * Removes the intended plan in the plans list.
     * The plan to delete is determined by the user who will
     * indicate the index of plan to delete in the plan list.
     *
     * @param userArgument The argument entered by user, that is, the index of plan to delete.
     * @return deletedPlan The plan object that is deleted from the plansDisplayList.
     * @throws NumberFormatException If index of plan that user entered is not an integer.
     * @throws InvalidPlanException If index of plan to delete is out of range.
     */
    public Plan deletePlan(String userArgument) throws NumberFormatException, InvalidPlanException {
        int indexToDelete = Integer.parseInt(userArgument.trim());
        String className = this.getClass().getSimpleName();

        boolean isPlanIndexToDeleteValid = checkPlanIndexIsWithinRange(indexToDelete);

        if (!isPlanIndexToDeleteValid) {
            throw new InvalidPlanException(className, InvalidPlanException.PLAN_INDEX_OUT_OF_RANGE);
        }

        assert (indexToDelete > 0) && (indexToDelete <= plansDisplayList.size());
        Plan deletedPlan = getPlanFromIndexNum(indexToDelete);
        String deletedPlanKey = deletedPlan.toString();
        plansDisplayList.remove(indexToDelete - 1);
        getPlansHashMapList().remove(deletedPlanKey);
        return deletedPlan;
    }

    /**
     * Checks whether the plan number is within the range of the current plan list (in plan /list).
     *
     * @param planIndex The plan number to check.
     * @return True if plan number is within the range of the plan list, else false if out of range.
     */
    private boolean checkPlanIndexIsWithinRange(int planIndex) {
        return planIndex > 0 && planIndex <= plansDisplayList.size();
    }

    /**
     * Checks whether the workout is in the plan.
     *
     * @param workoutToCheck The workout to look for in the plan.
     * @param plan An instance of the Plan class.
     * @return True if workout is found in the plan, else false if the workout is not in the plan.
     * @throws ArrayIndexOutOfBoundsException For operations which involves index checking.
     */
    private boolean checkWorkoutInPlan(String workoutToCheck, Plan plan) throws ArrayIndexOutOfBoundsException {
        ArrayList<Workout> workoutsInPlanList = plan.getWorkoutsInPlanList();
        String workoutInPlanDetails;
        for (Workout workoutsInPlan : workoutsInPlanList) {
            workoutInPlanDetails = workoutsInPlan.toString();
            if (workoutToCheck.equals(workoutInPlanDetails)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the number of plans which includes the deleted workout.
     *
     * @param workoutToCheck The workout to look for in the plan.
     * @return An Arraylist of integer which includes the number of the
     *         plan that includes the deleted workout.
     * @throws ArrayIndexOutOfBoundsException For operations which involves index checking.
     */
    private ArrayList<Integer> findPlanContainsDeletedWorkout(String workoutToCheck) throws
             ArrayIndexOutOfBoundsException {
        Plan planObject;
        boolean isWorkoutInPlan = false;
        ArrayList<Integer> planWithDeletedWorkout = new ArrayList<Integer>();
        for (int i = 1; i <= getPlansDisplayList().size(); i++) {
            planObject = getPlanFromIndexNum(i);
            isWorkoutInPlan = checkWorkoutInPlan(workoutToCheck, planObject);
            if (isWorkoutInPlan) {
                planWithDeletedWorkout.add(i);
            }
        }
        if (isWorkoutInPlan) {
            System.out.println(workoutToCheck + " is found in:\n");
        }
        return planWithDeletedWorkout;
    }

    /**
     * Deletes the plans which contains the deleted workouts.
     *
     * @param workoutToCheck The deleted workout.
     * @throws ArrayIndexOutOfBoundsException If index .
     * @throws InvalidPlanException For operations which involves index checking.
     */
    public void deletePlanContainsDeletedWorkout(String workoutToCheck) throws ArrayIndexOutOfBoundsException,
            InvalidPlanException {
        ArrayList<Integer> planWithDeletedWorkout = findPlanContainsDeletedWorkout(workoutToCheck);
        if (planWithDeletedWorkout.size() <= 0) {
            return;
        }
        for (int planNumber : planWithDeletedWorkout) {
            System.out.println("\t" + getPlansDisplayList().get(planNumber - 1));
            assert (checkPlanIndexIsWithinRange(planNumber)) : "Plan number is out of range.";
        }
        int totalNumberOfPlanToDelete = planWithDeletedWorkout.size();
        for (int i = 0; i < totalNumberOfPlanToDelete; i++) {
            if (i == 0) {
                System.out.println("\nThe following plan has been removed:\n");
            }
            System.out.println((i + 1) + ". " + getPlansDisplayList().get(planWithDeletedWorkout.get(i) - i - 1));
            deletePlan(Integer.toString(planWithDeletedWorkout.get(i) - i));
        }
    }
}
