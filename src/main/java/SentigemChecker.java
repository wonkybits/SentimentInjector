import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by pstene on 5/4/16.
 */
public class SentigemChecker {

    public static int checkPolarity(String sentence) {

        String APIKEY = "c149c35de7e895fd5647b4aa01ac52dacH7ADNniW9tJ120szwaTC4_h-KBmXp58";
        String SENTIGEMURL = "https://api.sentigem.com/external/get-sentiment";
        String jsonData = "";

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        return hostname.equals("api.sentigem.com");
                    }
                });

        try {

            URL url = new URL(SENTIGEMURL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write("api-key=" + APIKEY + "&text=" + sentence);
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                jsonData = line;
            }
            writer.close();
            reader.close();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();

        try{
            Object obj = parser.parse(jsonData);
            JSONObject jsonObject = (JSONObject) obj;

            String polarity = (String) jsonObject.get("polarity");
            Long status = (Long) jsonObject.get("status");
            if(status.intValue() == 1) {
                if(polarity.equals("positive")) {
                    return 1;
                } else if(polarity.equals("negative")) {
                    return -1;
                }
            }
        }catch(ParseException pe){

            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }

        return 0;
    }
}