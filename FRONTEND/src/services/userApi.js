import { api } from './api';

export const userApi = {
  me: () => api.get('/users/me'),
  updateProfile: (payload) => api.put('/users/me', payload),
  changePassword: (payload) => api.patch('/users/me/password', payload),
};
