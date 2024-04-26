package com.rms;

/**
 * Created by: arif hosain
 * Mail: arif@innoweb.co
 * Created at : 06/06/2021 11:42
 */


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Common {

    public static int defaultPageSize = 15;
    public static int DISPLAY_5 = 5;
    public static int DISPLAY_10 = 10;

    //        public static String resourcePath = "/Users/arifhosain/ccl_resources";
//    public static String resourcePath = "/home/arifhosain/ccl_resources";
    public static String resourcePath = "/home/tomcat/ccl_resources";
//    public static String resourcePath = "D:/ccl_resources";


    public static boolean isEmpty(String str) {
        if (str == null) return true;
        if (str.trim().equals("")) return true;
        return str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        if (str == null) return false;
        if(str.trim().equals("")) return false;
        return str.trim().length() > 0;
    }

    public static boolean isNotEmpty(Object obj) {
        return obj != null;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

//    public static boolean isValidEmail(Object email) {
//        boolean result = true;
//        try {
//            InternetAddress emailAddr = new InternetAddress(email.toString());
//            emailAddr.validate();
//        } catch (Exception ex) {
//            result = false;
//        }
//        return result;
//    }

    public static String removeFirstComma(String commaText) {
        for (int i = 0; i < commaText.length(); i++) {
            if (commaText.startsWith(",")) {
                commaText = commaText.substring(1);
            } else return commaText;
        }
        return commaText;
    }

    public static String getQuoteText(String txt) {
        String[] array = txt.split(",");
        StringBuilder data = new StringBuilder();
        for (String anArray : array) {
            data.append(",'").append(anArray).append("'");
        }
        return removeFirstComma(data.toString());
    }

    public static boolean notEmpty(List list) {
        if (list != null) return list.size() > 0;
        return false;
    }

    public static String getInitValue(String value) {
        if (value == null || value.equals("null")) return "";
        if (value.equals("")) return value;
        return value.trim();
    }

    public static String getString(Object value) {
        if (value == null || value.equals("null") || value.equals("")) {
            return "";
        } else return value.toString();
    }

    public static double getCWT(Object gwt, Object vwt) {
        if (gwt != null && vwt != null) {
            Double g = Double.parseDouble(gwt.toString());
            Double v = Double.parseDouble(vwt.toString());
            if (g >= v) return  Double.parseDouble(new DecimalFormat("##.##").format(gwt));
            else return Double.parseDouble(new DecimalFormat("##.##").format(vwt));
        }
        if (gwt == null && vwt != null) {
            return  Double.parseDouble(new DecimalFormat("##.##").format(vwt));
        }
        if (gwt != null && vwt == null) {
            return Double.parseDouble(new DecimalFormat("##.##").format(gwt));
        } else return 0;
    }

    public static String getObj(Object value) {
        if (value == null || value.equals("null") || value.equals("")) return "";
        else return value.toString();
    }

    public static Integer getNumberValue(Object value) {
        if (value == null || value.equals("null") || value.equals("")) return 0;
        else return Integer.parseInt(value.toString());
    }

    public static Double getDoubleValue(Object value) {
        if (value == null || value.equals("null") || value.equals("")) {
            return 0.00;
        } else return  Double.parseDouble(new DecimalFormat("##.##").format(value));
    }



    public static boolean isEmpty(List list) {
        if (list == null) return true;
        return list.size() < 1;
    }

    public static List<Map<String, Object>> mapData(List<Map<String, Object>> list) {
        return list;
    }


    public static Integer countRow(List<Map<String, Object>> data) {
        if (isNotEmpty(data)) {
            Map<String, Object> map = data.get(0);
            return Integer.parseInt(map.get("total").toString());
        } else return 0;
    }

    public static List listData(List list) {
        return list;
    }

    public static boolean isNotEmpty(List list) {
        if (list == null) return false;
        if (list.size() < 1) return false;
        return list.get(0) != null;
    }

    public static java.sql.Date stringToDateWithTime(String sDate) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy HH:mm");
        long dl = fmt.parse(sDate).getTime();
        return new java.sql.Date(dl);
    }

    public static java.sql.Date stringToDate(String sDate)  {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy");
            long dl = fmt.parse(sDate).getTime();
            return new java.sql.Date(dl);
        }catch (ParseException p){
            p.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(date);
    }

    public static String getTokenDate(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(date);
    }

    public static String dateToStrWithTime(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return fmt.format(date);
    }

    public static String dateToStr(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(date);
    }

    public static String today() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(new Date());
    }

    public static String todayDateTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm a");
        return fmt.format(new Date());
    }

    public static String todayFullDateTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        return fmt.format(new Date());
    }

    public static String customized() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd HH-mm-ss");
        return fmt.format(new Date());
    }

    public static String fileDate() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmm");
        return fmt.format(new Date());
    }

    public static String leftPad(String s, int length, char pad) {
        StringBuilder buffer = new StringBuilder(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }

    public static boolean isValidDate(String dstr) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d = fmt.parse(dstr);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    public static java.sql.Date stringToTime(String sDate)
            throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        long dl = fmt.parse(sDate).getTime();
        return new java.sql.Date(dl);
    }

    public static java.sql.Date excelFormattedDate(String sDate)
            throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long dl = fmt.parse(sDate).getTime();
        return new java.sql.Date(dl);
    }

    public static String dateToStringForToken(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("ddMM-yy");
        return fmt.format(date);
    }

    public static String timeToString(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return fmt.format(date);
    }

    public static String getTimeToString(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("hh:mm a");
        return fmt.format(date);
    }


    public static Date excelDate(Object date) {
        if (date != null && date != "") {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return fmt.parse(date.toString());
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }
        return null;
    }


    public static Date xmlDate(Object date) {
        if (date != null && date != "") {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return fmt.parse(date.toString());
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    public static Date excelTime(Object date) {
        if (date != null && date != "") {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                return fmt.parse(date.toString());
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    private static String get24TimeToString(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        return fmt.format(date);
    }

    public static String get24TimeFromObject(Object time) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        return fmt.format(time);
    }

    public static java.sql.Date getTodaysSqlDate() {
        return new java.sql.Date((new Date()).getTime());
    }

    public static Date copyTime(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        cal1.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
        cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
        cal1.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
        cal1.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
        return cal1.getTime();
    }

    public static Date startOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date endOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static String[] getRangeDate(String rangeDate) {
        String range = rangeDate.replace(" ", "");
        return range.split("-");
    }

    public static Date xlsStrToDate(String sDate)
            throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss z yyyy");
        return new Date(fmt.parse(sDate).getTime());
    }

    public static Date validXlsStrToDate(String sDate) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss z yyyy");
            return new Date(fmt.parse(sDate).getTime());
        } catch (ParseException ex) {
            ex.getMessage();
        }
        return null;
    }

    public static Date strToDate(String sDate) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return new Date(fmt.parse(sDate).getTime());
    }

    public static Date convertObjectDateToDate(Object sDate) throws ParseException {
        if (sDate == null) return null;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return new Date(fmt.parse(sDate.toString()).getTime());
    }

    public static Date convertObjectDateToFullDate(Object sDate) throws ParseException {
        if (sDate == null) return null;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return new Date(fmt.parse(sDate.toString()).getTime());
    }


    public static Date validStrToDate(String sDate) {
        if (isEmpty(sDate))
            return null;

        Date date = null;

        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            date = new Date(fmt.parse(sDate).getTime());
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }

        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd MM yy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd MM yyyy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex3) {
                ex3.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat(
                        "dd MM yyyy HH:mm:ss");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex4) {
                ex4.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
                date = new Date(fmt.parse(sDate).getTime());
            } catch (Exception ex5) {
                ex5.printStackTrace();
            }
        }
        if (date == null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yy");
                date = new Date(fmt.parse(sDate.replaceAll("/", "-"))
                        .getTime());
            } catch (Exception ex6) {
                ex6.printStackTrace();
            }
        }

        if (date == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        // log.debug("year="+year);
        if (year < 50)
            year += 2000;
        else if (year < 100)
            year += 1900;

        cal.set(Calendar.YEAR, year);
        return cal.getTime();
    }

    private static Date lastDayOfMonth(int month, int year)
            throws ParseException {

        int day = 0;
        if (month >= 1 && month <= 12) {
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 30;
            } else if (month == 2) {
                if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0))
                    day = 29;
                else
                    day = 28;
            } else {
                day = 31;
            }
        } else {
            throw new ParseException("Invalid Month", month);
        }

        String lastDay = day + "/" + month + "/" + year;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return new Date(fmt.parse(lastDay).getTime());
    }

    public static Date firstDayOfMonth(int month, int year)
            throws ParseException {
        int day = 1;
        String lastDay = day + "/" + month + "/" + year;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return new Date(fmt.parse(lastDay).getTime());
    }

    public static Date validLastDayOfMonth(int month, int year) {
        try {
            return lastDayOfMonth(month, year);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static String dateToNotNullStr(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        return fmt.format(date);
    }

    public static String getMonth(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-yyyy");
        return fmt.format(date);
    }

    public static String dayTextForFileName() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        return fmt.format(new Date());
    }

    public static String todayWithTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return fmt.format(new Date());
    }

    public static String fullDate(Object date) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        return fmt.format(date);
    }

    public static Date getDateFromToday(int d) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, d);
            return new Date(fmt.parse(fmt.format(cal.getTime())).getTime());
        } catch (ParseException ex) {
            ex.getMessage();
        }
        return null;
    }

    public static String lastWeek() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        return fmt.format(cal.getTime());
    }

    public static String lastMonth() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return fmt.format(cal.getTime());
    }

    public static String lastYear() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.YEAR, -1);
        return fmt.format(cal.getTime());
    }

    public static String curRollYear(int i) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.YEAR, i);
        return fmt.format(cal.getTime());
    }

    public static String nextWeek() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        return fmt.format(cal.getTime());
    }

    public static String month(Date day) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MMMMM/yyyy");
        String date = fmt.format(day);
        int index = date.indexOf("/");
        return date.substring(index + 1, date.lastIndexOf("/"));
    }

    public static String dateToStringForShot(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
        return fmt.format(date);
    }

    public static String intToMonthName(int month) {

        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            default:
                return "December";
        }
    }

    public static String intToShortMonthName(int month) {

        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            default:
                return "Dec";
        }
    }

    public static int getDiffDateBetween(Date toDate,
                                         Date fromDate) {
        if (toDate == null || fromDate == null)
            return 0;
        double toMilliSecs = startOfDate(toDate).getTime();
        double fromMilliSecs = startOfDate(fromDate).getTime();

        return (new Double((toMilliSecs - fromMilliSecs) / 86400000))
                .intValue(); // -->> 864000 = 24 * 60 * 60 * 1000
    }

    public static String getDiffDateBetweenTwoDate(Date toDate,
                                                   Date fromDate) {
        if (toDate == null || fromDate == null)
            return "";
        String dur = "";
        long toMilliSecs = startOfDate(toDate).getTime();
        long fromMilliSecs = startOfDate(fromDate).getTime();
        int duration = (int) (toMilliSecs - fromMilliSecs);
        int day = 0;
        int hour = 0;
        int minute = 0;
        if (duration >= (24 * 60 * 60 * 1000)) {
            day = duration / (24 * 60 * 60 * 1000);
            duration = duration % (24 * 60 * 60 * 1000);
        }
        if (duration >= (60 * 60 * 1000)) {
            hour = duration / (24 * 60 * 60);
            duration = duration % (24 * 60 * 60);
        }
        if (duration >= (60 * 1000)) {
            minute = duration / (60 * 1000);
        }
        if (day > 0)
            return "" + day + " Day " + hour + " Hour " + minute + " Min";
        if (hour > 0)
            return "" + hour + " Hour " + minute + " Min";
        if (minute > 0)
            return "" + minute + " Min";
        return "";
    }

    public static Long getHourDifference(Date secondDate, Date firstDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH);
        long diffInMillies = secondDate.getTime() - firstDate.getTime();
        if(diffInMillies <4)   return 0L;
        long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff >= 4) return diff;
        return 0L;
    }

    public static String getHours(Date toDate, Date fromDate) {
        if (toDate == null || fromDate == null)
            return "";

        String dur = "";
        long toMilliSecs = startOfDate(toDate).getTime();
        long fromMilliSecs = startOfDate(fromDate).getTime();
        int duration = (int) (toMilliSecs - fromMilliSecs);
        int day = 0;
        int hour = 0;
        int minute = 0;
        if (duration >= (24 * 60 * 60 * 1000)) {
            day = duration / (24 * 60 * 60 * 1000);
            duration = duration % (24 * 60 * 60 * 1000);
        }
        if (duration >= (60 * 60 * 1000)) {
            hour = duration / (24 * 60 * 60);
            duration = duration % (24 * 60 * 60);
        }
        if (duration >= (60 * 1000)) {
            minute = duration / (60 * 1000);
        }
        if (day > 0)
            return "" + day + " Day " + hour + " Hour " + minute + " Min";
        if (hour > 0)
            return "" + hour + " Hour " + minute + " Min";
        if (minute > 0)
            return "" + minute + " Min";
        return "";
    }

    public static Integer getDay(Date toDate, Date fromDate) {
        if (toDate == null || fromDate == null)
            return 0;
        String dur = "";
        long toMilliSecs = startOfDate(toDate).getTime();
        long fromMilliSecs = startOfDate(fromDate).getTime();
        int duration = (int) (toMilliSecs - fromMilliSecs);
        int day = 0;
        int hour = 0;

        if (duration >= (24 * 60 * 60 * 1000)) {
            day = duration / (24 * 60 * 60 * 1000);
            duration = duration % (24 * 60 * 60 * 1000);
        }
        if (duration >= (60 * 60 * 1000)) {
            hour = duration / (24 * 60 * 60);
            duration = duration % (24 * 60 * 60);
        }
        if (hour >= 12) {
            day++;
        }
        return day;
    }

    public static Long getDayFromHours(Object h) {
        Long hours = Math.round(Double.parseDouble(h.toString()));
        if (hours == 0) return hours;
        int j = 24;
        Long day = hours / j;
        Long last = hours % j;

        if (last >= 12) {
            day++;
        }
        return day;
    }

    public static Integer getDayFromHour(Object h) {
        Integer hours = (int) Double.parseDouble(h.toString());
        int j = 24;
        Integer day = hours / j;
        Integer last = hours % j;
        if (last >= 12) {
            day++;
        }
        return day;
    }


    public static String getFullName(String code) {
        if (code.equalsIgnoreCase("efl")) return "Expo Freight Limited";
        if (code.equalsIgnoreCase("cfl")) return "Cross Freight Limited";
        if (code.equalsIgnoreCase("aps")) return "APS Logistics International Limited";
        if (code.equalsIgnoreCase("wac")) return "WAC Logistics Limited";
        if (code.equalsIgnoreCase("bil")) return "BIL Logistics International Limited";
        if (code.equalsIgnoreCase("fol")) return "Freight Options Limited";
        if (code.equalsIgnoreCase("ull")) return "UniWorld Logistics Limited";
        return "";
    }



    public static List<String> getUnitList() {
        List<String> unit = new ArrayList();
        unit.add("EFL");
        unit.add("CFL");
        unit.add("APS");
        unit.add("WAC");
        unit.add("BIL");
        unit.add("FOL");
        unit.add("ULL");
        return unit;
    }

    public static List getPartySuffix() {
        ArrayList suffix = new ArrayList();
        suffix.add("KAM");
        suffix.add("SEA AIR");
        suffix.add("ROAD");
        suffix.add("ZANNIER");
        suffix.add("LC WIKIKI");
        suffix.add("IMPORT");
        suffix.add("IAS");
        suffix.add("COG");
        return suffix;
    }

    public static String getRandomPassword() {
        Random rand = new Random();
        Integer randomPassword = rand.nextInt(1000000000);
        return randomPassword.toString();
    }


    static void convert_to_words(char[] num)
    {
        // Get number of digits
        // in given number
        int len = num.length;

        // Base cases
        if (len == 0)
        {
            return;
        }
        if (len > 4)
        {
            return;
        }

    /* The first string is not used, it is to make
        array indexing simple */
        String[] single_digits = new String[]{ "ZERO", "ONE",
                "TWO", "THREE", "FOUR",
                "FIVE", "SIX", "SEVEN",
                "EIGHT", "NINE"};

    /* The first string is not used, it is to make
        array indexing simple */
        String[] two_digits = new String[]{"", "TEN", "ELEVEN", "TWELVE",
                "THIRTEEN", "FOURTEEN",
                "FIFTEEN", "SIXTEEN", "SEVENTEEN",
                "EIGHTEEN", "NINETEEN"};

        /* The first two string are not used, they are to make array indexing simple*/
        String[] tens_multiple = new String[]{"", "", "TWENTY", "THIRTY", "FORTY",
                "FIFTY","SIXTY", "SEVENTY",
                "EIGHTY", "NINETY"};

        String[] tens_power = new String[] {"HUNDRED", "THOUSAND"};

        /* Used for debugging purpose only */

        /* For single digit number */
        if (len == 1)
        {
            return;
        }

    /* Iterate while num
        is not '\0' */
        int x = 0;
        while (x < num.length)
        {

            /* Code path for first 2 digits */
            if (len >= 3)
            {
                if (num[x]-'0' != 0)
                {
                    // here len can be 3 or 4
                }
                --len;
            }

            /* Code path for last 2 digits */
            else
            {
            /* Need to explicitly handle
            10-19. Sum of the two digits
            is used as index of "two_digits"
            array of strings */
                if (num[x] - '0' == 1)
                {
                    int sum = num[x] - '0' +
                            num[x] - '0';
                    return;
                }

                /* Need to explicitely handle 20 */
                else if (num[x] - '0' == 2 &&
                        num[x + 1] - '0' == 0)
                {
                    return;
                }

            /* Rest of the two digit
            numbers i.e., 21 to 99 */
                else
                {
                    int i = (num[x] - '0');
                    if(i > 0)
                        System.out.print(tens_multiple[i]+" ");
                    else
                        System.out.print("");
                    ++x;
                    if (num[x] - '0' != 0)
                        System.out.println(single_digits[num[x] - '0']);
                }
            }
            ++x;
        }
    }

    public static String addDate(Date currentDate,Integer addDate){
        System.out.println(currentDate);
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, addDate);
        currentDate = c.getTime();
        return dateToNotNullStr(currentDate);
    }

    public static Date getAddDate(Date currentDate,Integer addDate){
        System.out.println(currentDate);
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, addDate);
        currentDate = c.getTime();
        return currentDate;
    }

    public static String numberFormat(float data){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(data);
    }
    public static String decimal3(float data){
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(data);
    }

    public static String getFileExtension(String fileName){
        Integer lastIndex =fileName.lastIndexOf(".");
        Integer length = fileName.length();
        return fileName.substring((lastIndex),(length));
    }


}
