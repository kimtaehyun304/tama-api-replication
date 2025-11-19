<h1>기존 쇼핑몰에 mysql 복제 + 수평 확장 적용</h1>

기존 쇼핑몰: https://github.com/kimtaehyun304/tama-api  
스택: 스프링 부트3, data jpa, mysql 8.0.37
  
버전1-로컬 개발 단계: cqrs로 패키지 분리, slave 로드 밸런싱, 도커로 mysql 복제 환경 구축 ex) master(1) slave(2)   
버전2-수동 수평 확장: aws rds (mysql), route53  
버전3-자동 수평 확장: aws aurora (mysql), rds proxy 

### 버전 업그레이드 과정

자세한 내용은 벨로그에 포스팅했습니다  

<a href="https://velog.io/@hyungman304/%EB%AA%A8%EB%86%80%EB%A6%AC%EC%8A%A4-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-db-%EC%88%98%ED%8F%89-%ED%99%95%EC%9E%A5-1-cqrs-%EB%B0%8F-%EB%B3%B5%EC%A0%9C-%EC%A0%81%EC%9A%A9">
버전1 요약
</a>
 <ul>
  <li>repository 패키지를 command, query로 분리</li>
  <li>yml에 적은 db 정보와 매핑되는 클래스 생성</li>
  <li>트랜잭션 readOnly에 따라 동적으로 db 결정 (LazyConnectionDataSourceProxy)</li>
  <li>slave 라운드 로빈을 위해 동시성을 보장하는 AtomicInteger 사용 (애플리케이션 레벨 라우팅)</li>
</ul>

<a href="https://velog.io/@hyungman304/%EB%AA%A8%EB%86%80%EB%A6%AC%EC%8A%A4-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-db-%EC%88%98%ED%8F%89-%ED%99%95%EC%9E%A5-2-aws-%EC%88%98%EB%8F%99-scale-out">
버전2 요약
</a>
 <ul>
  <li>로컬 db를 aws rds로 교체하고 yml에 반영</li>
  <li>db 스케일 아웃하고 배포를 안 하기위해 route53에서 db 라우팅으로 변경</li>
  <li>AOP로 분산이 잘 되는지 테스트</li>
</ul>

<a href="https://velog.io/@hyungman304/%EB%AA%A8%EB%86%80%EB%A6%AC%EC%8A%A4-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-db-%EC%88%98%ED%8F%89-%ED%99%95%EC%9E%A5-3-aws-%EC%9E%90%EB%8F%99-scale-out">
버전3 요약
</a>
 <ul>
  <li>aws rds는 db 자동 수평 확장을 제공하지 않아서 aurora severless2로 변경</li>
  <li>aurora 트래픽 분산 문제를 보완하기 위해, rds proxy 추가</li>
  <li>보안 그룹 생성 ex) ec2↔rdsProxy, rdsProxy↔rds</li>
  <li>aurora 오토 스케일링 정책 적용</li>
</ul>

