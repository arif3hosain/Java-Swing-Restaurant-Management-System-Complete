package com.rms.service;

import db.DBConnection;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AppService {

    ResultSet rs;
    PreparedStatement pst;
    DBConnection con = new DBConnection();


    public String[] getFoodTypes() {
        List<String> sizes = new ArrayList();
        try {
            pst = con.mkDataBase().prepareStatement("SELECT name FROM food_size");
            rs = pst.executeQuery();
            while (rs.next()) {
                sizes.add(rs.getString("name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error getting food sizes!");
        }
        return sizes.toArray(new String[0]);
    }
}
