import { useEffect, useMemo, useState } from 'react';
import { CalendarDays, Clock3, MapPin, Plus, Search } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { applicationApi } from '../services/applicationApi';
import { interviewApi } from '../services/interviewApi';
import { StatusBadge } from '../components/Badge';
import Modal from '../components/Modal';

export default function Interviews(){
  const {notify}=useApp();
  const [applications,setApplications]=useState([]);
  const [interviews,setInterviews]=useState([]);
  const [open,setOpen]=useState(false);
  const [loading,setLoading]=useState(true);
  const [form,setForm]=useState({applicationId:'',roundName:'',interviewDate:'',interviewType:'ONLINE',notes:''});

  const load=async()=>{
    setLoading(true);
    try{
      const {data}=await applicationApi.list({page:0,size:100,sort:'createdAt,desc'});
      const apps=data.content||[];
      setApplications(apps);
      if(!apps.length){setInterviews([]);return}
      const results=await Promise.all(apps.map(async a=>{try{const res=await interviewApi.listForApplication(a.id);return (res.data||[]).map(i=>({...i,companyName:a.companyName,jobTitle:a.jobTitle,applicationId:a.id}));}catch{return []}}));
      setInterviews(results.flat().sort((a,b)=>new Date(a.interviewDate)-new Date(b.interviewDate)));
    }catch(err){notify(err.response?.data?.message||'Unable to load interviews.','error')}
    finally{setLoading(false)}
  };

  useEffect(()=>{load()},[]);

  const submit=async(e)=>{
    e.preventDefault();
    try{
      const {data}=await interviewApi.create(form.applicationId,{roundName:form.roundName,interviewDate:form.interviewDate,interviewType:form.interviewType,notes:form.notes});
      const app=applications.find(a=>String(a.id)===String(form.applicationId));
      setInterviews(current=>[...current,{...data,companyName:app?.companyName,jobTitle:app?.jobTitle,applicationId:app?.id}].sort((a,b)=>new Date(a.interviewDate)-new Date(b.interviewDate)));
      setOpen(false); setForm({applicationId:'',roundName:'',interviewDate:'',interviewType:'ONLINE',notes:''}); notify('Interview scheduled successfully.');
    }catch(err){notify(err.response?.data?.message||'Unable to schedule the interview.','error')}
  };

  const defaultApplication=useMemo(()=>applications[0],[applications]);
  useEffect(()=>{if(defaultApplication&&!form.applicationId)setForm(current=>({...current,applicationId:String(defaultApplication.id)}))},[defaultApplication,form.applicationId]);

  return <div className="page-stack"><div className="page-heading"><div><span className="eyebrow orange">YOUR SCHEDULE</span><h1>Interviews</h1><p>Know what is coming up and what to prepare for.</p></div><button className="primary-button" onClick={()=>setOpen(true)} disabled={!applications.length}><Plus size={18}/> Add interview</button></div>
    {loading?<div className="panel" style={{minHeight:260}}><div className="skeleton" style={{width:'100%',height:80,marginBottom:14}}/><div className="skeleton" style={{width:'100%',height:80}}/></div>:!interviews.length?<div className="empty-state"><div className="empty-icon"><CalendarDays size={26}/></div><h3>{applications.length?'No interviews yet':'Add an application first'}</h3><p>{applications.length?'Your scheduled interviews will appear here.':'Interviews belong to a job application, so add an application before scheduling one.'}</p>{!applications.length&&<Link className="primary-button" to="/applications?new=true"><Plus size={17}/> Add application</Link>}</div>:<div className="interview-page-list">{interviews.map(i=><div className="panel interview-card" key={i.id}><div className="interview-date-large"><strong>{new Date(i.interviewDate).getDate()}</strong><span>{new Date(i.interviewDate).toLocaleDateString('en-IN',{month:'short'})}</span></div><div className="interview-main"><div className="eyebrow orange">{i.status}</div><h2>{i.companyName}</h2><p>{i.jobTitle}</p><div className="interview-meta"><span><CalendarDays size={16}/>{new Date(i.interviewDate).toLocaleDateString('en-IN',{weekday:'long',day:'numeric',month:'long'})}</span><span><Clock3 size={16}/>{new Date(i.interviewDate).toLocaleTimeString('en-IN',{hour:'numeric',minute:'2-digit'})}</span><span><MapPin size={16}/>{i.interviewType==='ONLINE'?'Online':i.interviewType==='PHONE'?'Phone':'In person'}</span></div></div><div className="interview-round"><strong>{i.roundName}</strong><StatusBadge status={i.status==='SCHEDULED'?'INTERVIEW':i.status||'INTERVIEW'}/><Link className="text-link" to={`/applications/${i.applicationId}`}>View application</Link></div></div>)}</div>}

    <Modal open={open} title="Schedule an interview" onClose={()=>setOpen(false)}><form className="app-form" onSubmit={submit}><div className="form-grid"><label className="field">Application<select value={form.applicationId} onChange={e=>setForm({...form,applicationId:e.target.value})} required>{applications.map(a=><option key={a.id} value={a.id}>{a.companyName} — {a.jobTitle}</option>)}</select></label><label className="field">Round name<input value={form.roundName} onChange={e=>setForm({...form,roundName:e.target.value})} placeholder="Technical Round" required/></label><label className="field">Date & time<input type="datetime-local" value={form.interviewDate} onChange={e=>setForm({...form,interviewDate:e.target.value})} required/></label><label className="field">Interview type<select value={form.interviewType} onChange={e=>setForm({...form,interviewType:e.target.value})}><option value="ONLINE">Online</option><option value="PHONE">Phone</option><option value="IN_PERSON">In person</option></select></label></div><label className="field">Notes<textarea rows="5" value={form.notes} onChange={e=>setForm({...form,notes:e.target.value})} placeholder="What should you remember?" maxLength="5000"/></label><div className="modal-actions"><button type="button" className="secondary-button" onClick={()=>setOpen(false)}>Cancel</button><button className="primary-button">Schedule interview</button></div></form></Modal>
  </div>
}
