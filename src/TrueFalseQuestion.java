public class TrueFalseQuestion extends Questions {

    public TrueFalseQuestion(String question, String correctAnswer, Difficulty difficultyLevel) {
        super(question, correctAnswer, difficultyLevel);
    }

    public String[] getTAndFOptions() {
        return new String[] {"True", "False"};
    }

    @Override
    public String displayQuestion() {
        return "True or False?";
    }

    @Override
    public boolean checkAnswer(String userAnswer) {
        if (userAnswer == null) {
            return false;
        }

        String user = userAnswer.trim();
        String correct = correctAnswer.trim();

        // Check full words first
        if (user.equalsIgnoreCase(correct)) {
            return true;
        }

        // Check single letter (T or F)
        if (user.length() == 1) {
            char userChar = Character.toUpperCase(user.charAt(0));
            char correctChar = Character.toUpperCase(correct.charAt(0));
            return userChar == correctChar;
        }

        return false;
    }
}
