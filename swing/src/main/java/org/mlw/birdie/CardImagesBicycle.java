package org.mlw.birdie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CardImagesBicycle implements CardImages {

    private static final Logger log = LoggerFactory.getLogger(CardImagesBicycle.class);

    private int width = 100, height = 150;
    private Map<Card, Image> cache = new HashMap<>();

    public CardImagesBicycle(List<Card> cards) throws IOException {
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("cards/default.properties"));

        for(Card card : cards){
            String key = card.getSuit() == null ? "Rook" : card.getSuit().name().substring(0,1) + card.getNumber();
            String resource = properties.getProperty(key);

            log.info(card + " : "+key+" loading resource: " + resource);
            try {
                BufferedImage image = ImageIO.read(ClassLoader.getSystemResourceAsStream("cards/" + resource));
                width = image.getWidth();
                height = image.getHeight();

                cache.put(card, image);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public Image getCard(Card card) {
        return cache.get(card);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
