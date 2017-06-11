package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
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
       
            boolean loginSuccess = false;
           
            boolean login = false;
           
            switch( hParse.getRequet() ) {
              case "POST":

                if ( hParse.getUrl().equals("/user/create") ){

                  String body = hParse.getBody();

                  log.debug(" * POST : " + body);

                  Map<String, String> params = HttpRequestUtils.parseQueryString(body);

                  User user = new User(
                      params.get("userId"),
                      params.get("password"),
                      params.get("name").replace("+", " "),
                      params.get("email").replace("%40", "@")
                      );

                  log.debug(user.toString());

                  DataBase.addUser(user);
                }
                
                if ( hParse.getUrl().equals("/user/login") ) {
                  
                  String body = hParse.getBody();
                  log.debug(" * POST : " + body);

                  Map<String, String> params = HttpRequestUtils.parseQueryString(body);

                  User found = DataBase.findUserById(params.get("userId"));
                  
                  if (found != null ) {
                      if( params.get("password").equals(found.getPassword()) ) {
                        loginSuccess = true;
                      }else{
                        loginSuccess = false;
                      }
                  }
                }

                break;
                
              case "GET":
                /* Param check */
                if( hParse.getUrl().equals("/user/create")
                    && (hParse.getParams() != null)  ) {

                  Map<String, String> params = HttpRequestUtils.parseQueryString(hParse.getParams());

                  User user = new User(
                      params.get("userId"),
                      params.get("password"),
                      params.get("name").replace("+", " "),
                      params.get("email").replace("%40", "@")
                      );

                  log.debug(user.toString());

                  DataBase.addUser(user);
                }
                else if( hParse.getUrl().equals("/user/list") ){
                  String cookies = hParse.getHeaderItem("Cookie");

                  Map<String,String> cookie= HttpRequestUtils.parseCookies(cookies);

                  String bool = cookie.get("logined");
                  login = Boolean.parseBoolean( bool );

                }

                break;
              default:
                break;
            }

            DataOutputStream dos = new DataOutputStream(out);
          
            if( hParse.getUrl().equals("/user/create")) {
              response302Header(dos, "/index.html",false);
              responseEmptyBody(dos);
              log.debug("response 302");
            }
            else if ( hParse.getUrl().equals("/user/login")) {
              if( loginSuccess ) {
                response302Header(dos, "/index.html", loginSuccess);

              }else{
                response302Header(dos, "/user/login_failed.html", loginSuccess);
              }
              responseEmptyBody(dos);
              log.debug("response 302");
            }
            else if (hParse.getUrl().equals("/user/list")) {
             
              if( login ) {
                byte[] body = "<h1>SUCCESS!<h1>".getBytes();

                response200Header(dos, body.length, hParse.getHeaderItem("Accept"));
                responseBody(dos, body);

              }
              else
              {
                response302Header(dos, "user/login.html", loginSuccess);
                responseEmptyBody(dos);
              }
            }
            else
            {
              Path file_path = Paths.get("./webapp", hParse.getUrl());
              byte[] body = new byte[0];
              body = Files.readAllBytes(file_path);

              response200Header(dos, body.length, hParse.getHeaderItem("Accept"));
              responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos, String location, boolean logged) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + String.valueOf(logged) + "\r\n");
            dos.writeBytes("\r\n");
            log.debug("Set-Cookie: logined=" + String.valueOf(logged) + "\r\n");
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
