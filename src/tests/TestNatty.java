package tests;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.*;

import org.antlr.runtime.tree.Tree;

import com.joestelmach.natty.*;

public class TestNatty {
    public static void main(String[] args) {


        Parser parser = new Parser();
        
//        # With date & duration
//        do homework from 12pm to 6pm on 15 mar
//        do homework 1200 - 1800 on 15 mar
//        do homework on 15 mar 1200 - 1800
//        on 23 march, attend wedding
//
//        # With date but no duration
//        do assignment 2 tomorrow
//        do homework on 15 mar
//        attend meeting next week
//        remember to eat tomorrow
//        lunch with Betty on Wednesday
//        pay bill next month
//        attend birthday party right now
//
//
//        # No date & no duration
//        do homework
//        create 20 word poem
//        remember the milk
//        read 300 books
//        watch the movie with Cheryl
//        watch "28 days later"

        
        String[] inputs = {"5 mar",
                           "tomorrow",
                           "2.30pm to 2.40pm",
                           "1200 to 1400",
                           "4pm to 6:30pm on 15 mar",
                           "2pm to 4pm 3 apr",
                           "2pm to 4pm tomorrow",
                           "2 friday",
                           "assignment 23 friday",
                           "next friday",
                           "20 word poem thursday",
                           "assignment 2 from 12pm to 6pm today",
                           "meeting 23 march 1200 - 1400",
                           "meeting 1200 - 1400 23 march",
                           "add read harry potter on 12 apr at 1200",
                           "today 2359",
                           "2359 today",
                           "cs1231 tomorrow",
                           "fries",
                           "find girlfriend in 2016",
                           "2016",
                           "see doctor in may",
                           "pay bill next month",
                           "ma1521",
                           "cs1231",
                           "add do 2103 homework every tuesday until 29 april",
                           "add watch tv every monday 8pm to 9pm until 6 jun",
                           "add do nothing everyday",
                           "add do homework every monday 2pm to 3pm from 2 apr to 10 may",
                           "something something next week",
                           "monday tuesday wednesday thursday friday saturday sunday"
        };
        
        for (String input : inputs) {
            List<DateGroup> groups = parser.parse(input);
            
            if (!groups.isEmpty()) {
                input = fixInput1(input, groups);
                groups = parser.parse(input);
            }
            
            if (!groups.isEmpty()) {
                input = fixInput(input, groups);
                groups = parser.parse(input);
            }
    
            for (DateGroup group : groups) {
                List<Date> dates = group.getDates();
                System.out.println("INPUT: " + input);
                System.out.println(group.getParseLocations());
                Tree tree = group.getSyntaxTree();
                System.out.println(tree.toStringTree());

                for (Date d : dates) {
                    System.out.println(LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault()));
                }
                
                LocalDateTime ldt = LocalDateTime.ofInstant(group.getDates().get(0).toInstant(), ZoneId.systemDefault());
    //            LocalDateTime ldt2 = LocalDateTime.ofInstant(group.getDates().get(1).toInstant(), ZoneId.systemDefault());
                boolean isRecurring = group.isRecurring();
                Date recursUntil = group.getRecursUntil();
                System.out.println(ldt + " " + isRecurring + " " + recursUntil);
    //            System.out.println(ldt2);
            }
            System.out.println();
        }
        
        
        
    }

    private static String fixInput1(String input, List<DateGroup> groups) {
        DateGroup group = groups.get(0);
        int parsePosition = group.getPosition();
        ArrayList<String> splitParsed = new ArrayList<String>(Arrays.asList(group.getText().split(" ")));
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        for (String parsedWord : splitParsed) {
            int c = 0;
            for (String inputWord : splitInput) {
                c += inputWord.length() + 1;
                if (inputWord.contains(parsedWord)) {
                    if (inputWord.equals(parsedWord)) {
                        break;
                    } else if (c >= parsePosition) {
                        int position = parsePosition + input.substring(parsePosition).indexOf(parsedWord);
                        System.out.println("ERROR5: " + input + ", word: " + parsedWord + ", position: " +
                                position);
                        input = escapeWordInInput(input, position);
                    }                    
                }
            }
        }
        return input;
    }

    private static String fixInput(String input, List<DateGroup> groups) {
        DateGroup group = groups.get(0);
        Map<String, List<ParseLocation>> pLocations = group.getParseLocations();
        int parsePosition = group.getPosition();
        System.out.println("INPUT: " + input);
        System.out.println(pLocations);
        
        
        if (pLocations.containsKey("explicit_time")) {
            for (ParseLocation s : pLocations.get("explicit_time")) {
                if (s.getText().length() < 3) {
                    System.out.println("ERROR1: " + input + ", word: " + s + ", position: " + s.getStart());
                    input = escapeWordInInput(input, s.getStart());
                }
            }
        }
        
        if (pLocations.containsKey("spelled_or_int_optional_prefix")) {
            for (ParseLocation s : pLocations.get("spelled_or_int_optional_prefix")) {
                System.out.println("ERROR2: " + input + ", word: " + s + ", position: " +
                                   s.getStart());
                input = escapeWordInInput(input, s.getStart());
            }
        }
        
        if (pLocations.containsKey("relaxed_year")) {
            for (ParseLocation s : pLocations.get("relaxed_year")) {
                if (Year.parse(s.getText()).isBefore(Year.now())) {
                    System.out.println("ERROR3: " + input + ", word: " + s + ", position: " +
                            s.getStart());
                    input = addWordsBeforePosition(input, s.getStart(), Year.now().toString() + " ");
                }
            }
        }
        
        if (pLocations.containsKey("explicit_time") && !pLocations.containsKey("date")) {
            if (pLocations.get("explicit_time").size() == 1) {
                String parsedWord = pLocations.get("explicit_time").get(0).getText();
                int position = parsePosition + input.substring(parsePosition).indexOf(parsedWord);
                System.out.println("ERROR4: " + input + ", word: " + parsedWord + ", position: " +
                                position);
                input = addWordsBeforePosition(input, position, "1/1/");
//                input = addWordsBeforePosition(input, position, LocalDate.now().getMonthValue() + "/" + LocalDate.now().getDayOfMonth() + "/");
//                input += " " +  LocalDate.now().getMonthValue() + "/" + LocalDate.now().getDayOfMonth();
            }
        }

        return input;
    }

    private static String addWordsBeforePosition(String input, int start, String words) {
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        int c = 0;
        String output = "";
        for (String word : splitInput) {
            if (start >= c && start < c + word.length() + 1) {
                output += words + word;
            } else {
                output += word;
            }
            output += " ";
            c += word.length() + 1;
        }
        System.out.println("new input: " + output.trim());
        return output.trim();     
    }

    private static String escapeWordInInput(String input, int start) {
        ArrayList<String> splitInput = new ArrayList<String>(Arrays.asList(input.split(" ")));
        int c = 0;
        String output = "";
        for (String word : splitInput) {
            if (start >= c && start < c + word.length() + 1) {
                if (word.startsWith("\"") && word.endsWith("\"")) {
                    output += word;
                } else {
                    output += "\"" + word + "\"";
                }
            } else {
                output += word;
            }
            output += " ";
            c += word.length() + 1;
        }
        System.out.println("new input: " + output.trim());
        return output.trim();        
    }
    

}
