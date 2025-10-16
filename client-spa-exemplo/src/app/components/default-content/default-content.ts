import { Component, inject, OnInit } from '@angular/core';
import { TokenService } from '../../services/token-service';

@Component({
  selector: 'app-default-content',
  imports: [],
  templateUrl: './default-content.html',
  styleUrl: './default-content.scss'
})
export class DefaultContent implements OnInit {

  private tokenService: TokenService = inject(TokenService);

  nome!: string;

  ngOnInit(): void {
    const payload = this.decodeJwt();

    if(payload) {
      this.nome = payload.profile;
    }
  }

  decodeJwt(): any {
    const token = this.tokenService.getIdToken();
    if(token) {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      return JSON.parse(decoded);
    }
  }

  
  
}
