package batchJobs.Temp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.oreilly.servlet.Base64Encoder;

    public class testSNAPI {

        // API Called: https://<instance>.service-now.com/api/now/table/incident?sysparm_limit=1

        public static void main(String[] args) {

            try {

                URL url = new URL(
                        "https://ven02634.service-now.com/api/x_tracl_tracecloud/getprojects");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                //String encoding = Base64Encoder.encode ("abel.tuter:abel.tuter");
                
                String userCredentials = "abel.tuter:abel.tuter";
                String basicAuth = "Basic " + new String(Base64Encoder.encode(userCredentials.getBytes()));

                conn.setRequestProperty("Authorization",
                		basicAuth);
                

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }

                conn.disconnect();

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }