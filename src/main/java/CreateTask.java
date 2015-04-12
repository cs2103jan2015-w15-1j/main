package main.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

//@author A0122393L
/**
 * This class process the infomation and creates the Task (Object/s) accordingly
 * - creates a task if it is not recurring - creates multiple instances of the
 * recurring task
 * 
 * CreateTask will determine if it is a recurring Task or not by finding the
 * rate and frequency at which the Task is recurring at. - frequncy is
 * represented by YEARLY, MONTHLY, WEEKLY, DAILY - rate is represented by a
 * integer
 * 
 * CreateTask will then split the information up and find the start date and
 * time, end date and time of recurring, and date exceptions should there be any
 */
public class CreateTask {
    public static enum Type {
        YEARLY, MONTHLY, WEEKLY, DAILY,
    };

    private static final String KEYWORD = "every";
    private static final String EXCEPTWORD = "except";
    private static final String STARTWORD = "from";
    private static final String[] IGNOREWORD = { " day from", " week from",
            " month from", " year from", "everyday from" };
    private static final String[] IGNOREWORD2 = { "monday from",
            "tuesday from", "wednesday from", "thursday from", "friday from",
            "saturday from", "sunday from", "mon from", "tue from", "wed from",
            "thu from", "fri from", "sat from", "sun from" };
    private static final String[] ENDWORD = { "until", "till" };
    private static final String[] ENDWORD2 = { " to ", " by " };
    private static final String[] MAINWORD = { "daily", "everyday", "monthly",
            "weekly", "yearly" };
    private static final String[] YEARWORD = { "yearly", "year" };
    private static final String[] MONTHWORD = { "monthly", "month", "jan",
            "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct",
            "nov", "dec" };
    private static final String[] WEEKWORD = { "weekly", "week", "mon", "tue",
            "wed", "thu", "fri", "sat", "sun" };
    private static final String[] DAYWORD = { "day", "daily" };
    private static final int YEAR_LIMIT = 10;
    private static final int MONTH_LIMIT = 3;
    private static final int LENGHT_OF_KEYWORD = 6;

    private static CreateTask taskCreator;
    private DateParser dateParser;
    private ArrayList<Task> tempList;
    private LocalDateTime endDateTime;
    private LocalDateTime limit;
    private ArrayList<String> removedWords;
    private ArrayList<LocalDate> exceptionDates;
    private String endDateWords;
    private int recurRate;
    private String exceptionString;
    private String rawInfo;

    // -------------------------------------------------------------------
    // get instance of createTask
    // -------------------------------------------------------------------
    public static CreateTask getInstance() {
        if (taskCreator == null) {
            taskCreator = new CreateTask();
        }
        return taskCreator;
    }

    // -------------------------------------------------------------------
    // passes the information to various methods for processing
    // -------------------------------------------------------------------
    public ArrayList<Task> create(String input,
            ArrayList<LocalDateTime> parsedDates, String parsedWords,
            String nonParsedWords) {
        resetField();
        rawInfo = input;
        Type type = checkFrequency(input);
        Boolean hasIgnoreWords = false;
        Boolean hasIgnoreWords2 = false;

        ArrayList<LocalDateTime> recurDate = new ArrayList<LocalDateTime>();
        String recurId;
        Task task;
        recurId = UUID.randomUUID().toString();

        input = findExceptionDates(input);
        hasIgnoreWords = searchIgnoreWord(input, IGNOREWORD);
        hasIgnoreWords2 = searchIgnoreWord(input, IGNOREWORD2);

        if (type != null) {
            recurDate = new ArrayList<LocalDateTime>(findNeededDates(type,
                    input));

            if (!parsedDates.isEmpty()
                    && !hasIgnoreWords
                    && parsedDates.size() > 1
                    && !recurDate.get(0).toLocalDate().isEqual(LocalDate.now())
                    && recurDate.get(0).toLocalDate()
                            .isBefore(parsedDates.get(0).toLocalDate())
                    && !(hasIgnoreWords2 && recurDate
                            .get(0)
                            .toLocalDate()
                            .isEqual(
                                    parsedDates.get(0).toLocalDate()
                                            .minusWeeks(1)))) {
                recurDate.set(0, parsedDates.get(0));
            }

            if (!parsedDates.isEmpty()
                    && parsedDates.get(0).toLocalTime().getSecond() != recurDate
                            .get(0).toLocalTime().getSecond()) {
                LocalDateTime timeFix = LocalDateTime.of(recurDate.get(0)
                        .toLocalDate(), parsedDates.get(0).toLocalTime());
                recurDate.set(0, timeFix);
            }

            getEndDateTime(recurDate);

            if (!removedWords.isEmpty()) {
                for (String remove : removedWords) {
                    input = input.replace(remove, "");
                    nonParsedWords = nonParsedWords.replace(remove, "");
                }
            }

            nonParsedWords = findCommonWord(input, nonParsedWords);

            createRecurring(type, input, parsedWords, nonParsedWords,
                    recurDate, recurId, rawInfo);
        } else {
            task = new Task(input, parsedDates, parsedWords, nonParsedWords);
            tempList.add(task);
        }

        return tempList;
    }

