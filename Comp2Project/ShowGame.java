package Comp2Project;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDate;

public class ShowGame extends JFrame implements ActionListener {
    JPanel contentPane, userpanel, dealerpanel;
    JButton push_button, hit_button, stay_button, draw_button;

    JTextField betting_money, input_money1, input_money2;
    JLabel label1, label2, label3, card_sum1, card_sum2, user_money, dealer_money, result;
    JTextArea userarea, dealerarea;

    Deck deck;
    Player<Card> player;
    Player<Card> dealer;
    File file;
    int dealermoney, playermoney, bettingmoney;
    final static String RESULT_FILE_PATH = "./history.txt";

    JButton restart_button; // 재시작을 위한 새로운 버튼

    ShowGame() {
        makeGame();
        setComponent();
    }

    //컴포넌트 배치
    void setComponent() {
        setLayout(null);
        setBounds(200, 40, 820, 760);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        userpanel = new JPanel(new BorderLayout());
        dealerpanel = new JPanel(new BorderLayout());
        dealerpanel.setBounds(5, 0, 790, 250);
        userpanel.setBounds(5, dealerpanel.getHeight(), 790, 250);
        userpanel.setBorder(BorderFactory.createTitledBorder("Player"));
        dealerpanel.setBorder(BorderFactory.createTitledBorder("Dealer"));

        userarea = new JTextArea(5, 15);
        dealerarea = new JTextArea(5, 15);
        Font font = new Font("Arial", Font.PLAIN, 25);
        userarea.setFont(font);
        dealerarea.setFont(font);
        userarea.setEditable(false);
        dealerarea.setEditable(false);

        //카드의 합 보여주는 라벨
        card_sum1 = new JLabel("");
        card_sum2 = new JLabel("");
        dealer_money = new JLabel();
        user_money = new JLabel();


        userpanel.add(userarea, BorderLayout.CENTER);
        dealerpanel.add(dealerarea, BorderLayout.CENTER);
        userpanel.add(card_sum1, BorderLayout.WEST);
        dealerpanel.add(card_sum2, BorderLayout.WEST);
        userpanel.add(user_money, BorderLayout.EAST);
        dealerpanel.add(dealer_money, BorderLayout.EAST);

        hit_button = new JButton(" hit");
        hit_button.setBounds(150, 650, 90, 30);
        hit_button.setEnabled(false);
        hit_button.addActionListener(this);

        stay_button = new JButton(" stay");
        stay_button.setEnabled(false);
        stay_button.addActionListener(this);

        draw_button = new JButton("draw");
        draw_button.addActionListener(this);
        stay_button.setBounds(hit_button.getX() + 100, hit_button.getY(), 90, 30);
        draw_button.setBounds(hit_button.getX() - 100, hit_button.getY(), 90, 30);
        draw_button.setEnabled(false);

        //==================재시작 버튼 추가 ======================
        restart_button = new JButton("Restart"); // "Restart" 버튼 추가
        restart_button.addActionListener(this);
        restart_button.setBounds(draw_button.getX(), draw_button.getY() - 40, 90, 30);
        restart_button.setEnabled(false); // 게임 종료 시에만 활성화

        label1 = new JLabel("Dealer Money: ");
        label1.setFont(new Font("Arial", Font.PLAIN, 15));
        label1.setBounds(450, 580, 200, 100);
        label2 = new JLabel("Player Money: ");
        label2.setFont(new Font("Arial", Font.PLAIN, 15));
        label2.setBounds(450, label1.getY() + 30, 200, 100);
        label3 = new JLabel("Betting Money: ");
        label3.setFont(new Font("Arial", Font.PLAIN, 15));
        label3.setBounds(450, label2.getY() + 30, 200, 100);

        //승패 메세지 보여주는 라벨
        result = new JLabel();
        result.setForeground(Color.BLUE);
        result.setFont(new Font("Arial", Font.PLAIN, 30));
        result.setBounds(300, stay_button.getY() - 110, 200, 60);

        // 보유금액, 배팅금액 입력
        betting_money = new JTextField(20);
        input_money1 = new JTextField(20);
        input_money2 = new JTextField(20);
        betting_money.setBounds(label1.getX() + 105, 680, 100, 23);
        input_money1.setBounds(betting_money.getX(), betting_money.getY() - 30, 100, 23);
        input_money2.setBounds(betting_money.getX(), betting_money.getY() - 60, 100, 23);

        //입력받은 값 보여줌
        input_money1.addActionListener(e -> {
            playermoney = Integer.parseInt(input_money1.getText());
            String str = " 보유금액: " + playermoney;
            user_money.setText(str + " ");
            input_money1.setEditable(false);
        });
        input_money2.addActionListener(e -> {
            dealermoney = Integer.parseInt(input_money2.getText());
            String str = " 보유금액: " + dealermoney;
            dealer_money.setText(str + " ");
            input_money2.setEditable(false);
        });

        // 딜러와 플레이어 같은 금액 배팅, 금액 설정하면 배팅금액의 절반만큼 보유금액에서 뺌
        betting_money.addActionListener(e -> {
            bettingmoney = Integer.parseInt(betting_money.getText());
            if (dealermoney >= bettingmoney && playermoney >= bettingmoney && bettingmoney > 0) {
                draw_button.setEnabled(true);
//                dealermoney -= bettingmoney / 2;
//                playermoney -= bettingmoney / 2;

                String str1 = " 보유금액: " + dealermoney;
                String str2 = " 보유금액: " + playermoney;
                dealer_money.setText(str1 + " ");
                user_money.setText(str2 + " ");
                betting_money.setEditable(false);
            } else if (bettingmoney == 0) {
                betting_money.setText("");
                JOptionPane.showMessageDialog(this, "베팅금액은 0 이상 가능합니다.");
                draw_button.setEnabled(false);
            } else {
                // 보유금액이 부족한 경우 처리
                betting_money.setText("");
                JOptionPane.showMessageDialog(this, "보유금액이 부족합니다. 다시 베팅해주세요.");
                draw_button.setEnabled(false);
            }
        });

        contentPane.add(betting_money);
        contentPane.add(label1);
        contentPane.add(label2);
        contentPane.add(label3);

        contentPane.add(input_money1);
        contentPane.add(input_money2);

        contentPane.add(hit_button);
        contentPane.add(stay_button);
        contentPane.add(draw_button);
        contentPane.add(result);
        contentPane.add(dealerpanel);
        contentPane.add(userpanel);

        contentPane.add(restart_button); // "Restart" 버튼 추가

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    void makeGame() {
        deck = new Deck();
        player = new Player<>("Comp2Project.Player");
        dealer = new Player<>("Dealer");
    }

    // history.txt에 전적 기록
    void saveResult() {

        LocalDate now = LocalDate.now();

        file = new File(RESULT_FILE_PATH);
        String resultMessage = result.getText();
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file, true))) {
            System.setOut(printStream);
            System.out.println(resultMessage);
            System.out.println();
            player.displayHand();
            dealer.displayHand();
            System.out.println(now);
            System.out.println("=============================");
            System.out.println("=============================");
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == restart_button) { //재시작 버튼
            restartGame();
        }
        
        if (e.getSource() == draw_button) { // 카드 draw
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());
            player.addCard(deck.drawCard());
            dealer.addCard(deck.drawCard());

            for (Card card : player.hand)
                userarea.append(card.printCard() + "\n");

            int cnt = 0;
            for (Card card : dealer.hand) {
                if (cnt != 0) {dealerarea.append("Unknown" + "\n");}
                else {dealerarea.append(card.printCard() + "\n");}

                cnt++;
            }
            draw_button.setEnabled(false);
            hit_button.setEnabled(true);
            stay_button.setEnabled(true);
            card_sum1.setText(" SUM: " + player.getScore() + "  ");
            card_sum2.setText("Unknown" + "  ");
        }
        if (e.getSource() == hit_button) {
            player.addCard(deck.drawCard());
            userarea.append(player.hand.get(player.hand.size() - 1).printCard() + "\n");
            card_sum1.setText(" SUM: " + player.getScore() + "  ");
            if (player.getScore() > 21) {
                card_sum2.setText(" SUM: " + dealer.getScore() + "  ");
//                card_sum1.setText(" SUM: " + player.getScore() + "  ");
                result.setText("Bust! you lose.");
                dealermoney += bettingmoney;
                playermoney -= bettingmoney;
                dealer_money.setText("보유금액: " + dealermoney +" ");
                user_money.setText("보유금액: " + playermoney +" ");

                hit_button.setEnabled(false);
                stay_button.setEnabled(false);
                saveResult();
                // "Restart" 버튼 활성화
                if (playermoney * dealermoney != 0) {
                    restart_button.setEnabled(true);
                    hit_button.setEnabled(false);
                    stay_button.setEnabled(false);
                    draw_button.setEnabled(false);
                    NewGame();
                } else {
                    JOptionPane.showMessageDialog(this, "게임 종료!");
                }

            }
        }
        if (e.getSource() == stay_button) {
            while (dealer.getScore() < 18) {
                dealer.addCard(deck.drawCard());
                dealerarea.append("Unknown\n");
            }

            card_sum2.setText(" SUM: " + dealer.getScore() + "  ");
            card_sum1.setText(" SUM: " + player.getScore() + "  ");

            if (dealer.getScore() > 21 || player.getScore() > dealer.getScore()) {
                result.setText("You win!");
                dealermoney -= bettingmoney;
                playermoney += bettingmoney;
                dealer_money.setText("보유금액: " + dealermoney +" ");
                user_money.setText("보유금액: " + playermoney +" ");
                saveResult();
                // "Restart" 버튼 활성화
                if (playermoney * dealermoney != 0) {
                    restart_button.setEnabled(true);
                    hit_button.setEnabled(false);
                    stay_button.setEnabled(false);
                    draw_button.setEnabled(false);
                    NewGame();
                } else {
                    JOptionPane.showMessageDialog(this, "게임 종료!");
                }
            } else if (player.getScore() == dealer.getScore()) {
                result.setText("Draw");
                saveResult();
                // "Restart" 버튼 활성화
                if (playermoney * dealermoney != 0) {
                    restart_button.setEnabled(true);
                    hit_button.setEnabled(false);
                    stay_button.setEnabled(false);
                    draw_button.setEnabled(false);
                    NewGame();
                } else {
                    JOptionPane.showMessageDialog(this, "게임 종료!");
                    hit_button.setEnabled(false);
                    stay_button.setEnabled(false);
                }
            } else {
                result.setText("You lose.");
                dealermoney += bettingmoney;
                playermoney -= bettingmoney;
                dealer_money.setText("보유금액: " + dealermoney +" ");
                user_money.setText("보유금액: " + playermoney +" ");
                // "Restart" 버튼 활성화
                if (playermoney * dealermoney != 0) {
                    restart_button.setEnabled(true);
                    hit_button.setEnabled(false);
                    stay_button.setEnabled(false);
                    draw_button.setEnabled(false);
                    NewGame();
                } else {
                    JOptionPane.showMessageDialog(this, "게임 종료!");
                }
            }
        }

    }

    void NewGame() {
        if (dealermoney > 0 || playermoney > 0) {
            player.clearHand();
            dealer.clearHand();
            makeGame();
        }
    }

    void restartGame() {
        restart_button.setEnabled(false); // "Restart" 버튼 비활성화

        // 게임 초기화
        player.clearHand();
        dealer.clearHand();
        makeGame();

        // 이전에 입력받은 금액 사용
        String str1 = " 보유금액: " + dealermoney;
        String str2 = " 보유금액: " + playermoney;
        dealer_money.setText(str1 + " ");
        user_money.setText(str2 + " ");

        // 보유금액 표시 라벨 초기화
        // user_money 라벨은 항상 표시
        user_money.setText(str2 + " ");
        // dealer_money 라벨은 새로운 게임 시작 시 표시
        dealer_money.setText(str1 + " ");

        // 텍스트필드 초기화 및 입력 불가능 상태로 설정
        betting_money.setText("");
        input_money1.setText("");
        input_money2.setText("");
        betting_money.setEditable(true);
        input_money1.setEditable(false);
        input_money2.setEditable(false);

        // 새로운 게임 시작
        draw_button.setEnabled(true);
        result.setText("");
        card_sum1.setText("");
        card_sum2.setText("");
        userarea.setText("");
        dealerarea.setText("");

        hit_button.setEnabled(false);
        stay_button.setEnabled(false);
        draw_button.setEnabled(false);
        // 보유금액이 0 미만인 경우 재시작 버튼 비활성화
        if (dealermoney <= 0 || playermoney <= 0) {
            restart_button.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        ShowGame game = new ShowGame();
    }
}
