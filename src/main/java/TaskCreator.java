package main.java;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class TaskCreator {
    // Not fully Tested!!!!
    // seem to be working add for recuring:
    // "add do homework every 2 monday from 3 apr to 25 jul"
    // "add do nothing every week from today to 30 jul"
    // "add foo every friday"
    // "add foo every day from 2pm to 4pm"
    // "add foo every week until 20 may"
    // "add foo weekly from 2pm to 5pm until 21 may"
    // "add foo every month from 4 apr to 6 sep"
    // needs more testing

    public static enum Type {
        YEARLY, MONTHLY, WEEKLY, DAILY,
    };

    private static final String KEYWORD = "every";
    private static final String STARTWORD = "from";
    private static final String[] ENDWORD = { "until", "til", "by" };
    private static final String[] MAINWORD = { "daily", "everyday", "monthly",
            "weekly", "yearly" };
    private static final String[] YEARWORD = { "yearly", "year" };
    private static final String[] MONTHWORD = { "monthly", "month", "jan",
            "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct",
            "nov", "dec" };
    private static final String[] WEEKWORD = { "weekly", "week", "mon", "tue",
            "wed", "thu", "fri", "sat", "sun" };
    private static final String[] DAYWORD = { "day", "daily" };

    private static TaskCreator taskCreator;
    private DateParser dateParser;
    private ArrayList<Task> tempList;
    private LocalDateTime endDateTime;
    private LocalDateTime limit;
    private int recurRate;

    public static TaskCreator getInstance() {
        if (taskCreator == null) {
            taskCreator = new TaskCreator();
        }
        return taskCreator;
    }

    public ArrayList<Task> create(String input,
            ArrayList<LocalDateTime> parsedDates, String parsedWords,
            String nonParsedWords) {
        endDateTime = null;
        dateParser = DateParser.getInstance();
        recurRate = 1;
        tempList = new ArrayList<Task>();
        Type type = checkRecurring(input);

        ArrayList<LocalDateTime> recurDate = new ArrayList<LocalDateTime>();
        String recurID;
        Task task;
        recurID = UUID.randomUUID().toString();

        if (type != null) {
            recurDate = new ArrayList<LocalDateTime>(findRecurDates(input));
            getEndDateTime(recurDate);
            createRecurring(type, input, parsedWords, nonParsedWords,
                    recurDate, recurID);
        } else {
            task = new Task(input, parsedDates, parsedWords, nonParsedWords);
            tempList.add(task);
        }

        return tempList;
    }

    private void createRecurring(Type type, String input, String parsedWords,
            String nonParsedWords, ArrayList<LocalDateTime> recurDate,
            String recurID) {
        Task task;
        LocalDateTime nextDate = null;

        while (!recurDate.get(0).isAfter(endDateTime)) {
            task = new Task(input, recurDate, parsedWords, nonParsedWords);
            task.setID(recurID);
            tempList.add(task);
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
            recurDate.set(0, nextDate);
        }

    }

    private ArrayList<LocalDateTime> findRecurDates(String input) {
        ArrayList<LocalDateTime> result = new ArrayList<LocalDateTime>();
        for (String check : ENDWORD) {
            if (input.toLowerCase().contains(check)) {
                String endCondition = input.substring(input.indexOf(check),
                        input.length());
                System.out.println(endCondition);
                dateParser.parse(endCondition);
                endDateTime = dateParser.getDates().get(0);
                input = input.replaceAll(endCondition, "");
                break;
            }
        }

        if (input.toLowerCase().contains(STARTWORD)) {
            String subString = input.substring(input.indexOf(STARTWORD),
                    input.length());
            dateParser.parse(subString);
            result.addAll(dateParser.getDates());
        } else {
            dateParser.parse(input);
            if (dateParser.getDates().size() < 1) {
                result.add(LocalDateTime.now());
            } else {
                result.addAll(dateParser.getDates());
            }
        }
        limit = result.get(0).plusYears(1);
        return result;
    }

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

    // Checks if the input contains words to indicate recurring tasks
    private Type checkRecurring(String input) {
        for (String check : MAINWORD) {
            if (input.toLowerCase().contains(check)) {
                switch (check) {
                case "yearly":
                    return Type.YEARLY;
                case "monthly":
                    return Type.MONTHLY;
                case "weekly":
                    return Type.WEEKLY;
                case "daily":
                    return Type.DAILY;
                case "everyday":
                    return Type.DAILY;
                }
            }
        }

        if (input.toLowerCase().contains(KEYWORD)) {
            String check = input.substring(input.lastIndexOf(KEYWORD) + 6,
                    input.length());
            String[] split = check.split(" ");
            try {
                recurRate = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                recurRate = 1;
            }
            for (String find : YEARWORD) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.YEARLY;
                }
            }
            for (String find : MONTHWORD) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.MONTHLY;
                }
            }
            for (String find : WEEKWORD) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.WEEKLY;
                }
            }
            for (String find : DAYWORD) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.DAILY;
                }
            }
            System.out.println(recurRate);
        }
        return null;
    }
}
