package dataload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.io.IOUtils;

public class JSONHelper
{

	static Properties prop = new Properties();

	public static void main(String[] args)
	{
		try
		{
			buildQueryJSON("SEARCHTERM");
			//getURLForQuery("iphoto");
		} catch (Exception e)
		{
		}
	}

	public static String buildQueryJSON(String searchString)
	{
	
		String phraseSlopValueStr = "phraseSlopValue";
		//Query DSL to represent the below JSON
		//{"from":0,"size":25,"fields":["docID","url"],"query":{"custom_filters_score":{"query":{"query_string":{"query":"SEARCHTERM","fields":["h1Tag1","description","textContent"],"phraseSlop":"15","useDisMax":true}},"filters":[{"filter":{"query":{"query_string":{"query":"frequently asked questions","fields":["h1Tag1"]}}},"boost":5},{"filter":{"query":{"query_string":{"query":"troubleshooting"}}},"boost":2}],"score_mode":"multiply"}},"rescore":{"window_size":25,"query":{"rescore_query":{"match":{"h1Tag1":{"query":"SEARCHTERM","type":"phrase","slop":"phraseSlopValue"}}},"query_weight":2,"rescore_query_weight":3}}}

		JSONObject json = new JSONObject();

		// Put a simple element
		json.put("from", 0);
		json.put("size", 15);

		JSONArray array = new JSONArray();
		array.add("docID");
		array.add("url");
		json.put("fields", array);
		
		JSONObject custFilters = new JSONObject();
		JSONObject queryFilters = new JSONObject();
		JSONObject subQuery = new JSONObject();
		
		JSONObject query = new JSONObject();
		JSONObject multiMatch = new JSONObject();
		JSONObject temp = new JSONObject();
		JSONArray fldsArray = new JSONArray();
		fldsArray.add("h1Tag1^2.5");
		fldsArray.add("description^3.5");
		fldsArray.add("textContent^5.5");

		temp.put("query", searchString);
		temp.put("fields", fldsArray);
		temp.put("phraseSlop", phraseSlopValueStr);
		temp.put("useDisMax", true);
		
		multiMatch.put("query_string", temp);
		
		JSONArray filtersArray = new JSONArray();

		JSONObject filterVal = new JSONObject();
		JSONObject filter = new JSONObject();	
		JSONObject match = new JSONObject();
		JSONObject qs = new JSONObject();
		qs.put("h1Tag1","frequently asked questions");
		match.put("match",qs);
		filter.put("query",match);
		filterVal.put("filter",filter);
		filterVal.put("boost",5);
		filtersArray.add(filterVal);		
		
		
		queryFilters.put("query",multiMatch);
		queryFilters.put("filters",filtersArray);
		queryFilters.put("score_mode", "multiply");
		custFilters.put("custom_filters_score", queryFilters);

		//Rescore
		JSONObject rescore = new JSONObject();
		rescore.put("window_size",50);
		JSONObject rquery = new JSONObject();
		JSONObject rescoreqry = new JSONObject();
		JSONObject match1 = new JSONObject();
		JSONObject h1tag1 = new JSONObject();
		h1tag1.put("query", searchString);
		h1tag1.put("type", "phrase");
		h1tag1.put("slop",0);
		match1.put("h1Tag1",h1tag1);
		rescoreqry.put("match",match1);
		rquery.put("rescore_query",rescoreqry);
		rquery.put("query_weight",2);
		rquery.put("rescore_query_weight",1);
		rescore.put("query",rquery);
		
		
		json.put("query", custFilters);
		json.put("rescore",rescore);
		System.out.println( json.toString());
		return json.toString();
		
	}
	
