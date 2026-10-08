// import { api } from './api';

// export const applicationApi = {
//   list: (params = {}) => api.get('/applications', { params }),
//   getById: (id) => api.get(`/applications/${id}`),
//   create: (payload) => api.post('/applications', payload),
//   update: (id, payload) => api.put(`/applications/${id}`, payload),
//   remove: (id) => api.delete(`/applications/${id}`),
//   updateStatus: (id, status) => api.patch(`/applications/${id}/status`, null, { params: { status } }),
//   followUps: (type) => api.get('/applications/follow-ups', { params: { type } }),
// };
import { api } from "./api";

export const applicationApi = {
  list: (params = {}) => api.get("/applications", { params }),

  getById: (id) => api.get(`/applications/${id}`),

  create: (payload) => api.post("/applications", payload),

  update: (id, payload) => api.put(`/applications/${id}`, payload),

  remove: (id) => api.delete(`/applications/${id}`),

  updateStatus: (id, status) =>
    api.patch(`/applications/${id}/status`, {
      status: status,
    }),

  followUps: (type) =>
    api.get("/applications/follow-ups", {
      params: { type },
    }),
};
