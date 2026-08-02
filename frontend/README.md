# MAPEDU React Frontend

Complete React + TypeScript + Vite frontend for MAPEDU RFID attendance.

## Run

```bash
npm install
npm run dev
```

## Production build

```bash
npm run build
```

Deploy the generated `dist/` directory.

## Backend integration

Default API base is `/api`, so frontend can be hosted with the Spring Boot backend behind one domain.

For a separate backend domain, create `.env`:

`VITE_API_BASE_URL=https://YOUR-BACKEND-DOMAIN/api`

Modules included: Dashboard, Students, Employees, RFID Cards, Devices, Attendance.
