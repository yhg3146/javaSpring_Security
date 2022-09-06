### Secured 정의
#### @Secured({"RUN_AS_PRIMARY","ROLE_USER"})<br/><br/><br/>

### 코드 예제 
```java
  @DisplayName("6. 사용자 user1이 임시로 교장선생님의 권한을 얻어서 시험지를 가져온다.")
    @Test
    void test_6() {
        paperService.setPaper(paper1);
        paperService.setPaper(paper2);
        paperService.setPaper(paper3);

        client = new TestRestTemplate("user1","1111");

        ResponseEntity<List<Paper>> response = client.exchange(
                uri("/paper/allpapers"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Paper>>() {
                });
        assertEquals(3,response.getBody().size());
        assertEquals(200,response.getStatusCodeValue());
    }
```
```java
  @GetMapping("/allpapers")
    public List<Paper> allPapers(@AuthenticationPrincipal User user){
        return paperService.getAllPapers();
    }
```
```java
@Service
public class PaperService implements InitializingBean {


    @Secured({"RUN_AS_PRIMARY","ROLE_USER"})
    public List<Paper> getAllPapers() {
        return paperDB.values().stream().collect(Collectors.toList());
    }
}
```
```java
