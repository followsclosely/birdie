package org.mlw.birdie;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardImagesAwt implements CardImages {

    private static final Logger log = LoggerFactory.getLogger(CardImagesAwt.class);
    private final Color[] COLORS = {Color.RED, Color.BLACK, Color.YELLOW.darker(), Color.GREEN.darker()};
    private Font SMALL_COLOR_FONT = new Font("Courier", Font.PLAIN, 12);
    private Font SMALL_NUNBER_FONT = new Font("Courier", Font.PLAIN, 24);
    private Font LARGE_NUNBER_FONT = new Font("Courier", Font.PLAIN, 44);
    private int width = 100, height = 140;
    private Map<Card, Image> cache = new HashMap<>();

    public CardImagesAwt(List<Card> cards) {

        int centerX = width / 2;
        int centerY = height / 2;
        int boxWidth = (int) (width / 1.7);
        int boxStartX = centerX - (boxWidth / 2);
        int boxStartY = centerY - (boxWidth / 2);

        for (Card card : cards) {

            log.info("Drawing card: " + card);
            String number = String.valueOf(card.getNumber());

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.LIGHT_GRAY);
            g.fill3DRect(0, 0, width - 1, height - 1, true);

            if( card.getSuit() != null ) {
                g.setColor(COLORS[card.getSuit().ordinal()]);

                g.setFont(SMALL_NUNBER_FONT);
                FontMetrics fm = g.getFontMetrics(SMALL_NUNBER_FONT);
                g.drawString(number, 5, fm.getHeight());
                int numberWidth = fm.stringWidth(number);

                g.setFont(SMALL_COLOR_FONT);
                fm = g.getFontMetrics(SMALL_COLOR_FONT);
                g.drawString(card.getSuit().name(), 6 + numberWidth, 4 + fm.getHeight());

                g.drawRect(boxStartX, boxStartY, boxWidth, boxWidth);

                g.setFont(LARGE_NUNBER_FONT);
                fm = g.getFontMetrics(LARGE_NUNBER_FONT);
                g.drawString(number, centerX - (fm.stringWidth(number) / 2), centerY + (fm.getHeight() / 3));
            } else {
                g.setColor(Color.BLACK);
                g.setFont(SMALL_NUNBER_FONT);
                FontMetrics fm = g.getFontMetrics(SMALL_NUNBER_FONT);
                g.drawString("R", 5, fm.getHeight());

                int numberWidth = fm.stringWidth("R");

                g.setFont(SMALL_COLOR_FONT);
                fm = g.getFontMetrics(SMALL_COLOR_FONT);
                g.drawString("ook", 6 + numberWidth, 4 + fm.getHeight());

                g.drawRect(boxStartX, boxStartY, boxWidth, boxWidth);
            }

            cache.put(card, image);
        }
    }

    public Image getCard(Card card) {
        return cache.get(card);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
