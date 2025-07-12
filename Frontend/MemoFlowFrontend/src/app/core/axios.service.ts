import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';
import { TokenService } from '../auth/token.service';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {
  private axiosInstance: AxiosInstance;

  constructor(
    private tokenService: TokenService,
    @Inject(PLATFORM_ID) private platformId: Object // Serve per capire se siamo in browser o no
  ) {
    // 🔁 Se siamo nel browser, prendiamo l'IP dinamico. Altrimenti fallback a localhost
    const backendHost = isPlatformBrowser(this.platformId)
      ? window.location.hostname
      : 'localhost';

    // ✅ Configurazione base di Axios con IP dinamico
    this.axiosInstance = axios.create({
      baseURL: `http://${backendHost}:8080`,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    // 🔐 Intercettore per aggiungere il token
    this.axiosInstance.interceptors.request.use(
      (config) => {
        const token = this.tokenService.getToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 🔄 Intercettore per gestire risposte (es. 401)
    this.axiosInstance.interceptors.response.use(
      (response) => response,
      (error) => {
        if (isPlatformBrowser(this.platformId) && error.response?.status === 401) {
          this.tokenService.clearToken();
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.get<T>(url, config);
    return response.data;
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.post<T>(url, data, config);
    return response.data;
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.put<T>(url, data, config);
    return response.data;
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.delete<T>(url, config);
    return response.data;
  }

  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await this.post<any>('/auth/login', { email, password });
      if (response && response.token) {
        this.tokenService.saveToken(response.token);
        return true;
      }
      return false;
    } catch (error) {
      return false;
    }
  }
}
