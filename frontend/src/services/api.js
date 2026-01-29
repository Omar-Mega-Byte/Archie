import axios from 'axios';

const api = axios.create({
  baseURL: '',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken) {
          const response = await axios.post('/api/auth/refresh', { refreshToken });
          const { accessToken } = response.data;

          localStorage.setItem('accessToken', accessToken);
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;

          return api(originalRequest);
        }
      } catch (refreshError) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);

export default api;

// API functions
export const authApi = {
  login: (data) => api.post('/api/auth/login', data),
  register: (data) => api.post('/api/auth/register', data),
  refresh: (refreshToken) => api.post('/api/auth/refresh', { refreshToken }),
  logout: (refreshToken) => api.post('/api/auth/logout', { refreshToken }),
};

export const userApi = {
  getProfile: () => api.get('/api/user/profile'),
  updateProfile: (data) => api.put('/api/user/profile', data),
  changePassword: (data) => api.post('/api/user/change-password', data),
};

export const dashboardApi = {
  getDashboard: () => api.get('/api/dashboard'),
  getStats: () => api.get('/api/dashboard/stats'),
  getAnalytics: () => api.get('/api/dashboard/analytics'),
};

export const generateApi = {
  analyze: (formData) => api.post('/api/generate/analyze', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  getDatabaseTypes: () => api.get('/api/generate/database-types'),
  downloadProject: (projectId) => `/api/generate/download/${projectId}`,
  updateFile: (data) => api.post('/api/generate/update-file', data),
};
