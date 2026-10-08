import { api } from './api';

export const interviewApi = {
  listForApplication: (applicationId) => api.get(`/applications/${applicationId}/interviews`),
  create: (applicationId, payload) => api.post(`/applications/${applicationId}/interviews`, payload),
  getById: (id) => api.get(`/interviews/${id}`),
  update: (id, payload) => api.put(`/interviews/${id}`, payload),
  remove: (id) => api.delete(`/interviews/${id}`),
  updateStatus: (id, status) => api.patch(`/interviews/${id}/status`, null, { params: { status } }),
};
