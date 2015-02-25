import java.util.Scanner;


public class Veto {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Controller controller = new Controller(args);

        while (!controller.isTimeToExit()) {
            String userInput = scanner.nextLine();
            String feedback = controller.execute(userInput);
            showLine(feedback);
        }

        scanner.close();
    }

    private static void showLine(String feedback) {
        System.out.println(feedback);
    }

}
