package org.example.matchinggameclient.home;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.example.matchinggameclient.components.CustomPanel;
import org.example.matchinggameclient.components.CustomScrollBarUI;
import org.example.matchinggameclient.components.InvitationRow;
import org.example.matchinggameclient.components.UserRow;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;
import org.example.matchinggameclient.components.BackgroundPanel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HomeView extends JFrame {

    public static int ALL_FILTER = 0;
    public static int ONLINE_FILTER = 1;

    private JLabel playerInfo;
    private JButton historyButton;
    private JButton logOutButton;

    private JLabel playButtonsLabel;
    private JButton cancelFindingButton;
    private JButton findMatchButton;
    private JButton practiceButton;

    private JComboBox<String> filterComboBox;
    private JTextField playerListSearchField;
    private JButton playerListSearchButton;

    private JPanel playerListContent;
    private JPanel invitationListContent;
    private int clientId;

    public HomeView(int clientId) {
        this.clientId = clientId;
        setTitle("Memory Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);

        //Set icon
        try {
            Image icon = ImageIO.read(new File("src/main/java/org/example/matchinggameclient/img/logo.png"));
            setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Main Panel
        JPanel mainPanel = new BackgroundPanel("src/main/java/org/example/matchinggameclient/img/back8.png");
        mainPanel.setLayout(new BorderLayout());

        // Top Menu Panel
        JPanel topMenu = new JPanel();
        topMenu.setLayout(new BorderLayout(10, 10));
        topMenu.setBackground(new Color(35, 124, 93));
        topMenu.setPreferredSize(new Dimension(50, 50));
        topMenu.setBorder(new EmptyBorder(5, 10, 5, 0));

        JPanel playerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        playerInfoPanel.setOpaque(false);
        playerInfo = new JLabel("Player: nguyenvietha    Rank: #003    Stars: 100");
        playerInfo.setBorder(new EmptyBorder(2, 0, 0, 0));
        playerInfo.setForeground(Color.WHITE);
        playerInfo.setFont(new Font("Arial", Font.BOLD, 22));
        playerInfoPanel.add(playerInfo, BorderLayout.CENTER);

        JPanel historyLogoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        historyLogoutPanel.setOpaque(false);
        //historyLogoutPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        historyButton = new JButton("History");
        logOutButton = new JButton("Log out");
        logOutButton.setBackground(new Color(130, 0, 0));
        logOutButton.setForeground(Color.WHITE);
        logOutButton.setFont(new Font("Arial", Font.PLAIN, 16));
        historyButton.setBackground(new Color(190, 70, 0));
        historyButton.setForeground(Color.WHITE);
        historyButton.setFont(new Font("Arial", Font.PLAIN, 16));
        historyLogoutPanel.add(historyButton);
        historyLogoutPanel.add(logOutButton);

//        BackgroundPanel userIconPanel = new BackgroundPanel("./resources/profile-user.png");
//        userIconPanel.setPreferredSize(new Dimension(45, 45));
//        userIconPanel.setOpaque(false);
//        userIconPanel.setBorder(new EmptyBorder(5, 10, 5, 5));

//        topMenu.add(userIconPanel, BorderLayout.WEST);
        topMenu.add(playerInfoPanel, BorderLayout.CENTER);
        topMenu.add(historyLogoutPanel, BorderLayout.EAST);

        JPanel playButtonsAndPlayerListPanel = new JPanel();
        playButtonsAndPlayerListPanel.setLayout(new BorderLayout(0, 20));
        playButtonsAndPlayerListPanel.setOpaque(false);
        playButtonsAndPlayerListPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Play Buttons Panel
        JPanel playButtonsPanel = new CustomPanel(20, 0.7f);
        playButtonsPanel.setOpaque(false);
        playButtonsPanel.setPreferredSize(new Dimension(400, 150));
        playButtonsPanel.setLayout(new GridLayout(2, 2, 40, 20));
        playButtonsPanel.setBackground(new Color(74, 107, 124));
        playButtonsPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        playButtonsLabel = new JLabel("Choose a play mode");
        playButtonsLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        playButtonsLabel.setForeground(Color.white);
        playButtonsLabel.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 25));
        playButtonsLabel.setVerticalAlignment(JLabel.CENTER);
        playButtonsPanel.add(playButtonsLabel);

        cancelFindingButton = new JButton("Cancel");
        cancelFindingButton.setForeground(Color.white);
        cancelFindingButton.setBackground(new Color(180, 0, 0));
        cancelFindingButton.setFont(new Font("Arial", Font.BOLD, 28));
        cancelFindingButton.setVisible(false);
        playButtonsPanel.add(cancelFindingButton);


        findMatchButton = new JButton("Find Match");
        findMatchButton.setForeground(Color.white);
        findMatchButton.setBackground(new Color(35, 124, 93));
        findMatchButton.setFont(new Font("Arial", Font.BOLD, 28));
        findMatchButton.setBorder(null);
        playButtonsPanel.add(findMatchButton);

        practiceButton = new JButton("Practice");
        practiceButton.setForeground(Color.white);
        practiceButton.setBackground(new Color(35, 124, 93));
        practiceButton.setFont(new Font("Arial", Font.BOLD, 28));
        practiceButton.setBorder(null);
        playButtonsPanel.add(practiceButton);

        playButtonsAndPlayerListPanel.add(playButtonsPanel, BorderLayout.NORTH);

        // Player List Panel

        JPanel playerListPanel = new CustomPanel(25, 0f);
        playerListPanel.setLayout(new BorderLayout());
        playerListPanel.setOpaque(false);
        playerListPanel.setBorder(null);
//        Border playerListBorder = BorderFactory.createLineBorder(
//        		new Color(0, 150, 15), 3);
//        playerListPanel.setBorder(playerListBorder);

        JLabel playerListLabel = new JLabel("PLAYER LIST", SwingConstants.CENTER);
        playerListLabel.setFont(new Font("Arial", Font.BOLD, 26));
        playerListLabel.setForeground(Color.black);
        playerListLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel playerListSearchPanel = new CustomPanel(20, 0.5f);
        playerListSearchPanel.setLayout(new BorderLayout());
        playerListSearchPanel.setBackground(new Color(250, 250, 250));
        playerListSearchPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        playerListSearchField = new JTextField(30);
        playerListSearchField.setFont(new Font("Arial", Font.PLAIN, 20));

        playerListSearchButton = new JButton("Search");
        playerListSearchButton.setBackground(new Color(35, 124, 93));
        playerListSearchButton.setForeground(Color.white);
        playerListSearchButton.setFont(new Font("Arial", Font.PLAIN, 16));


        String[] filterItems = {"All", "Online"};
        filterComboBox = new JComboBox<String>(filterItems);
        filterComboBox.setBackground(Color.white);
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        playerListSearchPanel.add(filterComboBox, BorderLayout.WEST);
        playerListSearchPanel.add(playerListSearchButton, BorderLayout.EAST);
        playerListSearchPanel.add(playerListSearchField, BorderLayout.CENTER);

        JPanel playerListLabelAndSearchPanel = new JPanel();
        playerListLabelAndSearchPanel.setBorder(null);
        playerListLabelAndSearchPanel.setLayout(new BorderLayout());
        playerListLabelAndSearchPanel.setOpaque(false);
        playerListLabelAndSearchPanel.add(playerListLabel, BorderLayout.NORTH);
        playerListLabelAndSearchPanel.add(playerListSearchPanel, BorderLayout.SOUTH);
        playerListPanel.add(playerListLabelAndSearchPanel, BorderLayout.NORTH);

        playerListContent = new CustomPanel(25, 0.1f);
        playerListContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        playerListContent.setLayout(new BoxLayout(playerListContent, BoxLayout.Y_AXIS));

        JScrollPane playerListScroll = new JScrollPane(playerListContent);
        playerListScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.LIGHT_GRAY, new Color(2, 69, 0)));
        playerListScroll.setOpaque(false);
        playerListScroll.getViewport().setOpaque(false);
        playerListScroll.setBorder(null);
        playerListScroll.setViewportBorder(null);
        playerListPanel.add(playerListScroll, BorderLayout.CENTER);


        playButtonsAndPlayerListPanel.add(playerListPanel, BorderLayout.CENTER);

        // Invitations Panel
        JPanel invitationsPanel = new CustomPanel(0, 0.5f);
        invitationsPanel.setPreferredSize(new Dimension(300, 100));
        invitationsPanel.setLayout(new BorderLayout());
        invitationsPanel.setBackground(new Color(2, 69, 0));
        invitationsPanel.setBorder(new EmptyBorder(0, 20, 20, 10));

        invitationListContent = new JPanel();
        invitationListContent.setLayout(new BoxLayout(invitationListContent, BoxLayout.Y_AXIS));
        invitationListContent.setOpaque(false);
        invitationListContent.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel invitationsLabel = new JLabel("INVITATIONS");
        invitationsLabel.setFont(new Font("Arial", Font.BOLD, 25));
        invitationsLabel.setForeground(Color.white);
        invitationsLabel.setHorizontalAlignment(JLabel.CENTER);
        invitationsLabel.setBorder(new EmptyBorder(15, 0, 20, 0));
        invitationsPanel.add(invitationsLabel, BorderLayout.NORTH);

        JScrollPane inviteScroll = new JScrollPane(invitationListContent);
        inviteScroll.setBorder(null);
        inviteScroll.setOpaque(false);
        inviteScroll.getViewport().setOpaque(false);
        inviteScroll.setViewportBorder(null);
        inviteScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.LIGHT_GRAY, new Color(2, 69, 0)));
        invitationsPanel.add(inviteScroll, BorderLayout.CENTER);

        // Add everything to mainPanel
        mainPanel.add(topMenu, BorderLayout.NORTH);
        mainPanel.add(playButtonsAndPlayerListPanel, BorderLayout.CENTER);
        mainPanel.add(invitationsPanel, BorderLayout.EAST);
        // Add mainPanel to JFrame
        add(mainPanel);
    }

    public void setClientInfo(User client)
    {
        String content = String.format("Player: %s    Rank: #%03d    Stars: %d",
                client.getUsername(), client.getRank(), client.getStar());
        playerInfo.setText(content);
    }

    public void setPlayButtonsLabelText(String content)
    {
        playButtonsLabel.setText(content);
    }

    public void setCancelFidningButtonVisible(boolean visible)
    {
        cancelFindingButton.setVisible(visible);
    }

    public void setFindMatchButtonEnabled(boolean enabled)
    {
        findMatchButton.setEnabled(enabled);
    }

    public void setPracticeButtonEnabled(boolean enabled)
    {
        practiceButton.setEnabled(enabled);
    }

    public void setSearchButtonEnabled(boolean enabled)
    {
        playerListSearchButton.setEnabled(enabled);
    }

    public String getSearchFieldText()
    {
        return playerListSearchField.getText();
    }

    public int getSearchFilterIndex()
    {
        return filterComboBox.getSelectedIndex();
    }

    public void addHistoryButtonListener(ActionListener listener)
    {
        historyButton.addActionListener(listener);
    }

    public void addLogoutButtonListener(ActionListener listener)
    {
        logOutButton.addActionListener(listener);
    }

    public void addFindMatchButtonListener(ActionListener listener)
    {
        findMatchButton.addActionListener(listener);
    }

    public void addPracticeButtonListener(ActionListener listener)
    {
        practiceButton.addActionListener(listener);
    }

    public void addCancelFindingButtonListener(ActionListener listener)
    {
        cancelFindingButton.addActionListener(listener);
    }

    public void addSearchButtonListener(ActionListener listener)
    {
        playerListSearchButton.addActionListener(listener);
    }

    public void addSearchFilterListener(ActionListener listener)
    {
        filterComboBox.addActionListener(listener);
    }

    public void addSearchFieldListener(ActionListener listener)
    {
        playerListSearchField.addActionListener(listener);
    }

    public void insertPlayerRow(User user, boolean showInviteButton)
    {
        UserRow userRow = new UserRow(user, showInviteButton);
        playerListContent.add(userRow);
        playerListContent.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    public void removeAllPlayerRows()
    {
        playerListContent.removeAll();
    }

    public void reloadPlayerListUI()
    {
        playerListContent.revalidate();
        playerListContent.repaint();
    }

    public void insertInvitationRow(Invitation invitation)
    {
        InvitationRow invitationRow = new InvitationRow(invitation);
        invitationListContent.add(invitationRow);
        invitationListContent.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    public void removeAllInvitations()
    {
        invitationListContent.removeAll();
    }

    public void reloadInvitationListUI()
    {
        invitationListContent.revalidate();
        invitationListContent.repaint();
    }
    public void updateUserList(ArrayList<User> updatedUsers) {
        // Clear the existing player rows
        removeAllPlayerRows();

        // Insert the updated user rows
        for (User user : updatedUsers) {
//            insertPlayerRow(user);
            if (user.getID() != clientId) {
                // Add the invite button for other users
                insertPlayerRow(user, true); // true to show the invite button
            } else {
                insertPlayerRow(user, false); // false to hide the invite button
            }
        }

        // Refresh the player list UI
        reloadPlayerListUI();
    }
}