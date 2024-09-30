import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class QuizApp {

    private static final String Valid_Users = "./src/main/resources/users.json";
    private static final String Set_of_Questions = "./src/main/resources/questions.json";
    private static final int Number_of_ques = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username");
        String username = scanner.nextLine();
        System.out.println("Enter your password");
        String password = scanner.nextLine();

        JSONObject user = userAuthentication(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.Please,Try Again");
            return;
        }

        String role = (String) user.get("role");
        if (role.equals("admin")) {
            System.out.println("Welcome admin! Please create new questions in the question bank.");
            adminQuestions(scanner);
        } else if (role.equals("student")) {
            System.out.println("Welcome " + username + " to the quiz! We will throw you " + Number_of_ques + " questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' to start.");
            String input = scanner.nextLine();
            if (input.equals("s")) {
                List<JSONObject> quizQuestions = quizQuestions(Number_of_ques);
                int marks = startQuiz(scanner, quizQuestions);
                finalResult(marks);
            }
            else System.out.println("Please, enter either s or q");
        }
    }

    private static JSONObject userAuthentication(String username, String password) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray users = (JSONArray) parser.parse(new FileReader(Valid_Users));
            for (Object obj : users) {
                JSONObject user = (JSONObject) obj;
                String userUsername = (String) user.get("username");
                String userPassword = (String) user.get("password");
                if (username.equals(userUsername) && password.equals(userPassword)) {
                    return user;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void adminQuestions(Scanner scanner) {
        JSONArray questions = new JSONArray();
        JSONParser parser = new JSONParser();

        try {
            questions = (JSONArray) parser.parse(new FileReader(Set_of_Questions));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        while (true) {
            JSONObject question = new JSONObject();
            System.out.println("Input your question");
            String questionText = scanner.nextLine();
            System.out.println("Input option 1:");
            String option1 = scanner.nextLine();
            System.out.println("Input option 2:");
            String option2 = scanner.nextLine();
            System.out.println("Input option 3:");
            String option3 = scanner.nextLine();
            System.out.println("Input option 4:");
            String option4 = scanner.nextLine();
            System.out.println("What is the answer key?");
            int answerKey = scanner.nextInt();

            question.put("question", questionText);
            question.put("option 1", option1);
            question.put("option 2", option2);
            question.put("option 3", option3);
            question.put("option 4", option4);
            question.put("answerkey", answerKey);

            questions.add(question);

            System.out.println("Saved successfully! Do you want to add more questions? (press 's' for start and 'q' for quit)");
            scanner.nextLine();
            String input = scanner.nextLine();
            if (input.equals("q")) {
                break;
            }
            else System.out.println("Please, enter either s or q");
            String input1 = scanner.nextLine();

        }

        try (FileWriter fileWriter = new FileWriter(Set_of_Questions)) {
            fileWriter.write(questions.toJSONString());
            System.out.println("Questions saved to " + Set_of_Questions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<JSONObject> quizQuestions(int numQuestions) {
        List<JSONObject> quizQuestions = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            JSONArray questions = (JSONArray) parser.parse(new FileReader(Set_of_Questions));
            int totalQuestions = questions.size();
            if (totalQuestions < numQuestions) {
                System.out.println("Insufficient questions in the quiz bank. Please add more questions.");
                return quizQuestions;
            }

            Random random = new Random();
            Set<Integer> selectedQuestionIndices = new HashSet<>();

            while (selectedQuestionIndices.size() < numQuestions) {
                int randomIndex = random.nextInt(totalQuestions);
                if (!selectedQuestionIndices.contains(randomIndex)) {
                    JSONObject question = (JSONObject) questions.get(randomIndex);
                    quizQuestions.add(question);
                    selectedQuestionIndices.add(randomIndex);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return quizQuestions;
    }

    private static int startQuiz(Scanner scanner, List<JSONObject> quizQuestions) {
        int marks = 0;
        int questionNum = 1;

        for (JSONObject question : quizQuestions) {
            System.out.println("[Question " + questionNum + "] " + question.get("question"));
            System.out.println("1. " + question.get("option 1"));
            System.out.println("2. " + question.get("option 2"));
            System.out.println("3. " + question.get("option 3"));
            System.out.println("4. " + question.get("option 4"));

            System.out.print("Student:> ");
            int userAnswer = scanner.nextInt();
            int answerKey = ((Long) question.get("answerkey")).intValue();

            if (userAnswer == answerKey) {
                marks++;
            }

            questionNum++;
        }

        return marks;
    }

    private static void finalResult(int marks) {
        System.out.println("Quiz has been completed successfully!");

        if (marks >= 8) {
            System.out.println("Excellent! You have marksd " + marks + " out of " + Number_of_ques);
        } else if (marks >= 5) {
            System.out.println("Good. You have marksd " + marks + " out of " + Number_of_ques);
        } else if (marks >= 2) {
            System.out.println("Very poor! You have marksd " + marks + " out of " + Number_of_ques);
        } else {
            System.out.println("Very sorry you are failed. You have marksd " + marks + " out of " + Number_of_ques);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to start again? Press 's' for start or 'q' for quit");
        String input = scanner.nextLine();
        if (input.equals("s")) {
            System.out.println();
            main(null);
        }
        else System.out.println("Please, enter either s or q");
        String input2 = scanner.nextLine();
    }


}







