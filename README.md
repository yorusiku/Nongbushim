# 실행방법
npm run build
(babel 폴더내의 js가 public/js/babel 내로 들어가게됩니다.)

# pm2를 이용할 시에 명령어 
npm install -g pm2
pm2 start app.js

# sass 컴파일 방법
node-sass --watch sass/nbs_style.scss --output public/css --output-style compressed


# DB 생성문
CREATE TABLE `farm_user_states` (
  `fid` int NOT NULL,
  `uid` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `job` varchar(8) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `state` int DEFAULT '0',
  PRIMARY KEY (`fid`,`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `farms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `areaPaths` text COLLATE utf8mb4_unicode_ci,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ownerName` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `corpNumber` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `web` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdAt` int DEFAULT '0',
  `postCode` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `addressDetail` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `img1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `img2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `img3` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imgOwner` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imgLogo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `users` (
  `id` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `strategy` int DEFAULT '0',
  `createdAt` int DEFAULT '0',
  `socialId` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


# DB 스키마
농장 테이블 (farms)
다각형 그리기 참고: https://apis.map.kakao.com/web/sample/drawShape/
- id *농장 고유 ID - INT 
- name *농장 이름 - VARCHAR(20)
- postCode 우편번호 - VARCHAR(5)
- address 농장 주소 - VARCHAR(100)
- addressDetail 상세 주소 - VARCHAR(100)
- areaPaths 다각형 영역 좌표 배열 - TEXT
- phone *전화번호 - VARCHAR(20)
- ownerName 농장주인명 - VARCHAR(10)
- corpNumber 법인 번호 - VARCHAR(20)
- web 사이트 주소 - VARCHAR(100)
- img1 - VARCHAR(255)
- img2 - VARCHAR(255)
- img3 - VARCHAR(255)
- imgOwner - 사장님 이미지  - VARCHAR(255)
- imgLogo 로고 이미지 - VARCHAR(255)

농장 소속 테이블 (farm_user_states)
역할은 AgSquared처럼 선택지를 주고 직접 기입 할 수 있도록 함
- fid *농장 고유 ID - INT
- uid*회원 고유 ID - VARCHAR(30)
- job *역할 - VARCHAR(8)
- company 회사 (일용직일떄 회사 기입) - VARCHAR(20)
- state *상태 - (1 / 승인, 0 / 대기, -1 / 거절)

회원 테이블 (users)
- id *회원 고유 ID - VARCHAR(30)
- username *접속 아이디/socialId - VARCHAR(60)
- password 접속 비밀번호 - VARCHAR(255)
- name *이름 -  VARCHAR(10)
- strategy* 가입경로
- createdAt 가입일 - INT
- socialId 소셜ID - VARCHAR(40)

스태프 테이블 (staffs)
- id *스태프 고유 ID - INT
- name *이름 - VARCHAR(10)
- job *역할 - VARCHAR(8)
- address 주소 - VARCHAR(100)
- phone *전화번호 - VARCHAR(20)
- email 이메일 - VARCHAR(100)
- wage 임금 - INT
- wageUnit 임금시간단위 (시급/일급/주급/월급 등 코드로 들어갈것) - VARCHAR(2)

농산물 정보 테이블 (crops)
농산물 관리를 할때 해당 농장이 등록한 농산물 목록에서 선택해서 관리하기 위한 테이블입니다.
- fid *소속 농장 ID - INT
- cid *농산물 ID -  INT
- name *농산물 이름 - VARCHAR(30)
- unit *단위 - VARCHAR(8)
- icon 아이콘 - VARCHAR(255) - 없을경우 custom 사진 업로드

농산물 관리 테이블 (manage_crops)
수확같은거 기록하는 것.
- fid *소속 농장 ID - INT
- cid *농산물 ID -INT
- createdAt *날짜 - INT
- sid 수확한 스태프 ID - INT
- status 상태 (상태 테이블 참조) - VARCHAR(6)
- condition 수확한 작물 상태 (condition, A/B/C로 들어감) - VARCHAR(1)
- unit *단위 - VARCHAR(8)
- unitPrice *단위당 가격 - INT
- saleUnitPrice 할인한 단위당 가격 - INT
- conditionNote 과일 상태 이유 - VARCHAR(100)


상태 테이블 (status_codes)
상태값 목록,  예를들면 1 - 수확완료, 2 - 씻음, 3 - 패키징완료 
과일을 딴 상태, 씻음, 패키징 완료, 창고보관, 출고준비, 배송완료, 판매완료 등등
DB 에서 알아보기 쉽도록 텍스트 코드사용 ex) 배송 완료 - delivered
- status 상태 코드 - VARCHAR(12)
- label 코드 설명 - VARCHAR(20)

회원탈퇴 / 농장 삭제시 관련된 모-든 데이터 제거