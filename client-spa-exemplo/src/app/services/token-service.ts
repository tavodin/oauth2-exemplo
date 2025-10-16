import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  
  private accessToken?: string;
  private idToken?: string;
  private refreshToken?: string;

  setAccessToken(accessToken: string) {
    this.accessToken = accessToken;
  }

  getAcessToken(): string {
    return this.accessToken!;
  }

  setIdToken(idToken: string) {
    this.idToken =  idToken;
  }

  getIdToken(): string {
    return this.idToken!;
  }

  setRefreshToken(refreshToken: string) {
    this.refreshToken =  refreshToken;
  }

  getRefreshToken(): string {
    return this.refreshToken!;
  }
}
