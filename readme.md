# 🚀 Server-Batch
Second 프로젝트의 배치 처리 전용 마이크로서비스

## 📖 프로젝트 개요
**Server-Batch**는 Second 프로젝트 내에서 배치 작업을 전담하는 마이크로서비스
대용량 데이터 처리, 스케줄링된 작업, 크롤링 등의 배치 업무를 안정적으로 수행

## 🎯 주요 기능
- **스케줄링**: Cron 기반 정기 실행 및 실시간 모니터링

## 🛠️ 기술 스택
- Language/Runtime
    - Java 17
    - Spring Boot 3.2.4
- Framework & Libraries
    - Spring Web, Spring Data JPA, Spring Scheduling
    - OpenFeign
- Database
    - MySQL
- Build/DevOps
    - Gradle
    - Docker

## ⏰ 스케줄링 패턴

### 실시간 수집
- 주식 시세: 1-5분 간격
- 환율: 10-30분 간격
- 암호화폐: 1-10분 간격

### 정기 수집
- 뉴스: 30분-1시간 간격
- 공시 정보: 1-4시간 간격
- 경제 지표: 일 1-2회

### 배치 처리
- 일봉/주봉/월봉 집계: 시장 마감 후
- 과거 데이터 보정: 주말/휴일
- 통계/분석 데이터 생성: 일 1회

---

## 🔧 설정 및 실행

- server-batch 프로젝트는 반드시 "second" 프로젝트 디렉터리 내부에 위치해야 합니다.
- 예: .../second/server-batch

이 규칙을 지키지 않으면 빌드/실행 및 배포 스크립트가 실패하도록 구성될 수 있습니다.

### 사전 요구사항
- **JDK 17** 이상
- **MySQL 8.0** 이상
- **Gradle 8.x**
