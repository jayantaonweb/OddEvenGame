import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class OddEvenGame extends JFrame {

    private JTextArea logArea;
    private JTextArea historyArea;
    private JLabel userScoreLabel;
    private JLabel pcScoreLabel;
    private JLabel currentUserNumberLabel;
    private JLabel currentPcNumberLabel;
    private JButton[] inputButtons;
    private JButton[] tossButtons;
    private JButton restartButton, historyButton, RuleButton;
    private Random random;
    private int pc_input, user_input, score, pc_score;
    private boolean batting, bowling, u_notout, pc_notout;
    private boolean tossPhase, gamePhase;
    private StringBuilder gameHistory;
    private int gameCount;
    public boolean inning1;

    public OddEvenGame() {
        setTitle("Odd Even Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        ImageIcon logo = new ImageIcon("logo.png");
        setIconImage(logo.getImage());

        setLayout(new BorderLayout());

        JPanel inputpanel = new JPanel(new GridLayout(2, 8));
        inputButtons = new JButton[8];
        for (int i = 0; i < 8; i++) {
            inputButtons[i] = new JButton(String.valueOf(i));
            if (i == 7) {
                inputButtons[7] = new JButton("0");
                inputButtons[7].addActionListener(new InputButtonListener(0));
            } else {
                inputButtons[i].addActionListener(new InputButtonListener(i));
            }
            inputpanel.add(inputButtons[i]);

        }

        JPanel scorepanel = new JPanel(new GridLayout(1, 2));
        scorepanel.setBorder(new TitledBorder("------------------------USER SCORE-----------" +
                "-----------------------PC SCORE-------------------------"));

        userScoreLabel = new JLabel("");
        userScoreLabel.setOpaque(true);
        userScoreLabel.setBackground(Color.orange);
        pcScoreLabel = new JLabel("");
        pcScoreLabel.setOpaque(true);
        pcScoreLabel.setBackground(Color.orange);

        scorepanel.add(userScoreLabel);
        scorepanel.add(pcScoreLabel);

        JPanel Tosspanel = new JPanel(new GridLayout(3, 2));
        Tosspanel.setBorder(new TitledBorder("|Your Num| |PC's Num|"));
        currentUserNumberLabel = new JLabel(" ");
        currentPcNumberLabel = new JLabel(" ");
        tossButtons = new JButton[4];
        tossButtons[0] = new JButton("Odd");
        tossButtons[0].addActionListener(new TossButtonListener(1));

        tossButtons[1] = new JButton("Even");
        tossButtons[1].addActionListener(new TossButtonListener(2));

        tossButtons[2] = new JButton("BAT");
        tossButtons[2].addActionListener(e -> handleTossResult(1));

        tossButtons[3] = new JButton("BOWL");
        tossButtons[3].addActionListener(e -> handleTossResult(2));

        Tosspanel.add(currentUserNumberLabel);
        Tosspanel.add(currentPcNumberLabel);
        Tosspanel.add(tossButtons[0]);
        Tosspanel.add(tossButtons[1]);
        Tosspanel.add(tossButtons[2]);
        Tosspanel.add(tossButtons[3]);

        JPanel displaypanel = new JPanel();
        logArea = new JTextArea(30, 22);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        displaypanel.add(logScrollPane);

        JPanel menupanel = new JPanel(new GridLayout(3, 1));
        menupanel.setBorder(new TitledBorder("MENU"));

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> resetGame());
        historyButton = new JButton("History");
        historyButton.addActionListener(e -> showHistory());
        RuleButton = new JButton("Rules");
        RuleButton.addActionListener(e -> showRules());

        menupanel.add(restartButton);
        menupanel.add(historyButton);
        menupanel.add(RuleButton);

        historyArea = new JTextArea(15, 50);
        historyArea.setEditable(false);

        inputpanel.setBackground(Color.cyan);
        scorepanel.setBackground(Color.cyan);
        Tosspanel.setBackground(Color.cyan);
        displaypanel.setBackground(Color.green);
        menupanel.setBackground(Color.cyan);

        
        add(inputpanel, BorderLayout.SOUTH);
        add(Tosspanel, BorderLayout.EAST);
        add(displaypanel, BorderLayout.CENTER);
        add(scorepanel, BorderLayout.NORTH);
        add(menupanel, BorderLayout.WEST);

        random = new Random();
        gameHistory = new StringBuilder();
        resetGame();
    }

    private void showRules() {
        JOptionPane.showMessageDialog(this,
                "Game Rules:\n1. Choose odd or even for the toss.\n"
                        + "2. Toss result will be selected randomly between Odd and Even\n"
                        + "3. Toss winner can choose to bat or bowl first\n"
                        + "4. While batting, the batsman has to select a number\n"
                        + "   and the bowler has to guess the number until the batsman is out\n"
                        + "5. The batsman's score will add up until they are out\n"
                        + "6. The batsman is out when the bowler guesses the correct number\n"
                        + "7. When the batsman is out, the bowler gets the chance to bat\n"
                        + "8. The result will be decided based on the total scores at the end\n"
                        + "                            THANK YOU FOR PLAYING",
                "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHistory() {
        JOptionPane.showMessageDialog(this, new JScrollPane(historyArea), "Game History",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateCurrentNumbers() {
        currentUserNumberLabel.setText("        " + user_input + "          ");

        currentPcNumberLabel.setText("         " + pc_input + "           ");
    }

    private void updateScores() {
        userScoreLabel.setText("__________________|   " + score + "   |________________");
        pcScoreLabel.setText("______________|   " + pc_score + "   |__________________");
    }

    private void resetGame() {
        score = 0;
        pc_score = 0;
        inning1 = false;
        batting = false;
        bowling = false;
        u_notout = true;
        pc_notout = true;
        tossPhase = true;
        gamePhase = false;
        logArea.setText("Press 'Odd' or 'Even' for toss call.\n");
        updateScores();
        updateCurrentNumbers();

        setInputButtonsEnabled(false);
        tossButtons[0].setEnabled(true);
        tossButtons[1].setEnabled(true);
        tossButtons[2].setEnabled(false);
        tossButtons[3].setEnabled(false);
    }

    private void handleToss(int tossChoice) {
        if (tossChoice == 1 || tossChoice == 2) {
            logArea.append("You chose: " + (tossChoice == 1 ? "Odd" : "Even") + "\n");
            pc_input = random.nextInt(7);
            logArea.append("TOSS Result: " + (pc_input == 1 ? "Odd" : "Even") + "\n");

            int toss_sum = pc_input;

            if ((toss_sum % 2 == 0 && tossChoice == 2) || (toss_sum % 2 != 0 && tossChoice == 1)) {
                logArea.append("You won the toss.\nChoose to bat or bowl.\n");
                tossPhase = false;
                tossButtons[0].setEnabled(false);
                tossButtons[1].setEnabled(false);
                tossButtons[2].setEnabled(true);
                tossButtons[3].setEnabled(true);
            } else {
                logArea.append("PC won the toss.\n");
                tossButtons[0].setEnabled(false);
                tossButtons[1].setEnabled(false);
                handlePCTossChoice();
            }
        }
    }

    private void handlePCTossChoice() {
        if (random.nextBoolean()) {

            logArea.append("PC chose to bat first.\n");
            batting = false;
            bowling = true;
            inning1 = true;

        } else {

            logArea.append("PC chose to bowl first.\n");
            batting = true;
            bowling = false;
            inning1 = true;

        }
        gamePhase = true;
        setInputButtonsEnabled(true);
        tossPhase = false;
    }

    private void handleTossResult(int choice) {
        if (choice == 1) {

            batting = true;
            bowling = false;
            inning1 = true;
            logArea.append("You chose to bat first.\n");
        } else {
            batting = false;
            bowling = true;
            inning1 = true;
            logArea.append("You chose to bowl first.\n");
        }
        gamePhase = true;
        setInputButtonsEnabled(true);
        tossButtons[2].setEnabled(false);
        tossButtons[3].setEnabled(false);
    }

    private void handleBatting() {

        pc_input = random.nextInt(7);
        logArea.append("You: " + user_input + ", PC: " + pc_input + "\n");
        updateCurrentNumbers();
        if (user_input == pc_input) {
            u_notout = false;
            logArea.append("You're out!\n");
        } else {
            score += user_input;
            updateScores();
        }

        if (!inning1 && score > pc_score) {
            bowling = false;
            batting = false;
            gamePhase = false;
            logArea.append("Congratulations! You won the game :)\n");
            endGame("You win");
        }
        if (inning1 && user_input == pc_input) {
            inning1 = false;
        }

    }

    private void handleBowling() {
        pc_input = random.nextInt(7);
        logArea.append("PC: " + pc_input + ", You: " + user_input + "\n");
        updateCurrentNumbers();
        if (user_input == pc_input) {
            pc_notout = false;
            logArea.append("PC is out!\n");
        } else {
            pc_score += pc_input;
            updateScores();
        }

        if (!inning1 && score < pc_score) {
            bowling = false;
            batting = false;
            gamePhase = false;
            logArea.append("Sorry You LOST :(\n");
            endGame("Pc Wins");
        }
        if (inning1 && user_input == pc_input) {
            inning1 = false;
        }
    }

    private void playGame() {
        if (gamePhase) {
            if (batting) {
                handleBatting();
                if (!u_notout && pc_notout) {
                    logArea.append("**PC's batting turn**\n");
                    batting = false;
                    bowling = true;
                }
            } else if (bowling) {
                handleBowling();
                if (!pc_notout && u_notout) {
                    logArea.append("**Your batting turn**\n");
                    bowling = false;
                    batting = true;
                }
            }

            if (!u_notout && !pc_notout) {
                if (pc_score > score) {
                    logArea.append("You lose :(\n");
                    endGame("PC wins");
                } else if (score == pc_score) {
                    logArea.append("The game is a draw :|\n");
                    endGame("Draw");
                } else {
                    logArea.append("Congratulations! You won the game :)\n");
                    endGame("You win");
                }
            }
        }
    }

    private void endGame(String result) {
        gamePhase = false;
        setInputButtonsEnabled(false);
        gameCount++;
        gameHistory.append("Game " + gameCount + ": " + result + "\n");
        gameHistory.append("Your score : " + score + "\nPC score : " + pc_score + "\n");

        logArea.append("Press 'Restart' to play again\n");
        historyArea.setText(gameHistory.toString());
    }

    private void setInputButtonsEnabled(boolean enabled) {
        for (JButton button : inputButtons) {
            button.setEnabled(enabled);
        }
    }

    public class InputButtonListener implements ActionListener {

        private int input;

        public InputButtonListener(int input) {
            this.input = input;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            user_input = input;

            if (gamePhase) {
                playGame();
            }
        }
    }

    private class TossButtonListener implements ActionListener {
        private int tossChoice;

        public TossButtonListener(int tossChoice) {
            this.tossChoice = tossChoice;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tossPhase) {
                handleToss(tossChoice);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OddEvenGame().setVisible(true));
    }
}
