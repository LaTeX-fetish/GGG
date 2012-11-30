import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class readIO {

	public static void main(String[] args) throws IOException{
		
		//Get the IDs from page 1 to page N
		for(int i=1; i <= 114; i++){
			
			//Creates a url for the nth GGG page on the website
			String test = makePageURL(i);
			System.out.println("Getting URL: ".concat(test));
			
			//Get the HTML of the current meme page
			String[] htmlTest = getMemeTOCHTML(test);
			
			//Get the memeID from the HTML and output it to a text file
			getMemeID(htmlTest);
		}
		
	}
	
	//Retrieve all GGG meme IDs
	//Format of URL is http://www.quickmeme.com/Good-Girl-Gina/popular/#/?upcoming
	public static String makePageURL(int n){
		String urlBase="http://www.quickmeme.com/Good-Girl-Gina/popular/";
		String urlEnd="/?upcoming";
		return urlBase.concat(Integer.toString(n)).concat(urlEnd);
	}
	
	//Gets meme IDs from the HTML
	public static String[] getMemeTOCHTML(String url) {
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
	
	//Pattern of meme ID is "/meme/[ID]/"
	//Gets the list of meme IDs on a page and outputs them to a text file	
	public static void getMemeID(String[] html) {
		for (String s : html){
			
			if (!s.contains("/meme/")) {
				continue;
			}
				
			Pattern p = Pattern.compile("/meme/([^\\s]+)/");
			Matcher m = p.matcher(s);
			if (!m.find()) {
				System.out.println("oh noes, no meme ID found!!");
			}
			else {
				  try{
					  // Create file 
					  FileWriter fstream = new FileWriter("IDList.txt",true);
					  BufferedWriter out = new BufferedWriter(fstream);
					  out.write(m.group(1).toString());
					  out.write("\n");
					  //Close the output stream
					  out.close();
					  }catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage());
					  }
			}
		}
	}
	
}
