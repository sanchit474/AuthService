import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { AppContext } from './AppContext.jsx';

const AppProvider = ({ children }) => {
  const backendURL = (import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080/api');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);

  const apiBase = backendURL.endsWith('/api') ? backendURL : `${backendURL.replace(/\/$/, '')}/api`;

  const getUserData = async () => {
    try {
      const res = await axios.get(`${apiBase}/profile`, { withCredentials: true });
      setUser(res.data);
      setIsLoggedIn(true);
      return res.data;
    } catch (err) {
      setUser(null);
      setIsLoggedIn(false);
      return null;
    }
  };

  const logout = async () => {
    try {
      await axios.post(`${apiBase}/logout`, {}, { withCredentials: true });
    } catch (err) {
      // ignore errors on logout
    } finally {
      setUser(null);
      setIsLoggedIn(false);
    }
  };

  useEffect(() => {
    // On app load, try to discover existing session
    getUserData();
  }, []);

  return (
    <AppContext.Provider value={{ backendURL, isLoggedIn, setIsLoggedIn, getUserData, user, logout }}>
      {children}
    </AppContext.Provider>
  );
};

export default AppProvider;
