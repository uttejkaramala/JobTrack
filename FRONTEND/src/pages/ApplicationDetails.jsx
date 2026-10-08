import { useEffect, useState } from 'react';
import { ArrowLeft, CalendarDays, ExternalLink, FileText, MapPin, MoreHorizontal, Pencil, Plus, Search, Trash2 } from 'lucide-react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { applicationApi } from '../services/applicationApi';
import { interviewApi } from '../services/interviewApi';
import { noteApi } from '../services/noteApi';
import { PriorityBadge, StatusBadge } from '../components/Badge';
import Modal from '../components/Modal';

const pretty=s=>s?.replaceAll('_',' ').toLowerCase().replace(/\b\w/g,c=>c.toUpperCase());
const statuses=['SAVED','APPLIED','SCREENING','INTERVIEW','OFFER','REJECTED','WITHDRAWN'];

export default function ApplicationDetails(){
  const {id}=useParams();
  const navigate=useNavigate();
  const {notify}=useApp();
  const [application,setApplication]=useState(null);
  const [interviews,setInterviews]=useState([]);
  const [notes,setNotes]=useState([]);
  const [loading,setLoading]=useState(true);
  const [error,setError]=useState('');
  const [note,setNote]=useState('');
  const [noteModal,setNoteModal]=useState(false);
  const [interviewModal,setInterviewModal]=useState(false);
  const [moreOpen,setMoreOpen]=useState(false);
  const [deleteConfirm,setDeleteConfirm]=useState(false);
  const [interview,setInterview]=useState({roundName:'',interviewDate:'',interviewType:'ONLINE',notes:''});

  const load=async()=>{
    setLoading(true); setError('');
    try{
      const [appRes, interviewRes, noteRes]=await Promise.all([
        applicationApi.getById(id),
        interviewApi.listForApplication(id),
        noteApi.listForApplication(id),
      ]);
      setApplication(appRes.data);
      setInterviews(interviewRes.data||[]);
      setNotes(noteRes.data||[]);
    }catch(err){
      setError(err.response?.data?.message || 'Unable to load this application.');
    }finally{setLoading(false)}
  };

  useEffect(()=>{load()},[id]);

  if(loading)return <DetailSkeleton/>;
  if(error)return <div className="empty-state page-not-found"><div className="empty-icon"><Search size={24}/></div><h3>Application not found</h3><p>{error}</p><Link className="secondary-button" to="/applications"><ArrowLeft size={17}/> Back to applications</Link></div>;
  if(!application)return null;

  const submitInterview=async(e)=>{
    e.preventDefault();
    try{
      const {data}=await interviewApi.create(application.id, interview);
      setInterviews(current=>[...current,data]);
      setInterview({roundName:'',interviewDate:'',interviewType:'ONLINE',notes:''});
      setInterviewModal(false);
      notify('Interview scheduled successfully.');
    }catch(err){notify(err.response?.data?.message || 'Unable to schedule the interview.','error')}
  };

  const addNote=async(e)=>{
    e.preventDefault();
    if(!note.trim())return;
    try{
      const {data}=await noteApi.create(application.id,{content:note.trim()});
      setNotes(current=>[...current,data]);
      setNote(''); setNoteModal(false); notify('Note added successfully.');
    }catch(err){notify(err.response?.data?.message || 'Unable to add the note.','error')}
  };

  const updateStatus=async(status)=>{
    if(status===application.status)return;
    try{
      const {data}=await applicationApi.updateStatus(application.id,status);
      setApplication(data);
      notify('Application status updated.');
    }catch(err){notify(err.response?.data?.message || 'Unable to update status.','error')}
  };

  const removeApplication=async()=>{
    try{
      await applicationApi.remove(application.id);
      notify('Application deleted.');
      navigate('/applications');
    }catch(err){notify(err.response?.data?.message || 'Unable to delete the application.','error')}
    finally{setDeleteConfirm(false)}
  };

  return <div className="page-stack">
    <Link className="back-link" to="/applications"><ArrowLeft size={17}/> Back to applications</Link>
    <div className="detail-header">
      <div className="detail-title"><span className="large-company-avatar">{application.companyName?.charAt(0)?.toUpperCase()}</span><div><div className="eyebrow orange">APPLICATION</div><h1>{application.companyName}</h1><p>{application.jobTitle}</p><div className="detail-meta"><span><MapPin size={15}/>{application.location||'Location not added'}</span><span>{pretty(application.workMode)}</span><span>{pretty(application.employmentType)}</span></div></div></div>
      <div className="detail-actions"><StatusBadge status={application.status}/><button className="secondary-button" onClick={()=>navigate(`/applications?edit=${application.id}`)}><Pencil size={16}/> Edit</button><div className="more-actions"><button className="secondary-button icon-only" onClick={()=>setMoreOpen(v=>!v)} aria-label="More application actions"><MoreHorizontal size={18}/></button>{moreOpen&&<div className="more-menu"><button className="more-menu-item danger-menu-item" onClick={()=>{setMoreOpen(false);setDeleteConfirm(true)}}><Trash2 size={16}/> Delete application</button></div>}</div></div>
    </div>

    <div className="detail-grid">
      <main className="detail-main">
        <section className="panel"><div className="panel-header"><div><h2>Application details</h2><p>Key information for this opportunity.</p></div></div><div className="detail-fields"><Info label="Applied" value={formatDate(application.appliedDate)}/><Info label="Source" value={pretty(application.source)}/><Info label="Priority" value={<PriorityBadge priority={application.priority}/>}/><Info label="Salary" value={application.salaryMin!=null||application.salaryMax!=null?`${money(application.salaryMin)} – ${money(application.salaryMax)}`:'Not added'}/><Info label="Follow-up" value={application.nextFollowUpDate?formatDate(application.nextFollowUpDate):'Not scheduled'}/><Info label="Job posting" value={application.jobUrl?<a href={application.jobUrl} target="_blank" rel="noreferrer" className="text-link">Open posting <ExternalLink size={14}/></a>:'Not added'}/></div></section>

        <section className="panel"><div className="panel-header"><div><h2>Interviews</h2><p>Keep every conversation in context.</p></div><button className="secondary-button" onClick={()=>setInterviewModal(true)}><Plus size={16}/> Add interview</button></div>{interviews.length?<div className="detail-list">{interviews.map(i=><div className="detail-list-item" key={i.id}><div className="date-tile"><CalendarDays size={19}/></div><div><strong>{i.roundName}</strong><span>{formatDateTime(i.interviewDate)} · {pretty(i.interviewType)}</span><small>{i.notes||'No notes added.'}</small></div><StatusBadge status={i.status||'SCHEDULED'}/></div>)}</div>:<EmptyBlock icon={CalendarDays} title="No interviews yet" text="Schedule the next conversation when it is confirmed."/>}</section>

        <section className="panel"><div className="panel-header"><div><h2>Notes</h2><p>Capture useful details while they are fresh.</p></div><button className="secondary-button" onClick={()=>setNoteModal(true)}><Plus size={16}/> Add note</button></div>{notes.length?<div className="notes-list">{notes.map(n=><article className="note-item" key={n.id}><FileText size={18}/><div><p>{n.content}</p><small>{formatDateTime(n.createdAt)}</small></div></article>)}</div>:<EmptyBlock icon={FileText} title="No notes yet" text="Add recruiter details, preparation points or reminders."/>}</section>
      </main>

      <aside className="detail-side">
        <section className="panel status-panel"><h3>Update status</h3><p>Move this application through your pipeline.</p><div className="status-options">{statuses.map(s=><button key={s} className={application.status===s?'current':''} onClick={()=>updateStatus(s)}><span className={`status-dot ${s.toLowerCase()}`}/>{pretty(s)}</button>)}</div></section>
      </aside>
    </div>

    <Modal open={interviewModal} title="Schedule an interview" onClose={()=>setInterviewModal(false)}>
      <form className="app-form" onSubmit={submitInterview}><div className="form-grid"><label className="field">Round name<input value={interview.roundName} onChange={e=>setInterview({...interview,roundName:e.target.value})} placeholder="Technical Round" required/></label><label className="field">Date & time<input type="datetime-local" value={interview.interviewDate} onChange={e=>setInterview({...interview,interviewDate:e.target.value})} required/></label><label className="field">Interview type<select value={interview.interviewType} onChange={e=>setInterview({...interview,interviewType:e.target.value})}><option value="ONLINE">Online</option><option value="PHONE">Phone</option><option value="IN_PERSON">In person</option></select></label></div><label className="field">Notes<textarea rows="5" value={interview.notes} onChange={e=>setInterview({...interview,notes:e.target.value})} placeholder="What should you remember?" maxLength="5000"/></label><div className="modal-actions"><button type="button" className="secondary-button" onClick={()=>setInterviewModal(false)}>Cancel</button><button className="primary-button">Schedule interview</button></div></form>
    </Modal>

    <Modal open={noteModal} title="Add a note" onClose={()=>setNoteModal(false)} width="520px"><form onSubmit={addNote}><label className="field">Note<textarea value={note} onChange={e=>setNote(e.target.value)} rows="6" maxLength="5000" placeholder="What do you want to remember about this application?" required/></label><div className="modal-actions"><button type="button" className="secondary-button" onClick={()=>setNoteModal(false)}>Cancel</button><button className="primary-button">Add note</button></div></form></Modal>

    <Modal open={deleteConfirm} title="Delete application?" onClose={()=>setDeleteConfirm(false)} width="460px"><div className="confirm-content"><div className="confirm-icon"><Trash2 size={21}/></div><h3>Remove {application.companyName}?</h3><p>This will also remove its related interviews and notes. This action cannot be undone.</p><div className="modal-actions"><button className="secondary-button" onClick={()=>setDeleteConfirm(false)}>Cancel</button><button className="danger-button" onClick={removeApplication}>Delete application</button></div></div></Modal>
  </div>;
}

function DetailSkeleton(){return <div className="page-stack"><div className="skeleton" style={{width:180,height:18}}/><div className="panel" style={{height:100}}><div className="skeleton" style={{width:300,height:32}}/></div>{[1,2,3].map(i=><div className="panel" style={{minHeight:170}} key={i}><div className="skeleton" style={{width:180,height:20,marginBottom:22}}/><div className="skeleton" style={{width:'100%',height:60}}/></div>)}</div>}
function Info({label,value}){return <div className="info-field"><span>{label}</span><strong>{value}</strong></div>}
function EmptyBlock({icon:Icon,title,text}){return <div className="empty-block"><Icon size={22}/><div><strong>{title}</strong><span>{text}</span></div></div>}
function formatDate(d){return d?new Date(`${d}T00:00:00`).toLocaleDateString('en-IN',{day:'2-digit',month:'short',year:'numeric'}):'—'}
function formatDateTime(d){return d?new Date(d).toLocaleString('en-IN',{day:'2-digit',month:'short',year:'numeric',hour:'numeric',minute:'2-digit'}):'—'}
function money(v){return new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR',maximumFractionDigits:0}).format(v||0)}
