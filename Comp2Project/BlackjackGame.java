package Comp2Project;

import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;
        import java.util.Scanner;
class Card {
    String pattern;
    String num;

    public Card(String pattern, String num) {
        this.pattern = pattern;
        this.num = num;
    }

    public String printCard() {
        return pattern + ": " + num;
    }

    public int getValue() {
        // 일단 11로 하고 마지막에 다시 계산
        if (num.equals("Ace")) {
            return 11;
        } else if (num.equals("K") || num.equals("Q") || num.equals("J")) {
            return 10;
        } else {
            return Integer.parseInt(num);
        }
    }

    public boolean isAce() {
        return num.equals("Ace");
    }
}

class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] patterns = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] nums = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "Ace"};

        for (String pattern : patterns) {
            for (String num : nums) {
                cards.add(new Card(pattern, num));
            }
        }

        Collections.shuffle(cards);
    }

    public Card drawCard() {
        return cards.remove(0);
    }
}

class Player<T extends Card> {
    String name;
    List<T> hand;
    int score = 0;
    int aceCount = 0;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(T card) {
        hand.add(card);
        score += card.getValue();

        if (card.isAce()) {
            aceCount++;
        }

        while (aceCount > 0 && score > 21) {
            score -= 10;
            aceCount--;
        }
    }
    public void clearHand() {
        hand.clear();
        score = 0;
        aceCount = 0;
    }

    public int getScore() {
        return score;
    }

    public void displayHand() {
        System.out.println(name + "'s Hand:");

        for (T card : hand) {
            System.out.println("    " + card.printCard());
        }

        System.out.println("Total Score: " + score);
        System.out.println();
    }
}

public class BlackjackGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Deck deck = new Deck();
        Player<Card> player = new Player<>("Comp2Project.Player");
        Player<Card> dealer = new Player<>("Dealer");

        // 처음에 두 장씩 나눠줌
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
        player.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        // 카드 딜링
        while (true) {
            player.displayHand();
            dealer.displayHand();

            System.out.print("Do you want to hit or stand? (h/s): ");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("h")) {
                player.addCard(deck.drawCard());
                if (dealer.getScore() < 18){
                    dealer.addCard(deck.drawCard());
                }

            } else if (choice.equalsIgnoreCase("s")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'h' or 's'.");
                continue;
            }

            if (player.getScore() > 21 || dealer.getScore() > 21) {
                break;
            }

            System.out.println("==========================================");
        }

        // 결과 출력
        player.displayHand();
        System.out.println();
        dealer.displayHand();

        if (player.getScore() > 21) {
            System.out.println("Bust! You lose.");
        } else if (dealer.getScore() > 21 || player.getScore() > dealer.getScore()) {
            System.out.println("You win!");
        } else if (player.getScore() == dealer.getScore()) {
            System.out.println("Draw!");
        } else {
            System.out.println("You lose.");
        }

        scanner.close();
    }
}
