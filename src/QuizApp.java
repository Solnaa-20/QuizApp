import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizApp {

    private JFrame frame;
    private CardLayout cardLayout;

    // 3 screens
    private JPanel startPanel;
    private JPanel questionPanel;
    private JPanel resultPanel;

    // Button difficulty
    private Difficulty selectedDifficulty;

    //Username
    private String userName;

    //Database
    private PersistenceManager dbase = new PersistenceManager();
    private UserScoreRecord userScoreRecord;

    //Track every card created
    private List<QuestionCard> questionCards = new ArrayList<>();

    // CONSTRUCTOR
    public QuizApp() {

        frame = new JFrame("Quiz Application");
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbase.initializeDatabase();

        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);

        startPanel = new JPanel();
        questionPanel = new JPanel();
        resultPanel = new JPanel();

        StartScreen();

        frame.add(startPanel, "Start");
        frame.add(questionPanel, "Question");
        frame.add(resultPanel, "Result");

        cardLayout.show(frame.getContentPane(), "Start");
        frame.setVisible(true);
    }


    // CIRCLE BUTTON
    class CircleButton extends JToggleButton {

        private Color normalColor = new Color(240, 240, 240);
        private Color selectedColor = new Color(0, 150, 136);
        private String letter;

        public CircleButton(String letter) {
            this.letter = letter;
            setPreferredSize(new Dimension(32, 32));
            setMinimumSize(new Dimension(32, 32));
            setMaximumSize(new Dimension(32, 32));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            // smooth edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // fill circle
            g2.setColor(isSelected() ? selectedColor : normalColor);
            g2.fillOval(0, 0, getWidth(), getHeight());

            // draw letter
            g2.setColor(isSelected() ? Color.WHITE : Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(letter)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 3;

            g2.drawString(letter, x, y);
            g2.dispose();
        }
    }

// QUESTION CARD
    class QuestionCard extends JPanel {

        private String selectedAnswer = null;
        private Questions questionObject;

        public QuestionCard(String question, String[] options) {

            setOpaque(false);

            // Dynamic height (T/F cards are smaller)
            int height = (options.length == 2) ? 200 : 280;
            setPreferredSize(new Dimension(520, height));
            setMaximumSize(new Dimension(520, height));

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // White rounded container
            JPanel background = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                }
            };
            background.setOpaque(false);
            background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
            background.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));


            // QUESTION TITLE
            JTextArea qLabel = new JTextArea(question);
            qLabel.setFont(new Font("Arial", Font.BOLD, 20));
            qLabel.setLineWrap(true);
            qLabel.setWrapStyleWord(true);
            qLabel.setEditable(false);
            qLabel.setOpaque(false);
            qLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            background.add(qLabel);
            background.add(Box.createVerticalStrut(10));


            // ANSWER OPTIONS
            String[] letters = {"A", "B", "C", "D"};
            ButtonGroup group = new ButtonGroup();

            int optionSpacing = (options.length == 2) ? 4 : 8;  // LESS space for T/F

            for (int i = 0; i < options.length; i++) {

                JPanel row = new JPanel();
                row.setOpaque(false);
                row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

                CircleButton btn = new CircleButton(letters[i]);
                group.add(btn);

                JLabel optLabel = new JLabel(options[i]);
                optLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                optLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

                // Clicking the row selects the button and stores the answer
                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        btn.doClick();
                        selectedAnswer = optLabel.getText();
                    }
                });

                btn.addActionListener(e->{
                    selectedAnswer = optLabel.getText();
                });

                row.add(btn);
                row.add(optLabel);
                row.add(Box.createHorizontalGlue());

                background.add(row);
                background.add(Box.createVerticalStrut(1));
                background.add(Box.createVerticalStrut(optionSpacing));

            }

            add(background);
        }

        public String getSelectedAnswer() {
            return selectedAnswer;
        }
        public Questions getQuestionObject() {
            return questionObject;
        }
        public void setQuestionObject(Questions q) {
            this.questionObject = q;
        }
    }

    // START SCREEN
    public void StartScreen() {

        startPanel.removeAll();
        startPanel.setLayout(new BorderLayout());
        startPanel.setBackground(new Color(20, 126, 21)); // green background

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("🌍 Climate Change Quiz 🌍");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Test your knowledge & learn something new!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Username field and label
        JLabel usernameLabel = new JLabel("Enter username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setMaximumSize(new Dimension(250,35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Start button
        JButton startButton = new JButton("Start Quiz");
        styleButton(startButton);
        addHoverEffect(startButton);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);


        // Difficulty label
        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 22));
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Difficulty dropdown
        String[] levels = {"EASY", "HARD"};
        JComboBox<String> difficulty = new JComboBox<>(levels);
        difficulty.setFont(new Font("Arial", Font.BOLD, 18));
        difficulty.setForeground(Color.WHITE);
        difficulty.setBackground(new Color(0, 150, 136));
        difficulty.setMaximumSize(new Dimension(250, 40));
        difficulty.setAlignmentX(Component.CENTER_ALIGNMENT);


        // Add spacing + components
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(usernameLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(usernameField);
        content.add(Box.createVerticalStrut(35));
        content.add(difficultyLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(difficulty);
        content.add(Box.createVerticalStrut(40));
        content.add(startButton);

        startPanel.add(content, BorderLayout.CENTER);

        // START BUTTON ACTION
        startButton.addActionListener(e -> {

            String username = usernameField.getText().trim();

            if(username.isEmpty()){
                JOptionPane.showMessageDialog(frame,
                        "Please enter your username!",
                        "Missing Name",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            userName = username; //Save the name

            String difficultyValue = difficulty.getSelectedItem().toString();
            selectedDifficulty = difficultyValue.equals("EASY") ? Difficulty.EASY : Difficulty.HARD;

            QuestionScreen();
            cardLayout.show(frame.getContentPane(), "Question");
        });

        startPanel.revalidate();
        startPanel.repaint();
    }


    // QUESTION SCREEN
    public void QuestionScreen() {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(20, 126, 21));

        JLabel questionTitle = new JLabel("Answer all Questions");
        questionTitle.setFont(new Font("Times New Romans", Font.BOLD, 45));
        questionTitle.setForeground(Color.WHITE);
        questionTitle.setOpaque(false);
        questionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(Box.createVerticalStrut(40));
        container.add(questionTitle);
        container.add(Box.createVerticalStrut(20));  // Gap before first card


        //Retrieve questions
        List<MultipleChoiceQuestion> allMcq = QuestionBank.getMultipleChoiceQuestions();
        List<TrueFalseQuestion> allTrueAndFalse = QuestionBank.getTrueFalseQuestions();

        //Filter by difficulty
        List<MultipleChoiceQuestion> filteredMcq = new ArrayList<>();
        List<TrueFalseQuestion> filteredTrueAndFalse = new ArrayList<>();

        for(MultipleChoiceQuestion mcq : allMcq){
            if(mcq.getDifficultyLevel()==selectedDifficulty){
                filteredMcq.add(mcq);
            }
        }
        for(TrueFalseQuestion tfq : allTrueAndFalse){
            if(tfq.getDifficultyLevel()==selectedDifficulty){
                filteredTrueAndFalse.add(tfq);
            }
        }

        //Shuffle questions
        Collections.shuffle(filteredMcq);
        Collections.shuffle(filteredTrueAndFalse);

        //take questions
        int numbering = 1;
        for(int i=0;i<10;i++){
            MultipleChoiceQuestion question = filteredMcq.get(i);

            container.add(Box.createVerticalStrut(40));

            QuestionCard card = new QuestionCard(numbering+ ". " + question.getQuestionText(),question.getOptions());

            card.setQuestionObject(question);
            questionCards.add(card);

            container.add(card);
            container.add(Box.createVerticalStrut(40));
            numbering++;
        }

        for(int i=0;i<10;i++){
            TrueFalseQuestion question1 = filteredTrueAndFalse.get(i);
            String[] options = question1.getTAndFOptions();

            QuestionCard card1 = new QuestionCard(numbering+ ". " + question1.getQuestionText(),   // question text
                    question1.getTAndFOptions());

            card1.setQuestionObject(question1);
            questionCards.add(card1);

            container.add(card1);
            container.add(Box.createVerticalStrut(40));
            numbering++;
        }

        JButton submitButton = new JButton("Submit Quiz");
        styleButton(submitButton);
        addHoverEffect(submitButton);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(submitButton);
        container.add(Box.createVerticalStrut(30));

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        questionPanel.setLayout(new BorderLayout());
        questionPanel.add(scroll, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            int score=calculateScore();
            ResultsScreen(score);
            saveScoreToDatabase(score);
            cardLayout.show(frame.getContentPane(), "Result");
        });
    }

    //Results screen
    public void ResultsScreen(int score){

        resultPanel.removeAll();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.setBackground(new Color(20, 126, 21));

        JLabel resultTitle = new JLabel("Quiz Results");
        resultTitle.setFont(new Font("Times New Romans", Font.BOLD, 45));
        resultTitle.setForeground(Color.WHITE);
        resultTitle.setOpaque(false);
        resultTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center container
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Username message
        JLabel userLabel = new JLabel("Well done, " + userName + "!");
        userLabel.setFont(new Font("Arial", Font.BOLD, 30));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Score message
        JLabel scoreLabel = new JLabel("Your Score: " + score + "/20");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 35));
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Back button
        JButton backButton = new JButton("Back to Start");
        styleButton(backButton);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> {
            questionCards.clear();
            questionPanel.removeAll();
            questionPanel.revalidate();
            questionPanel.repaint();

            StartScreen();
            cardLayout.show(frame.getContentPane(), "Start");
        });

        // Add components
        content.add(Box.createVerticalStrut(40));
        content.add(resultTitle);
        content.add(Box.createVerticalStrut(30));
        content.add(userLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(scoreLabel);
        content.add(Box.createVerticalStrut(40));
        content.add(backButton);

        resultPanel.add(content, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();


    }

    // STYLE FLAT BUTTON
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addHoverEffect(JButton btn) {
        Color normal = new Color(0, 150, 136);
        Color hover = new Color(0, 170, 150);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(normal); }
        });
    }

    //Calculating score
    private int calculateScore(){
        int score = 0;

        for(QuestionCard card : questionCards){
            Questions question = card.getQuestionObject();
            String userAnswer = card.getSelectedAnswer();

            if(userAnswer==null){continue;}
            if(question.checkAnswer(userAnswer)){score++;}
        }
        return score;
    }

    //Save the score
    private void saveScoreToDatabase(int score){
        UserScoreRecord record = new UserScoreRecord(
                userName,
                score,
                selectedDifficulty,
                System.currentTimeMillis()
        );
        dbase.saveScore(record);
    }
    
    // Main
    public static void main(String[] args) {
        new QuizApp();
    }
}



