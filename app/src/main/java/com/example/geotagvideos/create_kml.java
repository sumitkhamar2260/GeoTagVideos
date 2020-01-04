package com.example.geotagvideos;

import java.util.ArrayList;

public class create_kml {
    public String kml(ArrayList<String> timestamp,ArrayList<Double> latitude,ArrayList<Double> longitude,String video_title){
        String kml_start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "  <Document>\n" +
                "    <name>"+video_title+"</name>\n" +
                "    <Style id=\"paddle\">\n" +
                "      <IconStyle>\n" +
                "        <Icon>\n" +
                "          <href>http://maps.google.com/mapfiles/kml/pal4/icon53.png</href>\n" +
                "        </Icon>\n" +
                "        <hotSpot x=\"0\" y=\".5\" xunits=\"fraction\" yunits=\"fraction\"/>\n" +
                "      </IconStyle>\n" +
                "    </Style>";
        String kml_element = "";
        for(int i=5;i<latitude.size();i++){
            String description = timestamp.get(i).substring(0,19);
            String dateTime[] = description.split("T");
            String date = dateTime[0];
            String time = dateTime[1]+" IST";
            kml_element+= "\n  <Placemark>\n" +
                    "      <description>Date:"+date+"Time:"+time+"</description>\n" +
                    "      <TimeStamp>\n" +
                    "        <when>"+timestamp.get(i)+"</when>\n" +
                    "      </TimeStamp>\n" +
                    "      <styleUrl>#paddle</styleUrl>\n" +
                    "      <Point>\n" +
                    "        <coordinates>"+longitude.get(i)+","+latitude.get(i)+","+"0</coordinates>\n" +
                    "      </Point>\n" +
                    "    </Placemark>";
        }
        String kml_end = "\n</Document>\n" +
                "</kml>";
        String kml_file = kml_start+kml_element+kml_end;
        return kml_file;
    }

}
