import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../../services/token-service';
import { ITokenResponse } from '../../interfaces/token-response.interface';

@Component({
  selector: 'app-callback',
  imports: [],
  templateUrl: './callback.html',
  styleUrl: './callback.scss'
})
export class Callback implements OnInit {

  private activeteRoute: ActivatedRoute = inject(ActivatedRoute);
  private router: Router = inject(Router)
  private httpClient: HttpClient = inject(HttpClient);
  private tokenService: TokenService = inject(TokenService);

  ngOnInit(): void {
    this.activeteRoute.queryParams.subscribe(params => {
      const code = params['code'];
      if(code) {
        this.exchangeCodeForToken(code);
      }
    });
  }

  exchangeCodeForToken(code: string) {
    const clientId = 'my-client-spa';
    const clientSecret = 'my-secret-spa';

    const body = new HttpParams()
      .set('grant_type', 'authorization_code')
      .set('code', code)
      .set('redirect_uri', 'http://localhost:4200/callback')

    const basicAuth = btoa(`${clientId}:${clientSecret}`);

    this.httpClient.post<ITokenResponse>('http://localhost:9000/oauth2/token', body.toString(), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': `Basic ${basicAuth}`
      }
    })
    .subscribe({
      next: (tokenResponse) => {
        console.log(tokenResponse);
        this.tokenService.setAccessToken(tokenResponse.access_token);
        this.tokenService.setIdToken(tokenResponse.id_token!);
        this.tokenService.setRefreshToken(tokenResponse.refresh_token!);
        this.router.navigate([""])
      },
      error: (err) => console.log("Erro ao obter token", err)
    });
  }
}
