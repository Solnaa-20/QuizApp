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

                bg.add(row);
                bg.add(Box.createVerticalStrut(1));  // spacing below question
                bg.add(Box.createVerticalStrut(optionSpacing));

            }

            add(bg);
        }
    }

    // START SCREEN
    public void StartScreen() {

        startPanel.setLayout(new GridBagLayout());
        startPanel.setBackground(new Color(20, 126, 21));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel quizTitle = new JLabel("Climate Change Quiz Application");
        quizTitle.setFont(new Font("Arial", Font.BOLD, 55));
        quizTitle.setForeground(Color.WHITE);
        quizTitle.setOpaque(false);
        quizTitle.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel subtitle = new JLabel("Test your knowledge on climate change and learn something new!");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 30));
        subtitle.setForeground(Color.WHITE);
        subtitle.setOpaque(false);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        centerCard.setOpaque(false);
        centerCard.setPreferredSize(new Dimension(400, 600));
        centerCard.setLayout(new BoxLayout(centerCard, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to the Quiz");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Start Quiz");
        styleButton(startButton);
        addHoverEffect(startButton);

        String[] levels = {"EASY", "HARD"};
        JComboBox<String> difficulty = new JComboBox<>(levels);
        difficulty.setFont(new Font("SansSerif", Font.BOLD, 18));
        difficulty.setForeground(Color.WHITE);
        difficulty.setBackground(new Color(0, 150, 136));
        difficulty.setMaximumSize(new Dimension(200, 45));
        difficulty.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerCard.add(Box.createVerticalStrut(20));
        centerCard.add(title);
        centerCard.add(Box.createVerticalStrut(30));
        centerCard.add(startButton);
        centerCard.add(Box.createVerticalStrut(30));
        centerCard.add(difficulty);
        centerCard.add(Box.createVerticalStrut(30));

        content.add(quizTitle);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(centerCard);

        startPanel.add(content);

        startButton.addActionListener(e -> {

            String difficultyValue = difficulty.getSelectedItem().toString();
            if(difficultyValue.equals("EASY")){
                selectedDifficulty = Difficulty.EASY;
            }
            else if(difficultyValue.equals("HARD")){
                selectedDifficulty = Difficulty.HARD;
            }
            QuestionScreen();
            cardLayout.show(frame.getContentPane(), "Question");
        });

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

            container.add(Box.createVerticalStrut(40));   // top padding

            QuestionCard card = new QuestionCard(numbering+ ". " + question.getQuestionText(),question.getOptions());

            container.add(card);
            container.add(Box.createVerticalStrut(40));
            numbering++;
        }

        for(int i=0;i<10;i++){
            TrueFalseQuestion question1 = filteredTrueAndFalse.get(i);
            String[] options = question1.getTAndFOptions();

            QuestionCard card1 = new QuestionCard(numbering+ ". " + question1.getQuestionText(),   // question text
                    question1.getTAndFOptions());

            container.add(card1);
            container.add(Box.createVerticalStrut(40));
            numbering++;
        }

        JButton submitBtn = new JButton("Submit Quiz");
        styleButton(submitBtn);
        addHoverEffect(submitBtn);
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(submitBtn);
        container.add(Box.createVerticalStrut(30));

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        questionPanel.setLayout(new BorderLayout());
        questionPanel.add(scroll, BorderLayout.CENTER);

        submitBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(frame, "Quiz submitted! Scoring next.")
        );
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
    
    // Main
    public static void main(String[] args) {
        new QuizApp();
    }
}



