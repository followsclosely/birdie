package org.mlw.birdie;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.mlw.birdie.console.ConsolePlayerAdapter;
import org.mlw.birdie.engine.RookEngineBuilder;
import org.mlw.birdie.engine.event.support.GenericSubscriberExceptionHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main extends JDialog {
    private JPanel contentPane = new JPanel();

    public Main() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

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
        dialog.setSize(900, 200);
        //dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        EventBus guiBus = new AsyncEventBus("swing", Executors.newFixedThreadPool(1));
        guiBus.register(panel);

        EventBus serverBus = new EventBus(new GenericSubscriberExceptionHandler());

        new RookEngineBuilder(4)
                .server(serverBus)
                .player(new ConsolePlayerAdapter(serverBus, guiBus))
                .build().run();
    }

    private void onCancel() {
        dispose();
    }
}
