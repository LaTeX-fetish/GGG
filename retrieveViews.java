import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.net.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.json.*;

public class retrieveViews {
	
	static ArrayList<String> idList = new ArrayList<String>();
	static ArrayList<String> url1 = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException, JSONException {
		
		//Import the list of meme IDs
		getIDList();
		
		//idList.size()-1
		for(int count=0; count <= idList.size()-1; count++){
		
			//Retrieve HTML for the meme's page
			String[] html = getQkmemeHTML(makeFirstURL(idList.get(count)));
		
			int viewCount = getViewCount(html);
			System.out.println(idList.get(count));
			System.out.println(viewCount);
			
			String secondaryURL = createSecondaryURL(getSecondaryID(html));
			System.out.println(secondaryURL);
			
			JSONObject json = readJsonFromUrl(secondaryURL);
		    
		    String newJSON = json.get("caps").toString();
		    newJSON = newJSON.substring(1, newJSON.length() - 1);
		    
		    //Retrieves the caption portion from the JSON object
		    JSONArray caps = json.getJSONArray("caps");
		    String text[] = new String[caps.length()];
		    
		    //Gets the text from the caption portion
		    for (int i = 0; i < caps.length(); i++){
		    	JSONObject cap = caps.getJSONObject(i); 
		    	text[i] = cap.getString("txt");
		    	System.out.println(text[i]);
		    }
		    
		    try{
				  // Create captions file and write to it 
				  FileWriter fstream = new FileWriter("CAPTIONS.txt",true);
				  BufferedWriter out = new BufferedWriter(fstream);
				  
				  out.write(idList.get(count));
				  out.write("||");
				  out.write(Integer.toString(viewCount));
				  
				  for(int i=0; i <= caps.length()-1; i++){
					  out.write("||");
					  out.write(text[i]);
				  }
				  
				  //Insert line break for next input
				  out.write("\n");
				  
				  //Close the output stream
				  out.close();
		    }
		    catch (Exception e){
				  System.err.println("Error: " + e.getMessage());
			}
	    
		}
	}
	
	//Imports the ID list into an ArrayList variable
	public static void getIDList(){
		Scanner s = null;
		try {
			s = new Scanner(new File("IDList.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (s.hasNext()){
		    idList.add(s.next());
		}
		s.close();
	}
	
	//Constructs the quickmeme URL
	public static String makeFirstURL(String ID){
		String urlBase="http://www.quickmeme.com/meme/";
		return urlBase.concat(ID).concat("/");
		
	}
	
	//Retrieves the HTML of a quickmeme and puts it into a string
	public static String[] getQkmemeHTML(String url) {
		ArrayList<String> html = new ArrayList<String>();

		try {
			URL u = new URL(url);
			InputStream is = u.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String s = null;

			while ((s = br.readLine()) != null) {
				html.add(s);
			}

			is.close();
		} catch (IOException ioe) {
				System.out.println("YOUR HTML READER IS BAD AND YOU SHOULD FEEL BAD");
		}

		return html.toArray(new String[html.size()]);
	}
	
	//Retrieve the view count from the HTML
	//Search string: <div title="X views,  awesomes,  meh's and  dislikes" id="voting_rating">
	public static int getViewCount(String[] html) {
		for (String s : html) {
			if (!s.contains("<div title=\"")) {
				continue;
			}
			
			Pattern p = Pattern.compile("<div title=\"([^\\s]+)\\sviews,");
			Matcher m = p.matcher(s);
			if (!m.find()) {
				System.out.println("Could not find view count!!!!!!!");
				return 0;
			}

			return Integer.parseInt(m.group(1).replace(",",""));
		}

		return 0;
	}
	
	//Return the secondary ID for an image
	//Search string <img ... data-id="224462048"
	public static String getSecondaryID(String[] html) {
		for (String s : html){
			if (!s.contains("data-id=\"")) {
				continue;
			}
		
			Pattern p = Pattern.compile("data-id=\"([^\\s]+)\" src");
			Matcher m = p.matcher(s);
			if (!m.find()) {
				System.out.println("secondary ID dont real!!");
				return "";
			}
			return m.group(1).toString();
		}
		return "";
	}
	
	//Construct the URL from the secondary ID
	//Format http://www.quickmeme.com/make/caption/#id=X
	public static String createSecondaryURL(String secondaryID) {
		String urlBase="http://www.quickmeme.com/make/get_data/?id=";
		return urlBase.concat(secondaryID);
	}
	
	//Retrieve captions from all text boxes
	//The source URL is http://www.quickmeme.com/make/get_data/?id=X
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }

}
