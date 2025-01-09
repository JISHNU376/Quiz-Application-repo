import java.util.*;
import java.util.concurrent.*;

class QuizQuestion {
    private String question;
    private String[] options;
    private int correctOption;

    public QuizQuestion(String question, String[] options, int correctOption) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }
}

class QuizGame {
    private List<QuizQuestion> questions;
    private int score;
    private List<Boolean> results;

    public QuizGame() {
        questions = new ArrayList<>();
        score = 0;
        results = new ArrayList<>();
    }

    public void addQuestion(QuizQuestion question) {
        questions.add(question);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        System.out.println("Welcome to the Quiz Game!");

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion question = questions.get(i);

            System.out.println("\nQuestion " + (i + 1) + ": " + question.getQuestion());
            String[] options = question.getOptions();
            for (int j = 0; j < options.length; j++) {
                System.out.println((j + 1) + ". " + options[j]);
            }

            Future<Integer> future = executor.submit(() -> {
                System.out.print("Your answer (1-4): ");
                return scanner.nextInt();
            });

            int answer = -1;
            try {
                answer = future.get(15, TimeUnit.SECONDS); // 15-second timer
            } catch (TimeoutException e) {
                System.out.println("Time's up! Moving to the next question.");
                future.cancel(true);
            } catch (Exception e) {
                System.out.println("Invalid input. Skipping the question.");
            }

            if (answer == question.getCorrectOption()) {
                System.out.println("Correct!");
                score++;
                results.add(true);
            } else {
                System.out.println("Incorrect! The correct answer was: " + question.getCorrectOption());
                results.add(false);
            }
        }

        executor.shutdown();

        displayResults();
        scanner.close();
    }

    private void displayResults() {
        System.out.println("\nQuiz Over! Here are your results:");
        System.out.println("Total Score: " + score + "/" + questions.size());

        for (int i = 0; i < results.size(); i++) {
            System.out.println("Question " + (i + 1) + ": " + (results.get(i) ? "Correct" : "Incorrect"));
        }
    }
}

public class QuizApplication {
    public static void main(String[] args) {
        QuizGame quizGame = new QuizGame();

        quizGame.addQuestion(new QuizQuestion("What is the capital of France?", new String[]{"Berlin", "Paris", "Madrid", "Rome"}, 2));
        quizGame.addQuestion(new QuizQuestion("What is 2 + 2?", new String[]{"3", "4", "5", "6"}, 2));
        quizGame.addQuestion(new QuizQuestion("Which planet is known as the Red Planet?", new String[]{"Earth", "Mars", "Jupiter", "Venus"}, 2));
        quizGame.addQuestion(new QuizQuestion("Who wrote 'Hamlet'?", new String[]{"Shakespeare", "Dickens", "Chaucer", "Austen"}, 1));

        quizGame.start();
    }
}
