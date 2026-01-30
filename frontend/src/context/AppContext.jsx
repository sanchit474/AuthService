import React from 'react';

// Minimal AppContext with safe defaults so pages using it won't crash
export const AppContext = React.createContext({
  // NOTE: backend app runs with context-path `/api` (see backend `application.properties`).
  // Use VITE_BACKEND_URL to override (e.g., VITE_BACKEND_URL="http://localhost:8080/api").
  backendURL: (import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080/api'),
  isLoggedIn: false,
  setIsLoggedIn: () => {},
  getUserData: () => {},
});

export default AppContext;
