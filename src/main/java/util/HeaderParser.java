package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderParser {
  private static final Logger log = LoggerFactory.getLogger(HeaderParser.class);
  
  String url;
  String request;
  String params;
  String version;
  
  private InputStream is;
  Map<String,String> header = new HashMap<String,String>();
 
  public HeaderParser( InputStream is ){
    this.is = is;
    this.url = null;
    this.request = null;
    this.params = null;
    this.version = null;
    
    readStreams();
  }

  public String getUrl() {
    return url;
  }

  public String getRequet() {
    return request;
  }

  public String getVersion() {
    return version;
  }
  
  public String getParams() {
    return params;
  }
  
  private void readStreams(){
   
    BufferedReader bufRead = new BufferedReader(new InputStreamReader(this.is) );
    
    String line = null;
 
    boolean isFirst = true;
    
    do {
      try {
        line = bufRead.readLine();
        
        if( line == null ) { break; }

        if( isFirst ) {
          // Handle as a Request type
          
          String[] tokens = line.split(" ");
         
          this.request = tokens[0];
          this.url = tokens[1];
          this.version = tokens[2];
       
          /** URL Encoding check */
          int idx = -1; 
          if( (idx = getUrl().indexOf("?")) > 0) {
           
            this.params = this.url.substring(idx+1);
            this.url = this.url.substring(0, idx);
          }
          
          isFirst = false;
          
          log.debug(" request : " + this.request);
          log.debug(" url : " + this.url);
        }
        else {
          String[] tokens = line.split(":");
          if( tokens.length == 2 ) header.put(tokens[0],tokens[1]);

          log.debug(line);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } while( !"".equals(line) );
  
    log.debug("Read done");

  }

 
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    String str = "";
    
    str += "Request : " + getRequet() + "\n";
    str += "URL : " + getUrl() + "\n";
    str += "Version : " + getVersion() + "\n";
  
    for( String key : header.keySet()) {
      str += key + " { " + header.get(key) + " }\n";
    }
    return str;
  }
}
