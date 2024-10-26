package org.example.matchinggameclient.home;
import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.matchinggameclient.MainApp;
import org.example.matchinggameclient.controller.MessageHandler;
import org.example.matchinggameclient.controller.SocketHandle;
import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.Timer;


public class HomeController implements MessageHandler {

    private ArrayList<User> userList;
    private ArrayList<Invitation> invitationList;
    private HomeView homeView;
    private User client;
    private SocketHandle socketHandle;
    private static boolean jfxInitialized = false;

    public HomeController(int clientId, ArrayList<User> userList, ArrayList<Invitation> invitationList, HomeView homeView)
    {
        if (!jfxInitialized) {
            try {
                Platform.startup(() -> {
                    jfxInitialized = true;
                });
            } catch (IllegalStateException e) {
                // JavaFX đã được khởi tạo
                jfxInitialized = true;
            }
        }
        this.userList = userList;
        this.homeView = homeView;
        this.invitationList = invitationList;
        socketHandle = new SocketHandle(this);
        new Thread(socketHandle).start();

        for(User u : userList)
        {
            if(u.getID() == clientId)
            {
                this.client = u;
                break;
            }
        }

        this.homeView.setClientInfo(client);
        this.homeView.addHistoryButtonListener(new HistoryButtonClick());
        this.homeView.addLogoutButtonListener(new LogOutButtonClick());
        this.homeView.addFindMatchButtonListener(new QuickMatchButtonClick());
        this.homeView.addPracticeButtonListener(new PracticeButtonClick());
        this.homeView.addCancelFindingButtonListener(new CancelFindingButtonClick());
        this.homeView.addSearchFilterListener(new SearchButtonClick());
        this.homeView.addSearchFieldListener(new SearchButtonClick());
        this.homeView.addSearchButtonListener(new SearchButtonClick());

        updatePlayerList("", HomeView.ALL_FILTER);
        updateInvitationList();
    }

    private boolean checkFilter(int filter, boolean isOnline)
    {
        if(filter == HomeView.ALL_FILTER || filter == HomeView.ONLINE_FILTER && isOnline)
        {
            return true;
        }
        return false;
    }

    public void updatePlayerList(String searchText, int filter)
    {
        homeView.removeAllPlayerRows();
        for(int i = 0; i < userList.size(); i++)
        {
            User u = userList.get(i);
//            String combinedName = String.format("#%03d", u.getRank()) + u.getUsername();
            String combinedName = u.getUsername();
            if(combinedName.contains(searchText) && checkFilter(filter, u.isOnline()))
            {
                homeView.insertPlayerRow(u, u.getID() != client.getID());
            }
        }
        homeView.reloadPlayerListUI();
    }

    public void updateInvitationList()
    {
        homeView.removeAllInvitations();
        for(Invitation invitation : invitationList)
        {
            homeView.insertInvitationRow(invitation);
        }
        homeView.reloadInvitationListUI();
    }

    @Override
    public void onMessageReceived(String message) {
        System.out.println("Received message: " + message); // Thêm log

        String[] messageSplit = message.split(",");
        System.out.println("Message type: " + messageSplit[0]);

        if (messageSplit[0].equals("restart")) {
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/org/example/matchinggameclient/Login.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    homeView.dispose();
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Unknown message: " + message);
        }
    }

    class HistoryButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Show history");
        }
    }

    class LogOutButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Log out");
            try {
                int id = client.getID();
                String request = "offline," + id;
                socketHandle.write(request);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class QuickMatchButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            homeView.setPlayButtonsLabelText("Finding match...");
            homeView.setFindMatchButtonEnabled(false);
            homeView.setPracticeButtonEnabled(false);
            homeView.setCancelFidningButtonVisible(true);
        }

    }

    class PracticeButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            homeView.setPlayButtonsLabelText("Starting practicing");
            homeView.setFindMatchButtonEnabled(false);
            homeView.setPracticeButtonEnabled(false);
            homeView.setCancelFidningButtonVisible(true);
        }

    }

    class CancelFindingButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            homeView.setPlayButtonsLabelText("Choose a play mode");
            homeView.setFindMatchButtonEnabled(true);
            homeView.setPracticeButtonEnabled(true);
            homeView.setCancelFidningButtonVisible(false);
        }

    }

    class SearchButtonClick implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int searchFilterIndex = homeView.getSearchFilterIndex();
            String searchFieldText = homeView.getSearchFieldText();
            updatePlayerList(searchFieldText, searchFilterIndex);
        }

    }
}