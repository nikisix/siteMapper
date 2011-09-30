package com.ign.sitemap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

public class Sandbox {

	//TODO implement logging

	//VIDEO -- waiting for v3 api
	//TODO Recent Videos       As Published
	 
	//TODO All Videos for Month (Current Year)              Daily
	 
	//TODO All Videos for Year (Previous Years)               Daily
	 
	//TODO All Videos for Series            Daily
	 
	//TODO All Videos for "Channel"     Daily

	//NEWS  -- need to use v1 api. thus can't filter by month or year via api. could write something though...
	//Recent Articles      As Published
	 
	//TODO All Articles for Month (Current Year)             Daily
	 
	//TODO All Articles for Year (Previous Years)             Daily
	 
	//TODO All Articles for "Channel"   Daily

	//CHEATS
	//Recent Cheats    As Published
	 
	//All Cheats for Platform    Daily

	//WIKI -- api doesn't exist yet. ~end of month according to Steve
	//TODO Recent Wiki Pages               As Published
	 
	//TODO All Wiki Home Pages           Daily
	 
	//TODO All Pages for All Wikis (how group?)              Daily

	//http://content-api.ign.com/v1/articles
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	
	
		Document doc = DocumentFactory.getInstance().createDocument();
//		Element root = doc.addElement("myroot").addAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
		Element root = doc.addElement("myroot");
		Namespace ns = new Namespace("xmlns:", "http://www.sitemaps.org/schemas/sitemap/0.9");
//		root.addNamespace("", "http://www.sitemaps.org/schemas/sitemap/0.9");
		root.add(ns);
		root.addAttribute("xmlns:xsi", "http://www.sitemaps.org/schemas/sitemap/0.9");
//		root.addAttribute("myattr", "myval");
		Element elem = root.addElement("myelem");
		elem.addText("my text");
//		In this code you see how to create elements with child elements, how to add attributes and text data to elements. Once we have an in memory representation of the XML file, we can serialize it to the file system like this:
		
		FileOutputStream fos = new FileOutputStream("/temp2/simple.xml");
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(fos, format);
		writer.write(doc);
		writer.flush();
	}
	
    
	final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss-hh:mm", new Locale("en","En"));
    public static void xml2sitemapOLD(URL requestUrl, String baseUrl, String xmlBasePath,
                                  String xmlUrl, String xmlDate, File outputDir ) throws DocumentException, MalformedURLException, ParseException{
                   if(!outputDir.exists()){
                 	  outputDir.mkdir();
                   }
                   WebSitemapGenerator wsg = new WebSitemapGenerator(baseUrl, outputDir);
                   WebSitemapUrl sitemapUrl = null;
                   // this will configure the URL with lastmod=now, priority=1.0, changefreq=hourly
                  
                   SAXReader reader = new SAXReader();
                   Document doc = reader.read(requestUrl);
                   Node curnode = null;
                   @SuppressWarnings("unchecked")
                   List <Node> cheatSummarys = doc.selectNodes( xmlBasePath );
                   String cheatUrl, publishDate;
                   for (Iterator <Node> iter = cheatSummarys.iterator(); iter.hasNext(); ) {
                 	  curnode = iter.next();
                 	  cheatUrl = curnode.selectSingleNode(xmlUrl).getStringValue();
                 	  publishDate = curnode.selectSingleNode(xmlDate).getStringValue();
                 	  sitemapUrl = new WebSitemapUrl.Options(cheatUrl).lastMod(formatter.parse(publishDate)).build();
                 	  wsg.addUrl(sitemapUrl);
                   }                                       
                   wsg.write();
    }
    
//All this code will produce the following XML document:
//
//<?xml version="1.0" encoding="UTF-8"?>
//
//<myroot myattr="myval">
//<myelem>my text</myelem>
//</myroot>
}
