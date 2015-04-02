package main.java;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class TaskCreator {
    // VERY VERY VERY VERY BUGGY!!!!
    // seem to be working add for recuring:
    // "add do homework every 2 monday from 3 apr to 25 may"
    // normal task seem to be fine
    public static enum Type {
        YEARLY, MONTHLY, WEEKLY, DAILY,
    };

    private static String keyword = "every";
    private static String[] mainWords = { "daily", "everyday", "monthly",
            "weekly", "yearly" };
    private static String[] yearWords = { "yaer" };
    private static String[] monthWords = { "month", "jan", "feb", "mar", "apr",
            "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
    private static String[] weekWords = { "mon", "tue", "wed", "thu", "fri",
            "sat", "sun" };
    private static String[] dayWords = { "day" };

    private static TaskCreator taskCreator;
    private ArrayList<Task> tempList;
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

        recurRate = 1;
        tempList = new ArrayList<Task>();
        Type type = checkRecurring(input);

        ArrayList<LocalDateTime> recurDate = new ArrayList<LocalDateTime>(
                parsedDates);
        String recurID;
        Task task;
        recurID = UUID.randomUUID().toString();

        if (type != null) {
            switch (type) {
            case YEARLY:
                while (recurDate.get(0).isBefore(
                        parsedDates.get(parsedDates.size() - 1))) {
                    task = new Task(input, recurDate, parsedWords,
                            nonParsedWords);
                    task.setID(recurID);
                    tempList.add(task);
                    LocalDateTime nextDate = recurDate.get(0).plusYears(
                            recurRate);
                    recurDate.set(0, nextDate);
                }
                break;
            case MONTHLY:
                while (recurDate.get(0).isBefore(
                        parsedDates.get(parsedDates.size() - 1))) {
                    task = new Task(input, recurDate, parsedWords,
                            nonParsedWords);
                    task.setID(recurID);
                    tempList.add(task);
                    LocalDateTime nextDate = recurDate.get(0).plusMonths(
                            recurRate);
                    recurDate.set(0, nextDate);
                    // TESTING NONSENSE
                    System.out.println("recuring date:" + recurDate.get(0));
                }
                break;
            case WEEKLY:
                while (recurDate.get(0).isBefore(
                        parsedDates.get(parsedDates.size() - 1))) {
                    task = new Task(input, recurDate, parsedWords,
                            nonParsedWords);
                    task.setID(recurID);
                    tempList.add(task);
                    LocalDateTime nextDate = recurDate.get(0).plusWeeks(
                            recurRate);
                    recurDate.set(0, nextDate);
                }
                break;
            case DAILY:
                while (recurDate.get(0).isBefore(
                        parsedDates.get(parsedDates.size() - 1))) {
                    task = new Task(input, recurDate, parsedWords,
                            nonParsedWords);
                    task.setID(recurID);
                    tempList.add(task);
                    LocalDateTime nextDate = recurDate.get(0).plusDays(
                            recurRate);
                    recurDate.set(0, nextDate);
                }
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
            recurRate = Integer.parseInt(split[0]);
            for (String find : yearWords) {
                if (split[0].toLowerCase().contains(find) || split[1].toLowerCase().contains(find)) {
                    return Type.YEARLY;
                }
            }
            for (String find : monthWords) {
                if (split[0].toLowerCase().contains(find) || split[1].toLowerCase().contains(find)) {
                    return Type.MONTHLY;
                }
            }
            for (String find : weekWords) {
                if (split[0].toLowerCase().contains(find) || split[1].toLowerCase().contains(find)) {
                    return Type.WEEKLY;
                }
            }
            for (String find : dayWords) {
                if (split[0].toLowerCase().contains(find) || split[1].toLowerCase().contains(find)) {
                    return Type.DAILY;
                }
            }
            System.out.println(recurRate);
        }
        return null;
    }
}
