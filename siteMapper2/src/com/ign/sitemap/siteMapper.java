package com.ign.sitemap;
/**@author ntomasino
* can use per_page=max to limit the maximum number of games returned by any v2 api call
* */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
 
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
 
public class siteMapper {
               final static char slash = File.separatorChar;
               final static Date date = new Date();
               final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss-hh:mm", new Locale("en","En"));
               final static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
               final static String todaysDate = yyyyMMdd.format(date);
//            final static String requestUrl = "http://api.ign.com/v2/articles.xml?post_type=cheat&start_date="+todaysDate;
               private static enum platforms {
                              Xbox360, Wii, Playstation3, PC, NintendoDS, iPhone;
               }
               private static final Map<platforms, Integer> platformMap = new HashMap<platforms, Integer>();
			   static {
			       platformMap.put(platforms.Xbox360 , 664955);
			       platformMap.put(platforms.Wii , 679278);
			       platformMap.put(platforms.Playstation3 , 568479);
			       platformMap.put(platforms.PC , 20114);
			       platformMap.put(platforms.NintendoDS , 653161);
			       platformMap.put(platforms.iPhone , 14240024);
			   }
              
               public static void main(String [] args) throws DocumentException, ParseException, IOException{
//                           cheatsByPlatform(platforms.iPhone);
//                           cheatsByPlatform(platforms.NintendoDS);
//                             cheatsByPlatform(platforms.Playstation3);
                           recentNews();
               }

               public static void recentNews() throws DocumentException, ParseException, IOException{
                              URL requestUrl = new URL("http://content-api.ign.com/v1/articles.xml?type=news");
                              String xmlBasePath = "//articles/article";
                              String xmlUrl = "@url";
                              String xmlDate = "@publishDate";
                              File outputDir = new File(slash+"temp2"+slash+"news.xml");
                              xml2sitemap(requestUrl,xmlBasePath, xmlUrl, xmlDate, outputDir);
               }
              
               public static void recentCheats() throws DocumentException, ParseException, IOException{
                              URL requestUrl = new URL("http://content-api.ign.com/v1/cheats.xml?sort=publishDate&max=5");
                              String xmlBasePath = "//cheatSummaries/cheatSummary";
                              String xmlUrl = "@url";
                              String xmlDate = "publishDate";
                              File outputDir = new File(slash+"temp2");
                              xml2sitemap(requestUrl,xmlBasePath, xmlUrl, xmlDate, outputDir);
               }
              
               public static void cheatsByPlatform(platforms console) throws DocumentException, ParseException, IOException{
                              URL requestUrl = new URL("http://content-api.ign.com/v1/cheats.xml?sort=publishDate&max=5&platform="+platformMap.get(console));
                              String xmlBasePath = "//cheatSummaries/cheatSummary";
                              String xmlUrl = "@url";
                              String xmlDate = "publishDate";
                              File outputDir = new File(slash+"temp2"+slash+console+".xml");
                              xml2sitemap(requestUrl,xmlBasePath, xmlUrl, xmlDate, outputDir);
               }

               /**@param requestUrl - an api call
                * @param xmlBasePath - xml tree path root to the nodes to iterate over
                * @param xmlUrl - xml attribute or element name of the url
                * @param xmlDate - xml attribute or element name of the date
                * @param outputDir - where to write the sitemap to
                * @throws DocumentException
                * @throws ParseException
                * @throws MalformedURLException
               */
               public static void xml2sitemap(URL requestUrl, String xmlBasePath,
                       String xmlUrl, String xmlDate, File outfile ) throws DocumentException, ParseException, IOException{
				        if(!outfile.exists()){
				      	  outfile.createNewFile();
				        }
				        //xml writer creation
				        Document outdoc = DocumentFactory.getInstance().createDocument();
				        Element urlset = outdoc.addElement("urlset");
				        urlset.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
				        urlset.addNamespace("xsi:schemaLocation","http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");
				        urlset.addNamespace("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
				        Element urlElem = null;
				        
				        //xml path parser creation
				        SAXReader reader = new SAXReader();
				        Document apidoc = reader.read(requestUrl);
				        Node curnode = null;
				        @SuppressWarnings("unchecked")
				        List <Node> articleObjects = apidoc.selectNodes( xmlBasePath );
				        
				        String articleUrl, publishDate; //for every url in api results...
				        for (Iterator <Node> iter = articleObjects.iterator(); iter.hasNext(); ) {
				      	  curnode = iter.next();
				      	  //xPathParsing
				      	  articleUrl = curnode.selectSingleNode(xmlUrl).getStringValue();
				      	  publishDate = curnode.selectSingleNode(xmlDate).getStringValue();
				      	  
				      	  //xmlDoc writing...
				      	  urlElem = urlset.addElement("url");
				      	  urlElem.addElement("loc").addText(articleUrl);
				      	  urlElem.addElement("lastmod").addText(publishDate);
				        }
				        //write xmlDocument to file
				        FileOutputStream fos = new FileOutputStream(outfile);
						OutputFormat format = OutputFormat.createPrettyPrint();
						XMLWriter writer = new XMLWriter(fos, format);
						writer.write(outdoc);
						writer.flush();
               }

}