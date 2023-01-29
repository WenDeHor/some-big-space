package com.myhome.controllers.geo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class Geo {
    private static String kilometers = "kilometers";
    private static String meters = "meters";
    private static String miles = "miles";
    double lon1 = 0.0;
    double lat1 = 0.0;
    private static List<String> linkedList = new LinkedList<>();

    public static void main(String[] args) throws Exception {

        URL url = new URL("https://www.google.com.ua/maps");
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        String line = null;
//        StringBuilder stringBuilder = new StringBuilder();
//        while ((line = br.readLine()) != null) {
//            stringBuilder.append(line);
//            System.out.println(line);
//        }
        String[] split = br.readLine().toString().split("<meta content=");
        for (String s : split) {
//            System.out.println(i + "++++++" + split[i] + "\n");
            if (s.contains("https://maps.google.com/maps/api/staticmap?center=")) {
                String concat = s.concat("https://maps.google.com/maps/api/staticmap?center=");
                String[] split1 = concat.split("%2C");
                for (int j = 0; j < split1.length; j++) {
//                    System.out.println(split1[j]);
                    String log = split1[0].substring(51);
                    linkedList.add(log);
                    String lat = split1[1].split("&")[0];
                    linkedList.add(lat);
//                    System.out.println("long: " + log + "\n" + "lat: " + lat);

                }
            }
        }

        float distanceBetweenPointsNew = getDistanceBetweenPointsNew(Float.parseFloat(linkedList.get(0)), Float.parseFloat(linkedList.get(1)), Float.parseFloat("55.8680116"), Float.parseFloat("9.8483974"), kilometers);
        double calcBearing = calcBearing(Float.parseFloat(linkedList.get(0)), Float.parseFloat(linkedList.get(1)), Float.parseFloat("55.8680116"), Float.parseFloat("9.8483974"));
        System.out.println(distanceBetweenPointsNew + "     distance");
        System.out.println(calcBearing + "     calcBearing");

        double calcBearing2 = calcBearing(Float.parseFloat(linkedList.get(0)), Float.parseFloat(linkedList.get(1)), Float.parseFloat("50.1429018"), Float.parseFloat("28.7467921"));
        System.out.println(calcBearing2);
    }

    private static float getDistanceBetweenPointsNew(float latitude1, float longitude1, float latitude2, float longitude2, String unit) {
        float theta = longitude1 - longitude2;
        float distance = (float) (60 * 1.1515 * (180 / Math.PI) * Math.acos(
                Math.sin(latitude1 * (Math.PI / 180)) * Math.sin(latitude2 * (Math.PI / 180)) +
                        Math.cos(latitude1 * (Math.PI / 180)) * Math.cos(latitude2 * (Math.PI / 180)) * Math.cos(theta * (Math.PI / 180))
        ));
        if (unit.equals(miles)) {
            return distance;
        } else if (unit.equals(kilometers)) {
            return (float) (distance * 1.609344);
        } else {
            return 0f;
        }
    }

    private static double calcBearing(double lat1, double lon1, double lat2, double lon2) {

        // to convert degrees to radians, multiply by:
        final double rad = Math.toRadians(1.0);
        // to convert radians to degrees:
        final double deg = Math.toDegrees(1.0);

        double GLAT1 = rad * (lat1);
        double GLAT2 = rad * (lat2);
        double GLON1 = rad * (lon1);
        double GLON2 = rad * (lon2);

        // great circle angular separation in radians
        double alpha = Math.acos(Math.sin(GLAT1) * Math.sin(GLAT2) + Math.cos(GLAT1) * Math.cos(GLAT2)
                * Math.cos(GLON1 - GLON2));

        // forward azimuth from point 1 to 2 in radians
        double s2 = rad * (90.0 - lat2);
        double FAZ = Math.asin(Math.sin(s2) * Math.sin(GLON2 - GLON1) / Math.sin(alpha));

        double result = FAZ * deg; // radians to degrees
        if (result < 0.0) result += 360.0; // reset az from -180 to 180 to 0 to 360

        return result;
    }
}
