package org.example.matchinggameclient.home;

import org.example.matchinggameclient.model.Invitation;
import org.example.matchinggameclient.model.User;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.*;

public class HomeRun {
    public static void runHome(User client, ArrayList<User> userListHandle) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
//                Random r = new Random();
//                ArrayList<User> userList = new ArrayList<>();
                ArrayList<Invitation> invitationList = new ArrayList<>();

//                for (int i = 0; i < 10; i++) {
//                    User u = new User(i, "player" + i, r.nextInt(1000), r.nextBoolean());
//                    userList.add(u);
//                    if (u.isOnline()) {
//                        invitationList.add(new Invitation(u));
//                    }
//                }

//                Collections.sort(userList, new Comparator<User>() {
//                    public int compare(User u1, User u2) {
//                        return Integer.compare(u2.getStar(), u1.getStar());
//                    }
//                });

//                for (int i = 0; i < userList.size(); i++) {
//                    User u = userList.get(i);
//                    u.setRank(i + 1);
//                }

                HomeView homeView = new HomeView(client.getID());
                homeView.setVisible(true);
                HomeController homeController = new HomeController(client.getID(), userListHandle, invitationList, homeView);
            }
        });
    }
}

