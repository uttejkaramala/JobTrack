import { api } from './api';

export const noteApi = {
  listForApplication: (applicationId) => api.get(`/applications/${applicationId}/notes`),
  create: (applicationId, payload) => api.post(`/applications/${applicationId}/notes`, payload),
  update: (id, payload) => api.put(`/notes/${id}`, payload),
  remove: (id) => api.delete(`/notes/${id}`),
};
