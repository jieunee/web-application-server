package webserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HeaderParser;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
          
            HeaderParser hParse = new HeaderParser(in);
          
            if( hParse.getParams() != null ) {

              Map<String, String> params = HttpRequestUtils.parseQueryString(hParse.getParams());
             
              /*
              for( String key : params.keySet() ) {
                log.debug( key + " : " + params.get(key));
                
              }*/
              
              User user = new User(
                  params.get("userId"),
                  params.get("password"),
                  params.get("name").replace("+", " "),
                  params.get("email")
                  );
              
              log.debug(user.toString());
            }

            DataOutputStream dos = new DataOutputStream(out);
          
            if( hParse.getUrl().equals("/user/create")) {
              response302Header(dos, "/index.html");
              responseEmptyBody(dos);
              log.debug("response 302");
            }
            else
            {
              //byte[] body = "Hello YOU!".getBytes();
              //byte[] body = hParse.toString().replaceAll("\n", "<br>").getBytes();
              Path file_path = Paths.get("./webapp", hParse.getUrl());
              byte[] body = new byte[0];
              body = Files.readAllBytes(file_path);

              response200Header(dos, body.length);
              responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseEmptyBody(DataOutputStream dos) {
        try {
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
