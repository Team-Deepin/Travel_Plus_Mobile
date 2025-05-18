package com.example.travelplus.login;

public class KakaoLoginRequest {
    public String kakaoemail;
    public String token;
    public String name;

    public KakaoLoginRequest(String token, String kakaoemail, String name) {
        this.token = token;
        this.kakaoemail = kakaoemail;
        this.name = name;
    }
}
