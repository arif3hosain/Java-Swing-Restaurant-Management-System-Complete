package com.rms.bill;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemImageLoader {

    private static final int IMG_W = 120;
    private static final int IMG_H = 90;
    private static final String IMAGE_DIR = "src/image/";

    private final Map<String, ImageIcon> cache = new HashMap<>();

    public ImageIcon loadImage(String itemName) {
        String key = itemName.toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);

        ImageIcon icon = tryLoadFromDisk(itemName);
        if (icon == null) icon = generatePlaceholder(itemName);
        cache.put(key, icon);
        return icon;
    }

    private ImageIcon tryLoadFromDisk(String itemName) {
        String fileName = itemName.toLowerCase().replace(" ", "_");
        String[] exts = {".jpg", ".jpeg", ".png"};
        for (String ext : exts) {
            try {
                File f = new File(IMAGE_DIR + fileName + ext);
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        Image scaled = img.getScaledInstance(IMG_W, IMG_H, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                }
            } catch (Exception e) {
                System.err.println("Image load failed for: " + itemName + " - " + e.getMessage());
            }
        }
        return null;
    }

    private ImageIcon generatePlaceholder(String itemName) {
        int hash = Math.abs(itemName.hashCode());
        int r = 100 + (hash % 80);
        int g = 120 + ((hash / 80) % 80);
        int b = 140 + ((hash / 6400) % 80);
        Color color = new Color(r, g, b);

        BufferedImage img = new BufferedImage(IMG_W, IMG_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillRect(0, 0, IMG_W, IMG_H);

        // Draw first letter of item name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        String letter = itemName.trim().isEmpty() ? "?" : String.valueOf(itemName.trim().charAt(0)).toUpperCase();
        FontMetrics fm = g2.getFontMetrics();
        int x = (IMG_W - fm.stringWidth(letter)) / 2;
        int y = (IMG_H - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letter, x, y);
        g2.dispose();
        return new ImageIcon(img);
    }
}
