import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RPGGame {

    static int xp = 0;
    static int health = 100;
    static int gold = 50;
    static int currentWeapon = 0;
    static int fighting;
    static int monsterHealth;
    static List<String> inventory = new ArrayList<>();
    static Weapon[] weapons = {
            new Weapon("stick", 5),
            new Weapon("dagger", 30),
            new Weapon("claw hammer", 50),
            new Weapon("sword", 100)
    };
    static Monster[] monsters = {
            new Monster("slime", 2, 15),
            new Monster("fanged beast", 8, 60),
            new Monster("dragon", 20, 300)
    };

    public static void main(String[] args) {
        inventory.add("stick");
        goTown();
    }

    static void goTown() {
        System.out.println("You are in the town square. You see a sign that says \"Store\".");
        System.out.println("1: Go to store\n2: Go to cave\n3: Fight dragon");
        makeChoice(() -> goStore(), () -> goCave(), () -> fightDragon());
    }

    static void goStore() {
        System.out.println("You enter the store.");
        System.out.println("1: Buy 10 health (10 gold)\n2: Buy weapon (30 gold)\n3: Go to town square");
        makeChoice(() -> buyHealth(), () -> buyWeapon(), () -> goTown());
    }

    static void goCave() {
        System.out.println("You enter the cave. You see some monsters.");
        System.out.println("1: Fight slime\n2: Fight fanged beast\n3: Go to town square");
        makeChoice(() -> fight(0), () -> fight(1), () -> goTown());
    }

    static void fight(int monsterIndex) {
        fighting = monsterIndex;
        monsterHealth = monsters[fighting].getHealth();
        System.out.println("You are fighting a " + monsters[fighting].getName());
        System.out.println("1: Attack\n2: Dodge\n3: Run");
        makeChoice(() -> attack(), () -> dodge(), () -> goTown());
    }

    static void fightDragon() {
        fight(2);
    }

    static void attack() {
        System.out.println("You attack the " + monsters[fighting].getName() + " with your " + weapons[currentWeapon].getName() + ".");
        int damage = weapons[currentWeapon].getPower() + new Random().nextInt(xp + 1);
        monsterHealth -= damage;
        System.out.println("You dealt " + damage + " damage!");
        int monsterDamage = getMonsterAttackValue(monsters[fighting].getLevel());
        health -= monsterDamage;
        System.out.println("The " + monsters[fighting].getName() + " dealt " + monsterDamage + " damage!");

        if (health <= 0) {
            lose();
        } else if (monsterHealth <= 0) {
            if (fighting == 2) winGame();
            else defeatMonster();
        } else {
            fight(fighting);
        }
    }

    static void dodge() {
        System.out.println("You dodge the attack from the " + monsters[fighting].getName());
        fight(fighting);
    }

    static void defeatMonster() {
        System.out.println("You defeated the " + monsters[fighting].getName() + "!");
        gold += monsters[fighting].getLevel() * 6;
        xp += monsters[fighting].getLevel();
        System.out.println("You gained " + (monsters[fighting].getLevel() * 6) + " gold and " + monsters[fighting].getLevel() + " XP.");
        goCave();
    }

    static void buyHealth() {
        if (gold >= 10) {
            gold -= 10;
            health += 10;
            System.out.println("You bought 10 health. Current health: " + health + ", gold: " + gold);
        } else {
            System.out.println("You do not have enough gold to buy health.");
        }
        goStore();
    }

    static void buyWeapon() {
        if (currentWeapon < weapons.length - 1) {
            if (gold >= 30) {
                gold -= 30;
                currentWeapon++;
                inventory.add(weapons[currentWeapon].getName());
                System.out.println("You bought a " + weapons[currentWeapon].getName() + ".");
            } else {
                System.out.println("You do not have enough gold to buy a weapon.");
            }
        } else {
            System.out.println("You already have the most powerful weapon!");
        }
        goStore();
    }

    static void lose() {
        System.out.println("You died. Game over.");
        restart();
    }

    static void winGame() {
        System.out.println("You defeated the dragon! YOU WIN!");
        restart();
    }

    static void restart() {
        xp = 0;
        health = 100;
        gold = 50;
        currentWeapon = 0;
        inventory.clear();
        inventory.add("stick");
        goTown();
    }

    static int getMonsterAttackValue(int level) {
        int damage = level * 5 - new Random().nextInt(xp + 1);
        return Math.max(damage, 0);
    }

    static void makeChoice(Runnable... actions) {
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice > 0 && choice <= actions.length) {
            actions[choice - 1].run();
        } else {
            System.out.println("Invalid choice, try again.");
            makeChoice(actions);
        }
    }
}
