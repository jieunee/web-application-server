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
  String version;
  
  private InputStream is;
  Map<String,String> header = new HashMap<String,String>();
 
  public HeaderParser( InputStream is ){
    this.is = is;
    
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
         
          isFirst = false;
        }
        else {
          String[] tokens = line.split(":");
          if( tokens.length == 2 ) header.put(tokens[0],tokens[1]);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      log.debug(line);
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
