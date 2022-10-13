package com.clarus12.clarusscanner.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    final static private String URL = "https://clarus12.com/api/auth/login"; // "http:// 퍼블릭 DSN 주소/Login.php";
    private Map<String, String> parameters;

    public LoginRequest(String email, String password, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("email", email);
        parameters.put("password", password);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String>  params = new HashMap<String, String>();
        params.put("User-Agent", "Nintendo Gameboy");
        params.put("Accept-Language", "fr");
        return params;
    }
}