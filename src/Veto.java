import java.util.Scanner;


public class Veto {
    private static final String MESSAGE_COMMAND_PROMPT = "Command: ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Controller controller = new Controller(args);
        showLine(controller.getWelcomeMessage());

        while (!controller.isTimeToExit()) {
            show(MESSAGE_COMMAND_PROMPT);
            String userInput = scanner.nextLine();
            String feedback = controller.executeCommand(userInput);
            showLine(feedback);
        }

        scanner.close();
    }

    public static void show(String text) {
        System.out.print(text);
    }

    private static void showLine(String text) {
        System.out.println(text);
    }

}
