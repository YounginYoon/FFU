# FFU (Friends For U)

<img src="https://user-images.githubusercontent.com/81675254/167162362-d65d5c1f-546f-489d-9c72-5fa32487acf6.png" height="400px" width="500px">

------------

## 팀원 소개
|이름|역할|
|------|---|
|조명재|프로젝트 관리, DB 구축, 회원가입 및 프로필 변환 구현|
|박승규|DB 구축, 지도 API 처리 및 매핑 구현|
|김용현|UI/UX, 채팅 서버 구현, 매칭 유저 관리|
|윤영인|UI/UX, 프로필 변환 서버 통신 구현|

------------

## 시나리오
<img src="https://user-images.githubusercontent.com/81675254/173142022-9a7c5aed-0335-4682-824a-9d3f248eb446.png" height="400px" width="600px">

------------

## 프로젝트 기능
1. 로그인 및 회원가입
    + 로그인은 휴대전화로 인증 가능
    + 성별, 생일을 입력하여 회원가입을 할 수 있음
<img src="https://user-images.githubusercontent.com/81675254/173142021-a4ee1bd0-71b4-4b4f-afd7-752e704627f6.png" height="400px" width="600px">
<img src="https://user-images.githubusercontent.com/81675254/173150069-fc722ee1-cb7d-4c9c-890e-4d541dd8d45a.gif" height="400px" width="200px">


2. 추천
    + 사용자가 선택한 항목(거리, MBTI, 성격, 취미, 흡연 유무)을 기준으로 상대방을 추천
    + 사용자는 추천 리스트에 뜬 상대방의 정보를 보고 Like/Dislike를 상대방에게 보낼 수 있음
<img src="https://user-images.githubusercontent.com/81675254/173142017-83df0e32-35e1-4da8-81d0-d414f9f71c5a.png" height="400px" width="600px">
<img src="https://user-images.githubusercontent.com/81675254/173151305-0b3cb08b-b016-4531-8480-c96e62984875.png" height="400px" width="800px">
<img src="https://user-images.githubusercontent.com/81675254/173150074-4531b743-8b1d-4804-9588-5375eff2d9f9.gif" height="400px" width="200px">


3. 매칭
    + 나에게 Like를 보낸 상대방의 리스트가 뜸
    + 상대방에게 LIKE 를 보낼 경우 : 서로 매칭이 되면서 채팅방이 형성되며 1:1 채팅이 가능
    + 상대방에게 DISLIKE 를 보낸 경우 : 더이상 매칭이 될 수 없게 됨
<img src="https://user-images.githubusercontent.com/81675254/173151311-982edb5d-d28b-45e2-8fbe-abaccb6c221f.png" height="400px" width="650px">
<img src="https://user-images.githubusercontent.com/81675254/173150055-7c212b0f-3ded-41b0-b8a5-92b66bd97501.gif" height="400px" width="200px">

4. 채팅
    + 서로 Like를 보낸 사용자들끼리 매칭되어 채팅방 생성
    + 채팅 내용이 실시간으로 업데이트
<img src="https://user-images.githubusercontent.com/81675254/173142015-9eb19a0d-f39f-48c0-bda3-b1d0348c7a33.png" height="400px" width="650px">
<img src="https://user-images.githubusercontent.com/81675254/173150082-98d65db6-2764-42f2-b1ab-a201a3e1319f.gif" height="400px" width="200px">

5. 프로필
    + 프로필 편집이 가능하고 히스토리를 볼 수 있음
<img src="https://user-images.githubusercontent.com/81675254/173142020-48973124-962f-4d00-ba20-d1df2109bd09.png" height="400px" width="500px">
<img src="https://user-images.githubusercontent.com/81675254/173150079-716d1a47-128b-4102-b996-75980c74644e.gif" height="400px" width="200px">

------------

### 3/11 
+ 프로젝트 주제 결정

+ 주제 : 코로나 바이러스로 인해 대면 활동이 어려워진 사람들을 위한 친구 추천 APP

