package com.example.demo;

import com.example.demo.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringReader;
import java.net.URI;

@Slf4j
@RestController
public class ApiRestController {

    @Value("${google.auth.url}")
    private String googleAuthUrl;

    @Value("${google.login.url}")
    private String googleLoginUrl;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.redirect.url}")
    private String googleRedirectUrl;

    @Value("${google.secret}")
    private String googleClientSecret;

    private final UserInfoService userInfoService;

    @Autowired
    public ApiRestController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    // 구글 로그인창 호출
    // http://localhost:8080/login/getGoogleAuthUrl
    @GetMapping(value = "/login/getGoogleAuthUrl")
    public ResponseEntity<?> getGoogleAuthUrl(HttpServletRequest request) throws Exception {
        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUrl +
                "&response_type=code&scope=email%20profile%20openid&access_type=offline";

        log.info("myLog-LoginUrl : {}", googleLoginUrl);
        log.info("myLog-ClientId : {}", googleClientId);
        log.info("myLog-RedirectUrl : {}", googleRedirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    // 구글에서 리다이렉션
    @GetMapping(value = "/login/oauth_google_check")
    public String oauth_google_check(HttpServletRequest request,
                                     @RequestParam(value = "code") String authCode,
                                     HttpServletResponse response) throws Exception {
        GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(googleRedirectUrl)
                .grantType("authorization_code")
                .build();

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<GoogleLoginResponse> apiResponse = restTemplate.postForEntity(googleAuthUrl + "/token", googleOAuthRequest, GoogleLoginResponse.class);
        GoogleLoginResponse googleLoginResponse = apiResponse.getBody();

        log.info("responseBody {}", googleLoginResponse.toString());

        String googleToken = googleLoginResponse.getId_token();

        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo").queryParam("id_token", googleToken).toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);


        JsonObject jsonObject = Json.createReader(new StringReader(resultJson)).readObject();
        String email = jsonObject.getString("email");
        String name = jsonObject.getString("name");
        String picture = jsonObject.getString("picture");

        String snsProvider = "google"; // 구글 로그인이라고 가정
        String tokenInfo = googleToken; // 토큰 정보를 여기에 저장

        userInfoService.saveUserInfo(snsProvider, tokenInfo, email, name, picture);

        return resultJson;



    }



}
