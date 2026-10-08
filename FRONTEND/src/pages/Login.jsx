import { useState } from 'react';
import { ArrowRight, BriefcaseBusiness, CheckCircle2, Eye, EyeOff, ShieldCheck } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { userApi } from '../services/userApi';

const isDemoMode = import.meta.env.VITE_DEMO_MODE === 'true';

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [show, setShow] = useState(false);
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
        localStorage.setItem('jobtrack_user', JSON.stringify({ name: 'Uttej', email: form.email }));
      } else {
        const { data } = await api.post('/auth/login', form);
        localStorage.setItem('jobtrack_token', data.token);

        try {
          const me = await userApi.me();
          localStorage.setItem('jobtrack_user', JSON.stringify(me.data));
        } catch {
          localStorage.setItem('jobtrack_user', JSON.stringify({ email: form.email }));
        }
      }
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Unable to sign in. Please check your email and password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-visual">
        <div className="auth-visual-copy">
          <span className="eyebrow">YOUR JOB SEARCH, ORGANIZED</span>
          <div className="auth-brand-hero">JobTrack</div>
          <h1>Keep every application moving forward.</h1>
          <p>Track applications, interviews and follow-ups in one calm, focused workspace.</p>
          <div className="auth-points">
            <span><ShieldCheck size={19}/> Your data stays organized</span>
            <span><BriefcaseBusiness size={19}/> One place for every opportunity</span>
            <span><CheckCircle2 size={19}/> Clear next steps when you need them</span>
          </div>
        </div>
      </div>

      <div className="auth-form-side">
        <form className="auth-form" onSubmit={submit}>
          <div className="mobile-brand">JobTrack</div>
          <div className="auth-heading">
            <h2>Welcome back</h2>
            <p>Sign in to continue managing your job search.</p>
          </div>

          <label>Email
            <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" autoComplete="email" required />
          </label>

          <label>Password
            <div className="password-wrap">
              <input type={show ? 'text' : 'password'} value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Enter your password" autoComplete="current-password" required />
              <button type="button" className="password-toggle" onClick={() => setShow(!show)} aria-label={show ? 'Hide password' : 'Show password'}>
                {show ? <EyeOff size={18}/> : <Eye size={18}/>} 
              </button>
            </div>
          </label>

          {error && <div className="form-error">{error}</div>}

          <button className="primary-button full" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'} {!loading && <ArrowRight size={18}/>} 
          </button>

          <p className="auth-switch">Don't have an account? <Link to="/register">Create account</Link></p>
          {isDemoMode && <p className="demo-note">Demo mode is enabled.</p>}
        </form>
      </div>
    </div>
  );
}
