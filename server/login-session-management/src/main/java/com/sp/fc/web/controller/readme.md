### SessionController 작성
```java
@Controller
public class SessionController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/sessions")
    public String sessions(Model model){
        model.addAttribute("sessionList",
                sessionRegistry.getAllPrincipals().stream().map(p->UserSession.builder()
                .username(((SpUser)p).getUsername())
                        .sessions(sessionRegistry.getAllSessions(p,false).stream().map(s->
                        SessionInfo.builder()
                                .sessionId(s.getSessionId())
                                .time(s.getLastRequest())
                                .build())
                        .collect(Collectors.toList()))
                .build()).collect(Collectors.toList()));
        return "/sessionList";
    }

    @PostMapping("/session/expire")
    public String expireSession(@RequestParam String sessionId){
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
        if(!sessionInformation.isExpired()){
            sessionInformation.expireNow();
        }
        return "redirect:/sessions";
    }

    @GetMapping("/session-expired")
    public String sessionExpired(){
        return "/sessionExpired";
    }

}
```
