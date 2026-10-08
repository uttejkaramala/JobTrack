import { useEffect, useState } from 'react';
import { Check, LockKeyhole, LogOut, UserRound } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { userApi } from '../services/userApi';

export default function Settings(){
  const {notify}=useApp();
  const navigate=useNavigate();
  const [profile,setProfile]=useState({name:'',email:''});
  const [password,setPassword]=useState({currentPassword:'',newPassword:'',confirm:''});
  const [loading,setLoading]=useState(true);
  const [saving,setSaving]=useState(false);
  const [changing,setChanging]=useState(false);

  useEffect(()=>{
    userApi.me().then(({data})=>{setProfile({name:data.name||'',email:data.email||''});localStorage.setItem('jobtrack_user',JSON.stringify(data))}).catch(err=>notify(err.response?.data?.message||'Unable to load your profile.','error')).finally(()=>setLoading(false));
  },[notify]);

  const save=async(e)=>{
    e.preventDefault();setSaving(true);
    try{const {data}=await userApi.updateProfile(profile);setProfile({name:data.name,email:data.email});localStorage.setItem('jobtrack_user',JSON.stringify(data));notify('Profile changes saved.')}catch(err){notify(err.response?.data?.message||'Unable to save profile changes.','error')}finally{setSaving(false)}
  };

  const change=async(e)=>{
    e.preventDefault();
    if(password.newPassword!==password.confirm){notify('New passwords do not match.','error');return}
    if(password.newPassword.length<8){notify('New password must be at least 8 characters.','error');return}
    setChanging(true);
    try{await userApi.changePassword({currentPassword:password.currentPassword,newPassword:password.newPassword});setPassword({currentPassword:'',newPassword:'',confirm:''});notify('Password changed successfully.')}catch(err){notify(err.response?.data?.message||'Unable to change your password.','error')}finally{setChanging(false)}
  };

  const logout=()=>{localStorage.removeItem('jobtrack_token');localStorage.removeItem('jobtrack_user');navigate('/login')};

  return <div className="page-stack"><div className="page-heading"><div><span className="eyebrow orange">ACCOUNT</span><h1>Settings</h1><p>Manage your profile and account security.</p></div></div><div className="settings-grid">
    <section className="panel settings-panel"><div className="settings-title"><span className="settings-icon"><UserRound size={20}/></span><div><h2>Profile</h2><p>Keep your basic information up to date.</p></div></div><form onSubmit={save} className="settings-form"><label className="field">Full name<input value={profile.name} onChange={e=>setProfile({...profile,name:e.target.value})} required disabled={loading}/></label><label className="field">Email<input type="email" value={profile.email} onChange={e=>setProfile({...profile,email:e.target.value})} required disabled={loading}/></label><button className="primary-button" disabled={loading||saving}><Check size={17}/> {saving?'Saving…':'Save changes'}</button></form></section>
    <section className="panel settings-panel"><div className="settings-title"><span className="settings-icon"><LockKeyhole size={20}/></span><div><h2>Security</h2><p>Change your password when you need to.</p></div></div><form onSubmit={change} className="settings-form"><label className="field">Current password<input type="password" value={password.currentPassword} onChange={e=>setPassword({...password,currentPassword:e.target.value})} required/></label><label className="field">New password<input type="password" value={password.newPassword} onChange={e=>setPassword({...password,newPassword:e.target.value})} minLength="8" required/></label><label className="field">Confirm new password<input type="password" value={password.confirm} onChange={e=>setPassword({...password,confirm:e.target.value})} minLength="8" required/></label><button className="secondary-button" disabled={changing}>{changing?'Changing…':'Change password'}</button></form></section>
    <section className="panel settings-panel account-danger"><div><h2>Account</h2><p>Sign out of your JobTrack workspace on this device.</p></div><button className="danger-button" onClick={logout}><LogOut size={17}/> Log out</button></section>
  </div></div>
}
