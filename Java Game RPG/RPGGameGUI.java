import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RPGGameGUI {
    private JFrame frame;
    private JTextArea textArea;
    private JPanel buttonPanel;
    private JLabel statusLabel;
    private JLabel monsterStatusLabel;
    private static final int TEXT_LINE_LIMIT = 10;

    public RPGGameGUI() {
        // Setup frame
        frame = new JFrame("RPG Game - Blades Of The Fallen Kingdom");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        showWelcomeScreen();

        frame.setVisible(true);
        // Text area for game messages
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1));
        buttonPanel.setBackground(new Color(50, 50, 50));

        // Status labels
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);

        monsterStatusLabel = new JLabel("No monster currently.");
        monsterStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        monsterStatusLabel.setForeground(Color.RED);

        // Add components to frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(monsterStatusLabel, BorderLayout.EAST);

        updateStatus();
        goTown();

        frame.setVisible(true);
    }

    private void goTown() {
        displayMessage("You are in the town square. You see a sign that says \"Store\".");
        setMonsterStatus("No monster currently.");
        setOptions(
                new String[]{"Go to store", "Go to cave", "Fight dragon"},
                new Runnable[]{this::goStore, this::goCave, () -> fight(2)}
        );
    }
    private void showWelcomeScreen() {
        // Panel untuk welcome screen
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(new Color(44, 62, 80)); // Latar belakang gelap, sesuai tema kerajaan
        welcomePanel.setLayout(new BorderLayout());

        // Teks selamat datang dengan warna emas
        JLabel welcomeLabel = new JLabel(
                "<html><div style='text-align:center; color:#FFD700; font-size:28px; font-family:Serif;'>" +
                        "Welcome adventurer to<br>Blades Of The Fallen Kingdom</div></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Tombol untuk memulai
        JButton startButton = new JButton("Start Your Adventure");
        startButton.setFont(new Font("Serif", Font.BOLD, 18));
        startButton.setBackground(new Color(149, 81, 13, 242)); // Hijau gelap
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> {
            frame.getContentPane().remove(welcomePanel); // Hapus panel selamat datang
            frame.revalidate();
            frame.repaint();
            goTown(); // Lanjutkan ke game
        });

        // Tambahkan komponen ke panel
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(startButton, BorderLayout.SOUTH);

        // Tampilkan panel di frame
        frame.getContentPane().removeAll();
        frame.add(welcomePanel);
        frame.revalidate();
        frame.repaint();
    }


    private void goStore() {
        displayMessage("You enter the store.");
        setMonsterStatus("No monster currently.");
        setOptions(
                new String[]{"Buy 10 health (10 gold)", "Buy weapon (30 gold)", "Go to town square"},
                new Runnable[]{this::buyHealth, this::buyWeapon, this::goTown}
        );
    }

    private void goCave() {
        displayMessage("You enter the cave. You see some monsters.");
        setMonsterStatus("No monster currently.");
        setOptions(
                new String[]{"Fight slime", "Fight fanged beast", "Go to town square"},
                new Runnable[]{() -> fight(0), () -> fight(1), this::goTown}
        );
    }

    private void fight(int monsterIndex) {
        RPGGame.fighting = monsterIndex;
        RPGGame.monsterHealth = RPGGame.monsters[monsterIndex].getHealth();
        Monster monster = RPGGame.monsters[monsterIndex];
        setMonsterStatus(monster.getName() + " - Health: " + RPGGame.monsterHealth);
        displayMessage("You are fighting a " + monster.getName() + ".");
        setOptions(
                new String[]{"Attack", "Dodge", "Run"},
                new Runnable[]{this::attack, this::dodge, this::goTown}
        );
    }

    private void attack() {
        Monster monster = RPGGame.monsters[RPGGame.fighting];
        Weapon weapon = RPGGame.weapons[RPGGame.currentWeapon];
        int damage = weapon.getPower() + new Random().nextInt(RPGGame.xp + 1);
        RPGGame.monsterHealth -= damage;

        displayMessage("You attack the " + monster.getName() + " with your " + weapon.getName() +
                " and deal " + damage + " damage!");

        int monsterDamage = RPGGame.getMonsterAttackValue(monster.getLevel());
        RPGGame.health -= monsterDamage;

        displayMessage("The " + monster.getName() + " attacks and deals " + monsterDamage + " damage!");

        if (RPGGame.health <= 0) {
            lose();
        } else if (RPGGame.monsterHealth <= 0) {
            if (RPGGame.fighting == 2) {
                winGame();
            } else {
                defeatMonster();
            }
        } else {
            setMonsterStatus(monster.getName() + " - Health: " + RPGGame.monsterHealth);
            updateStatus();
        }
    }

    private void dodge() {
        displayMessage("You dodge the attack from the " + RPGGame.monsters[RPGGame.fighting].getName() + ".");
        fight(RPGGame.fighting);
    }

    private void defeatMonster() {
        Monster monster = RPGGame.monsters[RPGGame.fighting];
        int goldReward = monster.getLevel() * 6;
        int xpReward = monster.getLevel();

        RPGGame.gold += goldReward;
        RPGGame.xp += xpReward;

        displayMessage("You defeated the " + monster.getName() + "! You gain " +
                goldReward + " gold and " + xpReward + " XP.");
        setMonsterStatus("No monster currently.");
        updateStatus();
        goCave();
    }

    private void buyHealth() {
        if (RPGGame.gold >= 10) {
            RPGGame.gold -= 10;
            RPGGame.health += 10;
            displayMessage("You bought 10 health. Current health: " + RPGGame.health + ".");
        } else {
            displayMessage("You don't have enough gold to buy health.");
        }
        updateStatus();
        goStore();
    }

    private void buyWeapon() {
        if (RPGGame.currentWeapon < RPGGame.weapons.length - 1) {
            if (RPGGame.gold >= 30) {
                RPGGame.gold -= 30;
                RPGGame.currentWeapon++;
                RPGGame.inventory.add(RPGGame.weapons[RPGGame.currentWeapon].getName());
                displayMessage("You bought a " + RPGGame.weapons[RPGGame.currentWeapon].getName() + ".");
            } else {
                displayMessage("You don't have enough gold to buy a weapon.");
            }
        } else {
            displayMessage("You already have the most powerful weapon!");
        }
        updateStatus();
        goStore();
    }

    private void lose() {
        displayMessage("You died. Game over.");
        restart();
    }

    private void winGame() {
        displayMessage("You defeated the dragon! YOU WIN!");
        restart();
    }

    private void restart() {
        RPGGame.xp = 0;
        RPGGame.health = 100;
        RPGGame.gold = 50;
        RPGGame.currentWeapon = 0;
        RPGGame.inventory.clear();
        RPGGame.inventory.add("stick");
        setMonsterStatus("No monster currently.");
        updateStatus();
        goTown();
    }

    private void displayMessage(String message) {
        textArea.append("\n------\n"); // Tambahkan pembatas
        textArea.append(message + "\n");
        limitTextArea();
    }

    private void setOptions(String[] options, Runnable[] actions) {
        buttonPanel.removeAll();
        for (int i = 0; i < options.length; i++) {
            JButton button = new JButton(options[i]);
            int index = i;
            button.addActionListener(e -> actions[index].run());
            buttonPanel.add(button);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void updateStatus() {
        statusLabel.setText("<html>" +
                "<span style='color:red;'>Health: " + RPGGame.health + "</span> | " +
                "<span style='color:orange;'>Gold: " + RPGGame.gold + "</span> | " +
                "<span style='color:blue;'>XP: " + RPGGame.xp + "</span> | " +
                "<span style='color:#8B4513;'>Weapon: " + RPGGame.weapons[RPGGame.currentWeapon].getName() + "</span>" +
                "</html>");
    }



    private void setMonsterStatus(String status) {
        monsterStatusLabel.setText(status);
    }

    private void limitTextArea() {
        String[] lines = textArea.getText().split("\n");
        if (lines.length > TEXT_LINE_LIMIT) {
            lines = java.util.Arrays.copyOfRange(lines, lines.length - TEXT_LINE_LIMIT, lines.length);
            textArea.setText(String.join("\n", lines));
        }
    }

    public static void main(String[] args) {
        new RPGGameGUI();
    }
}
