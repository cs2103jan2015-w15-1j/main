package main.java;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class TaskCreator {
    // VERY VERY VERY VERY BUGGY!!!!
    // seem to be working add for recuring:
    // "add do homework every 2 monday from 3 apr to 25 jul"
    // "add do nothing every week from today to 30 jul"
    // needs more testing
    public static enum Type {
        YEARLY, MONTHLY, WEEKLY, DAILY,
    };

    private static String keyword = "every";
    private static String startWord = "from";
    private static String[] endWord = { "until", "til", "to", "by" };
    private static String[] mainWords = { "daily", "everyday", "monthly",
            "weekly", "yearly" };
    private static String[] yearWords = { "yearly", "year" };
    private static String[] monthWords = { "monthly", "month", "jan", "feb",
            "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov",
            "dec" };
    private static String[] weekWords = { "weekly", "week", "mon", "tue",
            "wed", "thu", "fri", "sat", "sun" };
    private static String[] dayWords = { "day", "daily" };

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
            switch (type) {
            case YEARLY:
                createYearly(input, parsedWords, nonParsedWords, recurDate,
                        recurID);
                break;
            case MONTHLY:
                createMonthly(input, parsedWords, nonParsedWords, recurDate,
                        recurID);
                break;
            case WEEKLY:
                createWeekly(input, parsedWords, nonParsedWords, recurDate,
                        recurID);
                break;
            case DAILY:
                createDaily(input, parsedWords, nonParsedWords, recurDate,
                        recurID);
                break;
            default:
                break;
            }
        } else {
            task = new Task(input, parsedDates, parsedWords, nonParsedWords);
            tempList.add(task);
        }

         System.out.println("templist: " + tempList);
         System.out.println("parsedDates: " + parsedDates);
         System.out.println("rate: " + recurRate);
         System.out.println("type: " + type);
        return tempList;
    }

    private ArrayList<LocalDateTime> findRecurDates(String input) {
        ArrayList<LocalDateTime> result = new ArrayList<LocalDateTime>();
        for (String check : endWord) {
            if (input.toLowerCase().contains(check)) {
                String endCondition = input.substring(input.indexOf(check),
                        input.length());
                System.out.println(endCondition);
                dateParser.parse(endCondition);
                endDateTime = dateParser.getDates().get(0);
            }
        }

        if (input.toLowerCase().contains(startWord)) {
            dateParser.parse(input);
            result.addAll(dateParser.getDates());
        } else {
            result.add(LocalDateTime.now());
        }
        limit = result.get(0).plusYears(1);
        return result;
    }

    private void createDaily(String input, String parsedWords,
            String nonParsedWords, ArrayList<LocalDateTime> recurDate,
            String recurID) {
        Task task;
        while (!recurDate.get(0).isAfter(limit)
                && !recurDate.get(0).isAfter(
                        recurDate.get(recurDate.size() - 1))) {
            task = new Task(input, recurDate, parsedWords, nonParsedWords);
            task.setID(recurID);
            tempList.add(task);
            LocalDateTime nextDate = recurDate.get(0).plusDays(recurRate);
            recurDate.set(0, nextDate);
        }
    }

    private void createWeekly(String input, String parsedWords,
            String nonParsedWords, ArrayList<LocalDateTime> recurDate,
            String recurID) {
        Task task;
        while (!recurDate.get(0).isAfter(limit)
                && !recurDate.get(0).isAfter(
                        recurDate.get(recurDate.size() - 1))) {
            task = new Task(input, recurDate, parsedWords, nonParsedWords);
            task.setID(recurID);
            tempList.add(task);
            LocalDateTime nextDate = recurDate.get(0).plusWeeks(recurRate);
            recurDate.set(0, nextDate);
        }
    }

    private void createMonthly(String input, String parsedWords,
            String nonParsedWords, ArrayList<LocalDateTime> recurDate,
            String recurID) {
        Task task;
        while (!recurDate.get(0).isAfter(limit)
                && !recurDate.get(0).isAfter(
                        recurDate.get(recurDate.size() - 1))) {
            task = new Task(input, recurDate, parsedWords, nonParsedWords);
            task.setID(recurID);
            tempList.add(task);
            LocalDateTime nextDate = recurDate.get(0).plusMonths(recurRate);
            recurDate.set(0, nextDate);
            // TESTING NONSENSE
            System.out.println("recuring date:" + recurDate.get(0));
        }
    }

    private void createYearly(String input, String parsedWords,
            String nonParsedWords, ArrayList<LocalDateTime> recurDate,
            String recurID) {
        Task task;
        while (!recurDate.get(0).isAfter(limit)
                && !recurDate.get(0).isAfter(
                        recurDate.get(recurDate.size() - 1))) {
            task = new Task(input, recurDate, parsedWords, nonParsedWords);
            task.setID(recurID);
            tempList.add(task);
            LocalDateTime nextDate = recurDate.get(0).plusYears(recurRate);
            recurDate.set(0, nextDate);
        }
    }

    // Checks if the input contains words to indicate recurring tasks
    private Type checkRecurring(String input) {
        for (String check : mainWords) {
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

        if (input.toLowerCase().contains(keyword)) {
            String check = input.substring(input.lastIndexOf(keyword) + 6,
                    input.length());
            String[] split = check.split(" ");
            try {
                recurRate = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                recurRate = 1;
            }
            for (String find : yearWords) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.YEARLY;
                }
            }
            for (String find : monthWords) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.MONTHLY;
                }
            }
            for (String find : weekWords) {
                if (split[0].toLowerCase().contains(find)
                        || (split.length > 1 && split[1].toLowerCase()
                                .contains(find))) {
                    return Type.WEEKLY;
                }
            }
            for (String find : dayWords) {
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
