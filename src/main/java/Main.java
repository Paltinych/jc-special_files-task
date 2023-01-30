import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> listCSV = parseCSV(columnMapping, fileName);

        String jsonCSV = listToJson(listCSV);

        writeString(jsonCSV, "data.json");

        List<Employee> listXML = parseXML("data.xml");

        String jsonXML = listToJson(listXML);

        writeString(jsonXML, "data2.json");

        String json = readString("data.json");

        List<Employee> list = jsonToList(json);

        list.forEach(System.out::println);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileCSV) {
        try (CSVReader reader = new CSVReader(new FileReader(fileCSV))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileXML) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileXML);
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (Node.ELEMENT_NODE == nodeList.item(i).getNodeType()) {
                Element element = (Element) nodeList.item(i);
                Employee employee = new Employee();

                NodeList childNodes = element.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNodes.item(j);

                        switch (childElement.getNodeName()) {
                            case "id": {
                                employee.setId(Long.parseLong(childElement.getTextContent()));
                                break;
                            }
                            case "firstName": {
                                employee.setFirstName(childElement.getTextContent());
                                break;
                            }
                            case "lastName": {
                                employee.setLastName(childElement.getTextContent());
                                break;
                            }
                            case "country": {
                                employee.setCountry(childElement.getTextContent());
                                break;
                            }
                            case "age": {
                                employee.setAge(Integer.parseInt(childElement.getTextContent()));
                            }
                        }
                    }
                }
                employees.add(employee);
            }
        }
        return employees;
    }

    public static String readString(String fileJSON) {

        try (BufferedReader br = new BufferedReader(
                new FileReader(fileJSON))) {
            return br.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> employees = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            for (Object jsonObject : jsonArray) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Employee employee = (gson.fromJson(jsonObject.toString(), Employee.class));
                employees.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return employees;
    }
}