    // -------------------------------------------------------------------
    // check if input consist any of the word in list
    // -------------------------------------------------------------------
    private Boolean searchIgnoreWord(String input, String[] list) {
        for (String string : list) {
            if (input.contains(string)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------
    // compare the 2 strings and returns the first similar substring
    // -------------------------------------------------------------------
    private String findCommonWord(String input, String nonParsedWords) {
        String result = null;
        int stringLength = Math.min(input.length(), nonParsedWords.length());

        for (int i = 0; i <= stringLength; i++) {
            if (input.substring(0, i).equals(nonParsedWords.substring(0, i))) {
                result = input.substring(0, i);
            } else {
                break;
            }
        }
        return result;
    }

    // -------------------------------------------------------------------
    // resets all the variables in CreateTask
    // -------------------------------------------------------------------
    private void resetField() {
        endDateTime = null;
        dateParser = DateParser.getInstance();
        recurRate = 1;
        tempList = new ArrayList<Task>();
        removedWords = new ArrayList<String>();
        exceptionDates = new ArrayList<LocalDate>();
        endDateWords = "";
        exceptionString = "";
        limit = null;
        rawInfo = "";
    }

    // -------------------------------------------------------------------
    // creates the instances of the recurring task
    // -------------------------------------------------------------------
    private void createRecurring(Type type, String input, String parsedWords,
            String nonParsedWords, ArrayList<LocalDateTime> recurDate,
            String recurId, String rawInfo) {
        Task task;
        LocalDateTime nextDate = null;
        boolean hasException = false;

        while (!recurDate.get(0).toLocalDate()
                .isAfter(endDateTime.toLocalDate())) {
            hasException = checkForException(recurDate.get(0));
            if (!hasException) {
                task = new Task(input, recurDate, parsedWords, nonParsedWords);
                task.setId(recurId);
                task.setRawInfo(rawInfo);
                task.setException(exceptionDates);
                tempList.add(task);
            }
            hasException = false;

            switch (type) {
            case DAILY:
                nextDate = recurDate.get(0).plusDays(recurRate);
                break;
            case WEEKLY:
                nextDate = recurDate.get(0).plusWeeks(recurRate);
                break;
            case MONTHLY:
                nextDate = recurDate.get(0).plusMonths(recurRate);
                break;
            case YEARLY:
                nextDate = recurDate.get(0).plusYears(recurRate);
                break;
            default:
                break;
            }
            if (!hasException) {
                recurDate.set(0, nextDate);
            }
        }

    }

    // -------------------------------------------------------------------
    // check if nextDate is and exception date
    // -------------------------------------------------------------------
    private Boolean checkForException(LocalDateTime nextDate) {
        for (LocalDate date : exceptionDates) {
            if (date.equals(nextDate.toLocalDate())) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------
    // find the start date time, end date time and the limit
    // -------------------------------------------------------------------
    private ArrayList<LocalDateTime> findNeededDates(Type type, String input) {
        ArrayList<LocalDateTime> result = new ArrayList<LocalDateTime>();
        ArrayList<LocalDateTime> tempResult = new ArrayList<LocalDateTime>();
        LocalDate tempDate;
        Boolean hasEndWord = false;

        // checks for "to" or "by" after "from"
        for (String check : ENDWORD2) {
            if (input.toLowerCase().contains(STARTWORD)) {
                String endCondition = input.substring(input.toLowerCase()
                        .indexOf(STARTWORD), input.length());
                dateParser.parse(endCondition);
                if (dateParser.getDates().size() > 1
                        && !dateParser
                                .getDates()
                                .get(0)
                                .toLocalDate()
                                .isEqual(
                                        dateParser.getDates().get(1)
                                                .toLocalDate())) {
                    if (endCondition.toLowerCase().contains(check)) {
                        endCondition = endCondition.substring(
                                endCondition.indexOf(check),
                                endCondition.length());
                        input = findEndDate(input, endCondition);
                        hasEndWord = true;
                        break;
                    }
                }
            }
        }

        // checks for "till" or "until"
        if (!hasEndWord) {
            for (String check : ENDWORD) {
                if (input.toLowerCase().contains(check)) {
                    String endCondition = input.substring(input.indexOf(check),
                            input.length());
                    input = findEndDate(input, endCondition);
                    break;
                }
            }
        }

        dateParser.parse(input);

        // search for the desired start date in the string based
        if (dateParser.getDates().size() > 1
                && input.toLowerCase().contains(STARTWORD)) {
            String subString = input.substring(input.indexOf(STARTWORD),
                    input.length());
            removedWords.add(dateParser.getParsedWords());
            tempResult.addAll(dateParser.getDates());

            dateParser.parse(subString);
            if (dateParser.getDates().isEmpty()) {
                tempDate = LocalDate.now();
            } else {
                tempDate = dateParser.getDates().get(0).toLocalDate();
            }
            for (LocalDateTime time : tempResult) {
                result.add(LocalDateTime.of(tempDate, time.toLocalTime()));
            }
            removedWords.add(dateParser.getParsedWords());
        } else if (input.toLowerCase().contains(STARTWORD)) {
            String subString = input.substring(input.indexOf(STARTWORD),
                    input.length());
            result = partialParse(subString, result);
            removedWords.add(dateParser.getParsedWords());
        } else {
            result = partialParse(input, result);
            removedWords.add(dateParser.getParsedWords());
        }

        // ensures that endDateTime is not the same as StartDateTime
        if (endDateTime != null
                && result.get(0).toLocalDate()
                        .isEqual(endDateTime.toLocalDate())) {
            result.add(endDateTime);
            endDateTime = null;
        }

        if (type.equals(Type.YEARLY)) {
            limit = result.get(0).plusYears(YEAR_LIMIT);
        } else if (type.equals(Type.MONTHLY)) {
            limit = result.get(0).plusYears(MONTH_LIMIT);
        } else {
            limit = result.get(0).plusYears(1);
        }
        return result;
    }

    // -------------------------------------------------------------------
    // adds current date time if no date is avaible from parsing
    // -------------------------------------------------------------------
    private ArrayList<LocalDateTime> partialParse(String input,
            ArrayList<LocalDateTime> result) {
        dateParser.parse(input);
        if (dateParser.getDates().isEmpty()) {
            result.add(LocalDateTime.now());
        } else {
            result.addAll(dateParser.getDates());
        }
        return result;
    }

    // -------------------------------------------------------------------
    // find and remove exception dates from the recurring task
    // -------------------------------------------------------------------
    private String findExceptionDates(String input) {
        if (input.toLowerCase().contains(EXCEPTWORD)) {
            exceptionString = input.substring(
                    input.toLowerCase().indexOf(EXCEPTWORD), input.length());
            exceptionString.replace(EXCEPTWORD, "");

            String[] split = exceptionString.split(",");
            for (String string : split) {
                try {
                    dateParser.parse(string);
                    exceptionDates.add(dateParser.getDates().get(0)
                            .toLocalDate());
                } catch (NullPointerException e) {
                }
            }
        }

        if (!exceptionString.equals(null)) {
            rawInfo = rawInfo.replace(exceptionString, "").trim();
            input = input.replace(exceptionString, "");
        }
        return input;
    }

    // -------------------------------------------------------------------
    // determines find the end date and remove from input
    // -------------------------------------------------------------------
    private String findEndDate(String input, String endCondition) {
        dateParser.parse(endCondition);
        endDateWords = dateParser.getParsedWords();
        if (!dateParser.getDates().isEmpty()) {
            endDateTime = dateParser.getDates().get(0);
        }
        input = input.replaceAll(endDateWords, "");
        removedWords.add(endDateWords);
        return input;
    }

    // -------------------------------------------------------------------
    // determine when to stop recurring of task
    // -------------------------------------------------------------------
    private void getEndDateTime(ArrayList<LocalDateTime> recurDate) {
        if (endDateTime == null) {
            if (recurDate.size() > 1
                    && !recurDate.get(recurDate.size() - 1).toLocalDate()
                            .isEqual(recurDate.get(0).toLocalDate())) {
                endDateTime = recurDate.get(recurDate.size() - 1);
            } else {
                endDateTime = limit;
            }
        }
    }

    // -------------------------------------------------------------------
    // checks if the input contains words to indicate recurring tasks
    // -------------------------------------------------------------------
    private Type checkFrequency(String input) {
        for (String check : MAINWORD) {
            if (input.toLowerCase().contains(check)) {
                switch (check) {
                case "yearly":
                    removedWords.add(check);
                    return Type.YEARLY;
                case "monthly":
                    removedWords.add(check);
                    return Type.MONTHLY;
                case "weekly":
                    removedWords.add(check);
                    return Type.WEEKLY;
                case "daily":
                    removedWords.add(check);
                    return Type.DAILY;
                case "everyday":
                    removedWords.add(check);
                    return Type.DAILY;
                }
            }
        }

        if (input.toLowerCase().contains(KEYWORD)) {
            String check = input.substring(input.lastIndexOf(KEYWORD)
                    + LENGHT_OF_KEYWORD, input.length());
            String[] split = check.split(" ");
            try {
                recurRate = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                recurRate = 1;
            }
            if (findFrequency(split, YEARWORD)) {
                return Type.YEARLY;
            } else if (findFrequency(split, MONTHWORD)) {
                return Type.MONTHLY;
            } else if (findFrequency(split, WEEKWORD)) {
                return Type.WEEKLY;
            } else if (findFrequency(split, DAYWORD)) {
                return Type.DAILY;
            }
        }
        return null;
    }

    // -------------------------------------------------------------------
    // checks if the splited string contains any frequency string
    // -------------------------------------------------------------------
    private Boolean findFrequency(String[] split, String[] frequency) {
        for (String find : frequency) {
            if (split[0].toLowerCase().contains(find)
                    || (split.length > 1 && split[1].toLowerCase().contains(
                            find))) {
                if (split.length > 1 && split[1].toLowerCase().contains(find)) {
                    removedWords.add(split[0] + " " + split[1]);
                    removedWords.add(KEYWORD + " " + split[0]);
                } else {
                    removedWords.add(KEYWORD + " " + split[0]);
                }
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------
    // methods for testing
    // -------------------------------------------------------------------
    private String getRecurRate() {
        return Integer.toString(recurRate);
    }

    // for testing if methods is able to give the correct rate and frequency
    public static String frequencyTest(String input) {
        String output = "";
        getInstance().resetField();
        Type frequency = getInstance().checkFrequency(input);
        try {
            switch (frequency) {
            case DAILY:
                output = getInstance().getRecurRate() + " daily";
                break;
            case WEEKLY:
                output = getInstance().getRecurRate() + " weekly";
                break;
            case MONTHLY:
                output = getInstance().getRecurRate() + " monthly";
                break;
            case YEARLY:
                output = getInstance().getRecurRate() + " yearly";
                break;
            default:
                output = getInstance().getRecurRate() + "";
                break;
            }
        } catch (NullPointerException e) {
            return output;
        }
        return output;
    }
}
