package uz.fp.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Launcher {


    public static void main(String[] args) {
        try {
            new Launcher().jsonToHtml();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void jsonToHtml() throws IOException {
        Document document = Jsoup.parse(new File(getClass().getResource("/template.html").getPath()));
        Object object = new JSONTokener(getClass().getResourceAsStream("/data.json")).nextValue();


        fillHtml(document, object, null, 0);

        FileWriter writer = new FileWriter(new File("out.html"));
        writer.write(document.body().html());
        writer.flush();
        System.out.println(document.body().html());

    }

    public void fillHtml(Element elementNode, Object object, String jsKey, int level) {
        if (object instanceof JSONObject) {

            for (String sKey : ((JSONObject) object).keySet()) {
                Elements elements = elementNode.select(String.format("[fp=%s]", sKey));

                for (Element element : elements) {
                    fillHtml(element, ((JSONObject) object).get(sKey), sKey, level + 1);
                }

            }
        } else if (object instanceof JSONArray) {
            for (Object obj : ((JSONArray) object)) {
                Element element = elementNode.clone();
                element.appendTo(elementNode.parent());
                fillHtml(element, obj, jsKey, level + 1);
            }
            elementNode.remove();

        } else {
            Elements elements = elementNode.select(String.format("[fp=%s]", jsKey));

            for (Element element : elements) {
                if (!element.hasAttr("level")) {
                    element.text(String.valueOf(object));
                } else if (element.attr("level").equals(String.valueOf(level)))
                    element.text(String.valueOf(object));
            }
        }

    }
}
