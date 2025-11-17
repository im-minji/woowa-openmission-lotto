# 우아한 테크코스 오픈 미션 

### 1. 프로젝트 설명

본 프로젝트는 우아한테크코스 4주차 오픈미션입니다. 
3주차에 Java 콘솔 애플리케이션으로 구현했던 '로또 미션'을 'Spring Boot 웹 API'로 확장하고, 
사용자가 실제 로또 번호를 기록하고 관리할 수 있는 웹 페이지를 구현하는 것을 목표로 합니다.

백엔드는 Spring Boot를 사용하여 RESTful API를 구축하고, 프론트엔드는 순수 HTML, CSS, JavaScript(Fetch API)를 사용하여 백엔드와 통신합니다.

&nbsp;

### 2. 사용 기술 및 선정 이유

### Backend 

Java 21

Spring Boot: 웹 API 구축, 의존성 관리, 계층형 아키텍처(Controller, Service, Repository) 구현을 위해 사용했습니다.

Spring Data JPA: MyLotto, PurchasedLotto 등 도메인 엔티티의 데이터 영속성을 관리하기 위해 사용했습니다.

H2 Database: 개발 및 테스트 단계에서 별도 설치 없이 사용할 수 있는 인메모리 DB로, 빠른 실행과 검증을 위해 사용했습니다. 
(주의: 애플리케이션을 재실행할 때마다 데이터베이스는 초기화 됩니다.)

Lombok: @Getter, @RequiredArgsConstructor 등을 통해 반복적인 보일러플레이트 코드를 줄이기 위해 사용했습니다.


### Frontend

HTML / CSS (Pure): Spring Boot 백엔드 API 개발에 집중하기 위해, 프론트엔드는 가장 기본적이고 표준적인 기술을 사용했습니다.

JavaScript (Fetch API): 정적 HTML 페이지가 백엔드 API 서버와 '비동기'로 통신(데이터 요청/저장)하기 위해 사용했습니다.

### Test

JUnit 5 & Mockito: LottoService 비즈니스 로직이 의존성(Repository)과 분리되어 정확히 동작하는지 검증하는 단위 테스트를 위해 사용했습니다.

Postman / Web Browser: 구현된 API 엔드포인트(LottoController)가 LottoApplication 실행 시 실제 HTTP 요청에 대해 의도대로 작동하는지 통합 테스트하기 위해 사용했습니다.

&nbsp;

### 3. 실행 및 테스트 방법

1. 본 프로젝트를 로컬 환경에 클론(Clone)합니다.

2. IDE(IntelliJ 등) 또는 터미널을 사용하여 Gradle 프로젝트를 빌드합니다.

3. com.woowa.lotto.LottoApplication의 main 메서드를 실행하여 Spring Boot 서버를 시작합니다.

4. 서버가 정상적으로 켜지면 8080 포트가 활성화됩니다.

5. 웹 브라우저를 열고 http://localhost:8080/ 주소로 접속하여 웹 페이지를 확인할 수 있습니다.

(참고: Spring Boot가 src/main/resources/static 폴더의 index.html을 자동으로 제공합니다.)

(선택) Postman과 같은 API 테스트 도구를 사용하여 http://localhost:8080/im-minji/ 경로의 API 엔드포인트를 직접 테스트할 수 있습니다.

&nbsp;
### 4. 페이지별 기능 설명

모든 페이지는 src/main/resources/static 경로에 HTML 파일로 존재하며, JavaScript fetch를 통해 백엔드 API와 통신합니다.

#### 1. index.html (홈 화면)

- 프로젝트의 개요와 목적을 설명하는 메인 페이지입니다.

#### 2. random-lotto.html (랜덤 로또 생성)

- 랜덤 번호를 생성할 수 있습니다. (재생성도 가능)
- 생성한 랜덤 번호를 이름과 함께 MyLotto(나만의 로또)에 저장할 수 있습니다. 


#### 3. my-lotto.html (나만의 로또 목록)

- 저장된 나만의 로또 목록(테이블)을 조회할 수 있습니다. 
- 목록의 구매 버튼을 통해 각 로또를 구매할 수 있습니다. (구매 시 purchasedLotto로 날짜와 함께 로또 복사본이 이동됩니다.)
- 삭제 버튼을 통해 각 로또를 삭제할 수 있습니다. 

#### 4. my-lotto-add.html (나만의 로또 입력)
- 로또 번호와 이름을 수동으로 입력해 myLotto 목록에 추가할 수 있습니다.


#### 5. purchased-lotto.html (구매 로또 목록)
- 저장된 구매 로또 목록(테이블)을 조회할 수 있습니다.
- 삭제 버튼을 통해 각 로또를 삭제할 수 있습니다.


#### 6. purchased-lotto-add.html (구매 로또 입력)
- 로또 번호와 구매 날짜를 수동으로 입력해 purchasedLotto 목록에 추가할 수 있습니다.

&nbsp;

### 5. 구현한 기능 목록 (API 기준)

#### 1) 로또 (Lotto)

- GET /im-minji/lotto/random: 6개 랜덤 로또 번호 생성 (DB 저장 X, 오름차순 정렬)

&nbsp;

#### 2) 나만의 로또 (MyLotto)

- POST /im-minji/my-lotto: myLotto 1개 저장 (랜덤/수동)



- GET /im-minji/my-lotto: myLotto 전체 목록 조회 (ID 오름차순 정렬)



- DELETE /im-minji/my-lotto/{id}: myLotto 1개 삭제

&nbsp;

#### 3) 구매 로또 (PurchasedLotto)

- POST /im-minji/purchase/manual: purchasedLotto 1개 수동 저장 (날짜 선택 가능)



- POST /im-minji/purchase/from-my-lotto/{id}: myLotto를 purchasedLotto로 복사 저장 (오늘 날짜로 저장)



- GET /im-minji/purchase: purchasedLotto 전체 목록 조회 (구매 날짜 최신순 정렬)



- DELETE /im-minji/purchase/{id}: purchasedLotto 1개 삭제