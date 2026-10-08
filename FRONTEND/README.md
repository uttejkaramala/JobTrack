# JobTrack Frontend

React frontend for the JobTrack Spring Boot application.

## Run locally

Create a `.env` file in the project root:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_DEMO_MODE=false
```

Then:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

## Backend integration

The frontend uses Axios and automatically sends the JWT from `localStorage` as:

```http
Authorization: Bearer <token>
```

The configured backend endpoints are:

- `/api/auth/register`
- `/api/auth/login`
- `/api/users/me`
- `/api/users/me/password`
- `/api/applications`
- `/api/applications/{id}`
- `/api/applications/{applicationId}/interviews`
- `/api/interviews/{id}`
- `/api/applications/{applicationId}/notes`
- `/api/notes/{id}`
- `/api/applications/follow-ups`
- `/api/dashboard/summary`

Demo mode is only enabled when `VITE_DEMO_MODE=true`. Keep it `false` for the real Spring Boot backend.
