import { useState } from 'react';
import { ArrowRight, CheckCircle2 } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { userApi } from '../services/userApi';

const isDemoMode = import.meta.env.VITE_DEMO_MODE === 'true';

export default function Register() {
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isDemoMode) {
        localStorage.setItem('jobtrack_token', 'demo-token');
        localStorage.setItem('jobtrack_user', JSON.stringify(form));
      } else {
        await api.post('/auth/register', form);
        const { data } = await api.post('/auth/login', { email: form.email, password: form.password });
        localStorage.setItem('jobtrack_token', data.token);
        try {
          const me = await userApi.me();
          localStorage.setItem('jobtrack_user', JSON.stringify(me.data));
        } catch {
          localStorage.setItem('jobtrack_user', JSON.stringify({ name: form.name, email: form.email }));
        }
      }
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to create your account.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-visual register-visual">
        <div className="auth-visual-copy">
          <span className="eyebrow">START WITH CLARITY</span>
          <div className="auth-brand-hero">JobTrack</div>
          <h1>A simpler way to stay on top of every opportunity.</h1>
          <p>Build momentum with one clear view of where every application stands.</p>
          <div className="feature-list">
            <span><CheckCircle2 size={18}/> Application pipeline</span>
            <span><CheckCircle2 size={18}/> Interview planning</span>
            <span><CheckCircle2 size={18}/> Follow-up reminders</span>
          </div>
        </div>
      </div>
      <div className="auth-form-side">
        <form className="auth-form" onSubmit={submit}>
          <div className="mobile-brand">JobTrack</div>
          <div className="auth-heading"><h2>Create your account</h2><p>Set up your workspace in less than a minute.</p></div>
          <label>Full name<input value={form.name} onChange={e=>setForm({...form,name:e.target.value})} placeholder="Your name" required minLength="2" /></label>
          <label>Email<input type="email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})} placeholder="you@example.com" required /></label>
          <label>Password<input type="password" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} placeholder="At least 8 characters" required minLength="8" /></label>
          {error&&<div className="form-error">{error}</div>}
          <button className="primary-button full" disabled={loading}>{loading?'Creating account…':'Create account'} {!loading&&<ArrowRight size={18}/>}</button>
          <p className="auth-switch">Already have an account? <Link to="/login">Sign in</Link></p>
        </form>
      </div>
    </div>
  );
}