+ 프로젝트 설명 : 사용자의 정보(취미, 관심사, 성격, 나이, 활동 지역, ...)와 현재 위치를 기준으로 추천한다.

+ 핵심은 실제 사진을 애니메이션처럼 변환한 사진을 프로필로 설정하는 것이다.

+ 실제 사진이 아닌 애니메이션 사진으로 정함으로써 사용자의 익명성이 보장되며, 사진 도용에 대한 우려가 없다.

------------

### 3/19 
1. 프로젝트의 문제점, 해결방안
    + 사진 도용 문제 해결방안에 대한 논의

2. 개발환경 결정
    + Android App 개발 환경 -> Android Studio
    + 데이터 베이스 -> Firebase
    + 애니메이션화 -> Tensor Flow

3. Github 생성
    + https://github.com/moungJae/FFU

4. 로그인 UI
    + 각자 로그인 UI 구현
------------

### 3/26 
1. 로그인 UI 구현 리뷰
    + 각자 로그인 UI 구현한 것을 수정

2. 개발 순서, 방법
    + UI 만드는 Tool로 화면을 구성하고 구현할 지 회의
    
3. 사용자 데이터베이스
    + 회원 가입 후 어떻게 Firebase 에 업로드할지 회의
------------

### 4/2
1. Firebase에 회원 등록 구현
    
2. Login 코드 리뷰 

3. 사용지 프로필 편집
    + 프로필 편집할 때 필요한 요소가 어떤 것이 있는지 회의
------------

### 4/9
1. 기능 추가 논의
    + 사용자와 사용자를 매칭하는 것을 어떤 기준으로 하면 좋을지 회의

2. 매칭 시나리오 구성
    1. 지도에서 사용자가 클릭하면 사용자가 설정한 거리(최소 5km)내의 사용자와 일정한 기준으로 매칭  
    2. 매칭하여 사용자가 요청을 보내면 요청받은 사용자가 수락하면 대화방 자동으로 생성
------------

### 4/10
1. recommend, matching 사진 변경

2. 회원가입 후 로그인하지 않고 종료 후 다시 접속 시 자동으로 회원가입한 이메일, 비밀번호로 로그인 되는 오류 수정
------------

### 5/4
1. Animation 사진 변환 확인
    + 애니메이션 변환 과정 확인
    + 주변에 빛이 많을 경우 변환된 사진이 약간 뭉개지는 문제 확인

2. Database JSON 구조 설계
    + 회원정보, 위치, 프로필, 채팅 등을 구조화

3. Recommend 방법 결정
    + 머신 러닝을 활용하기 보다 반경 내의 모든 사용자를 추출하여 각 사용자가 원하는 대로 분류하는 기능 추가

4. User request, accept 기능
    + 사용자가 상대방으로부터 요청을 받거나 상대방에게 요청을 할 때 필요한 뷰 구조 논의

5. User recommend 기능
    + 지도 API를 활용하여 사용자 추천을 어떻게 할지 논의
------------

### 5/6
1. Profile 세팅 과정에서 mbti, personality, religion, hobby 버튼을 누르면 dialog로 선택한 data를 가져오도록 수정
    + MbitActivity.kt, PersonalActivity.kt, ReligionActivity.kt, HobbyActivity.kt 제거
    + mbti.xml, personality.xml, religion.xml, hobby.xml 제거

2. 회원가입 시 Realtime Database 에서 users, profile 내에 name 대신에 uid로 교체

3. Profile 세팅 과정에서 나이 입력하는 부분 제거
    + 회원 가입시 year-month-day 입력하는 부분에서 (현재 year) - (사용자 year) + 1 을 나이로 지정

4. Profile 화면에서 현재 로그인 한 정보를 Realtime Database 에서 이름, 소개글 등을 가져와서 동적으로 변경되도록 수정

5. Profile 사진 변경 버튼을 누르면 갤러리에 사진을 가져와서 이를 Firebase Storage 에 photo/user_uid/real.jpg 로 저장
------------

