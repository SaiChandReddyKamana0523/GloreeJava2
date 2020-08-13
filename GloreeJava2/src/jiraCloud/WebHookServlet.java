package jiraCloud;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class WebHookServlet extends HttpServlet {

    public static String mkString(Iterable<?> values, String start, String sep, String end){
        // if the array is null or empty return an empty string
        if(values == null || !values.iterator().hasNext())
            return "";

        // move all non-empty values from the original array to a new list (empty is a null, empty or all-whitespace string)
        List<String> nonEmptyVals = new LinkedList<String>();
        for (Object val : values) {
            if(val != null && val.toString().trim().length() > 0){
                nonEmptyVals.add(val.toString());
            }
        }

        // if there are no "non-empty" values return an empty string
        if(nonEmptyVals.size() == 0)
            return "";

        // iterate the non-empty values and concatenate them with the separator, the entire string is surrounded with "start" and "end" parameters
        StringBuilder result = new StringBuilder();
        result.append(start);
        int i = 0;
        for (String val : nonEmptyVals) {
            if(i > 0)
                result.append(sep);
            result.append(val);
            i++;
        }
        result.append(end);

        return result.toString();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        String data = buffer.toString();

        String apiKey = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
        JSONParser parser = new JSONParser();
        try {
            JSONObject parse = (JSONObject)parser.parse(data);
            JSONObject issue = (JSONObject)parse.get("issue");
            String id = (String) issue.get("key");
            JSONObject fields = (JSONObject) issue.get("fields");
            String typeName = ((JSONObject) fields.get("issuetype")).get("name").toString();
            String priority = (String)((JSONObject) fields.get("priority")).get("name");
            String labels = mkString((JSONArray) (fields.get("labels")), "", ",", "");
            String status = (String)((JSONObject) fields.get("status")).get("name");
            JSONObject resolution1 = (JSONObject) fields.get("resolution");
            String resolution = resolution1 == null? null : (String) resolution1.get("name");

            String affectsVersion = fields.get("versions").toString();
            String fixVersion = fields.get("fixVersions").toString();
            String assignee="";
            Object assignee1 = fields.get("assignee");
            if(assignee1 != null) {
                assignee = ((JSONObject) assignee1).get("name").toString();
            }

            String reporter="";
            Object reporter1 = fields.get("reporter");
            if(reporter1 != null) {
                reporter = ((JSONObject) reporter1).get("name").toString();
            }

            String description ="";
            Object descField = fields.get("description");
            if(descField != null) {
                description = descField.toString();
            }
            String project = ((JSONObject)fields.get("project")).get("key").toString();

            String created ="";
            Object createdField = fields.get("created");
            if(createdField != null) {
                created = createdField.toString();
            }
            String updated ="";
            Object updateField = fields.get("updated");
            if(createdField != null) {
                updated = updateField.toString();
            }
            String summary ="";
            Object summaryField = fields.get("summary");
            if(summaryField != null) {
                summary = summaryField.toString();
            }

            System.out.println("id = " + id);
            System.out.println("typeName = " + typeName);
            System.out.println("priority = " + priority);
            System.out.println("labels = " + labels);
            System.out.println("status = " + status);
            System.out.println("resolution = " + resolution);

            System.out.println("affectsVersion = " + affectsVersion);
            System.out.println("fixVersion = " + fixVersion);
            System.out.println("assignee = " + assignee);
            System.out.println("reporter = " + reporter);
            System.out.println("description = " + description);
            System.out.println("project = " + project);
            System.out.println("created = " + created);
            System.out.println("updated = " + updated);
            //This is a little nasty but all we have to work with
            String self = issue.get("self").toString();
            String url = self.substring(0, self.indexOf("/rest")) + "browse/"+id;
            System.out.println("url = " + url);
            //This correspond to the old JTITLE field
            System.out.println("summary = " + summary);
        } catch (ParseException e) {
            throw new ServletException(e);
        }
    }
}