	public static String buildQueryJSONWithContentSpotlight(String searchString)
	{
		//Query DSL to represent the below JSON
		//{ "from": 0, "size": 10, "fields": ["docID","url"], "query": { "custom_filters_score": { "query": { "multi_match": { "query": "searchTerm", "fields": [ "subtitle^25", "snippet^35", "anchorLinkTags^30", "h2Tag^20", "content^50", "contentKeywords^60" ], "phraseSlop": "15", "useDisMax": true } }, "filters": [ { "filter": { "term": { "docType": "troubleshooting" } }, "boost": 6 }, { "filter": { "term": { "docType": "how to" } }, "boost": 7 }, { "filter": { "term": { "docType": "video" } }, "boost": 8 }, { "filter": { "term": { "docType": "legacy" } }, "boost": 3 }, { "filter": { "term": { "docType": "specifications" } }, "boost": 5 }, { "filter": { "term": { "docType": "downloads" } }, "boost": 6 }, { "filter": { "term": { "docType": "product" } }, "boost": 6 } ], "score_mode" : "multiply" } } }
		
		
		JSONObject json = new JSONObject();

		// Put a simple element
		json.put("from", 0);
		json.put("size", 30);

		JSONArray array = new JSONArray();
		array.add("docID");
		array.add("url");
		json.put("fields", array);
		
		JSONObject custFilters = new JSONObject();
		JSONObject queryFilters = new JSONObject();
		JSONObject subQuery = new JSONObject();
		
		JSONObject query = new JSONObject();
		JSONObject multiMatch = new JSONObject();
		JSONObject temp = new JSONObject();
		JSONArray fldsArray = new JSONArray();
		fldsArray.add("subtitle^25");
		fldsArray.add("snippet^35");
		fldsArray.add("anchorLinkTags^30");
		fldsArray.add("h2Tag^20");
		fldsArray.add("content^50");
		fldsArray.add("contentKeywords^60");
		temp.put("query", searchString);
		temp.put("fields", fldsArray);
		temp.put("phraseSlop", "15");
		temp.put("useDisMax", true);
		
		multiMatch.put("multi_match", temp);
		
		JSONArray filtersArray = new JSONArray();

		JSONObject filterVal = new JSONObject();
		JSONObject filter = new JSONObject();	
		JSONObject term = new JSONObject();
		term.put("docType","troubleshooting");
		filter.put("term",term);
		filterVal.put("filter",filter);
		filterVal.put("boost",6);
		filtersArray.add(filterVal);		
		
		JSONObject filterVal1 = new JSONObject();
		JSONObject filter1 = new JSONObject();	
		JSONObject term1 = new JSONObject();
		term1.put("docType","howto");
		filter1.put("term",term1);
		filterVal1.put("filter",filter1);
		filterVal1.put("boost",7);
		filtersArray.add(filterVal1);	
		
		JSONObject filterVal2 = new JSONObject();
		JSONObject filter2 = new JSONObject();	
		JSONObject term2 = new JSONObject();
		term2.put("docType","video");
		filter2.put("term",term2);
		filterVal2.put("filter",filter2);
		filterVal2.put("boost",8);
		filtersArray.add(filterVal2);
		
		JSONObject filterVal3 = new JSONObject();
		JSONObject filter3 = new JSONObject();	
		JSONObject term3 = new JSONObject();
		term3.put("docType","legacy");
		filter3.put("term",term3);
		filterVal3.put("filter",filter3);
		filterVal3.put("boost",3);
		filtersArray.add(filterVal3);
		
		JSONObject filterVal4 = new JSONObject();
		JSONObject filter4 = new JSONObject();	
		JSONObject term4 = new JSONObject();
		term4.put("docType","specifications");
		filter4.put("term",term4);
		filterVal4.put("filter",filter4);
		filterVal4.put("boost",5);
		filtersArray.add(filterVal4);
		
		JSONObject filterVal5 = new JSONObject();
		JSONObject filter5 = new JSONObject();	
		JSONObject term5 = new JSONObject();
		term5.put("docType","downloads");
		filter5.put("term",term5);
		filterVal5.put("filter",filter5);
		filterVal5.put("boost",6);
		filtersArray.add(filterVal5);
		
		JSONObject filterVal6 = new JSONObject();
		JSONObject filter6 = new JSONObject();	
		JSONObject term6 = new JSONObject();
		term6.put("docType","product");
		filter6.put("term",term6);
		filterVal6.put("filter",filter6);
		filterVal6.put("boost",6);
		filtersArray.add(filterVal6);
		queryFilters.put("query",multiMatch);
		queryFilters.put("filters",filtersArray);
		queryFilters.put("score_mode", "multiply");
		custFilters.put("custom_filters_score", queryFilters);

		json.put("query", custFilters);
		System.out.println( json.toString());
		return json.toString();
	}

	public static String makeQueryOld(String searchString)
	{
		// {"query":{"multi_match":{"query":"genius bar appointment at apple store",
		// "fields":["title","content","snippet"]}},"from":0,"fields":["docID","url"],"size":10}

		JSONObject json = new JSONObject();

		// Put a simple element
		json.put("from", 0);
		json.put("size", 20);

		JSONArray array = new JSONArray();
		array.add("docID");
		array.add("url");
		json.put("fields", array);

		JSONObject query = new JSONObject();
		JSONObject temp = new JSONObject();
		JSONArray fldsArray = new JSONArray();
		fldsArray.add("title^20");
		fldsArray.add("content");
		fldsArray.add("snippet");

		temp.put("query", searchString);
		temp.put("fields", fldsArray);

		// query.put("query_string", temp);
		query.put("multi_match", temp);
		json.put("query", query);
		System.out.println("Elastic Search json Query to be used for  "
				+ searchString + "  is --> " + json.toString());
		return json.toString();

	}
	
	public static void getURLForQuery(String searchString)
	{
	try {
		//No Need for encoding since we are using custom json
		//searchString = URLEncoder.encode(searchString, "UTF-8");
		
		// Build the query JSON
		String json = buildQueryJSON(searchString);
		json = URLEncoder.encode(json, "UTF-8");

		String baseSearchEngineURL = "http://17.169.50.226:9200/apple_prodsupport_data/crawl/_search?source=";

		String url = baseSearchEngineURL+json;
//		System.out.println("Use this in your browser:");
		System.out.println(url);


	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	}
	}