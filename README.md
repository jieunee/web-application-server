# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* 

BufferedReader bufRead = new BufferedReader(new InputStreamReader(this.is) );
    에 대해 line = BufferdReader.readline()의 종료조건으로 line이 null인 조건 과, line이 ""빈 문자열인 경우를 모두 생각해야 한다.
    
  나머지 헤더의 경우 현재 Colon을 기준으로 split()으로 사용하였으나, 마지막 빈줄때문에 로직오류가 있는듯 하다.
  현재는 split 결과 length가 2인 경우에만 처리하도록 임시로 처리 해 두었다. :(
  
### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 
로그인하기
Set-Cookie: logined=true 로그인
Set-Cookie: logined=false 로그아웃

Database.addUser()로 회원가입정보 저장

### 요구사항 6 - 사용자 목록 출력

localhost:8080/user/list로 접근시 사용자 목록 출력

로그인되지 않은 상태이면  index.html로 이동

### 요구사항 7 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 