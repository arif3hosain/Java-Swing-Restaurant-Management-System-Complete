package com.rms.bill;

import dto.Category;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

public class CategoryTabBar extends JPanel {

    private static final Color COLOR_SELECTED = new Color(37, 99, 235);
    private static final Color COLOR_UNSELECTED = new Color(245, 245, 245);
    private static final Color COLOR_SELECTED_TEXT = Color.WHITE;
    private static final Color COLOR_UNSELECTED_TEXT = new Color(60, 60, 60);

    private final ButtonGroup group = new ButtonGroup();
    private final Consumer<String> onSelect;

    public CategoryTabBar(List<Category> categories, Consumer<String> onSelect) {
        this.onSelect = onSelect;
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        addTab("All Items", "");
        for (Category cat : categories) {
            addTab(cat.name, cat.name);
        }

        // Select "All Items" by default
        Enumeration<AbstractButton> buttons = group.getElements();
        if (buttons.hasMoreElements()) {
            JToggleButton first = (JToggleButton) buttons.nextElement();
            first.setSelected(true);
            styleButton(first, true);
        }
    }

    private void addTab(String label, String categoryValue) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        styleButton(btn, false);

        btn.addActionListener(e -> {
            group.getElements().asIterator().forEachRemaining(b -> styleButton((JToggleButton) b, false));
            styleButton(btn, true);
            onSelect.accept(categoryValue);
        });

        group.add(btn);
        add(btn);
    }

    private void styleButton(JToggleButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(COLOR_SELECTED);
            btn.setForeground(COLOR_SELECTED_TEXT);
            btn.setOpaque(true);
        } else {
            btn.setBackground(COLOR_UNSELECTED);
            btn.setForeground(COLOR_UNSELECTED_TEXT);
            btn.setOpaque(true);
        }
    }
}
