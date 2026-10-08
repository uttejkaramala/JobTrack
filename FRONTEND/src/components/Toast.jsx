import { CheckCircle2, AlertCircle, X } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function Toast() {
  const { toast, dismissToast } = useApp();
  if (!toast) return null;
  const Icon = toast.type === 'error' ? AlertCircle : CheckCircle2;
  return <div className={`toast ${toast.type}`} role="status"><Icon size={19}/><span>{toast.message}</span><button className="icon-button toast-close" onClick={dismissToast} aria-label="Close notification"><X size={16}/></button></div>;
}