### 5/11
1. Animation-Server 에서 사용자의 request 처리
    + 다른 서비스를 이용하는 것이 아닌 public IP를 이용하고 pc를 서버로 사용
    + server 측에서는 private IP를, client 측에서는 public IP를 사용
------------

### 5/14
1. progress bar 추가
    + 로그인, 프로필 설정 시 나타나도록 하였음

2. 회원 가입 시 회원 정보를 입력한 후에 전화번호 인증 화면으로 이동하도록 변경
    + 전화번호 인증을 하기 위해 이메일, 비밀번호가 등록되어야 하는 issue 
    + 전화번호 인증 화면으로 이동 후 home 버튼을 누르면 현재 유저의 realtime database 에 삽입된 모든 data 및 이메일 계정 제거

3. Profile 설정 시 모든 데이터를 입력한 후 저장 버튼을 누르면 progress bar 가 나타나고 모든 data 가 realtime database 에 저장되면 화면에 빠져나오도록 구현
    + 프로필 사진 변경을 누르고 바로 저장 버튼을 누르면 최대 15 초 정도가 소요
------------

### 5/15
1. git merge 후 NaverMap 에러 문제 처리
    + 몇 개의 파일은 merge 되지 못한 것을 확인하고 이를 수정
    + 정상적으로 map이 띄워지는 것을 확인(zoom-in, zoom-out 및 이동이 가능)
    + background로 현재 사용자의 위치(x, y)를 실시간으로 update 해야함

2. personality, hobby 가 중복으로 들어가는 문제 해결
    + 약간의 로직 변경 및 personality 에 동일한 string 이 있는 문제를 확인 후 수정
------------

### 5/18
1. Phone verification 구현
    + 번호 발송 시 로봇체크가 뜨는 issue 발생
    + 해당 issue는 "Verifying you're not a robot..." 가 뜬 후에 메시지가 전달됨

2. Chatting 구현 완료
    + 매칭된 사용자들의 interactive communication 이 가능
------------

### 5/22
1. email, password 를 입력하여 로그인하는 방식을 phonenumber 인증을 통해 로그인하는 방식으로 변경
    + 총 3가지 패턴으로 화면이 변환됨
        1. 로그인 상태 + 회원가입 상태(모든 정보 기입) => profile 화면 이동 
        2. 로그인 상태 + 회원가입 안한 상태 => 회원가입 화면 이동
        3. 로그아웃 상태 => phonenumber 인증 화면 이동
    + CheckjoinActivity 가 가장 먼저 실행되며 로그인 상태 + join 여부를 판단하여 화면을 이동
    + phonenumber 인증이 완료되면 CheckJoinActivty 로 돌아와서 join 여부에 따라 화면이 이동 

2. 회원가입 화면의 구성은 다음과 같은 순서로 이루어짐
    + 생년월일/성별 입력 화면
    + 프로필 편집 화면

3. Json 구조를 약간 수정
    + 인물 사진인지 아닌지 판단 : Toast 문구 처리
    + server 가 정상적으로 request를 처리 : 처리가 완료될때까지 progressbar 진행
    + 프로필 변경을 마치고 저장할 경우 : 변환 사진에 permission 을 부여하여 사진을 정상적으로 띄움
------------

### 5/24
1. 번호 발송 시 로봇체크가 뜨는 issue 해결
    + Google Cloud Platform 에서 Android Device Verificaiton 을 사용 수락하여 해결

2. 애니메이션 사진으로 변환하고자 할 사진을 선택한 경우 변환 과정이 보여지도록 함
    + 사진 선택 시 해당 사진이 화면에 나타나고 "사진 변환중..." 이라는 문구를 통해 progressbar 가 진행
    + server 측에서 애니메이션 사진으로 변환한 경우 변환된 사진이 나타남
------------

