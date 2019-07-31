package org.mlw.birdie;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.console.ConsolePlayerAdapter;
import org.mlw.birdie.engine.RookEngineBuilder;
import org.mlw.birdie.engine.event.support.GenericSubscriberExceptionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Main extends JDialog {
    private JPanel contentPane = new JPanel();

    public Main() {
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void main(String[] args) throws IOException {

        Deck deck = DeckFactory.getStandardDeck();

        CardTablePanel panel = new CardTablePanel(new CardImagesAwt(deck.getCards()));
        panel.setBackground(Color.DARK_GRAY);

        Main dialog = new Main();
        dialog.setContentPane(panel);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);


        EventBus serverBus = new EventBus(new GenericSubscriberExceptionHandler());

        new RookEngineBuilder(4)
                .server(serverBus)
                .player(new ConsolePlayerAdapter(serverBus), panel)
                .build().run();
    }

    private void onCancel() {
        dispose();
    }
}
