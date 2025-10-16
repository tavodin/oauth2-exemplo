import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  login() {
    const clientId = 'my-client-spa';
    const redirectUri = 'http://localhost:4200/callback';
    const authServer = 'http://localhost:9000/oauth2/authorize';
    const scope = 'openid profile email';
    const responseType = 'code';

    const url = `${authServer}?response_type=${responseType}` +
      `&client_id=${encodeURIComponent(clientId)}` +
      `&redirect_uri=${encodeURIComponent(redirectUri)}` +
      `&scope=${encodeURIComponent(scope)}`;

    window.location.href = url;
  }
}