### 5/27
1. Firebase database 에서 data를 가져올 때 버퍼링 문제를 해결
    + UserInformation Class 를 이용하여 해결
    + 로그인 시 UserInformation 객체를 생성함으로써 Listener 등록
        + Firebasse database 내에 존재하는 data들을 companion object의 flag, map, arrayList를 통해 data 접근
    + 로그아웃 후 동일한 번호 or 다른 번호로 로그인할 경우
        + 기존에 존재하던 listener 및 flag, map, arrayList 에 대한 초기화 
        + listener 중첩 시 overhead 가 커지는 issue 방지
        + 다른 번호로 로그인 시 이전에 로그인한 유저의 map 및 arrayList 에 저장된 data가 지속적으로 남게 되는 issue 방지
    
2. 핸드폰 인증 시, 단 한번만 요청이 가능하도록 수정
------------

### 5/28
1. UI Design
    + progress bar, button, background, 글꼴 등 변경

2. Chatting
    + 실시간 채팅시, 화면이 고정되는 문제 해결
    + 애니메이션 사진이 버퍼링 없이 정상적으로 뜨도록 해결

3. Map
    + 특정 지점에서의 유저와의 거리 계산 해결
    + 반경 내에 존재하는 유저들을 listView 에 띄우도록 함

4. UserInformation Class 리모델링
    + Profile, Animation, Recommend 에 대한 data class 를 통해 총 3가지 map의 value를 언급한 클래스 타입으로 할당이 되도록 변경
        + PROFILE : key = userId, value = Profile
        + ANIMATION : key = userId, value = Animation
        + RECOMMEND : key = userId, value = Recommend
------------

### 5/29
1. CheckLoginActivity Class 추가
    + 어플을 삭제하고 설치하여 접근할 때 dummy uid 로 로그인되는 issue 가 존재했음
    + 따라서, 실제로 존재하는 uid 인지를 check 하여 존재하지 않을 경우 Logout 처리하였음

2. Packaging
    + join package
        + CheckLoginActivity : uid 검사
        + JoinActivity : birth, gender 입력
        + PhoneVerificationActivity : phone 인증
        + WelcomeActivity : 첫 화면
    + utils package
        + Animation : animation data class
        + Profile : profile data class
        + Recommend : recommend data class
        + DBKey : db key data class

3. Code refactoring 및 annotation
    + code 간결화 및 code 설명이 필요한 부분에 대해서 annotation 추가 
    + DBKey 를 통해 firebase Realtime database 에 data 접근 처리
    + Map 을 이용해 key-value 형태로 update 하지 않고, 클래스를 객체화하여 update 하는 형태로 변경
------------

### 6/2
1. Profile UI 수정
    + 자신의 소개글이 긴 경우, 사진과 닿는 issue 가 존재했음
    + 따라서 사진, 닉네임, 소개글 등을 각각 가운데 정렬을 하여 해결
    + 프로필 편집 시 MBTI, 성격, 취미 등을 선택할 때 무엇을 선택했는지 확인할 수 있는 문구를 추가

2. Matching 구현
    1. 반경 내에 존재하는 유저에게 LIKE 를 요청한다. (유저의 정보 확인 가능)
    2. 요청을 받은 유저는 해당 유저를 선택하여 정보 확인 및 LIKE 를 수락하여 매칭이 이뤄짐 (LIKE 거절 구현이 필요)

3. History 구현
    + 총 3 가지 케이스로 프로필 화면 아래에 날짜 및 문구가 뜬다.
        1. 상대방에게 LIKE 를 보낸 경우 : "... 님에게 like를 보냈습니다." 문구가 띄워짐
        2. 상대방에게 LIKE 를 받은 경우 : "... 님이 like를 보냈습니다." 문구가 띄워짐
        3. 상대방과 매칭이 이뤄진 경우 : "... 님과 match되었습니다." 문구가 띄워짐

