package org.example.matchinggameclient.components;

import org.example.matchinggameclient.model.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class UserRow extends CustomPanel
{
    private User user;

    private JButton inviteButton;
    private JButton historyButton;

    public UserRow(User user, boolean showInviteButton)
    {
        super(20, 0.8f);
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setOpaque(false);
        playerPanel.setBorder(new EmptyBorder(0, 10, 0, 20));

        JLabel playerRankLabel = new JLabel(String.format("#%03d", user.getRank()));
        playerRankLabel.setFont(new Font("Arial", Font.BOLD, 22));
        playerRankLabel.setForeground(new Color(0, 140, 255));
        playerPanel.add(playerRankLabel, BorderLayout.WEST);

        JLabel playerNameLabel = new JLabel(user.getUsername());
        playerNameLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
        Color statusColor = new Color(50, 160, 0);
        if(!user.isOnline())
        {
            statusColor = new Color(140, 140, 140);
        }
        playerNameLabel.setForeground(statusColor);
        playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerNameLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        playerPanel.add(playerNameLabel, BorderLayout.CENTER);

        JLabel playerStarsLabel = new JLabel("" + user.getStar());
        playerStarsLabel.setFont(new Font("Arial", Font.BOLD, 22));
        playerStarsLabel.setHorizontalAlignment(JLabel.RIGHT);
        playerStarsLabel.setForeground(new Color(200, 0, 200));
        playerStarsLabel.setPreferredSize(new Dimension(50, 50));
        playerPanel.add(playerStarsLabel, BorderLayout.EAST);

        JPanel availableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        availableButtonPanel.setOpaque(false);
        availableButtonPanel.setBorder(new EmptyBorder(5, 0, 0, 10));

        inviteButton = new JButton("Invite");
        inviteButton.setBackground(new Color(35, 124, 93));
        inviteButton.setForeground(Color.white);
        inviteButton.setFont(new Font("Arial", Font.PLAIN, 16));
        inviteButton.addActionListener(new InviteButtonClick());

        if (showInviteButton)
        {
            if(!user.isOnline())
                inviteButton.setEnabled(false);
        } else {
            // Đổi thành màu trong suốt
            inviteButton.setBackground(new Color(0, 0, 0, 0));
            inviteButton.setForeground(new Color(0, 0, 0, 0));
            inviteButton.setEnabled(false);

        }

        historyButton = new JButton("History");
        historyButton.setBackground(new Color(190, 70, 0));
        historyButton.setForeground(Color.white);
        historyButton.setFont(new Font("Arial", Font.PLAIN, 16));
        historyButton.addActionListener(new HistoryButtonClick());

        availableButtonPanel.add(inviteButton);
        availableButtonPanel.add(historyButton);

        add(playerPanel, BorderLayout.CENTER);
        add(availableButtonPanel, BorderLayout.EAST);

    }

    class HistoryButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Show history of player " + user.getUsername());
        }
    }

    class InviteButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Invite " + user.getUsername());
        }
    }
}