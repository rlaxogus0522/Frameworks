# Frameworks

### **개인 범용 프레임워크**
**MVVM clean architecture + Hilt + Coroutine +  Firebase + webview 모듈 + 보안 모듈**


### 사용 기술 및 라이브러리
- Kotlin
- MVVM, Hilt, Coroutines, Kotest
- Clean Architecture


### Clean Architecture
<img src="https://miro.medium.com/max/4800/1*3F7Wg1-TBqkhib7O-hXRIQ.png" width="600" height="400"/>


***Domain 계층 :***    
의존성을 가지고 있지 않은 계층. 비즈니스 로직에 필요한 Data Model 과 UseCase 가 포함되어있는 계층이다.    
Repository Pattern을 사용한다면, DataModel 에 대한 Repository 도 포함된다.


***Data 계층 :***      
Domain 계층에 의존성을 가지고 있는 계층. 말 그대로 Data 들을 control 하는 계층(CRUD)이라고 생각하면 편하다.   
- API 통신과 그 결과로 가져오는 Data Entity. 내부 DB (Room) 과 DAO.   
- 위의 데이터 (서버, 내부) 를 사용하기 위한 Repository 와 그 구현부.   
- Data 계층 데이터(쉽게 생각해서 받아오는 데이터 형태)와 Domain 계층 데이터(쉽게 생각해서 실제로 사용하는 데이터 형태)로 변환해주는 Mapper 클래스.   


***Presentation 계층 :***    
Data 계층과 Domain 계층에 의존성을 가지고 있는 계층.   
사실상 위에 두 계층에 포함되는거 제외하고 다 들어간다고 생각하면 편하다.    
정확히 말하면, 화면과 입력에 대한 처리 등 UI와 직접적으로 관련된 부분을 담당한다.   
- UI(Activity, Fragment)  
- VM(각 화면에 사용될 ViewModel)   
- DI    
- Module     