4. UserInformation 수정
    + 로그인된 유저가 로그아웃을 하고 다른 번호로 로그인 시 이전에 존재했던 매칭 유저들이 채팅창에 띄워지는 issue 발생
        + issue 원인 : 이전 유저의 매칭된 유저들과의 리스너가 계속해서 동작한다는 점 + 리스트, 맵에 대한 초기화 처리가 없었음
        + issue 해결 : 이전 정보를 담고 있는 리스트, 맵을 초기화 및 이전 리스너들에 대한 제거 작업을 통해 해결
    + fragment 에서 Profile => ... => Profile 와 같이 방문 시, History 문구가 중첩되는 issue 발생
        + issue 원인 : fragment 에 history 리스너가 제거되지 않아 profile fragment 를 여러번 방문할 때마다 리스너 중첩으로 인해 문구가 중첩되었음
        + issue 해결 : UserInformation 에서 현재 유저의 history 리스너를 생성하여 얻은 history 정보들을 arrayList 에 담았음 fragment 방문시 listener 를 등록하는 것이 아닌 UserInformation 에서 만들어진 arrayList 를 바로 적용함으로써 해결
------------

### 6/3
1. Dialog 모서리 부분을 둥글게 수정

2. Profile Setting UI 수정
    + 4 x 4 테이블 형태의 radiobutton 을 처리하는데 issue 발생
        + issue 원인 : 동일한 row 에 존재하는 radiobutton 들을 하나의 radiogroup 으로 총 4개의 radiogroup 을 생성하였으며 단 하나의 radiobutton 이 선택되도록 처리하는 과정에서 check 값이 제대로 update 가 되지 않았음
        + issue 해결 : radiobutton 들을 무조건 radiogroup 으로 처리한다는 관념을 깨뜨리고, radiobutton 을 선택할 때 리스너가 동작하게끔 하여 이전 행과 열(prevX, prevY)에 check 된 값을 false 로 update 하여 해결
------------

### 6/5
1. 채팅방 나가기 및 DISLIKE 기능 구현
    + 채팅방 나가기 : 특정 유저와 채팅하다가 마음에 안들면 채팅방을 나가고 채팅 목록에 해당 채팅방이 사라지도록 함
    + DISLIKE : 추천 목록에서 특정 유저에게 DISLIKE 를 보내게 될 경우 해당 유저가 추천되지 않도록 함

2. Logout 및 History 수정
    + Logout : 프로필 화면 오른쪽 상단에 설정 버튼을 누르면 activity 로 이동하여 로그아웃 하는 방식이 아니며 다이얼로그를 띄워서 로그아웃을 하도록 변경
    + History : 최신 history 알람이 위로 오도록 변경

3. Recommend 구현 완료
    + 반경 거리를 최소 500m 부터 최대 100km 까지 자유롭게 조절이 가능하며 반경 내의 사용자를 추천
    + 유저가 원하는 mbti, 성격, 취미 등을 선택하고 반경 내의 추천되는 유저들 중에 mbti, 성격, 취미 등이 가장 많이 겹칠 수록 상단에 배치되도록 우선순위를 설정

4. Matching 및 Chatting 이 다이나믹하게 실행되도록 수정
    + 특정 유저에게 LIKE 를 받게 될 경우 매칭 화면에서 즉각적으로 해당 유저가 띄워지도록 변경
    + LIKE 를 받은 유저가 해당 유저를 수락할 경우 채팅 화면에서 즉각적으로 해당 유저가 띄워지도록 변경

5. 여러 유저에게 LIKE 를 받고 매칭할 경우 채팅방에 한명만 뜨는 오류 수정
    + setValue 를 updateChildren 으로 변경하여 해결
        + issue 원인 : setValue 는 값 자체를 변경하는 메서드이므로 가장 마지막으로 매칭된 유저가 뜨게 됨
        + issue 해결 : updateChildren 으로 변경하여 값을 누적하여 업데이트가 가능하도록 함
------------

### 6/7
1. 전체적인 UI 수정
    + 회원가입 화면 : 다이얼로그로 생년월일을 선택하지 않고, 현재 화면 내에서 선택 가능하도록 수정
    + 핸드폰 인증 화면 : 인증 번호가 정상적으로 전송될 경우 제한 시간 텍스트를 추가 (ex : 02분 00초)
    + 프로필 세팅 화면 : 전체적인 UI 수정
    + 추천 화면 : LIKE 는 따봉 이미지, DISLIKE 는 거꾸로 된 따봉 이미지로 대체

