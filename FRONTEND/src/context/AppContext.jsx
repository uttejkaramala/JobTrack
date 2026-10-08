import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import Toast from '../components/Toast';

const AppContext = createContext(null);

export function AppProvider({ children }) {
  const [toast, setToast] = useState(null);

  const dismissToast = useCallback(() => setToast(null), []);

  const notify = useCallback((message, type = 'success') => {
    setToast({ message, type });
    window.clearTimeout(notify.timer);
    notify.timer = window.setTimeout(() => setToast(null), 2800);
  }, []);

  const value = useMemo(() => ({ toast, notify, dismissToast }), [toast, notify, dismissToast]);

  return <AppContext.Provider value={value}>{children}<Toast /></AppContext.Provider>;
}

export function useApp() { return useContext(AppContext); }
