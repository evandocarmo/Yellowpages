package com.elvisoliveira.yellowpages.ui;

import com.elvisoliveira.yellowpages.beans.contactbean;
import com.elvisoliveira.yellowpages.webservice.telelistas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import net.miginfocom.swing.MigLayout;

public class mainWindow {

    private static JScrollPane contactsListing;
    private static JTree tree;
    private static JTextField searchInput;
    private static JButton searchButton;
    private static List<String> strings;

    private static final JFrame window = new JFrame("YellowPages");
    private static final JPanel panel = new JPanel();

    // format the loading image with it's full path
    static final String ajaxLoader = String.format(
            "%s/src/main/java/%s/ajax-loader.gif",
            System.getProperty("user.dir").replace(".", "/"),
            new mainWindow().getClass().getPackage().getName().replace(".", "/")
    );

    public static void setContacts(String name) throws IOException {

        panel.setLayout(new MigLayout());

        // Search Fields
        searchInput = new JTextField();

        panel.add(searchInput, "growx, growy, split 2");

        // Search Button
        searchButton = new JButton();
        searchButton.setText("Buscar");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {

                // set the viewport
                contactsListing.setViewportView(new JLabel("loading... ", new ImageIcon(ajaxLoader), JLabel.CENTER));

                // get the input name
                final String name = searchInput.getText();

                SwingWorker<JTree, Void> swingWorker = new SwingWorker<JTree, Void>() {
                    @Override
                    public JTree doInBackground() throws IOException {
                        return changeContacts(name);
                    }

                    @Override
                    public void done() {
                        try {
                            contactsListing.setViewportView(get());
                        } catch (InterruptedException | ExecutionException ex) {
                            Logger.getLogger(mainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };

                swingWorker.execute();
            }
        });

        panel.add(searchButton, "wrap");

        //
        contactsListing = new JScrollPane();

        // if the name is not set, fulfill with instructions
        if (!name.isEmpty()) {
            contactsListing.setViewportView(changeContacts(name));
        } // else, make the preview
        else {
            contactsListing.setViewportView(new JLabel("Search a contact.", JLabel.CENTER));
        }

        contactsListing.setPreferredSize(new Dimension(400, 500));

        panel.add(contactsListing);

        // Window configuration
        window.add(panel);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        window.setResizable(false);

    }

    public static JTree changeContacts(String name) throws IOException {

        contactsListing.setViewportView(new JLabel("loading telelistas data...", new ImageIcon(ajaxLoader), JLabel.CENTER));

        List<contactbean> contactsList = telelistas.telelistas(name);

        contactsListing.setViewportView(new JLabel("forming menu tree...", new ImageIcon(ajaxLoader), JLabel.CENTER));

        tree = new JTree();

        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Contacts");

        for (contactbean object : contactsList) {
            DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(object.getName());
            DefaultMutableTreeNode nodeAddress = new DefaultMutableTreeNode(object.getAddress());
            DefaultMutableTreeNode nodeLink = new DefaultMutableTreeNode(object.getLink());

            nodeName.add(nodeAddress);
            nodeName.add(nodeLink);

            treeNode.add(nodeName);

        }

        tree.setModel(new DefaultTreeModel(treeNode));
        tree.setAutoscrolls(true);

        return tree;

    }

}
