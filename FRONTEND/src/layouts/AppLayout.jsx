import { useState } from 'react';
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { BriefcaseBusiness, CalendarDays, Clock3, Info, LayoutDashboard, LogOut, Menu, Settings, X } from 'lucide-react';

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/applications', label: 'Applications', icon: BriefcaseBusiness },
  { to: '/interviews', label: 'Interviews', icon: CalendarDays },
  { to: '/follow-ups', label: 'Follow-ups', icon: Clock3 },
];

export default function AppLayout() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('jobtrack_user') || '{"name":"Uttej"}');
  const title = location.pathname === '/' ? 'Dashboard' : location.pathname.startsWith('/applications') ? 'Applications' : location.pathname.startsWith('/interviews') ? 'Interviews' : location.pathname.startsWith('/follow-ups') ? 'Follow-ups' : location.pathname.startsWith('/about') ? 'About JobTrack' : 'Settings';

  const logout = () => {
    localStorage.removeItem('jobtrack_token');
    localStorage.removeItem('jobtrack_user');
    navigate('/login');
  };

  return (
    <div className="app-shell">
      <aside className={`sidebar ${mobileOpen ? 'open' : ''}`}>
        <div className="brand-row">
          <Link to="/" className="brand" onClick={() => setMobileOpen(false)}>JobTrack</Link>
          <button className="icon-button mobile-close" onClick={() => setMobileOpen(false)} aria-label="Close menu"><X size={20}/></button>
        </div>
        <nav className="main-nav">
          <p className="nav-label">Workspace</p>
          {navItems.map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} end={to === '/'} className={({isActive}) => `nav-item ${isActive ? 'active' : ''}`} onClick={() => setMobileOpen(false)}>
              <Icon size={19}/><span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <p className="nav-label">Account</p>
          <NavLink to="/settings" className={({isActive}) => `nav-item ${isActive ? 'active' : ''}`} onClick={() => setMobileOpen(false)}><Settings size={19}/><span>Settings</span></NavLink>
          <NavLink to="/about" className={({isActive}) => `nav-item ${isActive ? 'active' : ''}`} onClick={() => setMobileOpen(false)}><Info size={19}/><span>About JobTrack</span></NavLink>
          <button className="profile-card" onClick={() => navigate('/settings')}>
            <span className="avatar">{(user.name || 'U').charAt(0).toUpperCase()}</span>
            <span className="profile-copy"><strong>{user.name || 'Uttej'}</strong><small>My profile</small></span>
          </button>
          <button className="logout-button" onClick={logout}><LogOut size={18}/><span>Log out</span></button>
        </div>
      </aside>
      {mobileOpen && <div className="sidebar-overlay" onClick={() => setMobileOpen(false)} />}
      <main className="main-area">
        <header className="topbar">
          <button className="icon-button mobile-menu" onClick={() => setMobileOpen(true)} aria-label="Open menu"><Menu size={21}/></button>
          <div className="topbar-title"><span className="topbar-context">JobTrack</span><span className="topbar-separator">/</span><span>{title}</span></div>
          <div className="topbar-actions"><button className="topbar-avatar" onClick={() => navigate('/settings')} aria-label="Open profile">{(user.name || 'U').charAt(0).toUpperCase()}</button></div>
        </header>
        <div className="page-content"><Outlet /></div>
      </main>
    </div>
  );
}