2. 애니메이션화 되지 않은 사진을 보호
    + 의도치 않게 애니메이션화 되지 않은 경우 다른 사람들이 해당 사진을 보는 것을 방지하기 위해 permission 키 값을 통해 true 가 아니면 기본 이미지로 대체
        + 서버가 돌아가지 않는 경우에 특정 유저가 실물 사진을 변환하려고 할 때, 해당 실물 사진의 도용 문제가 발생할 수 있음

3. 추천 로직 수정
    + 유저가 추천 버튼을 눌러서 모든 유저들을 LIKE 및 DISLIKE 를 보내고 추천 목록에서 빠져나온 후, 다시 추천 버튼을 누르는 경우 문제 발생
        + issue 원인 : LIKE 및 DISLIKE 를 고려하지 않고 걸러낸 추천 유저들의 map 을 인자로 보낸 후, 파라메터로 받은 LIKE 및 DISLIKE 를 고려하는 로직으로 인해 추천할 사람이 없음에도 리스트가 띄워지는 issue 발생
        + issue 해결 : map 을 인자로 보내기 전에 한번 더 map 을 탐색하여 LIKE, DISLIKE 가 된 경우를 걸러낸 후, 최종적으로 map 을 인자로 보냄 (map 이 빈 경우 토스트로 "추천할 대상이 없습니다." 문구가 뜸)
------------

### 6/9
1. UI 다듬기 작업

2. 매칭 구현 수정
    + 호감화면에서 매칭이 정상적으로 이뤄지고 난 후에 다시 호감화면으로 가면 매칭이 이뤄졌음에도 불구하고 유저가 계속 떠있는 상황
        + issue 원인 : 나에게 온 receivedLike 및 나에게 보낸 상대방의 sendLike 의 value 값을 제거하지 않아서 생긴 문제
        + issue 해결 : 나에게 온 receivedLike 및 나에게 보낸 상대방의 sendLike 의 value 를 삭제하여 해결

3. 다이나믹한 채팅 리스트 구현
    + 채팅 리스트에 다이나믹함을 부여하여 즉각적으로 누구에게 채팅이 오는지, 어느 시간에 보냈는지를 알 수 있도록 기능 구현
    + 상대방이 채팅을 보내면 즉각적으로 채팅 목록에서 가장 마지막으로 보낸 채팅 로그 및 시간대가 나타나도록 기능 구현
    + 상대방이 채팅방을 나가게 될 경우 => "..." 님이 나갔습니다. 문구가 떠서 유저가 채팅방을 나간 것을 확인할 수 있도록 함 
    + 유저가 나간 채팅방에 들어가게 되면 문자를 보낼 수 없으며 오직 채팅방 나가기 및 뒤로가기만 가능하다.
------------

### 6/11 (Final)
1. 추천 로직 변경
    + 성격, 취미 등을 선택하지 않고 mbti 만 선택할 경우, 일치하는 mbti 가 있음에도 불구하고 추천이 되지 않는 issue 발생
    + issue 원인 : mbti ~> personality ~> hobby 순서로 유저들을 intersection 했기 때문
        + mbti = { "ESTJ" }, 성격 = { }, 취미 = { "게임" } 인 경우 { "ESTJ" } => { } => { } 
    + issue 해결 : intersection 방식을 union 방식으로 변경하였음
        + mbti = { "ESTJ" }, 성격 = { }, 취미 = { "게임" } 인 경우 { "ESTJ" } => { "ESTJ" } => { "ESTJ", "게임" }

2. 채팅 로그 변경
    + 채팅 날짜 "12시 9분" 를 "12시 09분" 으로 변경
        + minute < 10 : "0" + minute 으로 처리
    + 현재 날짜를 받아올 때, 오전 4:14 인 경우 오후 4:14 으로 받아오는 issue 발생
        + issue 원인 : System.currentTimeMillis() 를 호출하여 발생된 원인임을 확인
        + issue 해결 : LocalDateTime.now() 를 호출하여 해결
------------
