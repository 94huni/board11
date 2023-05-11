# board project
- 간단한 게시판 서비스입니다.
- 서비스를 사용하여 사용자는 게시글 작성, 정보보기, 수정, 삭제 할 수 있습니다.


 ## 기술 스택

### - Backend : 
 - Java 11 : 사용된 버전
 - Spring Boot : 자바 기반의 웹 애플리케이션 개발을 위한 프레임워크
 - Spring Data JPA : 스프링에서 JPA(Java Persistence API)를 편리하게 사용할 수 있는 모듈



### - 인증과 보안 :
 - Spring Security : 스프링기반의 인증과 인과를 제공하는 보안프레임워크
 - JWT : JSON Web Token 의 약자, 웹 토큰기술 중 한개로 인증과 인가정보를 안전하게 사용할 수 있다


### - API 관리 : 
 - Swagger : RESTful API 문서화를 자동화해주는 도구


### - Database : 
 - MariaDB : MySQL 데이터베이스의 포크버전, 오픈소스 관계형 데이터베이스 관리시스템


## 유저 기능 설명

### 로그인
- **요청**
  - Request Method: `POST`
  - Request Body: [클릭하여 확인](board/src/docs/Request/User/signin.json)
  - URL: `/user/sign-in`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/Response/User/signin.json)
  - Success Response Status Code: `200 OK`
  - Response Format: JSON
- **오류 처리**
  - [로그인 실패](board/src/docs/Error/401_username.json)
  - Response Format: JSON

### 회원가입
- **요청**
  - Request Method: `POST`
  - Request Body: [클릭하여 확인](board/src/docs/Request/User/signup.json)
  - URL: `/user/sign-up`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/Response/User/signup.json)
  - Success Response Status Code: `201 Created`
  - Response Format: JSON
- **오류처리**
  - [중복된 아이디가 있을때 응답](board/src/docs/Error/409.json)
  - Response Format: JSON
  - [비밀번호가 다를때 응답](board/src/docs/Error/400.json)
  - Response Format: JSON

### 회원 정보 조회
- **요청**
  - Request Method: `GET`
  - URL: `/user/get/{user_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/Response/User/getUser.json)
  - Success Response Status Code: `200 OK`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs/Error/401.json)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs/Error/404_user.json)
  - Response Format: JSON

### 회원 정보 수정 
- **요청**
  - Request Method: `PUT`
  - Request Body: [클릭하여 확인](board/src/docs/Request/User/updateUser.json)
  - URL: `/user/update`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/Response/User/updateUser.json)
  - Success Response Status Code: `200 OK`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/Error/401.json)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs/Error/401_authority.json)
  - Response Format: JSON
  - [비밀번호가 다를때 응답](board/src/docs/Error/400.json)
  - Response Format: JSON

### 회원 정보 삭제
- **요청**
  - Request Method: `DELETE`
  - URL: `/user/delete/{user_id}`
- **응답**
  - Success Response Status Code: `204 No Content`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs/401.json)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs/Error/401_authority.json)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs/Error/404.json)
  - Response Format: JSON



## 게시판 기능 설명

### 게시글 작성
- **요청**
  - Request Method: `POST`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/user/delete/{user_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `201 Create`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON
  



### 게시글 정보 조회

- **요청**
  - Request Method: `GET`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/board/get/{board_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 ok`
  - Response Format: JSON
- **오류처리**
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON


### 회원의 게시글 목록 조회

- **요청**
  - Request Method: `GET`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/board/get/{username}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 ok`
  - Response Format: JSON
- **오류처리**
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON


## 카테고리별 목록 조회

- **요청**
  - Request Method: `GET`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/board/get/{category}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 ok`
  - Response Format: JSON
- **오류처리**
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON


### 게시글 수정

- **요청**
  - Request Method: `PUT`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/board/update/{board_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 OK`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패시 응답](board/src/docs)
  - Response Format: JSON



## 게시글 삭제
- **요청**
  - Request Method: `DELETE`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/board/update/{board_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `204 No Content`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON



## 댓글 기능 설명
### 댓글 등록
- **요청**
  - Request Method: `POST`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/user/delete/{user_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `201 Create`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON


## 게시글의 댓글 조회

- **요청**
  - Request Method: `GET`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/comment/board/{board_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 ok`
  - Response Format: JSON
- **오류처리**
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON


## 회원의  댓글 조회

- **요청**
  - Request Method: `GET`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/comment/user/{user_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 ok`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON

## 댓글 수정

- **요청**
  - Request Method: `PUT`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/comment/update/{comment_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `200 OK`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패시 응답](board/src/docs)
  - Response Format: JSON


## 댓글 삭제

- **요청**
  - Request Method: `DELETE`
  - Request Body: [클릭하여 확인](board/src/docs)
  - URL: `/board/delete/{comment_id}`
- **응답**
  - Response Body: [클릭하여 확인](board/src/docs/)
  - Success Response Status Code: `204 No Content`
  - Response Format: JSON
- **오류처리**
  - [토큰인증 실패 응답](board/src/docs)
  - Response Format: JSON
  - [사용자의 토큰이 아닐시 응답](board/src/docs)
  - Response Format: JSON
  - [정보조회 실패 응답](board/src/docs)
  - Response Format: JSON
